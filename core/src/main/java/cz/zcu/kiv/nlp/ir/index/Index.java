package cz.zcu.kiv.nlp.ir.index;

import cz.zcu.kiv.nlp.ir.data.Document;
import cz.zcu.kiv.nlp.ir.data.QueryResult;

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

    void index(List<Indexable> documents);

    List<QueryResult> search(String query);

    boolean hasData();

    Optional<Document> getDocument(String id);
}
