package de.elmar_baumann.imv.controller.keywords;

import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.event.listener.RefreshListener;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.EditMetadataPanelsArray;
import de.elmar_baumann.imv.view.panels.ThumbnailsPanel;
import de.elmar_baumann.lib.io.FileUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-25
 */
public final class ControllerKeywordItemSelected implements
        ListSelectionListener, RefreshListener {

    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JList listKeywords = appPanel.getListKeywords();
    private final ThumbnailsPanel thumbnailsPanel =
            appPanel.getPanelThumbnails();
    private final EditMetadataPanelsArray editPanels =
            appPanel.getEditPanelsArray();

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
        if (!e.getValueIsAdjusting() && listKeywords.getSelectedIndex() >= 0) {
            update();
        }
    }

    private void update() {
        SwingUtilities.invokeLater(new ShowThumbnails());
    }

    private class ShowThumbnails implements Runnable {

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
            List<String> keywords = getSelectedKeywords();
            // Faster when using 2 different DB queries if only 1 keyword is
            // selected
            if (keywords.size() == 1) {
                return db.getFilenamesOfDcSubject(keywords.get(0));
            } else if (keywords.size() > 1) {
                return db.getFilenamesOfAllDcSubjects(keywords);
            }
            return null;
        }

        private List<String> getSelectedKeywords() {
            Object[] selValues = listKeywords.getSelectedValues();
            List<String> keywords = new ArrayList<String>();
            for (Object selValue : selValues) {
                if (selValue instanceof String) {
                    keywords.add((String) selValue);
                }
            }
            assert keywords.size() == selValues.length :
                    "Not all keywords are strings: " + keywords;
            return keywords;
        }

        private void setMetadataEditable() {
            if (thumbnailsPanel.getSelectionCount() <= 0) {
                editPanels.setEditable(false);
            }
        }
    }
}
