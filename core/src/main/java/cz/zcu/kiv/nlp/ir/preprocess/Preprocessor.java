package cz.zcu.kiv.nlp.ir.preprocess;

import java.util.List;

public interface Preprocessor {

  List<String> preprocess(String text);
}
