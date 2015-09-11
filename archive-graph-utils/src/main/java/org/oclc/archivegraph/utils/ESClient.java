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

package org.oclc.archivegraph.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.lang3.Validate;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.oclc.archivegraph.model.CreateRequest;
import org.oclc.archivegraph.model.CreateRequest2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Description: Yet another client implementation to help me figure out WTF is going on with ES when I use their
 * API.
 * User: jamiesoh
 * Date: 4/27/15
 * Time: 2:35 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public class ESClient {
    private static final Logger LOG = LoggerFactory.getLogger(ESClient.class);
    public static final String DEFAULT_CLUSTER = "eao-cluster";
    private final TransportClient tc;

    public ESClient(String clusterName, String... hostList) {
        Validate.notNull(clusterName,"no cluster name provided");
        Validate.notEmpty(hostList, "hostList cannot be empty!");
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("client.transport.sniff", true)
                .put("cluster.name", clusterName)
                .build();
        tc = new TransportClient(settings, false);

        for (String host : hostList) {
            LOG.info("adding transport socket: {}", host);
            tc.addTransportAddress(new InetSocketTransportAddress(host, 9300));
        }
    }


    public void exec(CreateRequest request) {
        Validate.notNull(tc, "transportClient is null");
        IndexResponse resp = tc.prepareIndex(request.getIndex(), request.getType(), request.getId()).setSource(request.getData()).execute().actionGet();
        LOG.trace("response: {}", resp.getIndex());
    }

    public void exec(CreateRequest2 request) {
        Validate.notNull(tc, "transportClient is null");
        IndexResponse resp = null;
        if (request.needsId()) {
            resp = tc.prepareIndex(request.getIndex(), request.getType()).setSource(request.getDoc()).execute().actionGet();
        } else {
            resp = tc.prepareIndex(request.getIndex(), request.getType(), request.getId()).setSource(request.getDoc()).execute().actionGet();
        }
        LOG.trace("response: {}", resp.getId());
    }

    public void close() {
        if (tc != null) {
            LOG.info("closing ES client {}", this.hashCode());
            tc.close();
        }
    }

    public CreateRequest readJson(String json) throws IOException {
        return new ObjectMapper().readValue(json, CreateRequest.class);
    }

    public Client getClient() {
        Validate.notNull(tc);
        return tc;
    }
}
