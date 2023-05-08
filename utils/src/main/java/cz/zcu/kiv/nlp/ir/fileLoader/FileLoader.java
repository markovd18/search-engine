package cz.zcu.kiv.nlp.ir.fileLoader;

import java.io.File;
import java.io.IOException;

/**
 * Interface for loading objects stored in files. Each implementation is
 * responsible for defining the logic of mapping the expected file structure to
 * the target object class.
 * 
 * @param T
 *          type of object to be loaded
 */
public interface FileLoader<T> {
  T loadFromFile(final File file) throws IOException;
}
