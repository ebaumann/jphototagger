package org.jphototagger.api.file;

/**
 * @author Elmar Baumann
 */
public enum CopyMoveFilesOptions {

    CONFIRM_OVERWRITE(0),
    FORCE_OVERWRITE(1),
    RENAME_SOURCE_FILE_IF_TARGET_FILE_EXISTS(2);

    private final int index;

    private CopyMoveFilesOptions(int index) {
        this.index = index;
    }

    public int getInt() {
        return index;
    }

    public static CopyMoveFilesOptions parseInteger(int index) {
        for (CopyMoveFilesOptions o : values()) {
            if (o.getInt() == index) {
                return o;
            }
        }
        return CONFIRM_OVERWRITE;
    }
}
