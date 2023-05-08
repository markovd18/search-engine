package cz.zcu.kiv.nlp.ir.preprocess.tokenizer;

import java.util.List;

/**
 * An interface providing methods for text tokenization.
 */
public interface Tokenizer {

  /**
   * Transforms given text into a list of tokens.
   * 
   * @param text
   *          text to be tokenized
   * @return list of tokens
   */
  List<String> tokenize(String text);
}
