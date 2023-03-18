package cz.zcu.kiv.nlp.ir;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class Storage {

  private final Logger logger;
  private final String path;

  public Storage(final String path, final ILoggerFactory loggerFactory) {
    if (StringUtils.isBlank(path)) {
      throw new IllegalArgumentException("Storage path may not be blank");
    }

    this.path = path;
    this.logger = loggerFactory.getLogger(getClass().getName());
    createStorageIfNotExists(path);
  }

  public void createStorageIfNotExists(final String path) {
    var outputDir = new File(path);
    if (outputDir.exists()) {
      logger.debug("Output storage already exists. Skipping.");
      return;
    }

    boolean mkdirs = outputDir.mkdirs();
    if (mkdirs) {
      logger.info("Output directory created: " + outputDir);
    } else {
      logger.error(
          "Output directory can't be created! Please either create it or change the STORAGE parameter.\nOutput directory: "
              + outputDir);
    }
  }

  public Optional<Set<String>> loadUrls(final String urlsPath) {
    File links = new File(path + urlsPath);
    if (links.exists()) {
      return loadUrlsFromStorage(links);
    }

    return Optional.empty();
  }

  public void saveUrls(final Set<String> urls, final String urlsPath) {
    try {
      FileUtils.saveFile(new File(path + urlsPath), urls);
    } catch (final UnsupportedEncodingException | FileNotFoundException e) {
      logger.error("Error while saving urls", e);
    }
  }

  public File createFile(final String name) {
    return new File(path + "/" + name);
  }

  private Optional<Set<String>> loadUrlsFromStorage(final File storage) {
    try {
      List<String> lines = FileUtils.readLines(new FileInputStream(storage));
      return Optional.of(lines.stream().collect(Collectors.toSet()));
    } catch (final IOException e) {
      logger.error("Storage with urls was not found", e);
      return Optional.empty();
    }
  }
}
