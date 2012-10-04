package org.jphototagger.program.module.filesystem;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bushe.swing.event.EventBus;
import org.jphototagger.api.file.CopyMoveFilesOptions;
import org.jphototagger.api.file.event.FileCopiedEvent;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.domain.FileCopyService;
import org.jphototagger.domain.event.listener.ProgressListenerSupport;
import org.jphototagger.domain.filefilter.FileFilterUtil;
import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.domain.metadata.xmp.XmpSidecarFileResolver;
import org.jphototagger.domain.repository.SaveOrUpdate;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.io.SourceTargetFile;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.xmp.XmpMetadata;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = FileCopyService.class)
public final class FilesystemCopy implements Runnable, FileCopyService {

    private final ProgressListenerSupport progressListeners = new ProgressListenerSupport();
    private final List<File> errorFiles = new ArrayList<File>();
    private final XmpSidecarFileResolver xmpSidecarFileResolver = Lookup.getDefault().lookup(XmpSidecarFileResolver.class);
    private final CopyMoveFilesOptions options;
    private final List<SourceTargetFile> sourceTargetFiles;
    private volatile boolean cancel;
    private volatile boolean copyListenerShallUpdateRepository = true;
    private static final Logger LOGGER = Logger.getLogger(FilesystemCopy.class.getName());

    public FilesystemCopy() {
        sourceTargetFiles = Collections.emptyList();
        options = CopyMoveFilesOptions.CONFIRM_OVERWRITE;
    }

    public FilesystemCopy(Collection<? extends SourceTargetFile> sourceTargetFiles, CopyMoveFilesOptions options) {
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
                    Object userObject = sourceTargetFile.getUserObject();
                    sourceTargetFile = new SourceTargetFile(sourceFile, targetFile); // targetFile may be modified if existing and renamed
                    sourceTargetFile.setUserObject(userObject);
                    logCopyFile(sourceFile, targetFile);
                    FileUtil.copyFile(sourceFile, targetFile);
                    copyXmp(sourceTargetFile);
                    if (!copyListenerShallUpdateRepository && FileFilterUtil.isImageFile(targetFile)) {
                        FilesystemRepositoryUpdater.insertFile(targetFile);
                    }
                    publishCopied(sourceFile, targetFile);
                } catch (Exception ex) {
                    Logger.getLogger(FilesystemCopy.class.getName()).log(Level.SEVERE, null, ex);
                    errorFiles.add(sourceTargetFile.getSourceFile());
                }
            }
            notifyPerformed(i + 1, sourceTargetFile);
        }
        notifyEnded();
    }

    private void publishCopied(File sourceFile, File targetFile) {
        FileCopiedEvent evt = new FileCopiedEvent(this, sourceFile, targetFile);
        evt.putProperty(SaveOrUpdate.class, copyListenerShallUpdateRepository
                ? SaveOrUpdate.OUT_OF_DATE
                : SaveOrUpdate.NONE);
        EventBus.publish(evt);
    }

    private void copyXmp(SourceTargetFile sourceTargetFile) {
        Object userObject = sourceTargetFile.getUserObject();
        if (userObject instanceof Xmp) {
            File targetFile = sourceTargetFile.getTargetFile();
            if (!xmpSidecarFileResolver.hasXmpSidecarFile(targetFile)) {
                File xmpFile = xmpSidecarFileResolver.suggestXmpSidecarFile(targetFile);
                Xmp xmp = (Xmp) userObject;
                if (!xmp.isEmpty() && !XmpMetadata.writeXmpToSidecarFile(xmp, xmpFile)) {
                    LOGGER.log(Level.WARNING, "XMP sidecar file for ''{0}'' couldn''t be written!", targetFile);
                }
            }
        }
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
        ProgressEvent evt = new ProgressEvent.Builder()
                .source(this)
                .minimum(0)
                .maximum(sourceTargetFiles.size())
                .value(0)
                .stringPainted(true)
                .stringToPaint(Bundle.getString(FilesystemCopy.class, "FilesystemCopy.ProgressBarString"))
                .build();
        progressListeners.notifyStarted(evt);
    }

    private synchronized void notifyPerformed(int value, SourceTargetFile sourceTargetFile) {
        ProgressEvent evt = new ProgressEvent.Builder()
                .source(this)
                .minimum(0)
                .maximum(sourceTargetFiles.size())
                .value(value)
                .stringPainted(true)
                .stringToPaint(Bundle.getString(FilesystemCopy.class, "FilesystemCopy.ProgressBarString"))
                .info(sourceTargetFile)
                .build();
        progressListeners.notifyPerformed(evt);
    }

    private synchronized void notifyEnded() {
        ProgressEvent evt = new ProgressEvent.Builder()
                .source(this)
                .minimum(0)
                .maximum(sourceTargetFiles.size())
                .value(sourceTargetFiles.size())
                .info(errorFiles).build();
        progressListeners.notifyEnded(evt);
    }

    private boolean checkOverwrite(SourceTargetFile sourceTargetFile) {
        boolean force = options.equals(CopyMoveFilesOptions.FORCE_OVERWRITE);
        boolean renameExisting = options.equals(CopyMoveFilesOptions.RENAME_SOURCE_FILE_IF_TARGET_FILE_EXISTS);
        if (force || renameExisting) {
            return true;
        }
        File target = sourceTargetFile.getTargetFile();
        if (target.exists()) {
            String message = Bundle.getString(FilesystemCopy.class, "FilesystemCopy.Confirm.OverwriteExisting",
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
            String message = Bundle.getString(FilesystemCopy.class, "FilesystemCopy.Error.FilesAreEquals", sourceTargetFile.getSourceFile());
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
        return new FilesystemCopy(sourceTargetFiles, options);
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

    /**
     * @param update Default: true
     */
    @Override
    public void setCopyListenerShallUpdateRepository(boolean update) {
        copyListenerShallUpdateRepository = update;
    }

    public boolean getCopyListenerShallUpdateRepository() {
        return copyListenerShallUpdateRepository;
    }
}
