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

import org.junit.Test;
import org.oclc.eao.collective.api.TripleHelper;

import java.util.regex.Matcher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Description:
 * User: jamiesoh
 * Date: 4/1/15
 * Time: 5:06 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public class RegexAssumptionsTest {
    @Test
    public void testLiteralLanguageMatch(){
        /*
        language literals can look like this:
        "this is some fine text"
        "this text has a language tag"@fr
        "this text has \"embedded\" quotes"
        "this is \"embedded\" quotes and tagged"@en
         */
        String l1 = "\"this is some fine text\"";
        String l2 = "\"this has a language tag\"@fr";
        String l3 = "\"here are some \\\"escaped\\\" quotes to handle\"";
        String l4 = "\"here are some \\\"escaped\\\" quotes with lang\"@fn";
        Matcher m1 = TripleHelper.FRAG_SIMPLE_LITERAL.matcher(l1);
        assertThat(m1.matches(), is(true));
        assertThat(m1.group(1), equalTo("this is some fine text"));

        Matcher m2 = TripleHelper.FRAG_LITERAL_WITH_LANG.matcher(l2);
        assertThat(m2.matches(), is(true));
        assertThat(m2.group(1), equalTo("this has a language tag"));
        assertThat(m2.group(2), equalTo("fr"));

        Matcher m3 = TripleHelper.FRAG_SIMPLE_LITERAL.matcher(l3);
        assertThat(m3.matches(), is(true));
        assertThat(m3.group(1), equalTo("here are some \\\"escaped\\\" quotes to handle"));

        Matcher m4 = TripleHelper.FRAG_LITERAL_WITH_LANG.matcher(l4);
        assertThat(m4.matches(), is(true));
        assertThat(m4.group(1), equalTo("here are some \\\"escaped\\\" quotes with lang"));
        assertThat(m4.group(2), equalTo("fn"));

    }
}
