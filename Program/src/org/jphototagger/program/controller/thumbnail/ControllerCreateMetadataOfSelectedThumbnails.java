package org.jphototagger.program.controller.thumbnail;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenuItem;

import org.jphototagger.domain.repository.InsertIntoRepository;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.helper.InsertImageFilesIntoDatabase;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.tasks.UserTasks;
import org.jphototagger.program.view.panels.ProgressBarUpdater;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;

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
    private final Map<JMenuItem, InsertIntoRepository[]> databaseUpdateOfMenuItem = new HashMap<JMenuItem, InsertIntoRepository[]>();

    /**
     * Konstruktor. <em>Nur eine Instanz erzeugen!</em>
     */
    public ControllerCreateMetadataOfSelectedThumbnails() {
        initDatabaseUpdateOfMenuItem();
        listen();
    }

    private void initDatabaseUpdateOfMenuItem() {
        PopupMenuThumbnails popupMenu = PopupMenuThumbnails.INSTANCE;

        databaseUpdateOfMenuItem.put(popupMenu.getItemUpdateMetadata(), new InsertIntoRepository[] { InsertIntoRepository.EXIF, InsertIntoRepository.XMP });
        databaseUpdateOfMenuItem.put(popupMenu.getItemUpdateThumbnail(), new InsertIntoRepository[] { InsertIntoRepository.THUMBNAIL });
    }

    private InsertIntoRepository[] getMetadataToInsertIntoDatabase(Object o) {
        if (o instanceof JMenuItem) {
            return databaseUpdateOfMenuItem.get((JMenuItem) o);
        }

        return new InsertIntoRepository[] { InsertIntoRepository.OUT_OF_DATE };
    }

    private void listen() {
        PopupMenuThumbnails popupMenu = PopupMenuThumbnails.INSTANCE;

        popupMenu.getItemUpdateThumbnail().addActionListener(this);
        popupMenu.getItemUpdateMetadata().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (GUI.getThumbnailsPanel().isAFileSelected()) {
            updateMetadata(getMetadataToInsertIntoDatabase(evt.getSource()));
        }
    }

    private void updateMetadata(InsertIntoRepository[] what) {
        InsertImageFilesIntoDatabase inserter = new InsertImageFilesIntoDatabase(GUI.getThumbnailsPanel().getSelectedFiles(), what);
        String pBarString = Bundle.getString(ControllerCreateMetadataOfSelectedThumbnails.class, "ControllerCreateMetadataOfSelectedThumbnails.ProgressBar.String");

        inserter.addProgressListener(new ProgressBarUpdater(inserter, pBarString));
        UserTasks.INSTANCE.add(inserter);
    }
}
