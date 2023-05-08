package cz.zcu.kiv.nlp.ir.trec.data;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

import cz.zcu.kiv.nlp.ir.data.Document;

public class DocumentNew implements Document, Serializable {
  String text;
  String id;
  String title;
  Date date;

  public static final long serialVersionUID = -5097715898427114007L;

  public long getId() {
    try {
      return Long.parseLong(id);
    } catch (final Exception ignored) {
      return -1;
    }
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDate() {
    return date.toString();
  }

  public void setDate(Date date) {
    this.date = date;
  }

  @Override
  public String getAuthor() {
    return "";
  }

  @Override
  public Optional<String> getCustomId() {
    return Optional.ofNullable(id);
  }

}
