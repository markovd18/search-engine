package cz.zcu.kiv.nlp.ir.preprocess.normalizer;

/**
 * Interface providing methods for tet normalization.
 */
public interface Normalizer {

  /**
   * Removes all accents from given text.
   * 
   * @param text
   *          text to remove accents from
   * @return text stripped of all accents
   */
  String removeAccents(String text);
}
