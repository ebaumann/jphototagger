package org.jphototagger.program.types;

/**
 * @author Elmar Baumann
 */
public interface Persistence {

    void restore();

    void persist();
}
