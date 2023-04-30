package cz.zcu.kiv.nlp.ir.index;

import static cz.zcu.kiv.nlp.ir.ValidationUtils.checkNotBlank;

import java.util.LinkedList;
import java.util.List;

public class VerbInfo {
  private final String term;
  private long documentFrequency = 0;
  private final List<String> postingsList = new LinkedList<>();

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

  public List<String> getPostingsList() {
    return postingsList;
  }

  public void addPosting(final String posting) {
    postingsList.add(posting);
  }

  public void incrementDocumentFrequency() {
    documentFrequency++;
  }
}
