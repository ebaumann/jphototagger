package org.jphototagger.program.controller.misc;

import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.app.update.UpdateDownload;
import org.jphototagger.program.resource.GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ControllerUpdateCheck implements ActionListener {
    public ControllerUpdateCheck() {
        GUI.getAppFrame().getMenuItemCheckForUpdates()
            .addActionListener(this);
    }

    /**
     *
     * @param e event, can be null
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (UpdateDownload.isCheckPending()) {
            MessageDisplayer.error(null,
                                   "ControllerUpdateCheck.Error.CheckDownload");
        } else {
            MessageDisplayer.information(null, "ControllerUpdateCheck.Info");
            UpdateDownload.checkForNewerVersion();
        }
    }
}
