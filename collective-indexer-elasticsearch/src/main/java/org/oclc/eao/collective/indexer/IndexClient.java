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

package org.oclc.eao.collective.indexer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.Validate;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.oclc.eao.collective.api.model.Triple;
import org.oclc.eao.collective.indexer.model.IndexSearchResponse;
import org.oclc.eao.collective.indexer.model.SearchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;

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
    public static final int SEARCH_TTL_MS = 30000;
    private String[] hostList;
    private TransportClient client;
    private final ObjectMapper om;

    public IndexClient(String... hostList) {
        this.hostList = hostList;
        om = new ObjectMapper();
    }

    public IndexClient connect() {
        Validate.isTrue(client == null, "client already connected");
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("client.transport.ignore_cluster_name", true)
                .put("client.transport.sniff", true)
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

    public void index(Triple triple) {
        Validate.notNull(client, CLIENT_NOT_CONNECTED);
        Validate.notEmpty(triple.getId(), "ID value missing; required");
        Validate.notEmpty(triple.getInstance(), "instance value missing; required");
        Validate.notEmpty(triple.getCollection(), "collection value missing; required");
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

    /**
     * basic object-term-only search.  This is deprecated now.
     *
     * @param subject
     * @param predicate
     * @param object
     * @param maxRows
     * @return
     */
    @Deprecated
    public List<String> search(String subject, String predicate, String object, int maxRows) {
        Validate.notNull(client, CLIENT_NOT_CONNECTED);
        // todo implement search via ES
        QueryBuilder qb = QueryBuilders.matchQuery("object", object);
        SearchResponse searchResponse = client.prepareSearch("collective")
                .setTypes("triple").setQuery(qb).setSize(maxRows).execute().actionGet();
        LOG.debug("search request returned {} hits", searchResponse.getHits().getTotalHits());

        List<String> keyList = new LinkedList<>();
        for (SearchHit hit : searchResponse.getHits()) {
            keyList.add(hit.getId());
        }
        return keyList;
    }

    /**
     * search method that is not pageable; you get the whole enchilada.
     * @param req
     * @return
     */
    public IndexSearchResponse searchAll(SearchRequest req) {
        Validate.notNull(client, CLIENT_NOT_CONNECTED);
        BoolQueryBuilder qb = QueryBuilders.boolQuery();

        if (req.getSubject() != null && req.getScrollId().trim().length() != 0) {
            qb.must(termQuery("subject", req.getSubject()));
        }
        if (req.getPredicate() != null && req.getPredicate().trim().length() != 0) {
            qb.must(termQuery("predicate", req.getPredicate()));
        }
        if (req.getObject() != null && req.getObject().trim().length() != 0) {
            qb.must(termQuery("object", req.getObject()));
        }
        if (req.getCollection() != null) {
            qb.must(termQuery("collection", req.getCollection()));
        }
        if (req.getInstance() != null) {
            qb.must(termQuery("instance", req.getInstance()));
        }

        SearchResponse searchResponse = client.prepareSearch("collective")
                .setTypes("triple")
                .setQuery(qb)
                .setSize(1000)
                .execute()
                .actionGet();
        LOG.debug("search request returned {} hits", searchResponse.getHits().getTotalHits());

        List<String> keyList = new LinkedList<>();
        for (SearchHit hit : searchResponse.getHits()) {
            keyList.add(hit.getId());
        }
        return new IndexSearchResponse(SEARCH_TTL_MS, keyList, null);
    }

    /**
     * Initiates a pageable search request.
     *
     * @param req
     * @return
     */
    public IndexSearchResponse scrollSearchBegin(SearchRequest req) {
        Validate.notNull(client, CLIENT_NOT_CONNECTED);
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        if (req.getSubject() != null && req.getScrollId().trim().length() != 0) {
            qb.must(termQuery("subject", req.getSubject()));
        }
        if (req.getPredicate() != null && req.getPredicate().trim().length() != 0) {
            qb.must(termQuery("predicate", req.getPredicate()));
        }
        if (req.getObject() != null && req.getObject().trim().length() != 0) {
            qb.must(termQuery("object", req.getObject()));
        }
        if (req.getCollection() != null) {
            qb.must(termQuery("collection", req.getCollection()));
        }
        if (req.getInstance() != null) {
            qb.must(termQuery("instance", req.getInstance()));
        }
        // start the scan; we should get a hash back to control the scroll!
        SearchResponse scrollResponse = client.prepareSearch("collective")
                .setTypes("triple")
                .setSearchType(SearchType.SCAN)
                .setScroll(new TimeValue(SEARCH_TTL_MS))
                .setQuery(qb)
                .setSize(req.getMaxRows())
                .execute()
                .actionGet();

        LOG.debug("beginScroll response: hits({}), scrollId({})", scrollResponse.getHits().getTotalHits(), scrollResponse.getScrollId());

        // if we got an id, assume there are results to view:
        IndexSearchResponse indexSearchResponse = new IndexSearchResponse();
        if (scrollResponse.getScrollId() != null) {
            indexSearchResponse = scrollSearchNext(scrollResponse.getScrollId(), SEARCH_TTL_MS);
        }
        LOG.debug("scrollSearchBegin complete, {} keys returned", indexSearchResponse.hitsCount());
        return indexSearchResponse;
    }

    /**
     * returns the next buffer of search results from a scan-scroll.
     *
     * @param scrollId
     * @param holdTime
     * @return
     */
    public IndexSearchResponse scrollSearchNext(String scrollId, int holdTime) {
        Validate.notNull(client, CLIENT_NOT_CONNECTED);
        SearchResponse scrollResponse = client.prepareSearchScroll(scrollId)
                .setScroll(new TimeValue(holdTime))
                .execute()
                .actionGet();

        List<String> keyList = new ArrayList<>();
        for (SearchHit hit : scrollResponse.getHits().getHits()) {
            keyList.add(hit.getId());
        }
        return new IndexSearchResponse(holdTime, keyList, scrollResponse.getScrollId());
    }
}
