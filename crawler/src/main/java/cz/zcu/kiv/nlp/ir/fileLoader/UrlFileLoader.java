package cz.zcu.kiv.nlp.ir.fileLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import cz.zcu.kiv.nlp.ir.FileUtils;

public class UrlFileLoader implements FileLoader<Set<String>> {

  @Override
  public Set<String> loadFromFile(final File file) throws IOException {
    return FileUtils.readLines(new FileInputStream(file)).stream().collect(Collectors.toSet());
  }

}
