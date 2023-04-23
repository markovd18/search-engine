package cz.zcu.kiv.nlp.ir;

import cz.zcu.kiv.nlp.ir.article.Article;
import cz.zcu.kiv.nlp.ir.storage.Storage;

public class Configuration {

  private final boolean justPrintHelp;
  private final Storage<? extends Article> storage;
  // TODO Interface
  private final String model;
  // TODO Interface
  private final String index;

  private Configuration(final boolean justPrintHelp, final Storage<? extends Article> storage, final String model,
      final String index) {
    this.justPrintHelp = justPrintHelp;
    this.storage = storage;
    this.model = model;
    this.index = index;
  }

  public boolean isJustPrintHelp() {
    return justPrintHelp;
  }

  public Storage<? extends Article> getStorage() {
    return storage;
  }

  public String getModel() {
    return model;
  }

  public String getIndex() {
    return index;
  }

  public static ConfigurationBuilder builder() {
    return new Configuration.ConfigurationBuilder();
  }

  public static class ConfigurationBuilder {

    private boolean justPrintHelp = false;
    private Storage<? extends Article> storage;
    private String model;
    private String index;

    public ConfigurationBuilder justPrintHelp(final boolean justPrintHelp) {
      this.justPrintHelp = justPrintHelp;
      return this;
    }

    public ConfigurationBuilder storage(final Storage<? extends Article> storage) {
      this.storage = storage;
      return this;
    }

    public ConfigurationBuilder model(final String model) {
      this.model = model;
      return this;
    }

    public ConfigurationBuilder index(final String index) {
      this.index = index;
      return this;
    }

    public Configuration build() {
      // checkNotNull(storage, "Storage");
      // checkNotNull(model, "Model");
      // checkNotNull(index, "Index");
      return new Configuration(justPrintHelp, storage, model, index);
    }
  }

}
