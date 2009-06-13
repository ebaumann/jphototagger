package de.elmar_baumann.imv.view.frames;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.app.AppInfo;
import de.elmar_baumann.imv.app.AppLock;
import de.elmar_baumann.imv.event.AppExitListener;
import de.elmar_baumann.imv.event.AppStartListener;
import de.elmar_baumann.imv.factory.MetaFactory;
import de.elmar_baumann.lib.comparator.FileSort;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

/**
 * Rahmenfenster der Anwendung.
 * 
 * @author Elmar Baumann <eb@elmar-baumann.de>
 */
public final class AppFrame extends javax.swing.JFrame {

    private final Map<FileSort, JRadioButtonMenuItem> menuItemOfSort =
            new HashMap<FileSort, JRadioButtonMenuItem>();
    private final Map<JRadioButtonMenuItem, FileSort> sortOfMenuItem =
            new HashMap<JRadioButtonMenuItem, FileSort>();
    private final Map<GoTo, JMenuItem> menuItemOfGoto =
            new HashMap<GoTo, JMenuItem>();
    private final Map<JMenuItem, GoTo> gotoOfMenuItem =
            new HashMap<JMenuItem, GoTo>();
    private final List<AppExitListener> exitListeners =
            new ArrayList<AppExitListener>();
    private final List<AppStartListener> startListeners =
            new ArrayList<AppStartListener>();
    private AppPanel appPanel;

    public AppFrame() {
        GUI.INSTANCE.setAppFrame(this);
        initComponents();
        postInitComponents();
    }

    private void initSortMenuItemsMap() {
        menuItemOfSort.put(FileSort.NAMES_ASCENDING,
                radioButtonMenuItemSortFilenameAscending);
        menuItemOfSort.put(FileSort.NAMES_DESCENDING,
                radioButtonMenuItemSortFilenameDescending);
        menuItemOfSort.put(FileSort.LAST_MODIFIED_ASCENDING,
                radioButtonMenuItemSortLastModifiedAscending);
        menuItemOfSort.put(FileSort.LAST_MODIFIED_DESCENDING,
                radioButtonMenuItemSortLastModifiedDescending);
        menuItemOfSort.put(FileSort.TYPES_ASCENDING,
                radioButtonMenuItemSortFileTypeAscending);
        menuItemOfSort.put(FileSort.TYPES_DESCENDING,
                radioButtonMenuItemSortFileTypeDescending);

        for (FileSort sort : menuItemOfSort.keySet()) {
            sortOfMenuItem.put(menuItemOfSort.get(sort), sort);
        }
    }

    private void initGotoMenuItemsMap() {
        menuItemOfGoto.put(GoTo.CATEGORIES, menuItemGotoCategories);
        menuItemOfGoto.put(GoTo.IMAGE_COLLECTIONS, menuItemGotoCollections);
        menuItemOfGoto.put(GoTo.DIRECTORIES, menuItemGotoDirectories);
        menuItemOfGoto.put(GoTo.EDIT_PANELS, menuItemGotoEdit);
        menuItemOfGoto.put(GoTo.EXIF_METADATA, menuItemGotoExifMetadata);
        menuItemOfGoto.put(GoTo.FAST_SEARCH, menuItemGotoFastSearch);
        menuItemOfGoto.put(GoTo.FAVORITE_DIRECTORIES,
                menuItemGotoFavoriteDirectories);
        menuItemOfGoto.put(GoTo.IPTC_METADATA, menuItemGotoIptcMetadata);
        menuItemOfGoto.put(GoTo.SAVED_SEARCHES, menuItemGotoSavedSearches);
        menuItemOfGoto.put(GoTo.KEYWORDS, menuItemGotoKeywords);
        menuItemOfGoto.put(GoTo.TIMELINE, menuItemGotoTimeline);
        menuItemOfGoto.put(GoTo.MISC_METADATA, menuItemGotoSelectionMiscMetadata);
        menuItemOfGoto.put(GoTo.THUMBNAILS_PANEL, menuItemGotoThumbnailsPanel);
        menuItemOfGoto.put(GoTo.XMP_METADATA, menuItemGotoXmpMetadata);

        for (GoTo gt : menuItemOfGoto.keySet()) {
            gotoOfMenuItem.put(menuItemOfGoto.get(gt), gt);
        }
    }

    public enum GoTo {

        FAST_SEARCH,
        EDIT_PANELS,
        DIRECTORIES,
        FAVORITE_DIRECTORIES,
        CATEGORIES,
        SAVED_SEARCHES,
        IMAGE_COLLECTIONS,
        KEYWORDS,
        TIMELINE,
        MISC_METADATA,
        THUMBNAILS_PANEL,
        EXIF_METADATA,
        IPTC_METADATA,
        XMP_METADATA,
    };

    private void postInitComponents() {
        addAppPanel();
        initSortMenuItemsMap();
        initGotoMenuItemsMap();
        listenToClose();
        setTitleAndFrameIcon();
        Thread thread = new Thread(MetaFactory.INSTANCE);
        thread.setName("AppFrame#postInitComponents"); // NOI18N
        thread.start();
        addAppExitListener(appPanel);
        appPanel.getEditPanelsArray().addDeleteListenerTo(menuItemDelete);
        notifyStart();
    }

    private void addAppPanel() {
        appPanel = new AppPanel();
        getContentPane().add(appPanel);
    }

    public synchronized void addAppStartListener(AppStartListener listener) {
        startListeners.add(listener);
    }

    private synchronized void notifyStart() {
        for (AppStartListener listener : startListeners) {
            listener.appWillStart();
        }
    }

    public synchronized void addAppExitListener(AppExitListener listener) {
        exitListeners.add(listener);
    }

    private synchronized void notifyExit() {
        for (AppExitListener listener : exitListeners) {
            listener.appWillExit();
        }
    }

    public GoTo getGotoOfMenuItem(JMenuItem item) {
        return gotoOfMenuItem.get(item);
    }

    public JMenuItem getMenuItemExtractEmbeddedXmp() {
        return menuItemExtractEmbeddedXmp;
    }

    public JMenuItem getMenuItemRenameInXmp() {
        return menuItemRenameInXmp;
    }

    public JMenuItem getMenuItemAutocopyDirectory() {
        return menuItemCopyFromAutocopyDirectory;
    }

    public JMenuItem getMenuItemCopy() {
        return menuItemCopy;
    }

    public JMenuItem getMenuItemPaste() {
        return menuItemPaste;
    }

    public JMenuItem getMenuItemCut() {
        return menuItemCut;
    }

    public JMenuItem getMenuItemOfGoto(GoTo gt) {
        return menuItemOfGoto.get(gt);
    }

    public JMenuItem getMenuItemDelete() {
        return menuItemDelete;
    }

    public JRadioButtonMenuItem getMenuItemOfSort(FileSort sort) {
        return menuItemOfSort.get(sort);
    }

    public FileSort getSortOfMenuItem(JRadioButtonMenuItem item) {
        return sortOfMenuItem.get(item);
    }

    public JMenuItem getMenuItemAbout() {
        return menuItemAbout;
    }

    public JMenuItem getMenuItemActions() {
        return menuItemActions;
    }

    public JMenuItem getMenuItemRename() {
        return menuItemFileSystemRename;
    }

    public JMenuItem getMenuItemHelp() {
        return menuItemHelp;
    }

    public JMenuItem getMenuItemMaintainDatabase() {
        return menuItemMaintainDatabase;
    }

    public JMenuItem getMenuItemRefresh() {
        return menuItemRefresh;
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

    private void writeProperties() {
        UserSettings.INSTANCE.getSettings().setSizeAndLocation(this);
        UserSettings.INSTANCE.writeToFile();
    }

    private void setTitleAndFrameIcon() {
        setIconImages(AppIcons.getAppIcons());
    }

    private void quit() {
        notifyExit();
        writeProperties();
        dispose();
        AppLock.unlock();
        System.exit(0);
    }

    private void listenToClose() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosed(WindowEvent evt) {
                quit();
            }
        });
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
        jSeparator4 = new javax.swing.JSeparator();
        menuItemExit = new javax.swing.JMenuItem();
        menuEdit = new javax.swing.JMenu();
        menuItemSettings = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        menuItemSearch = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JSeparator();
        menuItemCut = new javax.swing.JMenuItem();
        menuItemPaste = new javax.swing.JMenuItem();
        menuItemCopy = new javax.swing.JMenuItem();
        menuItemFileSystemRename = new javax.swing.JMenuItem();
        menuItemDelete = new javax.swing.JMenuItem();
        menuGoto = new javax.swing.JMenu();
        menuItemGotoFastSearch = new javax.swing.JMenuItem();
        menuItemGotoEdit = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JSeparator();
        menuItemGotoDirectories = new javax.swing.JMenuItem();
        menuItemGotoSavedSearches = new javax.swing.JMenuItem();
        menuItemGotoCollections = new javax.swing.JMenuItem();
        menuItemGotoCategories = new javax.swing.JMenuItem();
        menuItemGotoFavoriteDirectories = new javax.swing.JMenuItem();
        menuItemGotoKeywords = new javax.swing.JMenuItem();
        menuItemGotoTimeline = new javax.swing.JMenuItem();
        menuItemGotoSelectionMiscMetadata = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JSeparator();
        menuItemGotoThumbnailsPanel = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JSeparator();
        menuItemGotoIptcMetadata = new javax.swing.JMenuItem();
        menuItemGotoExifMetadata = new javax.swing.JMenuItem();
        menuItemGotoXmpMetadata = new javax.swing.JMenuItem();
        menuView = new javax.swing.JMenu();
        menuItemRefresh = new javax.swing.JMenuItem();
        menuSort = new javax.swing.JMenu();
        radioButtonMenuItemSortFilenameAscending = new javax.swing.JRadioButtonMenuItem();
        radioButtonMenuItemSortFilenameDescending = new javax.swing.JRadioButtonMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        radioButtonMenuItemSortLastModifiedAscending = new javax.swing.JRadioButtonMenuItem();
        radioButtonMenuItemSortLastModifiedDescending = new javax.swing.JRadioButtonMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        radioButtonMenuItemSortFileTypeAscending = new javax.swing.JRadioButtonMenuItem();
        radioButtonMenuItemSortFileTypeDescending = new javax.swing.JRadioButtonMenuItem();
        menuTools = new javax.swing.JMenu();
        menuItemToolIptcToXmp = new javax.swing.JMenuItem();
        menuItemExtractEmbeddedXmp = new javax.swing.JMenuItem();
        menuItemRenameInXmp = new javax.swing.JMenuItem();
        menuItemActions = new javax.swing.JMenuItem();
        menuHelp = new javax.swing.JMenu();
        menuItemHelp = new javax.swing.JMenuItem();
        menuItemAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(AppInfo.appName + " " + AppInfo.appVersion);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        menuFile.setMnemonic('d');
        menuFile.setText(Bundle.getString("AppFrame.menuFile.text")); // NOI18N

        menuItemScanDirectory.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        menuItemScanDirectory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icons/icon_folder.png"))); // NOI18N
        menuItemScanDirectory.setMnemonic('o');
        menuItemScanDirectory.setText(Bundle.getString("AppFrame.menuItemScanDirectory.text")); // NOI18N
        menuFile.add(menuItemScanDirectory);

        menuItemMaintainDatabase.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
        menuItemMaintainDatabase.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icons/icon_file.png"))); // NOI18N
        menuItemMaintainDatabase.setMnemonic('d');
        menuItemMaintainDatabase.setText(Bundle.getString("AppFrame.menuItemMaintainDatabase.text")); // NOI18N
        menuFile.add(menuItemMaintainDatabase);
        menuFile.add(jSeparator1);

        menuItemCopyFromAutocopyDirectory.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        menuItemCopyFromAutocopyDirectory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icons/icon_card.png"))); // NOI18N
        menuItemCopyFromAutocopyDirectory.setMnemonic('s');
        menuItemCopyFromAutocopyDirectory.setText(Bundle.getString("AppFrame.menuItemCopyFromAutocopyDirectory.text")); // NOI18N
        menuFile.add(menuItemCopyFromAutocopyDirectory);
        menuFile.add(jSeparator4);

        menuItemExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        menuItemExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icons/icon_exit.png"))); // NOI18N
        menuItemExit.setMnemonic('e');
        menuItemExit.setText(Bundle.getString("AppFrame.menuItemExit.text")); // NOI18N
        menuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemExitActionPerformed(evt);
            }
        });
        menuFile.add(menuItemExit);

        menuBar.add(menuFile);

        menuEdit.setMnemonic('b');
        menuEdit.setText(Bundle.getString("AppFrame.menuEdit.text")); // NOI18N

        menuItemSettings.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        menuItemSettings.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icons/icon_settings.png"))); // NOI18N
        menuItemSettings.setMnemonic('e');
        menuItemSettings.setText(Bundle.getString("AppFrame.menuItemSettings.text")); // NOI18N
        menuEdit.add(menuItemSettings);
        menuEdit.add(jSeparator5);

        menuItemSearch.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        menuItemSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icons/icon_search.png"))); // NOI18N
        menuItemSearch.setMnemonic('s');
        menuItemSearch.setText(Bundle.getString("AppFrame.menuItemSearch.text")); // NOI18N
        menuEdit.add(menuItemSearch);
        menuEdit.add(jSeparator6);

        menuItemCut.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        menuItemCut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icons/icon_edit_cut.png"))); // NOI18N
        menuItemCut.setMnemonic('a');
        menuItemCut.setText(Bundle.getString("AppFrame.menuItemCut.text")); // NOI18N
        menuItemCut.setEnabled(false);
        menuEdit.add(menuItemCut);

        menuItemPaste.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
        menuItemPaste.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icons/icon_edit_paste.png"))); // NOI18N
        menuItemPaste.setMnemonic('f');
        menuItemPaste.setText(Bundle.getString("AppFrame.menuItemPaste.text")); // NOI18N
        menuItemPaste.setEnabled(false);
        menuEdit.add(menuItemPaste);

        menuItemCopy.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        menuItemCopy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icons/icon_edit_copy.png"))); // NOI18N
        menuItemCopy.setMnemonic('k');
        menuItemCopy.setText(Bundle.getString("AppFrame.menuItemCopy.text")); // NOI18N
        menuItemCopy.setEnabled(false);
        menuEdit.add(menuItemCopy);

        menuItemFileSystemRename.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        menuItemFileSystemRename.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icons/icon_rename.png"))); // NOI18N
        menuItemFileSystemRename.setMnemonic('u');
        menuItemFileSystemRename.setText(Bundle.getString("AppFrame.menuItemFileSystemRename.text")); // NOI18N
        menuItemFileSystemRename.setEnabled(false);
        menuEdit.add(menuItemFileSystemRename);

        menuItemDelete.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        menuItemDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icons/icon_edit_delete.png"))); // NOI18N
        menuItemDelete.setMnemonic('l');
        menuItemDelete.setText(Bundle.getString("AppFrame.menuItemDelete.text")); // NOI18N
        menuItemDelete.setEnabled(false);
        menuEdit.add(menuItemDelete);

        menuBar.add(menuEdit);

        menuGoto.setMnemonic('g');
        menuGoto.setText(Bundle.getString("AppFrame.menuGoto.text")); // NOI18N

        menuItemGotoFastSearch.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        menuItemGotoFastSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icons/icon_search.png"))); // NOI18N
        menuItemGotoFastSearch.setMnemonic('s');
        menuItemGotoFastSearch.setText(Bundle.getString("AppFrame.menuItemGotoFastSearch.text")); // NOI18N
        menuGoto.add(menuItemGotoFastSearch);

        menuItemGotoEdit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_MASK));
        menuItemGotoEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icons/icon_workspace.png"))); // NOI18N
        menuItemGotoEdit.setMnemonic('b');
        menuItemGotoEdit.setText(Bundle.getString("AppFrame.menuItemGotoEdit.text")); // NOI18N
        menuGoto.add(menuItemGotoEdit);
        menuGoto.add(jSeparator7);

        menuItemGotoDirectories.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_1, java.awt.event.InputEvent.CTRL_MASK));
        menuItemGotoDirectories.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icons/icon_folder.png"))); // NOI18N
        menuItemGotoDirectories.setMnemonic('o');
        menuItemGotoDirectories.setText(Bundle.getString("AppFrame.menuItemGotoDirectories.text")); // NOI18N
        menuGoto.add(menuItemGotoDirectories);

        menuItemGotoSavedSearches.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_2, java.awt.event.InputEvent.CTRL_MASK));
        menuItemGotoSavedSearches.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icons/icon_search.png"))); // NOI18N
        menuItemGotoSavedSearches.setMnemonic('g');
        menuItemGotoSavedSearches.setText(Bundle.getString("AppFrame.menuItemGotoSavedSearches.text")); // NOI18N
        menuGoto.add(menuItemGotoSavedSearches);

        menuItemGotoCollections.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_3, java.awt.event.InputEvent.CTRL_MASK));
        menuItemGotoCollections.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icons/icon_imagecollection.png"))); // NOI18N
        menuItemGotoCollections.setMnemonic('i');
        menuItemGotoCollections.setText(Bundle.getString("AppFrame.menuItemGotoCollections.text")); // NOI18N
        menuGoto.add(menuItemGotoCollections);

        menuItemGotoCategories.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_4, java.awt.event.InputEvent.CTRL_MASK));
        menuItemGotoCategories.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icons/icon_category.png"))); // NOI18N
        menuItemGotoCategories.setMnemonic('k');
        menuItemGotoCategories.setText(Bundle.getString("AppFrame.menuItemGotoCategories.text")); // NOI18N
        menuGoto.add(menuItemGotoCategories);

        menuItemGotoFavoriteDirectories.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_5, java.awt.event.InputEvent.CTRL_MASK));
        menuItemGotoFavoriteDirectories.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icons/icon_favorite.png"))); // NOI18N
        menuItemGotoFavoriteDirectories.setMnemonic('f');
        menuItemGotoFavoriteDirectories.setText(Bundle.getString("AppFrame.menuItemGotoFavoriteDirectories.text")); // NOI18N
        menuGoto.add(menuItemGotoFavoriteDirectories);

        menuItemGotoKeywords.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK));
        menuItemGotoKeywords.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icons/icon_keyword.png"))); // NOI18N
        menuItemGotoKeywords.setMnemonic('w');
        menuItemGotoKeywords.setText(Bundle.getString("AppFrame.menuItemGotoKeywords.text")); // NOI18N
        menuGoto.add(menuItemGotoKeywords);

        menuItemGotoTimeline.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        menuItemGotoTimeline.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icons/icon_timeline.png"))); // NOI18N
        menuItemGotoTimeline.setMnemonic('z');
        menuItemGotoTimeline.setText(Bundle.getString("AppFrame.menuItemGotoTimeline.text")); // NOI18N
        menuGoto.add(menuItemGotoTimeline);

        menuItemGotoSelectionMiscMetadata.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_K, java.awt.event.InputEvent.CTRL_MASK));
        menuItemGotoSelectionMiscMetadata.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icons/icon_misc_metadata.png"))); // NOI18N
        menuItemGotoSelectionMiscMetadata.setMnemonic('c');
        menuItemGotoSelectionMiscMetadata.setText(Bundle.getString("AppFrame.menuItemGotoSelectionMiscMetadata.text")); // NOI18N
        menuGoto.add(menuItemGotoSelectionMiscMetadata);
        menuGoto.add(jSeparator8);

        menuItemGotoThumbnailsPanel.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_6, java.awt.event.InputEvent.CTRL_MASK));
        menuItemGotoThumbnailsPanel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icons/icon_thumbnails.png"))); // NOI18N
        menuItemGotoThumbnailsPanel.setMnemonic('v');
        menuItemGotoThumbnailsPanel.setText(Bundle.getString("AppFrame.menuItemGotoThumbnailsPanel.text")); // NOI18N
        menuGoto.add(menuItemGotoThumbnailsPanel);
        menuGoto.add(jSeparator9);

        menuItemGotoIptcMetadata.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_7, java.awt.event.InputEvent.CTRL_MASK));
        menuItemGotoIptcMetadata.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icons/icon_iptc.png"))); // NOI18N
        menuItemGotoIptcMetadata.setMnemonic('p');
        menuItemGotoIptcMetadata.setText(Bundle.getString("AppFrame.menuItemGotoIptcMetadata.text")); // NOI18N
        menuGoto.add(menuItemGotoIptcMetadata);

        menuItemGotoExifMetadata.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_8, java.awt.event.InputEvent.CTRL_MASK));
        menuItemGotoExifMetadata.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icons/icon_exif.png"))); // NOI18N
        menuItemGotoExifMetadata.setMnemonic('e');
        menuItemGotoExifMetadata.setText(Bundle.getString("AppFrame.menuItemGotoExifMetadata.text")); // NOI18N
        menuGoto.add(menuItemGotoExifMetadata);

        menuItemGotoXmpMetadata.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_9, java.awt.event.InputEvent.CTRL_MASK));
        menuItemGotoXmpMetadata.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icons/icon_xmp.png"))); // NOI18N
        menuItemGotoXmpMetadata.setMnemonic('x');
        menuItemGotoXmpMetadata.setText(Bundle.getString("AppFrame.menuItemGotoXmpMetadata.text")); // NOI18N
        menuGoto.add(menuItemGotoXmpMetadata);

        menuBar.add(menuGoto);

        menuView.setMnemonic('a');
        menuView.setText(Bundle.getString("AppFrame.menuView.text")); // NOI18N

        menuItemRefresh.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        menuItemRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icons/icon_refresh.png"))); // NOI18N
        menuItemRefresh.setMnemonic('a');
        menuItemRefresh.setText(Bundle.getString("AppFrame.menuItemRefresh.text")); // NOI18N
        menuView.add(menuItemRefresh);

        menuSort.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icons/icon_sort.png"))); // NOI18N
        menuSort.setMnemonic('s');
        menuSort.setText(Bundle.getString("AppFrame.menuSort.text")); // NOI18N

        radioButtonMenuItemSortFilenameAscending.setMnemonic('n');
        radioButtonMenuItemSortFilenameAscending.setText(Bundle.getString("AppFrame.radioButtonMenuItemSortFilenameAscending.text")); // NOI18N
        menuSort.add(radioButtonMenuItemSortFilenameAscending);

        radioButtonMenuItemSortFilenameDescending.setText(Bundle.getString("AppFrame.radioButtonMenuItemSortFilenameDescending.text")); // NOI18N
        menuSort.add(radioButtonMenuItemSortFilenameDescending);
        menuSort.add(jSeparator2);

        radioButtonMenuItemSortLastModifiedAscending.setMnemonic('d');
        radioButtonMenuItemSortLastModifiedAscending.setText(Bundle.getString("AppFrame.radioButtonMenuItemSortLastModifiedAscending.text")); // NOI18N
        menuSort.add(radioButtonMenuItemSortLastModifiedAscending);

        radioButtonMenuItemSortLastModifiedDescending.setText(Bundle.getString("AppFrame.radioButtonMenuItemSortLastModifiedDescending.text")); // NOI18N
        menuSort.add(radioButtonMenuItemSortLastModifiedDescending);
        menuSort.add(jSeparator3);

        radioButtonMenuItemSortFileTypeAscending.setMnemonic('t');
        radioButtonMenuItemSortFileTypeAscending.setText(Bundle.getString("AppFrame.radioButtonMenuItemSortFileTypeAscending.text")); // NOI18N
        menuSort.add(radioButtonMenuItemSortFileTypeAscending);

        radioButtonMenuItemSortFileTypeDescending.setText(Bundle.getString("AppFrame.radioButtonMenuItemSortFileTypeDescending.text")); // NOI18N
        menuSort.add(radioButtonMenuItemSortFileTypeDescending);

        menuView.add(menuSort);

        menuBar.add(menuView);

        menuTools.setMnemonic('w');
        menuTools.setText(Bundle.getString("AppFrame.menuTools.text")); // NOI18N

        menuItemToolIptcToXmp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        menuItemToolIptcToXmp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icons/icon_xmp.png"))); // NOI18N
        menuItemToolIptcToXmp.setMnemonic('i');
        menuItemToolIptcToXmp.setText(Bundle.getString("AppFrame.menuItemToolIptcToXmp.text")); // NOI18N
        menuTools.add(menuItemToolIptcToXmp);

        menuItemExtractEmbeddedXmp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icons/icon_xmp.png"))); // NOI18N
        menuItemExtractEmbeddedXmp.setMnemonic('b');
        menuItemExtractEmbeddedXmp.setText(Bundle.getString("AppFrame.menuItemExtractEmbeddedXmp.text")); // NOI18N
        menuTools.add(menuItemExtractEmbeddedXmp);

        menuItemRenameInXmp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        menuItemRenameInXmp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icons/icon_replace_in_xmp.png"))); // NOI18N
        menuItemRenameInXmp.setMnemonic('x');
        menuItemRenameInXmp.setText(Bundle.getString("AppFrame.menuItemRenameInXmp.text")); // NOI18N
        menuItemRenameInXmp.setEnabled(false);
        menuTools.add(menuItemRenameInXmp);

        menuItemActions.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, 0));
        menuItemActions.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icons/icon_action.png"))); // NOI18N
        menuItemActions.setMnemonic('a');
        menuItemActions.setText(Bundle.getString("AppFrame.menuItemActions.text")); // NOI18N
        menuTools.add(menuItemActions);

        menuBar.add(menuTools);

        menuHelp.setMnemonic('h');
        menuHelp.setText(Bundle.getString("AppFrame.menuHelp.text")); // NOI18N

        menuItemHelp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        menuItemHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icons/icon_help.png"))); // NOI18N
        menuItemHelp.setMnemonic('i');
        menuItemHelp.setText(Bundle.getString("AppFrame.menuItemHelp.text")); // NOI18N
        menuHelp.add(menuItemHelp);

        menuItemAbout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icons/icon_app_small.png"))); // NOI18N
        menuItemAbout.setMnemonic('b');
        menuItemAbout.setText(Bundle.getString("AppFrame.menuItemAbout.text")); // NOI18N
        menuHelp.add(menuItemAbout);

        menuBar.add(menuHelp);

        setJMenuBar(menuBar);
    }// </editor-fold>//GEN-END:initComponents

private void menuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemExitActionPerformed
    quit();
}//GEN-LAST:event_menuItemExitActionPerformed

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    quit();
}//GEN-LAST:event_formWindowClosing
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu menuEdit;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenu menuGoto;
    private javax.swing.JMenu menuHelp;
    private javax.swing.JMenuItem menuItemAbout;
    private javax.swing.JMenuItem menuItemActions;
    private javax.swing.JMenuItem menuItemCopy;
    private javax.swing.JMenuItem menuItemCopyFromAutocopyDirectory;
    private javax.swing.JMenuItem menuItemCut;
    private javax.swing.JMenuItem menuItemDelete;
    private javax.swing.JMenuItem menuItemExit;
    private javax.swing.JMenuItem menuItemExtractEmbeddedXmp;
    private javax.swing.JMenuItem menuItemFileSystemRename;
    private javax.swing.JMenuItem menuItemGotoCategories;
    private javax.swing.JMenuItem menuItemGotoCollections;
    private javax.swing.JMenuItem menuItemGotoDirectories;
    private javax.swing.JMenuItem menuItemGotoEdit;
    private javax.swing.JMenuItem menuItemGotoExifMetadata;
    private javax.swing.JMenuItem menuItemGotoFastSearch;
    private javax.swing.JMenuItem menuItemGotoFavoriteDirectories;
    private javax.swing.JMenuItem menuItemGotoIptcMetadata;
    private javax.swing.JMenuItem menuItemGotoKeywords;
    private javax.swing.JMenuItem menuItemGotoSavedSearches;
    private javax.swing.JMenuItem menuItemGotoSelectionMiscMetadata;
    private javax.swing.JMenuItem menuItemGotoThumbnailsPanel;
    private javax.swing.JMenuItem menuItemGotoTimeline;
    private javax.swing.JMenuItem menuItemGotoXmpMetadata;
    private javax.swing.JMenuItem menuItemHelp;
    private javax.swing.JMenuItem menuItemMaintainDatabase;
    private javax.swing.JMenuItem menuItemPaste;
    private javax.swing.JMenuItem menuItemRefresh;
    private javax.swing.JMenuItem menuItemRenameInXmp;
    private javax.swing.JMenuItem menuItemScanDirectory;
    private javax.swing.JMenuItem menuItemSearch;
    private javax.swing.JMenuItem menuItemSettings;
    private javax.swing.JMenuItem menuItemToolIptcToXmp;
    private javax.swing.JMenu menuSort;
    private javax.swing.JMenu menuTools;
    private javax.swing.JMenu menuView;
    private javax.swing.JRadioButtonMenuItem radioButtonMenuItemSortFileTypeAscending;
    private javax.swing.JRadioButtonMenuItem radioButtonMenuItemSortFileTypeDescending;
    private javax.swing.JRadioButtonMenuItem radioButtonMenuItemSortFilenameAscending;
    private javax.swing.JRadioButtonMenuItem radioButtonMenuItemSortFilenameDescending;
    private javax.swing.JRadioButtonMenuItem radioButtonMenuItemSortLastModifiedAscending;
    private javax.swing.JRadioButtonMenuItem radioButtonMenuItemSortLastModifiedDescending;
    // End of variables declaration//GEN-END:variables
}
