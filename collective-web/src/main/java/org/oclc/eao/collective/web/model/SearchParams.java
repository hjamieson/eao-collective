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
 * Description:
 * User: jamiesoh
 * Date: 4/6/15
 * Time: 11:47 AM
 * &copy;2013 OCLC Data Architecture Group
 */
public class SearchParams {
    private String subject;
    private String predicate;
    private String object;
    private int maxRows;

    public SearchParams() {
    }

    public SearchParams(String subject, String predicate, String object, int maxRows) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
        this.maxRows = maxRows;
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
}
