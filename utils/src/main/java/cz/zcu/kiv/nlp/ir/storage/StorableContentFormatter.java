package cz.zcu.kiv.nlp.ir.storage;

import java.util.List;

/**
 * This interface provides methods to transform raw content of storable object
 * to a format that is later expected by loader when loading the object back
 * into memory.
 * 
 * <p>
 * Generally intended to format crawled articles content to be stored into
 * {@link Storage} afterwards.
 * </p>
 */
public interface StorableContentFormatter {
  List<String> formatStorableContent(List<String> rawLines);
}
