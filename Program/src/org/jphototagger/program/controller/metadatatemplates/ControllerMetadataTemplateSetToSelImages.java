package org.jphototagger.program.controller.metadatatemplates;

import org.jphototagger.program.data.MetadataTemplate;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.InputHelperDialog;
import org.jphototagger.program.view.popupmenus.PopupMenuMetadataTemplates;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JButton;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ControllerMetadataTemplateSetToSelImages extends ControllerMetadataTemplate {
    public ControllerMetadataTemplateSetToSelImages() {
        listen();
    }

    private void listen() {
        listenToActionsOf(PopupMenuMetadataTemplates.INSTANCE.getItemSetToSelImages());
        getAddButton().addActionListener(this);
    }

    private JButton getAddButton() {
        return InputHelperDialog.INSTANCE.getPanelMetaDataTemplates().getButtonAddToSelImages();
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if ((evt.getSource() == getAddButton()) && isInputHelperListItemSelected()) {
            action(getTemplateOfInputHelperList());
        } else {
            super.actionPerformed(evt);
        }
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getKeyCode() == KeyEvent.VK_INSERT;
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getSource() == PopupMenuMetadataTemplates.INSTANCE.getItemSetToSelImages();
    }

    @Override
    protected void action(MetadataTemplate template) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }

        if (!imagesSelected()) {
            return;
        }

        GUI.getAppPanel().getEditMetadataPanels().setMetadataTemplate(template);
        focusList();
    }

    private boolean imagesSelected() {
        return GUI.getThumbnailsPanel().isFileSelected();
    }
}
