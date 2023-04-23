package cz.zcu.kiv.nlp.ir.fileLoader;

import java.io.File;
import java.io.IOException;

public interface FileLoader<T> {
  T loadFromFile(final File file) throws IOException;
}
