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

package org.oclc.eao.collective.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.Validate;
import org.oclc.eao.collective.api.model.ObjectHolder;
import org.oclc.eao.collective.api.model.SimpleLiteral;
import org.oclc.eao.collective.api.model.Triple;
import org.oclc.eao.collective.api.model.TypedLiteral;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: provides some handy helper functions that are probably useful thru out the
 * project when we want to deal with raw n-triple forms of triples.
 * User: jamiesoh
 * Date: 3/3/15
 * Time: 2:29 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public class TripleHelper {

    public static Pattern NT_3URI = Pattern.compile("<([^>]+)>\\s*<([^>]+)>\\s*<([^>]+)>\\s*[.]\\s*");
    public static Pattern NT_OBJECT_FRAGMENT = Pattern.compile("<[^>]+>\\s*<[^>]+>\\s*(.+)\\s*[.]\\s*");
    public static Pattern NT_SUBJ_PRED = Pattern.compile("<([^>]+)>\\s*<([^>]+)>.+");
    public static Pattern NT_LITERAL = Pattern.compile("<([^>]+)>\\s*<([^>]+)>\\s*(\".*)\\s+[.]\\s*$");
    public static Pattern NT_LITERAL_TYPE = Pattern.compile("<([^>]+)>\\s*<([^>]+)>\\s*(\".*) [.]\\s*$");

    // object patterns for helper methods
    public static Pattern FRAG_LITERAL_WITH_LANG = Pattern.compile("^\"(.+)\"@(..)$");
    public static Pattern FRAG_TYPED_LITERAL = Pattern.compile("^(.+)\\^{2}(.+)$");
    public static Pattern FRAG_SIMPLE_LITERAL = Pattern.compile("^\"(.+)\"");

    //todo need to handle blank node (_:alice, _:bob)

    private static ObjectMapper objectMapper = new ObjectMapper();

    private TripleHelper() {
    }

    /**
     * checks an n-triple expression for well-formed-ness.
     * @param nTripleString
     * @return
     */
    public static boolean isWellFormed(String nTripleString) {
        boolean good = true;

        Matcher m = NT_3URI.matcher(nTripleString);
        if (!m.matches()) {
            m = NT_LITERAL.matcher(nTripleString);
            if (!m.matches()) {
                m = NT_LITERAL_TYPE.matcher(nTripleString);
                if (!m.matches()) {
                    good = false;
                }
            }
        }
        return good;
    }

    public static String getSubject(String nTripleString) {
        try {
            Matcher m = NT_SUBJ_PRED.matcher(nTripleString);
            return (m.matches()) ? m.group(1) : "";
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() + "::" + nTripleString);
        }
    }

    public static String getPredicate(String nTripleString) {
        Matcher m = NT_SUBJ_PRED.matcher(nTripleString);
        return m.matches() ? m.group(2) : "";
    }

    public static ObjectHolder getObjectHolder(String objectFragment) {
        //todo we only handle text literals here; this needs widening!!
        ObjectHolder.Type ot = getObjectType(objectFragment);
        if (ot == ObjectHolder.Type.LITERAL) {
            return new SimpleLiteral(objectFragment);
        } else if (ot == ObjectHolder.Type.LITERAL_WITH_LANG) {
            Matcher m = FRAG_LITERAL_WITH_LANG.matcher(objectFragment);
            m.matches();
            return new SimpleLiteral(m.group(1), m.group(2));
        } else if (ot == ObjectHolder.Type.TYPED_LITERAL){
            Matcher m = FRAG_TYPED_LITERAL.matcher(objectFragment);
            m.matches();
            return new TypedLiteral(m.group(1), m.group(2));
        }
        throw new UnsupportedOperationException("unsupported object type: " + objectFragment);
    }

    /**
     * returrns the fragment of the n-triple string between the predicate and the '.'.
     * @param nTripleString
     * @return
     */
    public static String getObjectFragment(String nTripleString) {
        Matcher m = NT_OBJECT_FRAGMENT.matcher(nTripleString);
        return m.matches() ? m.group(1).trim() : "";
    }


    public static ObjectHolder.Type getObjectType(String frag) {
        if (FRAG_TYPED_LITERAL.matcher(frag).matches()) {
            return ObjectHolder.Type.TYPED_LITERAL;
        } else if (FRAG_LITERAL_WITH_LANG.matcher(frag).matches()) {
            return ObjectHolder.Type.LITERAL_WITH_LANG;
        } else if (FRAG_SIMPLE_LITERAL.matcher(frag).matches()) {
            return ObjectHolder.Type.LITERAL;
        } else {
            return ObjectHolder.Type.RESOURCE;
        }
    }

    public static String getLanguage(String nTripleString) {
        if (getObjectType(nTripleString) == ObjectHolder.Type.LITERAL_WITH_LANG) {
            Matcher m = FRAG_LITERAL_WITH_LANG.matcher(getObjectFragment(nTripleString));
            if (m.matches()) {
                return m.group(2);
            } else {
                return "";
            }
        }
        throw new IllegalArgumentException("no lang tag available");
    }

    public static Triple makeTriple(String nTripleText, String collection, String instance) {
        Validate.isTrue(isWellFormed(nTripleText));
        Triple triple = new Triple();
        triple.setSubject(TripleHelper.getSubject(nTripleText));
        triple.setPredicate(TripleHelper.getPredicate(nTripleText));
        triple.setObject(TripleHelper.getObjectFragment(nTripleText));
        triple.setCollection(collection);
        triple.setInstance(instance);
        return triple;
    }

    public static String toJSON(Triple triple) {
        try {
            return objectMapper.writeValueAsString(triple);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
