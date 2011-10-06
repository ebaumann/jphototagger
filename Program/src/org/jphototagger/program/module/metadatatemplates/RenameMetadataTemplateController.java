package org.jphototagger.program.module.metadatatemplates;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JButton;

import org.openide.util.Lookup;

import org.jphototagger.domain.repository.MetadataTemplatesRepository;
import org.jphototagger.domain.templates.MetadataTemplate;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.module.misc.InputHelperDialog;
import org.jphototagger.program.view.popupmenus.MetadataTemplatesPopupMenu;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class RenameMetadataTemplateController extends MetadataTemplateController {

    private final MetadataTemplatesRepository repo = Lookup.getDefault().lookup(MetadataTemplatesRepository.class);

    public RenameMetadataTemplateController() {
        listen();
    }

    private void listen() {
        listenToActionsOf(MetadataTemplatesPopupMenu.INSTANCE.getItemRename());
        getRenameButton().addActionListener(this);
    }

    private JButton getRenameButton() {
        return InputHelperDialog.INSTANCE.getPanelMetaDataTemplates().getButtonRename();
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if ((evt.getSource() == getRenameButton()) && isInputHelperListItemSelected()) {
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

        return evt.getKeyCode() == KeyEvent.VK_F2;
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getSource() == MetadataTemplatesPopupMenu.INSTANCE.getItemRename();
    }

    @Override
    protected void action(MetadataTemplate template) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }

        String fromName = template.getName();
        String toName = MetadataTemplateHelper.getNewTemplateName(fromName);

        if (toName != null) {
            if (!repo.updateRenameMetadataTemplate(fromName, toName)) {
                String message = Bundle.getString(RenameMetadataTemplateController.class, "RenameMetadataTemplateController.Error", fromName);
                MessageDisplayer.error(null, message);
            }
        }

        focusList();
    }
}
