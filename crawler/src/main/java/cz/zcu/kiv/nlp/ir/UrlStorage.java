package cz.zcu.kiv.nlp.ir;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Set;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import cz.zcu.kiv.nlp.ir.fileLoader.FileLoader;

public class UrlStorage {

  private final Logger logger;

  private final String basePath;
  private final FileLoader<Set<String>> loader;

  public UrlStorage(final String path, final FileLoader<Set<String>> loader, final ILoggerFactory loggerFactory) {
    this.basePath = path;
    this.loader = loader;
    this.logger = loggerFactory.getLogger(getClass().getName());
  }

  public File createFile(final String name) {
    return new File(basePath + "/" + name);
  }

  public Set<String> loadUrls(final String path) {
    try {
      return loader.loadFromFile(createFile(path));
    } catch (final IOException e) {
      logger.error("Error while loading urls from file", e);
      return Collections.emptySet();
    }
  }

  public void saveUrls(final Set<String> urls, final String path) {
    try {
      FileUtils.saveFile(createFile(path), urls);
    } catch (final UnsupportedEncodingException | FileNotFoundException e) {
      logger.error("Error while saving urls into file", e);
    }
  }

}
