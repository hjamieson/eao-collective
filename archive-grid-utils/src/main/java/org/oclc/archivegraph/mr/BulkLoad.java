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

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.oclc.archivegraph.model.CreateRequest;
import org.oclc.archivegraph.utils.ESClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Description: Reads archiveGrid json index commands(1-per-line) and uses the IndexClient to send the index request
 * to the ES cluster.  The input (per-line) is a JSON-ized version of the BULK CREATE statements that ES is expecting,
 * but JSON-ized to remove the need for CRLF.
 * User: jamiesoh
 * Date: 3/9/15
 * Time: 4:19 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public class BulkLoad extends Configured implements Tool {
    private static final Logger LOG = LoggerFactory.getLogger(BulkLoad.class);
    public static final String JOB_NAME = "ArchiveGraph ES Bulk Loader";
    public static final String HOST_LIST_PROPERTY = "org.collective.es.hostlist";

    private String[] hosts = {"tripstoreap01dxdu.dev.oclc.org", "tripstoreap02dxdu.dev.oclc.org"};

    private enum COUNTERS {
        SUCCESSFUL,
        FAILURES
    }

    public static void main(String[] args) throws Exception {
        System.exit(ToolRunner.run(new BulkLoad(), args));
    }

    @Override
    public int run(String[] args) throws Exception {
        if (args.length != 2) {
            LOG.error("Usage:" + BulkLoad.class.getSimpleName() + " <input-file> <rejects-file>");
            return 1;
        }
        /*
        args:
        0 = input path
        1 = output path
         */
        Configuration conf = getConf();
        if (conf.getClassLoader() == null) {
            conf.setClassLoader(this.getClass().getClassLoader());
        }

        conf.setStrings(HOST_LIST_PROPERTY, hosts);


        Job job = Job.getInstance(conf, JOB_NAME);
        job.setJarByClass(this.getClass());
        job.setMapperClass(Mapper.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setNumReduceTasks(0);
        TextInputFormat.setInputPaths(job, new Path(args[0]));
        TextOutputFormat.setOutputPath(job, new Path(args[1]));
        LazyOutputFormat.setOutputFormatClass(job, TextOutputFormat.class);

        return job.waitForCompletion(true) ? 0 : 1;
    }


    public static class Mapper extends org.apache.hadoop.mapreduce.Mapper<LongWritable, Text, Text, Text> {
        private Text failureReason = new Text();
        private Text ID = new Text();
        private ESClient indexer;
        private ObjectMapper om = new ObjectMapper();

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            indexer = new ESClient(context.getConfiguration().getStrings(HOST_LIST_PROPERTY));
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            indexer.close();
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            /*
             * we get a row that has the triples we want to emit
             */
            try {
                CreateRequest create = om.readValue(value.getBytes(), CreateRequest.class);
                indexer.exec(create);
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
