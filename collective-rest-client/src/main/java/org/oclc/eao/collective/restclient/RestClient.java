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

package org.oclc.eao.collective.restclient;

import org.apache.commons.lang.Validate;
import org.oclc.eao.collective.api.model.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

/**
 * Description:
 * User: jamiesoh
 * Date: 2/27/15
 * Time: 2:31 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public class RestClient {
    private static final Logger LOG = LoggerFactory.getLogger(RestClient.class);
    private String endpoint = "/triple";
    private String rootUrl;
    private RestTemplate restTemplate;
    private URI postURI;

    public RestClient(String rootUrl) {
        this.rootUrl = rootUrl;
        restTemplate = new RestTemplate();
        try {
            postURI = new URI(rootUrl + endpoint);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public RestClient() {
    }

    public String getRootUrl() {
        return rootUrl;
    }

    public void setRootUrl(String rootUrl) {
        this.rootUrl = rootUrl;
        try {
            postURI = new URI(rootUrl + endpoint);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }

    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        Validate.isTrue(endpoint.startsWith("/"), "endpoint must start with a /");
        this.endpoint = endpoint;
        try {
            postURI = new URI(rootUrl + endpoint);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public URI post(Triple triple) {
        URI location = restTemplate.postForLocation(postURI, triple);
        LOG.trace("triple {} created", location);
        return location;
    }

    public Triple get(String id) {
        return restTemplate.getForObject(rootUrl + endpoint + "/{id}", Triple.class, id);
    }

    public Triple get(URI location) {
        return restTemplate.getForObject(location.toString(), Triple.class, Collections.emptyMap());
    }

    public void delete(Triple triple) {
        restTemplate.delete(rootUrl + endpoint + "/{id}", triple.getId());
        LOG.trace("triple {} deleted", triple.getId());
    }


}
