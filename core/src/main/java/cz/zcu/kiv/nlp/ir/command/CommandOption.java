package cz.zcu.kiv.nlp.ir.command;

import org.apache.commons.cli.CommandLine;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.nlp.ir.Configuration.ConfigurationBuilder;
import cz.zcu.kiv.nlp.ir.article.HokejCzArticle;
import cz.zcu.kiv.nlp.ir.fileLoader.FileLoader;
import cz.zcu.kiv.nlp.ir.fileLoader.HokejCzArticleLoader;
import cz.zcu.kiv.nlp.ir.storage.FSStorage;
import cz.zcu.kiv.nlp.ir.storage.InMemoryStorage;
import cz.zcu.kiv.nlp.ir.storage.Storage;
import cz.zcu.kiv.nlp.ir.storage.StorageType;

public enum CommandOption {
  STORAGE("s", "storage", false, "storage-detail", "specify a storage to use (memory/file)") {
    @Override
    void handleOption(CommandLine commandLine, ConfigurationBuilder config) {
      final var storageTypeValue = commandLine.getOptionValue(getLongName(),
          StorageType.FILE_BASED.getArgumentName());
      final var storageType = StorageType.fromString(storageTypeValue)
          .orElseThrow(() -> new IllegalArgumentException("Invalid storage type"));

      Storage<HokejCzArticle> storage = null;
      if (storageType == StorageType.FILE_BASED) {
        final FileLoader<HokejCzArticle> fileLoader = new HokejCzArticleLoader();
        storage = new FSStorage<HokejCzArticle>(FSStorage.DEFAULT_PATH, fileLoader, LoggerFactory.getILoggerFactory());
      } else {
        storage = new InMemoryStorage<>(null);
      }

      config.storage(storage);
    }
  },
  MODEL("m", "model", false, "model-type", "specify engine model (boolean/vector)") {

    @Override
    void handleOption(CommandLine commandLine, ConfigurationBuilder config) {
      final var model = commandLine.getOptionValue(getLongName(), "boolean");
      // TODO Interface
      config.model(model);
    }
  },
  HELP("h", "help", false, null, "print usage info") {

    @Override
    void handleOption(CommandLine commandLine, ConfigurationBuilder config) {
      config.justPrintHelp(commandLine.hasOption(getLongName()));
    }
  },
  INDEX("i", "index", false, "index-type", "Type of index to use (memory/file)") {
    @Override
    void handleOption(CommandLine commandLine, ConfigurationBuilder config) {
      final var index = commandLine.getOptionValue(getLongName(), "boolean");
      // TODO interface
      config.index(index);
    }
  };

  private final String shortName;
  private final String longName;
  private final boolean required;
  private final String argName;
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
