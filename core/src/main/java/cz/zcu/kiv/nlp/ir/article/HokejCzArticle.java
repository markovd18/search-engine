package cz.zcu.kiv.nlp.ir.article;

public record HokejCzArticle(
    String title,
    String author,
    String date,
    String content) implements Article {

  public static final String DATE_PATTERN = "\\d\\d?\\.\\ [\\wú]+\\ \\d\\d?:\\d\\d";

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

}
