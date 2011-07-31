package org.jphototagger.program.view.frames;

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu.Separator;
import javax.swing.JRadioButtonMenuItem;

import org.jphototagger.lib.comparator.FileSort;
import org.jphototagger.lib.componentutil.MenuUtil;
import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.lib.system.SystemUtil;
import org.jphototagger.lib.util.ServiceLookup;
import org.jphototagger.lib.util.StringUtil;
import org.jphototagger.program.app.AppInfo;
import org.jphototagger.program.app.AppLifeCycle;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.comparator.ComparatorExifDateTimeOriginalAsc;
import org.jphototagger.program.comparator.ComparatorExifDateTimeOriginalDesc;
import org.jphototagger.program.comparator.ComparatorExifFocalLengthAsc;
import org.jphototagger.program.comparator.ComparatorExifFocalLengthDesc;
import org.jphototagger.program.comparator.ComparatorExifIsoSpeedRatingAsc;
import org.jphototagger.program.comparator.ComparatorExifIsoSpeedRatingDesc;
import org.jphototagger.program.comparator.ComparatorExifRecordingEquipmentAsc;
import org.jphototagger.program.comparator.ComparatorExifRecordingEquipmentDesc;
import org.jphototagger.program.comparator.ComparatorXmpIptcLocationAsc;
import org.jphototagger.program.comparator.ComparatorXmpIptcLocationDesc;
import org.jphototagger.program.comparator.ComparatorXmpRatingAsc;
import org.jphototagger.program.comparator.ComparatorXmpRatingDesc;
import org.jphototagger.program.controller.actions.EditUserDefinedFileTypesAction;
import org.jphototagger.program.controller.actions.ShowActionDialogAction;
import org.jphototagger.program.factory.MainWindowComponentPluginManager;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.AppPanel;
import org.jphototagger.services.plugin.EditMenuActionProvider;
import org.jphototagger.services.plugin.FileMenuActionProvider;
import org.jphototagger.services.plugin.MenuActionProvider;
import org.jphototagger.services.plugin.PositionComparator;
import org.jphototagger.services.plugin.ToolsMenuActionProvider;

/**
 * The application's frame.
 *
 * @author Elmar Baumann
 */
public final class AppFrame extends javax.swing.JFrame {
    private static final long serialVersionUID  = -7374684230320795331L;
    private final Map<Comparator<File>, JRadioButtonMenuItem> menuItemOfSortCmp = new HashMap<Comparator<File>, JRadioButtonMenuItem>();
    private final Map<JRadioButtonMenuItem, Comparator<File>> sortCmpOfMenuItem = new HashMap<JRadioButtonMenuItem, Comparator<File>>();
    private final Map<GoTo, JMenuItem> menuItemOfGoto = new EnumMap<GoTo, JMenuItem>(GoTo.class);
    private final Map<JMenuItem, GoTo> gotoOfMenuItem = new HashMap<JMenuItem, GoTo>();
    private AppPanel appPanel;

    private void initSortMenuItemsMap() {
        menuItemOfSortCmp.put(FileSort.PATHS_ASCENDING.getComparator(), radioButtonMenuItemSortFilepathAscending);
        menuItemOfSortCmp.put(FileSort.PATHS_DESCENDING.getComparator(), radioButtonMenuItemSortFilepathDescending);
        menuItemOfSortCmp.put(FileSort.NAMES_ASCENDING.getComparator(), radioButtonMenuItemSortFilenameAscending);
        menuItemOfSortCmp.put(FileSort.NAMES_DESCENDING.getComparator(), radioButtonMenuItemSortFilenameDescending);
        menuItemOfSortCmp.put(FileSort.LAST_MODIFIED_ASCENDING.getComparator(), radioButtonMenuItemSortLastModifiedAscending);
        menuItemOfSortCmp.put(FileSort.LAST_MODIFIED_DESCENDING.getComparator(), radioButtonMenuItemSortLastModifiedDescending);
        menuItemOfSortCmp.put(FileSort.TYPES_ASCENDING.getComparator(), radioButtonMenuItemSortFileTypeAscending);
        menuItemOfSortCmp.put(FileSort.TYPES_DESCENDING.getComparator(), radioButtonMenuItemSortFileTypeDescending);
        menuItemOfSortCmp.put(FileSort.NO_SORT.getComparator(), radioButtonMenuItemSortNone);
        menuItemOfSortCmp.put(new ComparatorExifDateTimeOriginalAsc(), radioButtonMenuItemSortExifDateTimeOriginalAscending);
        menuItemOfSortCmp.put(new ComparatorExifDateTimeOriginalDesc(), radioButtonMenuItemSortExifDateTimeOriginalDescending);
        menuItemOfSortCmp.put(new ComparatorExifFocalLengthAsc(), radioButtonMenuItemSortExifFocalLengthAscending);
        menuItemOfSortCmp.put(new ComparatorExifFocalLengthDesc(), radioButtonMenuItemSortExifFocalLengthDescending);
        menuItemOfSortCmp.put(new ComparatorExifIsoSpeedRatingAsc(), radioButtonMenuItemSortExifIsoSpeedRatingAscending);
        menuItemOfSortCmp.put(new ComparatorExifIsoSpeedRatingDesc(), radioButtonMenuItemSortExifIsoSpeedRatingDescending);
        menuItemOfSortCmp.put(new ComparatorExifRecordingEquipmentAsc(), radioButtonMenuItemSortExifRecordingEquipmentAscending);
        menuItemOfSortCmp.put(new ComparatorExifRecordingEquipmentDesc(), radioButtonMenuItemSortExifRecordingEquipmentDescending);
        menuItemOfSortCmp.put(new ComparatorXmpRatingAsc(), radioButtonMenuItemSortXmpRatingAscending);
        menuItemOfSortCmp.put(new ComparatorXmpRatingDesc(), radioButtonMenuItemSortXmpRatingDescending);
        menuItemOfSortCmp.put(new ComparatorXmpIptcLocationAsc(), radioButtonMenuItemSortXmpIptcLocationAscending);
        menuItemOfSortCmp.put(new ComparatorXmpIptcLocationDesc(), radioButtonMenuItemSortXmpIptcLocationDescending);

        for (Comparator<File> comparator : menuItemOfSortCmp.keySet()) {
            sortCmpOfMenuItem.put(menuItemOfSortCmp.get(comparator), comparator);
        }
    }

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
        addPluginsMenuItems();
        MenuUtil.setMnemonics(menuBar);
        initSortMenuItemsMap();
        initGotoMenuItemsMap();
        setIconImages(AppLookAndFeel.getAppIcons());
        AppLifeCycle.INSTANCE.started(this);
    }

    private void addPluginsMenuItems() {
        List<MenuActionProvider> fileMenuActionProviders
                = new ArrayList<MenuActionProvider>(ServiceLookup.lookupAll(FileMenuActionProvider.class));
        List<MenuActionProvider> editMenuActionProviders
                = new ArrayList<MenuActionProvider>(ServiceLookup.lookupAll(EditMenuActionProvider.class));
        List<MenuActionProvider> toolsMenuActionProviders
                = new ArrayList<MenuActionProvider>(ServiceLookup.lookupAll(ToolsMenuActionProvider.class));

        addPluginMenuItems(fileMenuActionProviders, menuFile);
        addPluginMenuItems(editMenuActionProviders, menuEdit);
        addPluginMenuItems(toolsMenuActionProviders, menuTools);
    }

    private void addPluginMenuItems(List<? extends MenuActionProvider> menuActionProviders, JMenu menu) {
        Collections.sort(menuActionProviders, PositionComparator.INSTANCE);

        if (!menuActionProviders.isEmpty()) {
            menu.add(new Separator());
        }

        for (MenuActionProvider menuActionProvider : menuActionProviders) {
            if (MainWindowComponentPluginManager.INSTANCE.isEnabled(menuActionProvider.getPlugin())) {
                Action menuAction = menuActionProvider.getMenuAction();

                if (checkPluginMenuActionHasName(menuActionProvider, menuAction)) {
                    menu.add(menuAction);
                }
            }
        }
    }

    private boolean checkPluginMenuActionHasName(MenuActionProvider menuActionProvider, Action action) {
        Object value = action.getValue(Action.NAME);
        boolean hasName = value != null && value instanceof String && StringUtil.hasContent(((String)value));

        if (!hasName) {
            Logger.getLogger(AppFrame.class.getName()).log(Level.WARNING,
                    "Action {0} of {1} hasn''t a name and will not be added to the menu", new Object[]{action, menuActionProvider});
        }

        return hasName;
    }

    public AppPanel getAppPanel() {
        return appPanel;
    }

    private void addAppPanel() {
        appPanel = new AppPanel();
        getContentPane().add(appPanel);
    }

    public JMenu getMenuSort() {
        return menuSort;
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

    public JRadioButtonMenuItem getMenuItemOfSortCmp(Comparator<File> sortCmp) {
        return menuItemOfSortCmp.get(sortCmp);
    }

    public Collection<JRadioButtonMenuItem> getSortMenuItems() {
        return menuItemOfSortCmp.values();
    }

    public Comparator<File> getSortCmpOfMenuItem(JRadioButtonMenuItem item) {
        return sortCmpOfMenuItem.get(item);
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

    public JMenuItem getMenuItemMaintainDatabase() {
        return menuItemMaintainDatabase;
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

    public JMenuItem getMenuItemImportKeywords() {
        return menuItemImportKeywords;
    }

    public JMenuItem getMenuItemExportKeywords() {
        return menuItemExportKeywords;
    }

    public JMenuItem getMenuItemExit() {
        return menuItemExit;
    }

    public JMenuItem getMenuItemAcceleratorKeys() {
        return menuItemAcceleratorKeys;
    }

    public JMenuItem getMenuItemOutputWindow() {
        return menuItemOutputWindow;
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

    public void selectMenuItemUnsorted() {
        radioButtonMenuItemSortNone.setSelected(true);
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

    public JMenuItem getMenuItemBackupDatabase() {
        return menuItemBackupDatabase;
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
        menuItemMaintainDatabase = new javax.swing.JMenuItem();
        menuItemMaintainance = new JMenuItem(org.jphototagger.program.controller.maintainance.DisplayMaintainanceDialogAction.INSTANCE);
        sep1 = new javax.swing.JPopupMenu.Separator();
        menuImportImageFiles = new javax.swing.JMenuItem();
        sep2 = new javax.swing.JPopupMenu.Separator();
        menuExport = new javax.swing.JMenu();
        menuItemExportJptMisc = new javax.swing.JMenuItem();
        menuItemExportKeywords = new javax.swing.JMenuItem();
        menuImport = new javax.swing.JMenu();
        menuItemImportJptMisc = new javax.swing.JMenuItem();
        menuItemImportKeywords = new javax.swing.JMenuItem();
        menuItemBackupDatabase = new javax.swing.JMenuItem();
        sep3 = new javax.swing.JPopupMenu.Separator();
        menuItemExit = new javax.swing.JMenuItem();
        menuEdit = new javax.swing.JMenu();
        menuItemSettings = new javax.swing.JMenuItem();
        sep4 = new javax.swing.JPopupMenu.Separator();
        menuItemSearch = new javax.swing.JMenuItem();
        menuView = new javax.swing.JMenu();
        menuSort = new javax.swing.JMenu();
        radioButtonMenuItemSortFilepathAscending = new javax.swing.JRadioButtonMenuItem();
        radioButtonMenuItemSortFilepathDescending = new javax.swing.JRadioButtonMenuItem();
        sep5 = new javax.swing.JPopupMenu.Separator();
        radioButtonMenuItemSortFilenameAscending = new javax.swing.JRadioButtonMenuItem();
        radioButtonMenuItemSortFilenameDescending = new javax.swing.JRadioButtonMenuItem();
        sep6 = new javax.swing.JPopupMenu.Separator();
        radioButtonMenuItemSortFileTypeAscending = new javax.swing.JRadioButtonMenuItem();
        radioButtonMenuItemSortFileTypeDescending = new javax.swing.JRadioButtonMenuItem();
        sep7 = new javax.swing.JPopupMenu.Separator();
        radioButtonMenuItemSortLastModifiedAscending = new javax.swing.JRadioButtonMenuItem();
        radioButtonMenuItemSortLastModifiedDescending = new javax.swing.JRadioButtonMenuItem();
        sep8 = new javax.swing.JPopupMenu.Separator();
        radioButtonMenuItemSortExifDateTimeOriginalAscending = new javax.swing.JRadioButtonMenuItem();
        radioButtonMenuItemSortExifDateTimeOriginalDescending = new javax.swing.JRadioButtonMenuItem();
        sep9 = new javax.swing.JPopupMenu.Separator();
        radioButtonMenuItemSortExifIsoSpeedRatingAscending = new javax.swing.JRadioButtonMenuItem();
        radioButtonMenuItemSortExifIsoSpeedRatingDescending = new javax.swing.JRadioButtonMenuItem();
        sep10 = new javax.swing.JPopupMenu.Separator();
        radioButtonMenuItemSortExifFocalLengthAscending = new javax.swing.JRadioButtonMenuItem();
        radioButtonMenuItemSortExifFocalLengthDescending = new javax.swing.JRadioButtonMenuItem();
        sep11 = new javax.swing.JPopupMenu.Separator();
        radioButtonMenuItemSortExifRecordingEquipmentAscending = new javax.swing.JRadioButtonMenuItem();
        radioButtonMenuItemSortExifRecordingEquipmentDescending = new javax.swing.JRadioButtonMenuItem();
        sep12 = new javax.swing.JPopupMenu.Separator();
        radioButtonMenuItemSortXmpIptcLocationAscending = new javax.swing.JRadioButtonMenuItem();
        radioButtonMenuItemSortXmpIptcLocationDescending = new javax.swing.JRadioButtonMenuItem();
        sep13 = new javax.swing.JPopupMenu.Separator();
        radioButtonMenuItemSortXmpRatingAscending = new javax.swing.JRadioButtonMenuItem();
        radioButtonMenuItemSortXmpRatingDescending = new javax.swing.JRadioButtonMenuItem();
        sep14 = new javax.swing.JPopupMenu.Separator();
        radioButtonMenuItemSortNone = new javax.swing.JRadioButtonMenuItem();
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
        sep19 = new javax.swing.JPopupMenu.Separator();
        menuItemOutputWindow = new javax.swing.JMenuItem();
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

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/view/frames/Bundle"); // NOI18N
        menuFile.setText(bundle.getString("AppFrame.menuFile.text")); // NOI18N
        menuFile.setName("menuFile"); // NOI18N

        menuItemScanDirectory.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcutWithShiftDown(KeyEvent.VK_R));
        menuItemScanDirectory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_folder.png"))); // NOI18N
        menuItemScanDirectory.setText(bundle.getString("AppFrame.menuItemScanDirectory.text")); // NOI18N
        menuItemScanDirectory.setName("menuItemScanDirectory"); // NOI18N
        menuFile.add(menuItemScanDirectory);

        menuItemMaintainDatabase.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcutWithShiftDown(KeyEvent.VK_D));
        menuItemMaintainDatabase.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_database.png"))); // NOI18N
        menuItemMaintainDatabase.setText(bundle.getString("AppFrame.menuItemMaintainDatabase.text")); // NOI18N
        menuItemMaintainDatabase.setName("menuItemMaintainDatabase"); // NOI18N
        menuFile.add(menuItemMaintainDatabase);

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

        menuItemExportJptMisc.setAction(org.jphototagger.program.exporter.JptExportAction.INSTANCE);
        menuItemExportJptMisc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_app_small.png"))); // NOI18N
        menuItemExportJptMisc.setText(bundle.getString("AppFrame.menuItemExportJptMisc.text")); // NOI18N
        menuItemExportJptMisc.setName("menuItemExportJptMisc"); // NOI18N
        menuExport.add(menuItemExportJptMisc);

        menuItemExportKeywords.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_keyword.png"))); // NOI18N
        menuItemExportKeywords.setText(bundle.getString("AppFrame.menuItemExportKeywords.text")); // NOI18N
        menuItemExportKeywords.setName("menuItemExportKeywords"); // NOI18N
        menuExport.add(menuItemExportKeywords);

        menuFile.add(menuExport);

        menuImport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_import.png"))); // NOI18N
        menuImport.setText(bundle.getString("AppFrame.menuImport.text")); // NOI18N
        menuImport.setName("menuImport"); // NOI18N

        menuItemImportJptMisc.setAction(org.jphototagger.program.importer.JptImportAction.INSTANCE);
        menuItemImportJptMisc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_app_small.png"))); // NOI18N
        menuItemImportJptMisc.setText(bundle.getString("AppFrame.menuItemImportJptMisc.text")); // NOI18N
        menuItemImportJptMisc.setName("menuItemImportJptMisc"); // NOI18N
        menuImport.add(menuItemImportJptMisc);

        menuItemImportKeywords.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_keyword.png"))); // NOI18N
        menuItemImportKeywords.setText(bundle.getString("AppFrame.menuItemImportKeywords.text")); // NOI18N
        menuItemImportKeywords.setName("menuItemImportKeywords"); // NOI18N
        menuImport.add(menuItemImportKeywords);

        menuFile.add(menuImport);

        menuItemBackupDatabase.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_save.png"))); // NOI18N
        menuItemBackupDatabase.setText(bundle.getString("AppFrame.menuItemBackupDatabase.text")); // NOI18N
        menuItemBackupDatabase.setName("menuItemBackupDatabase"); // NOI18N
        menuFile.add(menuItemBackupDatabase);

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

        menuSort.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_sort.png"))); // NOI18N
        menuSort.setText(bundle.getString("AppFrame.menuSort.text")); // NOI18N
        menuSort.setName("menuSort"); // NOI18N

        buttonGroupSort.add(radioButtonMenuItemSortFilepathAscending);
        radioButtonMenuItemSortFilepathAscending.setText(bundle.getString("AppFrame.radioButtonMenuItemSortFilepathAscending.text")); // NOI18N
        radioButtonMenuItemSortFilepathAscending.setName("radioButtonMenuItemSortFilepathAscending"); // NOI18N
        menuSort.add(radioButtonMenuItemSortFilepathAscending);

        buttonGroupSort.add(radioButtonMenuItemSortFilepathDescending);
        radioButtonMenuItemSortFilepathDescending.setText(bundle.getString("AppFrame.radioButtonMenuItemSortFilepathDescending.text")); // NOI18N
        radioButtonMenuItemSortFilepathDescending.setName("radioButtonMenuItemSortFilepathDescending"); // NOI18N
        menuSort.add(radioButtonMenuItemSortFilepathDescending);

        sep5.setName("sep5"); // NOI18N
        menuSort.add(sep5);

        buttonGroupSort.add(radioButtonMenuItemSortFilenameAscending);
        radioButtonMenuItemSortFilenameAscending.setText(bundle.getString("AppFrame.radioButtonMenuItemSortFilenameAscending.text")); // NOI18N
        radioButtonMenuItemSortFilenameAscending.setName("radioButtonMenuItemSortFilenameAscending"); // NOI18N
        menuSort.add(radioButtonMenuItemSortFilenameAscending);

        buttonGroupSort.add(radioButtonMenuItemSortFilenameDescending);
        radioButtonMenuItemSortFilenameDescending.setText(bundle.getString("AppFrame.radioButtonMenuItemSortFilenameDescending.text")); // NOI18N
        radioButtonMenuItemSortFilenameDescending.setName("radioButtonMenuItemSortFilenameDescending"); // NOI18N
        menuSort.add(radioButtonMenuItemSortFilenameDescending);

        sep6.setName("sep6"); // NOI18N
        menuSort.add(sep6);

        buttonGroupSort.add(radioButtonMenuItemSortFileTypeAscending);
        radioButtonMenuItemSortFileTypeAscending.setText(bundle.getString("AppFrame.radioButtonMenuItemSortFileTypeAscending.text")); // NOI18N
        radioButtonMenuItemSortFileTypeAscending.setName("radioButtonMenuItemSortFileTypeAscending"); // NOI18N
        menuSort.add(radioButtonMenuItemSortFileTypeAscending);

        buttonGroupSort.add(radioButtonMenuItemSortFileTypeDescending);
        radioButtonMenuItemSortFileTypeDescending.setText(bundle.getString("AppFrame.radioButtonMenuItemSortFileTypeDescending.text")); // NOI18N
        radioButtonMenuItemSortFileTypeDescending.setName("radioButtonMenuItemSortFileTypeDescending"); // NOI18N
        menuSort.add(radioButtonMenuItemSortFileTypeDescending);

        sep7.setName("sep7"); // NOI18N
        menuSort.add(sep7);

        buttonGroupSort.add(radioButtonMenuItemSortLastModifiedAscending);
        radioButtonMenuItemSortLastModifiedAscending.setText(bundle.getString("AppFrame.radioButtonMenuItemSortLastModifiedAscending.text")); // NOI18N
        radioButtonMenuItemSortLastModifiedAscending.setName("radioButtonMenuItemSortLastModifiedAscending"); // NOI18N
        menuSort.add(radioButtonMenuItemSortLastModifiedAscending);

        buttonGroupSort.add(radioButtonMenuItemSortLastModifiedDescending);
        radioButtonMenuItemSortLastModifiedDescending.setText(bundle.getString("AppFrame.radioButtonMenuItemSortLastModifiedDescending.text")); // NOI18N
        radioButtonMenuItemSortLastModifiedDescending.setName("radioButtonMenuItemSortLastModifiedDescending"); // NOI18N
        menuSort.add(radioButtonMenuItemSortLastModifiedDescending);

        sep8.setName("sep8"); // NOI18N
        menuSort.add(sep8);

        buttonGroupSort.add(radioButtonMenuItemSortExifDateTimeOriginalAscending);
        radioButtonMenuItemSortExifDateTimeOriginalAscending.setText(bundle.getString("AppFrame.radioButtonMenuItemSortExifDateTimeOriginalAscending.text")); // NOI18N
        radioButtonMenuItemSortExifDateTimeOriginalAscending.setName("radioButtonMenuItemSortExifDateTimeOriginalAscending"); // NOI18N
        menuSort.add(radioButtonMenuItemSortExifDateTimeOriginalAscending);

        buttonGroupSort.add(radioButtonMenuItemSortExifDateTimeOriginalDescending);
        radioButtonMenuItemSortExifDateTimeOriginalDescending.setText(bundle.getString("AppFrame.radioButtonMenuItemSortExifDateTimeOriginalDescending.text")); // NOI18N
        radioButtonMenuItemSortExifDateTimeOriginalDescending.setName("radioButtonMenuItemSortExifDateTimeOriginalDescending"); // NOI18N
        menuSort.add(radioButtonMenuItemSortExifDateTimeOriginalDescending);

        sep9.setName("sep9"); // NOI18N
        menuSort.add(sep9);

        buttonGroupSort.add(radioButtonMenuItemSortExifIsoSpeedRatingAscending);
        radioButtonMenuItemSortExifIsoSpeedRatingAscending.setText(bundle.getString("AppFrame.radioButtonMenuItemSortExifIsoSpeedRatingAscending.text")); // NOI18N
        radioButtonMenuItemSortExifIsoSpeedRatingAscending.setName("radioButtonMenuItemSortExifIsoSpeedRatingAscending"); // NOI18N
        menuSort.add(radioButtonMenuItemSortExifIsoSpeedRatingAscending);

        buttonGroupSort.add(radioButtonMenuItemSortExifIsoSpeedRatingDescending);
        radioButtonMenuItemSortExifIsoSpeedRatingDescending.setText(bundle.getString("AppFrame.radioButtonMenuItemSortExifIsoSpeedRatingDescending.text")); // NOI18N
        radioButtonMenuItemSortExifIsoSpeedRatingDescending.setName("radioButtonMenuItemSortExifIsoSpeedRatingDescending"); // NOI18N
        menuSort.add(radioButtonMenuItemSortExifIsoSpeedRatingDescending);

        sep10.setName("sep10"); // NOI18N
        menuSort.add(sep10);

        buttonGroupSort.add(radioButtonMenuItemSortExifFocalLengthAscending);
        radioButtonMenuItemSortExifFocalLengthAscending.setText(bundle.getString("AppFrame.radioButtonMenuItemSortExifFocalLengthAscending.text")); // NOI18N
        radioButtonMenuItemSortExifFocalLengthAscending.setName("radioButtonMenuItemSortExifFocalLengthAscending"); // NOI18N
        menuSort.add(radioButtonMenuItemSortExifFocalLengthAscending);

        buttonGroupSort.add(radioButtonMenuItemSortExifFocalLengthDescending);
        radioButtonMenuItemSortExifFocalLengthDescending.setText(bundle.getString("AppFrame.radioButtonMenuItemSortExifFocalLengthDescending.text")); // NOI18N
        radioButtonMenuItemSortExifFocalLengthDescending.setName("radioButtonMenuItemSortExifFocalLengthDescending"); // NOI18N
        menuSort.add(radioButtonMenuItemSortExifFocalLengthDescending);

        sep11.setName("sep11"); // NOI18N
        menuSort.add(sep11);

        buttonGroupSort.add(radioButtonMenuItemSortExifRecordingEquipmentAscending);
        radioButtonMenuItemSortExifRecordingEquipmentAscending.setText(bundle.getString("AppFrame.radioButtonMenuItemSortExifRecordingEquipmentAscending.text")); // NOI18N
        radioButtonMenuItemSortExifRecordingEquipmentAscending.setName("radioButtonMenuItemSortExifRecordingEquipmentAscending"); // NOI18N
        menuSort.add(radioButtonMenuItemSortExifRecordingEquipmentAscending);

        buttonGroupSort.add(radioButtonMenuItemSortExifRecordingEquipmentDescending);
        radioButtonMenuItemSortExifRecordingEquipmentDescending.setText(bundle.getString("AppFrame.radioButtonMenuItemSortExifRecordingEquipmentDescending.text")); // NOI18N
        radioButtonMenuItemSortExifRecordingEquipmentDescending.setName("radioButtonMenuItemSortExifRecordingEquipmentDescending"); // NOI18N
        menuSort.add(radioButtonMenuItemSortExifRecordingEquipmentDescending);

        sep12.setName("sep12"); // NOI18N
        menuSort.add(sep12);

        buttonGroupSort.add(radioButtonMenuItemSortXmpIptcLocationAscending);
        radioButtonMenuItemSortXmpIptcLocationAscending.setText(bundle.getString("AppFrame.radioButtonMenuItemSortXmpIptcLocationAscending.text")); // NOI18N
        radioButtonMenuItemSortXmpIptcLocationAscending.setName("radioButtonMenuItemSortXmpIptcLocationAscending"); // NOI18N
        menuSort.add(radioButtonMenuItemSortXmpIptcLocationAscending);

        buttonGroupSort.add(radioButtonMenuItemSortXmpIptcLocationDescending);
        radioButtonMenuItemSortXmpIptcLocationDescending.setText(bundle.getString("AppFrame.radioButtonMenuItemSortXmpIptcLocationDescending.text")); // NOI18N
        radioButtonMenuItemSortXmpIptcLocationDescending.setName("radioButtonMenuItemSortXmpIptcLocationDescending"); // NOI18N
        menuSort.add(radioButtonMenuItemSortXmpIptcLocationDescending);

        sep13.setName("sep13"); // NOI18N
        menuSort.add(sep13);

        buttonGroupSort.add(radioButtonMenuItemSortXmpRatingAscending);
        radioButtonMenuItemSortXmpRatingAscending.setText(bundle.getString("AppFrame.radioButtonMenuItemSortXmpRatingAscending.text")); // NOI18N
        radioButtonMenuItemSortXmpRatingAscending.setName("radioButtonMenuItemSortXmpRatingAscending"); // NOI18N
        menuSort.add(radioButtonMenuItemSortXmpRatingAscending);

        buttonGroupSort.add(radioButtonMenuItemSortXmpRatingDescending);
        radioButtonMenuItemSortXmpRatingDescending.setText(bundle.getString("AppFrame.radioButtonMenuItemSortXmpRatingDescending.text")); // NOI18N
        radioButtonMenuItemSortXmpRatingDescending.setName("radioButtonMenuItemSortXmpRatingDescending"); // NOI18N
        menuSort.add(radioButtonMenuItemSortXmpRatingDescending);

        sep14.setName("sep14"); // NOI18N
        menuSort.add(sep14);

        buttonGroupSort.add(radioButtonMenuItemSortNone);
        radioButtonMenuItemSortNone.setText(bundle.getString("AppFrame.radioButtonMenuItemSortNone.text")); // NOI18N
        radioButtonMenuItemSortNone.setEnabled(false);
        radioButtonMenuItemSortNone.setName("radioButtonMenuItemSortNone"); // NOI18N
        menuSort.add(radioButtonMenuItemSortNone);

        menuView.add(menuSort);

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

        menuItemActions.setAction(new ShowActionDialogAction());
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

        sep19.setName("sep19"); // NOI18N
        menuWindow.add(sep19);

        menuItemOutputWindow.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcutWithShiftDown(KeyEvent.VK_T));
        menuItemOutputWindow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_view_logfile.png"))); // NOI18N
        menuItemOutputWindow.setText(bundle.getString("AppFrame.menuItemOutputWindow.text")); // NOI18N
        menuItemOutputWindow.setName("menuItemOutputWindow"); // NOI18N
        menuWindow.add(menuItemOutputWindow);

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
    private javax.swing.JMenuItem menuItemBackupDatabase;
    private javax.swing.JMenuItem menuItemBrowseChangelog;
    private javax.swing.JMenuItem menuItemBrowseUserForum;
    private javax.swing.JMenuItem menuItemBrowseWebsite;
    private javax.swing.JMenuItem menuItemCheckForUpdates;
    private javax.swing.JMenuItem menuItemDisplayAllLogfile;
    private javax.swing.JMenuItem menuItemDisplayLogfile;
    private javax.swing.JMenuItem menuItemExit;
    private javax.swing.JMenuItem menuItemExportJptMisc;
    private javax.swing.JMenuItem menuItemExportKeywords;
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
    private javax.swing.JMenuItem menuItemImportKeywords;
    private javax.swing.JMenuItem menuItemInputHelper;
    private javax.swing.JMenuItem menuItemMaintainDatabase;
    private javax.swing.JMenuItem menuItemMaintainance;
    private javax.swing.JMenuItem menuItemOpenPdfUserManual;
    private javax.swing.JMenuItem menuItemOutputWindow;
    private javax.swing.JMenuItem menuItemScanDirectory;
    private javax.swing.JMenuItem menuItemSearch;
    private javax.swing.JMenuItem menuItemSendBugMail;
    private javax.swing.JMenuItem menuItemSendFeatureMail;
    private javax.swing.JMenuItem menuItemSettings;
    private javax.swing.JMenuItem menuItemSynonyms;
    private javax.swing.JMenuItem menuItemToolIptcToXmp;
    private javax.swing.JMenuItem menuItemUserDefinedFileFilter;
    private javax.swing.JMenuItem menuItemUserDefinedFileType;
    private javax.swing.JMenu menuSort;
    private javax.swing.JMenu menuTools;
    private javax.swing.JMenu menuView;
    private javax.swing.JMenu menuWindow;
    private javax.swing.JRadioButtonMenuItem radioButtonMenuItemSortExifDateTimeOriginalAscending;
    private javax.swing.JRadioButtonMenuItem radioButtonMenuItemSortExifDateTimeOriginalDescending;
    private javax.swing.JRadioButtonMenuItem radioButtonMenuItemSortExifFocalLengthAscending;
    private javax.swing.JRadioButtonMenuItem radioButtonMenuItemSortExifFocalLengthDescending;
    private javax.swing.JRadioButtonMenuItem radioButtonMenuItemSortExifIsoSpeedRatingAscending;
    private javax.swing.JRadioButtonMenuItem radioButtonMenuItemSortExifIsoSpeedRatingDescending;
    private javax.swing.JRadioButtonMenuItem radioButtonMenuItemSortExifRecordingEquipmentAscending;
    private javax.swing.JRadioButtonMenuItem radioButtonMenuItemSortExifRecordingEquipmentDescending;
    private javax.swing.JRadioButtonMenuItem radioButtonMenuItemSortFileTypeAscending;
    private javax.swing.JRadioButtonMenuItem radioButtonMenuItemSortFileTypeDescending;
    private javax.swing.JRadioButtonMenuItem radioButtonMenuItemSortFilenameAscending;
    private javax.swing.JRadioButtonMenuItem radioButtonMenuItemSortFilenameDescending;
    private javax.swing.JRadioButtonMenuItem radioButtonMenuItemSortFilepathAscending;
    private javax.swing.JRadioButtonMenuItem radioButtonMenuItemSortFilepathDescending;
    private javax.swing.JRadioButtonMenuItem radioButtonMenuItemSortLastModifiedAscending;
    private javax.swing.JRadioButtonMenuItem radioButtonMenuItemSortLastModifiedDescending;
    private javax.swing.JRadioButtonMenuItem radioButtonMenuItemSortNone;
    private javax.swing.JRadioButtonMenuItem radioButtonMenuItemSortXmpIptcLocationAscending;
    private javax.swing.JRadioButtonMenuItem radioButtonMenuItemSortXmpIptcLocationDescending;
    private javax.swing.JRadioButtonMenuItem radioButtonMenuItemSortXmpRatingAscending;
    private javax.swing.JRadioButtonMenuItem radioButtonMenuItemSortXmpRatingDescending;
    private javax.swing.JPopupMenu.Separator sep1;
    private javax.swing.JPopupMenu.Separator sep10;
    private javax.swing.JPopupMenu.Separator sep11;
    private javax.swing.JPopupMenu.Separator sep12;
    private javax.swing.JPopupMenu.Separator sep13;
    private javax.swing.JPopupMenu.Separator sep14;
    private javax.swing.JPopupMenu.Separator sep15;
    private javax.swing.JPopupMenu.Separator sep16;
    private javax.swing.JPopupMenu.Separator sep17;
    private javax.swing.JPopupMenu.Separator sep18;
    private javax.swing.JPopupMenu.Separator sep19;
    private javax.swing.JPopupMenu.Separator sep2;
    private javax.swing.JPopupMenu.Separator sep20;
    private javax.swing.JPopupMenu.Separator sep21;
    private javax.swing.JPopupMenu.Separator sep22;
    private javax.swing.JPopupMenu.Separator sep23;
    private javax.swing.JPopupMenu.Separator sep3;
    private javax.swing.JPopupMenu.Separator sep4;
    private javax.swing.JPopupMenu.Separator sep5;
    private javax.swing.JPopupMenu.Separator sep6;
    private javax.swing.JPopupMenu.Separator sep7;
    private javax.swing.JPopupMenu.Separator sep8;
    private javax.swing.JPopupMenu.Separator sep9;
    // End of variables declaration//GEN-END:variables
}
