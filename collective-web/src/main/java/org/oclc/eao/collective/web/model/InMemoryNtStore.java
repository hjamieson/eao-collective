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

package org.oclc.eao.collective.web.model;

import org.oclc.eao.collective.api.domain.NtStore;
import org.oclc.eao.collective.api.model.Triple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: a map-based store suitable for unit testing only.
 * User: jamiesoh
 * Date: 2/22/15
 * Time: 3:25 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public class InMemoryNtStore implements NtStore {
    private Map<String, Triple> map = new HashMap<>();

    @Override
    public void save(Triple nt) {
        map.put(nt.getId(), nt);
    }

    @Override
    public Triple get(String id) {
        return map.get(id);
    }

    @Override
    public List<Triple> find(String query) {
        return Collections.<Triple>emptyList();
    }

    @Override
    public void delete(String id) {
        map.remove(id);
    }

    @Override
    public List<Triple> get(Collection<String> keyList) throws IOException {
        // todo implement something interesting
        return Collections.unmodifiableList(new ArrayList(map.values()));
    }
}
