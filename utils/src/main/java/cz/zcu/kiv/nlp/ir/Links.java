package cz.zcu.kiv.nlp.ir;

/**
 * A collection of functions for working with URLs and links.
 */
public class Links {

  public static String prependBaseUrlIfNeeded(final String url, final String baseUrl) {
    return url.startsWith(baseUrl) ? url : baseUrl + url;
  }

  public static String prependHttpsIfNeeded(final String url) {
    return url.startsWith("http") ? url : "https://" + url;
  }
}
