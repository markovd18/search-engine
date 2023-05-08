package cz.zcu.kiv.nlp.ir.preprocess.stemmer;

/**
 * An interface providing methods for text stemming.
 */
public interface Stemmer {
  /**
   * Applies stemming algorithms on given input text.
   * 
   * @param input
   *          text to be stemmed
   * @return stemmed text
   */
  String stem(String input);
}
