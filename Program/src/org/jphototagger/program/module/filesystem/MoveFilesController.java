package org.jphototagger.program.module.filesystem;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.api.concurrent.Cancelable;
import org.jphototagger.api.file.CopyMoveFilesOptions;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressHandle;
import org.jphototagger.api.progress.ProgressHandleFactory;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.program.module.thumbnails.ThumbnailsPopupMenu;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.settings.AppPreferencesKeys;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class MoveFilesController implements ActionListener {

    private static final Logger LOGGER = Logger.getLogger(MoveFilesController.class.getName());

    public MoveFilesController() {
        listen();
    }

    private void listen() {
        ThumbnailsPopupMenu.INSTANCE.getItemFileSystemMoveFiles().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        moveSelectedFiles();
    }

    private void moveSelectedFiles() {
        List<File> selFiles = GUI.getSelectedImageFiles();
        if (!selFiles.isEmpty()) {
            MoveFilesToDirectoryDialog dlg = new MoveFilesToDirectoryDialog();
            dlg.setSourceFiles(selFiles);
            dlg.setVisible(true);
        } else {
            LOGGER.log(Level.WARNING, "Moving images: No images selected!");
        }
    }

    public void moveFilesWithoutConfirm(List<File> sourceFiles, File targetDirectory) {
        if (sourceFiles == null) {
            throw new NullPointerException("sourceFiles == null");
        }
        if (targetDirectory == null) {
            throw new NullPointerException("targetDirectory == null");
        }
        if (!sourceFiles.isEmpty() && targetDirectory.isDirectory()) {
            CopyMoveFilesOptions copyMoveFilesOptions = getCopyMoveFilesOptions();
            boolean renameIfTargetFileExists = copyMoveFilesOptions.equals(CopyMoveFilesOptions.RENAME_SOURCE_FILE_IF_TARGET_FILE_EXISTS);
            FileSystemMove fileSystemMove = new FileSystemMove(sourceFiles, targetDirectory, renameIfTargetFileExists);
            fileSystemMove.setMoveListenerShallUpdateRepository(false);
            fileSystemMove.addProgressListener(new MoveProgressListener(fileSystemMove));
            Thread thread = new Thread(fileSystemMove, "JPhotoTagger: Moving files");
            thread.start();
        }
    }

    private CopyMoveFilesOptions getCopyMoveFilesOptions() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        return prefs.containsKey(AppPreferencesKeys.KEY_FILE_SYSTEM_OPERATIONS_OPTIONS_COPY_MOVE_FILES)
                ? CopyMoveFilesOptions.parseInteger(prefs.getInt(AppPreferencesKeys.KEY_FILE_SYSTEM_OPERATIONS_OPTIONS_COPY_MOVE_FILES))
                : CopyMoveFilesOptions.CONFIRM_OVERWRITE;
    }

    private static class MoveProgressListener implements ProgressListener, Cancelable {

        private final FileSystemMove fileSystemMove;
        private ProgressHandle progressHandle;

        private MoveProgressListener(FileSystemMove fileSystemMove) {
            this.fileSystemMove = fileSystemMove;
        }

        @Override
        public void progressStarted(ProgressEvent evt) {
            progressHandle = Lookup.getDefault().lookup(ProgressHandleFactory.class).createProgressHandle(this);
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
            fileSystemMove.cancel();
        }
    }
}
