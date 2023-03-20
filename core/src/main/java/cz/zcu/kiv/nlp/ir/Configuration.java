package cz.zcu.kiv.nlp.ir;

import static cz.zcu.kiv.nlp.ir.ValidationUtils.checkNotNull;

public class Configuration {

  private final boolean justPrintHelp;
  // TODO Interface
  private final String storage;
  // TODO Interface
  private final String model;

  private Configuration(final boolean justPrintHelp, final String storage, final String model) {
    this.justPrintHelp = justPrintHelp;
    this.storage = storage;
    this.model = model;
  }

  public boolean isJustPrintHelp() {
    return justPrintHelp;
  }

  public String getStorage() {
    return storage;
  }

  public String getModel() {
    return model;
  }

  public static ConfigurationBuilder builder() {
    return new Configuration.ConfigurationBuilder();
  }

  public static class ConfigurationBuilder {

    private boolean justPrintHelp = false;
    private String storage;
    private String model;

    public ConfigurationBuilder justPrintHelp(final boolean justPrintHelp) {
      this.justPrintHelp = justPrintHelp;
      return this;
    }

    public ConfigurationBuilder storage(final String storage) {
      this.storage = storage;
      return this;
    }

    public ConfigurationBuilder model(final String model) {
      this.model = model;
      return this;
    }

    public Configuration build() {
      checkNotNull(storage, "Storage");
      checkNotNull(model, "Model");
      return new Configuration(justPrintHelp, storage, model);
    }
  }

}
