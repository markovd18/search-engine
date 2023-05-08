package cz.zcu.kiv.nlp.ir.index.query;

/**
 * Interface representing a query parser. It provides methods to parse a query
 * string into a {@link Query} object.
 */
public interface QueryParser {

  /**
   * Parses a query string into a {@link Query} object.
   * 
   * @param query
   *          query string
   * @return a parsed {@link Query} object
   */
  Query parse(String query);
}
