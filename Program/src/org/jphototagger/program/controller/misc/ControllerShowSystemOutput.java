package org.jphototagger.program.controller.misc;

import org.jphototagger.program.app.AppInit;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.lib.dialog.SystemOutputDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ControllerShowSystemOutput implements ActionListener {
    public ControllerShowSystemOutput() {
        listen();
    }

    private void listen() {
        if (AppInit.INSTANCE.getCommandLineOptions().isCaptureOutput()) {
            GUI.getAppFrame().getMenuItemOutputWindow()
                .addActionListener(this);
        } else {
            GUI.getAppFrame().getMenuItemOutputWindow().setEnabled(
                false);
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        showDialog();
    }

    private void showDialog() {
        ComponentUtil.show(SystemOutputDialog.INSTANCE);
    }
}
