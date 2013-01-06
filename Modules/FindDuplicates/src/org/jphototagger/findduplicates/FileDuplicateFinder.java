package org.jphototagger.findduplicates;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.domain.filefilter.AppFileFilterProvider;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class FileDuplicateFinder implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(FileDuplicateFinder.class.getName());
    private final List<File> sourceDirectories;
    private final boolean recursive;
    private final AppFileFilterProvider fileFilter = Lookup.getDefault().lookup(AppFileFilterProvider.class);
    private final Map<Long, Set<File>> filesOfSameSize = new HashMap<>();
    private final Map<Long, Set<File>> duplicateFiles = new HashMap<>();
    private int filecount;
    private volatile boolean compareOnlyEqualFilenames;
    private volatile boolean compareOnlyEqualDates;
    private volatile boolean stop;

    public FileDuplicateFinder(List<File> sourceDirectories, boolean recursive) {
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

    public void setCompareOnlyEqualFilenames(boolean compareOnlyEqualFilenames) {
        this.compareOnlyEqualFilenames = compareOnlyEqualFilenames;
    }

    public boolean isCompareOnlyEqualDates() {
        return compareOnlyEqualDates;
    }

    public void setCompareOnlyEqualDates(boolean compareOnlyEqualDates) {
        this.compareOnlyEqualDates = compareOnlyEqualDates;
    }

    /**
     * @return map key is size, set contains files with equal contents
     */
    public Map<Long, Set<File>> getDuplicateFiles() {
        return duplicateFiles;
    }

    @Override
    public void run() {
        findFilesOfSameSize();
        findDuplicates();
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
                    Logger.getLogger(FileDuplicateFinder.class.getName()).log(Level.SEVERE, null, ex);
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
        throw new UnsupportedOperationException();
    }

    private void addFile(File file) { // file can be directory
        if (file.isFile() && fileFilter.isAcceptedImageFile(file)) {
            Long size = file.length();
            Set<File> files = filesOfSameSize.get(size);
            if (files == null) {
                files = new HashSet<>();
            }
            files.add(file);
            filecount++;
            filesOfSameSize.put(size, files);
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
