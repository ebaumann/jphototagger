package org.jphototagger.domain.repository;

/**
 * @author Elmar Baumann
 */
public interface RepositoryMaintainance {

    boolean compressRepository();

    int deleteNotReferenced1n();
}
