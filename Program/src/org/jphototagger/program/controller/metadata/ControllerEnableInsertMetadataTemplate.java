package org.jphototagger.program.controller.metadata;

import org.jphototagger.program.event.listener.EditMetadataPanelsListener;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.AppPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 *
 * @author Elmar Baumann
 */
public final class ControllerEnableInsertMetadataTemplate implements EditMetadataPanelsListener, ActionListener {
    public ControllerEnableInsertMetadataTemplate() {
        listen();
    }

    private void listen() {
        GUI.getEditPanel().addEditMetadataPanelsListener(this);
        GUI.getAppPanel().getComboBoxMetadataTemplates().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        setButtonEnabled();
    }

    private void setButtonEnabled() {
        boolean editable = GUI.getEditPanel().isEditable();
        AppPanel appPanel = GUI.getAppPanel();
        boolean selected = appPanel.getComboBoxMetadataTemplates().getSelectedIndex() >= 0;
        JButton button = appPanel.getButtonMetadataTemplateInsert();

        button.setEnabled(editable && (selected));
    }

    @Override
    public void editEnabled() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                setButtonEnabled();
            }
        });
    }

    @Override
    public void editDisabled() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                setButtonEnabled();
            }
        });
    }
}
