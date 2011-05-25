package org.jphototagger.services.core;

import java.io.File;

/**
 * The user directory is the directory where JPhotoTagger stores application
 * specific data like settings.
 *
 * @author Elmar Baumann
 */
public interface UserDirectoryProvider {

    File getUserDirectory();
}
