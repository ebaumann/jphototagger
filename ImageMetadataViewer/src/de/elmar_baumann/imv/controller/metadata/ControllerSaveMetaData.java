package de.elmar_baumann.imv.controller.metadata;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.data.TextEntry;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.tasks.XmpUpdaterFromTextEntryArray;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.MetadataEditPanelsArray;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
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
    private MetadataEditPanelsArray editPanels = appPanel.getEditPanelsArray();
    private XmpUpdaterFromTextEntryArray updater = new XmpUpdaterFromTextEntryArray();

    public ControllerSaveMetaData() {
        listenToActionSource();
    }

    private void listenToActionSource() {
        buttonSave.addActionListener(this);
    }

    @Override
    public void setControl(boolean control) {
        super.setControl(control);
        if (!control) {
            updater.stop();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isControl()) {
            List<TextEntry> entries = editPanels.getTextEntries();
            List<String> filenames = editPanels.getFilenames();
            int filenameCount = filenames.size();
            if (filenameCount == 1) {
                updater.add(filenames, entries, true, false);
            } else if (filenameCount > 1) {
                updater.add(filenames, entries, false, true);
            }
            editPanels.setFocusToLastFocussedComponent();
        }
    }
}
