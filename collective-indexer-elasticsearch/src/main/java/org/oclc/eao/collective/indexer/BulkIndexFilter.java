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

package org.oclc.eao.collective.indexer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.oclc.eao.collective.api.TripleHelper;
import org.oclc.eao.collective.api.model.Triple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

/**
 * Description: This class reads a triple-per-line from stdin and emits the proper elasticsearch BULK api
 * formatted INDEX request, which can then be curl'd to the node via CURL POST.  This filter does NOT
 * asBulkIndex the index/type for each operation; instead, we expect that to be given on the URI used on the POST.
 * User: jamiesoh
 * Date: 3/9/15
 * Time: 11:14 AM
 * &copy;2013 OCLC Data Architecture Group
 */
public class BulkIndexFilter {

    private static final ObjectMapper om = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        /*
        args: none yet
         */
        if (args.length != 2) {
            System.err.println("Usage: " + BulkIndexFilter.class.getSimpleName() + " <collection> <instance>");
            System.exit(1);
        }
        String collection = args[0];
        String instance = args[1];

        BufferedReader reader = null;
        reader = new BufferedReader(new InputStreamReader(System.in));
        String data = null;
        try {
            while ((data = reader.readLine()) != null) {
                // todo since the ID is assigned by the store, we really can't do this here!
                Triple triple = TripleHelper.makeTriple(data, collection, instance);
                System.out.print(ElasticCommandBuilder.asBulkIndex(triple));
            }
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

}
