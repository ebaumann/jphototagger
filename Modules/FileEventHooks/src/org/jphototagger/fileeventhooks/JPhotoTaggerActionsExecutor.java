package org.jphototagger.fileeventhooks;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.openide.util.Lookup;

import org.jphototagger.api.file.event.FileCopiedEvent;
import org.jphototagger.api.file.event.FileDeletedEvent;
import org.jphototagger.api.file.event.FileMovedEvent;
import org.jphototagger.api.file.event.FileRenamedEvent;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesChangedEvent;
import org.jphototagger.lib.io.FileUtil;

/**
 * @author Elmar Baumann
 */
public final class JPhotoTaggerActionsExecutor {

    private static final Logger LOGGER = Logger.getLogger(JPhotoTaggerActionsExecutor.class.getName());
    private final Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
    private final Set<String> filenameSuffixes = new HashSet<String>();

    public JPhotoTaggerActionsExecutor() {
        filenameSuffixes.addAll(prefs.getStringCollection(PreferencesKeys.FILENAME_SUFFIXES_KEY));
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

    @EventSubscriber(eventClass = FileCopiedEvent.class)
    public void fileCopied(FileCopiedEvent evt) {
        File sourceFile = evt.getSourceFile();
        if (isSidecarFile(sourceFile)) {
            return;
        }
        File targetFile = evt.getTargetFile();
        synchronized (filenameSuffixes) {
            for (String suffix : filenameSuffixes) {
                File sourceFileWithSuffix = createFileWithSuffix(sourceFile, suffix);
                if (sourceFileWithSuffix != null && sourceFileWithSuffix.isFile()) {
                    File targetFileWithSuffix = createFileWithSuffix(targetFile, suffix);
                    if (targetFileWithSuffix != null) {
                        LOGGER.log(Level.INFO, "Copying also ''{0}'' to ''{1}''", new Object[]{sourceFileWithSuffix, targetFileWithSuffix});
                        try {
                            FileUtil.copyFile(sourceFileWithSuffix, targetFileWithSuffix);
                        } catch (Throwable t) {
                            LOGGER.log(Level.WARNING, "Error while copying ''{0}'' to ''{1}''", new Object[]{sourceFileWithSuffix, targetFileWithSuffix});
                        }
                    }
                }
            }
        }
    }

    @EventSubscriber(eventClass = FileDeletedEvent.class)
    public void fileDeleted(FileDeletedEvent evt) {
        File file = evt.getFile();
        if (isSidecarFile(file)) {
            return;
        }
        for (File fileWithSuffix : createFilesWithSuffixes(file)) {
            if (fileWithSuffix.isFile()) {
                LOGGER.log(Level.INFO, "Deleting also file ''{0}''", fileWithSuffix);
                if (!fileWithSuffix.delete()) {
                    LOGGER.log(Level.WARNING, "Couldn''t delete file: ''{0}''", fileWithSuffix);
                }
            }
        }
    }

    @EventSubscriber(eventClass = FileMovedEvent.class)
    public void fileMoved(FileMovedEvent evt) {
        File sourceFile = evt.getSourceFile();
        if (isSidecarFile(sourceFile)) {
            return;
        }
        File targetFile = evt.getTargetFile();
        synchronized (filenameSuffixes) {
            for (String suffix : filenameSuffixes) {
                moveFile(sourceFile, targetFile, suffix);
            }
        }
    }

    @EventSubscriber(eventClass = FileRenamedEvent.class)
    public void fileRenamed(FileRenamedEvent evt) {
        File sourceFile = evt.getSourceFile();
        if (isSidecarFile(sourceFile)) {
            return;
        }
        File targetFile = evt.getTargetFile();
        synchronized (filenameSuffixes) {
            for (String suffix : filenameSuffixes) {
                moveFile(sourceFile, targetFile, suffix);
            }
        }
    }

    private void moveFile(File sourceFile, File targetFile, String suffix) {
        File sourceFileWithSuffix = createFileWithSuffix(sourceFile, suffix);
        if (sourceFileWithSuffix != null && sourceFileWithSuffix.isFile()) {
            File targetFileWithSuffix = createFileWithSuffix(targetFile, suffix);
            if (targetFileWithSuffix != null) {
                LOGGER.log(Level.INFO, "Moving also ''{0}'' to ''{1}''", new Object[]{sourceFileWithSuffix, targetFileWithSuffix});
                if (!sourceFileWithSuffix.renameTo(targetFileWithSuffix)) {
                    LOGGER.log(Level.WARNING, "Error while moving ''{0}'' to ''{1}''", new Object[]{sourceFileWithSuffix, targetFileWithSuffix});
                }
            }
        }
    }

    private List<File> createFilesWithSuffixes(File file) {
        List<File> filesWithSuffixes;
        synchronized (filenameSuffixes) {
            filesWithSuffixes = new ArrayList<File>(filenameSuffixes.size());
            for (String suffix : filenameSuffixes) {
                File fileWithSuffix = createFileWithSuffix(file, suffix);
                if (fileWithSuffix != null) {
                    filesWithSuffixes.add(fileWithSuffix);
                }
            }
        }
        return filesWithSuffixes;
    }

    private File createFileWithSuffix(File file, String suffix) {
        String filePathname = file.getAbsolutePath();
        int lastDotIndex = filePathname.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filePathname.length() - 1) {
            String pathnameForFileWithSuffix = filePathname.substring(0, lastDotIndex + 1) + suffix;
            return new File(pathnameForFileWithSuffix);
        }
        return null;
    }

    @EventSubscriber(eventClass = PreferencesChangedEvent.class)
    public void userPreferenceChanged(PreferencesChangedEvent evt) {
        String key = evt.getKey();
        if (PreferencesKeys.FILENAME_SUFFIXES_KEY.equals(key)) {
            synchronized (filenameSuffixes) {
                filenameSuffixes.clear();
                filenameSuffixes.addAll(prefs.getStringCollection(PreferencesKeys.FILENAME_SUFFIXES_KEY));
            }
        }
    }

    private boolean isSidecarFile(File file) {
        String filenameLowercase = file.getName().toLowerCase();
        return filenameLowercase.endsWith(".xmp");
    }
}
