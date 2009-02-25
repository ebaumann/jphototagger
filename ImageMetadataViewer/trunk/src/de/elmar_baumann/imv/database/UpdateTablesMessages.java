package de.elmar_baumann.imv.database;

import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.dialog.ProgressDialog;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/29
 */
class UpdateTablesMessages {

    private ProgressDialog dialog;
    static final UpdateTablesMessages INSTANCE = new UpdateTablesMessages();

    ProgressDialog getProgressDialog() {
        return dialog;
    }

    void message(String text) {
        dialog.setInfoText(text);
        if (dialog.isVisible()) {
            dialog.toFront();
        } else {
            dialog.setVisible(true);
        }
    }

    private UpdateTablesMessages() {
        initDialog();
    }

    private void initDialog() {
        dialog = new ProgressDialog(null);
        dialog.setEnabledClose(false);
        dialog.setEnabledStop(false);
        dialog.setTitle(Bundle.getString("UpdateTables.InformationMessage.Title"));
        dialog.setIndeterminate(true);
    }
}
