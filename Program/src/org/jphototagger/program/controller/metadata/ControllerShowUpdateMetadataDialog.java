package org.jphototagger.program.controller.metadata;

import org.jphototagger.program.view.dialogs.UpdateMetadataOfDirectoriesDialog;
import org.jphototagger.lib.componentutil.ComponentUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: Dialog zum Aktualisieren der Metadaten anzeigen.
 *
 * @author Elmar Baumann
 */
public final class ControllerShowUpdateMetadataDialog
        implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent evt) {
        showDialog();
    }

    private void showDialog() {
        ComponentUtil.show(UpdateMetadataOfDirectoriesDialog.INSTANCE);
    }
}
