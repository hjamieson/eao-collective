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

package org.oclc.eao.collective.indexer.model;

/**
 * Description:
 * User: jamiesoh
 * Date: 4/10/15
 * Time: 5:23 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public class SearchRequest {
    private String collection;
    private String instance;
    private String subject;
    private String predicate;
    private String object;
    private int maxRows;
    private String scrollId;

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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public int getMaxRows() {
        return maxRows;
    }

    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }

    public String getScrollId() {
        return scrollId;
    }

    public void setScrollId(String scrollId) {
        this.scrollId = scrollId;
    }

    public SearchRequest(String collection, String instance, String subject, String predicate, String object, int maxRows, String scrollId) {
        this.collection = collection;
        this.instance = instance;
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
        this.maxRows = maxRows;
        this.scrollId = scrollId;
    }

    public SearchRequest() {
    }
}
