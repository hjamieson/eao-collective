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

package org.oclc.archivegraph.model;


import org.apache.commons.lang3.Validate;

/**
 * Description:
 * User: jamiesoh
 * Date: 5/5/15
 * Time: 2:11 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public class CreateRequest2 {
    private String index;
    private String type;
    private String id;
    private String doc;

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

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    public CreateRequest2(String index, String type, String id, String doc) {
        this.index = index;
        this.type = type;
        this.id = id;
        this.doc = doc;
    }

    public CreateRequest2(String data){
        parse(data);

    }
    public CreateRequest2() {
    }

    public void parse(String data){
        // make sure we have 4 fields:
        String[] split = data.split("\t");
        Validate.isTrue(split.length == 4, "input record is not 4 fields");
        this.index = split[0].trim();
        this.type = split[1].trim();
        this.id = split[2].trim();
        this.doc = split[3].trim();
        Validate.notEmpty(this.id);
    }

    public boolean needsId(){
        Validate.notEmpty(id, "id field is null");
        return id.equals("-1");
    }
}
