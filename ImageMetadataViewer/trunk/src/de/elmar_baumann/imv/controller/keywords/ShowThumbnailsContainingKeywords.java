package de.elmar_baumann.imv.controller.keywords;

import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.view.panels.EditMetadataPanelsArray;
import de.elmar_baumann.imv.view.panels.ThumbnailsPanel;
import de.elmar_baumann.lib.io.FileUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Displays in the {@link ThumbnailsPanel} thumbnails with specific keywords.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-09-02
 */
public final class ShowThumbnailsContainingKeywords implements Runnable {

    private final ThumbnailsPanel thumbnailsPanel =
            GUI.INSTANCE.getAppPanel().getPanelThumbnails();
    private final EditMetadataPanelsArray editPanels =
            GUI.INSTANCE.getAppPanel().getEditPanelsArray();
    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
    private final List<String> keywords;

    /**
     * Creates a new instance of this class.
     *
     * @param keywords one of that keywords a image must have to be displayed
     */
    public ShowThumbnailsContainingKeywords(List<String> keywords) {
        this.keywords = new ArrayList<String>(keywords);
    }

    @Override
    public void run() {
        setFilesToThumbnailsPanel();
        setMetadataEditable();
    }

    private void setFilesToThumbnailsPanel() {
        Set<String> filenames = getFilenamesOfSelectedKeywords();
        if (filenames != null) {
            thumbnailsPanel.setFiles(
                    FileUtil.getAsFiles(filenames), Content.KEYWORD);
        }
    }

    private Set<String> getFilenamesOfSelectedKeywords() {
        // Faster when using 2 different DB queries if only 1 keyword is
        // selected
        if (keywords.size() == 1) {
            return db.getFilenamesOfDcSubject(keywords.get(0));
        } else if (keywords.size() > 1) {
            return db.getFilenamesOfDcSubjects(keywords);
        }
        return null;
    }

    private void setMetadataEditable() {
        if (thumbnailsPanel.getSelectionCount() <= 0) {
            editPanels.setEditable(false);
        }
    }
}
