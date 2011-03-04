package org.jphototagger.program.controller.thumbnail;

import org.jphototagger.program.helper.InsertImageFilesIntoDatabase;
import org.jphototagger.program.helper.InsertImageFilesIntoDatabase.Insert;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.tasks.UserTasks;
import org.jphototagger.program.view.panels.ProgressBarUpdater;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenuItem;

/**
 * Kontrolliert die Aktion: Metadaten erzeugen für ausgewählte Bilder,
 * ausgelöst von
 * {@link org.jphototagger.program.view.popupmenus.PopupMenuThumbnails}.
 *
 * <em>Nur eine Instanz erzeugen!</em>
 *
 * @author Elmar Baumann
 */
public final class ControllerCreateMetadataOfSelectedThumbnails implements ActionListener {
    private final Map<JMenuItem, Insert[]> databaseUpdateOfMenuItem = new HashMap<JMenuItem, Insert[]>();

    /**
     * Konstruktor. <em>Nur eine Instanz erzeugen!</em>
     */
    public ControllerCreateMetadataOfSelectedThumbnails() {
        initDatabaseUpdateOfMenuItem();
        listen();
    }

    private void initDatabaseUpdateOfMenuItem() {
        PopupMenuThumbnails popupMenu = PopupMenuThumbnails.INSTANCE;

        databaseUpdateOfMenuItem.put(popupMenu.getItemUpdateMetadata(), new Insert[] { Insert.EXIF, Insert.XMP });
        databaseUpdateOfMenuItem.put(popupMenu.getItemUpdateThumbnail(), new Insert[] { Insert.THUMBNAIL });
    }

    private Insert[] getMetadataToInsertIntoDatabase(Object o) {
        if (o instanceof JMenuItem) {
            return databaseUpdateOfMenuItem.get((JMenuItem) o);
        }

        return new Insert[] { Insert.OUT_OF_DATE };
    }

    private void listen() {
        PopupMenuThumbnails popupMenu = PopupMenuThumbnails.INSTANCE;

        popupMenu.getItemUpdateThumbnail().addActionListener(this);
        popupMenu.getItemUpdateMetadata().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (GUI.getThumbnailsPanel().isFileSelected()) {
            updateMetadata(getMetadataToInsertIntoDatabase(evt.getSource()));
        }
    }

    private void updateMetadata(Insert[] what) {
        InsertImageFilesIntoDatabase inserter =
            new InsertImageFilesIntoDatabase(GUI.getThumbnailsPanel().getSelectedFiles(), what);
        String pBarString =
            JptBundle.INSTANCE.getString("ControllerCreateMetadataOfSelectedThumbnails.ProgressBar.String");

        inserter.addProgressListener(new ProgressBarUpdater(inserter, pBarString));
        UserTasks.INSTANCE.add(inserter);
    }
}
