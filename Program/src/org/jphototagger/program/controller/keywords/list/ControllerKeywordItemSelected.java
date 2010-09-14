/*
 * @(#)ControllerKeywordItemSelected.java    Created on 2008-10-25
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

import java.awt.EventQueue;
import org.jphototagger.program.event.listener.RefreshListener;
import org.jphototagger.program.event.RefreshEvent;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.types.Content;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.panels.AppPanel;
import org.jphototagger.program.view.panels.ThumbnailsPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JList;
import javax.swing.JRadioButton;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class ControllerKeywordItemSelected
        implements ActionListener, ListSelectionListener, RefreshListener {
    private static final String KEY_RADIO_BUTTON =
        "ControllerKeywordItemSelected.RadioButton";
    private final AppPanel     appPanel               =
        GUI.INSTANCE.getAppPanel();
    private final JList        listKeywords           =
        appPanel.getListSelKeywords();
    private final JRadioButton radioButtonAllKeywords =
        appPanel.getRadioButtonSelKeywordsMultipleSelAll();
    private final JRadioButton radioButtonOneKeyword =
        appPanel.getRadioButtonSelKeywordsMultipleSelOne();
    private final ThumbnailsPanel tnPanel = appPanel.getPanelThumbnails();

    public ControllerKeywordItemSelected() {
        readPersistent();
        listen();
    }

    private void listen() {
        listKeywords.addListSelectionListener(this);
        radioButtonAllKeywords.addActionListener(this);
        radioButtonOneKeyword.addActionListener(this);
        tnPanel.addRefreshListener(this, Content.KEYWORD);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (listKeywords.getSelectedIndex() >= 0) {
            writePersistent();
            update(null);
        }
    }

    @Override
    public void refresh(RefreshEvent evt) {
        if (listKeywords.getSelectedIndex() >= 0) {
            update(evt);
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting()
                && (listKeywords.getSelectedIndex() >= 0)) {
            update(null);
        }
    }

    private void update(RefreshEvent evt) {
        List<String> selKeywords = getSelectedKeywords();

        EventQueue.invokeLater(isAllKeywords()
                                   ? new ShowThumbnailsContainingAllKeywords(
                                       selKeywords, (evt == null)
                ? null
                : evt.getSettings())
                                   : new ShowThumbnailsContainingKeywords(
                                       selKeywords, (evt == null)
                ? null
                : evt.getSettings()));
    }

    private List<String> getSelectedKeywords() {
        Object[]     selValues = listKeywords.getSelectedValues();
        List<String> keywords  = new ArrayList<String>();

        for (Object selValue : selValues) {
            if (selValue instanceof String) {
                keywords.add((String) selValue);
            }
        }

        assert keywords.size() == selValues.length :
               "Not all keywords are strings: " + keywords;

        return keywords;
    }

    private boolean isAllKeywords() {
        return radioButtonAllKeywords.isSelected();
    }

    private void readPersistent() {
        Properties properties     = UserSettings.INSTANCE.getProperties();
        boolean    radioButtonAll = true;

        if (properties.containsKey(KEY_RADIO_BUTTON)) {
            radioButtonAll =
                UserSettings.INSTANCE.getSettings().getInt(KEY_RADIO_BUTTON)
                == 0;
        }

        radioButtonAllKeywords.setSelected(radioButtonAll);
        radioButtonOneKeyword.setSelected(!radioButtonAll);
    }

    private void writePersistent() {
        UserSettings.INSTANCE.getSettings().set(isAllKeywords()
                ? 0
                : 1, KEY_RADIO_BUTTON);
        UserSettings.INSTANCE.writeToFile();
    }
}
