package org.jphototagger.program.controller.metadatatemplates;

import org.jphototagger.program.controller.Controller;
import org.jphototagger.program.data.MetadataTemplate;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.model.ListModelMetadataTemplates;
import org.jphototagger.program.view.dialogs.InputHelperDialog;
import org.jphototagger.program.view.popupmenus.PopupMenuMetadataTemplates;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JList;

/**
 *
 *
 * @author Elmar Baumann
 */
public abstract class ControllerMetadataTemplate extends Controller {
    protected abstract void action(MetadataTemplate template);

    public ControllerMetadataTemplate() {
        listen();
    }

    private void listen() {
        listenToKeyEventsOf(InputHelperDialog.INSTANCE.getPanelMetaDataTemplates().getList());
    }

    @Override
    protected void action(ActionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        action(getTemplateOfPopupMenu());
    }

    @Override
    protected void action(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        action(getTemplateOfInputHelperList());
    }

    protected void focusList() {
        InputHelperDialog.INSTANCE.toFront();
        InputHelperDialog.INSTANCE.getPanelMetaDataTemplates().getList().requestFocusInWindow();
    }

    protected JList getInputHelperList() {
        return InputHelperDialog.INSTANCE.getPanelMetaDataTemplates().getList();
    }

    protected boolean isInputHelperListItemSelected() {
        return getInputHelperList().getSelectedIndex() >= 0;
    }

    private MetadataTemplate getTemplateOfPopupMenu() {
        int index = PopupMenuMetadataTemplates.INSTANCE.getSelIndex();

        if (index < 0) {
            return null;
        }

        ListModelMetadataTemplates model = ModelFactory.INSTANCE.getModel(ListModelMetadataTemplates.class);

        return (MetadataTemplate) model.get(index);
    }

    protected MetadataTemplate getTemplateOfInputHelperList() {
        assert isInputHelperListItemSelected();

        return (MetadataTemplate) getInputHelperList().getSelectedValue();
    }
}
