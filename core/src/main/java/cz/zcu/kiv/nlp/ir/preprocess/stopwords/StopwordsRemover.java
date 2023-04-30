package cz.zcu.kiv.nlp.ir.preprocess.stopwords;

public interface StopwordsRemover {

  // String removeStopwords(String tex);

  boolean isStopword(String word);
}
