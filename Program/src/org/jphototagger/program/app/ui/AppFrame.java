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
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu.Separator;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.windows.MainWindowMenuProvider;
import org.jphototagger.api.windows.MenuItemProvider;
import org.jphototagger.lib.api.LayerUtil;
import org.jphototagger.lib.api.LookAndFeelChangedEvent;
import org.jphototagger.lib.api.PositionProviderAscendingComparator;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.swing.FrameExt;
import org.jphototagger.lib.swing.KeyEventUtil;
import org.jphototagger.lib.swing.util.MenuUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.AppInfo;
import org.jphototagger.program.app.AppLifeCycle;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.resources.Icons;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class AppFrame extends FrameExt {

    private static final long serialVersionUID = 1L;
    private final Map<GoTo, JMenuItem> menuItemOfGoto = new EnumMap<>(GoTo.class);
    private final Map<JMenuItem, GoTo> gotoOfMenuItem = new HashMap<>();
    private AppPanel appPanel;
    private int lastGotoSelectionItemIndex = 8;
    private int lastGotoEditItemIndex = 13;
    private int lastSelectionGotoItemAcceleratorKeyCode = 0x37;
    private int lastEditGotoItemAcceleratorKeyCode = 0x30;

    private void initGotoMenuItemsMap() {
        menuItemOfGoto.put(GoTo.DIRECTORIES, menuItemGotoDirectories);
        menuItemOfGoto.put(GoTo.FAVORITES, menuItemGotoFavorites);
        menuItemOfGoto.put(GoTo.KEYWORDS_EDIT, menuItemGotoKeywordsEdit);
        menuItemOfGoto.put(GoTo.IMAGE_COLLECTIONS, menuItemGotoCollections);
        menuItemOfGoto.put(GoTo.KEYWORDS_SEL, menuItemGotoKeywordsSel);
        menuItemOfGoto.put(GoTo.MISC_METADATA, menuItemGotoMiscMetadata);
        menuItemOfGoto.put(GoTo.SAVED_SEARCHES, menuItemGotoSavedSearches);
        menuItemOfGoto.put(GoTo.TIMELINE, menuItemGotoTimeline);

        for (GoTo gt : menuItemOfGoto.keySet()) {
            gotoOfMenuItem.put(menuItemOfGoto.get(gt), gt);
        }
    }

    public enum GoTo {
        DIRECTORIES,
        FAVORITES,
        KEYWORDS_EDIT,
        IMAGE_COLLECTIONS,
        KEYWORDS_SEL,
        MISC_METADATA,
        SAVED_SEARCHES,
        TIMELINE,
    }

    public AppFrame() {
        init();
    }

    private void init() {
        initComponents();
        postInitComponents();
        GUI.setAppFrame(this);
        addAppPanel();
        lookupMenuItems();
        MenuUtil.setMnemonics(menuBar);
        initGotoMenuItemsMap();
        setIconImages(Icons.getAppIcons());
        AnnotationProcessor.process(this);
        AppLifeCycle.INSTANCE.started(this);
    }

    private void postInitComponents() {
        menuItemGotoDirectories.setIcon(Icons.getIcon("icon_folder.png")); // NOI18N
        menuItemGotoSavedSearches.setIcon(Icons.getIcon("icon_search.png")); // NOI18N
        menuItemGotoCollections.setIcon(Icons.getIcon("icon_imagecollection.png")); // NOI18N
        menuItemGotoFavorites.setIcon(Icons.getIcon("icon_favorite.png")); // NOI18N
        menuItemGotoKeywordsSel.setIcon(Icons.getIcon("icon_keyword.png")); // NOI18N
        menuItemGotoTimeline.setIcon(Icons.getIcon("icon_timeline.png")); // NOI18N
        menuItemGotoMiscMetadata.setIcon(Icons.getIcon("icon_misc_metadata.png")); // NOI18N
        menuItemGotoKeywordsEdit.setIcon(Icons.getIcon("icon_keyword.png")); // NOI18N
    }

    public AppPanel getAppPanel() {
        return appPanel;
    }

    private void addAppPanel() {
        appPanel = new AppPanel();
        getContentPane().add(appPanel);
    }

    public GoTo getGotoOfMenuItem(JMenuItem item) {
        return gotoOfMenuItem.get(item);
    }

    public JMenuItem getMenuItemOfGoto(GoTo gt) {
        return menuItemOfGoto.get(gt);
    }

    @Override
    public void setTitle(String title) {
        if (title.equals(AppInfo.APP_NAME)) {
            super.setTitle(AppInfo.APP_NAME + " " + AppInfo.APP_VERSION);
        } else {
            super.setTitle(title + " - " + AppInfo.APP_NAME + " " + AppInfo.APP_VERSION);
        }
    }

    private void lookupMenuItems() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                Collection<? extends MainWindowMenuProvider> providers = Lookup.getDefault().lookupAll(MainWindowMenuProvider.class);
                List<MenuItemProvider> editMenuItems = new ArrayList<>();
                List<MenuItemProvider> fileMenuItems = new ArrayList<>();
                List<MenuItemProvider> gotoMenuItems = new ArrayList<>();
                List<MenuItemProvider> toolsMenuItems = new ArrayList<>();
                List<MenuItemProvider> viewMenuItems = new ArrayList<>();
                List<MenuItemProvider> helpMenuItems = new ArrayList<>();
                List<MenuItemProvider> windowMenuItems = new ArrayList<>();
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

    private void addMenuItems(List<? extends MenuItemProvider> menuItemProviders, JMenu menu) {
        Collections.sort(menuItemProviders, PositionProviderAscendingComparator.INSTANCE);
        LayerUtil.logWarningIfNotUniquePositions(menuItemProviders);
        for (MenuItemProvider menuItemProvider : menuItemProviders) {
            addMenuItem(menuItemProvider, menu);
        }
    }

    private void addMenuItem(MenuItemProvider menuItemProvider, JMenu menu) {
        int position = menuItemProvider.getPosition();
        JMenuItem menuItem = menuItemProvider.getMenuItem();
        int itemCount = menu.getItemCount();
        int index = position < 0 || position > itemCount ? itemCount : position;

        if (menuItemProvider.isSeparatorBefore()) {
            menu.add(new Separator(), index);
            index++;
        }

        menu.add(menuItem, index);
    }

    void addGotoMenuItemForSelectionWindow(Action action) {
        JMenuItem item = UiFactory.menuItem(action);

        if (!hasAccelerator(action) && lastSelectionGotoItemAcceleratorKeyCode > 0x30 && lastSelectionGotoItemAcceleratorKeyCode <= 0x39) { // 0x30: TN-Panel
            KeyStroke keyStroke = KeyEventUtil.getKeyStrokeMenuShortcut(lastSelectionGotoItemAcceleratorKeyCode + 1);
            item.setAccelerator(keyStroke);
            lastSelectionGotoItemAcceleratorKeyCode++;
        }
        menuGoto.insert(item, lastGotoSelectionItemIndex + 1);
        lastGotoSelectionItemIndex++;
    }

    void addGotoMenuItem(Action action, int index, boolean separatorBefore) {
        if (separatorBefore) {
            menuGoto.add(new Separator(), index);
        }
        JMenuItem item = UiFactory.menuItem(action);
        MnemonicUtil.setMnemonics(item);
        menuGoto.insert(item, separatorBefore ? index + 1 : index);
    }

    private boolean hasAccelerator(Action action) {
        if (action instanceof AbstractAction) {
            AbstractAction abstractAction = (AbstractAction) action;
            Object value = abstractAction.getValue(Action.ACCELERATOR_KEY);
            return value != null;
        }
        return false;
    }

    void addGotoMenuItemForEditWindow(Action action) {
        JMenuItem item = UiFactory.menuItem(action);
        if (!hasAccelerator(action) && lastEditGotoItemAcceleratorKeyCode >= 0x30 && lastEditGotoItemAcceleratorKeyCode <= 0x39) {
            KeyStroke keyStroke = KeyStroke.getKeyStroke(lastEditGotoItemAcceleratorKeyCode + 1, InputEvent.ALT_MASK);
            item.setAccelerator(keyStroke);
            lastEditGotoItemAcceleratorKeyCode++;
        }
        menuGoto.insert(item, lastGotoEditItemIndex + 1);
        lastGotoEditItemIndex++;
    }

    @EventSubscriber(eventClass = LookAndFeelChangedEvent.class)
    public void lookAndFeelChanged(LookAndFeelChangedEvent evt) {
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void initComponents() {

        buttonGroupSort = new javax.swing.ButtonGroup();
        menuBar = UiFactory.menuBar();
        menuFile = UiFactory.menu();
        menuEdit = UiFactory.menu();
        menuView = UiFactory.menu();
        menuGoto = UiFactory.menu();
        sep17 = new javax.swing.JPopupMenu.Separator();
        menuItemGotoDirectories = UiFactory.menuItem();
        menuItemGotoSavedSearches = UiFactory.menuItem();
        menuItemGotoCollections = UiFactory.menuItem();
        menuItemGotoFavorites = UiFactory.menuItem();
        menuItemGotoKeywordsSel = UiFactory.menuItem();
        menuItemGotoTimeline = UiFactory.menuItem();
        menuItemGotoMiscMetadata = UiFactory.menuItem();
        sep18 = new javax.swing.JPopupMenu.Separator();
        menuItemGotoKeywordsEdit = UiFactory.menuItem();
        menuTools = UiFactory.menu();
        menuWindow = UiFactory.menu();
        menuHelp = UiFactory.menu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(AppInfo.APP_NAME);
        

        menuBar.setName("menuBar"); // NOI18N

        menuFile.setText(Bundle.getString(getClass(), "AppFrame.menuFile.text")); // NOI18N
        menuFile.setName("menuFile"); // NOI18N
        menuBar.add(menuFile);

        menuEdit.setText(Bundle.getString(getClass(), "AppFrame.menuEdit.text")); // NOI18N
        menuEdit.setName("menuEdit"); // NOI18N
        menuBar.add(menuEdit);

        menuView.setText(Bundle.getString(getClass(), "AppFrame.menuView.text")); // NOI18N
        menuView.setName("menuView"); // NOI18N
        menuBar.add(menuView);

        menuGoto.setText(Bundle.getString(getClass(), "AppFrame.menuGoto.text")); // NOI18N
        menuGoto.setName("menuGoto"); // NOI18N

        sep17.setName("sep17"); // NOI18N
        menuGoto.add(sep17);

        menuItemGotoDirectories.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_1));
        menuItemGotoDirectories.setText(Bundle.getString(getClass(), "AppFrame.menuItemGotoDirectories.text")); // NOI18N
        menuItemGotoDirectories.setName("menuItemGotoDirectories"); // NOI18N
        menuGoto.add(menuItemGotoDirectories);

        menuItemGotoSavedSearches.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_2));
        menuItemGotoSavedSearches.setText(Bundle.getString(getClass(), "AppFrame.menuItemGotoSavedSearches.text")); // NOI18N
        menuItemGotoSavedSearches.setName("menuItemGotoSavedSearches"); // NOI18N
        menuGoto.add(menuItemGotoSavedSearches);

        menuItemGotoCollections.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_3));
        menuItemGotoCollections.setText(Bundle.getString(getClass(), "AppFrame.menuItemGotoCollections.text")); // NOI18N
        menuItemGotoCollections.setName("menuItemGotoCollections"); // NOI18N
        menuGoto.add(menuItemGotoCollections);

        menuItemGotoFavorites.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_4));
        menuItemGotoFavorites.setText(Bundle.getString(getClass(), "AppFrame.menuItemGotoFavorites.text")); // NOI18N
        menuItemGotoFavorites.setName("menuItemGotoFavorites"); // NOI18N
        menuGoto.add(menuItemGotoFavorites);

        menuItemGotoKeywordsSel.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_5));
        menuItemGotoKeywordsSel.setText(Bundle.getString(getClass(), "AppFrame.menuItemGotoKeywordsSel.text")); // NOI18N
        menuItemGotoKeywordsSel.setName("menuItemGotoKeywordsSel"); // NOI18N
        menuGoto.add(menuItemGotoKeywordsSel);

        menuItemGotoTimeline.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_6));
        menuItemGotoTimeline.setText(Bundle.getString(getClass(), "AppFrame.menuItemGotoTimeline.text")); // NOI18N
        menuItemGotoTimeline.setName("menuItemGotoTimeline"); // NOI18N
        menuGoto.add(menuItemGotoTimeline);

        menuItemGotoMiscMetadata.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_7));
        menuItemGotoMiscMetadata.setText(Bundle.getString(getClass(), "AppFrame.menuItemGotoMiscMetadata.text")); // NOI18N
        menuItemGotoMiscMetadata.setName("menuItemGotoMiscMetadata"); // NOI18N
        menuGoto.add(menuItemGotoMiscMetadata);

        sep18.setName("sep18"); // NOI18N
        menuGoto.add(sep18);

        menuItemGotoKeywordsEdit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_4, java.awt.event.InputEvent.ALT_MASK));
        menuItemGotoKeywordsEdit.setText(Bundle.getString(getClass(), "AppFrame.menuItemGotoKeywordsEdit.text")); // NOI18N
        menuItemGotoKeywordsEdit.setName("menuItemGotoKeywordsEdit"); // NOI18N
        menuGoto.add(menuItemGotoKeywordsEdit);

        menuBar.add(menuGoto);

        menuTools.setText(Bundle.getString(getClass(), "AppFrame.menuTools.text")); // NOI18N
        menuTools.setName("menuTools"); // NOI18N
        menuBar.add(menuTools);

        menuWindow.setText(Bundle.getString(getClass(), "AppFrame.menuWindow.text")); // NOI18N
        menuWindow.setName("menuWindow"); // NOI18N
        menuBar.add(menuWindow);

        menuHelp.setText(Bundle.getString(getClass(), "AppFrame.menuHelp.text")); // NOI18N
        menuHelp.setName("menuHelp"); // NOI18N
        menuBar.add(menuHelp);

        setJMenuBar(menuBar);
    }

    private javax.swing.ButtonGroup buttonGroupSort;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu menuEdit;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenu menuGoto;
    private javax.swing.JMenu menuHelp;
    private javax.swing.JMenuItem menuItemGotoCollections;
    private javax.swing.JMenuItem menuItemGotoDirectories;
    private javax.swing.JMenuItem menuItemGotoFavorites;
    private javax.swing.JMenuItem menuItemGotoKeywordsEdit;
    private javax.swing.JMenuItem menuItemGotoKeywordsSel;
    private javax.swing.JMenuItem menuItemGotoMiscMetadata;
    private javax.swing.JMenuItem menuItemGotoSavedSearches;
    private javax.swing.JMenuItem menuItemGotoTimeline;
    private javax.swing.JMenu menuTools;
    private javax.swing.JMenu menuView;
    private javax.swing.JMenu menuWindow;
    private javax.swing.JPopupMenu.Separator sep17;
    private javax.swing.JPopupMenu.Separator sep18;
}
