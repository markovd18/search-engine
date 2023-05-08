package cz.zcu.kiv.nlp.ir.preprocess;

import java.util.List;

/**
 * Interface for preprocessing text.
 */
public interface Preprocessor {

  /**
   * Preprocesses given text and returns a list of created tokens.
   * 
   * @param text
   *          text to be preprocessed
   * @return list of tokens
   */
  List<String> preprocess(String text);
}
