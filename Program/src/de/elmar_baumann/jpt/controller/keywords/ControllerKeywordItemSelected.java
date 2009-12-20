/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.controller.keywords;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.types.Content;
import de.elmar_baumann.jpt.event.listener.RefreshListener;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
import de.elmar_baumann.lib.util.Settings;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-25
 */
public final class ControllerKeywordItemSelected implements
        ActionListener, ListSelectionListener, RefreshListener {

    private static final String KEY_RADIO_BUTTON =
            "ControllerKeywordItemSelected.RadioButton";
    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JList listKeywords = appPanel.getListKeywords();
    private final JRadioButton radioButtonAllKeywords =
            appPanel.getRadioButtonKeywordsMultipleSelAll();
    private final JRadioButton radioButtonOneKeyword =
            appPanel.getRadioButtonKeywordsMultipleSelOne();
    private final ThumbnailsPanel thumbnailsPanel =
            appPanel.getPanelThumbnails();

    public ControllerKeywordItemSelected() {
        readPersistent();
        listen();
    }

    private void listen() {
        listKeywords.addListSelectionListener(this);
        radioButtonAllKeywords.addActionListener(this);
        radioButtonOneKeyword.addActionListener(this);
        thumbnailsPanel.addRefreshListener(this, Content.KEYWORD);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (listKeywords.getSelectedIndex() >= 0) {
            writePersistent();
            update();
        }
    }

    @Override
    public void refresh() {
        if (listKeywords.getSelectedIndex() >= 0) {
            update();
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && listKeywords.getSelectedIndex() >= 0) {
            update();
        }
    }

    private void update() {
        List<String> selKeywords = getSelectedKeywords();
        SwingUtilities.invokeLater(
                isAllKeywords()
                ? new ShowThumbnailsContainingAllKeywords(selKeywords)
                : new ShowThumbnailsContainingKeywords(selKeywords));
    }

    private List<String> getSelectedKeywords() {
        Object[] selValues = listKeywords.getSelectedValues();
        List<String> keywords = new ArrayList<String>();
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
        Properties properties = UserSettings.INSTANCE.getProperties();
        boolean radioButtonAll = true;
        if (properties.containsKey(KEY_RADIO_BUTTON)) {
            radioButtonAll = UserSettings.INSTANCE.getSettings().getInt(
                    KEY_RADIO_BUTTON) == 0;
        }
        radioButtonAllKeywords.setSelected(radioButtonAll);
        radioButtonOneKeyword.setSelected(!radioButtonAll);
    }

    private void writePersistent() {
        UserSettings.INSTANCE.getSettings().setInt(isAllKeywords()
                                                   ? 0
                                                   : 1,
                KEY_RADIO_BUTTON);
        UserSettings.INSTANCE.writeToFile();
    }
}
