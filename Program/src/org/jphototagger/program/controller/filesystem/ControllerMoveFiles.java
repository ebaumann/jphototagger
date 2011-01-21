package org.jphototagger.program.controller.filesystem;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.event.listener.FileSystemListener;
import org.jphototagger.program.view.dialogs.MoveToDirectoryDialog;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import java.util.List;
import org.jphototagger.program.resource.GUI;

/**
 * Renames files in the file system.
 *
 * @author Elmar Baumann
 */
public final class ControllerMoveFiles
        implements ActionListener, FileSystemListener {
    public ControllerMoveFiles() {
        listen();
    }

    private void listen() {
        PopupMenuThumbnails.INSTANCE.getItemFileSystemMoveFiles()
            .addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        moveSelectedFiles();
    }

    private void moveSelectedFiles() {
        List<File> selFiles = GUI.getSelectedImageFiles();

        if (!selFiles.isEmpty()) {
            MoveToDirectoryDialog dlg = new MoveToDirectoryDialog();

            dlg.setSourceFiles(selFiles);
            dlg.addFileSystemListener(this);
            dlg.setVisible(true);
        } else {
            AppLogger.logWarning(
                ControllerMoveFiles.class,
                "ControllerMoveFiles.ErrorMessaga.NoImagesSelected");
        }
    }

    /**
     * Moves files into a target directory without asking for confirmation.
     *
     * @param srcFiles  source files to move
     * @param targetDir target directory
     */
    public void moveFiles(List<File> srcFiles, File targetDir) {
        if (srcFiles == null) {
            throw new NullPointerException("srcFiles == null");
        }

        if (targetDir == null) {
            throw new NullPointerException("targetDir == null");
        }

        if (!srcFiles.isEmpty() && targetDir.isDirectory()) {
            MoveToDirectoryDialog dlg = new MoveToDirectoryDialog();

            dlg.setSourceFiles(srcFiles);
            dlg.setTargetDirectory(targetDir);
            dlg.addFileSystemListener(this);
            dlg.setVisible(true);
        }
    }

    private boolean isXmpFile(File file) {
        return file.getName().toLowerCase().endsWith("xmp");
    }

    @Override
    public void fileMoved(File source, File target) {
        if (!isXmpFile(source)) {
            DatabaseImageFiles.INSTANCE.updateRename(source, target);
        }
    }

    @Override
    public void fileCopied(File source, File target) {

        // ignore
    }

    @Override
    public void fileDeleted(File file) {

        // ignore
    }

    @Override
    public void fileRenamed(File oldFile, File newFile) {

        // ignore
    }
}
