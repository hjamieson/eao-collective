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

package org.oclc.eao.collective.api.model;

import org.apache.commons.lang.Validate;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: The venerable RDF triple as it exists in this collective framework.  Its really just a hashmap.
 * User: jamiesoh
 * Date: 2/4/15
 * Time: 6:36 AM
 * &copy;2013 OCLC Data Architecture Group
 */
public class Triple extends HashMap<String, String> {
    public static final String ID_TAG = "id";
    public static final String LOADID_TAG = "loadId";
    public static final String TEXT_TAG = "text";
    public static final String WEIGHT_TAG = "weight";
    public static final String COLLECTION_TAG = "collection";
    public static final String DEFAULT_WEIGHT = "0.8";

    public Triple() {
        super();
        setWeight(DEFAULT_WEIGHT);
    }

//    public Triple(String id) {
//        this();
//        setId(id);
//    }

    public Triple(Map<? extends String, ? extends String> m) {
        super(m);
        setWeight(DEFAULT_WEIGHT);
    }

//    public Triple(String id, Map<? extends String, ? extends String> m) {
//        super(m);
//        setId(id);
//        setWeight(DEFAULT_WEIGHT);
//    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("triple{");
        sb.append("id:").append(getId());
        sb.append("}");
        return sb.toString();
    }

    public String getId() {
        return get(ID_TAG);
    }

    public void setId(String id) {
        put(ID_TAG, id);
    }

    public String getLoadId() {
        return get(LOADID_TAG);
    }

    public void setLoadId(String loadId) {
        put(LOADID_TAG, loadId);
    }

    public String getText() {
        return get(TEXT_TAG);
    }

    public void setText(String text) {
        put(TEXT_TAG, text);
    }

    public String getWeight() {
        return get(WEIGHT_TAG);
    }

    public void setWeight(String weight) {
        Validate.isTrue(weight.matches("(0\\.)?[0-9]+"), String.format("invalid weight specification: %1$s", weight));
        put(WEIGHT_TAG, weight);
    }

    public String getCollection() {
        return get(COLLECTION_TAG);
    }

    public void setCollection(String collection) {
        put(COLLECTION_TAG, collection);
    }
}
