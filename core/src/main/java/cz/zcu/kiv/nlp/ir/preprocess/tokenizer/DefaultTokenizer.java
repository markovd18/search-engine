package cz.zcu.kiv.nlp.ir.preprocess.tokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class DefaultTokenizer implements Tokenizer {

  // TODO check better alternatives for tokenization
  // video player junk

  private static final String hrefRegex = "(((([A-Za-z]{3,9}:(?:\\/\\/)?)(?:[-;:&=\\+\\$,\\w]+@)?[A-Za-z0-9.-]+(:[0-9]+)?|(?:www.|[-;:&=\\+\\$,\\w]+@)[A-Za-z0-9.-]+)((?:\\/[\\+~%\\/.\\w-_]*)?\\??(?:[-\\+=&;%@.\\w_]*)#?(?:[\\w]*))?))";
  // datum | cislo | html | tecky a ostatniY
  public static final String defaultRegex = hrefRegex
      + "|((\\d+\\.){2}(\\d+)?)|(\\d+[.,](\\d+))|([\\p{L}\\d\\*]+)|(<.*?>)|([\\p{Punct}])";

  private final Logger logger;

  public DefaultTokenizer(final ILoggerFactory loggerFactory) {
    this.logger = loggerFactory.getLogger(getClass().getName());
  }

  @Override
  public List<String> tokenize(final String text) {
    logger.debug("Tokenization started");

    final Pattern pattern = Pattern.compile(defaultRegex);
    final List<String> words = new ArrayList<>();

    final Matcher matcher = pattern.matcher(text);
    while (matcher.find()) {
      int start = matcher.start();
      int end = matcher.end();

      words.add(text.substring(start, end));
    }

    logger.debug("Tokenization finished. Found {} tokens", words.size());
    return words;
  }

}
