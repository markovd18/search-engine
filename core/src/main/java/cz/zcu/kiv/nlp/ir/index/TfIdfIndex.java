package cz.zcu.kiv.nlp.ir.index;

import static cz.zcu.kiv.nlp.ir.ValidationUtils.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import cz.zcu.kiv.nlp.ir.data.DefaultQueryResult;
import cz.zcu.kiv.nlp.ir.data.Document;
import cz.zcu.kiv.nlp.ir.data.QueryResult;
import cz.zcu.kiv.nlp.ir.index.query.Query;
import cz.zcu.kiv.nlp.ir.index.query.QueryParser;
import cz.zcu.kiv.nlp.ir.index.query.SearchModel;
import cz.zcu.kiv.nlp.ir.preprocess.Preprocessor;

/**
 * {@link Index} that uses Term Frequency-Inverse Document Frequency (TF-IDF) to
 * calculate weights for each term in each document.
 */
public class TfIdfIndex implements Index {

  /** Default number of results returned */
  private static final long DEFAULT_RESULT_SIZE = 10;
  private static final SearchModel DEFAULT_SEARCH_MODEL = SearchModel.VECTOR;

  private final Logger logger;
  private final Preprocessor preprocessor;
  private final QueryParser queryParser;

  private Dictionary dictionary = new Dictionary();
  private Map<Long, DocumentIndex> documents = new HashMap<>();

  public TfIdfIndex(final Preprocessor preprocessor, final QueryParser queryParser,
      final ILoggerFactory loggerFactory) {
    checkNotNull(preprocessor, "Preprocessor");
    checkNotNull(queryParser, "Query Parser");
    checkNotNull(loggerFactory, "Logger factory");
    this.preprocessor = preprocessor;
    this.queryParser = queryParser;
    this.logger = loggerFactory.getLogger(getClass().getName());
  }

  @Override
  public void index(final List<Indexable> documents) {
    if (documents == null || documents.isEmpty()) {
      return;
    }

    logger.info("Indexing {} documents...", documents.size());
    final var documentIndexes = documents.stream()
        .map(DocumentIndex::fromIndexable)
        .toList();

    dictionary.clear();
    logger.debug("Tokenizing documents...");
    for (final var document : documentIndexes) {
      final var tokens = document.tokenize(preprocessor);
      dictionary.addRecords(tokens.stream().collect(Collectors.toSet()), document.getId());
    }

    logger.debug("Calcilating weights...");
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

    logger.info("Normalizing weights...");
    for (final var document : documentIndexes) {
      normalizeDocumentWeights(document);
    }

    this.documents = documentIndexes
        .stream()
        .collect(Collectors.toMap(DocumentIndex::getId, Function.identity()));
    logger.info("Indexing finished.");
  }

  @Override
  public List<QueryResult> search(final String query) {
    return queryNDocuments(query, DEFAULT_SEARCH_MODEL, DEFAULT_RESULT_SIZE);
  }

  @Override
  public List<QueryResult> search(final String query, SearchModel searchModel) {
    return queryNDocuments(query, searchModel, DEFAULT_RESULT_SIZE);
  }

  @Override
  public boolean hasData() {
    return this.documents.size() > 0;
  }

  @Override
  public Optional<Document> getDocument(final long id) {
    checkNotNull(id, "Document ID");
    return Optional.ofNullable(documents.getOrDefault(id, null));
  }

  public List<QueryResult> queryNDocuments(final String queryString, final SearchModel searchModel, final long n) {
    checkNotNull(queryString, "Query");
    logger.info("Querrying '{}'...\n", queryString);
    System.out.format("Querrying '%s'...\n", queryString);
    if (documents == null || documents.isEmpty()) {
      return Collections.emptyList();
    }

    if (searchModel == SearchModel.BOOLEAN) {
      final var result = evaluateBooleanModelQuery(queryString, n);
      logger.info("Found {} documents.", result.size());
      return result;
    }

    final var result = evaluateVectorModelQuery(queryString, n);
    logger.info("Found {} documents.", result.size());
    return result;
  }

  private DocumentIndex createQueryDocument(final String rawQueryString) {
    final var queryDocument = new DocumentIndex(rawQueryString);
    queryDocument.tokenize(preprocessor);
    calculateQueryWeights(queryDocument);
    return queryDocument;
  }

  private void calculateQueryWeights(final DocumentIndex queryDocument) {
    for (final var entry : dictionary.getRecords()) {
      final String term = entry.getTerm();
      final long documentFrequency = entry.getDocumentFrequency();
      final double idf = invertedDocumentFrequency(documents.size(), documentFrequency);
      final double termFrequency = queryDocument.getTermWeight(term);
      final double tfidf = tfIdfWeight(termFrequency, idf);
      queryDocument.updateTermWeight(term, tfidf);
    }

    normalizeDocumentWeights(queryDocument);
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
    for (final var entry : first.getTermWeights()) {
      final String term = entry.getTerm();
      similiarity += entry.getWeight() * second.getTermWeight(term);
    }

    return similiarity;
  }

  private <T> Queue<DefaultQueryResult> mapToResultQueue(final Collection<T> collection,
      final Function<T, DefaultQueryResult> function) {
    return collection.stream()
        .map(function)
        .filter((result) -> result.getScore() > 0.0001)
        .collect(Collectors.toCollection(PriorityQueue::new));

  }

  private List<QueryResult> resultQueueToListOfN(final Queue<DefaultQueryResult> queue, final long n) {
    final List<QueryResult> result = new ArrayList<>();
    var queryResult = queue.poll();
    while (result.size() != n && queryResult != null) {
      queryResult.setRank(result.size() + 1);
      result.add(queryResult);
      queryResult = queue.poll();
    }

    return result;
  }

  private List<QueryResult> evaluateBooleanModelQuery(final String queryString, final long resultSize) {
    System.out.println("Using boolean search model.");
    final var query = queryParser.parse(queryString);
    if (query == null || query.isInvalid()) {
      System.out.println("Invalid query.");
      return Collections.emptyList();
    }

    if (query.isTermQuery()) {
      return evaluateBooleanTermQuery(query, resultSize);
    }

    return evaluateBooleanQuery(query, resultSize);
  }

  private List<QueryResult> evaluateBooleanQuery(final Query query, final long resultSize) {
    final var booleanQuery = query.getBooleanQuery()
        .orElseThrow(() -> new IllegalStateException("Boolean query is missing."));

    final var clauses = booleanQuery.clauses();
    if (clauses.isEmpty()) {
      return Collections.emptyList();
    }

    Collection<Long> result = null;
    // intentionaly using a queue over a stack since we need to process clauses in
    // order
    final Queue<BooleanClause> queue = new LinkedList<>();
    for (final var clause : clauses) {
      queue.add(clause);
    }

    while (!queue.isEmpty()) {
      final var clause = queue.poll();
      if (clause.getQuery() instanceof TermQuery termQuery) {
        final var postings = getPostings(termQuery.toString());
        if (result == null) {
          result = new ArrayList<>(postings);
          continue;
        }

        intersectPostings(result, postings, clause.getOccur());
        continue;
      }

      if (clause.getQuery() instanceof BooleanQuery subQuery) {
        for (final var subCaluse : booleanQuery.clauses()) {
          queue.add(subCaluse);
        }
      }

      throw new IllegalStateException(
          "Unknown operation for query type %s".formatted(clause.getQuery().getClass().getName()));
    }

    final String rawQueryString = query.tokenizeAndConcat();
    final var queryDocument = createQueryDocument(rawQueryString);
    final var resultQueue = mapToResultQueue(result, (posting) -> {
      final var document = documents.get(posting);
      final double score = normalizedCosineSimiliarity(document, queryDocument);
      return new DefaultQueryResult(posting, score, document.getCustomId().orElse(null));
    });

    return resultQueueToListOfN(resultQueue, resultSize);
  }

  private List<QueryResult> evaluateBooleanTermQuery(final Query query, final long resultSize) {
    final var rawQuery = query.getTermQuery()
        .orElseThrow(() -> new IllegalStateException("Term query is missing."));

    System.out.println(rawQuery.toString());
    final var verbInfo = dictionary.getVerbInfo(rawQuery.toString());
    if (verbInfo.isEmpty()) {
      return Collections.emptyList();
    }

    final var entry = verbInfo.get();
    final var postings = entry.getPostings();
    final var queue = mapToResultQueue(postings, (posting) -> {
      final var document = documents.get(posting);
      return new DefaultQueryResult(posting, document.getTermWeight(entry.getTerm()),
          document.getCustomId().orElse(null));
    });

    return resultQueueToListOfN(queue, resultSize);
  }

  private void intersectPostings(final Collection<Long> left, final Collection<Long> right, final Occur occur) {
    switch (occur) {
      case SHOULD:
        left.addAll(right);
        break;
      case MUST:
        left.retainAll(right);
        break;
      case MUST_NOT:
        left.removeAll(right);
        break;
      default:
        throw new IllegalArgumentException("Unknown operation for occur %s".formatted(occur));
    }
  }

  private List<QueryResult> evaluateVectorModelQuery(final String queryString, final long resultSize) {
    System.out.println("Using vector search model.");
    final var queryDocument = createQueryDocument(queryString);
    final var queue = mapToResultQueue(documents.values(), (document) -> {
      final double cosineSimiliarity = normalizedCosineSimiliarity(document, queryDocument);
      return new DefaultQueryResult(document.getId(), cosineSimiliarity, document.getCustomId().orElse(null));
    });

    return resultQueueToListOfN(queue, resultSize);
  }

  private Collection<Long> getPostings(final String term) {
    return dictionary.getVerbInfo(term)
        .map(info -> info.getPostings())
        .orElse(Collections.emptyList());
  }
}
