package org.jphototagger.lib.io;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.api.concurrent.CancelRequest;

/**
 * Based on File.lastModified()
 *
 * @author Elmar Baumann
 */
public final class DeleteOutOfDateFiles {

    private final long deleteIfNMillisecondsOlderThanNow;
    private final Collection<? extends File> files;
    private static final Logger LOGGER = Logger.getLogger(DeleteOutOfDateFiles.class.getName());

    public DeleteOutOfDateFiles(Collection<? extends File> files, long deleteIfNMillisecondsOlderThanNow) {
        if (files == null) {
            throw new NullPointerException("files == null");
        }

        if (deleteIfNMillisecondsOlderThanNow < 0) {
            throw new IllegalArgumentException("Negative millisecond difference: " + deleteIfNMillisecondsOlderThanNow);
        }

        this.files = new ArrayList<File>(files);
        this.deleteIfNMillisecondsOlderThanNow = deleteIfNMillisecondsOlderThanNow;
    }

    /**
     *
     * @param cancelRequest  may be null
     * @return count of deleted files
     */
    public int start(CancelRequest cancelRequest) {
        int countDeleted = 0;
        for (File file : files) {
            if (cancelRequest != null && cancelRequest.isCancel()) {
                return countDeleted;
            }

            long lastModifiedInMilliseconds = file.lastModified();
            long nowInMilliseconds = System.currentTimeMillis();
            long minMilliseconds = nowInMilliseconds - deleteIfNMillisecondsOlderThanNow;
            boolean isTooOld = lastModifiedInMilliseconds < minMilliseconds;

            if (isTooOld) {
                LOGGER.log(Level.FINEST, "Deleting file ''{0}'' because it is older than {1} milliseconds", new Object[]{file, deleteIfNMillisecondsOlderThanNow});
                boolean deleted = file.delete();
                if (deleted) {
                    countDeleted++;
                } else {
                    LOGGER.log(Level.WARNING, "Could not delete file ''{0}''", file);
                }
            }
        }
        LOGGER.log(Level.FINEST, "Deleted {0} files", countDeleted);
        return countDeleted;
    }
}
