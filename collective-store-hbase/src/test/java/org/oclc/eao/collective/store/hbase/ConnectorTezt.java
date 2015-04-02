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

package org.oclc.eao.collective.store.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.oclc.eao.collective.api.TripleHelper;
import org.oclc.eao.collective.api.model.Triple;

import java.io.IOException;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Description:
 * User: jamiesoh
 * Date: 2/22/15
 * Time: 5:06 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public class ConnectorTezt {

    private Configuration conf;
    private HBaseStore store;

    @Before
    public void setup() throws IOException {
        conf = HBaseConfiguration.create();
        store = new HBaseStore(conf);
    }

    @Test
    public void connectorCreated(){
        assertThat(store, notNullValue());
    }

    @After
    public void tearDown(){
        store.close();
    }

    @Test
    public void insertTest(){
        Triple triple = getTriple("foobar");

        try {
            store.save(triple);
            Triple findMe = store.get(triple.getId());
            assertThat(findMe, notNullValue());
            store.delete("foobar");
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    private Triple getTriple(String id) {
        Triple triple = TripleHelper.makeTriple("<subject><predicate><object> .","test","555");
        return triple;
    }

    @Test
    public void writeReadTest(){
        Triple triple = getTriple("barfoo");
        try {
            store.save(triple);
            Triple findMe = store.get("barfoo");
            store.delete("barfoo");
            assertThat(findMe, notNullValue());
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testDelete(){
        Triple triple = getTriple("klaatu");

        try {
            store.save(triple);
            Triple findMe = store.get("klaatu");
            assertThat(findMe, notNullValue());
            store.delete("klaatu");
            assertThat(store.get("klaatu"), nullValue());
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }
}
