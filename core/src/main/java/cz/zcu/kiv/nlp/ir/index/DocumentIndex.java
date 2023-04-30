package cz.zcu.kiv.nlp.ir.index;

import static cz.zcu.kiv.nlp.ir.ValidationUtils.checkNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import cz.zcu.kiv.nlp.ir.data.Document;
import cz.zcu.kiv.nlp.ir.preprocess.Preprocessor;

public class DocumentIndex implements Document {

  private final UUID id = UUID.randomUUID();
  private final Map<String, Double> termWeights = new HashMap<>();

  private final String title;
  private final String author;
  private final String date;
  private final String text;

  public DocumentIndex(final String title, final String author, final String date, final String text) {
    this.title = title;
    this.author = author;
    this.date = date;
    this.text = text;
  }

  public DocumentIndex(final String text) {
    this("", "", "", text);
  }

  public static DocumentIndex fromIndexable(final Indexable document) {
    checkNotNull(document, "Document");
    return new DocumentIndex(document.getTitle(), document.getAuthor(), document.getDate(), document.getText());
  }

  public List<String> tokenize(final Preprocessor preprocessor) {
    final var tokens = preprocessor.preprocess(getContent());
    for (final var token : tokens) {
      final var occurrenceCount = termWeights.putIfAbsent(token, 1.0);
      if (occurrenceCount != null) {
        termWeights.put(token, occurrenceCount + 1);
      }
    }

    return tokens;
  }

  public void updateTermWeight(final String term, final double weight) {
    if (termWeights.containsKey(term)) {
      termWeights.put(term, weight);
    }
  }

  public double getTermWeight(final String term) {
    if (termWeights.containsKey(term)) {
      return termWeights.get(term);
    }

    return 0;
  }

  public Set<TermWeight> getTermWeights() {
    return termWeights.entrySet()
        .stream()
        .map(entry -> new TermWeight(entry.getKey(), entry.getValue()))
        .collect(Collectors.toSet());
  }

  @Override
  public String getText() {
    return text;
  }

  @Override
  public String getId() {
    return id.toString();
  }

  @Override
  public String getAuthor() {
    return author;
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public String getDate() {
    return date;
  }

  private String getContent() {
    return String.join("\n", title, author, date, text);
  }
}
