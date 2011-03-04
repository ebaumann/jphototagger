package org.jphototagger.program.controller.metadatatemplates;

import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.data.MetadataTemplate;
import org.jphototagger.program.database.DatabaseMetadataTemplates;
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
public final class ControllerMetadataTemplateDelete extends ControllerMetadataTemplate {
    public ControllerMetadataTemplateDelete() {
        listen();
    }

    private void listen() {
        listenToActionsOf(PopupMenuMetadataTemplates.INSTANCE.getItemDelete());
        getDeleteButton().addActionListener(this);
    }

    private JButton getDeleteButton() {
        return InputHelperDialog.INSTANCE.getPanelMetaDataTemplates().getButtonDelete();
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if ((evt.getSource() == getDeleteButton()) && isInputHelperListItemSelected()) {
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

        return evt.getKeyCode() == KeyEvent.VK_DELETE;
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getSource() == PopupMenuMetadataTemplates.INSTANCE.getItemDelete();
    }

    @Override
    protected void action(MetadataTemplate template) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }

        String name = template.getName();

        if (MessageDisplayer.confirmYesNo(InputHelperDialog.INSTANCE, "ControllerMetadataTemplateDelete.Confirm",
                                          name)) {
            if (!DatabaseMetadataTemplates.INSTANCE.delete(name)) {
                MessageDisplayer.error(InputHelperDialog.INSTANCE, "ControllerMetadataTemplateDelete.Error", name);
            }
        }

        focusList();
    }
}
