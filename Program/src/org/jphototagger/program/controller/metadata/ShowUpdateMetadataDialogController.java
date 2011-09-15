package org.jphototagger.program.controller.metadata;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.program.view.dialogs.UpdateMetadataOfDirectoriesDialog;

/**
 * Kontrolliert die Aktion: Dialog zum Aktualisieren der Metadaten anzeigen.
 *
 * @author Elmar Baumann
 */
public final class ShowUpdateMetadataDialogController implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent evt) {
        showDialog();
    }

    private void showDialog() {
        ComponentUtil.show(UpdateMetadataOfDirectoriesDialog.INSTANCE);
    }
}
