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
    public static final String ID_TAG = ":id";
    public static final String ORIGIN_TAG = "origin";
    public static final String TEXT_TAG = "text";
    public static final String WEIGHT_TAG = "weight";
    public static final String DEFAULT_WEIGHT = "0.8";
    private String id;
    private String origin;
    private String weight;
    private String text;

    public Triple() {
        super();
        setWeight(DEFAULT_WEIGHT);
    }

    public Triple(String id) {
        this();
        this.id = id;
    }

    public Triple(Map<? extends String, ? extends String> m) {
        super(m);
        setWeight(DEFAULT_WEIGHT);
    }

    public Triple(String id, Map<? extends String, ? extends String> m) {
        super(m);
        setId(id);
        setWeight(DEFAULT_WEIGHT);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("triple{");
        sb.append("id:").append(id);
        sb.append("}");
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        Validate.isTrue(weight.matches("(0\\.)?[0-9]+"),String.format("invalid weight specification: %1$s", weight));
        this.weight = weight;
    }
}
