package cz.zcu.kiv.nlp.ir.index;

import static cz.zcu.kiv.nlp.ir.ValidationUtils.checkNotBlank;
import static cz.zcu.kiv.nlp.ir.ValidationUtils.checkNotNull;;

/**
 * Holds a term and information about it.
 */
public class Verb {

  private final String term;
  private final VerbInfo verbInfo;

  public Verb(final String term, final VerbInfo verbInfo) {
    checkNotBlank(term, "Term");
    checkNotNull(verbInfo, "Verb info");

    this.term = term;
    this.verbInfo = verbInfo;
  }

  public String getTerm() {
    return term;
  }

  public long getDocumentFrequency() {
    return verbInfo.getDocumentFrequency();
  }

}
