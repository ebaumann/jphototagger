package org.jphototagger.program.app.ui;

import java.awt.event.KeyEvent;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu.Separator;

import org.jphototagger.api.windows.MainWindowMenuItem;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.swing.KeyEventUtil;
import org.jphototagger.lib.swing.util.MenuUtil;
import org.jphototagger.lib.util.SystemUtil;
import org.jphototagger.program.app.AppInfo;
import org.jphototagger.program.app.AppLifeCycle;
import org.jphototagger.program.module.userdefinedfiletypes.EditUserDefinedFileTypesAction;
import org.jphototagger.program.resource.GUI;

/**
 * The application's frame.
 *
 * @author Elmar Baumann
 */
public final class AppFrame extends javax.swing.JFrame {

    private static final long serialVersionUID = 1L;
    private final Map<GoTo, JMenuItem> menuItemOfGoto = new EnumMap<GoTo, JMenuItem>(GoTo.class);
    private final Map<JMenuItem, GoTo> gotoOfMenuItem = new HashMap<JMenuItem, GoTo>();
    private AppPanel appPanel;

    private void initGotoMenuItemsMap() {
        menuItemOfGoto.put(GoTo.DIRECTORIES, menuItemGotoDirectories);
        menuItemOfGoto.put(GoTo.EDIT_PANELS, menuItemGotoEdit);
        menuItemOfGoto.put(GoTo.EXIF_METADATA, menuItemGotoExifMetadata);
        menuItemOfGoto.put(GoTo.FAST_SEARCH, menuItemGotoFastSearch);
        menuItemOfGoto.put(GoTo.FAVORITES, menuItemGotoFavorites);
        menuItemOfGoto.put(GoTo.KEYWORDS_EDIT, menuItemGotoKeywordsEdit);
        menuItemOfGoto.put(GoTo.IMAGE_COLLECTIONS, menuItemGotoCollections);
        menuItemOfGoto.put(GoTo.IPTC_METADATA, menuItemGotoIptcMetadata);
        menuItemOfGoto.put(GoTo.KEYWORDS_SEL, menuItemGotoKeywordsSel);
        menuItemOfGoto.put(GoTo.MISC_METADATA, menuItemGotoMiscMetadata);
        menuItemOfGoto.put(GoTo.NO_METADATA, menuItemGotoNoMetadata);
        menuItemOfGoto.put(GoTo.SAVED_SEARCHES, menuItemGotoSavedSearches);
        menuItemOfGoto.put(GoTo.THUMBNAILS_PANEL, menuItemGotoThumbnailsPanel);
        menuItemOfGoto.put(GoTo.TIMELINE, menuItemGotoTimeline);
        menuItemOfGoto.put(GoTo.XMP_METADATA, menuItemGotoXmpMetadata);

        for (GoTo gt : menuItemOfGoto.keySet()) {
            gotoOfMenuItem.put(menuItemOfGoto.get(gt), gt);
        }
    }

    public enum GoTo {
        DIRECTORIES,
        EDIT_PANELS,
        EXIF_METADATA,
        FAST_SEARCH,
        FAVORITES,
        KEYWORDS_EDIT,
        IMAGE_COLLECTIONS,
        IPTC_METADATA,
        KEYWORDS_SEL,
        MISC_METADATA,
        SAVED_SEARCHES,
        THUMBNAILS_PANEL,
        TIMELINE,
        XMP_METADATA,
        NO_METADATA,
    }

    public AppFrame() {
        init();
    }

    private void init() {
        initComponents();
        GUI.setAppFrame(this);
        addAppPanel();
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

    public JMenuItem getMenuItemExtractEmbeddedXmp() {
        return menuItemExtractEmbeddedXmp;
    }

    public JMenuItem getMenuItemImportImageFiles() {
        return menuImportImageFiles;
    }

    public JMenuItem getMenuItemOfGoto(GoTo gt) {
        return menuItemOfGoto.get(gt);
    }

    public JMenuItem getMenuItemAbout() {
        return menuItemAbout;
    }

    public JMenuItem getMenuItemHelp() {
        return menuItemHelp;
    }

    public JMenuItem getMenuItemOpenPdfUserManual() {
        return menuItemOpenPdfUserManual;
    }

    public JMenuItem getMenuItemMaintainRepository() {
        return menuItemMaintainRepository;
    }

    public JMenuItem getMenuItemScanDirectory() {
        return menuItemScanDirectory;
    }

    public JMenuItem getMenuItemSettings() {
        return menuItemSettings;
    }

    public JMenuItem getMenuItemSearch() {
        return menuItemSearch;
    }

    public JMenuItem getMenuItemToolIptcToXmp() {
        return menuItemToolIptcToXmp;
    }

    public JMenuItem getMenuItemInputHelper() {
        return menuItemInputHelper;
    }

    public JMenuItem getMenuItemExit() {
        return menuItemExit;
    }

    public JMenuItem getMenuItemAcceleratorKeys() {
        return menuItemAcceleratorKeys;
    }

    public JMenuItem getMenuItemDisplayLogfile() {
        return menuItemDisplayLogfile;
    }

    public JMenuItem getMenuItemDisplayAllLogfile() {
        return menuItemDisplayAllLogfile;
    }

    public JMenuItem getMenuItemSynonyms() {
        return menuItemSynonyms;
    }

    public JMenu getMenuEdit() {
        return menuEdit;
    }

    public JMenuItem getMenuItemSendBugMail() {
        return menuItemSendBugMail;
    }

    public JMenuItem getMenuItemSendFeatureMail() {
        return menuItemSendFeatureMail;
    }

    public JMenuItem getMenuItemBrowseUserForum() {
        return menuItemBrowseUserForum;
    }

    public JMenuItem getMenuItemBrowseChangelog() {
        return menuItemBrowseChangelog;
    }

    public JMenuItem getMenuItemBrowseWebsite() {
        return menuItemBrowseWebsite;
    }

    public JMenuItem getMenuItemUserDefinedFileFilter() {
        return menuItemUserDefinedFileFilter;
    }

    public JMenuItem getMenuItemCheckForUpdates() {
        return menuItemCheckForUpdates;
    }

    @Override
    public void setTitle(String title) {
        if (title.equals(AppInfo.APP_NAME)) {
            super.setTitle(AppInfo.APP_NAME);
        } else {
            super.setTitle(title + " - " + AppInfo.APP_NAME);
        }
    }

    void addToFileMenu(MainWindowMenuItem appMenuAction) {
        addToMenu(appMenuAction, menuFile);
    }

    void addToEditMenu(MainWindowMenuItem appMenuAction) {
        addToMenu(appMenuAction, menuEdit);
    }

    void addToViewMenu(MainWindowMenuItem appMenuAction) {
        addToMenu(appMenuAction, menuView);
    }

    void addToGotoMenu(MainWindowMenuItem appMenuAction) {
        addToMenu(appMenuAction, menuGoto);
    }

    void addToToolsMenu(MainWindowMenuItem appMenuAction) {
        addToMenu(appMenuAction, menuTools);
    }

    void addToWindowMenu(MainWindowMenuItem appMenuAction) {
        addToMenu(appMenuAction, menuWindow);
    }

    void addToHelpMenu(MainWindowMenuItem appMenuAction) {
        addToMenu(appMenuAction, menuHelp);
    }

    private void addToMenu(final MainWindowMenuItem appMenuAction, final JMenu menu) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                int position = appMenuAction.getPosition();
                JMenuItem menuItem = appMenuAction.getMenuItem();
                int itemCount = menu.getItemCount();
                int index = position < 0 || position > itemCount ? itemCount : position;

                if (appMenuAction.isSeparatorBefore()) {
                    menu.add(new Separator(), index);
                    index++;
                }

                menu.add(menuItem, index);

            }
        });
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
        menuItemScanDirectory = new javax.swing.JMenuItem();
        menuItemMaintainRepository = new javax.swing.JMenuItem();
        menuItemMaintainance = new JMenuItem(org.jphototagger.program.module.maintainance.DisplayMaintainanceDialogAction.INSTANCE);
        sep1 = new javax.swing.JPopupMenu.Separator();
        menuImportImageFiles = new javax.swing.JMenuItem();
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
        menuItemGotoNoMetadata = new javax.swing.JMenuItem();
        sep17 = new javax.swing.JPopupMenu.Separator();
        menuItemGotoThumbnailsPanel = new javax.swing.JMenuItem();
        sep18 = new javax.swing.JPopupMenu.Separator();
        menuItemGotoIptcMetadata = new javax.swing.JMenuItem();
        menuItemGotoExifMetadata = new javax.swing.JMenuItem();
        menuItemGotoXmpMetadata = new javax.swing.JMenuItem();
        menuItemGotoKeywordsEdit = new javax.swing.JMenuItem();
        menuTools = new javax.swing.JMenu();
        menuItemToolIptcToXmp = new javax.swing.JMenuItem();
        menuItemExtractEmbeddedXmp = new javax.swing.JMenuItem();
        menuWindow = new javax.swing.JMenu();
        menuItemInputHelper = new javax.swing.JMenuItem();
        menuItemActions = new javax.swing.JMenuItem();
        menuItemSynonyms = new javax.swing.JMenuItem();
        menuItemUserDefinedFileFilter = new javax.swing.JMenuItem();
        menuItemUserDefinedFileType = new JMenuItem(new EditUserDefinedFileTypesAction());
        sep20 = new javax.swing.JPopupMenu.Separator();
        menuItemDisplayLogfile = new javax.swing.JMenuItem();
        menuItemDisplayAllLogfile = new javax.swing.JMenuItem();
        menuHelp = new javax.swing.JMenu();
        menuItemHelp = new javax.swing.JMenuItem();
        menuItemOpenPdfUserManual = new javax.swing.JMenuItem();
        menuItemAcceleratorKeys = new javax.swing.JMenuItem();
        sep21 = new javax.swing.JPopupMenu.Separator();
        menuItemBrowseUserForum = new javax.swing.JMenuItem();
        menuItemBrowseWebsite = new javax.swing.JMenuItem();
        menuItemBrowseChangelog = new javax.swing.JMenuItem();
        sep22 = new javax.swing.JPopupMenu.Separator();
        menuItemSendBugMail = new javax.swing.JMenuItem();
        menuItemSendFeatureMail = new javax.swing.JMenuItem();
        sep23 = new javax.swing.JPopupMenu.Separator();
        menuItemCheckForUpdates = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        menuItemAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(AppInfo.APP_NAME);
        setName("Form"); // NOI18N

        menuBar.setName("menuBar"); // NOI18N

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/app/ui/Bundle"); // NOI18N
        menuFile.setText(bundle.getString("AppFrame.menuFile.text")); // NOI18N
        menuFile.setName("menuFile"); // NOI18N

        menuItemScanDirectory.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcutWithShiftDown(KeyEvent.VK_R));
        menuItemScanDirectory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_folder.png"))); // NOI18N
        menuItemScanDirectory.setText(bundle.getString("AppFrame.menuItemScanDirectory.text")); // NOI18N
        menuItemScanDirectory.setName("menuItemScanDirectory"); // NOI18N
        menuFile.add(menuItemScanDirectory);

        menuItemMaintainRepository.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcutWithShiftDown(KeyEvent.VK_D));
        menuItemMaintainRepository.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_database.png"))); // NOI18N
        menuItemMaintainRepository.setText(bundle.getString("AppFrame.menuItemMaintainRepository.text")); // NOI18N
        menuItemMaintainRepository.setName("menuItemMaintainRepository"); // NOI18N
        menuFile.add(menuItemMaintainRepository);

        menuItemMaintainance.setText(bundle.getString("AppFrame.menuItemMaintainance.text")); // NOI18N
        menuItemMaintainance.setName("menuItemMaintainance"); // NOI18N
        menuFile.add(menuItemMaintainance);

        sep1.setName("sep1"); // NOI18N
        menuFile.add(sep1);

        menuImportImageFiles.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcutWithShiftDown(KeyEvent.VK_P));
        menuImportImageFiles.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_card.png"))); // NOI18N
        menuImportImageFiles.setText(bundle.getString("AppFrame.menuImportImageFiles.text")); // NOI18N
        menuImportImageFiles.setName("menuImportImageFiles"); // NOI18N
        menuFile.add(menuImportImageFiles);

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

        menuItemGotoNoMetadata.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_8));
        menuItemGotoNoMetadata.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_no_metadata.png"))); // NOI18N
        menuItemGotoNoMetadata.setText(bundle.getString("AppFrame.menuItemGotoNoMetadata.text")); // NOI18N
        menuItemGotoNoMetadata.setName("menuItemGotoNoMetadata"); // NOI18N
        menuGoto.add(menuItemGotoNoMetadata);

        sep17.setName("sep17"); // NOI18N
        menuGoto.add(sep17);

        menuItemGotoThumbnailsPanel.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_0));
        menuItemGotoThumbnailsPanel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_thumbnails.png"))); // NOI18N
        menuItemGotoThumbnailsPanel.setText(bundle.getString("AppFrame.menuItemGotoThumbnailsPanel.text")); // NOI18N
        menuItemGotoThumbnailsPanel.setName("menuItemGotoThumbnailsPanel"); // NOI18N
        menuGoto.add(menuItemGotoThumbnailsPanel);

        sep18.setName("sep18"); // NOI18N
        menuGoto.add(sep18);

        menuItemGotoIptcMetadata.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_1, java.awt.event.InputEvent.ALT_MASK));
        menuItemGotoIptcMetadata.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_iptc.png"))); // NOI18N
        menuItemGotoIptcMetadata.setText(bundle.getString("AppFrame.menuItemGotoIptcMetadata.text")); // NOI18N
        menuItemGotoIptcMetadata.setName("menuItemGotoIptcMetadata"); // NOI18N
        menuGoto.add(menuItemGotoIptcMetadata);

        menuItemGotoExifMetadata.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_2, java.awt.event.InputEvent.ALT_MASK));
        menuItemGotoExifMetadata.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_exif.png"))); // NOI18N
        menuItemGotoExifMetadata.setText(bundle.getString("AppFrame.menuItemGotoExifMetadata.text")); // NOI18N
        menuItemGotoExifMetadata.setName("menuItemGotoExifMetadata"); // NOI18N
        menuGoto.add(menuItemGotoExifMetadata);

        menuItemGotoXmpMetadata.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_3, java.awt.event.InputEvent.ALT_MASK));
        menuItemGotoXmpMetadata.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_xmp.png"))); // NOI18N
        menuItemGotoXmpMetadata.setText(bundle.getString("AppFrame.menuItemGotoXmpMetadata.text")); // NOI18N
        menuItemGotoXmpMetadata.setName("menuItemGotoXmpMetadata"); // NOI18N
        menuGoto.add(menuItemGotoXmpMetadata);

        menuItemGotoKeywordsEdit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_4, java.awt.event.InputEvent.ALT_MASK));
        menuItemGotoKeywordsEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_keyword.png"))); // NOI18N
        menuItemGotoKeywordsEdit.setText(bundle.getString("AppFrame.menuItemGotoKeywordsEdit.text")); // NOI18N
        menuItemGotoKeywordsEdit.setName("menuItemGotoKeywordsEdit"); // NOI18N
        menuGoto.add(menuItemGotoKeywordsEdit);

        menuBar.add(menuGoto);

        menuTools.setText(bundle.getString("AppFrame.menuTools.text")); // NOI18N
        menuTools.setName("menuTools"); // NOI18N

        menuItemToolIptcToXmp.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcutWithShiftDown(KeyEvent.VK_I));
        menuItemToolIptcToXmp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_iptc.png"))); // NOI18N
        menuItemToolIptcToXmp.setText(bundle.getString("AppFrame.menuItemToolIptcToXmp.text")); // NOI18N
        menuItemToolIptcToXmp.setName("menuItemToolIptcToXmp"); // NOI18N
        menuTools.add(menuItemToolIptcToXmp);

        menuItemExtractEmbeddedXmp.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcutWithShiftDown(KeyEvent.VK_X));
        menuItemExtractEmbeddedXmp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_xmp.png"))); // NOI18N
        menuItemExtractEmbeddedXmp.setText(bundle.getString("AppFrame.menuItemExtractEmbeddedXmp.text")); // NOI18N
        menuItemExtractEmbeddedXmp.setName("menuItemExtractEmbeddedXmp"); // NOI18N
        menuTools.add(menuItemExtractEmbeddedXmp);

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

        menuItemSynonyms.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F7, 0));
        menuItemSynonyms.setText(bundle.getString("AppFrame.menuItemSynonyms.text")); // NOI18N
        menuItemSynonyms.setName("menuItemSynonyms"); // NOI18N
        menuWindow.add(menuItemSynonyms);

        menuItemUserDefinedFileFilter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_filter.png"))); // NOI18N
        menuItemUserDefinedFileFilter.setText(bundle.getString("AppFrame.menuItemUserDefinedFileFilter.text")); // NOI18N
        menuItemUserDefinedFileFilter.setName("menuItemUserDefinedFileFilter"); // NOI18N
        menuWindow.add(menuItemUserDefinedFileFilter);

        menuItemUserDefinedFileType.setText(bundle.getString("AppFrame.menuItemUserDefinedFileType.text")); // NOI18N
        menuItemUserDefinedFileType.setName("menuItemUserDefinedFileType"); // NOI18N
        menuWindow.add(menuItemUserDefinedFileType);

        sep20.setName("sep20"); // NOI18N
        menuWindow.add(sep20);

        menuItemDisplayLogfile.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcutWithShiftDown(KeyEvent.VK_L));
        menuItemDisplayLogfile.setText(bundle.getString("AppFrame.menuItemDisplayLogfile.text")); // NOI18N
        menuItemDisplayLogfile.setEnabled(false);
        menuItemDisplayLogfile.setName("menuItemDisplayLogfile"); // NOI18N
        menuWindow.add(menuItemDisplayLogfile);

        menuItemDisplayAllLogfile.setText(bundle.getString("AppFrame.menuItemDisplayAllLogfile.text")); // NOI18N
        menuItemDisplayAllLogfile.setName("menuItemDisplayAllLogfile"); // NOI18N
        menuWindow.add(menuItemDisplayAllLogfile);

        menuBar.add(menuWindow);

        menuHelp.setText(bundle.getString("AppFrame.menuHelp.text")); // NOI18N
        menuHelp.setName("menuHelp"); // NOI18N

        menuItemHelp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        menuItemHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_help.png"))); // NOI18N
        menuItemHelp.setText(bundle.getString("AppFrame.menuItemHelp.text")); // NOI18N
        menuItemHelp.setName("menuItemHelp"); // NOI18N
        menuHelp.add(menuItemHelp);

        menuItemOpenPdfUserManual.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_pdf_manual.png"))); // NOI18N
        menuItemOpenPdfUserManual.setText(bundle.getString("AppFrame.menuItemOpenPdfUserManual.text")); // NOI18N
        menuItemOpenPdfUserManual.setEnabled(SystemUtil.canOpen());
        menuItemOpenPdfUserManual.setName("menuItemOpenPdfUserManual"); // NOI18N
        menuHelp.add(menuItemOpenPdfUserManual);

        menuItemAcceleratorKeys.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_keyboard.png"))); // NOI18N
        menuItemAcceleratorKeys.setText(bundle.getString("AppFrame.menuItemAcceleratorKeys.text")); // NOI18N
        menuItemAcceleratorKeys.setName("menuItemAcceleratorKeys"); // NOI18N
        menuHelp.add(menuItemAcceleratorKeys);

        sep21.setName("sep21"); // NOI18N
        menuHelp.add(sep21);

        menuItemBrowseUserForum.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_web.png"))); // NOI18N
        menuItemBrowseUserForum.setText(bundle.getString("AppFrame.menuItemBrowseUserForum.text")); // NOI18N
        menuItemBrowseUserForum.setEnabled(SystemUtil.canBrowse());
        menuItemBrowseUserForum.setName("menuItemBrowseUserForum"); // NOI18N
        menuHelp.add(menuItemBrowseUserForum);

        menuItemBrowseWebsite.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_web.png"))); // NOI18N
        menuItemBrowseWebsite.setText(bundle.getString("AppFrame.menuItemBrowseWebsite.text")); // NOI18N
        menuItemBrowseWebsite.setEnabled(SystemUtil.canBrowse());
        menuItemBrowseWebsite.setName("menuItemBrowseWebsite"); // NOI18N
        menuHelp.add(menuItemBrowseWebsite);

        menuItemBrowseChangelog.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_web.png"))); // NOI18N
        menuItemBrowseChangelog.setText(bundle.getString("AppFrame.menuItemBrowseChangelog.text")); // NOI18N
        menuItemBrowseChangelog.setEnabled(SystemUtil.canBrowse());
        menuItemBrowseChangelog.setName("menuItemBrowseChangelog"); // NOI18N
        menuHelp.add(menuItemBrowseChangelog);

        sep22.setName("sep22"); // NOI18N
        menuHelp.add(sep22);

        menuItemSendBugMail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_mail.png"))); // NOI18N
        menuItemSendBugMail.setText(bundle.getString("AppFrame.menuItemSendBugMail.text")); // NOI18N
        menuItemSendBugMail.setEnabled(SystemUtil.canMail());
        menuItemSendBugMail.setName("menuItemSendBugMail"); // NOI18N
        menuHelp.add(menuItemSendBugMail);

        menuItemSendFeatureMail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_mail.png"))); // NOI18N
        menuItemSendFeatureMail.setText(bundle.getString("AppFrame.menuItemSendFeatureMail.text")); // NOI18N
        menuItemSendFeatureMail.setEnabled(SystemUtil.canMail());
        menuItemSendFeatureMail.setName("menuItemSendFeatureMail"); // NOI18N
        menuHelp.add(menuItemSendFeatureMail);

        sep23.setName("sep23"); // NOI18N
        menuHelp.add(sep23);

        menuItemCheckForUpdates.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_refresh.png"))); // NOI18N
        menuItemCheckForUpdates.setText(bundle.getString("AppFrame.menuItemCheckForUpdates.text")); // NOI18N
        menuItemCheckForUpdates.setName("menuItemCheckForUpdates"); // NOI18N
        menuHelp.add(menuItemCheckForUpdates);

        jSeparator1.setName("jSeparator1"); // NOI18N
        menuHelp.add(jSeparator1);

        menuItemAbout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_app_about.png"))); // NOI18N
        menuItemAbout.setText(bundle.getString("AppFrame.menuItemAbout.text")); // NOI18N
        menuItemAbout.setName("menuItemAbout"); // NOI18N
        menuHelp.add(menuItemAbout);

        menuBar.add(menuHelp);

        setJMenuBar(menuBar);
    }//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupSort;
    private javax.swing.JCheckBoxMenuItem checkBoxMenuItemKeywordOverlay;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu menuEdit;
    private javax.swing.JMenu menuExport;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenu menuGoto;
    private javax.swing.JMenu menuHelp;
    private javax.swing.JMenu menuImport;
    private javax.swing.JMenuItem menuImportImageFiles;
    private javax.swing.JMenuItem menuItemAbout;
    private javax.swing.JMenuItem menuItemAcceleratorKeys;
    private javax.swing.JMenuItem menuItemActions;
    private javax.swing.JMenuItem menuItemBrowseChangelog;
    private javax.swing.JMenuItem menuItemBrowseUserForum;
    private javax.swing.JMenuItem menuItemBrowseWebsite;
    private javax.swing.JMenuItem menuItemCheckForUpdates;
    private javax.swing.JMenuItem menuItemDisplayAllLogfile;
    private javax.swing.JMenuItem menuItemDisplayLogfile;
    private javax.swing.JMenuItem menuItemExit;
    private javax.swing.JMenuItem menuItemExportJptMisc;
    private javax.swing.JMenuItem menuItemExtractEmbeddedXmp;
    private javax.swing.JMenuItem menuItemGotoCollections;
    private javax.swing.JMenuItem menuItemGotoDirectories;
    private javax.swing.JMenuItem menuItemGotoEdit;
    private javax.swing.JMenuItem menuItemGotoExifMetadata;
    private javax.swing.JMenuItem menuItemGotoFastSearch;
    private javax.swing.JMenuItem menuItemGotoFavorites;
    private javax.swing.JMenuItem menuItemGotoIptcMetadata;
    private javax.swing.JMenuItem menuItemGotoKeywordsEdit;
    private javax.swing.JMenuItem menuItemGotoKeywordsSel;
    private javax.swing.JMenuItem menuItemGotoMiscMetadata;
    private javax.swing.JMenuItem menuItemGotoNoMetadata;
    private javax.swing.JMenuItem menuItemGotoSavedSearches;
    private javax.swing.JMenuItem menuItemGotoThumbnailsPanel;
    private javax.swing.JMenuItem menuItemGotoTimeline;
    private javax.swing.JMenuItem menuItemGotoXmpMetadata;
    private javax.swing.JMenuItem menuItemHelp;
    private javax.swing.JMenuItem menuItemImportJptMisc;
    private javax.swing.JMenuItem menuItemInputHelper;
    private javax.swing.JMenuItem menuItemMaintainRepository;
    private javax.swing.JMenuItem menuItemMaintainance;
    private javax.swing.JMenuItem menuItemOpenPdfUserManual;
    private javax.swing.JMenuItem menuItemScanDirectory;
    private javax.swing.JMenuItem menuItemSearch;
    private javax.swing.JMenuItem menuItemSendBugMail;
    private javax.swing.JMenuItem menuItemSendFeatureMail;
    private javax.swing.JMenuItem menuItemSettings;
    private javax.swing.JMenuItem menuItemSynonyms;
    private javax.swing.JMenuItem menuItemToolIptcToXmp;
    private javax.swing.JMenuItem menuItemUserDefinedFileFilter;
    private javax.swing.JMenuItem menuItemUserDefinedFileType;
    private javax.swing.JMenu menuTools;
    private javax.swing.JMenu menuView;
    private javax.swing.JMenu menuWindow;
    private javax.swing.JPopupMenu.Separator sep1;
    private javax.swing.JPopupMenu.Separator sep15;
    private javax.swing.JPopupMenu.Separator sep16;
    private javax.swing.JPopupMenu.Separator sep17;
    private javax.swing.JPopupMenu.Separator sep18;
    private javax.swing.JPopupMenu.Separator sep2;
    private javax.swing.JPopupMenu.Separator sep20;
    private javax.swing.JPopupMenu.Separator sep21;
    private javax.swing.JPopupMenu.Separator sep22;
    private javax.swing.JPopupMenu.Separator sep23;
    private javax.swing.JPopupMenu.Separator sep3;
    private javax.swing.JPopupMenu.Separator sep4;
    // End of variables declaration//GEN-END:variables
}
