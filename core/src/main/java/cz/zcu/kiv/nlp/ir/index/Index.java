package cz.zcu.kiv.nlp.ir.index;

import cz.zcu.kiv.nlp.ir.data.Document;
import cz.zcu.kiv.nlp.ir.data.QueryResult;
import cz.zcu.kiv.nlp.ir.index.query.SearchModel;

import java.util.List;
import java.util.Optional;

/**
 * @author tigi
 *
 *         Rozhraní, pro indexaci dokumentů.
 *
 *         Pokud potřebujete/chcete můžete přidat další metody např. pro
 *         indexaci po
 *         jednotlivých dokumentech
 *         a jiné potřebné metody (např. CRUD operace update, delete, ...
 *         dokumentů),
 *         ale zachovejte původní metodu.
 *
 *         metodu index implementujte ve třídě {@link Index}
 */
public interface Index {

    /**
     * Indexes the given list of documents.
     * 
     * @param documents
     *            documents to be indexed
     */
    void index(List<Indexable> documents);

    /**
     * Searches for documents that match the given query.
     * 
     * @param query
     *            query to be searched by
     * @return a list of {@link QueryResult} objects represent matching documents
     */
    List<QueryResult> search(String query);

    /**
     * Searches for documents that match the given query. Allows to specify a search
     * model to be used.
     * 
     * @param query
     *            query to be searched by
     * @param searchModel
     *            search model to be used
     * @return a list of {@link QueryResult} objects represent matching documents
     */
    List<QueryResult> search(String query, SearchModel searchModel);

    /**
     * Checks if there are any indexed documents present.
     * 
     * @return {@code true} if there are indexed documents present, otherwise
     *         {@code false}
     */
    boolean hasData();

    /**
     * Returns the document with the given ID if it exists.
     * 
     * @param id
     *            ID of the document
     * @return the document with the given ID if it exists, otherwise {@code Optional#empty()}
     */
    Optional<Document> getDocument(long id);
}
