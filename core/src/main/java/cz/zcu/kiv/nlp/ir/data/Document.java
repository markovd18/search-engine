package cz.zcu.kiv.nlp.ir.data;

import cz.zcu.kiv.nlp.ir.index.Indexable;

/**
 *
 * Interface reprezenting a document that may not only be indexed but also
 * searched for.
 * 
 */
public interface Document extends Indexable {

    /**
     * Returns a unique ID of the document.
     * 
     */
    long getId();

}
