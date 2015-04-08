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

package org.oclc.eao.collective.indexer;

import org.oclc.eao.collective.api.model.Triple;

import java.util.List;

/**
 * Description: container for the response from a scrollable scan request.
 * User: jamiesoh
 * Date: 4/7/15
 * Time: 3:51 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public class IndexSearchResponse {
    private int holdTime;
    private List<String> hits;
    private String scrollId;

    public int getHoldTime() {
        return holdTime;
    }

    public void setHoldTime(int holdTime) {
        this.holdTime = holdTime;
    }

    public List<String> getHits() {
        return hits;
    }

    public void setHits(List<String> hits) {
        this.hits = hits;
    }

    public String getScrollId() {
        return scrollId;
    }

    public void setScrollId(String scrollId) {
        this.scrollId = scrollId;
    }

    public IndexSearchResponse() {
    }

    public IndexSearchResponse(int holdTime, List<String> hits, String scrollId) {
        this.holdTime = holdTime;
        this.hits = hits;
        this.scrollId = scrollId;
    }

    public int hitsCount() {
        return (hits != null) ? hits.size() : 0;
    }
}
