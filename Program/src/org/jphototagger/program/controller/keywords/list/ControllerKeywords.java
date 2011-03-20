package org.jphototagger.program.controller.keywords.list;

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
import org.jdesktop.swingx.JXList;
import org.jphototagger.program.helper.KeywordsHelper;

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

        Object source = evt.getSource();

        if (source instanceof JXList) {
            JXList jxList = (JXList) source;

            action(KeywordsHelper.getSelectedKeywordsFromList(jxList));
        } else {
            action(getSelectedStrings((JList) source));
        }
    }

    protected ListModelKeywords getModel() {
        
        // All keyword lists having the same model
        return (ListModelKeywords) GUI.getAppPanel().getListEditKeywords().getModel();
    }

    private String getStringOfPopupMenu() {
        JXList list = (JXList) PopupMenuKeywordsList.INSTANCE.getList();
        int listIndex = PopupMenuKeywordsList.INSTANCE.getSelIndex();

        if (listIndex < 0) {
            return "";
        }

        int modelIndex = list.convertIndexToModel(listIndex);

        return (String) list.getModel().getElementAt(modelIndex);
    }

    private List<String> getSelectedStrings(JList list) {
        Object[] selValues = list.getSelectedValues();
        List<String> selStrings = new ArrayList<String>(selValues.length);

        for (Object selValue : selValues) {
            selStrings.add((String) selValue);
        }

        return selStrings;
    }
}
