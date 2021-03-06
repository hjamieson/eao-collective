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

package org.oclc.eao.collective.store.hbase;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    public static final byte[] ID_CQ = "id".getBytes();
    public static final byte[] SUBJ_CQ = "subject".getBytes();
    public static final byte[] PRED_CQ = "predicate".getBytes();
    public static final byte[] OBJ_CQ = "object".getBytes();
    public static final byte[] WEIGHT_CQ = "weight".getBytes();
    public static final byte[] COLLECTION_CQ = "collection".getBytes();
    public static final byte[] INSTANCE_CQ = "instance".getBytes();


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
        put.add(DEFAULT_CF, ID_CQ, nt.getId().getBytes());
        put.add(DEFAULT_CF, SUBJ_CQ, nt.getSubject().getBytes());
        put.add(DEFAULT_CF, PRED_CQ, nt.getPredicate().getBytes());
        put.add(DEFAULT_CF, OBJ_CQ, nt.getObject().getBytes());
        put.add(DEFAULT_CF, COLLECTION_CQ, nt.getCollection().toString().getBytes());
        put.add(DEFAULT_CF, INSTANCE_CQ, nt.getInstance().getBytes());
        put.add(DEFAULT_CF, WEIGHT_CQ, Bytes.toBytes(nt.getWeight()));
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
                Triple resp = getTripleFromResult(result);
                return resp;
            } else {
                return null;
            }
        } finally {
            hTable.close();
        }
    }

    @Override
    public List<Triple> get(Collection<String> keyList) throws IOException {
        LOG.debug("get for ({}) items", keyList.size());
        // todo implement multi-get from hbase using keys.
        HTableInterface hTable = null;
        List<Triple> triples = new ArrayList<>();
        try {
            hTable = hConnection.getTable(tableName);
            for (String key : keyList) {
                Result result = hTable.get(new Get(Bytes.toBytes(key)));
                if (!result.isEmpty()) {
                    triples.add(getTripleFromResult(result));
                } else {
                    LOG.debug("key {} is empty", key);
                }
            }
            return triples;
        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            hTable.close();
        }
    }

    public static Triple getTripleFromResult(Result result) {
        Triple resp = new Triple();
        resp.setId(result.getRow().toString());
        // get all columns in data family
        resp.setSubject(Bytes.toString(result.getValue(DEFAULT_CF, SUBJ_CQ)));
        resp.setPredicate(Bytes.toString(result.getValue(DEFAULT_CF, PRED_CQ)));
        resp.setObject(Bytes.toString(result.getValue(DEFAULT_CF, OBJ_CQ)));
        resp.setWeight(Bytes.toDouble(result.getValue(DEFAULT_CF, WEIGHT_CQ)));
        resp.setCollection(Bytes.toString(result.getValue(DEFAULT_CF, COLLECTION_CQ)));
        resp.setInstance(Bytes.toString(result.getValue(DEFAULT_CF, INSTANCE_CQ)));
        return resp;
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
