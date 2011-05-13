package org.jphototagger.program.controller.metadatatemplates;

import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.lib.event.util.MouseEventUtil;
import org.jphototagger.program.data.MetadataTemplate;
import org.jphototagger.program.view.dialogs.EditMetaDataTemplateDialog;
import org.jphototagger.program.view.dialogs.InputHelperDialog;
import org.jphototagger.program.view.popupmenus.PopupMenuMetadataTemplates;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ControllerMetadataTemplateEdit extends ControllerMetadataTemplate implements MouseListener {
    public ControllerMetadataTemplateEdit() {
        listen();
    }

    private void listen() {
        listenToActionsOf(PopupMenuMetadataTemplates.INSTANCE.getItemEdit());
        listenToDoubleClick();
        getEditButton().addActionListener(this);
    }

    private JButton getEditButton() {
        return InputHelperDialog.INSTANCE.getPanelMetaDataTemplates().getButtonEdit();
    }

    private void listenToDoubleClick() {
        InputHelperDialog.INSTANCE.getPanelMetaDataTemplates().getList().addMouseListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if ((evt.getSource() == getEditButton()) && isInputHelperListItemSelected()) {
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

        return KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_E);
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getSource() == PopupMenuMetadataTemplates.INSTANCE.getItemEdit();
    }

    @Override
    protected void action(MetadataTemplate template) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }

        EditMetaDataTemplateDialog dlg = new EditMetaDataTemplateDialog();

        dlg.setTemplate(template);
        ComponentUtil.show(dlg);
        focusList();
    }

    @Override
    public void mouseClicked(MouseEvent evt) {
        if (MouseEventUtil.isDoubleClick(evt)) {
            Object selValue = InputHelperDialog.INSTANCE.getPanelMetaDataTemplates().getList().getSelectedValue();

            if (selValue != null) {
                action((MetadataTemplate) selValue);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent evt) {

        // ignore
    }

    @Override
    public void mouseReleased(MouseEvent evt) {

        // ignore
    }

    @Override
    public void mouseEntered(MouseEvent evt) {

        // ignore
    }

    @Override
    public void mouseExited(MouseEvent evt) {

        // ignore
    }
}
