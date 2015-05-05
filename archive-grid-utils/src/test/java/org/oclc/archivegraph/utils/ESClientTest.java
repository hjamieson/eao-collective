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

package org.oclc.archivegraph.utils;


import org.junit.Test;
import org.oclc.archivegraph.model.CreateRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 * User: jamiesoh
 * Date: 4/27/15
 * Time: 2:36 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public class ESClientTest {
    @Test
    public void testIndexRecord(){
        ESClient a1 = new ESClient("tripstoreap01dxdu.dev.oclc.org");
        HashMap<String, Object> cmd = new HashMap<String, Object>();
        Map<String,Object> create = new HashMap<>();
        create.put("_index","hugh");
        create.put("_type", "people");
        create.put("_id","TEST:12345");
        cmd.put("create",create);

        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("firstName","Hugh");
        data.put("lastName","Jamieson");
        data.put("age",57);
        data.put("id",cmd.get("id"));
        CreateRequest request = new CreateRequest(cmd, data);
        a1.exec(request);
        a1.close();
    }
}
