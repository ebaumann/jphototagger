package org.jphototagger.program.app;

import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.lib.util.Settings;
import org.jphototagger.lib.util.SettingsHints;
import org.jphototagger.program.event.listener.AppExitListener;
import org.jphototagger.program.event.listener.UserSettingsListener;
import org.jphototagger.program.event.UserSettingsEvent;
import org.jphototagger.program.model.TableModelIptc;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.frames.AppFrame;
import org.jphototagger.program.view.panels.AppPanel;
import org.jphototagger.program.view.panels.KeywordsPanel;
import org.jphototagger.program.view.panels.ThumbnailsPanel;

import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.EventQueue;

import java.io.File;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.table.TableModel;

/**
 * Reads and writes persistent important settings of {@link AppPanel} and
 * {@link AppFrame}.
 *
 * @author Elmar Baumann
 */
public final class AppWindowPersistence
        implements ComponentListener, AppExitListener, UserSettingsListener {

    // Strings has to be equals to that in AppPanel!
    private static final String KEY_DIVIDER_LOCATION_MAIN =
        "AppPanel.DividerLocationMain";
    private static final String KEY_DIVIDER_LOCATION_THUMBNAILS =
        "AppPanel.DividerLocationThumbnails";
    private static final String KEY_KEYWORDS_VIEW = "AppPanel.KeywordsView";
    private final Component     cardSelKeywordsList =
        GUI.getAppPanel().getCardSelKeywordsList();
    private final Component cardSelKeywordsTree =
        GUI.getAppPanel().getCardSelKeywordsTree();
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

        // Strings has to be equal to the card names in AppPanel
        // (errors on renamings)!
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
        UserSettings.INSTANCE.addUserSettingsListener(this);
    }

    @Override
    public void componentShown(ComponentEvent evt) {
        Component c                 = evt.getComponent();
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
        final AppFrame appFrame = GUI.getAppFrame();

        UserSettings.INSTANCE.getSettings().applySizeAndLocation(appFrame);
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                appFrame.pack();
            }
        });
    }

    public void readAppPanelFromProperties() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                AppPanel appPanel = GUI.getAppPanel();

                UserSettings.INSTANCE.getSettings().applySettings(appPanel,
                        getAppPanelSettingsHints());
                appPanel.setEnabledIptcTab(
                    UserSettings.INSTANCE.isDisplayIptc());
                setInitKeywordsView(appPanel);
                selectFastSearch(appPanel);
            }
        });
    }

    private static SettingsHints getAppPanelSettingsHints() {
        SettingsHints hints = new SettingsHints();

        // Lists set by readList...() / writeListProperties() or other classes
        hints.addKeyToExclude(
            AppPersistenceKeys.APP_PANEL_LIST_IMAGE_COLLECTIONS);
        hints.addKeyToExclude(AppPersistenceKeys.APP_PANEL_LIST_NO_METADATA);
        hints.addKeyToExclude(AppPersistenceKeys.APP_PANEL_LIST_SAVED_SEARCHES);
        hints.addKeyToExclude(AppPersistenceKeys.APP_PANEL_LIST_SEL_KEYWORDS);

        // Trees set by readTree...() / writeTreeProperties() or other classes
        hints.addKeyToExclude(AppPersistenceKeys.APP_PANEL_TREE_DIRECTORIES);
        hints.addKeyToExclude(AppPersistenceKeys.APP_PANEL_TREE_EDIT_KEYWORDS);
        hints.addKeyToExclude(AppPersistenceKeys.APP_PANEL_TREE_FAVORITES);
        hints.addKeyToExclude(AppPersistenceKeys.APP_PANEL_TREE_MISC_METADATA);
        hints.addKeyToExclude(AppPersistenceKeys.APP_PANEL_TREE_SEL_KEYWORDS);
        hints.addKeyToExclude(AppPersistenceKeys.APP_PANEL_TREE_TIMELINE);

        return hints;
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

        // Strings has to be equal to the card names in AppPanel
        // (errors on renamings)!
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
        AppPanel appPanel = GUI.getAppPanel();
        Settings settings = UserSettings.INSTANCE.getSettings();

        settings.set(appPanel, getAppPanelSettingsHints());
        settings.set(appPanel.getSplitPaneMain().getDividerLocation(),
                     KEY_DIVIDER_LOCATION_MAIN);
        settings.set(
            appPanel.getSplitPaneThumbnailsMetadata().getDividerLocation(),
            KEY_DIVIDER_LOCATION_THUMBNAILS);
        appPanel.getPanelEditKeywords().writeProperties();

        // Later than settings.set(appPanel, null)!
        writeTreeProperties(appPanel);
        writeListProperties(appPanel);
        UserSettings.INSTANCE.writeToFile();
    }

    /**
     * To after the model has been created.
     */
    public static void readTreeSelKeywords() {
        read(GUI.getSelKeywordsTree(),
             AppPersistenceKeys.APP_PANEL_TREE_SEL_KEYWORDS);
    }

    /**
     * To after the model has been created.
     */
    public static void readTreeEditKeywords() {
        read(GUI.getEditKeywordsTree(),
             AppPersistenceKeys.APP_PANEL_TREE_EDIT_KEYWORDS);
    }

    /**
     * To after the model has been created.
     */
    public static void readTreeMiscMetadata() {
        read(GUI.getMiscMetadataTree(),
             AppPersistenceKeys.APP_PANEL_TREE_MISC_METADATA);
    }

    /**
     * To after the model has been created.
     */
    public static void readTreeTimeline() {
        read(GUI.getTimelineTree(), AppPersistenceKeys.APP_PANEL_TREE_TIMELINE);
    }

    /**
     * To after the model has been created.
     */
    public static void readTreeDirectories() {
        read(GUI.getDirectoriesTree(),
             AppPersistenceKeys.APP_PANEL_TREE_DIRECTORIES);
    }

    /**
     * To after the model has been created.
     */
    public static void readListSavedSearches() {
        read(GUI.getSavedSearchesList(),
             AppPersistenceKeys.APP_PANEL_LIST_SAVED_SEARCHES);
    }

    /**
     * To after the model has been created.
     */
    public static void readListImageCollections() {
        read(GUI.getImageCollectionsList(),
             AppPersistenceKeys.APP_PANEL_LIST_IMAGE_COLLECTIONS);
    }

    /**
     * To after the model has been created.
     */
    public static void readListSelKeywords() {
        read(GUI.getSelKeywordsList(),
             AppPersistenceKeys.APP_PANEL_LIST_SEL_KEYWORDS);
    }

    /**
     * To after the model has been created.
     */
    public static void readListNoMetadata() {
        read(GUI.getNoMetadataList(),
             AppPersistenceKeys.APP_PANEL_LIST_NO_METADATA);
    }

    private static void read(JTree tree, String key) {
        UserSettings.INSTANCE.getSettings().applySettings(tree, key);
    }

    private static void read(JList list, String key) {
        UserSettings.INSTANCE.getSettings().applySelectedIndices(list, key);
    }

    // Independent from renamings
    private void writeTreeProperties(AppPanel appPanel) {
        write(appPanel.getTreeSelKeywords(),
              AppPersistenceKeys.APP_PANEL_TREE_SEL_KEYWORDS);
        write(appPanel.getTreeEditKeywords(),
              AppPersistenceKeys.APP_PANEL_TREE_EDIT_KEYWORDS);
        write(appPanel.getTreeMiscMetadata(),
              AppPersistenceKeys.APP_PANEL_TREE_MISC_METADATA);
        write(appPanel.getTreeTimeline(),
              AppPersistenceKeys.APP_PANEL_TREE_TIMELINE);
        write(appPanel.getTreeDirectories(),
              AppPersistenceKeys.APP_PANEL_TREE_DIRECTORIES);
        write(appPanel.getTreeFavorites(),
              AppPersistenceKeys.APP_PANEL_TREE_FAVORITES);
    }

    // Independent from renamings
    private void writeListProperties(AppPanel appPanel) {
        write(appPanel.getListSavedSearches(),
              AppPersistenceKeys.APP_PANEL_LIST_SAVED_SEARCHES);
        write(appPanel.getListImageCollections(),
              AppPersistenceKeys.APP_PANEL_LIST_IMAGE_COLLECTIONS);
        write(appPanel.getListSelKeywords(),
              AppPersistenceKeys.APP_PANEL_LIST_SEL_KEYWORDS);
        write(appPanel.getListNoMetadata(),
              AppPersistenceKeys.APP_PANEL_LIST_NO_METADATA);
    }

    private void write(JTree tree, String key) {
        UserSettings.INSTANCE.getSettings().set(tree, key);
    }

    private void write(JList list, String key) {
        UserSettings.INSTANCE.getSettings().setSelectedIndices(list, key);
    }

    @Override
    public void applySettings(UserSettingsEvent evt) {
        if (evt.getType().equals(UserSettingsEvent.Type.DISPLAY_IPTC)) {
            boolean displayIptc = UserSettings.INSTANCE.isDisplayIptc();

            setEnabledIptcTab(displayIptc);

            if (displayIptc) {
                displayIptc();
            }
        }
    }

    private void setEnabledIptcTab(final boolean displayIptc) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                GUI.getAppPanel().setEnabledIptcTab(displayIptc);
            }
        });
    }

    private void displayIptc() {
        AppPanel        appPanel = GUI.getAppPanel();
        ThumbnailsPanel tnPanel  = appPanel.getPanelThumbnails();

        if (tnPanel.getSelectionCount() == 1) {
            final TableModel model = appPanel.getTableIptc().getModel();

            if (model instanceof TableModelIptc) {
                final List<File> selFiles = GUI.getSelectedImageFiles();

                if (selFiles.size() == 1) {
                    final File file = selFiles.get(0);

                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            ((TableModelIptc) model).setFile(file);
                        }
                    });
                }
            }
        }
    }

    @Override
    public void componentResized(ComponentEvent evt) {

        // ignore
    }

    @Override
    public void componentMoved(ComponentEvent evt) {

        // ignore
    }

    @Override
    public void componentHidden(ComponentEvent evt) {

        // ignore
    }
}
