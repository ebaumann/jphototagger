package org.jphototagger.program.module.keywords.list;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;

import org.jdesktop.swingx.JXList;

import org.jphototagger.program.module.Controller;
import org.jphototagger.program.module.keywords.KeywordsHelper;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.misc.InputHelperDialog;

/**
 *
 *
 * @author Elmar Baumann
 */
public abstract class KeywordsController extends Controller {

    abstract protected void action(List<String> keywords);

    protected KeywordsController() {
        listen();
    }

    private void listen() {
        listenToKeyEventsOf(GUI.getAppPanel().getListEditKeywords(),
                InputHelperDialog.INSTANCE.getPanelKeywords().getList());
    }

    @Override
    protected void action(ActionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        action(Arrays.asList(getStringOfPopupMenu()));
    }

    @Override
    protected void action(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        Object source = evt.getSource();

        if (source instanceof JXList) {
            JXList jxList = (JXList) source;

            action(KeywordsHelper.getSelectedKeywordsFromList(jxList));
        }
    }

    protected KeywordsListModel getModel() {

        // All keyword lists having the same model
        return (KeywordsListModel) GUI.getAppPanel().getListEditKeywords().getModel();
    }

    private String getStringOfPopupMenu() {
        JXList list = KeywordsListPopupMenu.INSTANCE.getList();
        int listIndex = KeywordsListPopupMenu.INSTANCE.getSelIndex();

        if (listIndex < 0) {
            return "";
        }

        int modelIndex = list.convertIndexToModel(listIndex);

        return (String) list.getModel().getElementAt(modelIndex);
    }
}
