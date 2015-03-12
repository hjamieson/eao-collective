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

package org.oclc.eao.collective.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Description:
 * User: jamiesoh
 * Date: 2/22/15
 * Time: 12:14 PM
 * &copy;2013 OCLC Data Architecture Group
 */
@SpringBootApplication
//@EnableWebMvc
@Configuration
//@EnableAutoConfiguration
@ComponentScan("org.oclc.eao.collective.web")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
