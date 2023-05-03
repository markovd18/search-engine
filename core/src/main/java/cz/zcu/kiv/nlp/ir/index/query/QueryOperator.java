package cz.zcu.kiv.nlp.ir.index.query;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

public enum QueryOperator {
  AND("AND"), OR("OR"), NOT("NOT"), LEFT_PARENTHESES("("), RIGHT_PARENTHESES(")");

  private final String token;

  QueryOperator(final String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }

  public boolean equalsToken(final String token) {
    if (StringUtils.isBlank(token)) {
      return false;
    }

    return this.token.toLowerCase().equals(token.toLowerCase());
  }

  public static Optional<QueryOperator> fromToken(final String token) {
    for (final var operator : QueryOperator.values()) {
      if (operator.equalsToken(token)) {
        return Optional.of(operator);
      }
    }

    return Optional.empty();
  }

  public boolean hasPrecedenceOver(final QueryOperator other) {
    if (other == QueryOperator.LEFT_PARENTHESES || other == QueryOperator.RIGHT_PARENTHESES) {
      return false;
    }

    if (this == QueryOperator.NOT || this == QueryOperator.AND || this == QueryOperator.OR) {
      return false;
    }

    return true;
  }
}
