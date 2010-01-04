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
package de.elmar_baumann.jpt.view.frames;

import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.app.AppInfo;
import de.elmar_baumann.jpt.app.AppLifeCycle;
import de.elmar_baumann.jpt.comparator.ComparatorExifDateTimeOriginalAsc;
import de.elmar_baumann.jpt.comparator.ComparatorExifDateTimeOriginalDesc;
import de.elmar_baumann.jpt.comparator.ComparatorExifFocalLengthAsc;
import de.elmar_baumann.jpt.comparator.ComparatorExifFocalLengthDesc;
import de.elmar_baumann.jpt.comparator.ComparatorExifIsoSpeedRatingAsc;
import de.elmar_baumann.jpt.comparator.ComparatorExifIsoSpeedRatingDesc;
import de.elmar_baumann.jpt.comparator.ComparatorExifRecordingEquipmentAsc;
import de.elmar_baumann.jpt.comparator.ComparatorExifRecordingEquipmentDesc;
import de.elmar_baumann.jpt.comparator.ComparatorXmpIptcLocationAsc;
import de.elmar_baumann.jpt.comparator.ComparatorXmpIptcLocationDesc;
import de.elmar_baumann.jpt.comparator.ComparatorXmpRatingAsc;
import de.elmar_baumann.jpt.comparator.ComparatorXmpRatingDesc;
import de.elmar_baumann.lib.comparator.FileSort;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.lib.componentutil.MenuUtil;
import java.io.File;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

/**
 * The application's frame.
 *
 * @author Elmar Baumann <eb@elmar-baumann.de>
 */
public final class AppFrame extends javax.swing.JFrame {

    private final Map<Comparator<File>, JRadioButtonMenuItem> menuItemOfSortCmp = new HashMap<Comparator<File>, JRadioButtonMenuItem>();
    private final Map<JRadioButtonMenuItem, Comparator<File>> sortCmpOfMenuItem = new HashMap<JRadioButtonMenuItem, Comparator<File>>();
    private final Map<GoTo, JMenuItem>                        menuItemOfGoto    = new HashMap<GoTo, JMenuItem>();
    private final Map<JMenuItem, GoTo>                        gotoOfMenuItem    = new HashMap<JMenuItem, GoTo>();
    private AppPanel                                          appPanel;

    private void initSortMenuItemsMap() {
        menuItemOfSortCmp.put(FileSort.PATHS_ASCENDING.getComparator()         , radioButtonMenuItemSortFilepathAscending);
        menuItemOfSortCmp.put(FileSort.PATHS_DESCENDING.getComparator()        , radioButtonMenuItemSortFilepathDescending);
        menuItemOfSortCmp.put(FileSort.NAMES_ASCENDING.getComparator()         , radioButtonMenuItemSortFilenameAscending);
        menuItemOfSortCmp.put(FileSort.NAMES_DESCENDING.getComparator()        , radioButtonMenuItemSortFilenameDescending);
        menuItemOfSortCmp.put(FileSort.LAST_MODIFIED_ASCENDING.getComparator() , radioButtonMenuItemSortLastModifiedAscending);
        menuItemOfSortCmp.put(FileSort.LAST_MODIFIED_DESCENDING.getComparator(), radioButtonMenuItemSortLastModifiedDescending);
        menuItemOfSortCmp.put(FileSort.TYPES_ASCENDING.getComparator()         , radioButtonMenuItemSortFileTypeAscending);
        menuItemOfSortCmp.put(FileSort.TYPES_DESCENDING.getComparator()        , radioButtonMenuItemSortFileTypeDescending);
        menuItemOfSortCmp.put(new ComparatorExifDateTimeOriginalAsc()          , radioButtonMenuItemSortExifDateTimeOriginalAscending);
        menuItemOfSortCmp.put(new ComparatorExifDateTimeOriginalDesc()         , radioButtonMenuItemSortExifDateTimeOriginalDescending);
        menuItemOfSortCmp.put(new ComparatorExifFocalLengthAsc()               , radioButtonMenuItemSortExifFocalLengthAscending);
        menuItemOfSortCmp.put(new ComparatorExifFocalLengthDesc()              , radioButtonMenuItemSortExifFocalLengthDescending);
        menuItemOfSortCmp.put(new ComparatorExifIsoSpeedRatingAsc()            , radioButtonMenuItemSortExifIsoSpeedRatingAscending);
        menuItemOfSortCmp.put(new ComparatorExifIsoSpeedRatingDesc()           , radioButtonMenuItemSortExifIsoSpeedRatingDescending);
        menuItemOfSortCmp.put(new ComparatorExifRecordingEquipmentAsc()        , radioButtonMenuItemSortExifRecordingEquipmentAscending);
        menuItemOfSortCmp.put(new ComparatorExifRecordingEquipmentDesc()       , radioButtonMenuItemSortExifRecordingEquipmentDescending);
        menuItemOfSortCmp.put(new ComparatorXmpRatingAsc()                     , radioButtonMenuItemSortXmpRatingAscending);
        menuItemOfSortCmp.put(new ComparatorXmpRatingDesc()                    , radioButtonMenuItemSortXmpRatingDescending);
        menuItemOfSortCmp.put(new ComparatorXmpIptcLocationAsc()               , radioButtonMenuItemSortXmpIptcLocationAscending);
        menuItemOfSortCmp.put(new ComparatorXmpIptcLocationDesc()              , radioButtonMenuItemSortXmpIptcLocationDescending);

        for (Comparator<File> comparator : menuItemOfSortCmp.keySet()) {
            sortCmpOfMenuItem.put(menuItemOfSortCmp.get(comparator), comparator);
        }
    }

    private void initGotoMenuItemsMap() {
        menuItemOfGoto.put(GoTo.DIRECTORIES          , menuItemGotoDirectories);
        menuItemOfGoto.put(GoTo.EDIT_PANELS          , menuItemGotoEdit);
        menuItemOfGoto.put(GoTo.EXIF_METADATA        , menuItemGotoExifMetadata);
        menuItemOfGoto.put(GoTo.FAST_SEARCH          , menuItemGotoFastSearch);
        menuItemOfGoto.put(GoTo.FAVORITE_DIRECTORIES , menuItemGotoFavoriteDirectories);
        menuItemOfGoto.put(GoTo.HIERARCHICAL_KEYWORDS, menuItemGotoHierarchicalKeywords);
        menuItemOfGoto.put(GoTo.IMAGE_COLLECTIONS    , menuItemGotoCollections);
        menuItemOfGoto.put(GoTo.IPTC_METADATA        , menuItemGotoIptcMetadata);
        menuItemOfGoto.put(GoTo.KEYWORDS             , menuItemGotoKeywords);
        menuItemOfGoto.put(GoTo.MISC_METADATA        , menuItemGotoMiscMetadata);
        menuItemOfGoto.put(GoTo.SAVED_SEARCHES       , menuItemGotoSavedSearches);
        menuItemOfGoto.put(GoTo.THUMBNAILS_PANEL     , menuItemGotoThumbnailsPanel);
        menuItemOfGoto.put(GoTo.TIMELINE             , menuItemGotoTimeline);
        menuItemOfGoto.put(GoTo.XMP_METADATA         , menuItemGotoXmpMetadata);

        for (GoTo gt : menuItemOfGoto.keySet()) {
            gotoOfMenuItem.put(menuItemOfGoto.get(gt), gt);
        }
    }

    public enum GoTo {

        DIRECTORIES,
        EDIT_PANELS,
        EXIF_METADATA,
        FAST_SEARCH,
        FAVORITE_DIRECTORIES,
        HIERARCHICAL_KEYWORDS,
        IMAGE_COLLECTIONS,
        IPTC_METADATA,
        KEYWORDS,
        MISC_METADATA,
        SAVED_SEARCHES,
        THUMBNAILS_PANEL,
        TIMELINE,
        XMP_METADATA,
    };

    public AppFrame() {
        GUI.INSTANCE.setAppFrame(this);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        addAppPanel();
        MenuUtil.setMnemonics(menuBar);
        initSortMenuItemsMap();
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

    public JMenuItem getMenuItemCopyFromAutocopyDirectory() {
        return menuItemCopyFromAutocopyDirectory;
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

    public JMenuItem getMenuItemActions() {
        return menuItemActions;
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

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menuBar = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuItemScanDirectory = new javax.swing.JMenuItem();
        menuItemMaintainDatabase = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        menuItemCopyFromAutocopyDirectory = new javax.swing.JMenuItem();
        jSeparator11 = new javax.swing.JSeparator();
        menuImport = new javax.swing.JMenu();
        menuItemImportKeywords = new javax.swing.JMenuItem();
        menuExport = new javax.swing.JMenu();
        menuItemExportKeywords = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        menuItemExit = new javax.swing.JMenuItem();
        menuEdit = new javax.swing.JMenu();
        menuItemSettings = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        menuItemSearch = new javax.swing.JMenuItem();
        menuView = new javax.swing.JMenu();
        menuSort = new javax.swing.JMenu();
        radioButtonMenuItemSortFilepathAscending = new javax.swing.JRadioButtonMenuItem();
        radioButtonMenuItemSortFilepathDescending = new javax.swing.JRadioButtonMenuItem();
        jSeparator26 = new javax.swing.JSeparator();
        radioButtonMenuItemSortFilenameAscending = new javax.swing.JRadioButtonMenuItem();
        radioButtonMenuItemSortFilenameDescending = new javax.swing.JRadioButtonMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        radioButtonMenuItemSortFileTypeAscending = new javax.swing.JRadioButtonMenuItem();
        radioButtonMenuItemSortFileTypeDescending = new javax.swing.JRadioButtonMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        radioButtonMenuItemSortLastModifiedAscending = new javax.swing.JRadioButtonMenuItem();
        radioButtonMenuItemSortLastModifiedDescending = new javax.swing.JRadioButtonMenuItem();
        jSeparator15 = new javax.swing.JPopupMenu.Separator();
        radioButtonMenuItemSortExifDateTimeOriginalAscending = new javax.swing.JRadioButtonMenuItem();
        radioButtonMenuItemSortExifDateTimeOriginalDescending = new javax.swing.JRadioButtonMenuItem();
        jSeparator16 = new javax.swing.JPopupMenu.Separator();
        radioButtonMenuItemSortExifIsoSpeedRatingAscending = new javax.swing.JRadioButtonMenuItem();
        radioButtonMenuItemSortExifIsoSpeedRatingDescending = new javax.swing.JRadioButtonMenuItem();
        jSeparator17 = new javax.swing.JPopupMenu.Separator();
        radioButtonMenuItemSortExifFocalLengthAscending = new javax.swing.JRadioButtonMenuItem();
        radioButtonMenuItemSortExifFocalLengthDescending = new javax.swing.JRadioButtonMenuItem();
        jSeparator18 = new javax.swing.JPopupMenu.Separator();
        radioButtonMenuItemSortExifRecordingEquipmentAscending = new javax.swing.JRadioButtonMenuItem();
        radioButtonMenuItemSortExifRecordingEquipmentDescending = new javax.swing.JRadioButtonMenuItem();
        jSeparator22 = new javax.swing.JPopupMenu.Separator();
        radioButtonMenuItemSortXmpIptcLocationAscending = new javax.swing.JRadioButtonMenuItem();
        radioButtonMenuItemSortXmpIptcLocationDescending = new javax.swing.JRadioButtonMenuItem();
        jSeparator19 = new javax.swing.JPopupMenu.Separator();
        radioButtonMenuItemSortXmpRatingAscending = new javax.swing.JRadioButtonMenuItem();
        radioButtonMenuItemSortXmpRatingDescending = new javax.swing.JRadioButtonMenuItem();
        jSeparator10 = new javax.swing.JSeparator();
        checkBoxMenuItemKeywordOverlay = new javax.swing.JCheckBoxMenuItem();
        menuGoto = new javax.swing.JMenu();
        menuItemGotoFastSearch = new javax.swing.JMenuItem();
        menuItemGotoEdit = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JSeparator();
        menuItemGotoDirectories = new javax.swing.JMenuItem();
        menuItemGotoSavedSearches = new javax.swing.JMenuItem();
        menuItemGotoCollections = new javax.swing.JMenuItem();
        menuItemGotoFavoriteDirectories = new javax.swing.JMenuItem();
        menuItemGotoKeywords = new javax.swing.JMenuItem();
        menuItemGotoTimeline = new javax.swing.JMenuItem();
        menuItemGotoMiscMetadata = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JSeparator();
        menuItemGotoThumbnailsPanel = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JSeparator();
        menuItemGotoIptcMetadata = new javax.swing.JMenuItem();
        menuItemGotoExifMetadata = new javax.swing.JMenuItem();
        menuItemGotoXmpMetadata = new javax.swing.JMenuItem();
        menuItemGotoHierarchicalKeywords = new javax.swing.JMenuItem();
        menuTools = new javax.swing.JMenu();
        menuItemToolIptcToXmp = new javax.swing.JMenuItem();
        menuItemExtractEmbeddedXmp = new javax.swing.JMenuItem();
        menuWindow = new javax.swing.JMenu();
        menuItemInputHelper = new javax.swing.JMenuItem();
        menuItemActions = new javax.swing.JMenuItem();
        jSeparator20 = new javax.swing.JPopupMenu.Separator();
        menuItemOutputWindow = new javax.swing.JMenuItem();
        jSeparator21 = new javax.swing.JPopupMenu.Separator();
        menuItemDisplayLogfile = new javax.swing.JMenuItem();
        menuHelp = new javax.swing.JMenu();
        menuItemHelp = new javax.swing.JMenuItem();
        menuItemOpenPdfUserManual = new javax.swing.JMenuItem();
        menuItemAcceleratorKeys = new javax.swing.JMenuItem();
        jSeparator12 = new javax.swing.JSeparator();
        menuItemAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(AppInfo.APP_NAME);

        menuFile.setText(Bundle.getString("AppFrame.menuFile.text")); // NOI18N

        menuItemScanDirectory.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        menuItemScanDirectory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_folder.png"))); // NOI18N
        menuItemScanDirectory.setText(Bundle.getString("AppFrame.menuItemScanDirectory.text")); // NOI18N
        menuFile.add(menuItemScanDirectory);

        menuItemMaintainDatabase.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
        menuItemMaintainDatabase.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_file.png"))); // NOI18N
        menuItemMaintainDatabase.setText(Bundle.getString("AppFrame.menuItemMaintainDatabase.text")); // NOI18N
        menuFile.add(menuItemMaintainDatabase);
        menuFile.add(jSeparator1);

        menuItemCopyFromAutocopyDirectory.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.ALT_MASK));
        menuItemCopyFromAutocopyDirectory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_card.png"))); // NOI18N
        menuItemCopyFromAutocopyDirectory.setText(Bundle.getString("AppFrame.menuItemCopyFromAutocopyDirectory.text")); // NOI18N
        menuFile.add(menuItemCopyFromAutocopyDirectory);
        menuFile.add(jSeparator11);

        menuImport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_import.png"))); // NOI18N
        menuImport.setText(Bundle.getString("AppFrame.menuImport.text")); // NOI18N

        menuItemImportKeywords.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_keyword.png"))); // NOI18N
        menuItemImportKeywords.setText(Bundle.getString("AppFrame.menuItemImportKeywords.text")); // NOI18N
        menuImport.add(menuItemImportKeywords);

        menuFile.add(menuImport);

        menuExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_export.png"))); // NOI18N
        menuExport.setText(Bundle.getString("AppFrame.menuExport.text")); // NOI18N

        menuItemExportKeywords.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_keyword.png"))); // NOI18N
        menuItemExportKeywords.setText(Bundle.getString("AppFrame.menuItemExportKeywords.text")); // NOI18N
        menuExport.add(menuItemExportKeywords);

        menuFile.add(menuExport);
        menuFile.add(jSeparator4);

        menuItemExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        menuItemExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_exit.png"))); // NOI18N
        menuItemExit.setText(Bundle.getString("AppFrame.menuItemExit.text")); // NOI18N
        menuFile.add(menuItemExit);

        menuBar.add(menuFile);

        menuEdit.setText(Bundle.getString("AppFrame.menuEdit.text")); // NOI18N

        menuItemSettings.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        menuItemSettings.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_settings.png"))); // NOI18N
        menuItemSettings.setText(Bundle.getString("AppFrame.menuItemSettings.text")); // NOI18N
        menuEdit.add(menuItemSettings);
        menuEdit.add(jSeparator5);

        menuItemSearch.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
        menuItemSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_search.png"))); // NOI18N
        menuItemSearch.setText(Bundle.getString("AppFrame.menuItemSearch.text")); // NOI18N
        menuEdit.add(menuItemSearch);

        menuBar.add(menuEdit);

        menuView.setText(Bundle.getString("AppFrame.menuView.text")); // NOI18N

        menuSort.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_sort.png"))); // NOI18N
        menuSort.setText(Bundle.getString("AppFrame.menuSort.text")); // NOI18N

        radioButtonMenuItemSortFilepathAscending.setText(Bundle.getString("AppFrame.radioButtonMenuItemSortFilepathAscending.text")); // NOI18N
        menuSort.add(radioButtonMenuItemSortFilepathAscending);

        radioButtonMenuItemSortFilepathDescending.setText(Bundle.getString("AppFrame.radioButtonMenuItemSortFilepathDescending.text")); // NOI18N
        menuSort.add(radioButtonMenuItemSortFilepathDescending);
        menuSort.add(jSeparator26);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/elmar_baumann/jpt/resource/properties/Bundle"); // NOI18N
        radioButtonMenuItemSortFilenameAscending.setText(bundle.getString("AppFrame.radioButtonMenuItemSortFilenameAscending.text")); // NOI18N
        menuSort.add(radioButtonMenuItemSortFilenameAscending);

        radioButtonMenuItemSortFilenameDescending.setText(bundle.getString("AppFrame.radioButtonMenuItemSortFilenameDescending.text")); // NOI18N
        menuSort.add(radioButtonMenuItemSortFilenameDescending);
        menuSort.add(jSeparator2);

        radioButtonMenuItemSortFileTypeAscending.setText(Bundle.getString("AppFrame.radioButtonMenuItemSortFileTypeAscending.text")); // NOI18N
        menuSort.add(radioButtonMenuItemSortFileTypeAscending);

        radioButtonMenuItemSortFileTypeDescending.setText(Bundle.getString("AppFrame.radioButtonMenuItemSortFileTypeDescending.text")); // NOI18N
        menuSort.add(radioButtonMenuItemSortFileTypeDescending);
        menuSort.add(jSeparator3);

        radioButtonMenuItemSortLastModifiedAscending.setText(Bundle.getString("AppFrame.radioButtonMenuItemSortLastModifiedAscending.text")); // NOI18N
        menuSort.add(radioButtonMenuItemSortLastModifiedAscending);

        radioButtonMenuItemSortLastModifiedDescending.setText(Bundle.getString("AppFrame.radioButtonMenuItemSortLastModifiedDescending.text")); // NOI18N
        menuSort.add(radioButtonMenuItemSortLastModifiedDescending);
        menuSort.add(jSeparator15);

        radioButtonMenuItemSortExifDateTimeOriginalAscending.setText(Bundle.getString("AppFrame.radioButtonMenuItemSortExifDateTimeOriginalAscending.text")); // NOI18N
        menuSort.add(radioButtonMenuItemSortExifDateTimeOriginalAscending);

        radioButtonMenuItemSortExifDateTimeOriginalDescending.setText(Bundle.getString("AppFrame.radioButtonMenuItemSortExifDateTimeOriginalDescending.text")); // NOI18N
        menuSort.add(radioButtonMenuItemSortExifDateTimeOriginalDescending);
        menuSort.add(jSeparator16);

        radioButtonMenuItemSortExifIsoSpeedRatingAscending.setText(Bundle.getString("AppFrame.radioButtonMenuItemSortExifIsoSpeedRatingAscending.text")); // NOI18N
        menuSort.add(radioButtonMenuItemSortExifIsoSpeedRatingAscending);

        radioButtonMenuItemSortExifIsoSpeedRatingDescending.setText(Bundle.getString("AppFrame.radioButtonMenuItemSortExifIsoSpeedRatingDescending.text")); // NOI18N
        menuSort.add(radioButtonMenuItemSortExifIsoSpeedRatingDescending);
        menuSort.add(jSeparator17);

        radioButtonMenuItemSortExifFocalLengthAscending.setText(Bundle.getString("AppFrame.radioButtonMenuItemSortExifFocalLengthAscending.text")); // NOI18N
        menuSort.add(radioButtonMenuItemSortExifFocalLengthAscending);

        radioButtonMenuItemSortExifFocalLengthDescending.setText(Bundle.getString("AppFrame.radioButtonMenuItemSortExifFocalLengthDescending.text")); // NOI18N
        menuSort.add(radioButtonMenuItemSortExifFocalLengthDescending);
        menuSort.add(jSeparator18);

        radioButtonMenuItemSortExifRecordingEquipmentAscending.setText(Bundle.getString("AppFrame.radioButtonMenuItemSortExifRecordingEquipmentAscending.text")); // NOI18N
        menuSort.add(radioButtonMenuItemSortExifRecordingEquipmentAscending);

        radioButtonMenuItemSortExifRecordingEquipmentDescending.setText(Bundle.getString("AppFrame.radioButtonMenuItemSortExifRecordingEquipmentDescending.text")); // NOI18N
        menuSort.add(radioButtonMenuItemSortExifRecordingEquipmentDescending);
        menuSort.add(jSeparator22);

        radioButtonMenuItemSortXmpIptcLocationAscending.setText(Bundle.getString("AppFrame.radioButtonMenuItemSortXmpIptcLocationAscending.text")); // NOI18N
        menuSort.add(radioButtonMenuItemSortXmpIptcLocationAscending);

        radioButtonMenuItemSortXmpIptcLocationDescending.setText(Bundle.getString("AppFrame.radioButtonMenuItemSortXmpIptcLocationDescending.text")); // NOI18N
        menuSort.add(radioButtonMenuItemSortXmpIptcLocationDescending);
        menuSort.add(jSeparator19);

        radioButtonMenuItemSortXmpRatingAscending.setText(Bundle.getString("AppFrame.radioButtonMenuItemSortXmpRatingAscending.text")); // NOI18N
        menuSort.add(radioButtonMenuItemSortXmpRatingAscending);

        radioButtonMenuItemSortXmpRatingDescending.setText(Bundle.getString("AppFrame.radioButtonMenuItemSortXmpRatingDescending.text")); // NOI18N
        menuSort.add(radioButtonMenuItemSortXmpRatingDescending);

        menuView.add(menuSort);
        menuView.add(jSeparator10);

        checkBoxMenuItemKeywordOverlay.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        checkBoxMenuItemKeywordOverlay.setText(bundle.getString("AppFrame.checkBoxMenuItemKeywordOverlay.text")); // NOI18N
        checkBoxMenuItemKeywordOverlay.setToolTipText(bundle.getString("AppFrame.checkBoxMenuItemKeywordOverlay.toolTipText")); // NOI18N
        menuView.add(checkBoxMenuItemKeywordOverlay);

        menuBar.add(menuView);

        menuGoto.setText(Bundle.getString("AppFrame.menuGoto.text")); // NOI18N

        menuItemGotoFastSearch.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        menuItemGotoFastSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_search.png"))); // NOI18N
        menuItemGotoFastSearch.setText(Bundle.getString("AppFrame.menuItemGotoFastSearch.text")); // NOI18N
        menuGoto.add(menuItemGotoFastSearch);

        menuItemGotoEdit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        menuItemGotoEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_workspace.png"))); // NOI18N
        menuItemGotoEdit.setText(Bundle.getString("AppFrame.menuItemGotoEdit.text")); // NOI18N
        menuGoto.add(menuItemGotoEdit);
        menuGoto.add(jSeparator7);

        menuItemGotoDirectories.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_1, java.awt.event.InputEvent.CTRL_MASK));
        menuItemGotoDirectories.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_folder.png"))); // NOI18N
        menuItemGotoDirectories.setText(Bundle.getString("AppFrame.menuItemGotoDirectories.text")); // NOI18N
        menuGoto.add(menuItemGotoDirectories);

        menuItemGotoSavedSearches.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_2, java.awt.event.InputEvent.CTRL_MASK));
        menuItemGotoSavedSearches.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_search.png"))); // NOI18N
        menuItemGotoSavedSearches.setText(Bundle.getString("AppFrame.menuItemGotoSavedSearches.text")); // NOI18N
        menuGoto.add(menuItemGotoSavedSearches);

        menuItemGotoCollections.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_3, java.awt.event.InputEvent.CTRL_MASK));
        menuItemGotoCollections.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_imagecollection.png"))); // NOI18N
        menuItemGotoCollections.setText(Bundle.getString("AppFrame.menuItemGotoCollections.text")); // NOI18N
        menuGoto.add(menuItemGotoCollections);

        menuItemGotoFavoriteDirectories.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_5, java.awt.event.InputEvent.CTRL_MASK));
        menuItemGotoFavoriteDirectories.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_favorite.png"))); // NOI18N
        menuItemGotoFavoriteDirectories.setText(Bundle.getString("AppFrame.menuItemGotoFavoriteDirectories.text")); // NOI18N
        menuGoto.add(menuItemGotoFavoriteDirectories);

        menuItemGotoKeywords.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_6, java.awt.event.InputEvent.CTRL_MASK));
        menuItemGotoKeywords.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_keyword.png"))); // NOI18N
        menuItemGotoKeywords.setText(Bundle.getString("AppFrame.menuItemGotoKeywords.text")); // NOI18N
        menuGoto.add(menuItemGotoKeywords);

        menuItemGotoTimeline.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_7, java.awt.event.InputEvent.CTRL_MASK));
        menuItemGotoTimeline.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_timeline.png"))); // NOI18N
        menuItemGotoTimeline.setText(Bundle.getString("AppFrame.menuItemGotoTimeline.text")); // NOI18N
        menuGoto.add(menuItemGotoTimeline);

        menuItemGotoMiscMetadata.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_8, java.awt.event.InputEvent.CTRL_MASK));
        menuItemGotoMiscMetadata.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_misc_metadata.png"))); // NOI18N
        menuItemGotoMiscMetadata.setText(Bundle.getString("AppFrame.menuItemGotoMiscMetadata.text")); // NOI18N
        menuGoto.add(menuItemGotoMiscMetadata);
        menuGoto.add(jSeparator8);

        menuItemGotoThumbnailsPanel.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_0, java.awt.event.InputEvent.CTRL_MASK));
        menuItemGotoThumbnailsPanel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_thumbnails.png"))); // NOI18N
        menuItemGotoThumbnailsPanel.setText(Bundle.getString("AppFrame.menuItemGotoThumbnailsPanel.text")); // NOI18N
        menuGoto.add(menuItemGotoThumbnailsPanel);
        menuGoto.add(jSeparator9);

        menuItemGotoIptcMetadata.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_1, java.awt.event.InputEvent.ALT_MASK));
        menuItemGotoIptcMetadata.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_iptc.png"))); // NOI18N
        menuItemGotoIptcMetadata.setText(Bundle.getString("AppFrame.menuItemGotoIptcMetadata.text")); // NOI18N
        menuGoto.add(menuItemGotoIptcMetadata);

        menuItemGotoExifMetadata.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_2, java.awt.event.InputEvent.ALT_MASK));
        menuItemGotoExifMetadata.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_exif.png"))); // NOI18N
        menuItemGotoExifMetadata.setText(Bundle.getString("AppFrame.menuItemGotoExifMetadata.text")); // NOI18N
        menuGoto.add(menuItemGotoExifMetadata);

        menuItemGotoXmpMetadata.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_3, java.awt.event.InputEvent.ALT_MASK));
        menuItemGotoXmpMetadata.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_xmp.png"))); // NOI18N
        menuItemGotoXmpMetadata.setText(Bundle.getString("AppFrame.menuItemGotoXmpMetadata.text")); // NOI18N
        menuGoto.add(menuItemGotoXmpMetadata);

        menuItemGotoHierarchicalKeywords.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_4, java.awt.event.InputEvent.ALT_MASK));
        menuItemGotoHierarchicalKeywords.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_tree.png"))); // NOI18N
        menuItemGotoHierarchicalKeywords.setText(Bundle.getString("AppFrame.menuItemGotoHierarchicalKeywords.text")); // NOI18N
        menuGoto.add(menuItemGotoHierarchicalKeywords);

        menuBar.add(menuGoto);

        menuTools.setText(Bundle.getString("AppFrame.menuTools.text")); // NOI18N

        menuItemToolIptcToXmp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        menuItemToolIptcToXmp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_iptc.png"))); // NOI18N
        menuItemToolIptcToXmp.setText(Bundle.getString("AppFrame.menuItemToolIptcToXmp.text")); // NOI18N
        menuTools.add(menuItemToolIptcToXmp);

        menuItemExtractEmbeddedXmp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_xmp.png"))); // NOI18N
        menuItemExtractEmbeddedXmp.setText(Bundle.getString("AppFrame.menuItemExtractEmbeddedXmp.text")); // NOI18N
        menuTools.add(menuItemExtractEmbeddedXmp);

        menuBar.add(menuTools);

        menuWindow.setText(Bundle.getString("AppFrame.menuWindow.text")); // NOI18N

        menuItemInputHelper.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F9, 0));
        menuItemInputHelper.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_edit.png"))); // NOI18N
        menuItemInputHelper.setText(Bundle.getString("AppFrame.menuItemInputHelper.text")); // NOI18N
        menuWindow.add(menuItemInputHelper);

        menuItemActions.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, 0));
        menuItemActions.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_action.png"))); // NOI18N
        menuItemActions.setText(Bundle.getString("AppFrame.menuItemActions.text")); // NOI18N
        menuWindow.add(menuItemActions);
        menuWindow.add(jSeparator20);

        menuItemOutputWindow.setText(bundle.getString("AppFrame.menuItemOutputWindow.text")); // NOI18N
        menuWindow.add(menuItemOutputWindow);
        menuWindow.add(jSeparator21);

        menuItemDisplayLogfile.setText(bundle.getString("AppFrame.menuItemDisplayLogfile.text")); // NOI18N
        menuItemDisplayLogfile.setEnabled(false);
        menuWindow.add(menuItemDisplayLogfile);

        menuBar.add(menuWindow);

        menuHelp.setText(Bundle.getString("AppFrame.menuHelp.text")); // NOI18N

        menuItemHelp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        menuItemHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_help.png"))); // NOI18N
        menuItemHelp.setText(Bundle.getString("AppFrame.menuItemHelp.text")); // NOI18N
        menuHelp.add(menuItemHelp);

        menuItemOpenPdfUserManual.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_pdf_manual.png"))); // NOI18N
        menuItemOpenPdfUserManual.setText(Bundle.getString("AppFrame.menuItemOpenPdfUserManual.text")); // NOI18N
        menuHelp.add(menuItemOpenPdfUserManual);

        menuItemAcceleratorKeys.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_keyboard.png"))); // NOI18N
        menuItemAcceleratorKeys.setText(bundle.getString("AppFrame.menuItemAcceleratorKeys.text")); // NOI18N
        menuHelp.add(menuItemAcceleratorKeys);
        menuHelp.add(jSeparator12);

        menuItemAbout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_app_small.png"))); // NOI18N
        menuItemAbout.setText(Bundle.getString("AppFrame.menuItemAbout.text")); // NOI18N
        menuHelp.add(menuItemAbout);

        menuBar.add(menuHelp);

        setJMenuBar(menuBar);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBoxMenuItem checkBoxMenuItemKeywordOverlay;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JPopupMenu.Separator jSeparator15;
    private javax.swing.JPopupMenu.Separator jSeparator16;
    private javax.swing.JPopupMenu.Separator jSeparator17;
    private javax.swing.JPopupMenu.Separator jSeparator18;
    private javax.swing.JPopupMenu.Separator jSeparator19;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator20;
    private javax.swing.JPopupMenu.Separator jSeparator21;
    private javax.swing.JPopupMenu.Separator jSeparator22;
    private javax.swing.JSeparator jSeparator26;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu menuEdit;
    private javax.swing.JMenu menuExport;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenu menuGoto;
    private javax.swing.JMenu menuHelp;
    private javax.swing.JMenu menuImport;
    private javax.swing.JMenuItem menuItemAbout;
    private javax.swing.JMenuItem menuItemAcceleratorKeys;
    private javax.swing.JMenuItem menuItemActions;
    private javax.swing.JMenuItem menuItemCopyFromAutocopyDirectory;
    private javax.swing.JMenuItem menuItemDisplayLogfile;
    private javax.swing.JMenuItem menuItemExit;
    private javax.swing.JMenuItem menuItemExportKeywords;
    private javax.swing.JMenuItem menuItemExtractEmbeddedXmp;
    private javax.swing.JMenuItem menuItemGotoCollections;
    private javax.swing.JMenuItem menuItemGotoDirectories;
    private javax.swing.JMenuItem menuItemGotoEdit;
    private javax.swing.JMenuItem menuItemGotoExifMetadata;
    private javax.swing.JMenuItem menuItemGotoFastSearch;
    private javax.swing.JMenuItem menuItemGotoFavoriteDirectories;
    private javax.swing.JMenuItem menuItemGotoHierarchicalKeywords;
    private javax.swing.JMenuItem menuItemGotoIptcMetadata;
    private javax.swing.JMenuItem menuItemGotoKeywords;
    private javax.swing.JMenuItem menuItemGotoMiscMetadata;
    private javax.swing.JMenuItem menuItemGotoSavedSearches;
    private javax.swing.JMenuItem menuItemGotoThumbnailsPanel;
    private javax.swing.JMenuItem menuItemGotoTimeline;
    private javax.swing.JMenuItem menuItemGotoXmpMetadata;
    private javax.swing.JMenuItem menuItemHelp;
    private javax.swing.JMenuItem menuItemImportKeywords;
    private javax.swing.JMenuItem menuItemInputHelper;
    private javax.swing.JMenuItem menuItemMaintainDatabase;
    private javax.swing.JMenuItem menuItemOpenPdfUserManual;
    private javax.swing.JMenuItem menuItemOutputWindow;
    private javax.swing.JMenuItem menuItemScanDirectory;
    private javax.swing.JMenuItem menuItemSearch;
    private javax.swing.JMenuItem menuItemSettings;
    private javax.swing.JMenuItem menuItemToolIptcToXmp;
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
    private javax.swing.JRadioButtonMenuItem radioButtonMenuItemSortXmpIptcLocationAscending;
    private javax.swing.JRadioButtonMenuItem radioButtonMenuItemSortXmpIptcLocationDescending;
    private javax.swing.JRadioButtonMenuItem radioButtonMenuItemSortXmpRatingAscending;
    private javax.swing.JRadioButtonMenuItem radioButtonMenuItemSortXmpRatingDescending;
    // End of variables declaration//GEN-END:variables
}
