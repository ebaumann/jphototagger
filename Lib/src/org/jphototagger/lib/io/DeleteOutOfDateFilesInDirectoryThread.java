package org.jphototagger.lib.io;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;
import org.jphototagger.api.concurrent.CancelRequest;
import org.jphototagger.api.concurrent.Cancelable;

/**
 * @author Elmar Baumann
 */
public final class DeleteOutOfDateFilesInDirectoryThread extends Thread implements Cancelable {

    private final File directory;
    private final FileFilter fileFilter;
    private final long deleteIfNMillisecondsOlderThanNow;
    private volatile boolean cancel;

    public DeleteOutOfDateFilesInDirectoryThread(File directory, FileFilter fileFilter, long deleteIfNMillisecondsOlderThanNow) {
        super("JPhotoTagger: Deleting out of date files");
        if (directory == null) {
            throw new NullPointerException("directory == null");
        }
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Not a directory: " + directory);
        }
        if (fileFilter == null) {
            throw new NullPointerException("fileFilter == null");
        }

        this.directory = directory;
        this.fileFilter = fileFilter;
        this.deleteIfNMillisecondsOlderThanNow = deleteIfNMillisecondsOlderThanNow;
    }

    @Override
    public void run() {
        List<File> files = Arrays.asList(directory.listFiles(fileFilter));
        DeleteOutOfDateFiles deleteOutOfDateFiles = new DeleteOutOfDateFiles(files, deleteIfNMillisecondsOlderThanNow);

        deleteOutOfDateFiles.start(cancelRequest);
    }

    @Override
    public void cancel() {
        cancel = true;
    }

    private final CancelRequest cancelRequest = new CancelRequest() {

        @Override
        public boolean isCancel() {
            return cancel;
        }
    };
}
