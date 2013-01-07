package org.jphototagger.findduplicates;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.domain.filefilter.AppFileFilterProvider;
import org.jphototagger.lib.io.IoUtil;
import org.openide.util.Lookup;

/**
 * For each found duplicates a {@link FileDuplicatesListener} will be notified by calling
 * {@link FileDuplicatesListener#duplicatesFound(java.util.Collection)}.
 *
 * @author Elmar Baumann
 */
public final class FileDuplicatesFinder implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(FileDuplicatesFinder.class.getName());
    private final List<FileDuplicatesListener> fileDuplicatesListeners = new CopyOnWriteArrayList<>();
    private final List<File> sourceDirectories;
    private final boolean recursive;
    private final AppFileFilterProvider fileFilter = Lookup.getDefault().lookup(AppFileFilterProvider.class);
    private final Map<Long, Set<File>> filesOfSameSize = new HashMap<>();
    private int filecount;
    private volatile boolean compareOnlyEqualFilenames;
    private volatile boolean compareOnlyEqualDates;
    private volatile boolean stop;

    public FileDuplicatesFinder(List<File> sourceDirectories, boolean recursive) {
        if (sourceDirectories == null) {
            throw new NullPointerException("sourceDirectories == null");
        }
        this.sourceDirectories = new ArrayList<>(sourceDirectories);
        this.recursive = recursive;
    }

    public void stop() {
        this.stop = true;
    }

    public boolean isCompareOnlyEqualFilenames() {
        return compareOnlyEqualFilenames;
    }

    /**
     * @param compareOnlyEqualFilenames Default: false
     */
    public void setCompareOnlyEqualFilenames(boolean compareOnlyEqualFilenames) {
        this.compareOnlyEqualFilenames = compareOnlyEqualFilenames;
    }

    public boolean isCompareOnlyEqualDates() {
        return compareOnlyEqualDates;
    }

    /**
     * @param compareOnlyEqualDates Default: false
     */
    public void setCompareOnlyEqualDates(boolean compareOnlyEqualDates) {
        this.compareOnlyEqualDates = compareOnlyEqualDates;
    }

    public void addFileDuplicateListener(FileDuplicatesListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }
        fileDuplicatesListeners.add(listener);
    }

    public void removeFileDuplicateListener(FileDuplicatesListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }
        fileDuplicatesListeners.remove(listener);
    }

    @Override
    public void run() {
        notifySearchStarted();
        findFilesOfSameSize();
        findDuplicates();
        notifySearchFinished();
    }

    private void findFilesOfSameSize() {
        filecount = 0;
        int dirCount = sourceDirectories.size();
        LOGGER.log(Level.INFO, "Searching {0}{1} directories for files of same size", new Object[]{recursive ? "recursively " : "", dirCount});
        for (int dirIndex = 0; dirIndex < dirCount && !stop; dirIndex++) {
            File dir = sourceDirectories.get(dirIndex);
            if (!dir.isDirectory()) {
                continue;
            }
            if (recursive) {
                try {
                    LOGGER.log(Level.FINE, "Searching directory ''{0}'' recursively for files of same size", dir);
                    Files.walkFileTree(dir.toPath(), fileVisitor);
                } catch (IOException ex) {
                    Logger.getLogger(FileDuplicatesFinder.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                LOGGER.log(Level.FINE, "Searching directory ''{0}'' for files of same size", dir);
                File[] files = dir.listFiles();
                if (files != null) {
                    for (int fileIndex = 0; fileIndex < files.length && !stop; fileIndex++) {
                        addFile(files[fileIndex]);
                    }
                }
            }
        }
    }

    private void findDuplicates() {
        LOGGER.log(Level.INFO, "Comparing files with same size to find duplicates. Total file count is {0}.", filecount);
        for (long size : filesOfSameSize.keySet()) {
            Set<File> duplicates = new HashSet<>();
            List<File> sameSizedFiles = new ArrayList<>(filesOfSameSize.get(size));
            int count = sameSizedFiles.size();
            for (int i = 0; i < count - 1; i++) {
                File select = sameSizedFiles.get(i);
                for (int j = i + 1; j < count; j++) {
                    File candidate = sameSizedFiles.get(j);
                    if (isCompare(select, candidate)) {
                        LOGGER.log(Level.FINEST, "Comparing file ''{0}'' with ''{1}''", new Object[]{select, candidate});
                        if (equals(select, candidate)) {
                            duplicates.add(select); // Added 1 times (Set)
                            duplicates.add(candidate); // Added 1 times (Set)
                        }
                    }
                }
            }
            if (duplicates.size() > 1) {
                notifyDuplicatesFound(duplicates);
            }
        }
    }

    private boolean equals(File file1, File file2) {
        try (InputStream is1 = new FileInputStream(file1); InputStream is2 = new FileInputStream(file2)) {
            return IoUtil.contentEquals(is1, is2);
        } catch (Throwable t) {
            Logger.getLogger(FileDuplicatesFinder.class.getName()).log(Level.SEVERE, null, t);
            return false;
        }
    }

    private boolean isCompare(File file1, File file2) {
        assert file1.length() == file2.length();
        if (compareOnlyEqualFilenames && !file1.getName().equals(file2.getName())) {
            return false;
        }
        if (compareOnlyEqualDates && file1.lastModified() != file2.lastModified()) {
            return false;
        }
        return true;
    }

    private void notifySearchStarted() {
        for (FileDuplicatesListener listener : fileDuplicatesListeners) {
            listener.searchStarted();
        }
    }

    private void notifySearchFinished() {
        LOGGER.log(Level.INFO, "Stopping finding duplicates. Cancelled: {0}", stop);
        for (FileDuplicatesListener listener : fileDuplicatesListeners) {
            listener.searchFinished(stop);
        }
    }

    private void notifyDuplicatesFound(Collection<? extends File> duplicates) {
        for (FileDuplicatesListener listener : fileDuplicatesListeners) {
            listener.duplicatesFound(duplicates);
        }
    }

    private void addFile(File file) { // file can be directory
        if (file.isFile() && fileFilter.isAcceptedImageFile(file)) {
            Long size = file.length();
            Set<File> sameSizedFiles = filesOfSameSize.get(size);
            if (sameSizedFiles == null) {
                sameSizedFiles = new HashSet<>();
                filesOfSameSize.put(size, sameSizedFiles);
            }
            sameSizedFiles.add(file);
            filecount++;
        }
    }

    private final FileVisitor<Path> fileVisitor = new FileVisitor<Path>() {
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            return isVisit();
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            addFile(file.toFile());
            return isVisit();
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            return isVisit();
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            return isVisit();
        }

        private FileVisitResult isVisit() {
            return stop
                    ? FileVisitResult.TERMINATE
                    : FileVisitResult.CONTINUE;
        }
    };
}
