package org.jphototagger.program.app.ui;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu.Separator;
import javax.swing.KeyStroke;

import org.openide.util.Lookup;

import org.jphototagger.api.windows.MainWindowMenuProvider;
import org.jphototagger.api.windows.MenuItemProvider;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.comparator.PositionProviderAscendingComparator;
import org.jphototagger.lib.swing.KeyEventUtil;
import org.jphototagger.lib.swing.util.MenuUtil;
import org.jphototagger.program.app.AppInfo;
import org.jphototagger.program.app.AppLifeCycle;
import org.jphototagger.program.resource.GUI;

/**
 * @author Elmar Baumann
 */
public final class AppFrame extends javax.swing.JFrame {

    private static final long serialVersionUID = 1L;
    private final Map<GoTo, JMenuItem> menuItemOfGoto = new EnumMap<GoTo, JMenuItem>(GoTo.class);
    private final Map<JMenuItem, GoTo> gotoOfMenuItem = new HashMap<JMenuItem, GoTo>();
    private AppPanel appPanel;
    private int lastGotoSelectionItemIndex = 9;
    private int lastGotoEditItemIndex = 13;
    private int lastSelectionGotoItemAcceleratorKeyCode = 0x37;
    private int lastEditGotoItemAcceleratorKeyCode = 0x30;

    private void initGotoMenuItemsMap() {
        menuItemOfGoto.put(GoTo.DIRECTORIES, menuItemGotoDirectories);
        menuItemOfGoto.put(GoTo.EDIT_PANELS, menuItemGotoEdit);
        menuItemOfGoto.put(GoTo.FAST_SEARCH, menuItemGotoFastSearch);
        menuItemOfGoto.put(GoTo.FAVORITES, menuItemGotoFavorites);
        menuItemOfGoto.put(GoTo.KEYWORDS_EDIT, menuItemGotoKeywordsEdit);
        menuItemOfGoto.put(GoTo.IMAGE_COLLECTIONS, menuItemGotoCollections);
        menuItemOfGoto.put(GoTo.KEYWORDS_SEL, menuItemGotoKeywordsSel);
        menuItemOfGoto.put(GoTo.MISC_METADATA, menuItemGotoMiscMetadata);
        menuItemOfGoto.put(GoTo.SAVED_SEARCHES, menuItemGotoSavedSearches);
        menuItemOfGoto.put(GoTo.THUMBNAILS_PANEL, menuItemGotoThumbnailsPanel);
        menuItemOfGoto.put(GoTo.TIMELINE, menuItemGotoTimeline);

        for (GoTo gt : menuItemOfGoto.keySet()) {
            gotoOfMenuItem.put(menuItemOfGoto.get(gt), gt);
        }
    }

    public enum GoTo {
        DIRECTORIES,
        EDIT_PANELS,
        FAST_SEARCH,
        FAVORITES,
        KEYWORDS_EDIT,
        IMAGE_COLLECTIONS,
        KEYWORDS_SEL,
        MISC_METADATA,
        SAVED_SEARCHES,
        THUMBNAILS_PANEL,
        TIMELINE,
    }

    public AppFrame() {
        init();
    }

    private void init() {
        initComponents();
        GUI.setAppFrame(this);
        addAppPanel();
        lookupMenuItems();
        MenuUtil.setMnemonics(menuBar);
        initGotoMenuItemsMap();
        setIconImages(AppLookAndFeel.getAppIcons());
        AppLifeCycle.INSTANCE.started(this);
    }

    public AppPanel getAppPanel() {
        return appPanel;
    }

    private void addAppPanel() {
        appPanel = new AppPanel();
        getContentPane().add(appPanel);
    }

    public JCheckBoxMenuItem getCheckBoxMenuItemKeywordOverlay() {
        return checkBoxMenuItemKeywordOverlay;
    }

    public GoTo getGotoOfMenuItem(JMenuItem item) {
        return gotoOfMenuItem.get(item);
    }

    public JMenuItem getMenuItemOfGoto(GoTo gt) {
        return menuItemOfGoto.get(gt);
    }

    public JMenuItem getMenuItemSettings() {
        return menuItemSettings;
    }

    public JMenuItem getMenuItemSearch() {
        return menuItemSearch;
    }

    public JMenuItem getMenuItemInputHelper() {
        return menuItemInputHelper;
    }

    public JMenuItem getMenuItemExit() {
        return menuItemExit;
    }

    public JMenu getMenuEdit() {
        return menuEdit;
    }

    @Override
    public void setTitle(String title) {
        if (title.equals(AppInfo.APP_NAME)) {
            super.setTitle(AppInfo.APP_NAME);
        } else {
            super.setTitle(title + " - " + AppInfo.APP_NAME);
        }
    }

    private void lookupMenuItems() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                Collection<? extends MainWindowMenuProvider> providers = Lookup.getDefault().lookupAll(MainWindowMenuProvider.class);
                List<MenuItemProvider> editMenuItems = new ArrayList<MenuItemProvider>();
                List<MenuItemProvider> fileMenuItems = new ArrayList<MenuItemProvider>();
                List<MenuItemProvider> gotoMenuItems = new ArrayList<MenuItemProvider>();
                List<MenuItemProvider> toolsMenuItems = new ArrayList<MenuItemProvider>();
                List<MenuItemProvider> viewMenuItems = new ArrayList<MenuItemProvider>();
                List<MenuItemProvider> helpMenuItems = new ArrayList<MenuItemProvider>();
                List<MenuItemProvider> windowMenuItems = new ArrayList<MenuItemProvider>();
                for (MainWindowMenuProvider provider : providers) {
                    editMenuItems.addAll(provider.getEditMenuItems());
                    fileMenuItems.addAll(provider.getFileMenuItems());
                    gotoMenuItems.addAll(provider.getGotoMenuItems());
                    helpMenuItems.addAll(provider.getHelpMenuItems());
                    toolsMenuItems.addAll(provider.getToolsMenuItems());
                    viewMenuItems.addAll(provider.getViewMenuItems());
                    windowMenuItems.addAll(provider.getWindowMenuItems());
                }
                addMenuItems(fileMenuItems, menuFile);
                addMenuItems(editMenuItems, menuEdit);
                addMenuItems(gotoMenuItems, menuGoto);
                addMenuItems(viewMenuItems, menuView);
                addMenuItems(toolsMenuItems, menuTools);
                addMenuItems(windowMenuItems, menuWindow);
                addMenuItems(helpMenuItems, menuHelp);
            }
        });
    }

    private void addMenuItems(List<? extends MenuItemProvider> items, JMenu menu) {
        Collections.sort(items, PositionProviderAscendingComparator.INSTANCE);
        for (MenuItemProvider menuItem : items) {
            addMenuItem(menuItem, menu);
        }
    }

    private void addMenuItem(MenuItemProvider item, JMenu menu) {
        int position = item.getPosition();
        JMenuItem menuItem = item.getMenuItem();
        int itemCount = menu.getItemCount();
        int index = position < 0 || position > itemCount ? itemCount : position;

        if (item.isSeparatorBefore()) {
            menu.add(new Separator(), index);
            index++;
        }

        menu.add(menuItem, index);
    }

    void addGotoMenuItemForSelectionWindow(Action action) {
        JMenuItem item = new JMenuItem(action);
        if (lastSelectionGotoItemAcceleratorKeyCode > 0x30 && lastSelectionGotoItemAcceleratorKeyCode < 0x39) { // 0x30: TN-Panel
            KeyStroke keyStroke = KeyEventUtil.getKeyStrokeMenuShortcut(lastSelectionGotoItemAcceleratorKeyCode + 1);
            item.setAccelerator(keyStroke);
            lastSelectionGotoItemAcceleratorKeyCode++;
        }
        menuGoto.insert(item, lastGotoSelectionItemIndex + 1);
        lastGotoSelectionItemIndex++;
    }

    void addGotoMenuItemForEditWindow(Action action) {
        JMenuItem item = new JMenuItem(action);
        if (lastEditGotoItemAcceleratorKeyCode >= 0x30 && lastEditGotoItemAcceleratorKeyCode < 0x39) {
            KeyStroke keyStroke = KeyStroke.getKeyStroke(lastEditGotoItemAcceleratorKeyCode + 1, InputEvent.ALT_MASK);
            item.setAccelerator(keyStroke);
            lastEditGotoItemAcceleratorKeyCode++;
        }
        menuGoto.insert(item, lastGotoEditItemIndex + 1);
        lastGotoEditItemIndex++;
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    private void initComponents() {//GEN-BEGIN:initComponents

        buttonGroupSort = new javax.swing.ButtonGroup();
        menuBar = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        sep2 = new javax.swing.JPopupMenu.Separator();
        menuExport = new javax.swing.JMenu();
        menuItemExportJptMisc = new javax.swing.JMenuItem();
        menuImport = new javax.swing.JMenu();
        menuItemImportJptMisc = new javax.swing.JMenuItem();
        sep3 = new javax.swing.JPopupMenu.Separator();
        menuItemExit = new javax.swing.JMenuItem();
        menuEdit = new javax.swing.JMenu();
        menuItemSettings = new javax.swing.JMenuItem();
        sep4 = new javax.swing.JPopupMenu.Separator();
        menuItemSearch = new javax.swing.JMenuItem();
        menuView = new javax.swing.JMenu();
        sep15 = new javax.swing.JPopupMenu.Separator();
        checkBoxMenuItemKeywordOverlay = new javax.swing.JCheckBoxMenuItem();
        menuGoto = new javax.swing.JMenu();
        menuItemGotoFastSearch = new javax.swing.JMenuItem();
        menuItemGotoEdit = new javax.swing.JMenuItem();
        sep16 = new javax.swing.JPopupMenu.Separator();
        menuItemGotoDirectories = new javax.swing.JMenuItem();
        menuItemGotoSavedSearches = new javax.swing.JMenuItem();
        menuItemGotoCollections = new javax.swing.JMenuItem();
        menuItemGotoFavorites = new javax.swing.JMenuItem();
        menuItemGotoKeywordsSel = new javax.swing.JMenuItem();
        menuItemGotoTimeline = new javax.swing.JMenuItem();
        menuItemGotoMiscMetadata = new javax.swing.JMenuItem();
        sep17 = new javax.swing.JPopupMenu.Separator();
        menuItemGotoThumbnailsPanel = new javax.swing.JMenuItem();
        sep18 = new javax.swing.JPopupMenu.Separator();
        menuItemGotoKeywordsEdit = new javax.swing.JMenuItem();
        menuTools = new javax.swing.JMenu();
        menuWindow = new javax.swing.JMenu();
        menuItemInputHelper = new javax.swing.JMenuItem();
        menuItemActions = new javax.swing.JMenuItem();
        menuHelp = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(AppInfo.APP_NAME);
        setName("Form"); // NOI18N

        menuBar.setName("menuBar"); // NOI18N

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/app/ui/Bundle"); // NOI18N
        menuFile.setText(bundle.getString("AppFrame.menuFile.text")); // NOI18N
        menuFile.setName("menuFile"); // NOI18N

        sep2.setName("sep2"); // NOI18N
        menuFile.add(sep2);

        menuExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_export.png"))); // NOI18N
        menuExport.setText(bundle.getString("AppFrame.menuExport.text")); // NOI18N
        menuExport.setName("menuExport"); // NOI18N

        menuItemExportJptMisc.setAction(org.jphototagger.program.module.exportimport.exporter.JptExportAction.INSTANCE);
        menuItemExportJptMisc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_app_small.png"))); // NOI18N
        menuItemExportJptMisc.setText(bundle.getString("AppFrame.menuItemExportJptMisc.text")); // NOI18N
        menuItemExportJptMisc.setName("menuItemExportJptMisc"); // NOI18N
        menuExport.add(menuItemExportJptMisc);

        menuFile.add(menuExport);

        menuImport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_import.png"))); // NOI18N
        menuImport.setText(bundle.getString("AppFrame.menuImport.text")); // NOI18N
        menuImport.setName("menuImport"); // NOI18N

        menuItemImportJptMisc.setAction(org.jphototagger.program.module.exportimport.importer.JptImportAction.INSTANCE);
        menuItemImportJptMisc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_app_small.png"))); // NOI18N
        menuItemImportJptMisc.setText(bundle.getString("AppFrame.menuItemImportJptMisc.text")); // NOI18N
        menuItemImportJptMisc.setName("menuItemImportJptMisc"); // NOI18N
        menuImport.add(menuItemImportJptMisc);

        menuFile.add(menuImport);

        sep3.setName("sep3"); // NOI18N
        menuFile.add(sep3);

        menuItemExit.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_Q));
        menuItemExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_exit.png"))); // NOI18N
        menuItemExit.setText(bundle.getString("AppFrame.menuItemExit.text")); // NOI18N
        menuItemExit.setName("menuItemExit"); // NOI18N
        menuFile.add(menuItemExit);

        menuBar.add(menuFile);

        menuEdit.setText(bundle.getString("AppFrame.menuEdit.text")); // NOI18N
        menuEdit.setName("menuEdit"); // NOI18N

        menuItemSettings.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcutWithShiftDown(KeyEvent.VK_S));
        menuItemSettings.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_settings.png"))); // NOI18N
        menuItemSettings.setText(bundle.getString("AppFrame.menuItemSettings.text")); // NOI18N
        menuItemSettings.setName("menuItemSettings"); // NOI18N
        menuEdit.add(menuItemSettings);

        sep4.setName("sep4"); // NOI18N
        menuEdit.add(sep4);

        menuItemSearch.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
        menuItemSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_search.png"))); // NOI18N
        menuItemSearch.setText(bundle.getString("AppFrame.menuItemSearch.text")); // NOI18N
        menuItemSearch.setName("menuItemSearch"); // NOI18N
        menuEdit.add(menuItemSearch);

        menuBar.add(menuEdit);

        menuView.setText(bundle.getString("AppFrame.menuView.text")); // NOI18N
        menuView.setName("menuView"); // NOI18N

        sep15.setName("sep15"); // NOI18N
        menuView.add(sep15);

        checkBoxMenuItemKeywordOverlay.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcutWithShiftDown(KeyEvent.VK_O));
        checkBoxMenuItemKeywordOverlay.setText(bundle.getString("AppFrame.checkBoxMenuItemKeywordOverlay.text")); // NOI18N
        checkBoxMenuItemKeywordOverlay.setToolTipText(bundle.getString("AppFrame.checkBoxMenuItemKeywordOverlay.toolTipText")); // NOI18N
        checkBoxMenuItemKeywordOverlay.setName("checkBoxMenuItemKeywordOverlay"); // NOI18N
        menuView.add(checkBoxMenuItemKeywordOverlay);

        menuBar.add(menuView);

        menuGoto.setText(bundle.getString("AppFrame.menuGoto.text")); // NOI18N
        menuGoto.setName("menuGoto"); // NOI18N

        menuItemGotoFastSearch.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_F));
        menuItemGotoFastSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_search.png"))); // NOI18N
        menuItemGotoFastSearch.setText(bundle.getString("AppFrame.menuItemGotoFastSearch.text")); // NOI18N
        menuItemGotoFastSearch.setName("menuItemGotoFastSearch"); // NOI18N
        menuGoto.add(menuItemGotoFastSearch);

        menuItemGotoEdit.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcutWithShiftDown(KeyEvent.VK_E));
        menuItemGotoEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_edit.png"))); // NOI18N
        menuItemGotoEdit.setText(bundle.getString("AppFrame.menuItemGotoEdit.text")); // NOI18N
        menuItemGotoEdit.setName("menuItemGotoEdit"); // NOI18N
        menuGoto.add(menuItemGotoEdit);

        sep16.setName("sep16"); // NOI18N
        menuGoto.add(sep16);

        menuItemGotoDirectories.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_1));
        menuItemGotoDirectories.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_folder.png"))); // NOI18N
        menuItemGotoDirectories.setText(bundle.getString("AppFrame.menuItemGotoDirectories.text")); // NOI18N
        menuItemGotoDirectories.setName("menuItemGotoDirectories"); // NOI18N
        menuGoto.add(menuItemGotoDirectories);

        menuItemGotoSavedSearches.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_2));
        menuItemGotoSavedSearches.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_search.png"))); // NOI18N
        menuItemGotoSavedSearches.setText(bundle.getString("AppFrame.menuItemGotoSavedSearches.text")); // NOI18N
        menuItemGotoSavedSearches.setName("menuItemGotoSavedSearches"); // NOI18N
        menuGoto.add(menuItemGotoSavedSearches);

        menuItemGotoCollections.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_3));
        menuItemGotoCollections.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_imagecollection.png"))); // NOI18N
        menuItemGotoCollections.setText(bundle.getString("AppFrame.menuItemGotoCollections.text")); // NOI18N
        menuItemGotoCollections.setName("menuItemGotoCollections"); // NOI18N
        menuGoto.add(menuItemGotoCollections);

        menuItemGotoFavorites.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_4));
        menuItemGotoFavorites.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_favorite.png"))); // NOI18N
        menuItemGotoFavorites.setText(bundle.getString("AppFrame.menuItemGotoFavorites.text")); // NOI18N
        menuItemGotoFavorites.setName("menuItemGotoFavorites"); // NOI18N
        menuGoto.add(menuItemGotoFavorites);

        menuItemGotoKeywordsSel.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_5));
        menuItemGotoKeywordsSel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_keyword.png"))); // NOI18N
        menuItemGotoKeywordsSel.setText(bundle.getString("AppFrame.menuItemGotoKeywordsSel.text")); // NOI18N
        menuItemGotoKeywordsSel.setName("menuItemGotoKeywordsSel"); // NOI18N
        menuGoto.add(menuItemGotoKeywordsSel);

        menuItemGotoTimeline.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_6));
        menuItemGotoTimeline.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_timeline.png"))); // NOI18N
        menuItemGotoTimeline.setText(bundle.getString("AppFrame.menuItemGotoTimeline.text")); // NOI18N
        menuItemGotoTimeline.setName("menuItemGotoTimeline"); // NOI18N
        menuGoto.add(menuItemGotoTimeline);

        menuItemGotoMiscMetadata.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_7));
        menuItemGotoMiscMetadata.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_misc_metadata.png"))); // NOI18N
        menuItemGotoMiscMetadata.setText(bundle.getString("AppFrame.menuItemGotoMiscMetadata.text")); // NOI18N
        menuItemGotoMiscMetadata.setName("menuItemGotoMiscMetadata"); // NOI18N
        menuGoto.add(menuItemGotoMiscMetadata);

        sep17.setName("sep17"); // NOI18N
        menuGoto.add(sep17);

        menuItemGotoThumbnailsPanel.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_0));
        menuItemGotoThumbnailsPanel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_thumbnails.png"))); // NOI18N
        menuItemGotoThumbnailsPanel.setText(bundle.getString("AppFrame.menuItemGotoThumbnailsPanel.text")); // NOI18N
        menuItemGotoThumbnailsPanel.setName("menuItemGotoThumbnailsPanel"); // NOI18N
        menuGoto.add(menuItemGotoThumbnailsPanel);

        sep18.setName("sep18"); // NOI18N
        menuGoto.add(sep18);

        menuItemGotoKeywordsEdit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_4, java.awt.event.InputEvent.ALT_MASK));
        menuItemGotoKeywordsEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_keyword.png"))); // NOI18N
        menuItemGotoKeywordsEdit.setText(bundle.getString("AppFrame.menuItemGotoKeywordsEdit.text")); // NOI18N
        menuItemGotoKeywordsEdit.setName("menuItemGotoKeywordsEdit"); // NOI18N
        menuGoto.add(menuItemGotoKeywordsEdit);

        menuBar.add(menuGoto);

        menuTools.setText(bundle.getString("AppFrame.menuTools.text")); // NOI18N
        menuTools.setName("menuTools"); // NOI18N
        menuBar.add(menuTools);

        menuWindow.setText(bundle.getString("AppFrame.menuWindow.text")); // NOI18N
        menuWindow.setName("menuWindow"); // NOI18N

        menuItemInputHelper.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F9, 0));
        menuItemInputHelper.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_edit.png"))); // NOI18N
        menuItemInputHelper.setText(bundle.getString("AppFrame.menuItemInputHelper.text")); // NOI18N
        menuItemInputHelper.setName("menuItemInputHelper"); // NOI18N
        menuWindow.add(menuItemInputHelper);

        menuItemActions.setAction(new org.jphototagger.program.module.actions.ShowActionsDialogAction());
        menuItemActions.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_action.png"))); // NOI18N
        menuItemActions.setText(bundle.getString("AppFrame.menuItemActions.text")); // NOI18N
        menuItemActions.setName("menuItemActions"); // NOI18N
        menuWindow.add(menuItemActions);

        menuBar.add(menuWindow);

        menuHelp.setText(bundle.getString("AppFrame.menuHelp.text")); // NOI18N
        menuHelp.setName("menuHelp"); // NOI18N
        menuBar.add(menuHelp);

        setJMenuBar(menuBar);
    }//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupSort;
    private javax.swing.JCheckBoxMenuItem checkBoxMenuItemKeywordOverlay;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu menuEdit;
    private javax.swing.JMenu menuExport;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenu menuGoto;
    private javax.swing.JMenu menuHelp;
    private javax.swing.JMenu menuImport;
    private javax.swing.JMenuItem menuItemActions;
    private javax.swing.JMenuItem menuItemExit;
    private javax.swing.JMenuItem menuItemExportJptMisc;
    private javax.swing.JMenuItem menuItemGotoCollections;
    private javax.swing.JMenuItem menuItemGotoDirectories;
    private javax.swing.JMenuItem menuItemGotoEdit;
    private javax.swing.JMenuItem menuItemGotoFastSearch;
    private javax.swing.JMenuItem menuItemGotoFavorites;
    private javax.swing.JMenuItem menuItemGotoKeywordsEdit;
    private javax.swing.JMenuItem menuItemGotoKeywordsSel;
    private javax.swing.JMenuItem menuItemGotoMiscMetadata;
    private javax.swing.JMenuItem menuItemGotoSavedSearches;
    private javax.swing.JMenuItem menuItemGotoThumbnailsPanel;
    private javax.swing.JMenuItem menuItemGotoTimeline;
    private javax.swing.JMenuItem menuItemImportJptMisc;
    private javax.swing.JMenuItem menuItemInputHelper;
    private javax.swing.JMenuItem menuItemSearch;
    private javax.swing.JMenuItem menuItemSettings;
    private javax.swing.JMenu menuTools;
    private javax.swing.JMenu menuView;
    private javax.swing.JMenu menuWindow;
    private javax.swing.JPopupMenu.Separator sep15;
    private javax.swing.JPopupMenu.Separator sep16;
    private javax.swing.JPopupMenu.Separator sep17;
    private javax.swing.JPopupMenu.Separator sep18;
    private javax.swing.JPopupMenu.Separator sep2;
    private javax.swing.JPopupMenu.Separator sep3;
    private javax.swing.JPopupMenu.Separator sep4;
    // End of variables declaration//GEN-END:variables
}
