package cz.zcu.kiv.nlp.ir.downloader;

import java.util.List;
import java.util.Set;

/**
 * This class is a demonstration of how crawler can be used to download a
 * website
 * Created by Tigi on 31.10.2014.
 */
public interface HTMLDownloader {

    /**
     * Get failed links.
     *
     * @return failed links
     */
    public Set<String> getFailedLinks();

    /**
     * Empty the empty links set
     */
    public void emptyFailedLinks();

    /**
     * Downloads given url page and extracts xpath expression.
     *
     * @param url
     *            page url
     * @param xpath
     *            xpath expression
     * @return extracted values
     */
    public List<String> processUrl(String url, String xpath);

    /**
     * Downloads given url page and extracts xpath expression.
     *
     * @param url
     *            page url
     * @param xPath
     *            xpath expression
     * @return list of extracted values
     */
    public List<String> getLinks(String url, String xPath);

    /**
     * Quit driver/browser
     */
    public void quit();
}
