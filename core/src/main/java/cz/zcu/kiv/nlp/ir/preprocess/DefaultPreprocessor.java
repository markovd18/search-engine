package cz.zcu.kiv.nlp.ir.preprocess;

import static cz.zcu.kiv.nlp.ir.ValidationUtils.checkNotNull;

import java.util.List;

import cz.zcu.kiv.nlp.ir.preprocess.normalizer.Normalizer;
import cz.zcu.kiv.nlp.ir.preprocess.stemmer.Stemmer;
import cz.zcu.kiv.nlp.ir.preprocess.stopwords.StopwordsRemover;
import cz.zcu.kiv.nlp.ir.preprocess.tokenizer.Tokenizer;

public class DefaultPreprocessor implements Preprocessor {

  private final Tokenizer tokenizer;
  private final Stemmer stemmer;
  private final Normalizer normalizer;
  private final StopwordsRemover stopwordsRemover;

  public DefaultPreprocessor(final Tokenizer tokenizer, final Stemmer stemmer, final Normalizer normalizer,
      final StopwordsRemover stopwordsRemover) {
    checkNotNull(tokenizer, "Tokenizer");
    checkNotNull(stemmer, "Stemmer");
    checkNotNull(normalizer, "Normalizer");
    checkNotNull(stopwordsRemover, "Stopwords Remover");

    this.tokenizer = tokenizer;
    this.stemmer = stemmer;
    this.normalizer = normalizer;
    this.stopwordsRemover = stopwordsRemover;
  }

  @Override
  public List<String> preprocess(final String text) {
    final var tokens = tokenizer.tokenize(text);
    return tokens.stream()
        .filter((token) -> !stopwordsRemover.isStopword(token))
        .map((token) -> normalizer.removeAccents(stemmer.stem(token)))
        .toList();
  }

}
