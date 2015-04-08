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

import org.oclc.eao.collective.api.model.Triple;

import java.util.Collections;
import java.util.List;

/**
 * Description: container for shipping the search results back to the UI.  It includes a token that is used to oage'
 * thru large resultsets.
 * User: jamiesoh
 * Date: 4/7/15
 * Time: 8:12 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public class SearchResults {
    private List<Triple> triples = Collections.<Triple>emptyList();
    private String scrollId;

    public List<Triple> getTriples() {
        return triples;
    }

    public void setTriples(List<Triple> triples) {
        this.triples = triples;
    }

    public String getScrollId() {
        return scrollId;
    }

    public void setScrollId(String scrollId) {
        this.scrollId = scrollId;
    }

    public SearchResults(List<Triple> triples, String scrollId) {
        this.triples = triples;
        this.scrollId = scrollId;
    }

    public SearchResults() {
    }
}
