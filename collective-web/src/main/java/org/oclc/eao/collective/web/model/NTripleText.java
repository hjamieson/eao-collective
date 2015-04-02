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

package org.oclc.eao.collective.web.model;

/**
 * Description: This is a web artifact that is used to support the submission of triples in their n-triple
 * format as a like of text.  The collection and instance must be carried explicitely.
 * User: jamiesoh
 * Date: 4/2/15
 * Time: 6:10 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public class NTripleText {
    private String text;
    private String collection;
    private String instance;

    public NTripleText(String text, String collection, String instance) {
        this.text = text;
        this.collection = collection;
        this.instance = instance;
    }

    public NTripleText() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }
}
