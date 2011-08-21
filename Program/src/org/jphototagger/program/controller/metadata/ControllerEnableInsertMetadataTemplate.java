package org.jphototagger.program.controller.metadata;

import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.AppPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.metadata.event.EditMetadataPanelsEditDisabledEvent;
import org.jphototagger.domain.metadata.event.EditMetadataPanelsEditEnabledEvent;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 *
 * @author Elmar Baumann
 */
public final class ControllerEnableInsertMetadataTemplate implements ActionListener {

    public ControllerEnableInsertMetadataTemplate() {
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
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

    @EventSubscriber(eventClass = EditMetadataPanelsEditEnabledEvent.class)
    public void editEnabled(final EditMetadataPanelsEditEnabledEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                setButtonEnabled();
            }
        });
    }

    @EventSubscriber(eventClass = EditMetadataPanelsEditDisabledEvent.class)
    public void editDisabled(final EditMetadataPanelsEditDisabledEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                setButtonEnabled();
            }
        });
    }
}
