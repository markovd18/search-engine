package cz.zcu.kiv.nlp.ir.storage;

import static cz.zcu.kiv.nlp.ir.ValidationUtils.checkNotNull;

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
  public static final String ENTRY_FILE_NAME_PREFIX = "entry-";
  public static final String ENTRY_FILE_NAME_SUFFIX = ".txt";

  private final Logger logger;
  private final FileLoader<TDocument> fileLoader;
  private final StorableContentFormatter contentFormatter;
  private final String path;
  private long nextEntryId = 0;

  public FSStorage(final String path, final FileLoader<TDocument> fileLoader,
      final StorableContentFormatter contentFormatter,
      final ILoggerFactory loggerFactory) {
    validate(path);
    checkNotNull(fileLoader, "File Loader");
    checkNotNull(contentFormatter, "Content Formatter");

    this.logger = loggerFactory.getLogger(getClass().getName());
    this.path = path;
    this.fileLoader = fileLoader;
    this.contentFormatter = contentFormatter;
    createStorageIfNotExists(path);
    this.nextEntryId = size() + 1;
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

  @Override
  public boolean hasData() {
    return size() > 0;
  }

  public long size() {
    final File directory = new File(path);
    if (!directory.exists() || !directory.isDirectory()) {
      return 0;
    }

    return directory.list().length;
  }

  @Override
  public boolean saveEntry(final List<String> content) {
    final var formattedContent = contentFormatter.formatStorableContent(content);
    if (formattedContent.isEmpty()) {
      return false;
    }

    final File entry = createFile(nextEntryId);
    try {
      FileUtils.saveFile(entry, formattedContent);
    } catch (UnsupportedEncodingException | FileNotFoundException e) {
      logger.error("Error while saving entry", e);
      return false;
    }

    nextEntryId++;
    return true;
  }

  private void createStorageIfNotExists(final String path) {
    var outputDir = new File(path);
    if (outputDir.exists()) {
      logger.info("Initiated storage from existing directory: '{}'", path);
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

  private File createFile(final long entryId) {
    final String entryName = ENTRY_FILE_NAME_PREFIX + String.format("%02d", entryId) + ENTRY_FILE_NAME_SUFFIX;
    return new File(path + "/" + entryName);
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
