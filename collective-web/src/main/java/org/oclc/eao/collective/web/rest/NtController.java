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

import org.oclc.eao.collective.api.domain.NtService;
import org.oclc.eao.collective.api.model.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("nt")
public class NtController {
    private static final Logger LOG = LoggerFactory.getLogger(NtController.class);

    @Autowired
    private NtService ntService;

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<Void> post(@RequestBody HashMap<String, String> map, ServletUriComponentsBuilder uriBuilder) throws IOException {
        LOG.debug("post for {} received", map);
        Triple triple = ntService.create(map);
        HttpHeaders headers = new HttpHeaders();
        UriComponents uri = uriBuilder.path("/nt/{id}").build().expand(triple.getId());
        headers.setLocation(uri.toUri());
        return new ResponseEntity<Void>(null, headers, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    Map<String, String> get(@PathVariable String id) throws IOException {
        return ntService.get(id);
    }
}
