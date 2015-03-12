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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Description:  This controller serves up the pages for browsing.
 * User: jamiesoh
 * Date: 3/12/15
 * Time: 10:50 AM
 * &copy;2013 OCLC Data Architecture Group
 */
@Controller
public class BrowseController {
    private static final Logger LOG = LoggerFactory.getLogger(BrowseController.class);

    @RequestMapping("/browsex")
    public String browse(){
        return "redirect:browse.html";
    }
}
