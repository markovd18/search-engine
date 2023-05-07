package cz.zcu.kiv.nlp.ir.preprocess;

import static cz.zcu.kiv.nlp.ir.ValidationUtils.checkNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class LucenePreprocessor implements Preprocessor {

  private final Logger logger;
  private final Analyzer analyzer;

  public LucenePreprocessor(final Analyzer analyzer, final ILoggerFactory loggerFactory) {
    checkNotNull(analyzer, "Analyzer");
    checkNotNull(loggerFactory, "Logger factory");
    this.logger = loggerFactory.getLogger(getClass().getName());
    this.analyzer = analyzer;
  }

  @Override
  public List<String> preprocess(final String text) {
    List<String> result = new ArrayList<>();

    try (TokenStream tokenStream = analyzer.tokenStream("", text)) {
      CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);

      tokenStream.reset();
      while (tokenStream.incrementToken()) {
        result.add(charTermAttribute.toString());
      }

      return result;
    } catch (final IOException e) {
      logger.error("Error while tokenizing", e);
      return Collections.emptyList();
    }
  }

}
