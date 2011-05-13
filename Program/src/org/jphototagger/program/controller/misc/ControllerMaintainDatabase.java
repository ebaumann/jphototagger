package org.jphototagger.program.controller.misc;

import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.program.view.dialogs.DatabaseMaintainanceDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: Datenbank warten.
 *
 * @author Elmar Baumann
 */
public final class ControllerMaintainDatabase implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent evt) {
        maintainDatabase();
    }

    private void maintainDatabase() {
        ComponentUtil.show(DatabaseMaintainanceDialog.INSTANCE);
    }
}
