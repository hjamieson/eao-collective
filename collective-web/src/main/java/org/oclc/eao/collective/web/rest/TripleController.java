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

package org.oclc.eao.collective.web.rest;

import org.oclc.eao.collective.api.TripleHelper;
import org.oclc.eao.collective.api.domain.NtService;
import org.oclc.eao.collective.api.model.Triple;
import org.oclc.eao.collective.indexer.IndexClient;
import org.oclc.eao.collective.web.model.NTripleText;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Description: REST endpoint for the Nt entities.
 * User: jamiesoh
 * Date: 2/22/15
 * Time: 12:18 PM
 * &copy;2013 OCLC Data Architecture Group
 */
@RestController
@RequestMapping("/triple")
public class TripleController {
    private static final Logger LOG = LoggerFactory.getLogger(TripleController.class);

    @Autowired
    private NtService ntService;
    @Value("${nt.index.active}")
    private boolean activeIndex;
    @Autowired
    private IndexClient indexClient;

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<Void> post(@RequestBody Triple triple, ServletUriComponentsBuilder uriBuilder) throws IOException {
        LOG.trace("post for {} received", triple);

        ResponseEntity<Void> response = null;
        try {
            Triple result = ntService.create(triple);
            HttpHeaders headers = new HttpHeaders();
            UriComponents uri = uriBuilder.path("/triple/{id}").build().expand(triple.getId());
            headers.setLocation(uri.toUri());
            response = new ResponseEntity<Void>(null, headers, HttpStatus.CREATED);

            // index this data if active:
            if (activeIndex) {
                indexClient.index(result);
            }
        } catch (IllegalArgumentException e) {
            LOG.error("POST for triple failed; reason={}", e.getMessage());
            response = new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    Triple get(@PathVariable String id) throws IOException {
        return ntService.get(id);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    ResponseEntity<Void> delete(@PathVariable String id) throws IOException {
        ntService.delete(id);
        if (activeIndex) {
            indexClient.delete(id);
        }
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @RequestMapping(value="/nt", method = RequestMethod.POST)
    ResponseEntity<Void> ntPost(@RequestBody NTripleText ntriple, ServletUriComponentsBuilder uriBuilder) throws IOException {
        LOG.trace("POST for {} received", ntriple.getText());

        ResponseEntity<Void> response = null;
        try {
            Triple triple = TripleHelper.makeTriple(ntriple.getText(), ntriple.getCollection(), ntriple.getInstance());
            Triple result = ntService.create(triple);
            HttpHeaders headers = new HttpHeaders();
            UriComponents uri = uriBuilder.path("/triple/{id}").build().expand(triple.getId());
            headers.setLocation(uri.toUri());
            response = new ResponseEntity<Void>(null, headers, HttpStatus.CREATED);

            // index this data if active:
            if (activeIndex) {
                indexClient.index(result);
            }
        } catch (IllegalArgumentException e) {
            LOG.error("POST for triple failed; reason={}", e.getMessage());
            response = new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        }
        return response;
    }

}
