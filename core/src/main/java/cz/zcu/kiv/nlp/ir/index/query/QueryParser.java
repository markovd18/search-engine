package cz.zcu.kiv.nlp.ir.index.query;

import org.apache.lucene.analysis.cz.CzechAnalyzer;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.precedence.PrecedenceQueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;

public class QueryParser {

  public Query parse(String query) {

    final var parser = new PrecedenceQueryParser(new CzechAnalyzer());
    try {
      final var result = parser.parse(query, "");
      if (result instanceof BooleanQuery) {
        return Query.booleanQuery((BooleanQuery) result);
      }

      if (result instanceof TermQuery) {
        return Query.termQuery((TermQuery) result);
      }

      return Query.invalidQuery();
    } catch (final QueryNodeException e) {
      return Query.invalidQuery();
    }
  }

  // private void processOperator(final QueryOperator newOperator,
  // final Stack<QueryOperator> operatorStack,
  // final Stack<QueryTerm> operandStack) {
  // if (newOperator == QueryOperator.LEFT_PARENTHESES) {
  // operatorStack.push(newOperator);
  // return;
  // }

  // if (newOperator == QueryOperator.RIGHT_PARENTHESES) {
  // while (!operatorStack.isEmpty() && operatorStack.peek() !=
  // QueryOperator.LEFT_PARENTHESES) {
  // final var operator = operatorStack.pop();
  // QueryTerm rightOperand = operandStack.pop();
  // QueryTerm leftOperand = operandStack.pop();
  // operandStack.push(new QueryTerm(operator, leftOperand, rightOperand));
  // }
  // operatorStack.pop(); // remove the opening parenthesis
  // return;
  // }

  // // AND, OR, NOT
  // // while (!operatorStack.isEmpty() &&
  // // newOperator.hasPrecedenceOver(operatorStack.peek())) {
  // // final var lastOperator = operatorStack.pop();
  // // List<QueryTerm> rightOperand = operandStack.pop();
  // // List<QueryTerm> leftOperand = operandStack.pop();
  // // operandStack.push(applyOperator(lastOperator, leftOperand, rightOperand));
  // // }

  // // operatorStack.push(newOperator);
  // }

  // private void processOperand(final String token, final Stack<QueryTerm>
  // operandStack,
  // final QueryOperator lastOperator) {
  // QueryTerm queryTerm = new QueryTerm(token, lastOperator);
  // operandStack.push(queryTerm); // add the query term to the list of query
  // terms
  // }

  // private void processOperand(final String token, final List<QueryTerm>
  // queryTerms,
  // final Stack<List<QueryTerm>> operandStack) {

  // QueryTerm queryTerm = new QueryTerm(token, QueryOperator.AND);
  // queryTerms.add(queryTerm); // add the query term to the list of query terms
  // List<QueryTerm> operand = new ArrayList<>();
  // operand.add(queryTerm);
  // operandStack.push(operand);
  // }

  public static QueryOperator getDefaultOperator() {
    return QueryOperator.AND;
  }

  // private List<QueryTerm> applyOperator(final QueryOperator operator, final
  // List<QueryTerm> leftOperand,
  // final List<QueryTerm> rightOperand) {
  // List<QueryTerm> result = new ArrayList<>();
  // switch (operator) {
  // case AND: {
  // for (QueryTerm queryTerm : leftOperand) {
  // if (rightOperand.contains(queryTerm)) {
  // result.add(queryTerm);
  // } else {
  // final var newQueryTerm = new QueryTerm(queryTerm.getTerm(),
  // QueryOperator.AND);
  // result.add(newQueryTerm);
  // }
  // }
  // break;
  // }
  // case OR: {
  // result.addAll(leftOperand);
  // for (QueryTerm queryTerm : rightOperand) {
  // if (!result.contains(queryTerm)) {
  // result.add(queryTerm);
  // }
  // }
  // break;
  // }
  // case NOT: {
  // for (QueryTerm queryTerm : leftOperand) {
  // if (!rightOperand.contains(queryTerm)) {
  // result.add(queryTerm);
  // }
  // }
  // break;
  // }
  // default:
  // // noop
  // }

  // return result;
  // }
}
