package de.elmar_baumann.imv.controller.imagecollection;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.controller.filesystem.ControllerDeleteFiles;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.tasks.ImageCollectionDatabaseUtils;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuThumbnails;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.List;
import javax.swing.JList;
import javax.swing.SwingUtilities;

/**
 * Listens to key events of {@link ImageFileThumbnailsPanel} and when the
 * <code>DEL</code> key was pressed deletes the selected files from the
 * file system if the panel's content <em>is</em>
 * {@link Content#IMAGE_COLLECTION}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/00/10
 * @see     ControllerDeleteFiles
 */
public final class ControllerDeleteFromImageCollection
        implements ActionListener, KeyListener {

    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JList list = appPanel.getListImageCollections();
    private final PopupMenuThumbnails popupMenu =
            PopupMenuThumbnails.INSTANCE;
    private final ImageFileThumbnailsPanel thumbnailsPanel =
            GUI.INSTANCE.getAppPanel().getPanelThumbnails();

    public ControllerDeleteFromImageCollection() {
        listen();
    }

    private void listen() {
        popupMenu.getItemDeleteFromImageCollection().addActionListener(this);
        thumbnailsPanel.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            delete();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        delete();
    }

    private void delete() {
        if (thumbnailsPanel.getContent().equals(Content.IMAGE_COLLECTION) &&
                thumbnailsPanel.getSelectionCount() > 0) {
            deleteSelectedFilesFromImageCollection();
        }
    }

    private void deleteSelectedFilesFromImageCollection() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                Object selectedValue = list.getSelectedValue();
                if (selectedValue != null) {
                    List<File> selectedFiles =
                            thumbnailsPanel.getSelectedFiles();
                    if (ImageCollectionDatabaseUtils.deleteImagesFromCollection(
                            selectedValue.toString(), FileUtil.getAsFilenames(
                            selectedFiles))) {
                        thumbnailsPanel.remove(selectedFiles);
                    }
                } else {
                    AppLog.logWarning(ControllerDeleteFromImageCollection.class,
                            Bundle.getString(
                            "ControllerDeleteFromImageCollection.Error.SelectedImageCollectionIsNull")); // NOI18N
                }
            }
        });
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // ignore
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // ignore
    }
}
