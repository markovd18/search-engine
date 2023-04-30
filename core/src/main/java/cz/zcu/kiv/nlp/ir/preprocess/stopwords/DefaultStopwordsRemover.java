package cz.zcu.kiv.nlp.ir.preprocess.stopwords;

import static cz.zcu.kiv.nlp.ir.ValidationUtils.checkNotBlank;
import static cz.zcu.kiv.nlp.ir.ValidationUtils.checkNotNull;

import java.util.Set;

public class DefaultStopwordsRemover implements StopwordsRemover {

  private final Set<String> stopwords;

  public DefaultStopwordsRemover(final Set<String> stopwords) {
    checkNotNull(stopwords, "Stopwords");
    this.stopwords = stopwords;
  }

  @Override
  public boolean isStopword(final String word) {
    checkNotBlank(word, "Word");
    return stopwords.contains(word);
  }

}
