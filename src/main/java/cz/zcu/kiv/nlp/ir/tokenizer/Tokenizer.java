package cz.zcu.kiv.nlp.ir.tokenizer;

import java.util.List;

public interface Tokenizer {

  List<String> tokenize(String text);
}
