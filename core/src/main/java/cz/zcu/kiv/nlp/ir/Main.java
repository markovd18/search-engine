package cz.zcu.kiv.nlp.ir;

import java.io.PrintWriter;
import java.util.List;
import org.apache.commons.cli.HelpFormatter;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.nlp.ir.article.Article;
import cz.zcu.kiv.nlp.ir.command.CommandParser;
import cz.zcu.kiv.nlp.ir.data.QueryResult;
import cz.zcu.kiv.nlp.ir.downloader.HTMLDownloader;
import cz.zcu.kiv.nlp.ir.downloader.HTMLDownloaderSelenium;
import cz.zcu.kiv.nlp.ir.factory.DefaultFactory;
import cz.zcu.kiv.nlp.ir.factory.Factory;
import cz.zcu.kiv.nlp.ir.factory.PreprocessorType;
import cz.zcu.kiv.nlp.ir.fileLoader.UrlFileLoader;
import cz.zcu.kiv.nlp.ir.index.Index;
import cz.zcu.kiv.nlp.ir.index.Indexable;
import cz.zcu.kiv.nlp.ir.index.query.SearchModel;
import cz.zcu.kiv.nlp.ir.preprocess.Preprocessor;
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

    final Factory factory = new DefaultFactory();

    final Preprocessor preprocessor = factory.createPreprocessor(PreprocessorType.LUCENE);
    final Index index = factory.createIndex(preprocessor);
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
      var input = UserInput.pass();
      while (input.getCommand() != UserCommand.EXIT) {
        input = userInterface.awaitInput();
        final var command = input.getCommand();
        if (command == UserCommand.QUERY) {
          handleQueryCommand(input, index);
          continue;
        }

        if (command == UserCommand.URL) {
          handleUrlCommand(input);
          continue;
        }

        if (command == UserCommand.CLEAR) {
          handleClearCommand();
          continue;
        }

        // TODO URL and (?INDEX?)
      }
    }
  }

  private static Crawler createCrawler(final long politenessIntervalMillis,
      final Storage<? extends Article> storage) {
    final HTMLDownloader downloader = new HTMLDownloaderSelenium();
    final UrlStorage urlStorage = new UrlStorage("urls", new UrlFileLoader(), LoggerFactory.getILoggerFactory());
    return new Crawler(downloader, politenessIntervalMillis, urlStorage, storage);
  }

  private static void printResult(final List<QueryResult> result, final Index index) {
    System.out.printf("Found %d documents.\n", result.size());
    for (final var queryResult : result) {
      final var documentId = queryResult.getDocumentId();
      final var document = index.getDocument(documentId)
          .orElseThrow(() -> new IllegalStateException("Document not found."));

      System.out.format("Document ID: %s, score: %.5f, title: %s...\n", document.getId(), queryResult.getScore(),
          document.getTitle().subSequence(0, Math.min(50, document.getTitle().length())));
    }
  }

  private static void handleQueryCommand(final UserInput input, final Index index) {
    final var query = input.getCommandArgument()
        .orElseThrow(() -> new IllegalStateException("No query provided"));
    final var model = input.getOptionValue()
        .map((option) -> SearchModel.valueOf(option.toUpperCase()))
        .orElseThrow(() -> new IllegalStateException("No query model provided"));

    final var result = index.search(query, model);
    printResult(result, index);
  }

  private static void handleUrlCommand(final UserInput input) {
    final var url = input.getCommandArgument().orElseThrow(() -> new IllegalStateException("No URL provided"));
    System.out.printf("Indexing URL %s...\n", url);
  }

  private static void handleClearCommand() {
    System.out.print("\033[H\033[2J");
    System.out.flush();
  }
}
