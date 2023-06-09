package cz.zcu.kiv.nlp.ir.fileLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import cz.zcu.kiv.nlp.ir.FileUtils;
import cz.zcu.kiv.nlp.ir.article.HokejCzArticle;

/**
 * Implementation of {@link FileLoader} that loads {@link HokejCzArticle} from
 * file into memory.
 */
public class HokejCzArticleLoader implements FileLoader<HokejCzArticle> {

  @Override
  public HokejCzArticle loadFromFile(final File file) throws IOException, IllegalArgumentException {
    final var lines = FileUtils.readLines(new FileInputStream(file));
    if (lines.size() < 4) {
      throw new IllegalArgumentException("Loaded article needs to have a title, author, date and content");
    }

    final String title = lines.get(0);

    String date = null;
    String author = null;
    StringBuilder contentBuilder = new StringBuilder();
    for (final String line : lines) {
      if (date == null) {
        date = line.matches(HokejCzArticle.DATE_PATTERN) ? line : null;
        continue;
      }

      if (author == null) {
        author = line;
        continue;
      }

      contentBuilder.append(line);
    }

    if (date == null) {
      throw new IllegalArgumentException("Date of the article was not found");
    }

    if (author == null) {
      throw new IllegalArgumentException("Author of the article was not found");
    }

    final String content = contentBuilder.toString();
    return new HokejCzArticle(title, author, date, content);
  }

}
