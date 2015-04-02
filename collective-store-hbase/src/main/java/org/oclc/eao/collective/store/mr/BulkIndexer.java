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
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.oclc.eao.collective.api.model.Triple;
import org.oclc.eao.collective.indexer.ElasticCommandBuilder;
import org.oclc.eao.collective.store.hbase.HBaseStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.NavigableMap;

/**
 * Description: Given a origin & collection, generate the elasticsearch BULK index statements that can
 * be posted to the elasticsearch cluster.  This program reads the triples from the eao_collective
 * hbase table to get the source triples.
 * User: jamiesoh
 * Date: 3/9/15
 * Time: 4:19 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public class BulkIndexer extends Configured implements Tool {
    private static final Logger LOG = LoggerFactory.getLogger(BulkIndexer.class);
    public static final byte[] CF_DATA = "data".getBytes();
    public static final String JOB_NAME = "Bulk Index Dump";
    public static final byte[] CQ_LOADID = "loadId".getBytes();
    public static final byte[] CQ_COLLECTION = "collection".getBytes();

    public static void main(String[] args) throws Exception {
        System.exit(ToolRunner.run(new BulkIndexer(), args));
    }

    @Override
    public int run(String[] args) throws Exception {
        if (args.length != 3) {
            LOG.error("Usage:" + BulkIndexer.class.getSimpleName() + " <loadId> <collection> <output-path>");
            return 1;
        }
        /*
        args:
        0 = loadId
        1 = collection
        2 = output path
         */
        Configuration conf = getConf();
        if (conf.getClassLoader() == null) {
            conf.setClassLoader(this.getClass().getClassLoader());
        }

        Scan scan = new Scan();
        scan.addFamily(CF_DATA);
        scan.setFilter(makeFilters(args));
        scan.setCaching(500);
        scan.setCacheBlocks(false);

        Job job = Job.getInstance(conf, JOB_NAME);
        job.setJarByClass(this.getClass());
        TableMapReduceUtil.initTableMapperJob("eao_collective", scan, Mapper.class, Text.class, Text.class, job);

        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);
        job.setNumReduceTasks(0);

        TextOutputFormat.setOutputPath(job, new Path(args[2]));

        return job.waitForCompletion(true) ? 0 : 1;
    }

    private Filter makeFilters(String[] args) {
        SingleColumnValueFilter c1 = new SingleColumnValueFilter(CF_DATA, CQ_LOADID, CompareFilter.CompareOp.EQUAL, args[0].getBytes());
        SingleColumnValueFilter c2 = new SingleColumnValueFilter(CF_DATA, CQ_COLLECTION, CompareFilter.CompareOp.EQUAL, args[1].getBytes());
        return new FilterList(FilterList.Operator.MUST_PASS_ALL, c1, c2);
    }

    public static class Mapper extends TableMapper<NullWritable, Text> {
        private Text outVal = new Text();

        @Override
        protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
            /*
             * we get a row that has the triples we want to emit
             */
            Triple triple = HBaseStore.getTripleFromResult(value);
            // note - asBulkIndex adds a CRLF at the end of the index command that we must remove before we
            // write it to prevent the outputformat from adding a NL in the output between commands.
            String spam = ElasticCommandBuilder.asBulkIndex(triple);
            spam = spam.substring(0, spam.length()-1);
            outVal.set(spam);
            context.write(NullWritable.get(), outVal);
        }
    }

}
