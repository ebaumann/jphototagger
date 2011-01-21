package org.jphototagger.program.controller.misc;

import org.jphototagger.program.view.dialogs.SettingsDialog;
import org.jphototagger.lib.componentutil.ComponentUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: Benutzereinstellungen-Dialog anzeigen.
 *
 * @author Elmar Baumann
 */
public final class ControllerShowUserSettingsDialog implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent evt) {
        showDialog();
    }

    private void showDialog() {
        ComponentUtil.show(SettingsDialog.INSTANCE);
    }
}
