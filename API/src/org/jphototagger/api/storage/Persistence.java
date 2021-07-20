package org.jphototagger.api.storage;

/**
 * @author Elmar Baumann
 */
public interface Persistence {

    void restore();

    void persist();
}
