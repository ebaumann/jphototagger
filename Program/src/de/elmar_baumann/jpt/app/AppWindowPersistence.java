/*
 * @(#)AppWindowPersistence.java    Created on 2010-01-15
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

package de.elmar_baumann.jpt.app;

import de.elmar_baumann.jpt.event.listener.AppExitListener;
import de.elmar_baumann.jpt.event.listener.UserSettingsListener;
import de.elmar_baumann.jpt.event.UserSettingsEvent;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.view.frames.AppFrame;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.jpt.view.panels.KeywordsPanel;
import de.elmar_baumann.lib.componentutil.ComponentUtil;
import de.elmar_baumann.lib.util.Settings;

import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Reads and writes persistent important settings of {@link AppPanel} and
 * {@link AppFrame}.
 *
 * @author  Elmar Baumann
 */
public final class AppWindowPersistence
        implements ComponentListener, AppExitListener, UserSettingsListener {

    // Strings has to be equals to that in AppPanel!
    private static final String KEY_DIVIDER_LOCATION_MAIN =
        "AppPanel.DividerLocationMain";
    private static final String KEY_DIVIDER_LOCATION_THUMBNAILS =
        "AppPanel.DividerLocationThumbnails";
    private static final String KEY_KEYWORDS_VIEW   = "AppPanel.KeywordsView";
    private final Component     cardSelKeywordsList =
        GUI.INSTANCE.getAppPanel().getCardSelKeywordsList();
    private final Component cardSelKeywordsTree =
        GUI.INSTANCE.getAppPanel().getCardSelKeywordsTree();
    private final Map<Component, String> NAME_OF_CARD = new HashMap<Component,
                                                            String>(2);
    private final Map<String, Component> CARD_OF_NAME = new HashMap<String,
                                                            Component>(2);

    // Not a singleton: init() gets cards of AppPanel that is not static
    public AppWindowPersistence() {
        init();
        listen();
    }

    private void init() {

        // Strings has to be equal to the card names in AppPanel (errors on renamings)!
        NAME_OF_CARD.put(cardSelKeywordsList, "flatKeywords");
        NAME_OF_CARD.put(cardSelKeywordsTree, "keywordsTree");

        for (Component c : NAME_OF_CARD.keySet()) {
            CARD_OF_NAME.put(NAME_OF_CARD.get(c), c);
        }
    }

    private void listen() {
        AppLifeCycle.INSTANCE.addAppExitListener(this);
        cardSelKeywordsList.addComponentListener(this);
        cardSelKeywordsTree.addComponentListener(this);
    }

    @Override
    public void componentShown(ComponentEvent e) {
        Component c                 = e.getComponent();
        boolean   isSelKeywordsCard = isSelKeywordsCard(c);
        boolean   knownCardName     = NAME_OF_CARD.containsKey(c);

        assert isSelKeywordsCard && knownCardName : c;

        if (isSelKeywordsCard && knownCardName) {
            UserSettings.INSTANCE.getSettings().set(NAME_OF_CARD.get(c),
                    KEY_KEYWORDS_VIEW);
            UserSettings.INSTANCE.writeToFile();
        }
    }

    private boolean isSelKeywordsCard(Component c) {
        return (c == cardSelKeywordsList) || (c == cardSelKeywordsTree);
    }

    public void readAppFrameFromProperties() {
        AppFrame appFrame = GUI.INSTANCE.getAppFrame();

        UserSettings.INSTANCE.getSettings().applySizeAndLocation(appFrame);
        appFrame.pack();
    }

    public void readAppPanelFromProperties() {
        AppPanel appPanel = GUI.INSTANCE.getAppPanel();

        UserSettings.INSTANCE.getSettings().applySettings(appPanel, null);
        appPanel.setEnabledIptcTab(UserSettings.INSTANCE.isDisplayIptc());
        setInitKeywordsView(appPanel);
        selectFastSearch(appPanel);
    }

    private void selectFastSearch(AppPanel appPanel) {
        ComponentUtil.forceRepaint(appPanel.getComboBoxFastSearch());
        appPanel.getTextAreaSearch().requestFocusInWindow();
    }

    private void setInitKeywordsView(AppPanel appPanel) {
        KeywordsPanel panelEditKeywords = appPanel.getPanelEditKeywords();

        panelEditKeywords.setKeyCard("AppPanel.Keywords.Card");
        panelEditKeywords.setKeyTree("AppPanel.Keywords.Tree");
        panelEditKeywords.readProperties();

        // Strings has to be equal to the card names in AppPanel (errors on renamings)!
        String name = "keywordsTree";

        if (UserSettings.INSTANCE.getProperties().containsKey(
                KEY_KEYWORDS_VIEW)) {
            String s = UserSettings.INSTANCE.getSettings().getString(
                           KEY_KEYWORDS_VIEW);

            if (s.equals("flatKeywords") || s.equals("keywordsTree")) {
                name = s;
            }
        }

        Component c = CARD_OF_NAME.get(name);

        if (c == cardSelKeywordsList) {
            appPanel.displaySelKeywordsList(AppPanel.SelectAlso.NOTHING_ELSE);
        } else if (c == cardSelKeywordsTree) {
            appPanel.displaySelKeywordsTree(AppPanel.SelectAlso.NOTHING_ELSE);
        } else {
            assert false;
        }
    }

    @Override
    public void appWillExit() {
        writeAppProperties();
    }

    private void writeAppProperties() {
        AppPanel appPanel = GUI.INSTANCE.getAppPanel();
        Settings settings = UserSettings.INSTANCE.getSettings();

        settings.set(appPanel, null);
        settings.set(appPanel.getSplitPaneMain().getDividerLocation(),
                     KEY_DIVIDER_LOCATION_MAIN);
        settings.set(
            appPanel.getSplitPaneThumbnailsMetadata().getDividerLocation(),
            KEY_DIVIDER_LOCATION_THUMBNAILS);
        appPanel.getPanelEditKeywords().writeProperties();
        UserSettings.INSTANCE.writeToFile();
    }

    @Override
    public void applySettings(UserSettingsEvent evt) {
        if (evt.getType().equals(UserSettingsEvent.Type.DISPLAY_IPTC)) {
            GUI.INSTANCE.getAppPanel().setEnabledIptcTab(
                UserSettings.INSTANCE.isDisplayIptc());
        }
    }

    @Override
    public void componentResized(ComponentEvent e) {

        // ignore
    }

    @Override
    public void componentMoved(ComponentEvent e) {

        // ignore
    }

    @Override
    public void componentHidden(ComponentEvent e) {

        // ignore
    }
}
