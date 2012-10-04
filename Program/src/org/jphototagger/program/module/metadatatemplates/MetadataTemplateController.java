package org.jphototagger.program.module.metadatatemplates;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import org.jdesktop.swingx.JXList;
import org.jphototagger.domain.templates.MetadataTemplate;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.misc.InputHelperDialog;
import org.jphototagger.program.module.Controller;

/**
 * @author Elmar Baumann
 */
public abstract class MetadataTemplateController extends Controller {

    protected abstract void action(MetadataTemplate template);

    public MetadataTemplateController() {
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

    protected JXList getInputHelperList() {
        return InputHelperDialog.INSTANCE.getPanelMetaDataTemplates().getList();
    }

    protected boolean isInputHelperListItemSelected() {
        return getInputHelperList().getSelectedIndex() >= 0;
    }

    private MetadataTemplate getTemplateOfPopupMenu() {
        int index = MetadataTemplatesPopupMenu.INSTANCE.getSelIndex();

        if (index < 0) {
            return null;
        }

        MetadataTemplatesListModel model = ModelFactory.INSTANCE.getModel(MetadataTemplatesListModel.class);
        int size = model.getSize();

        return index < size
                ? (MetadataTemplate) model.get(index)
                : null;
    }

    protected MetadataTemplate getTemplateOfInputHelperList() {
        assert isInputHelperListItemSelected();

        return (MetadataTemplate) getInputHelperList().getSelectedValue();
    }
}
