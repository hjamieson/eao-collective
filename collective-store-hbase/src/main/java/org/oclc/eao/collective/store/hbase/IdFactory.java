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

package org.oclc.eao.collective.store.hbase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Description: This class defines what the key is for each new triple that is stored on the table.  Since each
 * storage system may have varying requirements for key distribution, it seems like the right place to own
 * key creation.
 * <p/>
 * User: jamiesoh
 * Date: 3/11/15
 * Time: 5:45 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public class IdFactory {
    private static final Logger LOG = LoggerFactory.getLogger(IdFactory.class);

    public static String getNext() {
        // todo something simple for now.
        return UUID.randomUUID().toString();
    }
}
