package cz.zcu.kiv.nlp.ir.article;

import cz.zcu.kiv.nlp.ir.index.Indexable;

public interface Article extends Indexable {

  String getContent();
}
