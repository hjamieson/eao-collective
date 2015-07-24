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

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
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
    private final Connection connection;
    private Configuration config;
    private static final String DEFAULT_TABLE_NAME = "eao_collective";
    private final TableName tableName;

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
            connection = ConnectionFactory.createConnection(this.config);
            LOG.info("{} server instantiated...", this.getClass().getSimpleName());
            this.tableName = TableName.valueOf(DEFAULT_TABLE_NAME);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException("Failed to start HBaseStore:", e);
        }
    }

    @Override
    public void save(Triple nt) throws IOException {
        Put put = getPut(nt);
        try (Table hTable = connection.getTable(tableName)) {
            hTable.put(put);
        }
    }

    public static Put getPut(Triple nt) {

        Put put = new Put(Bytes.toBytes(nt.getId()));
        put.addColumn(DEFAULT_CF, ID_CQ, nt.getId().getBytes());
        put.addColumn(DEFAULT_CF, SUBJ_CQ, nt.getSubject().getBytes());
        put.addColumn(DEFAULT_CF, PRED_CQ, nt.getPredicate().getBytes());
        put.addColumn(DEFAULT_CF, OBJ_CQ, nt.getObject().getBytes());
        put.addColumn(DEFAULT_CF, COLLECTION_CQ, nt.getCollection().toString().getBytes());
        put.addColumn(DEFAULT_CF, INSTANCE_CQ, nt.getInstance().getBytes());
        put.addColumn(DEFAULT_CF, WEIGHT_CQ, Bytes.toBytes(nt.getWeight()));
        return put;
    }

    @Override
    public Triple get(String id) throws IOException {
        Get get = new Get(Bytes.toBytes(id));
        try (Table hTable = connection.getTable(tableName)) {
            Result result = hTable.get(get);
            if (!result.isEmpty()) {
                Triple resp = getTripleFromResult(result);
                return resp;
            } else {
                return null;
            }
        }
    }

    @Override
    public List<Triple> get(Collection<String> keyList) throws IOException {
        LOG.debug("get for ({}) items", keyList.size());
        // todo implement multi-get from hbase using keys.
        List<Triple> triples = new ArrayList<>();
        try (Table hTable = connection.getTable(tableName);) {

            for (String key : keyList) {
                Result result = hTable.get(new Get(Bytes.toBytes(key)));
                if (!result.isEmpty()) {
                    triples.add(getTripleFromResult(result));
                } else {
                    LOG.debug("key {} is empty", key);
                }
            }
            return triples;
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
        try (Table hTable = connection.getTable(tableName)) {
            hTable.delete(del);
        }
    }

    /**
     * signals that we are done with this store and all resources can be released.
     */
    public void close() {
        try {
            connection.close();
        } catch (IOException e) {
        }
        LOG.info("{} store closed", this.getClass().getSimpleName());
    }

}
