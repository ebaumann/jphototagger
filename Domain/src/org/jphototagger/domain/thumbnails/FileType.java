package org.jphototagger.domain.thumbnails;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Elmar Baumann
 */
public final class FileType {

    private final Set<String> filenameSuffixes;
    private final String displayName;

    /**
     * @param displayName
     * @param filenameSuffixes Without leading dot, will be converted into lower case
     */
    public FileType(String displayName, String... filenameSuffixes) {
        this(displayName, Arrays.asList(filenameSuffixes));
    }

    /**
     * @param displayName
     * @param filenameSuffixes Without leading dot, will be converted into lower case
     */
    public FileType(String displayName, Collection<? extends String> filenameSuffixes) {
        if (displayName == null) {
            throw new NullPointerException("displayName == null");
        }
        if (filenameSuffixes == null) {
            throw new NullPointerException("filenameSuffixes == null");
        }
        if (filenameSuffixes.size() < 1) {
            throw new IllegalStateException("At least one suffix has to be defined");
        }
        this.filenameSuffixes = new HashSet<>(filenameSuffixes.size());
        for (String filenameSuffix : filenameSuffixes) {
            this.filenameSuffixes.add(filenameSuffix.toLowerCase());
        }
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Set<String> getFilenameSuffixes() {
        return Collections.unmodifiableSet(filenameSuffixes);
    }

    public java.io.FileFilter createFileFilter() {
        return new java.io.FileFilter() {

            @Override
            public boolean accept(File file) {
                return isAccept(file);
            }
        };
    }

    public javax.swing.filechooser.FileFilter createFileChooserFilter() {
        return new javax.swing.filechooser.FileFilter() {

            @Override
            public boolean accept(File file) {
                return isAccept(file);
            }

            @Override
            public String getDescription() {
                return displayName;
            }
        };
    }

    private boolean isAccept(File file) {
        String name = file.getName().toLowerCase();
        for (String filenameSuffix : filenameSuffixes) {
            if (name.endsWith("." + filenameSuffix)) {
                return true;
            }
        }
        return false;
    }
}
