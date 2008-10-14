package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.database.Database;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Kontrolliert die Aktion: Lösche selektierte Thumbnails,
 * ausgelöst von {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/10
 */
public class ControllerDeleteThumbnailsFromDatabase extends Controller
    implements ActionListener {

    private Database db = Database.getInstance();
    private PopupMenuPanelThumbnails popup = PopupMenuPanelThumbnails.getInstance();
    private ImageFileThumbnailsPanel thumbnailsPanel = Panels.getInstance().getAppPanel().getPanelImageFileThumbnails();

    public ControllerDeleteThumbnailsFromDatabase() {
        listenToActionSource();
    }

    private void listenToActionSource() {
        popup.addActionListenerDeleteThumbnail(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStarted()) {
            deleteSelectedThumbnails();
        }
    }

    private void deleteSelectedThumbnails() {
        if (askDelete()) {
            List<String> files = popup.getThumbnailsPanel().getSelectedFilenames();
            int countFiles = files.size();
            int countDeleted = db.deleteImageFiles(files);
            if (countDeleted != countFiles) {
                messageErrorDeleteImageFiles(countFiles, countDeleted);
            }
            repaint(files);
            popup.getThumbnailsPanel().repaint();
        }
    }

    private boolean askDelete() {
        MessageFormat msg = new MessageFormat(Bundle.getString("ControllerDeleteThumbnailsFromDatabase.ConfirmMessage.DeleteSelectedFiles"));
        Object[] params = {popup.getThumbnailsPanel().getSelectionCount()};
        return JOptionPane.showConfirmDialog(
            null,
            msg.format(params),
            Bundle.getString("ControllerDeleteThumbnailsFromDatabase.ConfirmMessage.DeleteSelectedFiles.Title"),
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            AppSettings.getMediumAppIcon()) == JOptionPane.YES_OPTION;
    }

    private void messageErrorDeleteImageFiles(int countFiles, int countDeleted) {
        MessageFormat msg = new MessageFormat(Bundle.getString("ControllerDeleteThumbnailsFromDatabase.ErrorMessage.DeleteSelectedFiles"));
        Object[] params = {countFiles, countDeleted};
        JOptionPane.showMessageDialog(
            null,
            msg.format(params),
            Bundle.getString("ControllerDeleteThumbnailsFromDatabase.ErrorMessage.DeleteSelectedFiles.Title"),
            JOptionPane.ERROR_MESSAGE,
            AppSettings.getMediumAppIcon());
    }

    private void repaint(List<String> filenames) {
        List<String> deleted = new ArrayList<String>(filenames.size());
        for (String filename : filenames) {
            if (!db.existsFilename(filename)) {
                deleted.add(filename);
            }
        }
        thumbnailsPanel.remove(deleted);
    }
}
