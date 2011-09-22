package org.jphototagger.program.controller.thumbnail;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenuItem;

import org.jphototagger.domain.repository.InsertIntoRepository;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.helper.InsertImageFilesIntoRepository;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.tasks.UserTasks;
import org.jphototagger.program.view.panels.ProgressBarUpdater;
import org.jphototagger.program.view.popupmenus.ThumbnailsPopupMenu;

/**
 * Kontrolliert die Aktion: Metadaten erzeugen für ausgewählte Bilder,
 * ausgelöst von
 * {@code org.jphototagger.program.view.popupmenus.ThumbnailsPopupMenu}.
 *
 * <em>Nur eine Instanz erzeugen!</em>
 *
 * @author Elmar Baumann
 */
public final class CreateMetadataOfSelectedThumbnailsController implements ActionListener {

    private final Map<JMenuItem, InsertIntoRepository[]> insertIntoRepositoryOfMenuItem = new HashMap<JMenuItem, InsertIntoRepository[]>();

    /**
     * Konstruktor. <em>Nur eine Instanz erzeugen!</em>
     */
    public CreateMetadataOfSelectedThumbnailsController() {
        initInsertIntoRepositoryOfMenuItem();
        listen();
    }

    private void initInsertIntoRepositoryOfMenuItem() {
        ThumbnailsPopupMenu popupMenu = ThumbnailsPopupMenu.INSTANCE;

        insertIntoRepositoryOfMenuItem.put(popupMenu.getItemUpdateMetadata(), new InsertIntoRepository[]{InsertIntoRepository.EXIF, InsertIntoRepository.XMP});
        insertIntoRepositoryOfMenuItem.put(popupMenu.getItemUpdateThumbnail(), new InsertIntoRepository[]{InsertIntoRepository.THUMBNAIL});
    }

    private InsertIntoRepository[] getMetadataToInsertIntoRepository(Object o) {
        if (o instanceof JMenuItem) {
            return insertIntoRepositoryOfMenuItem.get((JMenuItem) o);
        }

        return new InsertIntoRepository[]{InsertIntoRepository.OUT_OF_DATE};
    }

    private void listen() {
        ThumbnailsPopupMenu popupMenu = ThumbnailsPopupMenu.INSTANCE;

        popupMenu.getItemUpdateThumbnail().addActionListener(this);
        popupMenu.getItemUpdateMetadata().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (GUI.getThumbnailsPanel().isAFileSelected()) {
            updateMetadata(getMetadataToInsertIntoRepository(evt.getSource()));
        }
    }

    private void updateMetadata(InsertIntoRepository[] what) {
        InsertImageFilesIntoRepository inserter = new InsertImageFilesIntoRepository(GUI.getThumbnailsPanel().getSelectedFiles(), what);
        String pBarString = Bundle.getString(CreateMetadataOfSelectedThumbnailsController.class, "CreateMetadataOfSelectedThumbnailsController.ProgressBar.String");

        inserter.addProgressListener(new ProgressBarUpdater(inserter, pBarString));
        UserTasks.INSTANCE.add(inserter);
    }
}
