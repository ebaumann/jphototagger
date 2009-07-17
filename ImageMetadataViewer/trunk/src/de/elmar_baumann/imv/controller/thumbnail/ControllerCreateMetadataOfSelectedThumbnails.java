package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.helper.InsertImageFilesIntoDatabase;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.ProgressBarUserTasks;
import de.elmar_baumann.imv.tasks.UserTasks;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuThumbnails;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JMenuItem;

/**
 * Kontrolliert die Aktion: Metadaten erzeugen für ausgewählte Bilder,
 * ausgelöst von {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuThumbnails}.
 * 
 * <em>Nur eine Instanz erzeugen!</em>
 * 
 * Der Aufruf von {@link #stop()} beendet alle noch wartenden Threads.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ControllerCreateMetadataOfSelectedThumbnails
        implements ActionListener {

    private final Map<JMenuItem, EnumSet<InsertImageFilesIntoDatabase.Insert>> databaseUpdateOfMenuItem =
            new HashMap<JMenuItem, EnumSet<InsertImageFilesIntoDatabase.Insert>>();
    private final PopupMenuThumbnails popupMenu =
            PopupMenuThumbnails.INSTANCE;
    private final ImageFileThumbnailsPanel thumbnailsPanel = GUI.INSTANCE.
            getAppPanel().getPanelThumbnails();

    /**
     * Konstruktor. <em>Nur eine Instanz erzeugen!</em>
     */
    public ControllerCreateMetadataOfSelectedThumbnails() {
        initDatabaseUpdateOfMenuItem();
        listen();
    }

    private void initDatabaseUpdateOfMenuItem() {

        databaseUpdateOfMenuItem.put(
                popupMenu.getItemUpdateMetadata(), EnumSet.of(
                InsertImageFilesIntoDatabase.Insert.EXIF,
                InsertImageFilesIntoDatabase.Insert.XMP));
        databaseUpdateOfMenuItem.put(
                popupMenu.getItemUpdateThumbnail(), EnumSet.of(
                InsertImageFilesIntoDatabase.Insert.THUMBNAIL));
    }

    private EnumSet<InsertImageFilesIntoDatabase.Insert> getMetadataToInsertIntoDatabase(
            Object o) {
        if (o instanceof JMenuItem) {
            return databaseUpdateOfMenuItem.get((JMenuItem) o);
        }
        return EnumSet.of(InsertImageFilesIntoDatabase.Insert.OUT_OF_DATE);
    }

    private void listen() {
        popupMenu.getItemUpdateThumbnail().addActionListener(this);
        popupMenu.getItemUpdateMetadata().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (thumbnailsPanel.getSelectionCount() > 0) {
            updateMetadata(getMetadataToInsertIntoDatabase(e.getSource()));
        }
    }

    private void updateMetadata(
            EnumSet<InsertImageFilesIntoDatabase.Insert> what) {

        UserTasks.INSTANCE.add(new InsertImageFilesIntoDatabase(
                FileUtil.getAsFilenames(thumbnailsPanel.getSelectedFiles()),
                what,
                ProgressBarUserTasks.INSTANCE));
    }
}
