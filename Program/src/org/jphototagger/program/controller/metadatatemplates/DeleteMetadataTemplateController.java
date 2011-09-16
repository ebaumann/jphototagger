package org.jphototagger.program.controller.metadatatemplates;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JButton;

import org.openide.util.Lookup;

import org.jphototagger.domain.repository.MetadataTemplatesRepository;
import org.jphototagger.domain.templates.MetadataTemplate;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.view.dialogs.InputHelperDialog;
import org.jphototagger.program.view.popupmenus.MetadataTemplatesPopupMenu;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class DeleteMetadataTemplateController extends MetadataTemplateController {

    private final MetadataTemplatesRepository repo = Lookup.getDefault().lookup(MetadataTemplatesRepository.class);

    public DeleteMetadataTemplateController() {
        listen();
    }

    private void listen() {
        listenToActionsOf(MetadataTemplatesPopupMenu.INSTANCE.getItemDelete());
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

        return evt.getSource() == MetadataTemplatesPopupMenu.INSTANCE.getItemDelete();
    }

    @Override
    protected void action(MetadataTemplate template) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }

        String name = template.getName();
        String message = Bundle.getString(DeleteMetadataTemplateController.class, "DeleteMetadataTemplateController.Confirm", name);

        if (MessageDisplayer.confirmYesNo(InputHelperDialog.INSTANCE, message)) {
            if (!repo.deleteMetadataTemplate(name)) {
                message = Bundle.getString(DeleteMetadataTemplateController.class, "DeleteMetadataTemplateController.Error", name);
                MessageDisplayer.error(InputHelperDialog.INSTANCE, message);
            }
        }

        focusList();
    }
}
