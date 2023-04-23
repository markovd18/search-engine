package cz.zcu.kiv.nlp.ir.storage;

import java.util.Arrays;
import java.util.Optional;

public enum StorageType {
  IN_MEMORY("memory"), FILE_BASED("file");

  private final String argumentName;

  StorageType(String argumentName) {
    this.argumentName = argumentName;
  }

  public String getArgumentName() {
    return argumentName;
  }

  public static Optional<StorageType> fromString(final String stringValue) {
    return Arrays.stream(StorageType.values())
        .filter((value) -> value.getArgumentName().equals(stringValue))
        .findFirst();
  }

}
