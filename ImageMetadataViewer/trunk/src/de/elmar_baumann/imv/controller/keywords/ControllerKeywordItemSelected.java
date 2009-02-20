package de.elmar_baumann.imv.controller.keywords;

import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.event.RefreshListener;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.EditMetadataPanelsArray;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.lib.io.FileUtil;
import java.util.Set;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/25
 */
public final class ControllerKeywordItemSelected implements ListSelectionListener, RefreshListener {

    private final DatabaseImageFiles db = DatabaseImageFiles.getInstance();
    private final AppPanel appPanel = Panels.getInstance().getAppPanel();
    private final JList listKeywords = appPanel.getListKeywords();
    private final ImageFileThumbnailsPanel thumbnailsPanel = appPanel.getPanelThumbnails();
    private final EditMetadataPanelsArray editPanels = appPanel.getEditPanelsArray();

    public ControllerKeywordItemSelected() {
        listen();
    }

    private void listen() {
        listKeywords.addListSelectionListener(this);
        thumbnailsPanel.addRefreshListener(this, Content.KEYWORD);
    }

    @Override
    public void refresh() {
        if (listKeywords.getSelectedIndex() >= 0) {
            setFilesToThumbnailsPanel();
            setMetadataEditable();
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (listKeywords.getSelectedIndex() >= 0) {
            setFilesToThumbnailsPanel();
            setMetadataEditable();
        }
    }

    private void setFilesToThumbnailsPanel() {
        String keyword = (String) listKeywords.getSelectedValue();
        Set<String> filenames = db.getFilenamesOfDcSubject(keyword);

        thumbnailsPanel.setFiles(FileUtil.getAsFiles(filenames), Content.KEYWORD);
    }

    private void setMetadataEditable() {
        if (thumbnailsPanel.getSelectionCount() <= 0) {
            editPanels.setEditable(false);
        }
    }
}
