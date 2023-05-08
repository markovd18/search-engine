package cz.zcu.kiv.nlp.ir.factory;

import cz.zcu.kiv.nlp.ir.Crawler;
import cz.zcu.kiv.nlp.ir.article.Article;
import cz.zcu.kiv.nlp.ir.index.Index;
import cz.zcu.kiv.nlp.ir.preprocess.Preprocessor;
import cz.zcu.kiv.nlp.ir.storage.Storage;

/**
 * Interface representing a factory for creating key modules of the application.
 */
public interface Factory {

  Preprocessor createPreprocessor(PreprocessorType type);

  Crawler createCrawler(long politenessIntervalMillis, final Storage<? extends Article> storage);

  Index createIndex(Preprocessor preprocessor);

}
