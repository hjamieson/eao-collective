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

import org.oclc.eao.collective.api.TripleHelper;
import org.oclc.eao.collective.api.domain.NtService;
import org.oclc.eao.collective.api.model.Triple;
import org.oclc.eao.collective.indexer.IndexClient;
import org.oclc.eao.collective.indexer.ScrollFind;
import org.oclc.eao.collective.web.model.NTripleText;
import org.oclc.eao.collective.web.model.SearchParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

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
        ScrollFind scrollFind = indexClient.beginScrollSearch(params.getSubject(), params.getPredicate(), params.getObject(), params.getMaxRows());
        if (scrollFind.hitsCount() > 0) {
            return ntService.get(scrollFind.getHits());
        } else {
            return Collections.<Triple>emptyList();
        }
    }
}
