package cz.zcu.kiv.nlp.ir.index;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Dictionary {

  private final Map<String, Integer> records = new HashMap<>();

  public void addRecord(final String token) {
    final var documentFrequency = records.putIfAbsent(token, 1);
    if (documentFrequency != null) {
      records.put(token, documentFrequency + 1);
    }
  }

  public void addRecords(final Set<String> tokens) {
    for (final var token : tokens) {
      addRecord(token);
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
}
