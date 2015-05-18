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

package org.oclc.archivegraph.mr;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.LazyOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.oclc.archivegraph.model.CreateRequest2;
import org.oclc.archivegraph.utils.ESClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Description: Reads archiveGrid json index commands(1-per-line) and uses the IndexClient to send the index request
 * to the ES cluster.  The input (per-line) is a JSON-ized version of the BULK CREATE statements that ES is expecting,
 * but JSON-ized to remove the need for CRLF.  To use this, pass in the file to load and the ES hostname you want to
 * use.
 * <p/>
 * 5-7-2015 - updated to conform to new input format: index<tab>type<tab>id<tab>json<tab>
 * <p/>
 * User: jamiesoh
 * Date: 3/9/15
 * Time: 4:19 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public class BulkLoad2 extends Configured implements Tool {
    private static final Logger LOG = LoggerFactory.getLogger(BulkLoad2.class);
    public static final String JOB_NAME = "ArchiveGraph ES Bulk Loader2";
    public static final String HOST_LIST_PROPERTY = "org.collective.es.hostlist";
    public static final String INDEX_NAME_OVERRIDE = "index.name.override";

    private enum COUNTERS {
        SUCCESSFUL,
        FAILURES
    }

    public static void main(String[] args) throws Exception {
        System.exit(ToolRunner.run(new BulkLoad2(), args));
    }

    @Override
    public int run(String[] args) throws Exception {
        if (args.length < 2) {
            LOG.error("Usage:" + BulkLoad2.class.getSimpleName()
                      + " <input-file> <es-host-name> [optional-index-override]");
            return 1;
        }
        /*
        args:
        0 = input path
        1 = output path
        2 = index name override (optional)
        rejects = outputpath + ".rejects"
         */
        Configuration conf = getConf();
        if (conf.getClassLoader() == null) {
            conf.setClassLoader(this.getClass().getClassLoader());
        }

        conf.set(HOST_LIST_PROPERTY, args[1]);
        // grab the filename to use for rejects
        String rejectsFile = args[0].substring(args[0].lastIndexOf("/") + 1) + ".rejects";
        LOG.info("rejects will be output to {}", rejectsFile);

        if (args.length > 2) {
            conf.set(INDEX_NAME_OVERRIDE, args[2]);
            LOG.info("index name override({}) requested", args[2]);
        }

        Job job = Job.getInstance(conf, JOB_NAME);
        job.setJarByClass(this.getClass());
        job.setMapperClass(Mapper.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setNumReduceTasks(0);
        TextInputFormat.setInputPaths(job, new Path(args[0]));
        TextOutputFormat.setOutputPath(job, new Path(rejectsFile));
        LazyOutputFormat.setOutputFormatClass(job, TextOutputFormat.class);

        return job.waitForCompletion(true) ? 0 : 1;
    }


    public static class Mapper extends org.apache.hadoop.mapreduce.Mapper<LongWritable, Text, Text, Text> {
        public static final int MAX_SEND_CNT = 1000;
        private Text failureReason = new Text();
        private Text ID = new Text();
        private ESClient indexer;
        private BulkProcessor bulkProcessor;
        private BulkRequestBuilder bulkRequestBuilder;
        private String indexNameOverride;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            indexer = new ESClient(context.getConfiguration().get(HOST_LIST_PROPERTY));
            bulkProcessor = BulkProcessor.builder(indexer.getClient(), new BulkProcessor.Listener() {
                @Override
                public void beforeBulk(long executionId, BulkRequest request) {
                    LOG.trace("we are in beforeBulk");
                }

                @Override
                public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                    LOG.trace("we are in afterBulk");
                    if (response.hasFailures()) {
                        LOG.info("afterBulk(id={}), hasFailures={}, itemsSaved={}", executionId, response.hasFailures(), response.getItems().length);
                        for (BulkItemResponse bir: response.getItems()){
                            LOG.error("item failed: {}", bir.getFailureMessage());
                        }
                    }
                }

                @Override
                public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                    LOG.trace("we are in error afterBulk");
                    LOG.info("afterBulk(id={})",executionId);
                    LOG.error(failure.getMessage(), failure);
                }
            }).setBulkActions(MAX_SEND_CNT)
                    .setBulkSize(new ByteSizeValue(250, ByteSizeUnit.MB))
                    .setConcurrentRequests(0)
                    .build();
            bulkRequestBuilder = indexer.getClient().prepareBulk();

            indexNameOverride = context.getConfiguration().get(INDEX_NAME_OVERRIDE);
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            bulkProcessor.awaitClose(5, TimeUnit.MINUTES);
            indexer.close();
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            /*
             * we get a row that has the triples we want to emit
             */
            try {
                CreateRequest2 create = new CreateRequest2(value.toString());
                bulkProcessor.add(new IndexRequest((indexNameOverride == null) ?
                        create.getIndex() : indexNameOverride,
                        create.getType(), UUID.randomUUID().toString()).source(create.getDoc()));
                context.getCounter(COUNTERS.SUCCESSFUL).increment(1l);
            } catch (Exception e) {
                // write the fail to the output file
                failureReason.set(e.getMessage());
                ID.set(value);
                context.getCounter(COUNTERS.FAILURES).increment(1l);
                context.write(ID, failureReason);
            }
        }
    }

}
