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
package de.elmar_baumann.jpt.controller.misc;

import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.frames.AppFrame;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
import de.elmar_baumann.jpt.view.panels.EditMetadataPanelsArray;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

/**
 * Controls the action: Go to ...
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-10
 */
public final class ControllerGoTo implements ActionListener {

    private final AppPanel                        appPanel         = GUI.INSTANCE.getAppPanel();
    private final ThumbnailsPanel                 thumbnailsPanel  = appPanel.getPanelThumbnails();
    private final EditMetadataPanelsArray         editPanels       = appPanel.getEditMetadataPanelsArray();
    private final JTextField                      textFieldSearch  = appPanel.getTextFieldSearch();
    private final AppFrame                        appFrame         = GUI.INSTANCE.getAppFrame();
    private final Map<AppFrame.GoTo, Component>   componentOfGoTo  = new HashMap<AppFrame.GoTo, Component>();
    private final Map<AppFrame.GoTo, JTabbedPane> tabbedPaneOfGoTo = new HashMap<AppFrame.GoTo, JTabbedPane>();

    // Not static (timing)
    private void initMaps() {
        componentOfGoTo.put(AppFrame.GoTo.IMAGE_COLLECTIONS , appPanel.getTabSelectionImageCollections());
        componentOfGoTo.put(AppFrame.GoTo.DIRECTORIES       , appPanel.getTabSelectionDirectories());
        componentOfGoTo.put(AppFrame.GoTo.FAVORITES         , appPanel.getTabSelectionFavoriteDirectories());
        componentOfGoTo.put(AppFrame.GoTo.SAVED_SEARCHES    , appPanel.getTabSelectionSavedSearches());
        componentOfGoTo.put(AppFrame.GoTo.KEYWORDS_SEL      , appPanel.getTabSelectionKeywords());
        componentOfGoTo.put(AppFrame.GoTo.TIMELINE          , appPanel.getTabSelectionTimeline());
        componentOfGoTo.put(AppFrame.GoTo.MISC_METADATA     , appPanel.getTabSelectionMiscMetadata());

        componentOfGoTo.put(AppFrame.GoTo.EDIT_PANELS       , appPanel.getTabMetadataEdit());
        componentOfGoTo.put(AppFrame.GoTo.EXIF_METADATA     , appPanel.getTabMetadataExif());
        componentOfGoTo.put(AppFrame.GoTo.IPTC_METADATA     , appPanel.getTabMetadataIptc());
        componentOfGoTo.put(AppFrame.GoTo.XMP_METADATA      , appPanel.getTabMetadataXmp());
        componentOfGoTo.put(AppFrame.GoTo.KEYWORDS_EDIT     , appPanel.getTabEditKeywords());

        tabbedPaneOfGoTo.put(AppFrame.GoTo.IMAGE_COLLECTIONS, appPanel.getTabbedPaneSelection());
        tabbedPaneOfGoTo.put(AppFrame.GoTo.DIRECTORIES      , appPanel.getTabbedPaneSelection());
        tabbedPaneOfGoTo.put(AppFrame.GoTo.FAVORITES        , appPanel.getTabbedPaneSelection());
        tabbedPaneOfGoTo.put(AppFrame.GoTo.SAVED_SEARCHES   , appPanel.getTabbedPaneSelection());
        tabbedPaneOfGoTo.put(AppFrame.GoTo.KEYWORDS_SEL     , appPanel.getTabbedPaneSelection());
        tabbedPaneOfGoTo.put(AppFrame.GoTo.TIMELINE         , appPanel.getTabbedPaneSelection());
        tabbedPaneOfGoTo.put(AppFrame.GoTo.MISC_METADATA    , appPanel.getTabbedPaneSelection());

        tabbedPaneOfGoTo.put(AppFrame.GoTo.EDIT_PANELS      , appPanel.getTabbedPaneMetadata());
        tabbedPaneOfGoTo.put(AppFrame.GoTo.EXIF_METADATA    , appPanel.getTabbedPaneMetadata());
        tabbedPaneOfGoTo.put(AppFrame.GoTo.IPTC_METADATA    , appPanel.getTabbedPaneMetadata());
        tabbedPaneOfGoTo.put(AppFrame.GoTo.XMP_METADATA     , appPanel.getTabbedPaneMetadata());
        tabbedPaneOfGoTo.put(AppFrame.GoTo.KEYWORDS_EDIT    , appPanel.getTabbedPaneMetadata());
    }

    public ControllerGoTo() {
        initMaps();
        listen();
    }

    private void listen() {
        for (AppFrame.GoTo gt : AppFrame.GoTo.values()) {
            appFrame.getMenuItemOfGoto(gt).addActionListener(this);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        selectComponent((JMenuItem) e.getSource());
    }

    private void selectComponent(JMenuItem item) {
        AppFrame.GoTo goTo = appFrame.getGotoOfMenuItem(item);
        if (tabbedPaneOfGoTo.containsKey(goTo)) {

            tabbedPaneOfGoTo.get(goTo).setSelectedComponent(componentOfGoTo.get(goTo));
            componentOfGoTo.get(goTo).requestFocusInWindow();

        } else if (goTo.equals(AppFrame.GoTo.FAST_SEARCH)) {

            textFieldSearch.requestFocusInWindow();

        } else if (goTo.equals(AppFrame.GoTo.THUMBNAILS_PANEL)) {

            thumbnailsPanel.requestFocusInWindow();
        }
        if (goTo.equals(AppFrame.GoTo.EDIT_PANELS)) {

            editPanels.setFocusToFirstEditField();
        }
    }
}
