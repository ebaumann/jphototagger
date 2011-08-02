package org.jphototagger.program.controller.misc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.program.app.update.UpdateDownload;
import org.jphototagger.program.resource.GUI;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ControllerUpdateCheck implements ActionListener {
    public ControllerUpdateCheck() {
        GUI.getAppFrame().getMenuItemCheckForUpdates().addActionListener(this);
    }

    /**
     *
     * @param e event, can be null
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (UpdateDownload.isCheckPending()) {
            String message = Bundle.getString(ControllerUpdateCheck.class, "ControllerUpdateCheck.Error.CheckDownload");
            MessageDisplayer.error(null, message);
        } else {
            String message = Bundle.getString(ControllerUpdateCheck.class, "ControllerUpdateCheck.Info");
            MessageDisplayer.information(null, message);
            UpdateDownload.checkForNewerVersion();
        }
    }
}
