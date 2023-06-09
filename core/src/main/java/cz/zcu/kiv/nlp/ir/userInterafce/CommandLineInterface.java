package cz.zcu.kiv.nlp.ir.userInterafce;

import static cz.zcu.kiv.nlp.ir.ValidationUtils.checkNotNull;

import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import cz.zcu.kiv.nlp.ir.Links;

/**
 * Object providing methods for communicationg with the user.
 */
public class CommandLineInterface implements AutoCloseable {

  private static final String PROMPT = "> ";
  private static final String OPTION_MARKER = "--";

  private final PrintStream outputStream;
  private final Scanner scanner;

  public CommandLineInterface(final InputStream inputStream, final PrintStream outputStream) {
    checkNotNull(inputStream, "Input stream");
    checkNotNull(outputStream, "Output stream");

    this.outputStream = outputStream;
    this.scanner = new Scanner(inputStream);
  }

  public UserInput awaitInput() {
    printPrompt();
    final var input = scanner.nextLine().trim();

    if (input.startsWith("exit")) {
      return UserInput.exit();
    }

    final var spaceIndex = input.indexOf(' ');
    if (spaceIndex == -1) {
      if (input.isBlank()) {
        return UserInput.pass();
      }

      if (input.equals("clear")) {
        return UserInput.clear();
      }

      final var command = input;
      outputStream.printf("Please provide an argument for command '%s' delimited by space.\n", command);
      return UserInput.pass();
    }

    final var command = input.substring(0, spaceIndex);
    if (command.equals("query")) {
      return parseQueryCommandInput(input, spaceIndex);
    }

    if (command.equals("url")) {
      return parseUrlCommandInput(input, spaceIndex);
    }

    if (command.equals("clear")) {
      return UserInput.clear();
    }

    return UserInput.pass();
  }

  private UserInput parseQueryCommandInput(final String input, final int startIndex) {
    var model = "vector";

    final var optionIndex = input.indexOf(OPTION_MARKER);
    if (optionIndex == -1) {
      final var query = input.substring(startIndex, input.length()).trim();
      return new UserInput(UserCommand.QUERY, query, model);
    }

    final var optionNameStartIndex = optionIndex + OPTION_MARKER.length();
    final var optionNameEndIndex = input.indexOf(' ', optionNameStartIndex);
    if (optionNameEndIndex != -1) {
      final var optionName = input.substring(optionNameStartIndex, optionNameEndIndex).trim();
      if (optionName.equals("model")) {
        final var modelOption = input.substring(optionNameEndIndex + 1);
        if (modelOption.equals("vector") || modelOption.equals("boolean")) {
          model = modelOption;
        } else {
          outputStream.printf("Unknown model option '%s'. Using default model.", modelOption);
        }

      }
    }

    final var query = input.substring(startIndex, optionIndex).trim();
    return new UserInput(UserCommand.QUERY, query, model);
  }

  private UserInput parseUrlCommandInput(final String input, final int startIndex) {
    final var url = input.substring(startIndex, input.length()).trim();
    final var urlWithProtocol = Links.prependHttpsIfNeeded(url);
    try {
      new URL(urlWithProtocol);
    } catch (final MalformedURLException e) {
      outputStream.println("Invalid URL. Please provide a valid URL.");
      return UserInput.pass();
    }

    return new UserInput(UserCommand.URL, urlWithProtocol, null);
  }

  @Override
  public void close() {
    scanner.close();
  }

  private void printPrompt() {
    outputStream.print(PROMPT);
  }

}
