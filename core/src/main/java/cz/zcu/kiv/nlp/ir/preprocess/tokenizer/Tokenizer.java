package cz.zcu.kiv.nlp.ir.preprocess.tokenizer;

import java.util.List;

public interface Tokenizer {

  List<String> tokenize(String text);
}
