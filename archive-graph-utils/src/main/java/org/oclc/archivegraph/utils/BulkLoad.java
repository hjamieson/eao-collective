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

package org.oclc.archivegraph.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.oclc.archivegraph.model.CreateRequest;

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
public class BulkLoad {
    private static int POOL_SIZE = 20;
    private ExecutorService es;
    private ObjectPool<Worker> workerPool;
    private ESClient client;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println(args);
            System.err.println("Usage: " + BulkLoad.class.getSimpleName() + " <ES Cluster Name> <ES cluster node>");
            System.exit(1);
        }

        BulkLoad loader = new BulkLoad();
        loader.client = new ESClient(args[0], args[1].split(","));
        loader.run();
    }

    public void run() {
        es = new ThreadPoolExecutor(POOL_SIZE,
                POOL_SIZE,
                100, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(POOL_SIZE),
                new ThreadPoolExecutor.CallerRunsPolicy());

        workerPool = new GenericObjectPool<Worker>(new WorkerFactory());

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String text = null;
        long loaded = 0;
        try {
            while ((text = reader.readLine()) != null) {
                try {
                    es.execute(new Task(text));
                    loaded++;
                    if (loaded % 10000 == 0) {
                        System.out.format("records submitted: %-10d %n", loaded);
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
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
                Worker worker = workerPool.borrowObject();
                CreateRequest ag = worker.om.readValue(text, CreateRequest.class);
                client.exec(ag);
                workerPool.returnObject(worker);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("failed:" + text);
            }
        }
    }

    static class Worker {
        ObjectMapper om = new ObjectMapper();
    }

    static class WorkerFactory extends BasePooledObjectFactory<Worker> {
        public WorkerFactory() {
        }

        @Override
        public Worker create() throws Exception {
            return new Worker();
        }

        @Override
        public PooledObject<Worker> wrap(Worker worker) {
            return new DefaultPooledObject<>(worker);
        }

    }
}
