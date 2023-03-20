package cz.zcu.kiv.nlp.ir.command;

import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.cli.CommandLine;
import cz.zcu.kiv.nlp.ir.Configuration.ConfigurationBuilder;

public enum CommandOption {
  STORAGE("s", "storage", true, "storage-detail", "specify a storage to use (in-memory, file-based)",
      getHandleStorageOption()),
  MODEL("m", "model", true, "model-type", "specify engine model (bool, vector)", getHandleModelOption()),
  HELP("h", "help", false, null, "print usage info", getHandleHelpOption());

  private final String shortName;
  private final String longName;
  private final boolean required;
  private final String argName;
  private final String description;

  private final Function<CommandLine, Consumer<ConfigurationBuilder>> handler;

  private CommandOption(final String shortName, final String longName, final boolean required,
      final String argName, final String description,
      final Function<CommandLine, Consumer<ConfigurationBuilder>> handler) {
    this.shortName = shortName;
    this.longName = longName;
    this.required = required;
    this.argName = argName;
    this.description = description;
    this.handler = handler;
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

  public void handleIfPresent(final CommandLine commandLine, final ConfigurationBuilder configuration) {
    if (commandLine.hasOption(this.longName)) {
      handler.apply(commandLine).accept(configuration);
    }
  }

  private static Function<CommandLine, Consumer<ConfigurationBuilder>> getHandleHelpOption() {
    return option -> config -> {
      config.justPrintHelp(true);
    };
  }

  private static Function<CommandLine, Consumer<ConfigurationBuilder>> getHandleStorageOption() {
    return option -> config -> {
      final var storageType = option.getOptionValue(STORAGE.getLongName(), "file");
      // TODO Interface
      config.storage(storageType);
    };
  }

  private static Function<CommandLine, Consumer<ConfigurationBuilder>> getHandleModelOption() {
    return option -> config -> {
      final var model = option.getOptionValue(MODEL.getLongName(), "boolean");
      // TODO Interface
      config.model(model);
    };
  }

}
