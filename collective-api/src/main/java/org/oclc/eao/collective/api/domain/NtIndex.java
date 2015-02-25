package org.oclc.eao.collective.api.domain;

import org.oclc.eao.collective.api.model.Triple;

import java.util.List;

/**
 * Description:
 * User: jamiesoh
 * Date: 2/25/15
 * Time: 5:07 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public interface NtIndex {
    /**
     * add this triple to the collective index
     *
     * @param triple
     */
    public void index(Triple triple);

    /**
     * returns the triple with the given id.
     *
     * @param id
     * @return
     */
    public Triple get(String id);

    /**
     * runs a search on triples.
     *
     * @param queryString
     * @return
     */
    public List<Triple> search(String queryString);

    /**
     * delete an indexed triple.
     *
     * @param id
     */
    public void delete(String id);

    /**
     * update a triple.
     *
     * @param triple
     */
    public void update(Triple triple);


}
