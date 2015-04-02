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

package org.oclc.eao.collective.store.mr;

import org.apache.commons.lang.Validate;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.LazyOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.oclc.eao.collective.api.TripleHelper;
import org.oclc.eao.collective.api.model.Triple;
import org.oclc.eao.collective.indexer.ElasticCommandBuilder;
import org.oclc.eao.collective.store.hbase.HBaseStore;
import org.oclc.eao.collective.store.hbase.IdFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Description: Job that takes a file with n-triples (1-per-line) and loads them into the collective.  It also
 * creates an elasticsearch BULK index commands that can be used to POST to elasticsearch.
 * User: jamiesoh
 * Date: 2/26/15
 * Time: 2:49 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public class BulkTripleLoader extends Configured implements Tool {
    private static final Logger LOG = LoggerFactory.getLogger(BulkTripleLoader.class);
    private static final String DEFAULT_TABLE = "eao_collective";
    public static final String JOB_TITLE = "Collective (HBase) Bulk Loader";
    public static final byte[] DEFAULT_CF = Bytes.toBytes("data");
    public static final String LOADID_KEY = "loader.loadid";
    public static final String COLLECTION_KEY = "loader.collection";
    public static final String TABLENAME_KEY = "loader.table.name";

    public static class BulkLoadMapper extends Mapper<LongWritable, Text, Text, NullWritable> {
        private Text ROWKEY = new Text();
        private String loadId;
        private String collection;
        private HTable collective;
        private MultipleOutputs mos;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            loadId = context.getConfiguration().get(LOADID_KEY);
            Validate.notEmpty(loadId, "unspecified LOADID value");
            collection = context.getConfiguration().get(COLLECTION_KEY);
            Validate.notEmpty(collection, "unspecified COLLECTION value");
            String tableName = context.getConfiguration().get(TABLENAME_KEY);
            Validate.notEmpty(tableName, "unspecified TABLENAME value");
            HBaseAdmin hAdmin = new HBaseAdmin(context.getConfiguration());
            Validate.isTrue(hAdmin.tableExists(tableName), "table specified does not exist!");
            collective = new HTable(context.getConfiguration(), tableName);
            collective.setAutoFlush(false);
            mos = new MultipleOutputs(context);
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            super.cleanup(context);
            collective.flushCommits();
            collective.close();
            mos.close();
        }

        @Override
        protected void map(LongWritable key, Text line, Context context) throws IOException, InterruptedException {
            // each row arrives as a line of text(we expect a well-formed n-triple).  We create the Triple, assign
            // an id & weight, and the origin is passed in form the args as a conf variable.
            String text = line.toString();
            if (TripleHelper.isWellFormed(text)) {
                Triple triple = TripleHelper.makeTriple(text, collection, loadId);
                triple.setId(IdFactory.getNext());
                Put put = HBaseStore.getPut(triple);
                collective.put(put);
                context.getCounter("Collective", "successful PUTs").increment(1l);

                // emit the index build command to the file
                String bulkCmd = ElasticCommandBuilder.asBulkIndex(triple);
                bulkCmd = bulkCmd.substring(0, bulkCmd.length() - 1); // strip off CRLF
                ROWKEY.set(bulkCmd);
                mos.write(ROWKEY, NullWritable.get(), "index/part");
            } else {    // bad input
                ROWKEY.set(text);
                mos.write(ROWKEY, NullWritable.get(), "rejects/part");
                context.getCounter("Errors", "bad input").increment(1l);
            }
        }
    }


    public static void main(String[] args) {
        try {
            int run = ToolRunner.run(new BulkTripleLoader(), args);
            System.exit(run);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            System.exit(1);
        }
    }

    /**
     * we expect three arguments: (0)= name of file, (1) = origin value, and (2) = collection value.
     *
     * @param args
     * @return
     * @throws Exception
     */
    @Override
    public int run(String[] args) throws Exception {
        if (args.length != 3) {
            System.err.println(
                    "Usage: " + BulkTripleLoader.class.getSimpleName() + " <nt-file-path> <collection> <loadId>");
            System.exit(1);
        }
        // args[0] = path to file
        // args[1] = collection value
        // args[2] = loadId value

        this.setConf(HBaseConfiguration.create(getConf()));
        Configuration conf = getConf();
        conf.set(COLLECTION_KEY, args[1]);
        conf.set(LOADID_KEY, args[2]);
        conf.set(TABLENAME_KEY, DEFAULT_TABLE);
        if (conf.getClassLoader() == null) {
            conf.setClassLoader(this.getClass().getClassLoader());
        }


        Job job = Job.getInstance(conf, JOB_TITLE);
        job.setJarByClass(this.getClass());
        job.setMapperClass(BulkLoadMapper.class);
        job.setNumReduceTasks(0);
        TextInputFormat.setInputPaths(job, new Path(args[0]));
        LazyOutputFormat.setOutputFormatClass(job, TextOutputFormat.class);
        TextOutputFormat.setOutputPath(job, new Path(args[0] + "-out"));

        return job.waitForCompletion(true) ? 0 : 1;
    }

}
