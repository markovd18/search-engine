package cz.zcu.kiv.nlp.ir;

import org.apache.commons.cli.HelpFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.nlp.ir.article.Article;
import cz.zcu.kiv.nlp.ir.command.CommandParser;
import cz.zcu.kiv.nlp.ir.downloader.HTMLDownloader;
import cz.zcu.kiv.nlp.ir.downloader.HTMLDownloaderSelenium;
import cz.zcu.kiv.nlp.ir.fileLoader.UrlFileLoader;
import cz.zcu.kiv.nlp.ir.storage.Storage;
import cz.zcu.kiv.nlp.ir.tokenizer.DefaultTokenizer;
import cz.zcu.kiv.nlp.ir.tokenizer.Tokenizer;

public class Main {

  private static final long DEFAULT_CRAWLER_POLITENESS_INTERVAL = 1200;

  public static void main(final String[] args) {
    final var parser = new CommandParser(LoggerFactory.getILoggerFactory());
    final var config = parser.parse(args);

    if (config.isJustPrintHelp()) {
      final var formatter = new HelpFormatter();
      formatter.printHelp("search-engine [-s STORAGE] [-m MODEL] [-i INDEX]", parser.getOptions());
      return;
    }

    final Storage<? extends Article> storage = config.getStorage();
    if (!storage.hasData()) {
      final Crawler crawler = createCrawler(DEFAULT_CRAWLER_POLITENESS_INTERVAL, storage);
      crawler.crawl();
    }

    final Logger logger = LoggerFactory.getLogger(Main.class);
    final Tokenizer tokenizer = new DefaultTokenizer(LoggerFactory.getILoggerFactory());
    tokenizer.tokenize("Ahoj, světe.");
    logger.info("Hello, world!");

    logger.error("Error");
    logger.warn("Warning");
    logger.debug("Debug");
    logger.trace("Not working");
  }

  private static final Crawler createCrawler(final long politenessIntervalMillis,
      final Storage<? extends Article> storage) {
    final HTMLDownloader downloader = new HTMLDownloaderSelenium();
    final UrlStorage urlStorage = new UrlStorage("urls", new UrlFileLoader(), LoggerFactory.getILoggerFactory());
    return new Crawler(downloader, politenessIntervalMillis, urlStorage, storage);
  }
}
