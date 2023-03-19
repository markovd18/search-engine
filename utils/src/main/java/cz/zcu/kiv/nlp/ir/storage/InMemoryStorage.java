package cz.zcu.kiv.nlp.ir.storage;

import java.util.HashSet;
import java.util.Set;

/**
 * In-Memory Storage implementation. Intended for testing and development
 * purposes only.
 */
public class InMemoryStorage<TDocument> implements Storage<TDocument> {

  private Set<TDocument> entries = new HashSet<>();

  public InMemoryStorage(final Set<TDocument> entries) {
    if (entries != null) {
      this.entries = entries;
    }
  }

  @Override
  public Set<TDocument> getEntries() {
    return entries;
  }

}
