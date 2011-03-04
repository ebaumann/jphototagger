package org.jphototagger.program.controller.keywords.tree;

import org.jphototagger.program.exporter.Exporter;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.KeywordExportDialog;
import org.jphototagger.program.view.frames.AppFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Listens to the menu item {@link AppFrame#getMenuItemExportKeywords()} and
 * on action performed exports keywords.
 *
 * @author Elmar Baumann
 */
public final class ControllerExportKeywords implements ActionListener {
    public ControllerExportKeywords() {
        listen();
    }

    private void listen() {
        GUI.getAppFrame().getMenuItemExportKeywords().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        exportKeywords();
    }

    private void exportKeywords() {
        KeywordExportDialog dlg = new KeywordExportDialog();

        dlg.setVisible(true);

        if (dlg.isAccepted()) {
            Exporter exporter = dlg.getExporter();

            assert exporter != null : "Exporter is null!";

            if (exporter != null) {
                exporter.exportFile(dlg.getFile());
            }
        }
    }
}
