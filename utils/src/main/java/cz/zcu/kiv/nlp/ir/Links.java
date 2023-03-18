package cz.zcu.kiv.nlp.ir;

public class Links {

  public static String prependBaseUrlIfNeeded(final String url, final String baseUrl) {
    return url.startsWith(baseUrl) ? url : baseUrl + url;
  }
}
