package cz.zcu.kiv.nlp.ir;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.cli.HelpFormatter;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.nlp.ir.article.Article;
import cz.zcu.kiv.nlp.ir.command.CommandParser;
import cz.zcu.kiv.nlp.ir.data.QueryResult;
import cz.zcu.kiv.nlp.ir.downloader.HTMLDownloader;
import cz.zcu.kiv.nlp.ir.downloader.HTMLDownloaderSelenium;
import cz.zcu.kiv.nlp.ir.fileLoader.UrlFileLoader;
import cz.zcu.kiv.nlp.ir.index.Index;
import cz.zcu.kiv.nlp.ir.index.Indexable;
import cz.zcu.kiv.nlp.ir.index.TfIdfIndex;
import cz.zcu.kiv.nlp.ir.preprocess.DefaultPreprocessor;
import cz.zcu.kiv.nlp.ir.preprocess.Preprocessor;
import cz.zcu.kiv.nlp.ir.preprocess.normalizer.DefaultNormalizer;
import cz.zcu.kiv.nlp.ir.preprocess.normalizer.Normalizer;
import cz.zcu.kiv.nlp.ir.preprocess.stemmer.CzechStemmerAgressive;
import cz.zcu.kiv.nlp.ir.preprocess.stemmer.Stemmer;
import cz.zcu.kiv.nlp.ir.preprocess.stopwords.DefaultStopwordsRemover;
import cz.zcu.kiv.nlp.ir.preprocess.stopwords.StopwordsRemover;
import cz.zcu.kiv.nlp.ir.preprocess.tokenizer.DefaultTokenizer;
import cz.zcu.kiv.nlp.ir.preprocess.tokenizer.Tokenizer;
import cz.zcu.kiv.nlp.ir.storage.Storage;
import cz.zcu.kiv.nlp.ir.userInterafce.CommandLineInterface;
import cz.zcu.kiv.nlp.ir.userInterafce.UserCommand;
import cz.zcu.kiv.nlp.ir.userInterafce.UserInput;

public class Main {

  private static final long DEFAULT_CRAWLER_POLITENESS_INTERVAL = 1200;

  public static void main(final String[] args) {
    final var parser = new CommandParser(LoggerFactory.getILoggerFactory());
    final var config = parser.parse(args);

    if (config.isJustPrintHelp()) {
      final var formatter = new HelpFormatter();
      try (final var writer = new PrintWriter(System.out)) {
        formatter.printUsage(writer, 100, "Search Engine", parser.getOptions());
        formatter.printOptions(writer, 100, parser.getOptions(), 10, 10);
      }

      return;
    }

    final Preprocessor preprocessor = createPreprocessor();
    final Index index = new TfIdfIndex(preprocessor);
    if (!index.hasData()) {
      final Storage<? extends Article> storage = config.getStorage();
      if (!storage.hasData()) {
        final Crawler crawler = createCrawler(DEFAULT_CRAWLER_POLITENESS_INTERVAL, storage);
        crawler.crawl();
      }

      final var documents = storage.getEntries()
          .stream()
          .map((entry) -> (Indexable) entry)
          .toList();
      index.index(documents);
    }

    final var testResult = index.search("Druhá řada bude svým způsobem pardubická");
    printResult(testResult, index);

    try (final CommandLineInterface userInterface = new CommandLineInterface(System.in, System.out)) {
      var input = userInterface.awaitInput();
      while (input.getCommand() != UserCommand.EXIT) {
        final var command = input.getCommand();
        if (command == UserCommand.QUERY) {
          handleQueryCommand(input, index);
        }

        // TODO URL and (?INDEX?)
        input = userInterface.awaitInput();
      }
    }
  }

  private static Crawler createCrawler(final long politenessIntervalMillis,
      final Storage<? extends Article> storage) {
    final HTMLDownloader downloader = new HTMLDownloaderSelenium();
    final UrlStorage urlStorage = new UrlStorage("urls", new UrlFileLoader(), LoggerFactory.getILoggerFactory());
    return new Crawler(downloader, politenessIntervalMillis, urlStorage, storage);
  }

  private static Preprocessor createPreprocessor() {
    final Tokenizer tokenizer = new DefaultTokenizer(LoggerFactory.getILoggerFactory());
    final Stemmer stemmer = new CzechStemmerAgressive();
    final Normalizer normalizer = new DefaultNormalizer();
    final StopwordsRemover stopwordsRemover = new DefaultStopwordsRemover(loadStopwords());
    return new DefaultPreprocessor(tokenizer, stemmer, normalizer, stopwordsRemover);
  }

  private static void printResult(final List<QueryResult> result, final Index index) {
    for (final var queryResult : result) {
      final var documentId = queryResult.getDocumentId();
      final var document = index.getDocument(documentId)
          .orElseThrow(() -> new IllegalStateException("Document not found."));

      System.out.format("Document ID: %s, score: %.5f, title: %s...\n", document.getId(), queryResult.getScore(),
          document.getTitle().subSequence(0, Math.min(50, document.getTitle().length())));
    }
  }

  private static Set<String> loadStopwords() {
    try {
      return FileUtils.readLines(new FileInputStream(new File("stopwords.txt"))).stream()
          .collect(Collectors.toSet());
    } catch (IOException e) {
      System.err.println("File not found: stopwords.txt\n" + e.getMessage());
      return Collections.emptySet();
    }
  }

  private static void handleQueryCommand(final UserInput input, final Index index) {
    final var query = input.getCommandArgument()
        .orElseThrow(() -> new IllegalStateException("No query provided"));
    final var model = input.getOptionValue()
        .orElseThrow(() -> new IllegalStateException("No query model provided"));
    final var result = index.search(query); // TODO model as parameter
    printResult(result, index);
  }
}
