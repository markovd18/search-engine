package cz.zcu.kiv.nlp.ir.index.query;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;

public class Query {

  private final BooleanQuery booleanQuery;
  private final TermQuery termQuery;

  private Query(final BooleanQuery booleanQuery, final TermQuery termQuery) {
    this.termQuery = termQuery;
    this.booleanQuery = booleanQuery;
  }

  public static Query booleanQuery(final BooleanQuery booleanQuery) {
    return new Query(booleanQuery, null);
  }

  public static Query termQuery(final TermQuery termQuery) {
    return new Query(null, termQuery);
  }

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
          "Unknown operation got query type {}".formatted(clause.getQuery().getClass().getName()));
    }

    return stringBuilder.toString().trim();
  }
}
