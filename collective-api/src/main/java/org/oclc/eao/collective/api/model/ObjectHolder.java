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

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Description: A wrapper around the object of a triple that give easy access to the type, language, etc.
 * User: jamiesoh
 * Date: 2/4/15
 * Time: 6:40 AM
 * &copy;2013 OCLC Data Architecture Group
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class ObjectHolder {
    public enum Type {
        RESOURCE,
        LITERAL,
        LITERAL_WITH_LANG,
        TYPED_LITERAL
    }
}
