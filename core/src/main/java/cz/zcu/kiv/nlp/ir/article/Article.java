package cz.zcu.kiv.nlp.ir.article;

import cz.zcu.kiv.nlp.ir.index.Indexable;

/**
 * Interface representing an article that may be indexed by {@link Index}
 * implementation.
 */
public interface Article extends Indexable {

  /**
   * Returns the content of the article. That is just the meaningful text of the
   * article, not including the title, author name on the date of publishing.
   */
  String getContent();
}
