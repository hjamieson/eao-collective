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
import org.oclc.eao.collective.api.TripleHelper;
import org.oclc.eao.collective.api.model.Triple;

/**
 * Description:
 * User: jamiesoh
 * Date: 3/11/15
 * Time: 7:10 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public class ElasticCommandBuilder {
    private static final ObjectMapper om = new ObjectMapper();

    /**
     * write out the bulk load command for the given triple document.
     *
     * @param triple
     */
    public static String asBulkIndex(Triple triple) {
        StringBuilder sb = new StringBuilder();

       /*
        {"index":{}}
        {"subj":"..","instance":"...","collection":"...",...}
         */
        sb.append("{\"index\":{\"_id\":\"").append(triple.getId()).append("\"}}").append('\n');
        try {
            String json = om.writeValueAsString(triple);
            sb.append(json).append('\n');
            return sb.toString();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
