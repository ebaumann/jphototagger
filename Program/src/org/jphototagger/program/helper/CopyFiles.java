package org.jphototagger.program.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.domain.event.listener.impl.ProgressListenerSupport;
import org.jphototagger.lib.concurrent.Cancelable;
import org.jphototagger.lib.event.ProgressEvent;
import org.jphototagger.lib.event.listener.ProgressListener;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.io.SourceTargetFile;
import org.jphototagger.program.app.MessageDisplayer;

/**
 * Kopieren von Dateien.
 *
 * @author Elmar Baumann
 */
public final class CopyFiles implements Runnable, Cancelable {

    private final ProgressListenerSupport ls = new ProgressListenerSupport();
    private final List<File> errorFiles = new ArrayList<File>();
    private final Options options;
    private final List<SourceTargetFile> sourceTargetFiles;
    private volatile boolean cancel;
    private static final Logger LOGGER = Logger.getLogger(CopyFiles.class.getName());

    /**
     * Konstruktor
     *
     * @param sourceTargetFiles    Zu kopierende Dateien. Die erste im Paar
     *                 ist die Quelldatei, die zweite die Zieldatei.
     * @param options  Optionen
     */
    public CopyFiles(List<SourceTargetFile> sourceTargetFiles, Options options) {
        if (sourceTargetFiles == null) {
            throw new NullPointerException("sourceTargetFiles == null");
        }

        if (options == null) {
            throw new NullPointerException("options == null");
        }

        this.sourceTargetFiles = new ArrayList<SourceTargetFile>(sourceTargetFiles);
        this.options = options;
    }

    @Override
    public synchronized void cancel() {
        cancel = true;
    }

    /**
     * Copy options.
     */
    public enum Options {

        /** Overwrite existing files only if confirmed */
        CONFIRM_OVERWRITE(0),
        /** Overwrite existing files without confirmYesNo */
        FORCE_OVERWRITE(1),
        /** Rename the source file if the target file exists */
        RENAME_SRC_FILE_IF_TARGET_FILE_EXISTS(2),;
        private final int index;

        private Options(int index) {
            this.index = index;
        }

        public int getInt() {
            return index;
        }

        public static Options fromInt(int index) {
            for (Options o : values()) {
                if (o.getInt() == index) {
                    return o;
                }
            }

            assert false : "Invalid index: " + index;

            return CONFIRM_OVERWRITE;
        }
    }

    /**
     * Fügt einen Aktionsbeobachter hinzu.
     * {@link ProgressListener#progressPerformed(ProgressEvent)}
     * liefert ein
     * {@link  org.jphototagger.program.event.ProgressEvent}-Objekt,
     * das mit {@link  org.jphototagger.program.event.ProgressEvent#getInfo()}
     * ein {@link SourceTargetFile}-Objekt liefert.
     *
     * {@link ProgressListener#progressEnded(ProgressEvent)}
     * liefert ein
     * {@link  org.jphototagger.program.event.ProgressEvent}-Objekt,
     * das mit {@link  ProgressEvent#getInfo()}
     * ein {@link java.util.List}-Objekt mit den Dateinamen der Dateien, die
     * nicht kopiert werden konnten.
     *
     * @param listener  Beobachter
     */
    public synchronized void addProgressListener(ProgressListener listener) {
        ls.add(listener);
    }

    @Override
    public void run() {
        notifyStart();

        int size = sourceTargetFiles.size();

        for (int i = 0; !cancel && (i < size); i++) {
            SourceTargetFile sourceTargetFile = sourceTargetFiles.get(i);

            if (checkDifferent(sourceTargetFile) && checkOverwrite(sourceTargetFile)) {
                try {
                    File sourceFile = sourceTargetFile.getSourceFile();
                    File targetFile = getTargetFile(sourceTargetFile);

                    logCopyFile(sourceFile, targetFile);
                    FileUtil.copyFile(sourceFile, targetFile);
                } catch (Exception ex) {
                    Logger.getLogger(CopyFiles.class.getName()).log(Level.SEVERE, null, ex);
                    errorFiles.add(sourceTargetFile.getSourceFile());
                }
            }

            notifyPerformed(i, sourceTargetFile);
        }

        notifyEnded();
    }

    private File getTargetFile(SourceTargetFile files) {
        File targetFile = files.getTargetFile();

        if (options.equals(Options.RENAME_SRC_FILE_IF_TARGET_FILE_EXISTS) && targetFile.exists()) {
            targetFile = FileUtil.getNotExistingFile(targetFile);
        }

        return targetFile;
    }

    private void logCopyFile(File sourceFile, File targetFile) {
        LOGGER.log(Level.INFO, "Copy file ''{0}'' to ''{1}''", new Object[]{sourceFile, targetFile});
    }

    private synchronized void notifyStart() {
        ProgressEvent evt = new ProgressEvent(this, 0, sourceTargetFiles.size(), 0, null);

        ls.notifyStarted(evt);
    }

    private synchronized void notifyPerformed(int value, SourceTargetFile sourceTargetFile) {
        ProgressEvent evt = new ProgressEvent(this, 0, sourceTargetFiles.size(), value, sourceTargetFile);

        ls.notifyPerformed(evt);
    }

    private synchronized void notifyEnded() {
        ProgressEvent evt = new ProgressEvent(this, 0, sourceTargetFiles.size(), sourceTargetFiles.size(), errorFiles);

        ls.notifyEnded(evt);
    }

    private boolean checkOverwrite(SourceTargetFile sourceTargetFile) {
        if (options.equals(Options.FORCE_OVERWRITE) || options.equals(Options.RENAME_SRC_FILE_IF_TARGET_FILE_EXISTS)) {
            return true;
        }

        File target = sourceTargetFile.getTargetFile();

        if (target.exists()) {
            MessageDisplayer.ConfirmAction action = MessageDisplayer.confirmYesNoCancel(null,
                    "CopyFiles.Confirm.OverwriteExisting", sourceTargetFile.getTargetFile(), sourceTargetFile.getSourceFile());

            if (action.equals(MessageDisplayer.ConfirmAction.CANCEL)) {
                cancel();
            } else {
                return action.equals(MessageDisplayer.ConfirmAction.YES);
            }
        }

        return true;
    }

    private boolean checkDifferent(SourceTargetFile sourceTargetFile) {
        if (sourceTargetFile.getSourceFile().equals(sourceTargetFile.getTargetFile())) {
            MessageDisplayer.error(null, "CopyFiles.Error.FilesAreEquals", sourceTargetFile.getSourceFile());

            return false;
        }

        return true;
    }
}
