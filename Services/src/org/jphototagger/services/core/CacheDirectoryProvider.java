package org.jphototagger.services.core;

import java.io.File;

/**
 * The cache directory is the directory for JPhotoTagger's and it's plugins cache.
 *
 * @author Elmar Baumann
 */
public interface CacheDirectoryProvider {

    File getCacheDirectory();
}
