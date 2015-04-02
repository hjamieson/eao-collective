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

/**
 * Description: a holder for a simple text literal with optional lang tagging.
 * User: jamiesoh
 * Date: 4/1/15
 * Time: 4:08 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public class TypedLiteral extends ObjectHolder {
    private String text;
    private String xmlType;

    public TypedLiteral() {
    }

    public TypedLiteral(String text, String xmlType) {
        this.text = text;
        this.xmlType = xmlType;
    }

    @Override
    public String toString() {
        if (xmlType != null) {
            return String.format("\"%1$s\"^^%2$s", text, xmlType);
        } else {
            return String.format("\"%1$s\"", text);
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getXmlType() {
        return xmlType;
    }

    public void setXmlType(String xmlType) {
        this.xmlType = xmlType;
    }
}
