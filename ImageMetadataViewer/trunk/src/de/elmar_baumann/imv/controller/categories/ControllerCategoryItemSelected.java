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
public final class ControllerCategoryItemSelected extends Controller
    implements ListSelectionListener, RefreshListener {

    private final DatabaseImageFiles db = DatabaseImageFiles.getInstance();
    private final AppPanel appPanel = Panels.getInstance().getAppPanel();
    private final JList listCategories = appPanel.getListCategories();
    private final ImageFileThumbnailsPanel thumbnailsPanel = appPanel.getPanelThumbnails();

    public ControllerCategoryItemSelected() {
        listenToActionSources();
    }

    private void listenToActionSources() {
        listCategories.addListSelectionListener(this);
        thumbnailsPanel.addRefreshListener(this, Content.CATEGORY);
    }

    @Override
    public void refresh() {
        if (isControl() && listCategories.getSelectedIndex() >= 0) {
            setFilesToThumbnailsPanel();
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (isControl() && listCategories.getSelectedIndex() >= 0) {
            setFilesToThumbnailsPanel();
        }
    }

    private void setFilesToThumbnailsPanel() {
        String category = (String) listCategories.getSelectedValue();
        Set<String> filenames = db.getFilenamesOfCategory(category);

        thumbnailsPanel.setFiles(FileUtil.getAsFiles(filenames), Content.CATEGORY);
    }
}
