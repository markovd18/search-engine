package cz.zcu.kiv.nlp.ir.storage;

import java.util.List;
import java.util.Set;

/**
 * Storage provides an interface to load any stored documents.
 * That may be documents created by crawler or manually created documents
 * intended for indexing.
 * 
 * 
 * <p>
 * The interface is agnostic of the type of documents stored. It is up to the
 * implementation to specify all required dependencies to handle the document
 * storing and loading.
 * </p>
 */
public interface Storage<TDocument> {

  Set<TDocument> getEntries();

  boolean hasData();

  boolean saveEntry(final List<String> contentLines);
}
