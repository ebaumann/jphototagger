package org.jphototagger.program.controller.metadatatemplates;

import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.program.data.MetadataTemplate;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.EditMetaDataTemplateDialog;
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
public final class ControllerMetadataTemplateAdd
        extends ControllerMetadataTemplate {
    public ControllerMetadataTemplateAdd() {
        listen();
    }

    private void listen() {
        listenToActionsOf(PopupMenuMetadataTemplates.INSTANCE.getItemAdd());
        getAddButtonEditPanel().addActionListener(this);
        getAddButtonInputHelper().addActionListener(this);
    }

    private JButton getAddButtonEditPanel() {
        return GUI.getAppPanel().getButtonMetadataTemplateAdd();
    }

    private JButton getAddButtonInputHelper() {
        return InputHelperDialog.INSTANCE.getPanelMetaDataTemplates()
            .getButtonAdd();
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_N);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();

        if ((source == getAddButtonEditPanel())
                || (source == getAddButtonInputHelper())) {
            action((MetadataTemplate) null);
        } else {
            super.actionPerformed(evt);
        }
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getSource()
               == PopupMenuMetadataTemplates.INSTANCE.getItemAdd();
    }

    @Override
    protected void action(MetadataTemplate template) {
        EditMetaDataTemplateDialog dlg = new EditMetaDataTemplateDialog();
        MetadataTemplate           t   = new MetadataTemplate();

        dlg.setTemplate(t);
        ComponentUtil.show(dlg);
        focusList();
    }
}
