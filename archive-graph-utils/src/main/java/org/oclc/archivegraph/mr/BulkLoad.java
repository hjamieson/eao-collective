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
import org.oclc.archivegraph.model.CreateRequest2;
import org.oclc.archivegraph.utils.ESClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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
public class BulkLoad extends Configured implements Tool {
    private static final Logger LOG = LoggerFactory.getLogger(BulkLoad.class);
    public static final String JOB_NAME = "ArchiveGraph ES Bulk Loader";
    public static final String HOST_LIST_PROPERTY = "org.collective.es.hostlist";

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
            LOG.error("Usage:" + BulkLoad.class.getSimpleName() + " <input-file> <es-host-name>");
            return 1;
        }
        /*
        args:
        0 = input path
        1 = output path
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
        private Text failureReason = new Text();
        private Text ID = new Text();
        private ESClient indexer;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            indexer = new ESClient(context.getConfiguration().get(HOST_LIST_PROPERTY));
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
                CreateRequest2 create = new CreateRequest2(value.toString());
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
