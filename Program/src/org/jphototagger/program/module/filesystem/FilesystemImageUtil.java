package org.jphototagger.program.module.filesystem;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openide.util.Lookup;

import org.jphototagger.api.concurrent.Cancelable;
import org.jphototagger.api.file.CopyMoveFilesOptions;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressHandle;
import org.jphototagger.api.progress.ProgressHandleFactory;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.domain.filefilter.FileFilterUtil;
import org.jphototagger.domain.metadata.xmp.XmpSidecarFileResolver;
import org.jphototagger.lib.io.SourceTargetFile;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.factory.ControllerFactory;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.settings.AppPreferencesKeys;
import org.jphototagger.xmp.XmpMetadata;

/**
 * @author Elmar Baumann
 */
public final class FilesystemImageUtil {

    private static final XmpSidecarFileResolver XMP_SIDECAR_FILE_RESOLVER = Lookup.getDefault().lookup(XmpSidecarFileResolver.class);

    private FilesystemImageUtil() {
    }

    public enum ConfirmOverwrite {

        YES, NO;

        public boolean yes() {
            return this.equals(YES);
        }

        public static ConfirmOverwrite fromBoolean(boolean b) {
            return b
                    ? YES
                    : NO;
        }
    }

    /**
     * Copies image files to a directory with the {@code CopyToDirectoryDialog}. Does not update the repository.
     *
     * @param sourceFiles source files
     * @param targetDirectory target directory
     * @param confirm         {@code ConfirmOverwrite#YES} if the user must confirm overwrite existing files
     */
    public static void copyImageFiles(List<File> sourceFiles, File targetDirectory, ConfirmOverwrite confirm) {
        if (sourceFiles == null) {
            throw new NullPointerException("sourceFiles == null");
        }
        if (targetDirectory == null) {
            throw new NullPointerException("targetDirectory == null");
        }
        if (confirm == null) {
            throw new NullPointerException("confirm == null");
        }
        String message = Bundle.getString(FilesystemImageUtil.class, "FilesystemImageUtil.Confirm.Copy", sourceFiles.size(), targetDirectory.getAbsolutePath());
        if (confirm.yes() && !confirmFileAction(message)) {
            return;
        }
        FilesystemCopy fileSystemCopy = new FilesystemCopy(getFiles(sourceFiles, targetDirectory), getCopyMoveFilesOptions());
        fileSystemCopy.setCopyListenerShallUpdateRepository(false);
        fileSystemCopy.addProgressListener(new CopyProgressListener(fileSystemCopy));
        Thread thread = new Thread(fileSystemCopy, "JPhotoTagger: Copying files to directories");
        thread.start();
    }

    private static CopyMoveFilesOptions getCopyMoveFilesOptions() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        return prefs.containsKey(AppPreferencesKeys.KEY_FILE_SYSTEM_OPERATIONS_OPTIONS_COPY_MOVE_FILES)
                ? CopyMoveFilesOptions.parseInteger(prefs.getInt(AppPreferencesKeys.KEY_FILE_SYSTEM_OPERATIONS_OPTIONS_COPY_MOVE_FILES))
                : CopyMoveFilesOptions.CONFIRM_OVERWRITE;
    }

    private static List<SourceTargetFile> getFiles(Collection<? extends File> sourceFiles, File targetDirectory) {
        List<SourceTargetFile> sourceTargetFiles = new ArrayList<SourceTargetFile>();
        for (File sourceFile : sourceFiles) {
            File targetFile = new File(targetDirectory + File.separator + sourceFile.getName());
            addXmp(sourceFile, targetDirectory, sourceTargetFiles);
            sourceTargetFiles.add(new SourceTargetFile(sourceFile, targetFile));
        }
        return sourceTargetFiles;
    }

    private static void addXmp(File imageFile, File targetDirectory, List<SourceTargetFile> sourceTargetFiles) {
        File sidecarFile = XMP_SIDECAR_FILE_RESOLVER.getXmpSidecarFileOrNullIfNotExists(imageFile);
        if (sidecarFile != null) {
            File sourceSidecarFile = sidecarFile;
            File targetSidecarFile = new File(targetDirectory + File.separator + sourceSidecarFile.getName());
            sourceTargetFiles.add(new SourceTargetFile(sourceSidecarFile, targetSidecarFile));
        }
    }

    /**
     * Moves image files to a directory with the {@code MoveToDirectoryDialog}.
     *
     * @param sourceFiles source files
     * @param targetDirectory target directory
     * @param confirm         {@code ConfirmOverwrite#YES} if the user must confirm overwrite existing files
     */
    public static void moveImageFiles(List<File> sourceFiles, File targetDirectory, ConfirmOverwrite confirm) {
        String message = Bundle.getString(FilesystemImageUtil.class, "FilesystemImageUtil.Confirm.Move", sourceFiles.size(), targetDirectory.getAbsolutePath());
        if (confirm.yes() && !confirmFileAction(message)) {
            return;
        }
        MoveFilesController ctrl = ControllerFactory.INSTANCE.getController(MoveFilesController.class);
        if (ctrl != null) {
            ctrl.moveFilesWithoutConfirm(sourceFiles, targetDirectory);
        }
    }

    private static boolean confirmFileAction(String message) {
        return MessageDisplayer.confirmYesNo(null, message);
    }

    private static class CopyProgressListener implements ProgressListener, Cancelable {

        private final FilesystemCopy fileSystemCopy;
        private ProgressHandle progressHandle;

        private CopyProgressListener(FilesystemCopy fileSystemCopy) {
            this.fileSystemCopy = fileSystemCopy;
        }

        @Override
        public void progressStarted(ProgressEvent evt) {
            progressHandle = Lookup.getDefault().lookup(ProgressHandleFactory.class).createProgressHandle(this  );
            progressHandle.progressStarted(evt);
        }

        @Override
        public void progressPerformed(ProgressEvent evt) {
            progressHandle.progressPerformed(evt);
        }

        @Override
        public void progressEnded(ProgressEvent evt) {
            progressHandle.progressEnded();
            GUI.refreshThumbnailsPanel();
        }

        @Override
        public void cancel() {
            fileSystemCopy.cancel();
        }
    }

    /**
     * Adds to a list of image files sidecar files.
     *
     * If a file in
     * <code>imageFiles</code> is not an image file it will not be added.
     *
     * @param imageFiles image files
     * @return image files and their sidecar files
     */
    public static List<File> addSidecarFiles(List<File> imageFiles) {
        if (imageFiles == null) {
            throw new NullPointerException("imageFiles == null");
        }
        List<File> files = new ArrayList<File>(imageFiles.size() * 2);
        for (File imageFile : imageFiles) {
            if ((imageFile != null) && FileFilterUtil.isImageFile(imageFile)) {
                files.add(imageFile);
                File sidecarFile = XMP_SIDECAR_FILE_RESOLVER.getXmpSidecarFileOrNullIfNotExists(imageFile);
                if (sidecarFile != null) {
                    files.add(sidecarFile);
                }
            }
        }
        return files;
    }

    /**
     * Checks whether a sidecar file can be written for an image file, else displays an error message.
     *
     * @param imageFile image file
     * @return true if a sidecar file can be written.
     */
    public static boolean checkImageEditable(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }
        if (!XmpMetadata.canWriteSidecarFileForImageFile(imageFile)) {
            String message = Bundle.getString(FilesystemImageUtil.class, "FilesystemImageUtil.Error.WriteSidecarFile", imageFile.getParentFile());
            MessageDisplayer.error(null, message);
            return false;
        }

        return true;
    }
}
