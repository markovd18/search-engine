package cz.zcu.kiv.nlp.ir.command;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import cz.zcu.kiv.nlp.ir.Configuration;

public class CommandParser {

  private final Logger logger;
  private final Options options = new Options();

  public CommandParser(final ILoggerFactory loggerFactory) {
    this.logger = loggerFactory.getLogger(getClass().getName());

    final var options = Arrays.stream(CommandOption.values())
        .map(option -> Option.builder(option.getShortName())
            .longOpt(option.getLongName())
            .desc(option.getDescription())
            .argName(option.getArgName())
            .required(option.isRequired())
            .build())
        .collect(Collectors.toSet());

    for (final var option : options) {
      this.options.addOption(option);
    }
  }

  public Configuration parse(final String[] args) {
    final var parser = new DefaultParser();
    try {
      final var commandLine = parser.parse(options, args);
      final var configBuilder = Configuration.builder();
      for (final var option : CommandOption.values()) {
        option.handleIfPresent(commandLine, configBuilder);
      }

      return configBuilder.build();
    } catch (final ParseException e) {
      logger.error("Error while parsing options", e);
      return Configuration.builder().justPrintHelp(true).build();
    }
  }

  public Options getOptions() {
    return options;
  }

}
