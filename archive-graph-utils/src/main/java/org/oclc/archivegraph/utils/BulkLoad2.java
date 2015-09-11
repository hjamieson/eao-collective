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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * the http://hostname.com:port to the cluster.  Assumes Client is threadsafe.
 * User: jamiesoh
 * Date: 2/27/15
 * Time: 4:33 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public class BulkLoad2 {
    private static int POOL_SIZE = 5;
    private ExecutorService es;
    private ESClient client;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println(args);
            System.err.println("Usage: " + BulkLoad2.class.getSimpleName() + " <ES Cluster Name> <ES cluster node>");
            System.exit(1);
        }

        BulkLoad2 loader = new BulkLoad2();

        loader.client = new ESClient(args[1], args[1].split(","));
        loader.run();
    }

    public void run() {
        es = new ThreadPoolExecutor(POOL_SIZE,
                POOL_SIZE,
                100, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(POOL_SIZE),
                new ThreadPoolExecutor.CallerRunsPolicy());

        ObjectMapper om = new ObjectMapper();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String text = null;
        long loaded = 0;
        try {
            while ((text = reader.readLine()) != null) {
                try {
                    es.execute(new Task(om.readValue(text, CreateRequest.class)));
                    loaded++;
                } catch (JsonMappingException e) {
                    System.err.println(e.getMessage());
                } catch (JsonParseException e) {
                    System.err.println(e.getMessage());
                }
                if (loaded % 10000 == 0){
                    System.out.format("records written: %1$-10d%n", loaded);
                }
            }
            es.shutdown();
            System.out.format("\n****\n%1$d records stored\n****\n", loaded);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            try {
                reader.close();
                es.awaitTermination(5000, TimeUnit.MILLISECONDS);
                client.close();
            } catch (Exception e) {
            }
        }
    }

    class Task implements Runnable {
        private CreateRequest createRequest;

        public Task(CreateRequest createRequest) {
            this.createRequest = createRequest;
        }

        @Override
        public void run() {
            try {
                client.exec(createRequest);
            } catch (Exception e) {
                System.err.println("failed: " + createRequest + "; reason=" + e.getMessage());
                e.printStackTrace();
            }
        }
    }


}
