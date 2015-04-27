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

package org.oclc.archivegrid.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.oclc.archivegrid.model.CreateRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Description: Reads special archive index commands from stdin and posts them to the ES cluster.  You must provide
 * the http://hostname.com:port to the cluster.
 * User: jamiesoh
 * Date: 2/27/15
 * Time: 4:33 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public class BatchIndexerLoader {
    private static int POOL_SIZE = 5;
    private String clusterNode;
    private ExecutorService es;
    private ObjectPool<IndexClient> clientPool;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println(args);
            System.err.println("Usage: " + BatchIndexerLoader.class.getSimpleName() + " <ES cluster node>");
            System.exit(1);
        }

        BatchIndexerLoader loader = new BatchIndexerLoader();
        loader.clusterNode = args[0];

        loader.run();
    }

    public void run() {
        es = new ThreadPoolExecutor(POOL_SIZE,
                POOL_SIZE,
                100, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(POOL_SIZE),
                new ThreadPoolExecutor.CallerRunsPolicy());

        clientPool = new GenericObjectPool<IndexClient>(new IndexClientFactory(clusterNode));

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String text = null;
        long loaded = 0;
        try {
            while ((text = reader.readLine()) != null) {
                es.execute(new Task(text));
                System.out.println("records loaded: " + ++loaded);
            }
            es.shutdown();
            System.out.format("%1$d records stored", loaded);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
            }
        }
    }

    class Task implements Runnable {
        private String text;

        public Task(String text) {
            this.text = text;
        }

        @Override
        public void run() {
            try {
                IndexClient client = clientPool.borrowObject();
                CreateRequest ag = client.readJson(text);
                client.index(ag);
                clientPool.returnObject(client);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("failed:" + text);
            }
        }
    }

    static class IndexClientFactory extends BasePooledObjectFactory<IndexClient> {
        private String clusterNode;

        public IndexClientFactory(String clusterNode) {
            this.clusterNode = clusterNode;
        }

        @Override
        public IndexClient create() throws Exception {
            IndexClient client = new IndexClient(clusterNode).connect();
            return client;
        }

        @Override
        public PooledObject<IndexClient> wrap(IndexClient indexClient) {
            return new DefaultPooledObject<>(indexClient);
        }

        @Override
        public void destroyObject(PooledObject<IndexClient> p) throws Exception {
            p.getObject().close();
        }
    }
}
