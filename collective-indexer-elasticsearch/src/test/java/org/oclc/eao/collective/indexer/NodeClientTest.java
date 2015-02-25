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

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Description:
 * User: jamiesoh
 * Date: 2/25/15
 * Time: 3:21 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public class NodeClientTest {

    private NodeClient client;


    @Test
    public void indexTest() {
        client = new NodeClient().connect();
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", "Bazinga");
        map.put("address", "511 Eleven Street");
        client.index("test", "widget", "123", map);
        Map<String, Object> result = client.get("test", "widget", "123");
        assertThat(result, notNullValue());
        assertThat(result.containsKey("name"), is(true));
        assertThat(result.get("address").toString(), equalTo("511 Eleven Street"));
        client.close();
    }
}
