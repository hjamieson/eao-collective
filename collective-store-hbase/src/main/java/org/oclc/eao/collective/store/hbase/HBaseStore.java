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
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.oclc.eao.collective.api.domain.NtStore;
import org.oclc.eao.collective.api.model.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

/**
 * Description: Implementation of the NtStore interface that uses HBase as the underlying store.
 * User: jamiesoh
 * Date: 2/22/15
 * Time: 4:05 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public class HBaseStore implements NtStore {
    private static final Logger LOG = LoggerFactory.getLogger(HBaseStore.class);
    public static final byte[] DEFAULT_CF = "data".getBytes();
    private final HConnection hConnection;
    private Configuration config;
    private String tableName = "eao_collective";

    public HBaseStore(Configuration config) {
        try {
            this.config = config;
            hConnection = HConnectionManager.createConnection(config);
            LOG.info("HTable server instantiated...");
        } catch (ZooKeeperConnectionException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException("Failed to start HBaseStore:", e);
        }
    }

    @Override
    public void save(Triple nt) throws IOException {
        Put put = getPut(nt);
        HTableInterface hTable = null;
        try {
            hTable = hConnection.getTable(tableName);
            hTable.put(put);
        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            hTable.close();
        }
    }

    public static Put getPut(Triple nt) {
        Put put = new Put(Bytes.toBytes(nt.getId()));
        put.add(DEFAULT_CF, Triple.ID_TAG.getBytes(), nt.getId().getBytes());
        put.add(DEFAULT_CF, Triple.TEXT_TAG.getBytes(), nt.getText().getBytes());
        put.add(DEFAULT_CF, Triple.ORIGIN_TAG.getBytes(), nt.getOrigin().getBytes());
        put.add(DEFAULT_CF, Triple.WEIGHT_TAG.getBytes(), Bytes.toBytes(nt.getWeight()));
        for (Map.Entry<String, String> e : nt.entrySet()) {
            put.add(DEFAULT_CF, e.getKey().getBytes(), e.getValue().getBytes());
        }
        return put;
    }

    @Override
    public Triple get(String id) throws IOException {
        Get get = new Get(Bytes.toBytes(id));
        HTableInterface hTable = null;
        try {
            hTable = hConnection.getTable(tableName);
            Result result = hTable.get(get);
            if (!result.isEmpty()) {
                Triple resp = new Triple();
                resp.setId(result.getRow().toString());
                // get all columns in data family
                NavigableMap<byte[], byte[]> familyMap = result.getFamilyMap(DEFAULT_CF);
                for (Map.Entry<byte[], byte[]> v : familyMap.entrySet()) {
                    resp.put(Bytes.toString(v.getKey()), Bytes.toString(v.getValue()));
                }
                return resp;
            } else {
                return null;
            }
        } finally {
            hTable.close();
        }
    }

    @Override
    public List<Triple> find(String query) {
        return null;
    }

    @Override
    public void delete(String id) throws IOException {
        Delete del = new Delete(Bytes.toBytes(id));
        HTableInterface hTable = null;
        try {
            hTable = hConnection.getTable(tableName);
            hTable.delete(del);
            hTable.flushCommits();
        } finally {
            hTable.close();
        }
    }

    /**
     * signals that we are done with this store and all resources can be released.
     */
    public void close() {
        try {
            hConnection.close();
        } catch (IOException e) {
        }
        LOG.info("{} store closed", this.getClass().getSimpleName());
    }


    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

}
