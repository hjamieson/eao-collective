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

package org.oclc.eao.collective.mr;

import org.apache.commons.lang.Validate;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.oclc.eao.collective.api.model.Triple;
import org.oclc.eao.collective.store.hbase.IdFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * Description: (deprecated)Job that takes a file with n-triples (1-per-line) and loads them into the collective.
 * User: jamiesoh
 * Date: 2/26/15
 * Time: 2:49 PM
 * &copy;2013 OCLC Data Architecture Group
 */
@Deprecated
public class HBaseLoader extends Configured implements Tool {
    private static final Logger LOG = LoggerFactory.getLogger(HBaseLoader.class);
    private static final String DEFAULT_TABLE = "eao_collective";
    public static final String JOB_TITLE = "Collective (HBase) Bulk Loader";
    public static final byte[] DEFAULT_CF = Bytes.toBytes("data");
    public static final String LOADID_KEY = "loader.loadid";
    public static final String COLLECTION_KEY = "loader.collection";

    public static class NtLoaderMapper extends Mapper<LongWritable, Text, ImmutableBytesWritable, Put> {
        private ImmutableBytesWritable ROWKEY = new ImmutableBytesWritable();
        private String loadId;
        private String collection;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            loadId = context.getConfiguration().get(LOADID_KEY);
            Validate.notEmpty(loadId, "unspecified LOADID value");
            collection = context.getConfiguration().get(COLLECTION_KEY);
            Validate.notEmpty(collection, "unspecified COLLECTION value");
        }

        @Override
        protected void map(LongWritable key, Text line, Context context) throws IOException, InterruptedException {
            // each row arrives as a line of text(we expect a well-formed n-triple).  We create the Triple, assign
            // an id & weight, and the origin is passed in form the args as a conf variable.
            Triple triple = new Triple();
            triple.setId(IdFactory.getNext());
            triple.setText(line.toString());
            triple.setLoadId(loadId);
            triple.setCollection(collection);
            Put put = getPut(triple);
            ROWKEY.set(put.getRow());
            context.write(ROWKEY, put);
        }
    }

    private static Put getPut(Triple nt) {
        Put put = new Put(Bytes.toBytes(nt.getId()));
        put.add(DEFAULT_CF, Triple.ID_TAG.getBytes(), nt.getId().getBytes());
        put.add(DEFAULT_CF, Triple.TEXT_TAG.getBytes(), nt.getText().getBytes());
        put.add(DEFAULT_CF, Triple.LOADID_TAG.getBytes(), nt.getLoadId().getBytes());
        put.add(DEFAULT_CF, Triple.COLLECTION_TAG.getBytes(), nt.getCollection().getBytes());
        put.add(DEFAULT_CF, Triple.WEIGHT_TAG.getBytes(), Bytes.toBytes(nt.getWeight()));
        for (Map.Entry<String, String> e : nt.entrySet()) {
            put.add(DEFAULT_CF, e.getKey().getBytes(), e.getValue().getBytes());
        }
        return put;
    }

    public static void main(String[] args) {
        try {
            int run = ToolRunner.run(new HBaseLoader(), args);
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
            System.err.println("Usage: " + HBaseLoader.class.getSimpleName() + " <nt-file-path> <collection> <loadId>");
            System.exit(1);
        }
        // args[0] = path to file
        // args[1] = collection value
        // args[2] = loadId value

        Configuration conf = getConf();

        conf.set(COLLECTION_KEY, args[1]);
        conf.set(LOADID_KEY, args[2]);
        if (conf.getClassLoader() == null) {
            conf.setClassLoader(this.getClass().getClassLoader());
        }

        Job job = Job.getInstance(conf, JOB_TITLE);
        job.setJarByClass(this.getClass());
        job.setMapperClass(NtLoaderMapper.class);
        job.setNumReduceTasks(0);
        TextInputFormat.setInputPaths(job, new Path(args[0]));
        TableMapReduceUtil.initTableReducerJob(DEFAULT_TABLE, null, job);

        return job.waitForCompletion(true) ? 0 : 1;
    }

}
