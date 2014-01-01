package org.jphototagger.program.module.thumbnails;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JMenuItem;
import org.jphototagger.api.concurrent.SerialTaskExecutor;
import org.jphototagger.domain.repository.SaveOrUpdate;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.ProgressBarUpdater;
import org.jphototagger.program.misc.SaveToOrUpdateFilesInRepositoryImpl;
import org.jphototagger.program.resource.GUI;
import org.openide.util.Lookup;

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

    private final Map<JMenuItem, SaveOrUpdate[]> insertIntoRepositoryOfMenuItem = new HashMap<>();
    private final SerialTaskExecutor executor = Lookup.getDefault().lookup(SerialTaskExecutor.class);

    /**
     * Konstruktor. <em>Nur eine Instanz erzeugen!</em>
     */
    public CreateMetadataOfSelectedThumbnailsController() {
        initInsertIntoRepositoryOfMenuItem();
        listen();
    }

    private void initInsertIntoRepositoryOfMenuItem() {
        ThumbnailsPopupMenu popupMenu = ThumbnailsPopupMenu.INSTANCE;

        insertIntoRepositoryOfMenuItem.put(popupMenu.getItemUpdateMetadata(), new SaveOrUpdate[]{SaveOrUpdate.EXIF, SaveOrUpdate.XMP});
        insertIntoRepositoryOfMenuItem.put(popupMenu.getItemUpdateThumbnail(), new SaveOrUpdate[]{SaveOrUpdate.THUMBNAIL});
    }

    private SaveOrUpdate[] getMetadataToInsertIntoRepository(Object o) {
        if (o instanceof JMenuItem) {
            return insertIntoRepositoryOfMenuItem.get((JMenuItem) o);
        }

        return new SaveOrUpdate[]{SaveOrUpdate.OUT_OF_DATE};
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

    private void updateMetadata(SaveOrUpdate[] what) {
        SaveToOrUpdateFilesInRepositoryImpl inserter = new SaveToOrUpdateFilesInRepositoryImpl(GUI.getThumbnailsPanel().getSelectedFiles(), what);
        String pBarString = Bundle.getString(CreateMetadataOfSelectedThumbnailsController.class, "CreateMetadataOfSelectedThumbnailsController.ProgressBar.String");

        inserter.addProgressListener(new ProgressBarUpdater(inserter, pBarString));
        executor.addTask(inserter);
    }
}
