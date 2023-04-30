package cz.zcu.kiv.nlp.ir;

import cz.zcu.kiv.nlp.ir.downloader.HTMLDownloader;
import cz.zcu.kiv.nlp.ir.storage.Storage;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cz.zcu.kiv.nlp.ir.ValidationUtils.checkNotNull;

/**
 * CrawlerVSCOM class acts as a controller. You should only adapt this file to
 * serve your needs.
 * Created by Tigi on 31.10.2014.
 */
public class Crawler {
    /**
     * Xpath expression to extract.
     */
    private static final String XPATH = "//div[contains(@class, 'article')]/tidyText()";
    // private static final String XPATH = "//div[contains(@class,
    // 'article')]/div[contains(@class, 'box__info')]/a[contains(@class,
    // 'author')]/tidyText()";

    private static String SITE = "https://www.hokej.cz";
    private static String URLS_STORAGE_PATH = "_urls.txt";

    private static final Logger logger = LoggerFactory.getLogger(Crawler.class);

    /**
     * Be polite and don't send requests too often.
     * Waiting period between requests.
     */
    private final long politenessIntervalMillis;
    private final HTMLDownloader downloader;
    private final UrlStorage urlStorage;
    private final Storage<?> articleStorage;

    public Crawler(final HTMLDownloader downloader, final long politenessIntervalMillis,
            final UrlStorage urlStorage, final Storage<?> articleStorage) {
        validateParams(downloader, politenessIntervalMillis, urlStorage, articleStorage);

        this.downloader = downloader;
        this.politenessIntervalMillis = politenessIntervalMillis;
        this.urlStorage = urlStorage;
        this.articleStorage = articleStorage;
    }

    private void validateParams(final HTMLDownloader downloader, final long politenessIntervalMillis,
            final UrlStorage storage, final Storage<?> articleStorage) {
        checkNotNull(downloader, "Downloader");
        checkNotNull(storage, "storage");
        checkNotNull(articleStorage, "Article storage");
    }

    public void crawl() {
        final var urls = loadUrls();
        if (urls.isEmpty()) {
            logger.error("Error while loading urls");
            return;
        }

        urlStorage.saveUrls(urls, URLS_STORAGE_PATH);

        int count = 0;
        for (final String url : urls) {
            final var content = processUrl(url, count, urls.size());
            final boolean savedSuccessfully = articleStorage.saveEntry(content);
            count++;
            if (!savedSuccessfully) {
                logger.error("Couldn't save article no.{}", count);
            }

            waitForPolitenessDuration();
        }

        // Save links that failed in some way.
        // Be sure to go through these and explain why the process failed on these
        // links.
        // Try to eliminate all failed links - they consume your time while crawling
        // data.
        reportProblems(downloader.getFailedLinks());
        downloader.emptyFailedLinks();
        logger.info("-----------------------------");
    }

    private Set<String> loadUrls() {
        final var storedUrls = urlStorage.loadUrls(URLS_STORAGE_PATH);
        if (!storedUrls.isEmpty()) {
            return storedUrls;
        }

        return crawlUrlsFromWebsite();
    }

    private Set<String> crawlUrlsFromWebsite() {
        logger.info("Crawling urls from {}...", SITE);
        // final var mainArticleUrls = downloader.getLinks(SITE,
        // "//section[@class='h-posts-section']//div[@class='h-posts-box']//article/a[starts-with(@href,
        // '/')]/@href")
        // .stream()
        // .collect(Collectors.toSet());

        final var sideListArticleUrls = downloader.getLinks(SITE,
                "//section[@class='h-posts-section']//ul[@class='h-posts-list']//li/h3/a[starts-with(@href, '/')]/@href")
                .stream()
                .collect(Collectors.toSet());

        final var allArticles = downloader.getLinks(SITE, "//article//a[starts-with(@href, '/')]/@href").stream()
                .collect(Collectors.toSet());

        allArticles.addAll(sideListArticleUrls);
        logger.info("Found {} articles", allArticles.size());
        return allArticles;
    }

    private List<String> processUrl(final String url, final int order, final int totalCount) {
        final var link = Links.prependBaseUrlIfNeeded(url, SITE);
        // Download and extract data according to xpathMap
        final List<String> products = downloader.processUrl(link, XPATH);
        if (order % 100 == 0) {
            logger.info(order + " / " + totalCount + " = " + order / ((float) totalCount) + "% done.");
        }

        logger.debug(Arrays.toString(products.toArray()));

        final var result = new LinkedList<String>();
        for (final var product : products) {
            // maybe add url as a first line so we can index and query it later??
            final var lines = product.split("\n");
            result.addAll(Arrays.asList(lines));
        }

        return result;
    }

    private void waitForPolitenessDuration() {
        try {
            Thread.sleep(politenessIntervalMillis);
        } catch (InterruptedException e) {
            logger.error("Error while performing sleep", e);
        }
    }

    /**
     * Save file with failed links for later examination.
     *
     * @param failedLinks
     *            links that couldn't be downloaded, extracted etc.
     */
    private void reportProblems(Set<String> failedLinks) {
        if (failedLinks.isEmpty()) {
            return;
        }

        urlStorage.saveUrls(failedLinks,
                FileUtils.SDF.format(System.currentTimeMillis()) + "_failed_links_size_"
                        + failedLinks.size() + ".txt");
        logger.info("Failed links: " + failedLinks.size());
    }

}
