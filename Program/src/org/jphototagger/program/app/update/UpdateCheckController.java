package org.jphototagger.program.app.update;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.resource.GUI;

/**
 * @author Elmar Baumann
 */
public final class UpdateCheckController implements ActionListener {

    public UpdateCheckController() {
        listen();
    }

    private void listen() {
        GUI.getAppFrame().getMenuItemCheckForUpdates().addActionListener(this);
    }

    /**
     *
     * @param e event, can be null
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (UpdateDownload.isCheckPending()) {
            String message = Bundle.getString(UpdateCheckController.class, "UpdateCheckController.Error.CheckDownload");
            MessageDisplayer.error(null, message);
        } else {
            String message = Bundle.getString(UpdateCheckController.class, "UpdateCheckController.Info");
            MessageDisplayer.information(null, message);
            UpdateDownload.checkForNewerVersion();
        }
    }
}
