package cz.zcu.kiv.nlp.ir.factory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cz.CzechAnalyzer;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.nlp.ir.Crawler;
import cz.zcu.kiv.nlp.ir.FileUtils;
import cz.zcu.kiv.nlp.ir.UrlStorage;
import cz.zcu.kiv.nlp.ir.article.Article;
import cz.zcu.kiv.nlp.ir.downloader.HTMLDownloader;
import cz.zcu.kiv.nlp.ir.downloader.HTMLDownloaderSelenium;
import cz.zcu.kiv.nlp.ir.fileLoader.UrlFileLoader;
import cz.zcu.kiv.nlp.ir.index.Index;
import cz.zcu.kiv.nlp.ir.index.TfIdfIndex;
import cz.zcu.kiv.nlp.ir.index.query.DefaultQueryParser;
import cz.zcu.kiv.nlp.ir.preprocess.DefaultPreprocessor;
import cz.zcu.kiv.nlp.ir.preprocess.LucenePreprocessor;
import cz.zcu.kiv.nlp.ir.preprocess.Preprocessor;
import cz.zcu.kiv.nlp.ir.preprocess.normalizer.DefaultNormalizer;
import cz.zcu.kiv.nlp.ir.preprocess.normalizer.Normalizer;
import cz.zcu.kiv.nlp.ir.preprocess.stemmer.CzechStemmerAgressive;
import cz.zcu.kiv.nlp.ir.preprocess.stemmer.Stemmer;
import cz.zcu.kiv.nlp.ir.preprocess.stopwords.DefaultStopwordsRemover;
import cz.zcu.kiv.nlp.ir.preprocess.stopwords.StopwordsRemover;
import cz.zcu.kiv.nlp.ir.preprocess.tokenizer.DefaultTokenizer;
import cz.zcu.kiv.nlp.ir.preprocess.tokenizer.Tokenizer;
import cz.zcu.kiv.nlp.ir.storage.Storage;

/**
 * Default implementation of {@link Factory}.
 */
public class DefaultFactory implements Factory {

  @Override
  public Preprocessor createPreprocessor(final PreprocessorType type) {
    if (type == PreprocessorType.LUCENE) {
      return new LucenePreprocessor(createLuceneAnalyzer(), LoggerFactory.getILoggerFactory());
    }

    if (type == PreprocessorType.DEFAULT) {
      final Tokenizer tokenizer = new DefaultTokenizer(LoggerFactory.getILoggerFactory());
      final Stemmer stemmer = new CzechStemmerAgressive();
      final Normalizer normalizer = new DefaultNormalizer();
      final StopwordsRemover stopwordsRemover = new DefaultStopwordsRemover(loadStopwords());
      return new DefaultPreprocessor(tokenizer, stemmer, normalizer, stopwordsRemover);
    }

    throw new IllegalArgumentException("Implementation for preprocessor type not found(type : %s)".formatted(type));
  }

  @Override
  public Crawler createCrawler(final long politenessIntervalMillis, final Storage<? extends Article> storage) {
    final HTMLDownloader downloader = new HTMLDownloaderSelenium();
    final UrlStorage urlStorage = new UrlStorage("urls", new UrlFileLoader(), LoggerFactory.getILoggerFactory());
    return new Crawler(downloader, politenessIntervalMillis, urlStorage, storage);
  }

  private static Set<String> loadStopwords() {
    try {
      return FileUtils.readLines(new FileInputStream(new File("stopwords.txt"))).stream()
          .collect(Collectors.toSet());
    } catch (IOException e) {
      System.err.println("File not found: stopwords.txt\n" + e.getMessage());
      return Collections.emptySet();
    }
  }

  @Override
  public Index createIndex(final Preprocessor preprocessor) {
    return new TfIdfIndex(preprocessor, new DefaultQueryParser(createLuceneAnalyzer()),
        LoggerFactory.getILoggerFactory());
  }

  private Analyzer createLuceneAnalyzer() {
    return new CzechAnalyzer();
  }

}
