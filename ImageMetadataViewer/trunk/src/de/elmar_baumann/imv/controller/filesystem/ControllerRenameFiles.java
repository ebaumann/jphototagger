package de.elmar_baumann.imv.controller.filesystem;

import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.event.listener.impl.ListenerProvider;
import de.elmar_baumann.imv.event.RenameFileEvent;
import de.elmar_baumann.imv.event.listener.RenameFileListener;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.dialogs.RenameDialog;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.Collections;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 * Listens to key events of {@link ImageFileThumbnailsPanel} and when
 * <code>F2</code> was pressed shows the {@link RenameDialog} to rename the
 * selected files.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/13
 */
public final class ControllerRenameFiles
        implements KeyListener, RenameFileListener {

    private final ImageFileThumbnailsPanel thumbnailsPanel =
            GUI.INSTANCE.getAppPanel().getPanelThumbnails();
    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;

    public ControllerRenameFiles() {
        listen();
    }

    private void listen() {
        thumbnailsPanel.addKeyListener(this);
        ListenerProvider.INSTANCE.addRenameFileListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_F2) {
            renameSelectedFiles();
        }
    }

    private void renameSelectedFiles() {
        List<File> files = thumbnailsPanel.getSelectedFiles();
        if (files.size() > 0) {
            RenameDialog dialog = new RenameDialog();
            Collections.sort(files);
            dialog.setFiles(files);
            dialog.setEnabledTemplates(
                    thumbnailsPanel.getContent().isUniqueFileSystemDirectory());
            dialog.setVisible(true);
        }
    }

    @Override
    public void actionPerformed(final RenameFileEvent action) {
        db.updateRenameImageFilename(action.getOldFile().getAbsolutePath(),
                action.getNewFile().getAbsolutePath());
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                thumbnailsPanel.rename(action.getOldFile(), action.getNewFile());
            }
        });
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // nothing to do
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // nothing to do
    }
}
