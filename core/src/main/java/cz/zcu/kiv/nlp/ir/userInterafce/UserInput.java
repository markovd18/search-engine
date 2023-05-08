package cz.zcu.kiv.nlp.ir.userInterafce;

import static cz.zcu.kiv.nlp.ir.ValidationUtils.checkNotNull;

import java.util.Optional;

/**
 * Represents a parsed user input that may be further handled by the
 * application.
 */
public class UserInput {

  private final UserCommand command;
  private final String commandArgument;
  private final String optionValue;

  /**
   * Constructs a new {@link UserInput} with given command and optional arguments.
   * 
   * @param command
   *          passed command
   * @param commandArgument
   *          optional argument
   * @param optionValue
   *          specified option that is not required
   */
  public UserInput(final UserCommand command, final String commandArgument, final String optionValue) {
    checkNotNull(command, "Command");

    this.command = command;
    this.commandArgument = commandArgument;
    this.optionValue = optionValue;
  }

  public UserInput(final UserCommand command) {
    this(command, null, null);
  }

  public UserCommand getCommand() {
    return command;
  }

  public Optional<String> getCommandArgument() {
    return Optional.ofNullable(commandArgument);
  }

  public Optional<String> getOptionValue() {
    return Optional.ofNullable(optionValue);
  }

  /**
   * Constructs a new user input that represents a blank input.
   * 
   */
  public static UserInput pass() {
    return new UserInput(UserCommand.PASS);
  }

  /**
   * Constructs a new user input that requests the application to quit.
   * 
   */
  public static UserInput exit() {
    return new UserInput(UserCommand.EXIT);
  }

  /**
   * Constructs a new user input that requests to clear the screen.
   * 
   */
  public static UserInput clear() {
    return new UserInput(UserCommand.CLEAR);
  }

}
