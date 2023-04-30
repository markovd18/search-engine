package cz.zcu.kiv.nlp.ir.data;

import static cz.zcu.kiv.nlp.ir.ValidationUtils.checkNotBlank;

/**
 * Created by Tigi on 6.1.2015.**Třída{@link DefaultQueryResult} implementuje
 * rozhraní {@link QueryResult}
 *
 * Představuje výsledek pro ohodnocené vyhledávání. Tzn. po zadání dotazu
 * vyhledávač vrátí
 * "List<Result>", kde každý objekt {@link QueryResult} reprezentuje jeden
 * dokument a
 * jeho relevanci k zadanému dotazu.
 * => tj. id dokumentu, skóre podobnosti mezi tímto dokumentem a dotazem (např.
 * kosinova podobnost), a rank tj.
 * pořadí mezi ostatními vrácenými dokumenty (dokument s rankem 1 bude dokument,
 * který je nejrelevantnější k dodtazu)
 *
 * Od této třídy byste měli dědit pokud vám nestačí implementace třídy
 * {@link ResultImpl}, např. pokud potřebujete
 * přidat nějaké další proměnné.
 *
 * Metodu toString(String topic) neměnte, ani nepřepisujte v odděděných třídách
 * slouží pro generování výstupu
 * v daném formátu pro vyhodnocovací skript.
 *
 */
public class DefaultQueryResult implements QueryResult, Comparable<QueryResult> {

    private final String documentId;
    private long rank = 0;
    private final double score;

    public DefaultQueryResult(final String documentId, final double score) {
        checkNotBlank(documentId, "Document ID");
        this.documentId = documentId;
        this.score = score;
    }

    @Override
    public String getDocumentId() {
        return documentId;
    }

    @Override
    public double getScore() {
        return score;
    }

    public void setRank(long rank) {
        this.rank = rank;
    }

    @Override
    public long getRank() {
        return rank;
    }

    @Override
    public String toString() {
        return "Result{" +
                "documentID='" + documentId + '\'' +
                ", rank=" + rank +
                ", score=" + score +
                '}';
    }

    /**
     * Metoda používaná pro generování výstupu pro vyhodnocovací skript.
     * Metodu nepřepisujte (v potomcích) ani neupravujte
     */
    @Override
    public String toString(final String topic) {
        return topic + " Q0 " + documentId + " " + rank + " " + score + " runindex1";
    }

    @Override
    public int compareTo(final QueryResult other) {
        // comparing algorithm is intentionally interchanged since java's PriorityQueue
        // implementation stores the "smallest" elements as first
        return score < other.getScore() ? 1 : score > other.getScore() ? -1 : 0;
    }
}