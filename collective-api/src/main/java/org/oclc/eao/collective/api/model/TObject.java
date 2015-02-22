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

/**
 * Description: A wrapper around the object of a triple that give easy access to the type, language, etc.
 * User: jamiesoh
 * Date: 2/4/15
 * Time: 6:40 AM
 * &copy;2013 OCLC Data Architecture Group
 */
public class TObject {
    public enum Type {
        RESOURCE,
        LITERAL,
        TYPED_LITERAL
    }

    private String rawData;
    private Type type;

    public Type getType() {
        return type;
    }

    public String getText() {
        return rawData;
    }

    public String getLang(){
        return null;
    }
}
