package cz.zcu.kiv.nlp.ir.userInterafce;

import static cz.zcu.kiv.nlp.ir.ValidationUtils.checkNotNull;

import java.util.Optional;

public class UserInput {

  private final UserCommand command;
  private final String commandArgument;
  private final String optionValue;

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

  public static UserInput pass() {
    return new UserInput(UserCommand.PASS);
  }

  public static UserInput exit() {
    return new UserInput(UserCommand.EXIT);
  }

}
