package cz.zcu.kiv.nlp.ir.command;

import org.apache.commons.cli.CommandLine;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.nlp.ir.Configuration.ConfigurationBuilder;
import cz.zcu.kiv.nlp.ir.article.HokejCzArticle;
import cz.zcu.kiv.nlp.ir.fileLoader.FileLoader;
import cz.zcu.kiv.nlp.ir.fileLoader.HokejCzArticleLoader;
import cz.zcu.kiv.nlp.ir.storage.FSStorage;
import cz.zcu.kiv.nlp.ir.storage.HokejCzArticleContentFormatter;
import cz.zcu.kiv.nlp.ir.storage.StorableContentFormatter;
import cz.zcu.kiv.nlp.ir.storage.Storage;
import cz.zcu.kiv.nlp.ir.storage.StorageType;

/**
 * Command options that may be passed to {@code Search Engine} from CLI. Each
 * option defines not only it's name and description but also the handler when
 * the value is present in the command-line.
 */
public enum CommandOption {
  /**
   * Allows to specify a {@link Storage} implementation to be used.
   */
  STORAGE("s", "storage", false, "storage-detail", "specify a storage to use (memory/file)") {
    @Override
    void handleOption(CommandLine commandLine, ConfigurationBuilder config) {
      final var storageTypeValue = commandLine.getOptionValue(getLongName(),
          StorageType.FILE_BASED.getArgumentName());
      final var storageType = StorageType.fromString(storageTypeValue)
          .orElseThrow(() -> new IllegalArgumentException("Invalid storage type"));

      Storage<HokejCzArticle> storage = null;
      if (storageType == StorageType.IN_MEMORY) {
        throw new UnsupportedOperationException("In-memory storage is not supported");
      }

      final FileLoader<HokejCzArticle> fileLoader = new HokejCzArticleLoader();
      final StorableContentFormatter contentFormatter = new HokejCzArticleContentFormatter();
      storage = new FSStorage<HokejCzArticle>(FSStorage.DEFAULT_PATH, fileLoader, contentFormatter,
          LoggerFactory.getILoggerFactory());

      config.storage(storage);
    }
  },
  /**
   * Prints usage info.
   */
  HELP("h", "help", false, null, "print usage info") {

    @Override
    void handleOption(CommandLine commandLine, ConfigurationBuilder config) {
      config.justPrintHelp(commandLine.hasOption(getLongName()));
    }
  };

  /** Short option name (eg. -h) */
  private final String shortName;
  /** Long option name (eg. --help) */
  private final String longName;
  /** Is the option required? */
  private final boolean required;
  private final String argName;
  /** Description of this command line option. */
  private final String description;

  abstract void handleOption(final CommandLine commandLine, final ConfigurationBuilder config);

  CommandOption(final String shortName, final String longName, final boolean required,
      final String argName, final String description) {
    this.shortName = shortName;
    this.longName = longName;
    this.required = required;
    this.argName = argName;
    this.description = description;
  }

  public String getShortName() {
    return shortName;
  }

  public String getLongName() {
    return longName;
  }

  public boolean isRequired() {
    return required;
  }

  public String getArgName() {
    return argName;
  }

  public String getDescription() {
    return description;
  }

}
