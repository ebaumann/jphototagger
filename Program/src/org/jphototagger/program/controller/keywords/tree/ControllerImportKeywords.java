package org.jphototagger.program.controller.keywords.tree;

import org.jphototagger.program.importer.KeywordsImporter;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.KeywordImportDialog;
import org.jphototagger.program.view.frames.AppFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Listens to the menu item {@link AppFrame#getMenuItemImportKeywords()} and
 * on action performed imports keywords.
 *
 * @author Elmar Baumann
 */
public final class ControllerImportKeywords implements ActionListener {
    public ControllerImportKeywords() {
        listen();
    }

    private void listen() {
        GUI.getAppFrame().getMenuItemImportKeywords().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        importKeywords();
    }

    private void importKeywords() {
        KeywordImportDialog dlg = new KeywordImportDialog();

        dlg.setVisible(true);

        if (dlg.isAccepted()) {
            KeywordsImporter importer = dlg.getImporter();

            assert importer != null : "Importer is null!";

            if (importer != null) {
                importer.importFile(dlg.getFile());
            }
        }
    }
}
