package org.jphototagger.lib.help;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.openide.util.Lookup;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.lib.swing.Dialog;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;

/**
 * Browser for HTML help files.
 *
 * @author Elmar Baumann
 */
public final class HelpBrowser extends Dialog implements HyperlinkListener, TreeSelectionListener {

    private static final long serialVersionUID = 1L;
    private static final String KEY_DIVIDER_LOCATION = "HelpBrowser.DividerLocation";
    private final LinkedList<URL> urlHistory = new LinkedList<URL>();
    private int currentHistoryIndex = -1;
    private final Set<HelpBrowserListener> listeners = new CopyOnWriteArraySet<HelpBrowserListener>();
    private final GoToNextUrlAction goToNextUrlAction = new GoToNextUrlAction();
    private final GoToPreviousUrlAction goToPreviousUrlAction = new GoToPreviousUrlAction();
    private String displayUrl;
    private boolean settingPath;
    private final HelpNode rootNode;
    private String titlePostfix = Bundle.getString(HelpBrowser.class, "HelpBrowser.TitlePostfix");

    public HelpBrowser(HelpNode rootNode) {
        super(ComponentUtil.findFrameWithIcon());
        if (rootNode == null) {
            throw new NullPointerException("rootNode == null");
        }
        this.rootNode = rootNode;
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

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        popupMenuEditorPane = new javax.swing.JPopupMenu();
        menuItemGotNextUrl = new javax.swing.JMenuItem();
        menuItemGotoPreviousUrl = new javax.swing.JMenuItem();
        splitPane = new javax.swing.JSplitPane();
        panelTree = new javax.swing.JPanel();
        scrollPaneTree = new javax.swing.JScrollPane();
        tree = new javax.swing.JTree();
        panelPage = new javax.swing.JPanel();
        scrollPanePage = new javax.swing.JScrollPane();
        editorPanePage = new javax.swing.JEditorPane();
        panelGotoButtons = new javax.swing.JPanel();
        buttonGotoPreviousUrl = new javax.swing.JButton();
        buttonGotoNextUrl = new javax.swing.JButton();

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

        panelTree.setName("panelTree"); // NOI18N
        panelTree.setLayout(new java.awt.GridBagLayout());

        scrollPaneTree.setName("scrollPaneTree"); // NOI18N
        scrollPaneTree.setPreferredSize(new java.awt.Dimension(150, 10));

        tree.setModel(null);
        tree.setCellRenderer(new org.jphototagger.lib.help.HelpContentsTreeCellRenderer());
        tree.setName("tree"); // NOI18N
        scrollPaneTree.setViewportView(tree);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelTree.add(scrollPaneTree, gridBagConstraints);

        splitPane.setLeftComponent(panelTree);

        panelPage.setName("panelPage"); // NOI18N
        panelPage.setLayout(new java.awt.GridBagLayout());

        scrollPanePage.setName("scrollPanePage"); // NOI18N

        editorPanePage.setEditable(false);
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
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelPage.add(panelGotoButtons, gridBagConstraints);

        splitPane.setRightComponent(panelPage);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }//GEN-END:initComponents

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
    private javax.swing.JEditorPane editorPanePage;
    private javax.swing.JMenuItem menuItemGotNextUrl;
    private javax.swing.JMenuItem menuItemGotoPreviousUrl;
    private javax.swing.JPanel panelGotoButtons;
    private javax.swing.JPanel panelPage;
    private javax.swing.JPanel panelTree;
    private javax.swing.JPopupMenu popupMenuEditorPane;
    private javax.swing.JScrollPane scrollPanePage;
    private javax.swing.JScrollPane scrollPaneTree;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JTree tree;
    // End of variables declaration//GEN-END:variables
}
