/**
 * *************************************************************************************************************
 * <p/>
 * Copyright (c) 2014 OCLC, Inc. All Rights Reserved.
 * <p/>
 * OCLC proprietary information: the enclosed materials contain
 * proprietary information of OCLC, Inc. and shall not be disclosed in whole or in
 * any part to any third party or used by any person for any purpose, without written
 * consent of OCLC, Inc.  Duplication of any portion of these materials shall include this notice.
 * <p/>
 * ****************************************************************************************************************
 */

package org.oclc.eao.collective.indexer.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Description:
 * User: jamiesoh
 * Date: 4/23/15
 * Time: 1:25 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public class ArchiveGraphModelTest {
    @Test
    public void testReadSample() {
        try {
            ObjectMapper om = new ObjectMapper();
            File agDemo = new File("src/test/resources/ag_demo1.json");
            Map<String, Object> map = om.readValue(agDemo, Map.class);
            for (Map.Entry me : map.entrySet()) {
                System.out.println(me.getKey() + ":" + me.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testReadSampleIntoModel() {
        try {
            ObjectMapper om = new ObjectMapper();
            File agDemo = new File("src/test/resources/ag_demo1.json");
            ArchiveGraph ag = om.readValue(agDemo, ArchiveGraph.class);
            for (Map.Entry me : ag.getData().entrySet()) {
                System.out.println(me.getKey() + ":" + me.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testReadIndexType() {
        try {
            ObjectMapper om = new ObjectMapper();
            File agDemo = new File("src/test/resources/ag_demo1.json");
            ArchiveGraph ag = om.readValue(agDemo, ArchiveGraph.class);
            assertTrue(ag.getCommand().get("create") instanceof Map);
            assertThat(((Map) ag.getCommand().get("create")).get("_index").toString(), equalTo("everest"));
            assertThat(ag.getIndex(), equalTo("everest"));
            assertThat(((Map)ag.getCommand().get("create")).get("_type").toString(), equalTo("entities"));
            assertThat(ag.getType(), equalTo("entities"));
            assertThat(ag.getId(), equalTo("http://dbpedia.org/resource/London"));
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }
}
