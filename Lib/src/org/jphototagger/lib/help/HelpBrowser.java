package org.jphototagger.lib.help;

import java.net.URL;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import org.jphototagger.lib.util.Bundle;

/**
 * Browser for HTML help files.
 *
 * @author Elmar Baumann
 */
public final class HelpBrowser extends Dialog implements HyperlinkListener, TreeSelectionListener {
    private static final long serialVersionUID = 1L;
    private static final String KEY_DIVIDER_LOCATION = "HelpBrowser.DividerLocation";
    private static final String DISPLAY_NAME_ACTION_PREVIOUS = Bundle.getString(HelpBrowser.class, "HelpBrowser.Action.Previous");
    private static final String DISPLAY_NAME_ACTION_NEXT = Bundle.getString(HelpBrowser.class, "HelpBrowser.Action.Next");
    private final Set<HelpBrowserListener> listeners = new CopyOnWriteArraySet<HelpBrowserListener>();
    private String displayUrl;
    private boolean settingPath;

    public HelpBrowser() {
        super(ComponentUtil.findFrameWithIcon());
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        editorPanePage.addHyperlinkListener(this);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(this);
        setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
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
        setUrl(url);
        notifyUrlChanged(url);
    }

    public synchronized void setDisplayUrl(String url) {
        if (url == null) {
            throw new NullPointerException("url == null");
        }

        displayUrl = url;
    }

    /**
     * Sets the URL of the contents XML-File wich validates against <code>helpindex.dtd</code>.
     * <code>helpindex.dtd</code> is in this package.
     *
     * @param url URL, eg. <code>/org/jphototagger/program/resource/doc/de/contents.xml</code>
     */
    public synchronized void setContentsUrl(String url) {
        if (url == null) {
            throw new NullPointerException("url == null");
        }

        HelpIndexParser helpIndexParser = new HelpIndexParser(url);
        HelpNode rootNode = helpIndexParser.parse(HelpBrowser.class.getResourceAsStream(url));

        if (rootNode != null) {
            Collection<? extends HelpContentProvider> providers = Lookup.getDefault().lookupAll(HelpContentProvider.class);
            for (HelpContentProvider provider : providers) {
                String helpContentsUrl = provider.getHelpContentUrl();
                helpIndexParser = new HelpIndexParser(helpContentsUrl);
                HelpNode helpNode = helpIndexParser.parse(HelpBrowser.class.getResourceAsStream(helpContentsUrl));
                addChildrenToNode(helpNode, rootNode);
            }
            tree.setModel(new HelpContentsTreeModel(rootNode));
        }
    }

    private void addChildrenToNode(HelpNode fromHelpNode, HelpNode toHelpNode) {
        int childCount = fromHelpNode.getChildCount();
        for (int index = 0; index < childCount; index++) {
            Object child = fromHelpNode.getChild(index);
            if (child instanceof HelpNode) {
                HelpNode helpNode = (HelpNode) child;
                toHelpNode.addNode(helpNode);
            } else if (child instanceof HelpPage) {
                HelpPage helpPage = (HelpPage) child;
                toHelpNode.addPage(helpPage);
            }
        }
    }

    private synchronized void selectStartUrl() {
        if (displayUrl != null) {
            HelpNode node = (HelpNode) tree.getModel().getRoot();
            Object[] path = node.getPagePath(displayUrl);

            if (path != null) {
                tree.setSelectionPath(new TreePath(path));
            }
        }
    }

    private void setUrl(URL url) {
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
            selectStartUrl();
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
                showUrl(url);
        }
    }

    /**
     * Returns the last path component of an URL.
     *
     * @param  url  URL
     * @return last path component
     */
    public static String getLastPathComponent(URL url) {
        if (url == null) {
            throw new NullPointerException("url == null");
        }

        String path = url.getPath();
        int index = path.lastIndexOf('/');

        if ((index > 0) && (index < path.length() - 1)) {
            return path.substring(index + 1);
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

                    setTitle(helpPage.getTitle() + Bundle.getString(HelpBrowser.class, "HelpBrowser.TitlePostfix"));
                    showUrl(url);
                }
            }
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

        splitPane = new javax.swing.JSplitPane();
        panelTree = new javax.swing.JPanel();
        scrollPaneTree = new javax.swing.JScrollPane();
        tree = new javax.swing.JTree();
        panelPage = new javax.swing.JPanel();
        scrollPanePage = new javax.swing.JScrollPane();
        editorPanePage = new javax.swing.JEditorPane();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/lib/help/Bundle"); // NOI18N
        setTitle(bundle.getString("HelpBrowser.title")); // NOI18N
        setName("Form"); // NOI18N

        splitPane.setDividerLocation(250);
        splitPane.setDividerSize(2);
        splitPane.setName("splitPane"); // NOI18N

        panelTree.setName("panelTree"); // NOI18N

        scrollPaneTree.setName("scrollPaneTree"); // NOI18N

        tree.setModel(null);
        tree.setCellRenderer(new org.jphototagger.lib.help.HelpContentsTreeCellRenderer());
        tree.setName("tree"); // NOI18N
        scrollPaneTree.setViewportView(tree);

        javax.swing.GroupLayout panelTreeLayout = new javax.swing.GroupLayout(panelTree);
        panelTree.setLayout(panelTreeLayout);
        panelTreeLayout.setHorizontalGroup(
            panelTreeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneTree, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        panelTreeLayout.setVerticalGroup(
            panelTreeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneTree, javax.swing.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE)
        );

        splitPane.setLeftComponent(panelTree);

        panelPage.setName("panelPage"); // NOI18N

        scrollPanePage.setName("scrollPanePage"); // NOI18N

        editorPanePage.setEditable(false);
        editorPanePage.setName("editorPanePage"); // NOI18N
        scrollPanePage.setViewportView(editorPanePage);

        javax.swing.GroupLayout panelPageLayout = new javax.swing.GroupLayout(panelPage);
        panelPage.setLayout(panelPageLayout);
        panelPageLayout.setHorizontalGroup(
            panelPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPanePage, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
        );
        panelPageLayout.setVerticalGroup(
            panelPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPanePage, javax.swing.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE)
        );

        splitPane.setRightComponent(panelPage);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 573, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }//GEN-END:initComponents

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new HelpBrowser().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane editorPanePage;
    private javax.swing.JPanel panelPage;
    private javax.swing.JPanel panelTree;
    private javax.swing.JScrollPane scrollPanePage;
    private javax.swing.JScrollPane scrollPaneTree;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JTree tree;
    // End of variables declaration//GEN-END:variables
}
