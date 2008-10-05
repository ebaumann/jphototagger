package de.elmar_baumann.imagemetadataviewer.controller.metadata;

import de.elmar_baumann.imagemetadataviewer.controller.Controller;
import de.elmar_baumann.imagemetadataviewer.data.TextEntry;
import de.elmar_baumann.imagemetadataviewer.resource.Panels;
import de.elmar_baumann.imagemetadataviewer.tasks.XmpUpdaterFromTextEntryArray;
import de.elmar_baumann.imagemetadataviewer.view.panels.AppPanel;
import de.elmar_baumann.imagemetadataviewer.view.panels.MetaDataEditPanelsArray;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JButton;

/**
 * Kontrolliert die Aktion: Metadaten sollen gesichert werden.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class ControllerSaveMetaData extends Controller
    implements ActionListener {

    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private JButton buttonSave = appPanel.getButtonSaveMetadata();
    private MetaDataEditPanelsArray editPanels = appPanel.getEditPanelsArray();
    private XmpUpdaterFromTextEntryArray updater = new XmpUpdaterFromTextEntryArray();

    public ControllerSaveMetaData() {
        listenToActionSource();
    }

    private void listenToActionSource() {
        buttonSave.addActionListener(this);
    }

    @Override
    public void stop() {
        updater.stop();
        super.stop();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStarted()) {
            List<TextEntry> entries = editPanels.getTextEntries();
            List<String> filenames = editPanels.getFilenames();
            int filenameCount = filenames.size();
            if (filenameCount == 1) {
                updater.add(filenames, entries, true, false);
            } else if (filenameCount > 1) {
                updater.add(filenames, entries, false, true);
            }
        }
    }
}
