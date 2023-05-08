package cz.zcu.kiv.nlp.ir.index;

import static cz.zcu.kiv.nlp.ir.ValidationUtils.checkNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import cz.zcu.kiv.nlp.ir.data.Document;
import cz.zcu.kiv.nlp.ir.preprocess.Preprocessor;

/**
 * Implementation of indexed document.
 */
public class DocumentIndex implements Document {

  /** Unique ID that will be assigned to next constructed document index. */
  private static long nextId = 1;

  private final long id;
  private final Map<String, Double> termWeights = new HashMap<>();

  private final String title;
  private final String author;
  private final String date;
  private final String text;

  /**
   * Constructs new document index with all fields present.
   * 
   */
  public DocumentIndex(final String title, final String author, final String date, final String text) {
    this.id = nextId++;
    this.title = title;
    this.author = author;
    this.date = date;
    this.text = text;
  }

  /**
   * Constructs new document index with just the text field.
   * 
   */
  public DocumentIndex(final String text) {
    this("", "", "", text);
  }

  /**
   * Converts generic indexable document into document index.
   * 
   */
  public static DocumentIndex fromIndexable(final Indexable document) {
    checkNotNull(document, "Document");
    return new DocumentIndex(document.getTitle(), document.getAuthor(), document.getDate(), document.getText());
  }

  /**
   * Tokenizes the content of the document using given preprocessor and stores
   * each term frequency as it's current weight.
   * 
   * @param preprocessor
   *          preprocessor to be used for tokenization
   * @return list of tokens
   */
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

  /**
   * Updates a weight of given term in this document index if it exists.
   * 
   * <p>
   * This method is primarily indented to be used after calculating more specific
   * weight of the term rather than the already present term frequency.
   * </p>
   * 
   * @param term
   *          term to update the weight of
   * @param weight
   *          new weight
   */
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
  public long getId() {
    return id;
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
