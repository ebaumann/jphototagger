package org.jphototagger.program.misc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.app.ui.EditMetadataPanels;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class EmptyAllEditPanelsController implements ActionListener {

    public EmptyAllEditPanelsController() {
        listen();
    }

    private void listen() {
        GUI.getAppPanel().getButtonEmptyMetadata().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                EditMetadataPanels editPanel = GUI.getEditPanel();
                editPanel.emptyAllEditPanels();
            }
        });
    }
}
