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
package de.elmar_baumann.jpt.app;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.event.UserSettingsEvent;
import de.elmar_baumann.jpt.event.listener.AppExitListener;
import de.elmar_baumann.jpt.event.listener.UserSettingsListener;
import de.elmar_baumann.jpt.factory.MetaFactory;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.ViewUtil;
import de.elmar_baumann.jpt.view.frames.AppFrame;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.jpt.view.panels.KeywordsPanel;
import de.elmar_baumann.lib.componentutil.ComponentUtil;
import de.elmar_baumann.lib.util.Settings;
import de.elmar_baumann.lib.util.SettingsHints;
import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import javax.swing.SwingUtilities;

/**
 * Reads and writes persistent important settings of {@link AppPanel} and
 * {@link AppFrame}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-15
 */
public final class AppWindowPersistence
        implements ComponentListener,
                   AppExitListener,
                   UserSettingsListener
    {

    // Strings has to be equals to that in AppPanel!
    private static final String                 KEY_DIVIDER_LOCATION_MAIN       = "AppPanel.DividerLocationMain";
    private static final String                 KEY_DIVIDER_LOCATION_THUMBNAILS = "AppPanel.DividerLocationThumbnails";

    private static final String                 KEY_KEYWORDS_VIEW               = "AppPanel.KeywordsView";
    private final        Component              cardSelKeywordsList             = GUI.INSTANCE.getAppPanel().getCardSelKeywordsList();
    private final        Component              cardSelKeywordsTree             = GUI.INSTANCE.getAppPanel().getCardSelKeywordsTree();
    private final        Map<Component, String> NAME_OF_CARD                    = new HashMap<Component, String>(2);
    private final        Map<String, Component> CARD_OF_NAME                    = new HashMap<String, Component>(2);

    public AppWindowPersistence() {
        init();
        listen();
    }

    private void init() {
        // Strings has to be equals to that in AppPanel!
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
        Component c = e.getComponent();

        assert NAME_OF_CARD.containsKey(c) : c;

        if (isSelKeywordsCard(c) && NAME_OF_CARD.containsKey(c)) {
            UserSettings.INSTANCE.getSettings().setString(NAME_OF_CARD.get(c), KEY_KEYWORDS_VIEW);
            UserSettings.INSTANCE.writeToFile();
        }
    }

    private boolean isSelKeywordsCard(Component c) {
        return c == cardSelKeywordsList || c == cardSelKeywordsTree;
    }

    public void readAppFrameFromProperties() {
        AppFrame appFrame = GUI.INSTANCE.getAppFrame();
        UserSettings.INSTANCE.getSettings().getSizeAndLocation(appFrame);
        appFrame.pack();
    }

    public void readAppPanelFromProperties() {
        final AppPanel appPanel = GUI.INSTANCE.getAppPanel();

        UserSettings.INSTANCE.getSettings().getComponent(appPanel, getPersistentSettingsHints());

        setDisplayIptc();
        setInitKeywordsView();
        ComponentUtil.forceRepaint(appPanel.getComboBoxFastSearch());
        appPanel.getTextAreaSearch().requestFocusInWindow();

        // Some models maybe not ready
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(3 * 1000);
                } catch (InterruptedException ex) {
                    AppLog.logSevere(MetaFactory.class, ex);
                }

                String   keyPrefix = AppPanel.class.getName() + ".";
                Settings settings  = UserSettings.INSTANCE.getSettings();

                settings.getSelectedIndex(appPanel.getListNoMetadata()   , keyPrefix + "listNoMetadata");
                settings.getSelectedIndex(appPanel.getListSavedSearches(), keyPrefix + "listSavedSearches");
                settings.getSelectedIndex(appPanel.getListSelKeywords()  , keyPrefix + "listSelKeywords");
                settings.getTree         (appPanel.getTreeSelKeywords()  , keyPrefix + "treeSelKeywords");
                settings.getTree         (appPanel.getTreeMiscMetadata() , keyPrefix + "treeMiscMetadata");
                settings.getTree         (appPanel.getTreeSelKeywords()  , keyPrefix + "treeSelKeywords");
                settings.getTree         (appPanel.getTreeTimeline()     , keyPrefix + "treeTimeline");
            }
        });
    }

    private void setDisplayIptc() {
        boolean displayIptc = UserSettings.INSTANCE.isDisplayIptc();
        GUI.INSTANCE.getAppPanel().setEnabledIptcTab(displayIptc);
    }

    private void setInitKeywordsView() {
        AppPanel      appPanel          = GUI.INSTANCE.getAppPanel();
        KeywordsPanel panelEditKeywords = appPanel.getPanelEditKeywords();

        panelEditKeywords.setKeyCard("AppPanel.Keywords.Card");
        panelEditKeywords.setKeyTree("AppPanel.Keywords.Tree");
        panelEditKeywords.readProperties();

        String name = "keywordsTree";
        if (UserSettings.INSTANCE.getProperties().containsKey(KEY_KEYWORDS_VIEW)) {
            String s = UserSettings.INSTANCE.getSettings().getString(KEY_KEYWORDS_VIEW);
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

        settings.setComponent(appPanel, getPersistentSettingsHints());
        settings.setInt      (appPanel.getSplitPaneMain().getDividerLocation(), KEY_DIVIDER_LOCATION_MAIN);
        settings.setInt      (appPanel.getSplitPaneThumbnailsMetadata().getDividerLocation(), KEY_DIVIDER_LOCATION_THUMBNAILS);
        ViewUtil.writeTreeDirectoriesToProperties();
        appPanel.getPanelEditKeywords().writeProperties();
        UserSettings.INSTANCE.writeToFile();
    }

    @Override
    public void applySettings(UserSettingsEvent evt) {
        if (evt.getType().equals(UserSettingsEvent.Type.DISPLAY_IPTC)) {
            setDisplayIptc();
        }
    }

    public SettingsHints getPersistentSettingsHints() {
        SettingsHints hints     = new SettingsHints(EnumSet.of(SettingsHints.Option.SET_TABBED_PANE_CONTENT));
        String        className = AppPanel.class.getName();
        hints.addExclude(className + ".textAreaSearch");
        hints.addExclude(className + ".panelEditMetadata");
        hints.addExclude(className + ".treeDirectories");
        hints.addExclude(className + ".treeFavorites");
        return hints;
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
