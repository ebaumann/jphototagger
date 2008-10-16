package de.elmar_baumann.imv.view.frames;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.AppInfo;
import de.elmar_baumann.imv.AppLock;
import de.elmar_baumann.imv.event.AppExitListener;
import de.elmar_baumann.imv.event.AppStartListener;
import de.elmar_baumann.imv.factory.MetaFactory;
import de.elmar_baumann.imv.io.FileSort;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.lib.persistence.PersistentAppSizes;
import de.elmar_baumann.lib.persistence.PersistentSettings;
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
public class AppFrame extends javax.swing.JFrame {

    private HashMap<FileSort, JRadioButtonMenuItem> menuItemOfSort = new HashMap<FileSort, JRadioButtonMenuItem>();
    private HashMap<JRadioButtonMenuItem, FileSort> sortOfMenuItem = new HashMap<JRadioButtonMenuItem, FileSort>();
    private Map<Goto, JMenuItem> menuItemOfGoto = new HashMap<Goto, JMenuItem>();
    private Map<JMenuItem, Goto> gotoOfMenuItem = new HashMap<JMenuItem, Goto>();
    private List<AppExitListener> exitListeners = new ArrayList<AppExitListener>();
    private List<AppStartListener> startListeners = new ArrayList<AppStartListener>();

    public AppFrame() {
        Panels.getInstance().setAppFrame(this);
        initComponents();
        postInitComponents();
    }

    private void initSortMenuItemsMap() {
        menuItemOfSort.put(FileSort.NamesAscending, radioButtonMenuItemSortFilenameAscending);
        menuItemOfSort.put(FileSort.NamesDescending, radioButtonMenuItemSortFilenameDescending);
        menuItemOfSort.put(FileSort.LastModifiedAscending, radioButtonMenuItemSortLastModifiedAscending);
        menuItemOfSort.put(FileSort.LastModifiedDescending, radioButtonMenuItemSortLastModifiedDescending);
        menuItemOfSort.put(FileSort.TypesAscending, radioButtonMenuItemSortFileTypeAscending);
        menuItemOfSort.put(FileSort.TypesDescending, radioButtonMenuItemSortFileTypeDescending);

        for (FileSort sort : menuItemOfSort.keySet()) {
            sortOfMenuItem.put(menuItemOfSort.get(sort), sort);
        }
    }
    
    private void initTotoMenuItemsMap() {
        menuItemOfGoto.put(Goto.Categories, menuItemGotoCategories);
        menuItemOfGoto.put(Goto.ImageCollections, menuItemGotoCollections);
        menuItemOfGoto.put(Goto.Directories, menuItemGotoDirectories);
        menuItemOfGoto.put(Goto.EditPanels, menuItemGotoEdit);
        menuItemOfGoto.put(Goto.ExifMetadata, menuItemGotoExifMetadata);
        menuItemOfGoto.put(Goto.FastSearch, menuItemGotoFastSearch);
        menuItemOfGoto.put(Goto.FavoriteDirectories, menuItemGotoFavoriteDirectories);
        menuItemOfGoto.put(Goto.IptcMetadata, menuItemGotoIptcMetadata);
        menuItemOfGoto.put(Goto.SavedSearches, menuItemGotoSavedSearches);
        menuItemOfGoto.put(Goto.XmpMetadata, menuItemGotoXmpMetadata);
        
        for (Goto gt : menuItemOfGoto.keySet()) {
            gotoOfMenuItem.put(menuItemOfGoto.get(gt), gt);
        }
    }
    
    public enum Goto {
        FastSearch,
        EditPanels,
        Directories,
        FavoriteDirectories,
        Categories,
        SavedSearches,
        ImageCollections,
        ExifMetadata,
        IptcMetadata,
        XmpMetadata,
    };

    private void postInitComponents() {
        initSortMenuItemsMap();
        initTotoMenuItemsMap();
        readPersistent();
        listenToClose();
        setTitleAndFrameIcon();
        MetaFactory.getInstance().startController();
        notifyStart();
    }

    public void addAppStartListener(AppStartListener listener) {
        startListeners.add(listener);
    }

    public void removeAppStartListener(AppStartListener listener) {
        startListeners.remove(listener);
    }

    private void notifyStart() {
        for (AppStartListener listener : startListeners) {
            listener.appWillStart();
        }
    }

    public void addAppExitListener(AppExitListener listener) {
        exitListeners.add(listener);
    }

    public void removeAppExitListener(AppExitListener listener) {
        exitListeners.remove(listener);
    }

    private void notifyExit() {
        for (AppExitListener listener : exitListeners) {
            listener.appWillExit();
        }
    }
    
    public Goto getGotoOfMenuItem(JMenuItem item) {
        return gotoOfMenuItem.get(item);
    }
    
    public JMenuItem getMenuItemOfGoto(Goto gt) {
        return menuItemOfGoto.get(gt);
    }
    
    public JMenuItem getMenuItemFileSystemDelete() {
        return menuItemFileSystemDelete;
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

    public JMenuItem getMenuItemFileSystemRename() {
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

    private void readPersistent() {
        PersistentAppSizes.getSizeAndLocation(this);
        pack();
    }

    private void writePersistent() {
        PersistentAppSizes.setSizeAndLocation(this);
        PersistentSettings.getInstance().writeToFile();
    }

    private void setTitleAndFrameIcon() {
        setIconImages(AppSettings.getAppIcons());
    }

    private void quit() {
        notifyExit();
        MetaFactory.getInstance().stopController();
        writePersistent();
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

        appPanel = new de.elmar_baumann.imv.view.panels.AppPanel();
        menuBar = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuItemScanDirectory = new javax.swing.JMenuItem();
        menuItemMaintainDatabase = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        menuItemExit = new javax.swing.JMenuItem();
        menuEdit = new javax.swing.JMenu();
        menuItemSettings = new javax.swing.JMenuItem();
        menuItemSearch = new javax.swing.JMenuItem();
        menuItemFileSystemRename = new javax.swing.JMenuItem();
        menuItemFileSystemDelete = new javax.swing.JMenuItem();
        menuGoto = new javax.swing.JMenu();
        menuItemGotoFastSearch = new javax.swing.JMenuItem();
        menuItemGotoEdit = new javax.swing.JMenuItem();
        menuItemGotoDirectories = new javax.swing.JMenuItem();
        menuItemGotoFavoriteDirectories = new javax.swing.JMenuItem();
        menuItemGotoSavedSearches = new javax.swing.JMenuItem();
        menuItemGotoCategories = new javax.swing.JMenuItem();
        menuItemGotoCollections = new javax.swing.JMenuItem();
        menuItemGotoExifMetadata = new javax.swing.JMenuItem();
        menuItemGotoIptcMetadata = new javax.swing.JMenuItem();
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
        menuItemScanDirectory.setText(Bundle.getString("AppFrame.menuItemScanDirectory.text")); // NOI18N
        menuFile.add(menuItemScanDirectory);

        menuItemMaintainDatabase.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
        menuItemMaintainDatabase.setText(Bundle.getString("AppFrame.menuItemMaintainDatabase.text")); // NOI18N
        menuFile.add(menuItemMaintainDatabase);
        menuFile.add(jSeparator1);

        menuItemExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
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
        menuItemSettings.setText(Bundle.getString("AppFrame.menuItemSettings.text")); // NOI18N
        menuEdit.add(menuItemSettings);

        menuItemSearch.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        menuItemSearch.setMnemonic('s');
        menuItemSearch.setText(Bundle.getString("AppFrame.menuItemSearch.text")); // NOI18N
        menuEdit.add(menuItemSearch);

        menuItemFileSystemRename.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        menuItemFileSystemRename.setText(Bundle.getString("AppFrame.menuItemFileSystemRename.text")); // NOI18N
        menuEdit.add(menuItemFileSystemRename);

        menuItemFileSystemDelete.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        menuItemFileSystemDelete.setText(Bundle.getString("AppFrame.menuItemFileSystemDelete.text")); // NOI18N
        menuItemFileSystemDelete.setToolTipText(Bundle.getString("AppFrame.menuItemFileSystemDelete.toolTipText")); // NOI18N
        menuEdit.add(menuItemFileSystemDelete);

        menuBar.add(menuEdit);

        menuGoto.setMnemonic('g');
        menuGoto.setText(Bundle.getString("AppFrame.menuGoto.text")); // NOI18N

        menuItemGotoFastSearch.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        menuItemGotoFastSearch.setMnemonic('s');
        menuItemGotoFastSearch.setText(Bundle.getString("AppFrame.menuItemGotoFastSearch.text")); // NOI18N
        menuItemGotoFastSearch.setToolTipText(Bundle.getString("AppFrame.menuItemGotoFastSearch.toolTipText")); // NOI18N
        menuGoto.add(menuItemGotoFastSearch);

        menuItemGotoEdit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_MASK));
        menuItemGotoEdit.setMnemonic('b');
        menuItemGotoEdit.setText(Bundle.getString("AppFrame.menuItemGotoEdit.text")); // NOI18N
        menuGoto.add(menuItemGotoEdit);

        menuItemGotoDirectories.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_1, java.awt.event.InputEvent.CTRL_MASK));
        menuItemGotoDirectories.setMnemonic('o');
        menuItemGotoDirectories.setText(Bundle.getString("AppFrame.menuItemGotoDirectories.text")); // NOI18N
        menuGoto.add(menuItemGotoDirectories);

        menuItemGotoFavoriteDirectories.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_2, java.awt.event.InputEvent.CTRL_MASK));
        menuItemGotoFavoriteDirectories.setMnemonic('f');
        menuItemGotoFavoriteDirectories.setText(Bundle.getString("AppFrame.menuItemGotoFavoriteDirectories.text")); // NOI18N
        menuGoto.add(menuItemGotoFavoriteDirectories);

        menuItemGotoSavedSearches.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_3, java.awt.event.InputEvent.CTRL_MASK));
        menuItemGotoSavedSearches.setMnemonic('g');
        menuItemGotoSavedSearches.setText(Bundle.getString("AppFrame.menuItemGotoSavedSearches.text")); // NOI18N
        menuGoto.add(menuItemGotoSavedSearches);

        menuItemGotoCategories.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_4, java.awt.event.InputEvent.CTRL_MASK));
        menuItemGotoCategories.setMnemonic('k');
        menuItemGotoCategories.setText(Bundle.getString("AppFrame.menuItemGotoCategories.text")); // NOI18N
        menuGoto.add(menuItemGotoCategories);

        menuItemGotoCollections.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_5, java.awt.event.InputEvent.CTRL_MASK));
        menuItemGotoCollections.setMnemonic('i');
        menuItemGotoCollections.setText(Bundle.getString("AppFrame.menuItemGotoCollections.text")); // NOI18N
        menuGoto.add(menuItemGotoCollections);

        menuItemGotoExifMetadata.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_6, java.awt.event.InputEvent.CTRL_MASK));
        menuItemGotoExifMetadata.setMnemonic('e');
        menuItemGotoExifMetadata.setText(Bundle.getString("AppFrame.menuItemGotoExifMetadata.text")); // NOI18N
        menuGoto.add(menuItemGotoExifMetadata);

        menuItemGotoIptcMetadata.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_7, java.awt.event.InputEvent.CTRL_MASK));
        menuItemGotoIptcMetadata.setMnemonic('p');
        menuItemGotoIptcMetadata.setText(Bundle.getString("AppFrame.menuItemGotoIptcMetadata.text")); // NOI18N
        menuGoto.add(menuItemGotoIptcMetadata);

        menuItemGotoXmpMetadata.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_8, java.awt.event.InputEvent.CTRL_MASK));
        menuItemGotoXmpMetadata.setMnemonic('x');
        menuItemGotoXmpMetadata.setText(Bundle.getString("AppFrame.menuItemGotoXmpMetadata.text")); // NOI18N
        menuGoto.add(menuItemGotoXmpMetadata);

        menuBar.add(menuGoto);

        menuView.setMnemonic('a');
        menuView.setText(Bundle.getString("AppFrame.menuView.text")); // NOI18N

        menuItemRefresh.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        menuItemRefresh.setMnemonic('a');
        menuItemRefresh.setText(Bundle.getString("AppFrame.menuItemRefresh.text")); // NOI18N
        menuView.add(menuItemRefresh);

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

        menuItemToolIptcToXmp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        menuItemToolIptcToXmp.setMnemonic('i');
        menuItemToolIptcToXmp.setText(Bundle.getString("AppFrame.menuItemToolIptcToXmp.text")); // NOI18N
        menuTools.add(menuItemToolIptcToXmp);

        menuBar.add(menuTools);

        menuHelp.setMnemonic('h');
        menuHelp.setText(Bundle.getString("AppFrame.menuHelp.text")); // NOI18N

        menuItemHelp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        menuItemHelp.setMnemonic('i');
        menuItemHelp.setText(Bundle.getString("AppFrame.menuItemHelp.text")); // NOI18N
        menuHelp.add(menuItemHelp);

        menuItemAbout.setMnemonic('b');
        menuItemAbout.setText(Bundle.getString("AppFrame.menuItemAbout.text")); // NOI18N
        menuHelp.add(menuItemAbout);

        menuBar.add(menuHelp);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(appPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(appPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

private void menuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemExitActionPerformed
    quit();
}//GEN-LAST:event_menuItemExitActionPerformed

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    quit();
}//GEN-LAST:event_formWindowClosing
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.elmar_baumann.imv.view.panels.AppPanel appPanel;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu menuEdit;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenu menuGoto;
    private javax.swing.JMenu menuHelp;
    private javax.swing.JMenuItem menuItemAbout;
    private javax.swing.JMenuItem menuItemExit;
    private javax.swing.JMenuItem menuItemFileSystemDelete;
    private javax.swing.JMenuItem menuItemFileSystemRename;
    private javax.swing.JMenuItem menuItemGotoCategories;
    private javax.swing.JMenuItem menuItemGotoCollections;
    private javax.swing.JMenuItem menuItemGotoDirectories;
    private javax.swing.JMenuItem menuItemGotoEdit;
    private javax.swing.JMenuItem menuItemGotoExifMetadata;
    private javax.swing.JMenuItem menuItemGotoFastSearch;
    private javax.swing.JMenuItem menuItemGotoFavoriteDirectories;
    private javax.swing.JMenuItem menuItemGotoIptcMetadata;
    private javax.swing.JMenuItem menuItemGotoSavedSearches;
    private javax.swing.JMenuItem menuItemGotoXmpMetadata;
    private javax.swing.JMenuItem menuItemHelp;
    private javax.swing.JMenuItem menuItemMaintainDatabase;
    private javax.swing.JMenuItem menuItemRefresh;
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
