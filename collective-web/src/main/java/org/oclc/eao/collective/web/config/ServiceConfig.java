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

package org.oclc.eao.collective.web.config;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.oclc.eao.collective.api.domain.NtService;
import org.oclc.eao.collective.api.domain.NtStore;
import org.oclc.eao.collective.indexer.IndexClient;
import org.oclc.eao.collective.store.hbase.HBaseStore;
import org.oclc.eao.collective.web.model.InMemoryNtStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Description:
 * User: jamiesoh
 * Date: 2/22/15
 * Time: 3:15 PM
 * &copy;2013 OCLC Data Architecture Group
 */
@Configuration
public class ServiceConfig {
    @Value("${index.host.list}")
    private String[] indexHostList;

    @Bean
    NtStore ntStore() {
        return new HBaseStore(HBaseConfiguration.create());
    }

    @Bean
    NtService ntService() {
        return new NtService(ntStore());
    }

    @Bean
    IndexClient indexClient() {
        return new IndexClient(indexHostList).connect();
    }
}
