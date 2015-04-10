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

package org.oclc.eao.collective.store.mr;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.output.LazyOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.oclc.eao.collective.api.model.Triple;
import org.oclc.eao.collective.indexer.ElasticCommandBuilder;
import org.oclc.eao.collective.indexer.IndexClient;
import org.oclc.eao.collective.store.hbase.HBaseStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Description: Given a origin & collection, send index requests to the ES cluster.  This program reads the triples from
 * the eao_collective
 * hbase table to get the source triples.
 * User: jamiesoh
 * Date: 3/9/15
 * Time: 4:19 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public class DirectIndexer extends Configured implements Tool {
    private static final Logger LOG = LoggerFactory.getLogger(DirectIndexer.class);
    public static final byte[] CF_DATA = "data".getBytes();
    public static final String JOB_NAME = "Collective Direct Indexer";
    public static final byte[] CQ_INSTANCE = "instance".getBytes();
    public static final byte[] CQ_COLLECTION = "collection".getBytes();
    public static final String HOST_LIST_PROPERTY = "org.collective.es.hostlist";
    public static final String COLLECTIVE_TABLE = "eao_collective";

    private String[] hosts = {"tripstoreap01dxdu.dev.oclc.org", "tripstoreap02dxdu.dev.oclc.org"};

    private enum COUNTERS {
        SUCCESSFUL,
        FAILURES
    }

    public static void main(String[] args) throws Exception {
        System.exit(ToolRunner.run(new DirectIndexer(), args));
    }

    @Override
    public int run(String[] args) throws Exception {
        if (args.length != 3) {
            LOG.error("Usage:" + DirectIndexer.class.getSimpleName() + " <collection> <instance> <rejects-file>");
            return 1;
        }
        /*
        args:
        0 = instance
        1 = collection
        2 = output path
         */
        Configuration conf = getConf();
        if (conf.getClassLoader() == null) {
            conf.setClassLoader(this.getClass().getClassLoader());
        }

        conf.setStrings(HOST_LIST_PROPERTY, hosts);

        Scan scan = new Scan();
        scan.addFamily(CF_DATA);
        scan.setFilter(makeFilters(args));
        scan.setCaching(500);
        scan.setCacheBlocks(false);

        Job job = Job.getInstance(conf, JOB_NAME);
        job.setJarByClass(this.getClass());
        TableMapReduceUtil.initTableMapperJob(COLLECTIVE_TABLE, scan, Mapper.class, Text.class, Text.class, job);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setNumReduceTasks(0);

        TextOutputFormat.setOutputPath(job, new Path(args[2]));
        LazyOutputFormat.setOutputFormatClass(job, TextOutputFormat.class);


        return job.waitForCompletion(true) ? 0 : 1;
    }

    private Filter makeFilters(String[] args) {
        SingleColumnValueFilter c2 = new SingleColumnValueFilter(CF_DATA, CQ_COLLECTION, CompareFilter.CompareOp.EQUAL, args[0].getBytes());
        SingleColumnValueFilter c1 = new SingleColumnValueFilter(CF_DATA, CQ_INSTANCE, CompareFilter.CompareOp.EQUAL, args[1].getBytes());
        return new FilterList(FilterList.Operator.MUST_PASS_ALL, c1, c2);
    }

    public static class Mapper extends TableMapper<Text, Text> {
        private Text failureReason = new Text();
        private Text ID = new Text();
        private IndexClient indexer;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            indexer = new IndexClient(context.getConfiguration().getStrings(HOST_LIST_PROPERTY));
            indexer.connect();
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            indexer.close();
        }

        @Override
        protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
            /*
             * we get a row that has the triples we want to emit
             */
            try {
                Triple triple = HBaseStore.getTripleFromResult(value);
                indexer.index(triple);
                context.getCounter(COUNTERS.SUCCESSFUL).increment(1l);
            } catch (Exception e) {
                // write the fail to the output file
                failureReason.set(e.getMessage());
                ID.set(key.get());
                context.getCounter(COUNTERS.FAILURES).increment(1l);
                context.write(ID, failureReason);
            }
        }
    }

}
