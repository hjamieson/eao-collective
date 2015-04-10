package org.oclc.eao.collective.api.domain;

import org.apache.commons.lang.Validate;
import org.oclc.eao.collective.api.model.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
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
    public static final String TRIPLE_ALREADY_PERSISTED = "id has already been assigned!";


    private NtStore ntStore;

    public Triple create(Triple nt) throws IOException {
        Validate.isTrue(nt.getId() == null, TRIPLE_ALREADY_PERSISTED);
        Validate.notEmpty(nt.getInstance(), "instance missing");
        Validate.notEmpty(nt.getSubject(), "subject missing");
        Validate.notEmpty(nt.getSubject(), "subject missing");
        Validate.notEmpty(nt.getPredicate(), "predicate missing");
        Validate.notNull(nt.getObject(), "object missing");
        Validate.notEmpty(nt.getCollection(), "no COLLECTION tag found");
        nt.setId(UUID.randomUUID().toString());

        ntStore.save(nt);
        return nt;
    }

    public Triple get(String id) throws IOException {
        return ntStore.get(id);
    }

    public void delete(String id) throws IOException {
        ntStore.delete(id);
    }

    public List<Triple> get(List<String> keyList) throws IOException {
        return ntStore.get(keyList);
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
