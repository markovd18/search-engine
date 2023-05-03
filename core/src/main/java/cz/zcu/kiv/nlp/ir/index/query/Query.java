package cz.zcu.kiv.nlp.ir.index.query;

import java.util.Optional;

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
}
