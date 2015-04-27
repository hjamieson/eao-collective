/**
 * *************************************************************************************************************
 * <p/>
 * Copyright (c) 2014 OCLC, Inc. All Rights Reserved.
 * <p/>
 * OCLC proprietary information: the enclosed materials contain
 * proprietary information of OCLC, Inc. and shall not be disclosed in whole or in
 * any part to any third party or used by any person for any purpose, without written
 * consent of OCLC, Inc.  Duplication of any portion of these materials shall include this notice.
 * <p/>
 * ****************************************************************************************************************
 */

package org.oclc.archivegrid.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.Validate;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.oclc.archivegrid.model.CreateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Description: uses transport mode to make calls to the elasticsearch cluster.  This is the
 * client to use unless you are doing unit testing.  This implementation is probably not
 * threadsafe, so make sure you use it accordingly.
 * User: jamiesoh
 * Date: 2/25/15
 * Time: 2:14 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public class IndexClient {
    private static final Logger LOG = LoggerFactory.getLogger(IndexClient.class);
    public static final String CLIENT_NOT_CONNECTED = "client not connected";
    public static final int SEARCH_TTL_MS = 30000;
    private String[] hostList;
    private TransportClient client;
    private final ObjectMapper om;

    public IndexClient(String... hostList) {
        this.hostList = hostList;
        om = new ObjectMapper();
    }

    public IndexClient connect() {
        Validate.notNull(hostList);
        Validate.isTrue(client == null, "client already connected");
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name","eao-cluster")
                .put("client.transport.ignore_cluster_name", false)
                .put("client.transport.sniff", false)
                .put("client.transport.ping_timeout", "10s")
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
        Validate.notNull(client, CLIENT_NOT_CONNECTED);
        client.prepareIndex(idx, type, key)
                .setSource(obj)
                .execute()
                .actionGet();
    }

    public Map<String, Object> get(String idx, String type, String key) {
        Validate.notNull(client, CLIENT_NOT_CONNECTED);
        GetResponse resp = client.prepareGet(idx, type, key)
                .execute()
                .actionGet();
        return resp.getSourceAsMap();
    }


    public void index(CreateRequest ag) {
        Validate.notNull(client, CLIENT_NOT_CONNECTED);
        try {
            String json = om.writeValueAsString(ag.getData());
            IndexRequestBuilder request = client.prepareIndex(ag.getIndex(), ag.getType(), ag.getId()).setSource(json);
            request.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CreateRequest readJson(String json) throws IOException {
        CreateRequest ag = om.readValue(json, CreateRequest.class);
        return ag;
    }

}
