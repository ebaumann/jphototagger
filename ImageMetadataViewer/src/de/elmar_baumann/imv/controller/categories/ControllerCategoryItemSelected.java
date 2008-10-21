package de.elmar_baumann.imv.controller.categories;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.Content;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.lib.io.FileUtil;
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
    implements ListSelectionListener {

    private DatabaseImageFiles db = DatabaseImageFiles.getInstance();
    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private JList listCategories = appPanel.getListCategories();
    private ImageFileThumbnailsPanel thumbnailsPanel = appPanel.getPanelThumbnails();

    public ControllerCategoryItemSelected() {
        listCategories.addListSelectionListener(this);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (isControl()) {
            setFilenamesToThumbnailPanel();
        }
    }

    private void setFilenamesToThumbnailPanel() {
        thumbnailsPanel.setFiles(FileUtil.getAsFiles(
            db.getFilenamesOfCategory((String) listCategories.getSelectedValue())),
            Content.Category);
    }
}
