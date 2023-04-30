package cz.zcu.kiv.nlp.ir.downloader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.codecraft.xsoup.Xsoup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class is a demonstration of how crawler4j can be used to download a
 * website
 * Created by Tigi on 31.10.2014.
 */
public class HTMLDownloaderSelenium implements HTMLDownloader {

    static final Logger log = LoggerFactory.getLogger(HTMLDownloaderSelenium.class);
    WebDriver driver;

    Set<String> failedLinks = new HashSet<String>();

    /**
     * Constructor
     */
    public HTMLDownloaderSelenium() {
        super();
        System.setProperty("webdriver.chrome.driver", "./chromedriver");
        driver = new ChromeDriver();
    }

    /**
     * Quit driver/browser
     */
    public void quit() {
        driver.quit();
    }

    /**
     * Downloads given url page and extracts xpath expressions.
     *
     * @param url
     *            page url
     * @param xpathMap
     *            pairs of description and xpath expression
     * @return pairs of descriptions and extracted values
     */
    @Override
    public List<String> processUrl(final String url, String xpath) {
        log.info("Processing: {}", url);
        driver.get(url);
        String dom = driver.getPageSource();
        if (dom == null) {
            log.info("Couldn't fetch the content of the page.");
            failedLinks.add(url);
            return Collections.emptyList();
        }

        Document document = Jsoup.parse(dom);
        final var result = Xsoup.compile(xpath).evaluate(document).list();
        log.info("Url processed.");
        return result;
    }

    /**
     * Downloads given url page and extracts xpath expression.
     *
     * @param url
     *            page url
     * @param xPath
     *            xpath expression
     * @return list of extracted values
     */
    public List<String> getLinks(String url, String xPath) {
        ArrayList<String> list = new ArrayList<String>();
        log.info("Processing: " + url);
        driver.get(url);
        String dom = driver.getPageSource();
        if (dom != null) {
            Document document = Jsoup.parse(dom);
            List<String> xlist = Xsoup.compile(xPath).evaluate(document).list();
            list.addAll(xlist);
        } else {
            log.info("Couldn't fetch the content of the page.");
            failedLinks.add(url);
        }
        return list;
    }

    /**
     * Get failed links.
     *
     * @return failed links
     */
    public Set<String> getFailedLinks() {
        return failedLinks;
    }

    /**
     * Empty the empty links set
     */
    public void emptyFailedLinks() {
        failedLinks = new HashSet<String>();
    }
}
