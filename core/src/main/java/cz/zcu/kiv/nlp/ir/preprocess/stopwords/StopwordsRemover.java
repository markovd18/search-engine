package cz.zcu.kiv.nlp.ir.preprocess.stopwords;

public interface StopwordsRemover {

  boolean isStopword(String word);
}
