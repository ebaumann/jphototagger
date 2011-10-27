package org.jphototagger.program.module.filesystem;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bushe.swing.event.EventBus;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.file.CopyMoveFilesOptions;
import org.jphototagger.api.file.event.FileCopiedEvent;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.domain.FileCopyService;
import org.jphototagger.domain.event.listener.ProgressListenerSupport;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.io.SourceTargetFile;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = FileCopyService.class)
public final class CopyFiles implements Runnable, FileCopyService {

    private final ProgressListenerSupport progressListeners = new ProgressListenerSupport();
    private final List<File> errorFiles = new ArrayList<File>();
    private final CopyMoveFilesOptions options;
    private final List<SourceTargetFile> sourceTargetFiles;
    private volatile boolean cancel;
    private static final Logger LOGGER = Logger.getLogger(CopyFiles.class.getName());

    public CopyFiles() {
        sourceTargetFiles = Collections.emptyList();
        options = CopyMoveFilesOptions.CONFIRM_OVERWRITE;
    }

    public CopyFiles(Collection<? extends SourceTargetFile> sourceTargetFiles, CopyMoveFilesOptions options) {
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

    @Override
    public void addProgressListener(ProgressListener progessListener) {
        progressListeners.add(progessListener);
    }

    @Override
    public void removeProgressListener(ProgressListener progessListener) {
        progressListeners.add(progessListener);
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

                    EventBus.publish(new FileCopiedEvent(this, sourceFile, targetFile));
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

        if (options.equals(CopyMoveFilesOptions.RENAME_SOURCE_FILE_IF_TARGET_FILE_EXISTS) && targetFile.exists()) {
            targetFile = FileUtil.getNotExistingFile(targetFile);
        }

        return targetFile;
    }

    private void logCopyFile(File sourceFile, File targetFile) {
        LOGGER.log(Level.INFO, "Copy file ''{0}'' to ''{1}''", new Object[]{sourceFile, targetFile});
    }

    private synchronized void notifyStart() {
        ProgressEvent evt = new ProgressEvent.Builder().source(this).minimum(0).maximum(sourceTargetFiles.size()).value(0).build();

        progressListeners.notifyStarted(evt);
    }

    private synchronized void notifyPerformed(int value, SourceTargetFile sourceTargetFile) {
        ProgressEvent evt = new ProgressEvent.Builder().source(this).minimum(0).maximum(sourceTargetFiles.size()).value(value).info(sourceTargetFile).build();

        progressListeners.notifyPerformed(evt);
    }

    private synchronized void notifyEnded() {
        ProgressEvent evt = new ProgressEvent.Builder().source(this).minimum(0).maximum(sourceTargetFiles.size()).value(sourceTargetFiles.size()).info(errorFiles).build();

        progressListeners.notifyEnded(evt);
    }

    private boolean checkOverwrite(SourceTargetFile sourceTargetFile) {
        if (options.equals(CopyMoveFilesOptions.FORCE_OVERWRITE) || options.equals(CopyMoveFilesOptions.RENAME_SOURCE_FILE_IF_TARGET_FILE_EXISTS)) {
            return true;
        }

        File target = sourceTargetFile.getTargetFile();

        if (target.exists()) {
            String message = Bundle.getString(CopyFiles.class, "CopyFiles.Confirm.OverwriteExisting",
                    sourceTargetFile.getTargetFile(), sourceTargetFile.getSourceFile());

            MessageDisplayer.ConfirmAction action = MessageDisplayer.confirmYesNoCancel(null, message);

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
            String message = Bundle.getString(CopyFiles.class, "CopyFiles.Error.FilesAreEquals", sourceTargetFile.getSourceFile());

            MessageDisplayer.error(null, message);

            return false;
        }

        return true;
    }

    @Override
    public FileCopyService createInstance(Collection<? extends SourceTargetFile> sourceTargetFiles, CopyMoveFilesOptions options) {
        if (sourceTargetFiles == null) {
            throw new NullPointerException("sourceTargetFiles == null");
        }

        if (options == null) {
            throw new NullPointerException("options == null");
        }

        return new CopyFiles(sourceTargetFiles, options);
    }

    @Override
    public void copyInNewThread() {
        Thread thread = new Thread(this);
        thread.setName("JPhotoTagger: Copy Files");
        thread.start();
    }

    @Override
    public void copyWaitForTermination() {
        run();
    }
}
