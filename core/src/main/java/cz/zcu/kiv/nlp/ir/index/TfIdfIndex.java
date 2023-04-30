package cz.zcu.kiv.nlp.ir.index;

import static cz.zcu.kiv.nlp.ir.ValidationUtils.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;

import cz.zcu.kiv.nlp.ir.ValidationUtils;
import cz.zcu.kiv.nlp.ir.data.DefaultQueryResult;
import cz.zcu.kiv.nlp.ir.data.Document;
import cz.zcu.kiv.nlp.ir.data.QueryResult;
import cz.zcu.kiv.nlp.ir.preprocess.Preprocessor;

public class TfIdfIndex implements Index {

  private static final long DEFAULT_QUERY_SIZE = 10;

  private final Preprocessor preprocessor;
  private Dictionary dictionary = new Dictionary();
  private Map<String, DocumentIndex> documents = new HashMap<>();

  public TfIdfIndex(final Preprocessor preprocessor) {
    checkNotNull(preprocessor, "Preprocessor");
    this.preprocessor = preprocessor;
  }

  @Override
  public void index(final List<Indexable> documents) {
    if (documents == null || documents.isEmpty()) {
      return;
    }

    final var documentIndexes = documents.stream()
        .map(DocumentIndex::fromIndexable)
        .toList();

    dictionary.clear();
    for (final var document : documentIndexes) {
      final var tokens = document.tokenize(preprocessor);
      dictionary.addRecords(tokens.stream().collect(Collectors.toSet()), document.getId());
    }

    final long documentCount = documentIndexes.size();
    for (final var entry : dictionary.getRecords()) {
      final String term = entry.getTerm();
      final long documentFrequency = entry.getDocumentFrequency();
      final double idf = invertedDocumentFrequency(documentCount, documentFrequency);
      for (final var document : documentIndexes) {
        final double termFrequency = document.getTermWeight(term);
        final double tfidf = tfIdfWeight(termFrequency, idf);
        document.updateTermWeight(term, tfidf);
      }
    }

    for (final var document : documentIndexes) {
      normalizeDocumentWeights(document);
    }

    this.documents = documentIndexes
        .stream()
        .collect(Collectors.toMap(DocumentIndex::getId, Function.identity()));
  }

  @Override
  public List<QueryResult> search(final String query) {
    return queryNDocuments(query, DEFAULT_QUERY_SIZE);
  }

  @Override
  public boolean hasData() {
    return this.documents.size() > 0;
  }

  @Override
  public Optional<Document> getDocument(final String id) {
    ValidationUtils.checkNotNull(id, "Document ID");
    return Optional.ofNullable(documents.get(id));
  }

  public List<QueryResult> queryNDocuments(final String queryString, final long n) {
    checkNotNull(queryString, "Query");
    System.out.format("Querrying '%s'...\n", queryString);
    if (documents == null || documents.isEmpty()) {
      return Collections.emptyList();
    }

    // TODO query parsing and handling - may be Boolean model or vector model search
    final var queryDocument = new DocumentIndex(queryString);
    queryDocument.tokenize(preprocessor);
    for (final var entry : dictionary.getRecords()) {
      final String term = entry.getTerm();
      final long documentFrequency = entry.getDocumentFrequency();
      final double idf = invertedDocumentFrequency(documents.size(), documentFrequency);
      final double termFrequency = queryDocument.getTermWeight(term);
      final double tfidf = tfIdfWeight(termFrequency, idf);
      queryDocument.updateTermWeight(term, tfidf);
    }

    normalizeDocumentWeights(queryDocument);

    final Queue<DefaultQueryResult> queue = new PriorityQueue<>();
    for (final var document : documents.values()) {
      final double cosineSimiliarity = normalizedCosineSimiliarity(document, queryDocument);
      queue.add(new DefaultQueryResult(document.getId(), cosineSimiliarity));
    }

    final List<QueryResult> result = new ArrayList<>();
    var queryResult = queue.poll();
    while (result.size() != n && queryResult != null) {
      queryResult.setRank(result.size() + 1);
      result.add(queryResult);
      queryResult = queue.poll();
    }

    return result;
  }

  private double invertedDocumentFrequency(final long documentCount, final long termDocumentFrequency) {
    if (termDocumentFrequency <= 0 || documentCount <= 0) {
      return 0;
    }

    return Math.log10(documentCount / ((double) termDocumentFrequency));
  }

  private double tfIdfWeight(final double termFrequency, final double invertedDocumentFrequency) {
    if (termFrequency <= 0) {
      return 0;
    }

    return (1 + Math.log10(termFrequency)) * invertedDocumentFrequency;
  }

  private static double calculateDocumentNorm(final DocumentIndex document) {
    double sum = 0;
    for (final var termWeight : document.getTermWeights()) {
      sum += termWeight.getWeight() * termWeight.getWeight();
    }

    return Math.sqrt(sum);
  }

  private static void normalizeDocumentWeights(final DocumentIndex document) {
    final double norm = calculateDocumentNorm(document);
    for (final var termWeight : document.getTermWeights()) {
      final double normalizedWeight = termWeight.getWeight() / norm;
      document.updateTermWeight(termWeight.getTerm(), normalizedWeight);
    }
  }

  private double normalizedCosineSimiliarity(final DocumentIndex first, final DocumentIndex second) {
    double similiarity = 0;
    for (final var entry : dictionary.getRecords()) {
      final String term = entry.getTerm();
      similiarity += first.getTermWeight(term) * second.getTermWeight(term);
    }

    return similiarity;
  }

}
