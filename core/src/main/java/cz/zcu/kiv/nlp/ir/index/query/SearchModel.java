package cz.zcu.kiv.nlp.ir.index.query;

/**
 * Specifies different search models that may be used by {@link Index} to search
 * for documents.
 */
public enum SearchModel {
  BOOLEAN, VECTOR;
}
