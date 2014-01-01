package org.jphototagger.program.module.metadatatemplates;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import org.jphototagger.domain.templates.MetadataTemplate;
import org.jphototagger.lib.swing.KeyEventUtil;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.program.misc.InputHelperDialog;

/**
 * @author Elmar Baumann
 */
public final class AddMetadataTemplateController extends MetadataTemplateController {

    public AddMetadataTemplateController() {
        listen();
    }

    private void listen() {
        listenToActionsOf(MetadataTemplatesPopupMenu.INSTANCE.getItemAdd());
        getAddButtonInputHelper().addActionListener(this);
    }

    private JButton getAddButtonInputHelper() {
        return InputHelperDialog.INSTANCE.getPanelMetaDataTemplates().getButtonAdd();
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_N);
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }
        Object source = evt.getSource();
        return source == MetadataTemplatesPopupMenu.INSTANCE.getItemAdd()
                || source == getAddButtonInputHelper();
    }

    @Override
    protected void action(MetadataTemplate template) {
        EditMetaDataTemplateDialog dlg = new EditMetaDataTemplateDialog();
        MetadataTemplate t = new MetadataTemplate();

        dlg.setTemplate(t);
        ComponentUtil.show(dlg);
        focusList();
    }
}
