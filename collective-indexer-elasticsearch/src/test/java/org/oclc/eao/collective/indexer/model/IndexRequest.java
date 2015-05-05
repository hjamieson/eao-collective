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

package org.oclc.eao.collective.indexer.model;
/**
 * Description:
 * User: jamiesoh
 * Date: 4/27/15
 * Time: 2:44 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public class IndexRequest {
    String index;
    String type;
    String id;
    DemoData demoData;

    public IndexRequest(String index, String type, String id) {
        this.index = index;
        this.type = type;
        this.id = id;
    }

    public IndexRequest(String index, String type, String id, DemoData demoData) {
        this.index = index;
        this.type = type;
        this.id = id;
        this.demoData = demoData;
    }

    public IndexRequest() {
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DemoData getDemoData() {
        return demoData;
    }

    public void setDemoData(DemoData demoData) {
        this.demoData = demoData;
    }
}
