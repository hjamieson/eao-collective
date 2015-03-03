/****************************************************************************************************************
 *
 *  Copyright (c) 2014 OCLC, Inc. All Rights Reserved.
 *
 *  OCLC proprietary information: the enclosed materials contain
 *  proprietary information of OCLC, Inc. and shall not be disclosed in whole or in 
 *  any part to any third party or used by any person for any purpose, without written
 *  consent of OCLC, Inc.  Duplication of any portion of these materials shall include this notice.
 *
 ******************************************************************************************************************/

package org.oclc.eao.collective.indexer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.oclc.eao.collective.api.model.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Description: uses transport mode to make calls to the elasticsearch cluster.  This is the
 * client to use unless you are doing unit testing.
 * User: jamiesoh
 * Date: 2/25/15
 * Time: 2:14 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public class IndexClient {
    private static final Logger LOG = LoggerFactory.getLogger(IndexClient.class);
    public static final String CLIENT_NOT_CONNECTED = "client not connected";
    public static final String INDEX = "collective";
    public static final String TYPE_NT = "triple";
    private String[] hostList;
    private TransportClient client;
    private final ObjectMapper om;

    public IndexClient(String... hostList) {
        this.hostList = hostList;
        om = new ObjectMapper();
    }

    public IndexClient connect() {
        if (client != null) {
            throw new IllegalStateException("client already connected");
        }
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("client.transport.ignore_cluster_name", true)
                .put("client.transport.sniff", true)
                .build();
        client = new TransportClient(settings);
        for (String hostName : hostList) {
            client.addTransportAddress(new InetSocketTransportAddress(hostName, 9300));
        }
        return this;
    }

    public void close() {
        if (client != null) {
            client.close();
            client = null;
        }
        LOG.info("client closed");
    }

    public void index(String idx, String type, String key, Map<String, Object> obj) {
        if (client != null) {
            client.prepareIndex(idx, type, key)
                    .setSource(obj)
                    .execute()
                    .actionGet();
        } else {
            throw new IllegalStateException(CLIENT_NOT_CONNECTED);
        }
    }

    public Map<String, Object> get(String idx, String type, String key) {
        if (client != null) {
            GetResponse resp = client.prepareGet(idx, type, key)
                    .execute()
                    .actionGet();
            return resp.getSourceAsMap();
        } else {
            throw new IllegalStateException(CLIENT_NOT_CONNECTED);
        }
    }

    public void index(Triple triple) {
        try {
            String json = om.writeValueAsString(triple);
            IndexRequestBuilder request = client.prepareIndex(INDEX, TYPE_NT, triple.getId()).setSource(json);
            request.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(String id) {
        client.prepareDelete(INDEX, TYPE_NT, id).execute();
    }
}
