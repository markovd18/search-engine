package cz.zcu.kiv.nlp.ir.index;

public interface Indexable {

  /**
   * Text záznamu
   * 
   * @return text
   */
  String getText();

  /**
   * Autor záznamu.
   * 
   */
  String getAuthor();

  /**
   * Titulek dokumentu
   * 
   * @return titulek dokumentu
   */
  String getTitle();

  /**
   * Datum publikace dokumentu
   */
  String getDate();

}
