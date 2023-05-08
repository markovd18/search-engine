package cz.zcu.kiv.nlp.ir.index;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Dictionary holds a collection of terms, their frequencies and postings.
 */
public class Dictionary {

  private final Map<String, VerbInfo> records = new HashMap<>();

  /**
   * Adds a term to the dictionary. If the term is already in the dictionary,
   * increments its frequency.
   * 
   * @param term
   *          found term
   * @param documentId
   *          document ID of the found term
   */
  public void addRecord(final String term, final long documentId) {
    final var verbInfo = records.containsKey(term) ? records.get(term) : new VerbInfo(term);
    verbInfo.incrementDocumentFrequency();
    verbInfo.addPosting(documentId);

    if (!records.containsKey(term)) {
      records.put(term, verbInfo);
    }

  }

  /**
   * Batch operation that adds multiple terms into dictionary.
   * 
   * @param terms
   *          found terms
   * @param documentId
   *          document ID of found terms
   */
  public void addRecords(final Set<String> terms, final long documentId) {
    for (final var token : terms) {
      addRecord(token, documentId);
    }
  }

  /**
   * Returns all terms in the dictionary, theit frequencies and postings.
   * 
   */
  public Set<Verb> getRecords() {
    return records.entrySet()
        .stream()
        .map((entry) -> new Verb(entry.getKey(), entry.getValue()))
        .collect(Collectors.toSet());
  }

  /** Clears the dictionary of all records. */
  public void clear() {
    records.clear();
  }

  public Optional<VerbInfo> getVerbInfo(final String token) {
    return Optional.ofNullable(records.getOrDefault(token, null));
  }
}
