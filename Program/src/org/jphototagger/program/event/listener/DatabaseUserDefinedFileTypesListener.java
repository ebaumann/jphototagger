package org.jphototagger.program.event.listener;

import org.jphototagger.program.data.UserDefinedFileType;

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
