package cz.zcu.kiv.nlp.ir.index.query;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;

/**
 * Query object representing a parsed query string.
 */
public class Query {

  private final BooleanQuery booleanQuery;
  private final TermQuery termQuery;

  private Query(final BooleanQuery booleanQuery, final TermQuery termQuery) {
    this.termQuery = termQuery;
    this.booleanQuery = booleanQuery;
  }

  /**
   * Constructs a new boolean query.
   * 
   */
  public static Query booleanQuery(final BooleanQuery booleanQuery) {
    return new Query(booleanQuery, null);
  }

  /**
   * Constructs a new term query.
   */
  public static Query termQuery(final TermQuery termQuery) {
    return new Query(null, termQuery);
  }

  /**
   * Constructs a new invalid query.
   */
  public static Query invalidQuery() {
    return new Query(null, null);
  }

  public boolean isBooleanQuery() {
    return booleanQuery != null;
  }

  public Optional<BooleanQuery> getBooleanQuery() {
    return Optional.ofNullable(booleanQuery);
  }

  public boolean isTermQuery() {
    return termQuery != null;
  }

  public Optional<TermQuery> getTermQuery() {
    return Optional.ofNullable(termQuery);
  }

  public boolean isInvalid() {
    return booleanQuery == null && termQuery == null;
  }

  /**
   * Tokenizes the underlying query object back into a query string and strips all
   * possible special characters leaving just space delimited terms.
   */
  public String tokenizeAndConcat() {
    if (isInvalid()) {
      return "";
    }

    if (isTermQuery()) {
      return termQuery.getTerm().text();
    }

    final var clauses = booleanQuery.clauses();
    if (clauses.isEmpty()) {
      return "";
    }

    final StringBuilder stringBuilder = new StringBuilder();
    final Queue<BooleanClause> stack = new LinkedList<>();
    for (final var clause : booleanQuery.clauses()) {
      stack.add(clause);
    }

    while (!stack.isEmpty()) {
      final var clause = stack.poll();
      if (clause.getQuery() instanceof TermQuery termQuery) {
        stringBuilder.append(termQuery.getTerm().text() + " ");
        continue;
      }

      if (clause.getQuery() instanceof BooleanQuery booleanQuery) {
        for (final var subClause : booleanQuery.clauses()) {
          stack.add(subClause);
        }
        continue;
      }

      throw new IllegalStateException(
          "Unknown operation got query type %s".formatted(clause.getQuery().getClass().getName()));
    }

    return stringBuilder.toString().trim();
  }
}
