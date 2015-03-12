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

package org.oclc.eao.collective.restclient;

import org.oclc.eao.collective.api.model.Triple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Description: Reads n-triples from stdin and posts them to the collective-web where they
 * will be stored.  You must supply an orgin value on the commandline.
 * User: jamiesoh
 * Date: 2/27/15
 * Time: 4:33 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public class BatchLoader {
    public static final int CORE_POOL_SIZE = 20;
    public static final int MAXIMUM_POOL_SIZE = 20;
    public static final int QUEUE_SIZE = 20;
    private static RestClient restClient;

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println(args);
            System.err.println("Usage: BatchLoader <web-address> <loadId> <collection>");
            System.exit(1);
        }
        ExecutorService es =
                new ThreadPoolExecutor(CORE_POOL_SIZE,
                        MAXIMUM_POOL_SIZE,
                        100, TimeUnit.MILLISECONDS,
                        new ArrayBlockingQueue<Runnable>(QUEUE_SIZE),
                        new ThreadPoolExecutor.CallerRunsPolicy());

        restClient = new RestClient(args[0]);
//        restClient.setEndpoint("/noop");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String text = null;
        long loaded = 0;
        try {
            while ((text = reader.readLine()) != null) {
                es.execute(new Task(text, args[1], args[2]));
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

    public static class Task implements Runnable {
        private String text;
        private String loadId;
        private String collection;

        public Task(String text, String loadId, String collection) {
            this.text = text;
            this.loadId = loadId;
            this.collection = collection;
        }

        @Override
        public void run() {
            Triple t = new Triple();
            t.setText(text);
            t.setLoadId(loadId);
            t.setCollection(collection);
            restClient.post(t);
        }
    }
}
