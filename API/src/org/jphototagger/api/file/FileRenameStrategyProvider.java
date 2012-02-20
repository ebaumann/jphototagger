package org.jphototagger.api.file;

import java.util.Collection;

/**
 * @author Elmar Baumann
 */
public interface FileRenameStrategyProvider {

    Collection<? extends FileRenameStrategy> getFileRenameStrategies();
}
