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

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String object;  // in this model, literals include the quotes so that we can tell its a literal!
    private double weight;
    private String collection;
    private String instance;

    public Triple() {
        setWeight(DEFAULT_WEIGHT);
    }

    public Triple(String subj, String pred, String literal) {
        setSubject(subj);
        setPredicate(pred);
        setObject(literal);
        setWeight(DEFAULT_WEIGHT);
    }


    public Triple(String subject, String predicate, String object, double weight, String collection, String instance) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
        this.weight = weight;
        this.collection = collection;
        this.instance = instance;
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

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
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

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    @JsonIgnore
    public ObjectHolder getObjectHolder(){
        return TripleHelper.getObjectHolder(this.object);
    }
}
