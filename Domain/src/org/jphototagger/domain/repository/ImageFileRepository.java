package org.jphototagger.domain.repository;

import java.io.File;
import java.util.Collection;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface ImageFileRepository {

    Collection<? extends File> getAllImageFiles();

    long getRepositoryImageFileTimestamp(File imageFile);

    long getRepositoryXmpFileTimestamp(File imageFile);

    // A lot of stuff will appended here
}
