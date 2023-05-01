package cz.zcu.kiv.nlp.ir.index;

import static cz.zcu.kiv.nlp.ir.ValidationUtils.checkNotBlank;

import java.util.PriorityQueue;
import java.util.Queue;

public class VerbInfo {
  private final String term;
  private long documentFrequency = 0;
  private final Queue<Long> postingsList = new PriorityQueue<>();

  public VerbInfo(final String term) {
    checkNotBlank(term, "Term");
    this.term = term;
  }

  public String getTerm() {
    return term;
  }

  public long getDocumentFrequency() {
    return documentFrequency;
  }

  public void addPosting(final Long posting) {
    postingsList.add(posting);
  }

  public void incrementDocumentFrequency() {
    documentFrequency++;
  }
}
