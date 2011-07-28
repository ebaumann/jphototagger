package org.jphototagger.domain.event.listener;

import org.jphototagger.domain.filetypes.UserDefinedFileType;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface DatabaseUserDefinedFileTypesListener {

    void fileTypeInserted(UserDefinedFileType fileType);

    void fileTypeUpdated(UserDefinedFileType oldFileType, UserDefinedFileType newFileType);

    void fileTypeDeleted(UserDefinedFileType fileType);
}
