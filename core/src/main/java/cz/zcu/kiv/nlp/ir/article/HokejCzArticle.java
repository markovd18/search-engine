package cz.zcu.kiv.nlp.ir.article;

import java.util.List;

public record HokejCzArticle(
    String title,
    String author,
    String date,
    String content) implements Article {

  private static final String datePattern = "\\d\\d?\\.\\ [\\wú]+\\ \\d\\d?:\\d\\d";

  @Override
  public String getTitle() {
    return this.title;
  }

  @Override
  public String getDate() {
    return this.date;
  }

  @Override
  public String getAuthor() {
    return this.author;
  }

  @Override
  public String getContent() {
    return this.content;
  }

  public static Article fromTXTFile(final List<String> lines) {
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
