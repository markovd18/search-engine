package cz.zcu.kiv.nlp.ir.index;

import static cz.zcu.kiv.nlp.ir.ValidationUtils.checkNotNull;

public class Verb {

  private final String term;
  private final int documentFrequency;

  public Verb(final String term, final int termFrequency) {
    checkNotNull(term, "Term");

    this.term = term;
    this.documentFrequency = termFrequency;
  }

  public String getTerm() {
    return term;
  }

  public int getDocumentFrequency() {
    return documentFrequency;
  }

}
