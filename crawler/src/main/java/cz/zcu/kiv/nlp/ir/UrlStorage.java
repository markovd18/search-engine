package cz.zcu.kiv.nlp.ir;

import static cz.zcu.kiv.nlp.ir.ValidationUtils.checkNotBlank;
import static cz.zcu.kiv.nlp.ir.ValidationUtils.checkNotNull;

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
    checkNotNull(loader, "URL Loader");
    checkNotNull(loggerFactory, "Logger factory");
    checkNotBlank(path, "URL storage path");

    this.basePath = path;
    this.loader = loader;
    this.logger = loggerFactory.getLogger(getClass().getName());
    createStorageIfNotExists(path);
  }

  private void createStorageIfNotExists(final String path) {
    var outputDir = new File(path);
    if (outputDir.exists()) {
      logger.info("Initiated storage from existing directory: '{}'", path);
      return;
    }

    boolean mkdirs = outputDir.mkdirs();
    if (mkdirs) {
      logger.info("Output directory created: '{}'", outputDir);
    } else {
      logger.error(
          "URL storage directory can't be created!\nOutput directory: '{}'",
          outputDir);
    }
  }

  public File createFile(final String name) {
    return new File(basePath + "/" + name);
  }

  public Set<String> loadUrls(final String path) {
    final var urlsFile = createFile(path);
    if (!urlsFile.exists() || urlsFile.isDirectory()) {
      return Collections.emptySet();
    }

    try {
      return loader.loadFromFile(urlsFile);
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
