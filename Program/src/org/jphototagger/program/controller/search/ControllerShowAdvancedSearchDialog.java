package org.jphototagger.program.controller.search;

import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.program.view.dialogs.AdvancedSearchDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: Dialog für erweiterte Suche anzeigen.
 *
 * @author Elmar Baumann
 */
public final class ControllerShowAdvancedSearchDialog implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent evt) {
        showDialog();
    }

    void showDialog() {
        ComponentUtil.show(AdvancedSearchDialog.INSTANCE);
    }
}
