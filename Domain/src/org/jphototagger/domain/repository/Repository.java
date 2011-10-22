package org.jphototagger.domain.repository;

/**
 * @author Elmar Baumann
 */
public interface Repository {

    void init();

    void shutdown();

    boolean isInit();
}
