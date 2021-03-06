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

import org.junit.Test;
import org.oclc.eao.collective.api.TripleHelper;
import org.oclc.eao.collective.api.model.Triple;

import java.net.URI;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Description:
 * User: jamiesoh
 * Date: 2/27/15
 * Time: 2:43 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public class RestClientTezt {
    @Test
    public void testPost() {
        RestClient restClient = new RestClient("http://localhost:8080/collective");
        Triple triple = TripleHelper.makeTriple("<foo> <bar> \"testing\" .", "junit", "junit");
        URI location = restClient.post(triple);
        assertThat(location, notNullValue());
        System.out.println("location was: " + location.toString());
        Triple findMe = restClient.get(location);
        assertThat(findMe, notNullValue());
        restClient.delete(findMe);

    }
    @Test
    public void testGet(){
        RestClient restClient = new RestClient("http://localhost:8080/collective");
        Triple triple = TripleHelper.makeTriple("<foo> <bar> \"testing\" .", "junit", "junit");
        URI location = restClient.post(triple);
        assertThat(location, notNullValue());

        Triple findMe = restClient.get(location);
        assertThat(findMe, notNullValue());
        assertThat(findMe.getSubject(), equalTo("<foo>"));
        assertThat(findMe.getInstance(), equalTo("junit"));
        findMe = restClient.get(findMe.getId());
        assertThat(findMe, notNullValue());
        assertThat(findMe.getPredicate(), equalTo("<bar>"));
        assertThat(findMe.getInstance(), equalTo("junit"));
        restClient.delete(findMe);
    }

    @Test
    public void testNoopEndpoint(){
        RestClient restClient = new RestClient("http://localhost:8080/collective");
        restClient.setEndpoint("/noop");
        Triple triple = TripleHelper.makeTriple("<foo> <bar> \"testing\" .", "junit", "junit");
        triple.setInstance("junit");
        triple.setCollection("junit");
        URI location = restClient.post(triple);
        assertThat(location, notNullValue());
    }
}
