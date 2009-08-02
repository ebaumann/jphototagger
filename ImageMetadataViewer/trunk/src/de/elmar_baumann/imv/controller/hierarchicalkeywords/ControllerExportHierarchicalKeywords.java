package de.elmar_baumann.imv.controller.hierarchicalkeywords;

import de.elmar_baumann.imv.exporter.HierarchicalKeywordsExporter;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.dialogs.HierarchicalKeywordsExportDialog;
import de.elmar_baumann.imv.view.frames.AppFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Listens to the menu item {@link AppFrame#getMenuItemExportKeywords()} and
 * on action performed exports hierarchical keywords.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-02
 */
public final class ControllerExportHierarchicalKeywords
        implements ActionListener {

    public ControllerExportHierarchicalKeywords() {
        listen();
    }

    private void listen() {
        GUI.INSTANCE.getAppFrame().getMenuItemExportKeywords().addActionListener(
                this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        exportKeywords();
    }

    private void exportKeywords() {
        HierarchicalKeywordsExportDialog dlg =
                new HierarchicalKeywordsExportDialog();
        dlg.setVisible(true);
        if (dlg.isAccepted()) {
            HierarchicalKeywordsExporter exporter = dlg.getExporter();
            assert exporter != null : "Exporter is null!";
            if (exporter != null) {
                exporter.export(dlg.getFile());
            }
        }
    }
}
