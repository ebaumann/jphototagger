package org.jphototagger.lib.help;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.openide.util.Lookup;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.swing.Dialog;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.swingx.SearchInComponentAction;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;

/**
 * Browser for HTML help files.
 *
 * @author Elmar Baumann
 */
public final class HelpBrowser extends Dialog implements HyperlinkListener, TreeSelectionListener {

    private static final long serialVersionUID = 1L;
    private static final String KEY_DIVIDER_LOCATION = "HelpBrowser.DividerLocation";
    private static final String KEY_TEXT_FONT_SIZE = "HelpBrowser.TextFontSize";
    private final LinkedList<URL> urlHistory = new LinkedList<URL>();
    private int currentHistoryIndex = -1;
    private final Set<HelpBrowserListener> listeners = new CopyOnWriteArraySet<HelpBrowserListener>();
    private final GoToNextUrlAction goToNextUrlAction = new GoToNextUrlAction();
    private final GoToPreviousUrlAction goToPreviousUrlAction = new GoToPreviousUrlAction();
    private String displayUrl;
    private boolean settingPath;
    private final HelpNode rootNode;
    private final HelpSearch helpSearch;
    private String titlePostfix = Bundle.getString(HelpBrowser.class, "HelpBrowser.TitlePostfix");
    private HelpPage selectedFoundPage;
    private final Map<JMenuItem, Integer> textFontSizeOfMenuItem = new HashMap<JMenuItem, Integer>();
    private final ObservableList<HelpPage> foundPages = ObservableCollections.observableList(new ArrayList<HelpPage>());

    public HelpBrowser(HelpNode rootNode) {
        super(ComponentUtil.findFrameWithIcon());
        if (rootNode == null) {
            throw new NullPointerException("rootNode == null");
        }
        this.rootNode = rootNode;
        this.helpSearch = new HelpSearch(rootNode);
        initComponents();
        postInitComponents();
        tree.setModel(new HelpContentsTreeModel(rootNode));
    }

    private void postInitComponents() {
        editorPanePage.addHyperlinkListener(this);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(this);
        setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
        MnemonicUtil.setMnemonics(this);
        MnemonicUtil.setMnemonics(popupMenuEditorPane);
        addSearchAction();
        initPreviousNextShortcuts();
        initTextFontSizeOfMenuItem();
        new HeplpSearchInit().start();
    }

    private void initTextFontSizeOfMenuItem() {
        textFontSizeOfMenuItem.put(menuItemTextFontSizeSmall, 10);
        textFontSizeOfMenuItem.put(menuItemTextFontSizeNormal, 14);
        textFontSizeOfMenuItem.put(menuItemTextFontSizeLarge, 18);
        textFontSizeOfMenuItem.put(menuItemTextFontSizeHuge, 28);
    }

    private void initPreviousNextShortcuts() {
        ActionMap actionMap = getRootPane().getActionMap();
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.ALT_DOWN_MASK), "nexturl");
        actionMap.put("nexturl", goToNextUrlAction);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.ALT_DOWN_MASK), "prevurl");
        actionMap.put("prevurl", goToPreviousUrlAction);
    }

    public void addHelpBrowserListener(HelpBrowserListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        listeners.add(listener);
    }

    public void removeHelpBrowserListener(HelpBrowserListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        listeners.remove(listener);
    }

    private void notifyUrlChanged(URL url) {
        HelpBrowserEvent action = new HelpBrowserEvent(this, HelpBrowserEvent.Type.URL_CHANGED, url);

        for (HelpBrowserListener listener : listeners) {
            listener.actionPerformed(action);
        }
    }

    private void showUrl(URL url) {
        removeLastUrlFromUrlHistory();
        currentHistoryIndex++;
        urlHistory.add(url);
        setGotActionsEnabled();
        setPage(url);
        notifyUrlChanged(url);
    }

    private void removeLastUrlFromUrlHistory() {
        int historyUrlCount = urlHistory.size();
        boolean canRemove = historyUrlCount > 0
                && currentHistoryIndex >= 0
                && currentHistoryIndex < historyUrlCount;

        if (canRemove) {
            int removeCount = historyUrlCount - currentHistoryIndex - 1;

            for (int i = 0; i < removeCount; i++) {
                urlHistory.pollLast();
            }
        }
    }

    public synchronized void setDisplayUrl(String url) {
        if (url == null) {
            throw new NullPointerException("url == null");
        }

        displayUrl = url;
    }

    private synchronized void selectDisplayUrl() {
        if (displayUrl != null) {
            HelpNode node = (HelpNode) tree.getModel().getRoot();
            Object[] path = node.getPagePath(displayUrl);

            if (path != null) {
                tree.setSelectionPath(new TreePath(path));
            }
        }
    }

    private void setPage(URL url) {
        try {
            editorPanePage.setPage(url);
        } catch (Exception ex) {
            Logger.getLogger(HelpBrowser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            readDividerLocationFromPreferences();
            restoreTextFontSize();
            selectDisplayUrl();
        } else {
            writeDividerLocationToPreferences();
        }

        super.setVisible(visible);
    }

    private void readDividerLocationFromPreferences() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        if (storage != null && storage.containsKey(KEY_DIVIDER_LOCATION)) {
            splitPane.setDividerLocation(storage.getInt(KEY_DIVIDER_LOCATION));
        }
    }

    private void writeDividerLocationToPreferences() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        if (storage != null) {
            storage.setInt(KEY_DIVIDER_LOCATION, splitPane.getDividerLocation());
        }
    }

    @Override
    public void hyperlinkUpdate(HyperlinkEvent evt) {
        if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            URL url = evt.getURL();
            String jptUrl = toJPhotoTaggerUrl(url);
            Object[] path = rootNode.getPagePath(jptUrl);
            if (path == null) {
                showUrl(url);
            } else {
                tree.setSelectionPath(new TreePath(path));
            }
        }
    }

    public static String toJPhotoTaggerUrl(URL url) {
        if (url == null) {
            throw new NullPointerException("url == null");
        }

        String path = url.getPath();
        int index = path.indexOf("/org/jphototagger");

        if (index >= 0 && index < path.length() - 1) {
            return path.substring(index);
        }

        return path;
    }

    @Override
    public void valueChanged(TreeSelectionEvent evt) {
        synchronized (this) {
            if (!settingPath && evt.isAddedPath()) {
                Object o = evt.getNewLeadSelectionPath().getLastPathComponent();

                if (o instanceof HelpPage) {
                    HelpPage helpPage = (HelpPage) o;
                    String helpPageUrl = helpPage.getUrl();
                    URL url = getClass().getResource(helpPageUrl);

                    super.setTitle(helpPage.getTitle() + " - " + titlePostfix);
                    showUrl(url);
                }
            }
        }
    }

    @Override
    public void setTitle(String title) {
        if (title == null) {
            throw new NullPointerException("title == null");
        }

        titlePostfix = title;
        super.setTitle(title);
    }

    private void setFontSizeOfHelpPageText(int textFontSize) {
        Font font = editorPanePage.getFont();
        int oldSize = font.getSize();
        if (textFontSize == oldSize) {
            return;
        }
        String fontName = font.getName();
        Font newFont = new Font(fontName, 0, textFontSize);
        editorPanePage.setFont(newFont);
        persisteTextFontSize(textFontSize);
        ComponentUtil.forceRepaint(panelContents);
    }

    private void setTextFontSizeOfMenuItem(JMenuItem menuItem) {
        Integer textFontSize = textFontSizeOfMenuItem.get(menuItem);
        if (textFontSize != null) {
            setFontSizeOfHelpPageText(textFontSize);
        }
    }

    private void persisteTextFontSize(int textFontSize) {
        Preferences preferences = Lookup.getDefault().lookup(Preferences.class);
        preferences.setInt(KEY_TEXT_FONT_SIZE, textFontSize);
    }

    private void restoreTextFontSize() {
        Preferences preferences = Lookup.getDefault().lookup(Preferences.class);
        if (preferences != null && preferences.containsKey(KEY_TEXT_FONT_SIZE)) {
            int textFontSize = preferences.getInt(KEY_TEXT_FONT_SIZE);
            for (JMenuItem menuItem : textFontSizeOfMenuItem.keySet()) {
                int fontSizeOfMenuItem = textFontSizeOfMenuItem.get(menuItem);
                if (fontSizeOfMenuItem == textFontSize) {
                     menuItem.setSelected(true);
                     setFontSizeOfHelpPageText(textFontSize);
                     break;
                }
            }
        }
    }

    private final class GoToNextUrlAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        private GoToNextUrlAction() {
            super(Bundle.getString(HelpBrowser.class, "HelpBrowser.GoToNextUrlAction"));
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (canGoToNextUrl()) {
                currentHistoryIndex++;
                URL url = urlHistory.get(currentHistoryIndex);
                setPage(url);
                URL nextUrl = urlHistory.get(currentHistoryIndex);
                String jPhotoTaggerNextUrl = toJPhotoTaggerUrl(nextUrl);
                setSelectionPath(jPhotoTaggerNextUrl);
                setGotActionsEnabled();
            }
        }
    }

    private final class GoToPreviousUrlAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        private GoToPreviousUrlAction() {
            super(Bundle.getString(HelpBrowser.class, "HelpBrowser.GoToPreviousUrlAction"));
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (canGoToPreviousUrl()) {
                currentHistoryIndex--;
                URL url = urlHistory.get(currentHistoryIndex);
                setPage(url);
                URL previousUrl = urlHistory.get(currentHistoryIndex);
                String jPhotoTaggerPreviousUrl = toJPhotoTaggerUrl(previousUrl);
                setSelectionPath(jPhotoTaggerPreviousUrl);
                setGotActionsEnabled();
            }
        }
    }

    private void setGotActionsEnabled() {
        goToNextUrlAction.setEnabled(canGoToNextUrl());
        goToPreviousUrlAction.setEnabled(canGoToPreviousUrl());
    }

    private boolean canGoToNextUrl() {
        return currentHistoryIndex + 1 > 0 && currentHistoryIndex + 1 < urlHistory.size();
    }

    private boolean canGoToPreviousUrl() {
        return currentHistoryIndex - 1 >= 0 && currentHistoryIndex - 1 < urlHistory.size();
    }

    private void setSelectionPath(String lastPathComponent) {
        Object[] path = rootNode.getPagePath(lastPathComponent);

        if (path != null) {
            settingPath = true;
            tree.setSelectionPath(new TreePath(path));
            settingPath = false;
        }
    }

    public HelpPage getSelectedFoundPage() {
        return selectedFoundPage;
    }

    public void setSelectedFoundPage(HelpPage selectedFoundPage) {
        this.selectedFoundPage = selectedFoundPage;
        if (selectedFoundPage != null) {
            setDisplayUrl(selectedFoundPage.getUrl());
            selectDisplayUrl();
        }
    }

    public ObservableList<HelpPage> getFoundPages() {
        return foundPages;
    }

    private class HeplpSearchInit extends Thread {

        private HeplpSearchInit() {
            super("JPhotoTagger: Init Help Search");
        }

        @Override
        public void run() {
            final int index = tabbedPaneContents.indexOfComponent(panelSearch);
            tabbedPaneContents.setEnabledAt(index, false);
            helpSearch.startIndexing();
            EventQueueUtil.invokeInDispatchThread(new Runnable() {

                @Override
                public void run() {
                    textFieldSearch.getDocument().addDocumentListener(new SearchTextFieldListener());
                    tabbedPaneContents.setEnabledAt(index, true);
                }
            });
        }
    }

    private class SearchTextFieldListener implements DocumentListener{

        @Override
        public void insertUpdate(DocumentEvent e) {
            search();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            search();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            search();
        }
    }

    private void search() {
        String searchText = textFieldSearch.getText();
        foundPages.clear();
        if (StringUtil.hasContent(searchText)) {
            List<HelpPage> foundHelpPages = helpSearch.findHelpPagesMatching(searchText);
            foundPages.addAll(foundHelpPages);
        }
    }

    private static class HelpContentsTreeCellRenderer extends DefaultTreeCellRenderer {

        private static final ImageIcon ICON_SECTION_COLLAPSED = IconUtil.getImageIcon(HelpContentsTreeCellRenderer.class, "help_section_collapsed.png");
        private static final ImageIcon ICON_SECTION_EXPANDED = IconUtil.getImageIcon(HelpContentsTreeCellRenderer.class, "help_section_expanded.png");
        private static final ImageIcon ICON_PAGE = IconUtil.getImageIcon(HelpContentsTreeCellRenderer.class, "help_page.png");
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, false, row, hasFocus);

            if (value instanceof HelpPage) {
                setIcon(ICON_PAGE);
                setText(((HelpPage) value).getTitle());
            } else if (value instanceof HelpNode) {
                setIcon(expanded
                        ? ICON_SECTION_EXPANDED
                        : ICON_SECTION_COLLAPSED);
                setText(((HelpNode) value).getTitle());
            } else if (value == tree.getModel().getRoot()) {
                setIcon(null);
                setText("");
            }

            return this;
        }
}

    private static class HelpPageListCellRenderer extends DefaultListCellRenderer {
        private static final long serialVersionUID = 1L;
        private static final ImageIcon ICON = IconUtil.getImageIcon(HelpPageListCellRenderer.class, "help_page.png");

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setIcon(ICON);

            return label;
        }

    }

    private void addSearchAction(){
        buttonSearchInCurrentPage.setAction(new SearchInComponentAction(editorPanePage));
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        popupMenuEditorPane = new javax.swing.JPopupMenu();
        menuItemGotNextUrl = new javax.swing.JMenuItem();
        menuItemGotoPreviousUrl = new javax.swing.JMenuItem();
        buttonGroupTextSize = new javax.swing.ButtonGroup();
        splitPane = new javax.swing.JSplitPane();
        tabbedPaneContents = new javax.swing.JTabbedPane();
        panelContents = new javax.swing.JPanel();
        scrollPaneTree = new javax.swing.JScrollPane();
        tree = new javax.swing.JTree();
        panelSearch = new javax.swing.JPanel();
        labelSearch = new javax.swing.JLabel();
        textFieldSearch = new org.jdesktop.swingx.JXTextField();
        scrollPaneSearchResults = new javax.swing.JScrollPane();
        listSearchResults = new org.jdesktop.swingx.JXList();
        panelPage = new javax.swing.JPanel();
        scrollPanePage = new javax.swing.JScrollPane();
        editorPanePage = new org.jdesktop.swingx.JXEditorPane();
        buttonSearchInCurrentPage = new javax.swing.JButton();
        panelGotoButtons = new javax.swing.JPanel();
        buttonGotoPreviousUrl = new javax.swing.JButton();
        buttonGotoNextUrl = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        menuView = new javax.swing.JMenu();
        menuItemTextFontSizeSmall = new javax.swing.JRadioButtonMenuItem();
        menuItemTextFontSizeNormal = new javax.swing.JRadioButtonMenuItem();
        menuItemTextFontSizeLarge = new javax.swing.JRadioButtonMenuItem();
        menuItemTextFontSizeHuge = new javax.swing.JRadioButtonMenuItem();

        popupMenuEditorPane.setName("popupMenuEditorPane"); // NOI18N

        menuItemGotNextUrl.setAction(goToNextUrlAction);
        menuItemGotNextUrl.setName("menuItemGotNextUrl"); // NOI18N
        popupMenuEditorPane.add(menuItemGotNextUrl);

        menuItemGotoPreviousUrl.setAction(goToPreviousUrlAction);
        menuItemGotoPreviousUrl.setName("menuItemGotoPreviousUrl"); // NOI18N
        popupMenuEditorPane.add(menuItemGotoPreviousUrl);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/lib/help/Bundle"); // NOI18N
        setTitle(bundle.getString("HelpBrowser.title")); // NOI18N
        setName("Form"); // NOI18N

        splitPane.setDividerLocation(250);
        splitPane.setDividerSize(2);
        splitPane.setName("splitPane"); // NOI18N

        tabbedPaneContents.setName("tabbedPaneContents"); // NOI18N

        panelContents.setName("panelContents"); // NOI18N
        panelContents.setLayout(new java.awt.GridBagLayout());

        scrollPaneTree.setName("scrollPaneTree"); // NOI18N
        scrollPaneTree.setPreferredSize(new java.awt.Dimension(150, 10));

        tree.setModel(null);
        tree.setCellRenderer(new HelpContentsTreeCellRenderer());
        tree.setName("tree"); // NOI18N
        scrollPaneTree.setViewportView(tree);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelContents.add(scrollPaneTree, gridBagConstraints);

        tabbedPaneContents.addTab(bundle.getString("HelpBrowser.panelContents.TabConstraints.tabTitle"), panelContents); // NOI18N

        panelSearch.setName("panelSearch"); // NOI18N
        panelSearch.setLayout(new java.awt.GridBagLayout());

        labelSearch.setLabelFor(textFieldSearch);
        labelSearch.setText(bundle.getString("HelpBrowser.labelSearch.text")); // NOI18N
        labelSearch.setName("labelSearch"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        panelSearch.add(labelSearch, gridBagConstraints);

        textFieldSearch.setColumns(15);
        textFieldSearch.setName("textFieldSearch"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panelSearch.add(textFieldSearch, gridBagConstraints);

        scrollPaneSearchResults.setName("scrollPaneSearchResults"); // NOI18N

        listSearchResults.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listSearchResults.setCellRenderer(new HelpPageListCellRenderer());
        listSearchResults.setName("listSearchResults"); // NOI18N

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${foundPages}");
        org.jdesktop.swingbinding.JListBinding jListBinding = org.jdesktop.swingbinding.SwingBindings.createJListBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, listSearchResults);
        bindingGroup.addBinding(jListBinding);
        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedFoundPage}"), listSearchResults, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        scrollPaneSearchResults.setViewportView(listSearchResults);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelSearch.add(scrollPaneSearchResults, gridBagConstraints);

        tabbedPaneContents.addTab(bundle.getString("HelpBrowser.panelSearch.TabConstraints.tabTitle"), panelSearch); // NOI18N

        splitPane.setLeftComponent(tabbedPaneContents);

        panelPage.setName("panelPage"); // NOI18N
        panelPage.setLayout(new java.awt.GridBagLayout());

        scrollPanePage.setName("scrollPanePage"); // NOI18N
        scrollPanePage.setPreferredSize(new java.awt.Dimension(200, 24));

        editorPanePage.setEditable(false);
        editorPanePage.setFont(new java.awt.Font("Verdana", 0, 14));
        editorPanePage.setComponentPopupMenu(popupMenuEditorPane);
        editorPanePage.setName("editorPanePage"); // NOI18N
        scrollPanePage.setViewportView(editorPanePage);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelPage.add(scrollPanePage, gridBagConstraints);

        buttonSearchInCurrentPage.setToolTipText(bundle.getString("HelpBrowser.buttonSearchInCurrentPage.toolTipText")); // NOI18N
        buttonSearchInCurrentPage.setName("buttonSearchInCurrentPage"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        panelPage.add(buttonSearchInCurrentPage, gridBagConstraints);

        panelGotoButtons.setName("panelGotoButtons"); // NOI18N
        panelGotoButtons.setLayout(new java.awt.GridLayout(1, 0, 5, 0));

        buttonGotoPreviousUrl.setAction(goToPreviousUrlAction);
        buttonGotoPreviousUrl.setName("buttonGotoPreviousUrl"); // NOI18N
        panelGotoButtons.add(buttonGotoPreviousUrl);

        buttonGotoNextUrl.setAction(goToNextUrlAction);
        buttonGotoNextUrl.setName("buttonGotoNextUrl"); // NOI18N
        panelGotoButtons.add(buttonGotoNextUrl);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 5);
        panelPage.add(panelGotoButtons, gridBagConstraints);

        splitPane.setRightComponent(panelPage);

        jMenuBar1.setName("jMenuBar1"); // NOI18N

        menuView.setText(bundle.getString("HelpBrowser.menuView.text")); // NOI18N
        menuView.setName("menuView"); // NOI18N

        buttonGroupTextSize.add(menuItemTextFontSizeSmall);
        menuItemTextFontSizeSmall.setText(bundle.getString("HelpBrowser.menuItemTextFontSizeSmall.text")); // NOI18N
        menuItemTextFontSizeSmall.setName("menuItemTextFontSizeSmall"); // NOI18N
        menuItemTextFontSizeSmall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemTextFontSizeSmallActionPerformed(evt);
            }
        });
        menuView.add(menuItemTextFontSizeSmall);

        buttonGroupTextSize.add(menuItemTextFontSizeNormal);
        menuItemTextFontSizeNormal.setSelected(true);
        menuItemTextFontSizeNormal.setText(bundle.getString("HelpBrowser.menuItemTextFontSizeNormal.text")); // NOI18N
        menuItemTextFontSizeNormal.setName("menuItemTextFontSizeNormal"); // NOI18N
        menuItemTextFontSizeNormal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemTextFontSizeNormalActionPerformed(evt);
            }
        });
        menuView.add(menuItemTextFontSizeNormal);

        buttonGroupTextSize.add(menuItemTextFontSizeLarge);
        menuItemTextFontSizeLarge.setText(bundle.getString("HelpBrowser.menuItemTextFontSizeLarge.text")); // NOI18N
        menuItemTextFontSizeLarge.setName("menuItemTextFontSizeLarge"); // NOI18N
        menuItemTextFontSizeLarge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemTextFontSizeLargeActionPerformed(evt);
            }
        });
        menuView.add(menuItemTextFontSizeLarge);

        buttonGroupTextSize.add(menuItemTextFontSizeHuge);
        menuItemTextFontSizeHuge.setText(bundle.getString("HelpBrowser.menuItemTextFontSizeHuge.text")); // NOI18N
        menuItemTextFontSizeHuge.setName("menuItemTextFontSizeHuge"); // NOI18N
        menuItemTextFontSizeHuge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemTextFontSizeHugeActionPerformed(evt);
            }
        });
        menuView.add(menuItemTextFontSizeHuge);

        jMenuBar1.add(menuView);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();

        pack();
    }//GEN-END:initComponents

    private void menuItemTextFontSizeSmallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemTextFontSizeSmallActionPerformed
        setTextFontSizeOfMenuItem(menuItemTextFontSizeSmall);
    }//GEN-LAST:event_menuItemTextFontSizeSmallActionPerformed

    private void menuItemTextFontSizeNormalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemTextFontSizeNormalActionPerformed
        setTextFontSizeOfMenuItem(menuItemTextFontSizeNormal);
    }//GEN-LAST:event_menuItemTextFontSizeNormalActionPerformed

    private void menuItemTextFontSizeLargeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemTextFontSizeLargeActionPerformed
        setTextFontSizeOfMenuItem(menuItemTextFontSizeLarge);
    }//GEN-LAST:event_menuItemTextFontSizeLargeActionPerformed

    private void menuItemTextFontSizeHugeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemTextFontSizeHugeActionPerformed
        setTextFontSizeOfMenuItem(menuItemTextFontSizeHuge);
    }//GEN-LAST:event_menuItemTextFontSizeHugeActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new HelpBrowser(new HelpNode()).setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonGotoNextUrl;
    private javax.swing.JButton buttonGotoPreviousUrl;
    private javax.swing.ButtonGroup buttonGroupTextSize;
    private javax.swing.JButton buttonSearchInCurrentPage;
    private org.jdesktop.swingx.JXEditorPane editorPanePage;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JLabel labelSearch;
    private org.jdesktop.swingx.JXList listSearchResults;
    private javax.swing.JMenuItem menuItemGotNextUrl;
    private javax.swing.JMenuItem menuItemGotoPreviousUrl;
    private javax.swing.JRadioButtonMenuItem menuItemTextFontSizeHuge;
    private javax.swing.JRadioButtonMenuItem menuItemTextFontSizeLarge;
    private javax.swing.JRadioButtonMenuItem menuItemTextFontSizeNormal;
    private javax.swing.JRadioButtonMenuItem menuItemTextFontSizeSmall;
    private javax.swing.JMenu menuView;
    private javax.swing.JPanel panelContents;
    private javax.swing.JPanel panelGotoButtons;
    private javax.swing.JPanel panelPage;
    private javax.swing.JPanel panelSearch;
    private javax.swing.JPopupMenu popupMenuEditorPane;
    private javax.swing.JScrollPane scrollPanePage;
    private javax.swing.JScrollPane scrollPaneSearchResults;
    private javax.swing.JScrollPane scrollPaneTree;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JTabbedPane tabbedPaneContents;
    private org.jdesktop.swingx.JXTextField textFieldSearch;
    private javax.swing.JTree tree;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
