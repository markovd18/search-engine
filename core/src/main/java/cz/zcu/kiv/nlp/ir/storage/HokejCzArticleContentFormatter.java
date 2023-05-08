package cz.zcu.kiv.nlp.ir.storage;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import cz.zcu.kiv.nlp.ir.article.HokejCzArticle;

/**
 * This content formatted provides methods to format raw crawled appearance of
 * {@link HokejCzArticle} into storable format that may be later loaded back
 * into memory.
 */
public class HokejCzArticleContentFormatter implements StorableContentFormatter {

  @Override
  public List<String> formatStorableContent(final List<String> rawLines) {
    final var result = new LinkedList<String>();

    boolean headerFound = false;
    boolean dateFound = false;
    boolean authorFound = false;
    for (final String line : rawLines) {
      if (!headerFound) {
        if (!line.isBlank()) {
          headerFound = true;
          result.add(line);
        }

        continue;
      }

      if (!dateFound) {
        if (line.matches(HokejCzArticle.DATE_PATTERN)) {
          dateFound = true;
          result.add(line);
        }

        continue;
      }

      if (!authorFound) {
        authorFound = true;
        // xpath leaves junk characters we need to remove
        result.add(StringUtils.remove(line, "<>"));
        continue;
      }

      result.add(line);
    }

    return result;
  }

  // TODO když zbyde čas, udělat hezky pomocí tohohle
  // enum FormatStage {
  // HEADER {

  // @Override
  // boolean lineMatchesCondition(final String line) {
  // return !line.isBlank();
  // }

  // @Override
  // Optional<FormatStage> nexStage() {
  // return Optional.of(DATE);
  // }
  // },
  // DATE {

  // @Override
  // boolean lineMatchesCondition(final String line) {
  // return line.matches(HokejCzArticle.DATE_PATTERN);
  // }

  // @Override
  // Optional<FormatStage> nexStage() {
  // return Optional.of(AUTHOR);
  // }

  // },
  // AUTHOR {

  // @Override
  // boolean lineMatchesCondition(final String line) {
  // return true;
  // }

  // @Override
  // Optional<FormatStage> nexStage() {
  // return Optional.of(CONTENT);
  // }

  // },
  // CONTENT {
  // @Override
  // boolean lineMatchesCondition(String line) {
  // return true;
  // }

  // @Override
  // Optional<FormatStage> nexStage() {
  // return Optional.empty();
  // }
  // };

  // abstract boolean lineMatchesCondition(String line);
  // abstract Optional<FormatStage> nexStage();
  // abstract boolean moveToNextStageAfterMatch();

  // public static FormatStage firstStage() {
  // return FormatStage.HEADER;
  // }
  // }

}
