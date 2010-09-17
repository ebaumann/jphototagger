/*
 * @(#)ControllerEditKeywordSynonyms.java    Created on 2010-02-09
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.controller.keywords.list;

import org.jphototagger.lib.dialog.InputDialog;
import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.program.database.DatabaseSynonyms;
import org.jphototagger.program.database.metadata.xmp
    .ColumnXmpDcSubjectsSubject;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.dialogs.InputHelperDialog;
import org.jphototagger.program.view.panels.EditRepeatableTextEntryPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuKeywordsList;

import java.awt.event.ActionEvent;
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

/**
 *
 * @author Elmar Baumann
 */
public final class ControllerEditKeywordSynonyms extends ControllerKeywords
        implements PopupMenuListener {
    private static final String DELIM = ";";
    private final JMenuItem     itemEditSynonyms =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "ControllerEditKeywordSynonyms.MenuItemEditSynonyms.DisplayName"));

    public ControllerEditKeywordSynonyms() {
        addMenuItem();
        listen();
    }

    private void listen() {
        listenToActionsOf(PopupMenuKeywordsList.INSTANCE.getItemEditSynonyms(),
                          itemEditSynonyms);
        getKeywordsList().addKeyListener(this);
        getPopupMenu().addPopupMenuListener(this);
    }

    private EditRepeatableTextEntryPanel getKeywordsPanel() {
        return (EditRepeatableTextEntryPanel) GUI.getAppPanel()
            .getEditMetadataPanels()
            .getEditPanel(ColumnXmpDcSubjectsSubject.INSTANCE);
    }

    private JList getKeywordsList() {
        return getKeywordsPanel().getList();
    }

    private JPopupMenu getPopupMenu() {
        return getKeywordsPanel().getPopupMenu();
    }

    private void addMenuItem() {
        itemEditSynonyms.setAccelerator(
            KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_S));

        JPopupMenu popupMenu = getPopupMenu();

        popupMenu.add(new Separator());
        popupMenu.add(itemEditSynonyms);
    }

    private void editInEditList() {
        List<String> keywords = new ArrayList<String>();

        for (Object selValue : getKeywordsList().getSelectedValues()) {
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
        if ((evt.getSource() == getKeywordsList()) && myKey(evt)) {
            editInEditList();
        } else {
            super.keyPressed(evt);
        }
    }

    private boolean itemsInEditListSelected() {
        return getKeywordsList().getSelectedValue() != null;
    }

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent evt) {
        boolean                      selected  = itemsInEditListSelected();
        EditRepeatableTextEntryPanel editPanel = getKeywordsPanel();

        editPanel.getItemRename().setEnabled(selected);
        editPanel.getItemRemove().setEnabled(selected);
        itemEditSynonyms.setEnabled(selected);
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return KeyEventUtil.isMenuShortcutWithAlt(evt, KeyEvent.VK_S);
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getSource()
               == PopupMenuKeywordsList.INSTANCE.getItemEditSynonyms();
    }

    @Override
    protected void action(List<String> keywords) {
        if (keywords == null) {
            throw new NullPointerException("keywords == null");
        }

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
    public void popupMenuWillBecomeInvisible(PopupMenuEvent evt) {

        // ignore
    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent evt) {

        // ignore
    }
}
