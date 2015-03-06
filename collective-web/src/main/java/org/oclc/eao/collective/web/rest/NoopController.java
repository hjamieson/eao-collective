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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

/**
 * Description: This controller is a dummy artifact that can be used for unit testing and problem
 * determination.  It can act as an endpoint for client tests.
 * User: jamiesoh
 * Date: 3/5/15
 * Time: 9:26 AM
 * &copy;2013 OCLC Data Architecture Group
 */
@RestController
@RequestMapping("/noop")
public class NoopController {
    private static final Logger LOG = LoggerFactory.getLogger(NoopController.class);

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<Void> post(@RequestBody HashMap<String, String> map, ServletUriComponentsBuilder uriBuilder) throws IOException {
        LOG.trace("post for {} received", map);

        ResponseEntity<Void> response = null;
        HttpHeaders headers = new HttpHeaders();
        UriComponents uri = uriBuilder.path("/noop/{id}").build().expand(UUID.randomUUID().toString());
        headers.setLocation(uri.toUri());
        response = new ResponseEntity<Void>(null, headers, HttpStatus.CREATED);

        return response;
    }

}
