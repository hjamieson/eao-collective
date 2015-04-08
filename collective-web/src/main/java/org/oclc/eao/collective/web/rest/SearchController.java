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

package org.oclc.eao.collective.web.rest;

import org.oclc.eao.collective.api.domain.NtService;
import org.oclc.eao.collective.api.model.Triple;
import org.oclc.eao.collective.indexer.IndexClient;
import org.oclc.eao.collective.indexer.IndexSearchResponse;
import org.oclc.eao.collective.web.model.SearchParams;
import org.oclc.eao.collective.web.model.SearchResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Description: REST endpoint for the Nt entities.
 * User: jamiesoh
 * Date: 2/22/15
 * Time: 12:18 PM
 * &copy;2013 OCLC Data Architecture Group
 */
@RestController
@RequestMapping("/search")
public class SearchController {
    private static final Logger LOG = LoggerFactory.getLogger(SearchController.class);
    public static final int DEFAULT_SCAN_HOLD_TIME_MS = 30000;

    @Autowired
    private NtService ntService;
    @Autowired
    private IndexClient indexClient;

    @RequestMapping(method = RequestMethod.POST)
    public List<Triple> search(@RequestBody SearchParams params) throws IOException {
        LOG.debug("search request received, object={}, maxRows={}", params.getObject(), params.getMaxRows());

//        List<String> keys = indexClient.search1(params.getSubject(), params.getPredicate(), params.getObject(), params.getMaxRows());
//        List<Triple> triples = ntService.get(keys);
//        return triples;
        IndexSearchResponse indexSearchResponse = indexClient.scrollSearchBegin(params.getSubject(), params.getPredicate(), params.getObject(), params.getMaxRows());
        if (indexSearchResponse.hitsCount() > 0) {
            return ntService.get(indexSearchResponse.getHits());
        } else {
            return Collections.<Triple>emptyList();
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "scroll")
    public SearchResults scrollSearch(@RequestBody SearchParams params) throws IOException {
        LOG.debug("scrollSearch request, scrollId({})", params.getScrollId());
        IndexSearchResponse indexSearchResponse = null;
        SearchResults searchResults = new SearchResults();
        if (params.getScrollId() == null) {
            indexSearchResponse = indexClient.scrollSearchBegin(params.getSubject(), params.getPredicate(), params.getObject(), params.getMaxRows());
        } else {
            indexSearchResponse = indexClient.scrollSearchNext(params.getScrollId(), DEFAULT_SCAN_HOLD_TIME_MS);
        }
        if (indexSearchResponse.hitsCount() > 0) {
            List<Triple> triples = ntService.get(indexSearchResponse.getHits());
            searchResults.setTriples(triples);
        }
        // if no hits came back, blank out the scrollId!
        searchResults.setScrollId(searchResults.getTriples().size() > 0 ? indexSearchResponse.getScrollId() : null);
        return searchResults;
    }
}
