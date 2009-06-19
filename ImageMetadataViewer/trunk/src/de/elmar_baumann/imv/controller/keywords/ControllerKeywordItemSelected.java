package de.elmar_baumann.imv.controller.keywords;

import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.event.listener.RefreshListener;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.InfoSetThumbnails;
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
public final class ControllerKeywordItemSelected implements
        ListSelectionListener, RefreshListener {

    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JList listKeywords = appPanel.getListKeywords();
    private final ImageFileThumbnailsPanel thumbnailsPanel = appPanel.
            getPanelThumbnails();
    private final EditMetadataPanelsArray editPanels = appPanel.
            getEditPanelsArray();

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
            update();
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (listKeywords.getSelectedIndex() >= 0) {
            update();
        }
    }

    private void update() {
        Thread update = new Thread(new Runnable() {

            @Override
            public void run() {
                InfoSetThumbnails info = new InfoSetThumbnails();
                setFilesToThumbnailsPanel();
                setMetadataEditable();
                info.hide();
            }
        });
        update.setName("Keyword selected" + " @ " + getClass().getName()); // NOI18N
        update.start();
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
