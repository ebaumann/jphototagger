/*
 * @(#)ControllerKeywords.java    Created on 2010-01-07
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package de.elmar_baumann.jpt.controller.keywords.list;

import de.elmar_baumann.jpt.controller.Controller;
import de.elmar_baumann.jpt.model.ListModelKeywords;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.dialogs.InputHelperDialog;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuKeywordsList;
import de.elmar_baumann.lib.thirdparty.SortedListModel;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JList;

/**
 *
 *
 * @author  Elmar Baumann
 */
public abstract class ControllerKeywords extends Controller {
    abstract protected void action(List<String> keywords);

    protected ControllerKeywords() {
        listen();
    }

    private void listen() {
        listenToKeyEventsOf(
            GUI.INSTANCE.getAppPanel().getListEditKeywords(),
            InputHelperDialog.INSTANCE.getPanelKeywords().getList());
    }

    @Override
    protected void action(ActionEvent evt) {
        action(Arrays.asList(getStringOfPopupMenu()));
    }

    @Override
    protected void action(KeyEvent evt) {
        action(getSelStrings((JList) evt.getSource()));
    }

    protected ListModelKeywords getModel() {

        // All lists have the same model
        return (ListModelKeywords) ((SortedListModel) GUI.INSTANCE.getAppPanel()
            .getListEditKeywords().getModel()).getUnsortedModel();
    }

    private String getStringOfPopupMenu() {
        JList list  = PopupMenuKeywordsList.INSTANCE.getList();
        int   index = PopupMenuKeywordsList.INSTANCE.getSelIndex();

        if (index < 0) {
            return "";
        }

        return (String) list.getModel().getElementAt(index);
    }

    private List<String> getSelStrings(JList list) {
        Object[]     selValues  = list.getSelectedValues();
        List<String> selStrings = new ArrayList<String>(selValues.length);

        for (Object selValue : selValues) {
            assert selValue instanceof String : selValue;
            selStrings.add((String) selValue);
        }

        return selStrings;
    }
}
