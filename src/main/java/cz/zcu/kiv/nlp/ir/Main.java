package cz.zcu.kiv.nlp.ir;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

  private static final Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(final String[] args) {
    logger.info("Hello, world!");
    logger.error("Error");
    logger.warn("Warning");
    logger.debug("Debug");
    logger.trace("Not working");
  }
}
