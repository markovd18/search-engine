package cz.zcu.kiv.nlp.ir.index;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Dictionary {

  private final Map<String, VerbInfo> records = new HashMap<>();

  public void addRecord(final String token, final long documentId) {
    final var verbInfo = records.containsKey(token) ? records.get(token) : new VerbInfo(token);
    verbInfo.incrementDocumentFrequency();
    verbInfo.addPosting(documentId);

    if (!records.containsKey(token)) {
      records.put(token, verbInfo);
    }

  }

  public void addRecords(final Set<String> tokens, final long documentId) {
    for (final var token : tokens) {
      addRecord(token, documentId);
    }
  }

  public Set<Verb> getRecords() {
    return records.entrySet()
        .stream()
        .map((entry) -> new Verb(entry.getKey(), entry.getValue()))
        .collect(Collectors.toSet());
  }

  public void clear() {
    records.clear();
  }

  public Optional<VerbInfo> getVerbInfo(final String token) {
    return Optional.ofNullable(records.getOrDefault(token, null));
  }
}
