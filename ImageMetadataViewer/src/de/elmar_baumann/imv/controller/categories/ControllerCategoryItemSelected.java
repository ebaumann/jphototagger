package de.elmar_baumann.imv.controller.categories;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.event.RefreshListener;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.lib.io.FileUtil;
import java.util.Set;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Kontrolliert die Aktion: Ein Kategorie-Item wurde selektiert.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class ControllerCategoryItemSelected extends Controller
    implements ListSelectionListener, RefreshListener {

    private DatabaseImageFiles db = DatabaseImageFiles.getInstance();
    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private JList listCategories = appPanel.getListCategories();
    private ImageFileThumbnailsPanel thumbnailsPanel = appPanel.getPanelThumbnails();

    public ControllerCategoryItemSelected() {
        listenToActionSources();
    }

    private void listenToActionSources() {
        listCategories.addListSelectionListener(this);
        thumbnailsPanel.addRefreshListener(this, Content.Category);
    }

    @Override
    public void refresh() {
        if (isControl()) {
            setFilesToThumbnailsPanel();
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (isControl()) {
            setFilesToThumbnailsPanel();
        }
    }

    private void setFilesToThumbnailsPanel() {
        String category = (String) listCategories.getSelectedValue();
        Set<String> filenames = db.getFilenamesOfCategory(category);

        thumbnailsPanel.setFiles(FileUtil.getAsFiles(filenames), Content.Category);
    }
}
