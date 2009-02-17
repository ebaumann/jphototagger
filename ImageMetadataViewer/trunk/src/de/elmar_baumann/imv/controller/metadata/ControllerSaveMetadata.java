package de.elmar_baumann.imv.controller.metadata;

import de.elmar_baumann.imv.data.TextEntry;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.tasks.XmpUpdaterFromTextEntryArray;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.EditMetadataPanelsArray;
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
public final class ControllerSaveMetadata implements ActionListener {

    private final AppPanel appPanel = Panels.getInstance().getAppPanel();
    private final JButton buttonSave = appPanel.getButtonSaveMetadata();
    private final EditMetadataPanelsArray editPanels = appPanel.getEditPanelsArray();

    public ControllerSaveMetadata() {
        buttonSave.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        saveMetadata();
    }

    private void saveMetadata() {
        saveMetadata(editPanels);
        editPanels.setFocusToLastFocussedComponent();
    }

    public static void saveMetadata(EditMetadataPanelsArray array) {
        XmpUpdaterFromTextEntryArray updater = new XmpUpdaterFromTextEntryArray();
        List<TextEntry> entries = array.getTextEntries();
        List<String> filenames = array.getFilenames();
        int filenameCount = filenames.size();
        if (filenameCount == 1) {
            updater.add(filenames, entries, true, false);
        } else if (filenameCount > 1) {
            updater.add(filenames, entries, false, true);
        }
        array.setDirty(false);
    }
}
