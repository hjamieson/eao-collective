package org.oclc.eao.collective.api.domain;

import org.apache.commons.lang.Validate;
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
    public static final String TRIPLE_ALREADY_PERSISTED = "id has already been assigned!";


    private NtStore ntStore;

    public Triple create(Map<String, String> map) throws IOException {
        Validate.isTrue(!map.containsKey(Triple.ID_TAG),TRIPLE_ALREADY_PERSISTED);
        Validate.notEmpty(map.get(Triple.ORIGIN_TAG),"no ORIGIN tag found");
        Validate.notEmpty(map.get(Triple.TEXT_TAG),"no TEXT tag found");
        Validate.notEmpty(map.get(Triple.COLLECTION_TAG), "no COLLECTION tag found");
        Triple triple = new Triple();
        triple.setId(UUID.randomUUID().toString());
        map.remove(Triple.ID_TAG);
        triple.setOrigin(map.get(Triple.ORIGIN_TAG));
        map.remove(Triple.ORIGIN_TAG);
        triple.setText(map.get(Triple.TEXT_TAG));
        map.remove(Triple.TEXT_TAG);
        if (map.containsKey(Triple.WEIGHT_TAG)) {
            triple.setWeight(map.get(Triple.WEIGHT_TAG));
            map.remove(Triple.WEIGHT_TAG);
        }

        triple.putAll(map);

        ntStore.save(triple);
        return triple;
    }

    public Triple create(Triple triple) throws IOException {
        Validate.isTrue(triple.getId() == null, TRIPLE_ALREADY_PERSISTED);
        Validate.notEmpty(triple.getOrigin(), "missing N-Triple origin");
        Validate.notEmpty(triple.getText(), "missing N-Triple text");
        Validate.notEmpty(triple.getCollection(), "missing N-Triple collection name");
        triple.setId(UUID.randomUUID().toString());
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
