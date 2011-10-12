package org.jphototagger.program.module.search;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.jphototagger.lib.swing.util.ComponentUtil;

/**
 * Kontrolliert die Aktion: Dialog für erweiterte Suche anzeigen.
 *
 * @author Elmar Baumann
 */
public final class ShowAdvancedSearchDialogController implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent evt) {
        showDialog();
    }

    void showDialog() {
        ComponentUtil.show(AdvancedSearchDialog.INSTANCE);
    }
}
