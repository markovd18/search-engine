package cz.zcu.kiv.nlp.ir.index.query;

import static cz.zcu.kiv.nlp.ir.ValidationUtils.checkNotBlank;
import static cz.zcu.kiv.nlp.ir.ValidationUtils.checkNotNull;

public class QueryTerm {
  private String term;
  private QueryOperator operator;

  private QueryTerm leftOperand;
  private QueryTerm rightOperand;

  public QueryTerm(final String term, final QueryOperator operator) {
    checkNotBlank(term, "Term");
    checkNotNull(operator, "Operator");

    this.term = term;
    this.operator = operator;
  }

  public QueryTerm(final QueryOperator operator, final QueryTerm leftOperand, final QueryTerm rightOperand) {
    checkNotNull(operator, "Operator");
    checkNotNull(leftOperand, "Left operand");
    checkNotNull(rightOperand, "Right operand");

    this.operator = operator;
    this.leftOperand = leftOperand;
    this.rightOperand = rightOperand;
  }

  public String getTerm() {
    return term;
  }

  public QueryOperator getOperator() {
    return operator;
  }

  public QueryTerm getLeftOperand() {
    return leftOperand;
  }

  public QueryTerm getRightOperand() {
    return rightOperand;
  }

  
}
