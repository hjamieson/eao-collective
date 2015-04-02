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

package org.oclc.eao.collective.api.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.oclc.eao.collective.api.TripleHelper;

import java.io.IOException;

import static org.junit.Assert.fail;

/**
 * Description:
 * User: jamiesoh
 * Date: 4/1/15
 * Time: 8:07 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public class JsonTest {
    @Test
    public void testSerializeLiterals(){
        Triple nt = new Triple("http://hugh.org/123","http://schema.org/name","Seriously Good-Looking","en");
        nt.setCollection("K-Mart");
        nt.setLoadId("testing");
        ObjectMapper om = new ObjectMapper();
        try {
            String json = om.writeValueAsString(nt);
            System.out.println(json);
            om.readValue(json, Triple.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
