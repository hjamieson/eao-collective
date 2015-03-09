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

package org.oclc.eao.collective.indexer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.oclc.eao.collective.api.model.Triple;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Description:
 * User: jamiesoh
 * Date: 3/9/15
 * Time: 1:21 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public class BulkIndexTest {
    @Test
    public void testMapper() throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        Triple t = new Triple("foo");
        t.setOrigin("junit");
        t.setCollection("junit-collection");
        String json = om.writeValueAsString(t);
        System.out.println(json);
        assertThat(json.contains("\n"), is(false));
    }
}
