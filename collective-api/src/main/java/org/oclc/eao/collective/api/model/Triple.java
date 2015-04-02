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

package org.oclc.eao.collective.api.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.oclc.eao.collective.api.TripleHelper;

/**
 * Description: The venerable RDF triple as it exists in this collective framework.  Its really just a hashmap.
 * User: jamiesoh
 * Date: 2/4/15
 * Time: 6:36 AM
 * &copy;2013 OCLC Data Architecture Group
 */
public class Triple {
    public static final double DEFAULT_WEIGHT = 0.5;

    private String id;
    private String subject;
    private String predicate;
    private ObjectHolder object;
    private double weight;
    private String collection;
    private String loadId;

    public Triple() {
        setWeight(DEFAULT_WEIGHT);
    }

    public Triple(String subj, String pred, String literal) {
        setSubject(subj);
        setPredicate(pred);
        setObject(new SimpleLiteral(literal));
        setWeight(DEFAULT_WEIGHT);
    }

    public Triple(String subj, String pred, String literal, String lang) {
        setSubject(subj);
        setPredicate(pred);
        setObject(new SimpleLiteral(literal, lang));
        setWeight(DEFAULT_WEIGHT);
    }

    public Triple(String subject, String predicate, ObjectHolder object, double weight, String collection, String loadId) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
        this.weight = weight;
        this.collection = collection;
        this.loadId = loadId;
    }

    @Override
    public String toString() {
        return TripleHelper.toJSON(this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public ObjectHolder getObject() {
        return object;
    }

    public void setObject(ObjectHolder object) {
        this.object = object;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getLoadId() {
        return loadId;
    }

    public void setLoadId(String loadId) {
        this.loadId = loadId;
    }
}
