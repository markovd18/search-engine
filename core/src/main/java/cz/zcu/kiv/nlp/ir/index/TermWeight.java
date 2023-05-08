package cz.zcu.kiv.nlp.ir.index;

import static cz.zcu.kiv.nlp.ir.ValidationUtils.checkNotNull;

/**
 * Holds a term and its weight.
 */
public class TermWeight {

  private double weight;
  private final String term;

  public TermWeight(final String term, final double weight) {
    checkNotNull(term, "Term");

    this.term = term;
    this.weight = weight;
  }

  public double getWeight() {
    return weight;
  }

  public String getTerm() {
    return term;
  }

  public boolean termEquals(final String term) {
    if (term == null) {
      return false;
    }

    return this.term.equals(term);
  }

  public void updateWeight(final double weight) {
    this.weight = weight;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((term == null) ? 0 : term.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj == null) {
      return false;
    }

    if (getClass() != obj.getClass()) {
      return false;
    }

    TermWeight other = (TermWeight) obj;
    if (term == null) {
      if (other.term != null) {
        return false;
      }
    } else if (!term.equals(other.term)) {
      return false;
    }

    return true;
  }

}
