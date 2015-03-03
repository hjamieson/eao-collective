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

/**
 * Description: Reads n-triples from stdin and posts them to the collective-web where they
 * will be stored.  You must supply an orgin value on the commandline.
 * User: jamiesoh
 * Date: 2/27/15
 * Time: 4:33 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public class BatchLoader {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println(args);
            System.err.println("Usage: BatchLoader <web-address> <origin>");
            System.exit(1);
        }
        RestClient rest = new RestClient(args[0]);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String text = null;
        long loaded = 0;
        try {
            while ((text = reader.readLine()) != null) {
                Triple t = new Triple();
                t.setText(text);
                t.setOrigin(args[1]);
                rest.post(t);
                loaded++;
                if (loaded % 10000 == 0){
                    System.out.println("records posted: "+ loaded);
                }
            }
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
}
