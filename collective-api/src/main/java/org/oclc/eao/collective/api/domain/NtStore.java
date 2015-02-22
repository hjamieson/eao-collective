package org.oclc.eao.collective.api.domain;

import org.oclc.eao.collective.api.model.Triple;

import java.io.IOException;
import java.util.List;

/**
 * Description: Interface that provides access to the underlying storage mechanism so that we can use
 * alternate forms of storage such as hbase, mongo, etc.
 * User: jamiesoh
 * Date: 2/22/15
 * Time: 12:48 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public interface NtStore {
    void save(Triple nt) throws IOException;

    Triple get(String id) throws IOException;

    List<Triple> find(String query) throws IOException;

    void delete(String id) throws IOException;

}
