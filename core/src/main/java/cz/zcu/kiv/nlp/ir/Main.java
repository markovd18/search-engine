package cz.zcu.kiv.nlp.ir;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.nlp.ir.tokenizer.DefaultTokenizer;
import cz.zcu.kiv.nlp.ir.tokenizer.Tokenizer;

public class Main {

  public static void main(final String[] args) {
    final Logger logger = LoggerFactory.getLogger(Main.class);
    final Tokenizer tokenizer = new DefaultTokenizer(LoggerFactory.getILoggerFactory());
    tokenizer.tokenize("Ahoj, světe.");

    logger.info("Hello, world!");
    logger.error("Error");
    logger.warn("Warning");
    logger.debug("Debug");
    logger.trace("Not working");
  }
}
