package de.elmar_baumann.imv.controller.keywords;

import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
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
public class ControllerKeywordItemSelected extends Controller
    implements ListSelectionListener {

    private DatabaseImageFiles db = DatabaseImageFiles.getInstance();
    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private JList listKeywords = appPanel.getListKeywords();
    private ImageFileThumbnailsPanel thumbnailsPanel = appPanel.getPanelThumbnails();

    public ControllerKeywordItemSelected() {
        listKeywords.addListSelectionListener(this);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (isControl()) {
            setFilesToThumbnailsPanel();
        }
    }

    private void setFilesToThumbnailsPanel() {
        String keyword = (String) listKeywords.getSelectedValue();
        Set<String> filenames = db.getFilenamesOfDcSubject(keyword);

        thumbnailsPanel.setFiles(FileUtil.getAsFiles(filenames), Content.Keyword);
    }
}
