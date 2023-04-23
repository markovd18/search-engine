package cz.zcu.kiv.nlp.ir.fileLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import cz.zcu.kiv.nlp.ir.FileUtils;
import cz.zcu.kiv.nlp.ir.article.HokejCzArticle;

public class HokejCzArticleLoader implements FileLoader<HokejCzArticle> {

  private static final String datePattern = "\\d\\d?\\.\\ [\\wú]+\\ \\d\\d?:\\d\\d";

  @Override
  public HokejCzArticle loadFromFile(final File file) throws IOException, IllegalArgumentException {
    final var lines = FileUtils.readLines(new FileInputStream(file));
    if (lines.size() < 4) {
      throw new IllegalArgumentException("Loaded article needs to have a title, author, date and content");
    }

    final String title = lines.get(0);

    String date = null;
    String author = null;
    StringBuilder sb = new StringBuilder();
    for (final String line : lines) {
      if (date == null) {
        date = line.matches(datePattern) ? line : null;
        continue;
      }

      if (author == null) {
        author = line;
        continue;
      }

      sb.append(line);
    }

    if (date == null) {
      throw new IllegalArgumentException("Date of the article was not found");
    }

    if (author == null) {
      throw new IllegalArgumentException("Author of the article was not found");
    }

    final String content = sb.toString();
    return new HokejCzArticle(title, author, date, content);
  }

}
