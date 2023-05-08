package cz.zcu.kiv.nlp.ir.article;

import java.util.Optional;

/**
 * Indexable article from {@link https://www.hokej.cz/}.
 */
public record HokejCzArticle(
    String title,
    String author,
    String date,
    String content) implements Article {

  /** Regex matching the published date format present in the article. */
  public static final String DATE_PATTERN = "\\d\\d?\\.\\ [\\p{L}]+\\ \\d\\d?:\\d\\d";

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

  @Override
  public String getText() {
    return getContent();
  }

  @Override
  public Optional<String> getCustomId() {
    return Optional.empty();
  }

}
