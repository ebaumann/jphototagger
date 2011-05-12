package org.jphototagger.program.controller.metadata;

import org.jphototagger.program.resource.GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ControllerEmptyMetadata implements ActionListener {
    public ControllerEmptyMetadata() {
        listen();
    }

    private void listen() {
        GUI.getAppPanel().getButtonEmptyMetadata().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        EventQueueUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                GUI.getEditPanel().emptyPanels(true);
            }
        });
    }
}
