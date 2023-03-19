package cz.zcu.kiv.nlp.ir;

import cz.zcu.kiv.nlp.ir.downloader.HTMLDownloader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
     * Xpath expressions to extract and their descriptions.
     */
    private final static Map<String, String> xpathMap = new HashMap<String, String>();

    static {
        xpathMap.put("allText", "//div[contains(@class, 'article')]/allText()");
        xpathMap.put("html", "//div[contains(@class, 'article')]/html()");
        xpathMap.put("tidyText", "//div[contains(@class, 'article')]/tidyText()");
    }

    private static String SITE = "https://www.hokej.cz";

    private static String URLS_STORAGE_PATH = "_urls.txt";

    private static final Logger log = LoggerFactory.getLogger(Crawler.class);

    /**
     * Be polite and don't send requests too often.
     * Waiting period between requests.
     */
    private final int politenessIntervalMillis;
    private final HTMLDownloader downloader;
    private final UrlStorage storage;

    public Crawler(final HTMLDownloader downloader, final int politenessIntervalMillis,
            final UrlStorage storage) {
        validateParams(downloader, politenessIntervalMillis, storage);

        this.downloader = downloader;
        this.politenessIntervalMillis = politenessIntervalMillis;
        this.storage = storage;
    }

    private void validateParams(final HTMLDownloader downloader, final int politenessIntervalMillis,
            final UrlStorage storage) {
        checkNotNull(downloader, "Downloader");
        checkNotNull(storage, "storage");

        if (politenessIntervalMillis <= 0) {
            throw new IllegalArgumentException("Politeness interval has to be a positive integer");
        }
    }

    public void crawl() {
        Map<String, Map<String, List<String>>> results = new HashMap<String, Map<String, List<String>>>();

        for (String key : xpathMap.keySet()) {
            Map<String, List<String>> map = new HashMap<String, List<String>>();
            results.put(key, map);
        }

        final var urls = loadUrls();
        if (urls.isEmpty()) {
            log.error("Error while loading urls");
            return;
        }

        storage.saveUrls(urls, URLS_STORAGE_PATH);

        final var printStreamMap = initiatePrintStreams(results);

        int count = 0;
        for (String url : urls) {
            processUrl(url, count, urls.size(), results, printStreamMap);
            count++;

            waitForPolitenessDuration();
        }

        closePrintStreams(results, printStreamMap);

        // Save links that failed in some way.
        // Be sure to go through these and explain why the process failed on these
        // links.
        // Try to eliminate all failed links - they consume your time while crawling
        // data.
        reportProblems(downloader.getFailedLinks());
        downloader.emptyFailedLinks();
        log.info("-----------------------------");
    }

    private Set<String> loadUrls() {
        final var storedUrls = storage.loadUrls(URLS_STORAGE_PATH);
        if (!storedUrls.isEmpty()) {
            return storedUrls;
        }

        return crawlUrlsFromWebsite();
    }

    private Set<String> crawlUrlsFromWebsite() {
        final var mainArticleUrls = downloader.getLinks(SITE,
                "//section[@class='h-posts-section']//div[@class='h-posts-box']//article/a[starts-with(@href, '/')]/@href")
                .stream()
                .collect(Collectors.toSet());

        final var sideListArticleUrls = downloader.getLinks(SITE,
                "//section[@class='h-posts-section']//ul[@class='h-posts-list']//li/h3/a[starts-with(@href, '/')]/@href")
                .stream()
                .collect(Collectors.toSet());

        mainArticleUrls.addAll(sideListArticleUrls);
        return mainArticleUrls;
    }

    private Map<String, PrintStream> initiatePrintStreams(final Map<String, Map<String, List<String>>> results) {
        Map<String, PrintStream> printStreamMap = new HashMap<String, PrintStream>();
        for (String key : results.keySet()) {
            File file = storage.createFile(FileUtils.SDF.format(System.currentTimeMillis()) + "_" + key + ".txt");
            PrintStream printStream = null;
            try {
                printStream = new PrintStream(new FileOutputStream(file));
            } catch (final FileNotFoundException e) {
                e.printStackTrace();
            }
            printStreamMap.put(key, printStream);
        }

        return printStreamMap;
    }

    private void closePrintStreams(final Map<String, Map<String, List<String>>> results,
            final Map<String, PrintStream> printStreamMap) {
        for (String key : results.keySet()) {
            PrintStream printStream = printStreamMap.get(key);
            printStream.close();
        }
    }

    private void processUrl(final String url, final int order, final int totalCount,
            final Map<String, Map<String, List<String>>> results,
            final Map<String, PrintStream> printStreamMap) {
        final var link = Links.prependBaseUrlIfNeeded(url, SITE);
        // Download and extract data according to xpathMap
        Map<String, List<String>> products = downloader.processUrl(link, xpathMap);
        if (order % 100 == 0) {
            log.info(order + " / " + totalCount + " = " + order / ((float) totalCount) + "% done.");
        }
        for (String key : results.keySet()) {
            Map<String, List<String>> map = results.get(key);
            List<String> list = products.get(key);
            if (list == null) {
                continue;
            }

            map.put(url, list);
            log.info(Arrays.toString(list.toArray()));
            // print
            PrintStream printStream = printStreamMap.get(key);
            for (String result : list) {
                printStream.println(url + "\t" + result);
            }
        }
    }

    private void waitForPolitenessDuration() {
        try {
            Thread.sleep(politenessIntervalMillis);
        } catch (InterruptedException e) {
            log.error("Error while performing sleep", e);
        }
    }

    /**
     * Save file with failed links for later examination.
     *
     * @param failedLinks links that couldn't be downloaded, extracted etc.
     */
    private void reportProblems(Set<String> failedLinks) {
        if (failedLinks.isEmpty()) {
            return;
        }

        storage.saveUrls(failedLinks,
                FileUtils.SDF.format(System.currentTimeMillis()) + "_failed_links_size_"
                        + failedLinks.size() + ".txt");
        log.info("Failed links: " + failedLinks.size());
    }

}
