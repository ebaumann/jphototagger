package org.jphototagger.program.controller.keywords.list;

import org.jphototagger.lib.thirdparty.SortedListModel;
import org.jphototagger.program.controller.Controller;
import org.jphototagger.program.model.ListModelKeywords;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.InputHelperDialog;
import org.jphototagger.program.view.popupmenus.PopupMenuKeywordsList;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JList;

/**
 *
 *
 * @author Elmar Baumann
 */
public abstract class ControllerKeywords extends Controller {
    abstract protected void action(List<String> keywords);

    protected ControllerKeywords() {
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

        action(getSelStrings((JList) evt.getSource()));
    }

    protected ListModelKeywords getModel() {

        // All lists have the same model
        return (ListModelKeywords) ((SortedListModel) GUI.getAppPanel().getListEditKeywords().getModel())
            .getUnsortedModel();
    }

    private String getStringOfPopupMenu() {
        JList list = PopupMenuKeywordsList.INSTANCE.getList();
        int index = PopupMenuKeywordsList.INSTANCE.getSelIndex();

        if (index < 0) {
            return "";
        }

        return (String) list.getModel().getElementAt(index);
    }

    private List<String> getSelStrings(JList list) {
        Object[] selValues = list.getSelectedValues();
        List<String> selStrings = new ArrayList<String>(selValues.length);

        for (Object selValue : selValues) {
            assert selValue instanceof String : selValue;
            selStrings.add((String) selValue);
        }

        return selStrings;
    }
}
