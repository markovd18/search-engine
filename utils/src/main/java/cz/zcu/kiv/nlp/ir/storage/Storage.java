package cz.zcu.kiv.nlp.ir.storage;

import java.util.List;
import java.util.Set;

/**
 * Storage provides an interface to load any stored documents.
 * That may be documents created by crawler or manually created documents
 * intended for indexing.
 */
public interface Storage<TDocument> {

  Set<TDocument> getEntries();

  boolean hasData();

  boolean saveEntry(final List<String> contentLines); // TODO do we want to type the content also?
}
