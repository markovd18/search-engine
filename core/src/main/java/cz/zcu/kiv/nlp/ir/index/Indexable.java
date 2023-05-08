package cz.zcu.kiv.nlp.ir.index;

import java.util.Optional;

/**
 * Interface representing any object that may be passed to {@link Index}
 * to be indexed.
 */
public interface Indexable {

  /**
   * Returns the text of the indexable object.
   * 
   * @return the text of the indexable object
   */
  String getText();

  /**
   * Returns the author of the indexable object.
   * 
   * @return the author of the indexable object
   */
  String getAuthor();

  /**
   * Returns the title of the indexable object.
   * 
   * @return the title of the indexable object
   */
  String getTitle();

  /**
   * Returns a date that is connected to the indexable object. It is returned as
   * string
   * therefore the format may be customized and does not rely on the
   * {@link java.util.Date} implementation.
   */
  String getDate();

  /**
   * Optionally returns a custom ID that may or may not be present.
   * 
   */
  Optional<String> getCustomId();

}
