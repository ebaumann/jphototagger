package org.jphototagger.program.controller.misc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.program.view.dialogs.SettingsDialog;

/**
 * Kontrolliert die Aktion: Benutzereinstellungen-Dialog anzeigen.
 *
 * @author Elmar Baumann
 */
public final class ShowUserSettingsDialogController implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent evt) {
        showDialog();
    }

    private void showDialog() {
        ComponentUtil.show(SettingsDialog.INSTANCE);
    }
}
