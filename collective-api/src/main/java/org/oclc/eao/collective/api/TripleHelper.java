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

package org.oclc.eao.collective.api;

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
    public static Pattern NT_OBJ_WITH_LANG = Pattern.compile("^.+@(..)$");
    public static Pattern NT_OBJ_WITH_TYPE = Pattern.compile("^.+\\^{2}(.+)$");
    public static Pattern NT_OBJ_LITERAL = Pattern.compile("^\".+");

    //todo need to handle blank node (_:alice, _:bob)

    private TripleHelper() {
    }

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
        Matcher m = NT_SUBJ_PRED.matcher(nTripleString);
        return (m.matches()) ? m.group(1) : "";
    }

    public static String getPredicate(String nTripleString) {
        Matcher m = NT_SUBJ_PRED.matcher(nTripleString);
        return m.matches() ? m.group(2) : "";
    }

    public static String getObjectFragment(String nTripleString) {
        Matcher m = NT_OBJECT_FRAGMENT.matcher(nTripleString);
        return m.matches() ? m.group(1).trim() : "";
    }

    public enum ObjectType {
        LITERAL,
        LITERAL_WITH_LANG,
        LITERAL_WITH_TYPE,
        URI
    }

    public static ObjectType getObjectType(String nTripleString) {
        String frag = getObjectFragment(nTripleString);
        if (NT_OBJ_WITH_TYPE.matcher(frag).matches()) {
            return ObjectType.LITERAL_WITH_TYPE;
        } else if (NT_OBJ_WITH_LANG.matcher(frag).matches()) {
            return ObjectType.LITERAL_WITH_LANG;
        } else if (NT_OBJ_LITERAL.matcher(frag).matches()) {
            return ObjectType.LITERAL;
        } else {
            return ObjectType.URI;
        }
    }

    public static String getLanguage(String nTripleString) {
        if (getObjectType(nTripleString) == ObjectType.LITERAL_WITH_LANG) {
            Matcher m = NT_OBJ_WITH_LANG.matcher(getObjectFragment(nTripleString));
            if (m.matches()) {
                return m.group(1);
            } else {
                return "";
            }
        }
        throw new IllegalArgumentException("no lang tag available");
    }

}
