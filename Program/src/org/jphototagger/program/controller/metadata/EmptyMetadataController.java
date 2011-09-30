package org.jphototagger.program.controller.metadata;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.EditMetadataPanels;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class EmptyMetadataController implements ActionListener {

    public EmptyMetadataController() {
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
