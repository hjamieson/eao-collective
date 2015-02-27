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
        if (args.length!=1){
            System.err.println("");
        }
    }
}
