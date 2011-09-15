package org.jphototagger.program.controller.filesystem;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.file.event.FileMovedEvent;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.MoveToDirectoryDialog;
import org.jphototagger.program.view.popupmenus.ThumbnailsPopupMenu;
import org.openide.util.Lookup;

/**
 * Renames files in the file system.
 *
 * @author Elmar Baumann
 */
public final class MoveFilesController implements ActionListener {

    private static final Logger LOGGER = Logger.getLogger(MoveFilesController.class.getName());
    private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);

    public MoveFilesController() {
        listen();
    }

    private void listen() {
        ThumbnailsPopupMenu.INSTANCE.getItemFileSystemMoveFiles().addActionListener(this);
        AnnotationProcessor.process(this);
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
            dlg.setVisible(true);
        } else {
            LOGGER.log(Level.WARNING, "Moving images: No images selected!");
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
            dlg.setVisible(true);
        }
    }

    private boolean isXmpFile(File file) {
        return file.getName().toLowerCase().endsWith("xmp");
    }

    @EventSubscriber(eventClass = FileMovedEvent.class)
    public void fileMoved(FileMovedEvent evt) {
        File sourceFile = evt.getSourceFile();
        File targetFile = evt.getTargetFile();

        if (!isXmpFile(sourceFile)) {
            repo.updateRenameImageFile(sourceFile, targetFile);
        }
    }
}
