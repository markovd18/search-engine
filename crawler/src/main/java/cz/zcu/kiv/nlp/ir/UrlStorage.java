package cz.zcu.kiv.nlp.ir;

import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.ILoggerFactory;

import cz.zcu.kiv.nlp.ir.storage.FSStorage;

public class UrlStorage {

  private final FSStorage<Set<String>> storage;

  public UrlStorage(final String path, final ILoggerFactory loggerFactory) {
    storage = new FSStorage<>(path, (lines) -> lines.stream().collect(Collectors.toSet()), loggerFactory);
  }

  public File createFile(final String name) {
    return new File(storage.getPath() + "/" + name);
  }

  public Set<String> loadUrls(final String path) {
    return storage.loadLines(path);
  }

  public void saveUrls(final Set<String> urls, final String path) {
    storage.saveLines(urls, path);
  }
}
