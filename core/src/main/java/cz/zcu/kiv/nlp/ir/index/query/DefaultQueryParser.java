package cz.zcu.kiv.nlp.ir.index.query;

import org.apache.lucene.analysis.cz.CzechAnalyzer;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.precedence.PrecedenceQueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;

public class DefaultQueryParser implements QueryParser {

  private final PrecedenceQueryParser parser = new PrecedenceQueryParser(new CzechAnalyzer());

  @Override
  public Query parse(String query) {

    try {
      final var result = parser.parse(query, "");
      if (result instanceof BooleanQuery) {
        return Query.booleanQuery((BooleanQuery) result);
      }

      if (result instanceof TermQuery) {
        return Query.termQuery((TermQuery) result);
      }

      return Query.invalidQuery();
    } catch (final QueryNodeException e) {
      return Query.invalidQuery();
    }
  }

}
