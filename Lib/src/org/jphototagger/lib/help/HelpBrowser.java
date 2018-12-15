package org.jphototagger.lib.help;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.lib.api.LookAndFeelChangedEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.swing.Dialog;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.swingx.SearchInComponentAction;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * Browser for HTML showHelp files.
 *
 * @author Elmar Baumann
 */
public final class HelpBrowser extends Dialog implements HyperlinkListener, TreeSelectionListener, PropertyChangeListener {

    private static final long serialVersionUID = 1L;
    private static final String KEY_DIVIDER_LOCATION = "HelpBrowser.DividerLocation";
    private static final String KEY_TEXT_FONT_SIZE = "HelpBrowser.TextFontSize";
    private final LinkedList<URL> urlHistory = new LinkedList<>();
    private int currentHistoryIndex = -1;
    private final Set<HelpBrowserListener> listeners = new CopyOnWriteArraySet<>();
    private final GoToNextUrlAction goToNextUrlAction = new GoToNextUrlAction();
    private final GoToPreviousUrlAction goToPreviousUrlAction = new GoToPreviousUrlAction();
    private String displayUrl;
    private boolean settingPath;
    private final HelpNode rootNode;
    private final Collection<HelpPage> helpPages;
    private final HelpSearch helpSearch;
    private String titlePostfix = Bundle.getString(HelpBrowser.class, "HelpBrowser.TitlePostfix");
    private HelpPage selectedFoundPage;
    private final Map<JMenuItem, Integer> textFontSizeOfMenuItem = new HashMap<>();
    private final ObservableList<HelpPage> foundPages = ObservableCollections.observableList(new ArrayList<HelpPage>());
    private volatile boolean isHighlight;
    private final TextHighlighter textHighLighter;

    public HelpBrowser(HelpNode rootNode) {
        super(ComponentUtil.findFrameWithIcon());
        if (rootNode == null) {
            throw new NullPointerException("rootNode == null");
        }
        this.rootNode = rootNode;
        this.helpSearch = new HelpSearch(rootNode);
        this.helpPages = new LinkedHashSet<>(HelpUtil.findHelpPagesRecursive(rootNode));
        initComponents();
        postInitComponents();
        tree.setModel(new HelpContentsTreeModel(rootNode));
        textHighLighter = new TextHighlighter(editorPanePage);
    }

    private void postInitComponents() {
        editorPanePage.addHyperlinkListener(this);
        editorPanePage.addPropertyChangeListener(this);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(this);
        tree.setRowHeight(0);
        setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
        MnemonicUtil.setMnemonics(this);
        MnemonicUtil.setMnemonics(popupMenuEditorPane);
        addSearchAction();
        initPreviousNextShortcuts();
        initTextFontSizeOfMenuItem();
        AnnotationProcessor.process(this);
        new HeplpSearchInit().start();
    }

    private void initTextFontSizeOfMenuItem() {
        textFontSizeOfMenuItem.put(menuItemTextFontSizeSmall, UiFactory.scale(10));
        textFontSizeOfMenuItem.put(menuItemTextFontSizeNormal, UiFactory.scale(14));
        textFontSizeOfMenuItem.put(menuItemTextFontSizeLarge, UiFactory.scale(18));
        textFontSizeOfMenuItem.put(menuItemTextFontSizeHuge, UiFactory.scale(28));
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
            textHighLighter.removeAllHighlights();
            editorPanePage.setPage(url);
        } catch (Throwable t) {
            Logger.getLogger(HelpBrowser.class.getName()).log(Level.SEVERE, null, t);
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
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        if (prefs != null && prefs.containsKey(KEY_DIVIDER_LOCATION)) {
            splitPane.setDividerLocation(prefs.getInt(KEY_DIVIDER_LOCATION));
        }
    }

    private void writeDividerLocationToPreferences() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        if (prefs != null) {
            prefs.setInt(KEY_DIVIDER_LOCATION, splitPane.getDividerLocation());
        }
    }

    @Override
    public void hyperlinkUpdate(HyperlinkEvent evt) {
        if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            URL url = evt.getURL();
            String jptUrl = toPageUrl(url);
            Object[] path = rootNode.getPagePath(jptUrl);
            if (path == null) {
                showUrl(url);
            } else {
                tree.setSelectionPath(new TreePath(path));
            }
        }
    }

    public String toPageUrl(URL url) {
        if (url == null) {
            throw new NullPointerException("url == null");
        }

        String urlPath = url.getPath();
        if (!StringUtil.hasContent(urlPath)) {
            return "";
        }

        String foundPageUrl = null;
        for (HelpPage page : helpPages) {
            String pageUrl = page.getUrl();
            if (pageUrl.equals(urlPath) || urlPath.endsWith(pageUrl)) {
                foundPageUrl = pageUrl;
                break;
            }
        }

        return foundPageUrl == null ? urlPath : foundPageUrl;
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
                String jPhotoTaggerNextUrl = toPageUrl(nextUrl);
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
                String jPhotoTaggerPreviousUrl = toPageUrl(previousUrl);
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
            isHighlight = true;
            selectDisplayUrl();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("page".equals(evt.getPropertyName()) && isHighlight) {
            isHighlight = false;
            highLightSearchTerms();
        }
    }

    private void highLightSearchTerms() {
        String text = textFieldSearch.getText();
        String[] searchTerms = text.split(" ");
        List<String> searchTermsWithContent = StringUtil.getStringsWithContent(searchTerms);
        if (!searchTermsWithContent.isEmpty()) {
            textHighLighter.highlightWords(new HashSet<>(searchTermsWithContent));
            ComponentUtil.forceRepaint(editorPanePage);
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

        private static final ImageIcon ICON_SECTION_COLLAPSED = org.jphototagger.resources.Icons.getIcon("icon_help_section_collapsed.png");
        private static final ImageIcon ICON_SECTION_EXPANDED = org.jphototagger.resources.Icons.getIcon("icon_help_section_expanded.png");
        private static final ImageIcon ICON_PAGE = org.jphototagger.resources.Icons.getIcon("icon_help_page.png");
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
        private static final ImageIcon ICON = org.jphototagger.resources.Icons.getIcon("icon_help_page.png");

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setIcon(ICON);
            return label;
        }
    }

    private void addSearchAction(){
        String name = Bundle.getString(HelpBrowser.class, "HelpBrowser.SearchInComponentAction.Name");
        String tooltipText = Bundle.getString(HelpBrowser.class, "HelpBrowser.SearchInComponentAction.TooltipText");
        SearchInComponentAction searchInComponentAction = new SearchInComponentAction(editorPanePage, name);
        buttonSearchInCurrentPage.setAction(searchInComponentAction);
        buttonSearchInCurrentPage.setToolTipText(tooltipText);
        MnemonicUtil.setMnemonics(buttonSearchInCurrentPage);
    }

    private void printPage() {
        try {
            editorPanePage.print();
        } catch (Throwable t) {
            Logger.getLogger(HelpBrowser.class.getName()).log(Level.SEVERE, null, t);
        }
    }

    @EventSubscriber(eventClass = LookAndFeelChangedEvent.class)
    public void lookAndFeelChanged(LookAndFeelChangedEvent evt) {
        SwingUtilities.updateComponentTreeUI(this);
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        popupMenuEditorPane = new javax.swing.JPopupMenu();
        menuItemGotNextUrl = org.jphototagger.resources.UiFactory.menuItem();
        menuItemGotoPreviousUrl = org.jphototagger.resources.UiFactory.menuItem();
        buttonGroupTextSize = new javax.swing.ButtonGroup();
        splitPane = UiFactory.splitPane();
        tabbedPaneContents = org.jphototagger.resources.UiFactory.tabbedPane();
        panelContents = org.jphototagger.resources.UiFactory.panel();
        scrollPaneTree = org.jphototagger.resources.UiFactory.scrollPane();
        tree = org.jphototagger.resources.UiFactory.tree();
        panelSearch = org.jphototagger.resources.UiFactory.panel();
        labelSearch = org.jphototagger.resources.UiFactory.label();
        textFieldSearch = new org.jdesktop.swingx.JXTextField();
        scrollPaneSearchResults = org.jphototagger.resources.UiFactory.scrollPane();
        listSearchResults = org.jphototagger.resources.UiFactory.jxList();
        panelPage = org.jphototagger.resources.UiFactory.panel();
        scrollPanePage = org.jphototagger.resources.UiFactory.scrollPane();
        editorPanePage = new org.jdesktop.swingx.JXEditorPane();
        panelButtons = org.jphototagger.resources.UiFactory.panel();
        buttonPrint = org.jphototagger.resources.UiFactory.button();
        buttonSearchInCurrentPage = org.jphototagger.resources.UiFactory.button();
        buttonGotoPreviousUrl = org.jphototagger.resources.UiFactory.button();
        buttonGotoNextUrl = org.jphototagger.resources.UiFactory.button();
        menuBar = org.jphototagger.resources.UiFactory.menuBar();
        menuView = org.jphototagger.resources.UiFactory.menu();
        menuItemTextFontSizeSmall = UiFactory.radioButtonMenuItem();
        menuItemTextFontSizeNormal = UiFactory.radioButtonMenuItem();
        menuItemTextFontSizeLarge = UiFactory.radioButtonMenuItem();
        menuItemTextFontSizeHuge = UiFactory.radioButtonMenuItem();

        popupMenuEditorPane.setName("popupMenuEditorPane"); // NOI18N

        menuItemGotNextUrl.setAction(goToNextUrlAction);
        menuItemGotNextUrl.setName("menuItemGotNextUrl"); // NOI18N
        popupMenuEditorPane.add(menuItemGotNextUrl);

        menuItemGotoPreviousUrl.setAction(goToPreviousUrlAction);
        menuItemGotoPreviousUrl.setName("menuItemGotoPreviousUrl"); // NOI18N
        popupMenuEditorPane.add(menuItemGotoPreviousUrl);

        setTitle(Bundle.getString(getClass(), "HelpBrowser.title")); // NOI18N
        setName("Form"); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        splitPane.setDividerLocation(250);
        splitPane.setDividerSize(2);
        splitPane.setName("splitPane"); // NOI18N

        tabbedPaneContents.setName("tabbedPaneContents"); // NOI18N

        panelContents.setName("panelContents"); // NOI18N
        panelContents.setLayout(new java.awt.GridBagLayout());

        scrollPaneTree.setName("scrollPaneTree"); // NOI18N
        scrollPaneTree.setPreferredSize(org.jphototagger.resources.UiFactory.dimension(150, 10));

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

        tabbedPaneContents.addTab(Bundle.getString(getClass(), "HelpBrowser.panelContents.TabConstraints.tabTitle"), panelContents); // NOI18N

        panelSearch.setName("panelSearch"); // NOI18N
        panelSearch.setLayout(new java.awt.GridBagLayout());

        labelSearch.setLabelFor(textFieldSearch);
        labelSearch.setText(Bundle.getString(getClass(), "HelpBrowser.labelSearch.text")); // NOI18N
        labelSearch.setName("labelSearch"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 0, 0);
        panelSearch.add(labelSearch, gridBagConstraints);

        textFieldSearch.setColumns(15);
        textFieldSearch.setName("textFieldSearch"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 0, 5);
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
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 5, 5);
        panelSearch.add(scrollPaneSearchResults, gridBagConstraints);

        tabbedPaneContents.addTab(Bundle.getString(getClass(), "HelpBrowser.panelSearch.TabConstraints.tabTitle"), panelSearch); // NOI18N

        splitPane.setLeftComponent(tabbedPaneContents);

        panelPage.setName("panelPage"); // NOI18N
        panelPage.setLayout(new java.awt.GridBagLayout());

        scrollPanePage.setName("scrollPanePage"); // NOI18N
        scrollPanePage.setPreferredSize(org.jphototagger.resources.UiFactory.dimension(200, 24));

        editorPanePage.setEditable(false);
        editorPanePage.setFont(new java.awt.Font("Verdana", 0, UiFactory.scale(14))); // NOI18N
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

        panelButtons.setName("panelButtons"); // NOI18N
        panelButtons.setLayout(new java.awt.GridLayout(1, 0, UiFactory.scale(5), 0));

        buttonPrint.setText(Bundle.getString(getClass(), "HelpBrowser.buttonPrint.text")); // NOI18N
        buttonPrint.setToolTipText(Bundle.getString(getClass(), "HelpBrowser.buttonPrint.toolTipText")); // NOI18N
        buttonPrint.setName("buttonPrint"); // NOI18N
        buttonPrint.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPrintActionPerformed(evt);
            }
        });
        panelButtons.add(buttonPrint);

        buttonSearchInCurrentPage.setToolTipText(Bundle.getString(getClass(), "HelpBrowser.buttonSearchInCurrentPage.toolTipText")); // NOI18N
        buttonSearchInCurrentPage.setName("buttonSearchInCurrentPage"); // NOI18N
        panelButtons.add(buttonSearchInCurrentPage);

        buttonGotoPreviousUrl.setAction(goToPreviousUrlAction);
        buttonGotoPreviousUrl.setName("buttonGotoPreviousUrl"); // NOI18N
        panelButtons.add(buttonGotoPreviousUrl);

        buttonGotoNextUrl.setAction(goToNextUrlAction);
        buttonGotoNextUrl.setName("buttonGotoNextUrl"); // NOI18N
        panelButtons.add(buttonGotoNextUrl);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 5, 5);
        panelPage.add(panelButtons, gridBagConstraints);

        splitPane.setRightComponent(panelPage);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(7, 7, 7, 7);
        getContentPane().add(splitPane, gridBagConstraints);

        menuBar.setName("menuBar"); // NOI18N

        menuView.setText(Bundle.getString(getClass(), "HelpBrowser.menuView.text")); // NOI18N
        menuView.setName("menuView"); // NOI18N

        buttonGroupTextSize.add(menuItemTextFontSizeSmall);
        menuItemTextFontSizeSmall.setText(Bundle.getString(getClass(), "HelpBrowser.menuItemTextFontSizeSmall.text")); // NOI18N
        menuItemTextFontSizeSmall.setName("menuItemTextFontSizeSmall"); // NOI18N
        menuItemTextFontSizeSmall.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemTextFontSizeSmallActionPerformed(evt);
            }
        });
        menuView.add(menuItemTextFontSizeSmall);

        buttonGroupTextSize.add(menuItemTextFontSizeNormal);
        menuItemTextFontSizeNormal.setSelected(true);
        menuItemTextFontSizeNormal.setText(Bundle.getString(getClass(), "HelpBrowser.menuItemTextFontSizeNormal.text")); // NOI18N
        menuItemTextFontSizeNormal.setName("menuItemTextFontSizeNormal"); // NOI18N
        menuItemTextFontSizeNormal.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemTextFontSizeNormalActionPerformed(evt);
            }
        });
        menuView.add(menuItemTextFontSizeNormal);

        buttonGroupTextSize.add(menuItemTextFontSizeLarge);
        menuItemTextFontSizeLarge.setText(Bundle.getString(getClass(), "HelpBrowser.menuItemTextFontSizeLarge.text")); // NOI18N
        menuItemTextFontSizeLarge.setName("menuItemTextFontSizeLarge"); // NOI18N
        menuItemTextFontSizeLarge.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemTextFontSizeLargeActionPerformed(evt);
            }
        });
        menuView.add(menuItemTextFontSizeLarge);

        buttonGroupTextSize.add(menuItemTextFontSizeHuge);
        menuItemTextFontSizeHuge.setText(Bundle.getString(getClass(), "HelpBrowser.menuItemTextFontSizeHuge.text")); // NOI18N
        menuItemTextFontSizeHuge.setName("menuItemTextFontSizeHuge"); // NOI18N
        menuItemTextFontSizeHuge.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemTextFontSizeHugeActionPerformed(evt);
            }
        });
        menuView.add(menuItemTextFontSizeHuge);

        menuBar.add(menuView);

        setJMenuBar(menuBar);

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

    private void buttonPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonPrintActionPerformed
        printPage();
    }//GEN-LAST:event_buttonPrintActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonGotoNextUrl;
    private javax.swing.JButton buttonGotoPreviousUrl;
    private javax.swing.ButtonGroup buttonGroupTextSize;
    private javax.swing.JButton buttonPrint;
    private javax.swing.JButton buttonSearchInCurrentPage;
    private org.jdesktop.swingx.JXEditorPane editorPanePage;
    private javax.swing.JLabel labelSearch;
    private org.jdesktop.swingx.JXList listSearchResults;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem menuItemGotNextUrl;
    private javax.swing.JMenuItem menuItemGotoPreviousUrl;
    private javax.swing.JRadioButtonMenuItem menuItemTextFontSizeHuge;
    private javax.swing.JRadioButtonMenuItem menuItemTextFontSizeLarge;
    private javax.swing.JRadioButtonMenuItem menuItemTextFontSizeNormal;
    private javax.swing.JRadioButtonMenuItem menuItemTextFontSizeSmall;
    private javax.swing.JMenu menuView;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelContents;
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
