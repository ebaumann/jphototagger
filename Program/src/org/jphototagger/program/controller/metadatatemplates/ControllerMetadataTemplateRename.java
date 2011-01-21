package org.jphototagger.program.controller.metadatatemplates;

import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.data.MetadataTemplate;
import org.jphototagger.program.database.DatabaseMetadataTemplates;
import org.jphototagger.program.helper.MetadataTemplateHelper;
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
public final class ControllerMetadataTemplateRename
        extends ControllerMetadataTemplate {
    public ControllerMetadataTemplateRename() {
        listen();
    }

    private void listen() {
        listenToActionsOf(PopupMenuMetadataTemplates.INSTANCE.getItemRename());
        getRenameButton().addActionListener(this);
    }

    private JButton getRenameButton() {
        return InputHelperDialog.INSTANCE.getPanelMetaDataTemplates()
            .getButtonRename();
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if ((evt.getSource() == getRenameButton())
                && isInputHelperListItemSelected()) {
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

        return evt.getSource()
               == PopupMenuMetadataTemplates.INSTANCE.getItemRename();
    }

    @Override
    protected void action(MetadataTemplate template) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }

        String fromName = template.getName();
        String toName   = MetadataTemplateHelper.getNewTemplateName(fromName);

        if (toName != null) {
            if (!DatabaseMetadataTemplates.INSTANCE.updateRename(fromName,
                    toName)) {
                MessageDisplayer.error(
                    null, "ControllerMetadataTemplateRename.Error", fromName);
            }
        }

        focusList();
    }
}
