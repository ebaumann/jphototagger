package org.jphototagger.program.app.ui;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.TreeSelectionModel;
import org.bushe.swing.event.EventBus;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTree;
import org.jphototagger.api.messages.MessageType;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.windows.MainWindowComponent;
import org.jphototagger.api.windows.MainWindowComponentProvider;
import org.jphototagger.api.windows.StatusLineElementProvider;
import org.jphototagger.api.windows.TabInEditWindowDisplayedEvent;
import org.jphototagger.api.windows.TabInSelectionWindowDisplayedEvent;
import org.jphototagger.domain.metadata.search.SearchComponent;
import org.jphototagger.domain.thumbnails.MainWindowThumbnailsComponent;
import org.jphototagger.lib.api.LayerUtil;
import org.jphototagger.lib.api.PositionProviderAscendingComparator;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.swing.KeyEventUtil;
import org.jphototagger.lib.swing.MessageLabel;
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.lib.swing.TreeExpandCollapseAllAction;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.swingx.ListTextFilter;
import org.jphototagger.lib.swingx.SearchInJxListAction;
import org.jphototagger.lib.swingx.SearchInJxTreeAction;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.module.keywords.KeywordsPanel;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.resources.Icons;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 * @author Tobias Stening
 */
public final class AppPanel extends PanelExt {

    private static final long serialVersionUID = 1L;
    private static final String KEY_DIVIDER_LOCATION_MAIN = "AppPanel.DividerLocationMain";
    private static final String KEY_DIVIDER_LOCATION_THUMBNAILS = "AppPanel.DividerLocationThumbnails";
    private static final int DEFAULT_DIVIDER_LOCATION_MAIN = 100;
    private static final int DEFAULT_DIVIDER_LOCATION_THUMBNAILS = 200;
    private transient MessageLabel messageLabel;
    private final List<JTree> selectionTrees = new ArrayList<>();
    private final List<JXList> selectionLists = new ArrayList<>();
    private ListTextFilter listSelKeywordsTextFilter;
    private ListTextFilter listImageCollectionsTextFilter;
    private ListTextFilter listSavedSearchesTextFilter;

    private static final Logger LOGGER = Logger.getLogger(AppPanel.class.getName());

    public AppPanel() {
        init();
    }

    private void init() {
        initComponents();
        messageLabel = new MessageLabel(labelStatusbarText);
        setTreesSingleSelection();
        initCollections();
        initListTextFilters();
        setMnemonics();
        GUI.setAppPanel(this);
        lookupWindows();
        lookupStatusLineElements();
        tabbedPaneMetadata.addChangeListener(tabbedPaneEditChangeListener);
        tabbedPaneSelection.addChangeListener(tabbedPaneSelectionChangeListener);
        treeDirectories.setRowHeight(0);
        treeFavorites.setRowHeight(0);
        treeMiscMetadata.setRowHeight(0);
        treeSelKeywords.setRowHeight(0);
        treeTimeline.setRowHeight(0);

        tabbedPaneSelection.setIconAt(0, Icons.getIcon("icon_folder.png"));
        tabbedPaneSelection.setIconAt(1, Icons.getIcon("icon_search.png"));
        tabbedPaneSelection.setIconAt(2, Icons.getIcon("icon_imagecollection.png"));
        tabbedPaneSelection.setIconAt(3, Icons.getIcon("icon_favorite.png"));
        tabbedPaneSelection.setIconAt(4, Icons.getIcon("icon_keyword.png"));
        tabbedPaneSelection.setIconAt(5, Icons.getIcon("icon_timeline.png"));
        tabbedPaneSelection.setIconAt(6, Icons.getIcon("icon_misc_metadata.png"));
        tabbedPaneMetadata.setIconAt(4, Icons.getIcon("icon_keyword.png"));
    }

    private void setMnemonics() {

        // Do not set mnemonics to left panel because it can trigger edit actions!
        MnemonicUtil.setMnemonics((Container) panelSearch);
    }

    private void initListTextFilters() {
        listSelKeywordsTextFilter = new ListTextFilter(listSelKeywords);
        listImageCollectionsTextFilter = new ListTextFilter(listImageCollections);
        listSavedSearchesTextFilter = new ListTextFilter(listSavedSearches);

        listSelKeywordsTextFilter.filterOnDocumentChanges(textFieldListSelKeywordsFilter.getDocument());
        listSavedSearchesTextFilter.filterOnDocumentChanges(textFieldListSavedSearchesFilter.getDocument());
        listImageCollectionsTextFilter.filterOnDocumentChanges(textFieldListImageCollectionsFilter.getDocument());
    }

    private void initCollections() {
        initSelectionTreesCollection();
        initSelectionListsCollection();
    }

    private void initSelectionTreesCollection() {
        selectionTrees.add(treeDirectories);
        selectionTrees.add(treeFavorites);
        selectionTrees.add(treeMiscMetadata);
        selectionTrees.add(treeTimeline);
        selectionTrees.add(treeSelKeywords);
    }

    private void initSelectionListsCollection() {
        selectionLists.add(listImageCollections);
        selectionLists.add(listSelKeywords);
        selectionLists.add(listSavedSearches);
    }

    private int getDividerLocationMain() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        int location = prefs.getInt(KEY_DIVIDER_LOCATION_MAIN);

        return (location >= 0)
               ? location
               : DEFAULT_DIVIDER_LOCATION_MAIN;
    }

    private void setTreesSingleSelection() {
        setSingleSelection(treeDirectories);
        setSingleSelection(treeFavorites);
        setSingleSelection(treeMiscMetadata);
        setSingleSelection(treeTimeline);
        setSingleSelection(treeSelKeywords);
    }

    private void setSingleSelection(JTree tree) {
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

    public enum SelectAlso { NOTHING_ELSE, SEL_KEYWORDS_TAB }

    public void displaySelKeywordsTree(SelectAlso select) {
        if (select == null) {
            throw new NullPointerException("select == null");
        }

        if (select.equals(SelectAlso.SEL_KEYWORDS_TAB)) {
            tabbedPaneSelection.setSelectedComponent(panelSelKeywords);
        }

        displaySelKeywordsCard("keywordsTree");
    }

    public void displaySelKeywordsList(SelectAlso select) {
        if (select == null) {
            throw new NullPointerException("select == null");
        }

        if (select.equals(SelectAlso.SEL_KEYWORDS_TAB)) {
            tabbedPaneSelection.setSelectedComponent(panelSelKeywords);
        }

        displaySelKeywordsCard("flatKeywords");
    }

    private void displaySelKeywordsCard(String name) {
        CardLayout cl = (CardLayout) (panelSelKeywords.getLayout());

        cl.show(panelSelKeywords, name);
    }

    private int getDividerLocationThumbnails() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        int location = prefs.getInt(KEY_DIVIDER_LOCATION_THUMBNAILS);

        return (location >= 0)
               ? location
               : DEFAULT_DIVIDER_LOCATION_THUMBNAILS;
    }

    void setStatusbarText(String text, MessageType type, final long milliseconds) {
        if (milliseconds > 0) {
            messageLabel.showMessage(text, type, milliseconds);
        } else {
            labelStatusbarText.setText(text);
        }
        LOGGER.log(Level.FINE, text);
    }

    public JTabbedPane getTabbedPaneMetadata() {
        return tabbedPaneMetadata;
    }

    public boolean isSelectionComponentSelected(Component component) {
        Component selectedComponent = tabbedPaneSelection.getSelectedComponent();
        return component == selectedComponent;
    }

    public boolean isEditComponentSelected(Component component) {
        Component selectedComponent = tabbedPaneMetadata.getSelectedComponent();
        return component == selectedComponent;
    }

    public JTabbedPane getTabbedPaneSelection() {
        return tabbedPaneSelection;
    }

    public Component getTabEditKeywords() {
        return panelEditKeywords;
    }

    public Component getTabSelectionKeywords() {
        return panelSelKeywords;
    }

    public Component getCardSelKeywordsList() {
        return panelSelKeywordsList;
    }

    public Component getCardSelKeywordsTree() {
        return panelSelKeywordsTree;
    }

    public Component getTabSelectionDirectories() {
        return panelDirectories;
    }

    public Component getTabSelectionFavoriteDirectories() {
        return panelFavorites;
    }

    public Component getTabSelectionImageCollections() {
        return panelImageCollections;
    }

    public Component getTabSelectionTimeline() {
        return panelTimeline;
    }

    public Component getTabSelectionMiscMetadata() {
        return panelMiscMetadata;
    }

    public JPanel getTabSelectionSavedSearches() {
        return panelSavedSearches;
    }

    public JTree getTreeTimeline() {
        return treeTimeline;
    }

    public JTree getTreeMiscMetadata() {
        return treeMiscMetadata;
    }

    public JTree getTreeEditKeywords() {
        return panelEditKeywords.getTree();
    }

    public JTree getTreeSelKeywords() {
        return treeSelKeywords;
    }

    public List<JTree> getSelectionTrees() {
        return Collections.unmodifiableList(selectionTrees);
    }

    public List<JXList> getSelectionLists() {
        return Collections.unmodifiableList(selectionLists);
    }

    public KeywordsPanel getPanelEditKeywords() {
        return panelEditKeywords;
    }

    public JXList getListImageCollections() {
        return listImageCollections;
    }

    public JXList getListSavedSearches() {
        return listSavedSearches;
    }

    public JTree getTreeDirectories() {
        return treeDirectories;
    }

    public JTree getTreeFavorites() {
        return treeFavorites;
    }

    public JXList getListSelKeywords() {
        return listSelKeywords;
    }

    public JXList getListEditKeywords() {
        return panelEditKeywords.getList();
    }

    public JToggleButton getToggleButtonSelKeywords() {
        return toggleButtonExpandAllNodesSelKeywords;
    }

    public JRadioButton getRadioButtonSelKeywordsMultipleSelAll() {
        return radioButtonSelKeywordsMultipleSelAll;
    }

    public JRadioButton getRadioButtonSelKeywordsMultipleSelOne() {
        return radioButtonSelKeywordsMultipleSelOne;
    }

    public JSplitPane getSplitPaneMain() {
        return splitPaneMain;
    }

    public JSplitPane getSplitPaneThumbnailsMetadata() {
        return splitPaneThumbnailsMetadata;
    }

    public JCheckBox getCheckBoxDirectoriesRecursive() {
        return checkBoxDirectoriesRecursive;
    }

    public JCheckBox getCheckBoxFavoritesRecursive() {
        return checkBoxFavoritesRecursive;
    }

    private void lookupWindows() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                lookupSearchComponent();
                lookupMainWindowThumbnailsComponent();
                Collection<? extends MainWindowComponentProvider> providers = Lookup.getDefault().lookupAll(MainWindowComponentProvider.class);
                List<MainWindowComponent> selectionsComponents = new ArrayList<>();
                List<MainWindowComponent> editComponents = new ArrayList<>();
                for (MainWindowComponentProvider provider : providers) {
                    editComponents.addAll(provider.getMainWindowEditComponents());
                    selectionsComponents.addAll(provider.getMainWindowSelectionComponents());
                }
                instertIntoEditTabbedPane(editComponents);
                insertIntoSelectionTabbedPane(selectionsComponents);
            }
        });
    }

    private void lookupSearchComponent() {
        SearchComponent searchComponent = Lookup.getDefault().lookup(SearchComponent.class);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panelSearch.add(searchComponent.getSearchComponent(), gbc);
        GUI.getAppFrame().addGotoMenuItem(searchComponent.getSelectSearchComponentAction(), 0, false);
    }

    private void lookupMainWindowThumbnailsComponent() {
        MainWindowThumbnailsComponent tComponent = Lookup.getDefault().lookup(MainWindowThumbnailsComponent.class);
        Component component = tComponent.getThumbnailsComponent();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        thumbnailPanelComponent.add(component, gbc);
        SelectThumbnailsPanelAction selectThumbnailsPanelAction =
                new SelectThumbnailsPanelAction(tComponent.getThumbnailsDisplayingComponent());
        GUI.getAppFrame().addGotoMenuItem(selectThumbnailsPanelAction, 2, false);

    }

    private static class SelectThumbnailsPanelAction extends AbstractAction {

        private static final long serialVersionUID = 1L;
        private final Component thumbnailsPanel;

        private SelectThumbnailsPanelAction(Component thumbnailsPanel) {
            super(Bundle.getString(AppPanel.class, "SelectThumbnailsPanelAction.Name"));
            this.thumbnailsPanel = thumbnailsPanel;
            init();
        }

        private void init() {
            putValue(ACCELERATOR_KEY, KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_0));
            putValue(SMALL_ICON, Icons.getIcon("icon_thumbnails.png"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            thumbnailsPanel.requestFocusInWindow();
        }
    }

    private void instertIntoEditTabbedPane(List<MainWindowComponent> mainWindowComponents) {
        Collections.sort(mainWindowComponents, PositionProviderAscendingComparator.INSTANCE);
        LayerUtil.logWarningIfNotUniquePositions(mainWindowComponents);
        for (MainWindowComponent component : mainWindowComponents) {
            dockIntoTabbedPane(component, tabbedPaneMetadata);
            SelectTabAction selectTabAction = createSelectTabAction(component, tabbedPaneMetadata);
            GUI.getAppFrame().addGotoMenuItemForEditWindow(selectTabAction);
        }
    }

    private void insertIntoSelectionTabbedPane(List<MainWindowComponent> mainWindowComponents) {
        Collections.sort(mainWindowComponents, PositionProviderAscendingComparator.INSTANCE);
        LayerUtil.logWarningIfNotUniquePositions(mainWindowComponents);
        for (MainWindowComponent component : mainWindowComponents) {
            dockIntoTabbedPane(component, tabbedPaneSelection);
            SelectTabAction selectTabAction = createSelectTabAction(component, tabbedPaneSelection);
            GUI.getAppFrame().addGotoMenuItemForSelectionWindow(selectTabAction);
        }
    }

    private SelectTabAction createSelectTabAction(MainWindowComponent mainWindowComponent, JTabbedPane tabbedPane) {
        SelectTabAction action = new SelectTabAction(tabbedPane, mainWindowComponent.getComponent());
        action.putValue(Action.NAME, mainWindowComponent.getTitle());
        action.putValue(Action.SMALL_ICON, mainWindowComponent.getSmallIcon());
        KeyStroke keyStroke = mainWindowComponent.getOptionalSelectionAccelaratorKey();
        if (keyStroke != null) {
            action.putValue(Action.ACCELERATOR_KEY, keyStroke);
        }
        return action;
    }

    private static class SelectTabAction extends AbstractAction {
        private static final long serialVersionUID = 1L;
        private final JTabbedPane tabbedPane;
        private final Component component;

        private SelectTabAction(JTabbedPane tabbedPane, Component component) {
            this.tabbedPane = tabbedPane;
            this.component = component;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Component selectedComponent = tabbedPane.getSelectedComponent();
            if (component != selectedComponent) {
                tabbedPane.setSelectedComponent(component);
            } else {
                EventBus.publish(new TabInEditWindowDisplayedEvent(this, component));
            }
        }
    }

    private void dockIntoTabbedPane(final MainWindowComponent appWindow, final JTabbedPane tabbedPane) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                int tabCount = tabbedPane.getTabCount();
                int appWindowPosition = appWindow.getPosition();
                int tabIndex = appWindowPosition < 0 || appWindowPosition > tabCount ? tabCount : appWindowPosition;
                Component component = appWindow.getComponent();
                Icon icon = appWindow.getSmallIcon();
                String tip = appWindow.getTooltipText();
                String title = appWindow.getTitle();

                tabbedPane.insertTab(title, icon, component, tip, tabIndex);
            }
        });
    }

    private void lookupStatusLineElements() {
        List<StatusLineElementProvider> providers = new ArrayList<StatusLineElementProvider>(
                Lookup.getDefault().lookupAll(StatusLineElementProvider.class));
        Collections.sort(providers, PositionProviderAscendingComparator.INSTANCE);
        boolean isFirst = true;
        for (StatusLineElementProvider provider : providers) {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = UiFactory.insets(0, isFirst ? 0 : 10, 0, 0);
            isFirst = false;
            statusLineElementsPanel.add(provider.getStatusLineElement(), gbc);
        }
    }

    private final ChangeListener tabbedPaneEditChangeListener = new ChangeListener() {

        @Override
        public void stateChanged(ChangeEvent e) {
            Component selectedComponent = tabbedPaneMetadata.getSelectedComponent();
            EventBus.publish(new TabInEditWindowDisplayedEvent(tabbedPaneMetadata, selectedComponent));
        }
    };

    private final ChangeListener tabbedPaneSelectionChangeListener = new ChangeListener() {

        @Override
        public void stateChanged(ChangeEvent e) {
            Component selectedComponent = tabbedPaneSelection.getSelectedComponent();
            EventBus.publish(new TabInSelectionWindowDisplayedEvent(tabbedPaneSelection, selectedComponent));
        }
    };

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroupKeywordsMultipleSel = new javax.swing.ButtonGroup();
        splitPaneMain = UiFactory.splitPane();
        splitPaneMain.setDividerLocation(getDividerLocationMain());
        panelSelection = UiFactory.panel();
        panelSearch = UiFactory.panel();
        tabbedPaneSelection = UiFactory.tabbedPane();
        panelDirectories = UiFactory.panel();
        scrollPaneDirectories = UiFactory.scrollPane();
        treeDirectories = UiFactory.jxTree();
        treeDirectories.setShowsRootHandles(true);
        buttonSearchInDirectories = UiFactory.button();
        checkBoxDirectoriesRecursive = UiFactory.checkBox();
        panelSavedSearches = UiFactory.panel();
        panelListSavedSearchesFilter = UiFactory.panel();
        labelListSavedSearchesFilter = UiFactory.label();
        textFieldListSavedSearchesFilter = UiFactory.textField();
        scrollPaneSavedSearches = UiFactory.scrollPane();
        listSavedSearches = UiFactory.jxList();
        buttonSearchInSavedSearches = UiFactory.button();
        panelImageCollections = UiFactory.panel();
        panelListImageCollectionsFilter = UiFactory.panel();
        labelListImageCollectionsFilter = UiFactory.label();
        textFieldListImageCollectionsFilter = UiFactory.textField();
        scrollPaneImageCollections = UiFactory.scrollPane();
        listImageCollections = UiFactory.jxList();
        buttonSearchInImageCollections = UiFactory.button();
        panelFavorites = UiFactory.panel();
        scrollPaneFavorites = UiFactory.scrollPane();
        treeFavorites = UiFactory.jxTree();
        treeFavorites.setTransferHandler(new org.jphototagger.program.module.directories.DirectoryTreeTransferHandler());
        treeFavorites.setShowsRootHandles(true);
        buttonSearchInTreeFavorites = UiFactory.button();
        checkBoxFavoritesRecursive = UiFactory.checkBox();
        panelSelKeywords = UiFactory.panel();
        panelSelKeywordsTree = UiFactory.panel();
        scrollPaneSelKeywordsTree = UiFactory.scrollPane();
        treeSelKeywords = UiFactory.jxTree();
        treeSelKeywords.setTransferHandler(new org.jphototagger.program.module.keywords.tree.KeywordsTreeTransferHandler());
        treeSelKeywords.setShowsRootHandles(true);
        buttonDisplaySelKeywordsList = UiFactory.button();
        toggleButtonExpandAllNodesSelKeywords = UiFactory.toggleButton();
        buttonSearchInTreeSelKeywords = UiFactory.button();
        panelSelKeywordsList = UiFactory.panel();
        panelListSelKeywordsFilter = UiFactory.panel();
        labelListSelKeywordsFilter = UiFactory.label();
        textFieldListSelKeywordsFilter = UiFactory.textField();
        scrollPaneSelKeywordsList = UiFactory.scrollPane();
        listSelKeywords = UiFactory.jxList();
        listSelKeywords.setTransferHandler(new org.jphototagger.program.module.keywords.list.KeywordsListTransferHandler());
        panelSelKeywordsListMultipleSelection = UiFactory.panel();
        radioButtonSelKeywordsMultipleSelAll = UiFactory.radioButton();
        radioButtonSelKeywordsMultipleSelOne = UiFactory.radioButton();
        buttonDisplaySelKeywordsTree = UiFactory.button();
        buttonSearchInListSelKeywords = UiFactory.button();
        panelTimeline = UiFactory.panel();
        scrollPaneTimeline = UiFactory.scrollPane();
        treeTimeline = UiFactory.jxTree();
        treeTimeline.setShowsRootHandles(true);
        toggleButtonExpandCollapseTreeTimeline = UiFactory.toggleButton();
        buttonSearchInTreeTimeline = UiFactory.button();
        panelMiscMetadata = UiFactory.panel();
        scrollPaneMiscMetadata = UiFactory.scrollPane();
        treeMiscMetadata = UiFactory.jxTree();
        treeMiscMetadata.setTransferHandler(new org.jphototagger.program.module.miscmetadata.MiscMetadataTreeTransferHandler());
        treeMiscMetadata.setShowsRootHandles(true);
        toggleButtonExpandCollapseTreeMiscMetadata = UiFactory.toggleButton();
        buttonSearchInTreeMiscMetadata = UiFactory.button();
        panelThumbnailsMetadata = UiFactory.panel();
        splitPaneThumbnailsMetadata = UiFactory.splitPane();
        splitPaneThumbnailsMetadata.setDividerLocation(getDividerLocationThumbnails());
        thumbnailPanelComponent = UiFactory.panel();
        panelMetadata = UiFactory.panel();
        tabbedPaneMetadata = UiFactory.tabbedPane();
        panelEditKeywords = new org.jphototagger.program.module.keywords.KeywordsPanel();
        panelStatusbar = UiFactory.panel();
        labelStatusbarText = UiFactory.label();
        statusLineElementsPanel = UiFactory.panel();

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        splitPaneMain.setDividerSize(UiFactory.scale(6));
        splitPaneMain.setName("splitPaneMain"); // NOI18N
        splitPaneMain.setOneTouchExpandable(true);

        panelSelection.setMinimumSize(UiFactory.dimension(100, 200));
        panelSelection.setName("panelSelection"); // NOI18N
        panelSelection.setLayout(new java.awt.GridBagLayout());

        panelSearch.setName("panelSearch"); // NOI18N
        panelSearch.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 5);
        panelSelection.add(panelSearch, gridBagConstraints);

        tabbedPaneSelection.setName("tabbedPaneSelection"); // NOI18N

        panelDirectories.setName("panelDirectories"); // NOI18N
        panelDirectories.setLayout(new java.awt.GridBagLayout());

        scrollPaneDirectories.setName("scrollPaneDirectories"); // NOI18N

        treeDirectories.setModel(org.jphototagger.lib.swing.WaitTreeModel.INSTANCE);
        treeDirectories.setCellRenderer(new org.jphototagger.lib.swing.AllSystemDirectoriesTreeCellRenderer());
        treeDirectories.setDragEnabled(true);
        treeDirectories.setDropMode(javax.swing.DropMode.ON);
        treeDirectories.setName("treeDirectories"); // NOI18N
        treeDirectories.setRootVisible(false);
        scrollPaneDirectories.setViewportView(treeDirectories);
        treeDirectories.setTransferHandler(new org.jphototagger.program.module.directories.DirectoryTreeTransferHandler());
        treeDirectories.setShowsRootHandles(true);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelDirectories.add(scrollPaneDirectories, gridBagConstraints);

        buttonSearchInDirectories.setAction(new SearchInJxTreeAction((JXTree)treeDirectories));
        buttonSearchInDirectories.setText(Bundle.getString(getClass(), "AppPanel.buttonSearchInDirectories.text")); // NOI18N
        buttonSearchInDirectories.setMargin(UiFactory.insets(1, 1, 1, 1));
        buttonSearchInDirectories.setName("buttonSearchInDirectories"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(2, 2, 2, 0);
        panelDirectories.add(buttonSearchInDirectories, gridBagConstraints);

        checkBoxDirectoriesRecursive.setText(Bundle.getString(getClass(), "AppPanel.checkBoxDirectoriesRecursive.text")); // NOI18N
        checkBoxDirectoriesRecursive.setName("checkBoxDirectoriesRecursive"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(2, 2, 2, 0);
        panelDirectories.add(checkBoxDirectoriesRecursive, gridBagConstraints);

        tabbedPaneSelection.addTab(Bundle.getString(getClass(), "AppPanel.panelDirectories.TabConstraints.tabTitle"), panelDirectories); // NOI18N

        panelSavedSearches.setName("panelSavedSearches"); // NOI18N
        panelSavedSearches.setLayout(new java.awt.GridBagLayout());

        panelListSavedSearchesFilter.setName("panelListSavedSearchesFilter"); // NOI18N
        panelListSavedSearchesFilter.setLayout(new java.awt.GridBagLayout());

        labelListSavedSearchesFilter.setLabelFor(textFieldListSavedSearchesFilter);
        labelListSavedSearchesFilter.setText(Bundle.getString(getClass(), "AppPanel.labelListSavedSearchesFilter.text")); // NOI18N
        labelListSavedSearchesFilter.setName("labelListSavedSearchesFilter"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelListSavedSearchesFilter.add(labelListSavedSearchesFilter, gridBagConstraints);

        textFieldListSavedSearchesFilter.setName("textFieldListSavedSearchesFilter"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelListSavedSearchesFilter.add(textFieldListSavedSearchesFilter, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(3, 3, 3, 3);
        panelSavedSearches.add(panelListSavedSearchesFilter, gridBagConstraints);

        scrollPaneSavedSearches.setName("scrollPaneSavedSearches"); // NOI18N

        listSavedSearches.setModel(org.jphototagger.lib.swing.WaitListModel.INSTANCE);
        listSavedSearches.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listSavedSearches.setCellRenderer(new org.jphototagger.program.module.search.SavedSearchesListCellRenderer());
        listSavedSearches.setName("listSavedSearches"); // NOI18N
        scrollPaneSavedSearches.setViewportView(listSavedSearches);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelSavedSearches.add(scrollPaneSavedSearches, gridBagConstraints);

        buttonSearchInSavedSearches.setAction(new SearchInJxListAction(listSavedSearches));
        buttonSearchInSavedSearches.setText(Bundle.getString(getClass(), "AppPanel.buttonSearchInSavedSearches.text")); // NOI18N
        buttonSearchInSavedSearches.setMargin(UiFactory.insets(1, 1, 1, 1));
        buttonSearchInSavedSearches.setName("buttonSearchInSavedSearches"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = UiFactory.insets(2, 2, 2, 0);
        panelSavedSearches.add(buttonSearchInSavedSearches, gridBagConstraints);

        tabbedPaneSelection.addTab(Bundle.getString(getClass(), "AppPanel.panelSavedSearches.TabConstraints.tabTitle"), panelSavedSearches); // NOI18N

        panelImageCollections.setName("panelImageCollections"); // NOI18N
        panelImageCollections.setLayout(new java.awt.GridBagLayout());

        panelListImageCollectionsFilter.setName("panelListImageCollectionsFilter"); // NOI18N
        panelListImageCollectionsFilter.setLayout(new java.awt.GridBagLayout());

        labelListImageCollectionsFilter.setLabelFor(textFieldListImageCollectionsFilter);
        labelListImageCollectionsFilter.setText(Bundle.getString(getClass(), "AppPanel.labelListImageCollectionsFilter.text")); // NOI18N
        labelListImageCollectionsFilter.setName("labelListImageCollectionsFilter"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelListImageCollectionsFilter.add(labelListImageCollectionsFilter, gridBagConstraints);

        textFieldListImageCollectionsFilter.setName("textFieldListImageCollectionsFilter"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelListImageCollectionsFilter.add(textFieldListImageCollectionsFilter, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(3, 3, 3, 3);
        panelImageCollections.add(panelListImageCollectionsFilter, gridBagConstraints);

        scrollPaneImageCollections.setName("scrollPaneImageCollections"); // NOI18N

        listImageCollections.setModel(org.jphototagger.lib.swing.WaitListModel.INSTANCE);
        listImageCollections.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listImageCollections.setCellRenderer(new org.jphototagger.program.module.imagecollections.ImageCollectionsListCellRenderer());
        listImageCollections.setDragEnabled(true);
        listImageCollections.setName("listImageCollections"); // NOI18N
        scrollPaneImageCollections.setViewportView(listImageCollections);
        listImageCollections.setTransferHandler(new org.jphototagger.program.module.imagecollections.ImageCollectionsListTransferHandler());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelImageCollections.add(scrollPaneImageCollections, gridBagConstraints);

        buttonSearchInImageCollections.setAction(new SearchInJxListAction(listImageCollections));
        buttonSearchInImageCollections.setText(Bundle.getString(getClass(), "AppPanel.buttonSearchInImageCollections.text")); // NOI18N
        buttonSearchInImageCollections.setMargin(UiFactory.insets(1, 1, 1, 1));
        buttonSearchInImageCollections.setName("buttonSearchInImageCollections"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = UiFactory.insets(2, 2, 2, 0);
        panelImageCollections.add(buttonSearchInImageCollections, gridBagConstraints);

        tabbedPaneSelection.addTab(Bundle.getString(getClass(), "AppPanel.panelImageCollections.TabConstraints.tabTitle"), panelImageCollections); // NOI18N

        panelFavorites.setName("panelFavorites"); // NOI18N
        panelFavorites.setLayout(new java.awt.GridBagLayout());

        scrollPaneFavorites.setName("scrollPaneFavorites"); // NOI18N

        treeFavorites.setModel(org.jphototagger.lib.swing.WaitTreeModel.INSTANCE);
        treeFavorites.setCellRenderer(new org.jphototagger.program.module.favorites.FavoritesTreeCellRenderer());
        treeFavorites.setDragEnabled(true);
        treeFavorites.setDropMode(javax.swing.DropMode.ON);
        treeFavorites.setName("treeFavorites"); // NOI18N
        treeFavorites.setRootVisible(false);
        scrollPaneFavorites.setViewportView(treeFavorites);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelFavorites.add(scrollPaneFavorites, gridBagConstraints);

        buttonSearchInTreeFavorites.setAction(new SearchInJxTreeAction((JXTree)treeFavorites));
        buttonSearchInTreeFavorites.setText(Bundle.getString(getClass(), "AppPanel.buttonSearchInTreeFavorites.text")); // NOI18N
        buttonSearchInTreeFavorites.setMargin(UiFactory.insets(1, 1, 1, 1));
        buttonSearchInTreeFavorites.setName("buttonSearchInTreeFavorites"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(2, 2, 2, 0);
        panelFavorites.add(buttonSearchInTreeFavorites, gridBagConstraints);

        checkBoxFavoritesRecursive.setText(Bundle.getString(getClass(), "AppPanel.checkBoxFavoritesRecursive.text")); // NOI18N
        checkBoxFavoritesRecursive.setName("checkBoxFavoritesRecursive"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(2, 2, 2, 0);
        panelFavorites.add(checkBoxFavoritesRecursive, gridBagConstraints);

        tabbedPaneSelection.addTab(Bundle.getString(getClass(), "AppPanel.panelFavorites.TabConstraints.tabTitle"), panelFavorites); // NOI18N

        panelSelKeywords.setName("panelSelKeywords"); // NOI18N
        panelSelKeywords.setLayout(new java.awt.CardLayout());

        panelSelKeywordsTree.setName("panelSelKeywordsTree"); // NOI18N
        panelSelKeywordsTree.setLayout(new java.awt.GridBagLayout());

        scrollPaneSelKeywordsTree.setName("scrollPaneSelKeywordsTree"); // NOI18N

        treeSelKeywords.setModel(org.jphototagger.lib.swing.WaitTreeModel.INSTANCE);
        treeSelKeywords.setCellRenderer(new org.jphototagger.program.module.keywords.tree.KeywordsTreeCellRenderer());
        treeSelKeywords.setName("treeSelKeywords"); // NOI18N
        treeSelKeywords.setRootVisible(false);
        scrollPaneSelKeywordsTree.setViewportView(treeSelKeywords);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelSelKeywordsTree.add(scrollPaneSelKeywordsTree, gridBagConstraints);

        buttonDisplaySelKeywordsList.setText(Bundle.getString(getClass(), "AppPanel.buttonDisplaySelKeywordsList.text")); // NOI18N
        buttonDisplaySelKeywordsList.setMargin(UiFactory.insets(1, 1, 1, 1));
        buttonDisplaySelKeywordsList.setName("buttonDisplaySelKeywordsList"); // NOI18N
        buttonDisplaySelKeywordsList.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDisplaySelKeywordsListActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(2, 2, 1, 0);
        panelSelKeywordsTree.add(buttonDisplaySelKeywordsList, gridBagConstraints);

        toggleButtonExpandAllNodesSelKeywords.setText(Bundle.getString(getClass(), "AppPanel.toggleButtonExpandAllNodesSelKeywords.text")); // NOI18N
        toggleButtonExpandAllNodesSelKeywords.setMargin(UiFactory.insets(1, 1, 1, 1));
        toggleButtonExpandAllNodesSelKeywords.setName("toggleButtonExpandAllNodesSelKeywords"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(2, 2, 1, 0);
        panelSelKeywordsTree.add(toggleButtonExpandAllNodesSelKeywords, gridBagConstraints);

        buttonSearchInTreeSelKeywords.setAction(new SearchInJxTreeAction((JXTree)treeSelKeywords));
        buttonSearchInTreeSelKeywords.setText(Bundle.getString(getClass(), "AppPanel.buttonSearchInTreeSelKeywords.text")); // NOI18N
        buttonSearchInTreeSelKeywords.setMargin(UiFactory.insets(1, 1, 1, 1));
        buttonSearchInTreeSelKeywords.setName("buttonSearchInTreeSelKeywords"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(2, 2, 1, 0);
        panelSelKeywordsTree.add(buttonSearchInTreeSelKeywords, gridBagConstraints);

        panelSelKeywords.add(panelSelKeywordsTree, "keywordsTree");

        panelSelKeywordsList.setName("panelSelKeywordsList"); // NOI18N
        panelSelKeywordsList.setLayout(new java.awt.GridBagLayout());

        panelListSelKeywordsFilter.setName("panelListSelKeywordsFilter"); // NOI18N
        panelListSelKeywordsFilter.setLayout(new java.awt.GridBagLayout());

        labelListSelKeywordsFilter.setLabelFor(textFieldListSelKeywordsFilter);
        labelListSelKeywordsFilter.setText(Bundle.getString(getClass(), "AppPanel.labelListSelKeywordsFilter.text")); // NOI18N
        labelListSelKeywordsFilter.setName("labelListSelKeywordsFilter"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelListSelKeywordsFilter.add(labelListSelKeywordsFilter, gridBagConstraints);

        textFieldListSelKeywordsFilter.setName("textFieldListSelKeywordsFilter"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelListSelKeywordsFilter.add(textFieldListSelKeywordsFilter, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(3, 3, 3, 3);
        panelSelKeywordsList.add(panelListSelKeywordsFilter, gridBagConstraints);

        scrollPaneSelKeywordsList.setName("scrollPaneSelKeywordsList"); // NOI18N

        listSelKeywords.setModel(org.jphototagger.lib.swing.WaitListModel.INSTANCE);
        listSelKeywords.setCellRenderer(new org.jphototagger.program.module.keywords.list.KeywordsListCellRenderer());
        listSelKeywords.setDragEnabled(true);
        listSelKeywords.setName("listSelKeywords"); // NOI18N
        scrollPaneSelKeywordsList.setViewportView(listSelKeywords);
        listSelKeywords.setTransferHandler(new org.jphototagger.program.module.keywords.list.KeywordsListTransferHandler());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelSelKeywordsList.add(scrollPaneSelKeywordsList, gridBagConstraints);

        panelSelKeywordsListMultipleSelection.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString(getClass(), "AppPanel.panelSelKeywordsListMultipleSelection.border.title"))); // NOI18N
        panelSelKeywordsListMultipleSelection.setName("panelSelKeywordsListMultipleSelection"); // NOI18N
        panelSelKeywordsListMultipleSelection.setLayout(new java.awt.GridBagLayout());

        buttonGroupKeywordsMultipleSel.add(radioButtonSelKeywordsMultipleSelAll);
        radioButtonSelKeywordsMultipleSelAll.setText(Bundle.getString(getClass(), "AppPanel.radioButtonSelKeywordsMultipleSelAll.text")); // NOI18N
        radioButtonSelKeywordsMultipleSelAll.setName("radioButtonSelKeywordsMultipleSelAll"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 2, 0, 0);
        panelSelKeywordsListMultipleSelection.add(radioButtonSelKeywordsMultipleSelAll, gridBagConstraints);

        buttonGroupKeywordsMultipleSel.add(radioButtonSelKeywordsMultipleSelOne);
        radioButtonSelKeywordsMultipleSelOne.setText(Bundle.getString(getClass(), "AppPanel.radioButtonSelKeywordsMultipleSelOne.text")); // NOI18N
        radioButtonSelKeywordsMultipleSelOne.setName("radioButtonSelKeywordsMultipleSelOne"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 2, 2, 0);
        panelSelKeywordsListMultipleSelection.add(radioButtonSelKeywordsMultipleSelOne, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(2, 2, 0, 0);
        panelSelKeywordsList.add(panelSelKeywordsListMultipleSelection, gridBagConstraints);

        buttonDisplaySelKeywordsTree.setText(Bundle.getString(getClass(), "AppPanel.buttonDisplaySelKeywordsTree.text")); // NOI18N
        buttonDisplaySelKeywordsTree.setMargin(UiFactory.insets(1, 1, 1, 1));
        buttonDisplaySelKeywordsTree.setName("buttonDisplaySelKeywordsTree"); // NOI18N
        buttonDisplaySelKeywordsTree.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDisplaySelKeywordsTreeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(2, 4, 2, 2);
        panelSelKeywordsList.add(buttonDisplaySelKeywordsTree, gridBagConstraints);

        buttonSearchInListSelKeywords.setAction(new SearchInJxListAction(listSelKeywords));
        buttonSearchInListSelKeywords.setText(Bundle.getString(getClass(), "AppPanel.buttonSearchInListSelKeywords.text")); // NOI18N
        buttonSearchInListSelKeywords.setMargin(UiFactory.insets(1, 1, 1, 1));
        buttonSearchInListSelKeywords.setName("buttonSearchInListSelKeywords"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(0, 2, 0, 0);
        panelSelKeywordsList.add(buttonSearchInListSelKeywords, gridBagConstraints);

        panelSelKeywords.add(panelSelKeywordsList, "flatKeywords");

        tabbedPaneSelection.addTab(Bundle.getString(getClass(), "AppPanel.panelSelKeywords.TabConstraints.tabTitle"), panelSelKeywords); // NOI18N

        panelTimeline.setName("panelTimeline"); // NOI18N
        panelTimeline.setLayout(new java.awt.GridBagLayout());

        scrollPaneTimeline.setName("scrollPaneTimeline"); // NOI18N

        treeTimeline.setModel(org.jphototagger.lib.swing.WaitTreeModel.INSTANCE);
        treeTimeline.setCellRenderer(new org.jphototagger.program.module.timeline.TimelineTreeCellRenderer());
        treeTimeline.setName("treeTimeline"); // NOI18N
        treeTimeline.setRootVisible(false);
        scrollPaneTimeline.setViewportView(treeTimeline);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelTimeline.add(scrollPaneTimeline, gridBagConstraints);

        toggleButtonExpandCollapseTreeTimeline.setAction(new TreeExpandCollapseAllAction(toggleButtonExpandCollapseTreeTimeline, treeTimeline));
        toggleButtonExpandCollapseTreeTimeline.setText(TreeExpandCollapseAllAction.NOT_SELECTED_TEXT);
        toggleButtonExpandCollapseTreeTimeline.setMargin(UiFactory.insets(1, 1, 1, 1));
        toggleButtonExpandCollapseTreeTimeline.setName("toggleButtonExpandCollapseTreeTimeline"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(1, 1, 1, 0);
        panelTimeline.add(toggleButtonExpandCollapseTreeTimeline, gridBagConstraints);

        buttonSearchInTreeTimeline.setAction(new SearchInJxTreeAction((JXTree)treeTimeline));
        buttonSearchInTreeTimeline.setText(Bundle.getString(getClass(), "AppPanel.buttonSearchInTreeTimeline.text")); // NOI18N
        buttonSearchInTreeTimeline.setMargin(UiFactory.insets(1, 1, 1, 1));
        buttonSearchInTreeTimeline.setName("buttonSearchInTreeTimeline"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(1, 2, 1, 0);
        panelTimeline.add(buttonSearchInTreeTimeline, gridBagConstraints);

        tabbedPaneSelection.addTab(Bundle.getString(getClass(), "AppPanel.panelTimeline.TabConstraints.tabTitle"), panelTimeline); // NOI18N

        panelMiscMetadata.setName("panelMiscMetadata"); // NOI18N
        panelMiscMetadata.setLayout(new java.awt.GridBagLayout());

        scrollPaneMiscMetadata.setName("scrollPaneMiscMetadata"); // NOI18N

        treeMiscMetadata.setModel(org.jphototagger.lib.swing.WaitTreeModel.INSTANCE);
        treeMiscMetadata.setCellRenderer(new org.jphototagger.program.module.miscmetadata.MiscMetadataTreeCellRenderer());
        treeMiscMetadata.setDragEnabled(true);
        treeMiscMetadata.setName("treeMiscMetadata"); // NOI18N
        treeMiscMetadata.setRootVisible(false);
        scrollPaneMiscMetadata.setViewportView(treeMiscMetadata);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelMiscMetadata.add(scrollPaneMiscMetadata, gridBagConstraints);

        toggleButtonExpandCollapseTreeMiscMetadata.setAction(new TreeExpandCollapseAllAction(toggleButtonExpandCollapseTreeMiscMetadata, treeMiscMetadata));
        toggleButtonExpandCollapseTreeMiscMetadata.setText(TreeExpandCollapseAllAction.NOT_SELECTED_TEXT);
        toggleButtonExpandCollapseTreeMiscMetadata.setMargin(UiFactory.insets(1, 1, 1, 1));
        toggleButtonExpandCollapseTreeMiscMetadata.setName("toggleButtonExpandCollapseTreeMiscMetadata"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(1, 1, 1, 0);
        panelMiscMetadata.add(toggleButtonExpandCollapseTreeMiscMetadata, gridBagConstraints);

        buttonSearchInTreeMiscMetadata.setAction(new SearchInJxTreeAction((JXTree)treeMiscMetadata));
        buttonSearchInTreeMiscMetadata.setText(Bundle.getString(getClass(), "AppPanel.buttonSearchInTreeMiscMetadata.text")); // NOI18N
        buttonSearchInTreeMiscMetadata.setMargin(UiFactory.insets(1, 1, 1, 1));
        buttonSearchInTreeMiscMetadata.setName("buttonSearchInTreeMiscMetadata"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(1, 2, 1, 0);
        panelMiscMetadata.add(buttonSearchInTreeMiscMetadata, gridBagConstraints);

        tabbedPaneSelection.addTab(Bundle.getString(getClass(), "AppPanel.panelMiscMetadata.TabConstraints.tabTitle"), panelMiscMetadata); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.9;
        gridBagConstraints.insets = UiFactory.insets(7, 0, 0, 0);
        panelSelection.add(tabbedPaneSelection, gridBagConstraints);

        splitPaneMain.setLeftComponent(panelSelection);

        panelThumbnailsMetadata.setMinimumSize(UiFactory.dimension(100, 200));
        panelThumbnailsMetadata.setName("panelThumbnailsMetadata"); // NOI18N
        panelThumbnailsMetadata.setLayout(new java.awt.GridBagLayout());

        splitPaneThumbnailsMetadata.setDividerSize(UiFactory.scale(6));
        splitPaneThumbnailsMetadata.setName("splitPaneThumbnailsMetadata"); // NOI18N
        splitPaneThumbnailsMetadata.setOneTouchExpandable(true);

        thumbnailPanelComponent.setMinimumSize(UiFactory.dimension(150, 200));
        thumbnailPanelComponent.setName("thumbnailPanelComponent"); // NOI18N
        thumbnailPanelComponent.setPreferredSize(UiFactory.dimension(300, 200));
        thumbnailPanelComponent.setLayout(new java.awt.GridBagLayout());
        splitPaneThumbnailsMetadata.setLeftComponent(thumbnailPanelComponent);

        panelMetadata.setMinimumSize(UiFactory.dimension(100, 200));
        panelMetadata.setName("panelMetadata"); // NOI18N
        panelMetadata.setLayout(new java.awt.GridBagLayout());

        tabbedPaneMetadata.setName("tabbedPaneMetadata"); // NOI18N
        tabbedPaneMetadata.setOpaque(true);

        panelEditKeywords.setName("panelEditKeywords"); // NOI18N
        tabbedPaneMetadata.addTab(Bundle.getString(getClass(), "AppPanel.panelEditKeywords.TabConstraints.tabTitle"), panelEditKeywords); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelMetadata.add(tabbedPaneMetadata, gridBagConstraints);

        splitPaneThumbnailsMetadata.setRightComponent(panelMetadata);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelThumbnailsMetadata.add(splitPaneThumbnailsMetadata, gridBagConstraints);

        splitPaneMain.setRightComponent(panelThumbnailsMetadata);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 5);
        add(splitPaneMain, gridBagConstraints);

        panelStatusbar.setName("panelStatusbar"); // NOI18N
        panelStatusbar.setLayout(new java.awt.GridBagLayout());

        labelStatusbarText.setName("labelStatusbarText"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(0, 10, 0, 0);
        panelStatusbar.add(labelStatusbarText, gridBagConstraints);

        statusLineElementsPanel.setName("statusLineElementsPanel"); // NOI18N
        statusLineElementsPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 10, 0, 0);
        panelStatusbar.add(statusLineElementsPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = UiFactory.insets(2, 5, 3, 5);
        add(panelStatusbar, gridBagConstraints);
    }

    private void buttonDisplaySelKeywordsListActionPerformed(java.awt.event.ActionEvent evt) {
        displaySelKeywordsCard("flatKeywords");
    }

    private void buttonDisplaySelKeywordsTreeActionPerformed(java.awt.event.ActionEvent evt) {
        displaySelKeywordsCard("keywordsTree");
    }

    private javax.swing.JButton buttonDisplaySelKeywordsList;
    private javax.swing.JButton buttonDisplaySelKeywordsTree;
    private javax.swing.ButtonGroup buttonGroupKeywordsMultipleSel;
    private javax.swing.JButton buttonSearchInDirectories;
    private javax.swing.JButton buttonSearchInImageCollections;
    private javax.swing.JButton buttonSearchInListSelKeywords;
    private javax.swing.JButton buttonSearchInSavedSearches;
    private javax.swing.JButton buttonSearchInTreeFavorites;
    private javax.swing.JButton buttonSearchInTreeMiscMetadata;
    private javax.swing.JButton buttonSearchInTreeSelKeywords;
    private javax.swing.JButton buttonSearchInTreeTimeline;
    private javax.swing.JCheckBox checkBoxDirectoriesRecursive;
    private javax.swing.JCheckBox checkBoxFavoritesRecursive;
    private javax.swing.JLabel labelListImageCollectionsFilter;
    private javax.swing.JLabel labelListSavedSearchesFilter;
    private javax.swing.JLabel labelListSelKeywordsFilter;
    private javax.swing.JLabel labelStatusbarText;
    private org.jdesktop.swingx.JXList listImageCollections;
    private org.jdesktop.swingx.JXList listSavedSearches;
    private org.jdesktop.swingx.JXList listSelKeywords;
    private javax.swing.JPanel panelDirectories;
    private org.jphototagger.program.module.keywords.KeywordsPanel panelEditKeywords;
    private javax.swing.JPanel panelFavorites;
    private javax.swing.JPanel panelImageCollections;
    private javax.swing.JPanel panelListImageCollectionsFilter;
    private javax.swing.JPanel panelListSavedSearchesFilter;
    private javax.swing.JPanel panelListSelKeywordsFilter;
    private javax.swing.JPanel panelMetadata;
    private javax.swing.JPanel panelMiscMetadata;
    private javax.swing.JPanel panelSavedSearches;
    private javax.swing.JPanel panelSearch;
    private javax.swing.JPanel panelSelKeywords;
    private javax.swing.JPanel panelSelKeywordsList;
    private javax.swing.JPanel panelSelKeywordsListMultipleSelection;
    private javax.swing.JPanel panelSelKeywordsTree;
    private javax.swing.JPanel panelSelection;
    private javax.swing.JPanel panelStatusbar;
    private javax.swing.JPanel panelThumbnailsMetadata;
    private javax.swing.JPanel panelTimeline;
    private javax.swing.JRadioButton radioButtonSelKeywordsMultipleSelAll;
    private javax.swing.JRadioButton radioButtonSelKeywordsMultipleSelOne;
    private javax.swing.JScrollPane scrollPaneDirectories;
    private javax.swing.JScrollPane scrollPaneFavorites;
    private javax.swing.JScrollPane scrollPaneImageCollections;
    private javax.swing.JScrollPane scrollPaneMiscMetadata;
    private javax.swing.JScrollPane scrollPaneSavedSearches;
    private javax.swing.JScrollPane scrollPaneSelKeywordsList;
    private javax.swing.JScrollPane scrollPaneSelKeywordsTree;
    private javax.swing.JScrollPane scrollPaneTimeline;
    private javax.swing.JSplitPane splitPaneMain;
    private javax.swing.JSplitPane splitPaneThumbnailsMetadata;
    private javax.swing.JPanel statusLineElementsPanel;
    private javax.swing.JTabbedPane tabbedPaneMetadata;
    private javax.swing.JTabbedPane tabbedPaneSelection;
    private javax.swing.JTextField textFieldListImageCollectionsFilter;
    private javax.swing.JTextField textFieldListSavedSearchesFilter;
    private javax.swing.JTextField textFieldListSelKeywordsFilter;
    private javax.swing.JPanel thumbnailPanelComponent;
    private javax.swing.JToggleButton toggleButtonExpandAllNodesSelKeywords;
    private javax.swing.JToggleButton toggleButtonExpandCollapseTreeMiscMetadata;
    private javax.swing.JToggleButton toggleButtonExpandCollapseTreeTimeline;
    private javax.swing.JTree treeDirectories;
    private javax.swing.JTree treeFavorites;
    private javax.swing.JTree treeMiscMetadata;
    private javax.swing.JTree treeSelKeywords;
    private javax.swing.JTree treeTimeline;
}
