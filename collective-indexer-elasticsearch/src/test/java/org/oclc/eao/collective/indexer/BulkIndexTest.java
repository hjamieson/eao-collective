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
import org.oclc.eao.collective.api.TripleHelper;
import org.oclc.eao.collective.api.model.Triple;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
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
        Triple t = TripleHelper.makeTriple("<foo><bar> \"Test bulk index test\" .","test","714");
        String json = TripleHelper.toJSON(t);
        System.out.println(json);
        assertThat(json.contains("\n"), is(false));
    }

    @Test
    public void testBulkIndexFilter(){
        Triple t = TripleHelper.makeTriple("<foo><bar> \"jes nes se pa\"@fr .","junit","404");
        String json = ElasticCommandBuilder.asBulkIndex(t);
        assertThat(json.charAt(json.length()-2), not(equalTo('\n')));
        System.out.println(json);
    }
}
