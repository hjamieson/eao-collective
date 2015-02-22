package org.oclc.eao.collective.api.domain;

import org.oclc.eao.collective.api.model.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * Description: This interface is used to apply business model constraints upon the data model.
 * User: jamiesoh
 * Date: 2/22/15
 * Time: 2:46 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public class NtService {
    private static final Logger LOG = LoggerFactory.getLogger(NtService.class);

    public static final String ID_TAG = ":id";
    public static final String ORIGIN_TAG = "origin";
    public static final String TEXT_TAG = "text";

    private NtStore ntStore;

    public Triple create(Map<String, String> nt) throws IOException {
        // make sure we don't have an id:
        if (nt.containsKey(ID_TAG)) {
            throw new IllegalArgumentException("id has already been assigned!");
        }
        // make sure we have an origin:
        if (!nt.containsKey(ORIGIN_TAG)) {
            throw new IllegalArgumentException("no origin tag found");
        }
        // make sure we have a text value for the triple:
        if (!nt.containsKey(TEXT_TAG)) {
            throw new IllegalArgumentException("no text tag found");
        }

        Triple triple = new Triple(nt, UUID.randomUUID().toString());
        ntStore.save(triple);
        return triple;

    }

    public Triple get(String id) throws IOException {
        return ntStore.get(id);
    }

    public void delete(String id) throws IOException {
        ntStore.delete(id);
    }

    public NtService() {
    }

    public NtService(NtStore ntStore) {
        this.ntStore = ntStore;
    }

    public void setNtStore(NtStore ntStore) {
        this.ntStore = ntStore;
    }
}
