package cz.zcu.kiv.nlp.ir;

import cz.zcu.kiv.nlp.ir.article.Article;
import cz.zcu.kiv.nlp.ir.storage.Storage;

/**
 * This class holds the application configuration parsed from the options passed
 * from command line.
 */
public class Configuration {
  /** Should the application just print usage info? */
  private final boolean justPrintHelp;
  /** Chosen {@link Storage} implementation. */
  private final Storage<? extends Article> storage;

  private Configuration(final boolean justPrintHelp, final Storage<? extends Article> storage) {
    this.justPrintHelp = justPrintHelp;
    this.storage = storage;
  }

  public boolean isJustPrintHelp() {
    return justPrintHelp;
  }

  public Storage<? extends Article> getStorage() {
    return storage;
  }

  /**
   * Creates a builder for {@link Configuration} that provides a convenient way of
   * building configuration.
   */
  public static ConfigurationBuilder builder() {
    return new Configuration.ConfigurationBuilder();
  }

  /**
   * A builder pattern for construction of {@link Configuration} instances.
   */
  public static class ConfigurationBuilder {

    private boolean justPrintHelp = false;
    private Storage<? extends Article> storage;

    public ConfigurationBuilder justPrintHelp(final boolean justPrintHelp) {
      this.justPrintHelp = justPrintHelp;
      return this;
    }

    public ConfigurationBuilder storage(final Storage<? extends Article> storage) {
      this.storage = storage;
      return this;
    }

    public Configuration build() {
      return new Configuration(justPrintHelp, storage);
    }
  }

}
