/*
 * @(#)ControllerEditKeywordSynonyms.java    2010-02-09
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

import de.elmar_baumann.jpt.database.DatabaseSynonyms;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.view.dialogs.InputHelperDialog;
import de.elmar_baumann.jpt.view.panels.EditRepeatableTextEntryPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuKeywordsList;
import de.elmar_baumann.lib.dialog.InputDialog;
import de.elmar_baumann.lib.event.util.KeyEventUtil;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JPopupMenu.Separator;
import javax.swing.KeyStroke;

/**
 *
 * @author  Elmar Baumann
 */
public final class ControllerEditKeywordSynonyms extends ControllerKeywords
        implements PopupMenuListener {
    private final JMenuItem itemEditSynonyms =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "ControllerEditKeywordSynonyms.MenuItemEditSynonyms.DisplayName"));
    private final EditRepeatableTextEntryPanel editPanel =
        (EditRepeatableTextEntryPanel) GUI.INSTANCE.getAppPanel()
            .getEditMetadataPanels()
            .getEditPanel(ColumnXmpDcSubjectsSubject.INSTANCE);
    private final JPopupMenu    popupMenuEditPanel = editPanel.getPopupMenu();
    private final JList         listEditPanel      = editPanel.getList();
    private static final String DELIM              = ";";

    public ControllerEditKeywordSynonyms() {
        addMenuItem();
        listen();
    }

    private void listen() {
        listenToActionsOf(PopupMenuKeywordsList.INSTANCE.getItemEditSynonyms(),
                          itemEditSynonyms);
        listEditPanel.addKeyListener(this);
        popupMenuEditPanel.addPopupMenuListener(this);
    }

    private void addMenuItem() {
        itemEditSynonyms.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                InputEvent.CTRL_MASK | InputEvent.CTRL_MASK));
        popupMenuEditPanel.add(new Separator());
        popupMenuEditPanel.add(itemEditSynonyms);
    }

    private void editInEditList() {
        List<String> keywords = new ArrayList<String>();

        for (Object selValue : listEditPanel.getSelectedValues()) {
            keywords.add(selValue.toString());
        }

        if (!keywords.isEmpty()) {
            action(keywords);
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == itemEditSynonyms) {
            editInEditList();
        } else {
            super.actionPerformed(evt);
        }
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        if ((evt.getSource() == listEditPanel) && myKey(evt)) {
            editInEditList();
        } else {
            super.keyPressed(evt);
        }
    }

    private boolean itemsInEditListSelected() {
        return listEditPanel.getSelectedValue() != null;
    }

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        boolean selected = itemsInEditListSelected();

        editPanel.getItemRename().setEnabled(selected);
        editPanel.getItemRemove().setEnabled(selected);
        itemEditSynonyms.setEnabled(selected);
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        return KeyEventUtil.isControlAlt(evt, KeyEvent.VK_S);
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        return evt.getSource()
               == PopupMenuKeywordsList.INSTANCE.getItemEditSynonyms();
    }

    @Override
    protected void action(List<String> keywords) {
        for (String keyword : keywords) {
            editSynonyms(keyword);
        }
    }

    private void editSynonyms(String keyword) {
        Set<String> oldSynonyms =
            DatabaseSynonyms.INSTANCE.getSynonymsOf(keyword);
        InputDialog dlg = new InputDialog(
                              InputHelperDialog.INSTANCE,
                              JptBundle.INSTANCE.getString(
                                  "ControllerEditKeywordSynonyms.Info.Input",
                                  keyword, DELIM), catSynonyms(oldSynonyms),
                                      UserSettings.INSTANCE.getProperties(),
                                      "ControllerEditKeywordSynonyms.Pos");

        dlg.setVisible(true);

        String synonyms = dlg.getInput();

        if (dlg.isAccepted() && (synonyms != null)) {
            Set<String> newSynonyms = splitSynonyms(synonyms);

            for (String synonym : newSynonyms) {
                DatabaseSynonyms.INSTANCE.insert(keyword, synonym);
            }

            for (String synonym : oldSynonyms) {
                if (!newSynonyms.contains(synonym)) {
                    DatabaseSynonyms.INSTANCE.delete(keyword, synonym);
                }
            }
        }
    }

    private Set<String> splitSynonyms(String synonymString) {
        Set<String>     synonyms = new HashSet<String>();
        StringTokenizer st       = new StringTokenizer(synonymString, DELIM);

        while (st.hasMoreTokens()) {
            String synonym = st.nextToken().trim();

            if (!synonym.isEmpty()) {
                synonyms.add(synonym);
            }
        }

        return synonyms;
    }

    private String catSynonyms(Set<String> synonyms) {
        StringBuilder sb = new StringBuilder();
        int           i  = 0;

        for (String synonym : synonyms) {
            sb.append((i++ == 0)
                      ? ""
                      : DELIM);
            sb.append(synonym);
        }

        return sb.toString();
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

        // ignore
    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {

        // ignore
    }
}
