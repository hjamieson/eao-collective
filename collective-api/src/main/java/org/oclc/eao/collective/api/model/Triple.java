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

import org.oclc.eao.collective.api.domain.NtService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Description: The venerable RDF triple as it exists in this collective framework.  Its really just a hashmap.
 * User: jamiesoh
 * Date: 2/4/15
 * Time: 6:36 AM
 * &copy;2013 OCLC Data Architecture Group
 */
public class Triple extends HashMap<String, String> {
    private String id;

    public Triple() {
        super();
        setId(UUID.randomUUID().toString());
    }

    public Triple(Map<? extends String, ? extends String> m) {
        super(m);
        setId(UUID.randomUUID().toString());
    }

    public Triple(Map<? extends String, ? extends String> m, String id){
        super(m);
        setId(id);
    }

    public Triple(String id) {
        setId(id);
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
        this.put(NtService.ID_TAG, id);
    }
}
