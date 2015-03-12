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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.fail;

/**
 * User: jamiesoh
 * Date: 3/3/15
 * Time: 3:12 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public class TripleHelperTest {
    private String[] triple = {
            "dummy",
            "<http://viaf.org/viaf/100000538/> <http://xmlns.com/foaf/0.1/primaryTopic> <http://viaf.org/viaf/100000538> .",
            "<http://viaf.org/viaf/100000593> <http://schema.org/alternateName> \"Fighting Irish\" . ",
            "<http://viaf.org/viaf/100188011> <http://schema.org/birthDate> \"Curious George\\\\\"@en .",
            "<http://en.wikipedia.org/wiki/Helium> <http://example.org/elements/atomicNumber> \"2\"^^<http://www.w3.org/2001/XMLSchema#integer> . ",
            "<http://en.wikipedia.org/wiki/Helium> <http://example.org/elements/specificGravity> \"1.663E-4\"^^<http://www.w3.org/2001/XMLSchema#double> .",
            "<http://viaf.org/viaf/100000538/> <http://xmlns.com/foaf/0.1/primaryTopic> <http://viaf.org/viaf/100000538> . ",
    };

    @Test
    public void testGetSubject() {
        assertThat(TripleHelper.getSubject(triple[1]), equalTo("http://viaf.org/viaf/100000538/"));
        assertThat(TripleHelper.getSubject(triple[2]), equalTo("http://viaf.org/viaf/100000593"));
        assertThat(TripleHelper.getSubject(triple[3]), equalTo("http://viaf.org/viaf/100188011"));
        assertThat(TripleHelper.getSubject(triple[4]), equalTo("http://en.wikipedia.org/wiki/Helium"));
        assertThat(TripleHelper.getSubject(triple[5]), equalTo("http://en.wikipedia.org/wiki/Helium"));
        assertThat(TripleHelper.getSubject(triple[6]), equalTo("http://viaf.org/viaf/100000538/"));

    }

    @Test
    public void testGetPredicate() {
        assertThat(TripleHelper.getPredicate(triple[1]), equalTo("http://xmlns.com/foaf/0.1/primaryTopic"));
        assertThat(TripleHelper.getPredicate(triple[2]), equalTo("http://schema.org/alternateName"));
        assertThat(TripleHelper.getPredicate(triple[3]), equalTo("http://schema.org/birthDate"));
        assertThat(TripleHelper.getPredicate(triple[4]), equalTo("http://example.org/elements/atomicNumber"));
    }

    @Test
    public void testGetObjectFragment() {
        assertThat(TripleHelper.getObjectFragment(triple[1]), equalTo("<http://viaf.org/viaf/100000538>"));
        assertThat(TripleHelper.getObjectFragment(triple[2]), equalTo("\"Fighting Irish\""));
        assertThat(TripleHelper.getObjectFragment(triple[3]), equalTo("\"Curious George\\\\\"@en"));
        assertThat(TripleHelper.getObjectFragment(triple[4]), equalTo("\"2\"^^<http://www.w3.org/2001/XMLSchema#integer>"));
    }


    @Test
    public void testGetLiteralType() {
        assertThat(TripleHelper.getObjectType(triple[1]), equalTo(TripleHelper.ObjectType.URI));
        assertThat(TripleHelper.getObjectType(triple[2]), equalTo(TripleHelper.ObjectType.LITERAL));
        assertThat(TripleHelper.getObjectType(triple[3]), equalTo(TripleHelper.ObjectType.LITERAL_WITH_LANG));
        assertThat(TripleHelper.getObjectType(triple[4]), equalTo(TripleHelper.ObjectType.LITERAL_WITH_TYPE));
        assertThat(TripleHelper.getObjectType(triple[5]), equalTo(TripleHelper.ObjectType.LITERAL_WITH_TYPE));
    }

    @Test
    public void testIsWellFormed() {
        assertThat(TripleHelper.isWellFormed(triple[1]), is(true));
        assertThat(TripleHelper.isWellFormed(triple[2]), is(true));
        assertThat(TripleHelper.isWellFormed(triple[3]), is(true));
        assertThat(TripleHelper.isWellFormed(triple[4]), is(true));
        assertThat(TripleHelper.isWellFormed(triple[5]), is(true));
        assertThat(TripleHelper.isWellFormed("total garbage"), is(false));

    }

    @Test
    public void testGetLiteral() {

    }

    @Test
    public void testGetLiteralLang() {
        assertThat(TripleHelper.getLanguage(triple[3]), equalTo("en"));

    }

    @Test
    public void testBadFactory(){
        Triple t1 = TripleHelper.makeTriple("<foo><bar><baz> .","hughs","12345");
        try {
            TripleHelper.makeTriple("<foo <bar><baz> .","hughs","12345");
            fail("should throw");
        } catch (Exception e) {
        }
        try {
            TripleHelper.makeTriple("<foo> <bar><baz> ","hughs","12345");
            fail("missing .; should throw");
        } catch (Exception e) {
        }

    }

}
