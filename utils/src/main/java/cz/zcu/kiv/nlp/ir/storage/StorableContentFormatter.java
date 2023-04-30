package cz.zcu.kiv.nlp.ir.storage;

import java.util.List;

public interface StorableContentFormatter {
  List<String> formatStorableContent(List<String> rawLines);
}
