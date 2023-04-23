package cz.zcu.kiv.nlp.ir.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import cz.zcu.kiv.nlp.ir.FileUtils;
import cz.zcu.kiv.nlp.ir.fileLoader.FileLoader;

public class FSStorage<TDocument> implements Storage<TDocument> {

  public static final String DEFAULT_PATH = "storage";

  private final Logger logger;

  private final String path;

  private final FileLoader<TDocument> fileLoader;

  public FSStorage(final String path, final FileLoader<TDocument> fileLoader,
      final ILoggerFactory loggerFactory) {
    validate(path);

    this.logger = loggerFactory.getLogger(getClass().getName());
    this.path = path;
    this.fileLoader = fileLoader;
    createStorageIfNotExists(path);
  }

  private void validate(final String path) {
    if (StringUtils.isBlank(path)) {
      throw new IllegalArgumentException("Storage path may not be blank");
    }
  }

  @Override
  public Set<TDocument> getEntries() {
    File directory = new File(path);
    if (directory.exists() && directory.isDirectory()) {
      return loadDocumentsFromStorage(directory);
    }

    return Collections.emptySet();
  }

  private void createStorageIfNotExists(final String path) {
    var outputDir = new File(path);
    if (outputDir.exists()) {
      logger.info("Initiated storage from existing directory: '%s'", path);
      return;
    }

    boolean mkdirs = outputDir.mkdirs();
    if (mkdirs) {
      logger.info("Output directory created: '%s'", outputDir);
    } else {
      logger.error(
          "Output directory can't be created! Please either create it or change the STORAGE parameter.\nOutput directory: '%s'",
          outputDir);
    }
  }

  public Set<String> loadLines(final String filePath) {
    File file = new File(path + filePath);
    if (file.exists()) {
      return loadLinesFromStorage(file);
    }

    return Collections.emptySet();
  }

  public void saveLines(final Set<String> lines, final String filePath) {
    try {
      FileUtils.saveFile(new File(path + filePath), lines);
    } catch (final UnsupportedEncodingException | FileNotFoundException e) {
      logger.error("Error while saving urls", e);
    }
  }

  public String getPath() {
    return this.path;
  }

  private Set<String> loadLinesFromStorage(final File storage) {
    try {
      List<String> lines = FileUtils.readLines(new FileInputStream(storage));
      return lines.stream().collect(Collectors.toSet());
    } catch (final IOException e) {
      logger.error("Error while loading lines from storage", e);
      return Collections.emptySet();
    }
  }

  private Set<TDocument> loadDocumentsFromStorage(final File directory) {
    final Set<TDocument> documents = new HashSet<>();
    for (final var entry : directory.listFiles()) {
      try {
        documents.add(fileLoader.loadFromFile(entry));
      } catch (final IOException e) {
        logger.error("Error while loading document from storage", e);
      }
    }

    return documents;
  }

}
