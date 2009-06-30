package de.elmar_baumann.imv.controller.filesystem;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.tasks.FileSystemDeleteImageFiles;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.types.DeleteOption;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 * Deletes files from the filesystem.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/12
 */
public final class ControllerDeleteFiles implements ActionListener {

    private final ImageFileThumbnailsPanel thumbnailsPanel = GUI.INSTANCE.
            getAppPanel().getPanelThumbnails();
    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
    private final PopupMenuPanelThumbnails popupMenu =
            PopupMenuPanelThumbnails.INSTANCE;
    private static final List<Content> deleteableContent =
            new ArrayList<Content>();


    static {
        deleteableContent.add(Content.CATEGORY);
        deleteableContent.add(Content.DIRECTORY);
        deleteableContent.add(Content.FAST_SEARCH);
        deleteableContent.add(Content.FAVORITE_DIRECTORY);
        deleteableContent.add(Content.KEYWORD);
        deleteableContent.add(Content.MISC_METADATA);
        deleteableContent.add(Content.SAFED_SEARCH);
        deleteableContent.add(Content.TIMELINE);
    }

    public ControllerDeleteFiles() {
        listen();
    }

    private void listen() {
        popupMenu.getItemFileSystemDeleteFiles().addActionListener(this);
        GUI.INSTANCE.getAppFrame().getMenuItemDelete().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isDeletable(thumbnailsPanel.getContent())) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    deleteSelectedFiles();
                    thumbnailsPanel.repaint();
                }
            });
        }
    }

    private boolean isDeletable(Content content) {
        return deleteableContent.contains(content);
    }

    private void deleteSelectedFiles() {
        int selCount = thumbnailsPanel.getSelectionCount();
        List<File> deletedImageFiles = FileSystemDeleteImageFiles.delete(
                thumbnailsPanel.getSelectedFiles(), EnumSet.of(
                DeleteOption.CONFIRM_DELETE,
                DeleteOption.MESSAGES_ON_FAILURES));
        int delCount = deletedImageFiles.size();

        if (delCount > 0) {
            db.deleteImageFiles(FileUtil.getAsFilenames(deletedImageFiles));
            thumbnailsPanel.remove(deletedImageFiles);
        }
        if (delCount != selCount) {
            AppLog.logWarning(ControllerDeleteFiles.class, Bundle.getString(
                    "ControllerDeleteFiles.ErrorMessage.NotAllImagesDeleted",
                    selCount, delCount));
        }
    }
}
