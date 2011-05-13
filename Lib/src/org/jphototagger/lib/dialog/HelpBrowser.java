package org.jphototagger.lib.dialog;

import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.lib.event.HelpBrowserEvent;
import org.jphototagger.lib.event.listener.HelpBrowserListener;
import org.jphototagger.lib.model.TreeModelHelpContents;
import org.jphototagger.lib.renderer.TreeCellRendererHelpContents;
import org.jphototagger.lib.resource.JslBundle;
import org.jphototagger.lib.util.help.HelpNode;
import org.jphototagger.lib.util.help.HelpPage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Frame;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.net.URL;
import java.util.LinkedList;
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
import org.jphototagger.lib.util.Settings;

/**
 * Browser for HTML help files. Usually those are packaged with the application
 * in a JAR file.
 *
 * @author Elmar Baumann
 */
public final class HelpBrowser extends Dialog
        implements ActionListener, HyperlinkListener, MouseListener, TreeSelectionListener {
    private static final long serialVersionUID = 6909713450716449838L;
    private static final String KEY_DIVIDER_LOCATION = "HelpBrowser.DividerLocation";
    private static final String DISPLAY_NAME_ACTION_PREVIOUS = JslBundle.INSTANCE.getString("HelpBrowser.Action.Previous");
    private static final String DISPLAY_NAME_ACTION_NEXT = JslBundle.INSTANCE.getString("HelpBrowser.Action.Next");
    private final LinkedList<URL> urlHistory = new LinkedList<URL>();
    private final Set<HelpBrowserListener> listeners = new CopyOnWriteArraySet<HelpBrowserListener>();
    private int currentHistoryIndex = -1;
    private PopupMenu popupMenu;
    private MenuItem itemPrevious;
    private MenuItem itemNext;
    private String startUrl;
    private String baseUrl;
    private String contentsUrl;
    private boolean settingPath;
    public static final HelpBrowser INSTANCE = new HelpBrowser(ComponentUtil.getFrameWithIcon());

    private HelpBrowser(Frame parent) {
        super(parent);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        initPopupMenu();
        editorPanePage.addHyperlinkListener(this);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(this);
        setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
    }

    /**
     * Adds an action listener.
     *
     * @param listener  listener
     */
    public void addHelpBrowserListener(HelpBrowserListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        listeners.add(listener);
    }

    /**
     * Removes an action listener.
     *
     * @param listener  listener
     */
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
        removeNextHistory();
        currentHistoryIndex++;
        urlHistory.add(url);
        setButtonStatus();
        setUrl(url);
        notifyUrlChanged(url);
    }

    /**
     * Shows a page with a specific URL. Call this, if the dialog is visible.
     *
     * @param url  URL
     */
    public synchronized void showUrl(String url) {
        if (url == null) {
            throw new NullPointerException("url == null");
        }

        if (isVisible()) {
            showUrl(getClass().getResource(baseUrl + "/" + url));
        }
    }

    /**
     * Sets the URL of the page to be initial displayed. It has to be relative
     * and exist in the contents XML-File set with
     * {@link #setDisplayUrl(java.lang.String)}.
     *
     * @param url  URL, eg. <code>firststeps.html</code>
     */
    public synchronized void setDisplayUrl(String url) {
        if (url == null) {
            throw new NullPointerException("url == null");
        }

        startUrl = url;
    }

    /**
     * Sets the URL of the contents XML-File wich validates against <code>helpindex.dtd</code>.
     * <code>helpindex.dtd</code> is in this library:
     * <code>/org/jphototagger/lib/resource/dtd/helpindex.dtd</code>
     * All paths to help pages within this file have to be relative.
     *
     * @param url URL, eg. <code>/org/jphototagger/program/resource/doc/de/contents.xml</code>
     */
    public synchronized void setContentsUrl(String url) {
        if (url == null) {
            throw new NullPointerException("url == null");
        }

        contentsUrl = url;
        urlHistory.clear();
        tree.setModel(new TreeModelHelpContents(url));
        setBaseUrl(url);
    }

    public synchronized String getContentsUrl() {
        return contentsUrl;
    }

    private synchronized void setBaseUrl(String url) {
        int index = url.lastIndexOf('/');

        baseUrl = url.substring(0, index);
    }

    private synchronized void selectStartUrl() {
        if (startUrl != null) {
            HelpNode node = (HelpNode) tree.getModel().getRoot();
            Object[] path = node.getPagePath(startUrl);

            if (path != null) {
                tree.setSelectionPath(new TreePath(path));
            }
        }
    }

    private boolean canGoNext() {
        return (currentHistoryIndex + 1 > 0) && (currentHistoryIndex + 1 < urlHistory.size());
    }

    private boolean canGoPrevious() {
        return (currentHistoryIndex - 1 >= 0) && (currentHistoryIndex - 1 < urlHistory.size());
    }

    private void goNext() {
        if ((currentHistoryIndex + 1 >= 0) && (currentHistoryIndex + 1 < urlHistory.size())) {
            currentHistoryIndex++;
            setUrl(urlHistory.get(currentHistoryIndex));
            setSelectionPath(getLastPathComponent(urlHistory.get(currentHistoryIndex)));
            setButtonStatus();
        }
    }

    private void goPrevious() {
        if ((currentHistoryIndex - 1 >= 0) && (currentHistoryIndex - 1 < urlHistory.size())) {
            currentHistoryIndex--;
            setUrl(urlHistory.get(currentHistoryIndex));
            setSelectionPath(getLastPathComponent(urlHistory.get(currentHistoryIndex)));
            setButtonStatus();
        }
    }

    private void initPopupMenu() {
        popupMenu = new PopupMenu();
        itemPrevious = new MenuItem(DISPLAY_NAME_ACTION_PREVIOUS);
        itemNext = new MenuItem(DISPLAY_NAME_ACTION_NEXT);
        itemPrevious.addActionListener(this);
        itemNext.addActionListener(this);
        editorPanePage.addMouseListener(this);
        popupMenu.add(itemPrevious);
        popupMenu.add(itemNext);
        add(popupMenu);
    }

    private void removeNextHistory() {
        int historyUrlCount = urlHistory.size();
        boolean canRemove = (historyUrlCount > 0)
                            && (currentHistoryIndex >= 0)
                            && (currentHistoryIndex < historyUrlCount);

        if (canRemove) {
            int removeCount = historyUrlCount - currentHistoryIndex - 1;

            for (int i = 0; i < removeCount; i++) {
                urlHistory.pollLast();
            }
        }
    }

    private void setButtonStatus() {
        buttonPrevious.setEnabled(canGoPrevious());
        buttonNext.setEnabled(canGoNext());
    }

    private void showPopupMenu(MouseEvent evt) {
        if (evt.isPopupTrigger()) {
            itemPrevious.setEnabled(canGoPrevious());
            itemNext.setEnabled(canGoNext());
            popupMenu.show(editorPanePage, evt.getX(), evt.getY());
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
            readDividerLocationFromProperties();
            selectStartUrl();
        } else {
            writeDividerLocationToProperties();
        }

        super.setVisible(visible);
    }

    private void readDividerLocationFromProperties() {
        Settings settings = getSettings();

        if (settings != null && settings.containsKey(KEY_DIVIDER_LOCATION)) {
            splitPane.setDividerLocation(settings.getInt(KEY_DIVIDER_LOCATION));
        }
    }

    private void writeDividerLocationToProperties() {
        Settings settings = getSettings();

        if (settings != null) {
            settings.set(splitPane.getDividerLocation(), KEY_DIVIDER_LOCATION);
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == itemPrevious) {
            goPrevious();
        } else if (evt.getSource() == itemNext) {
            goNext();
        }
    }

    @Override
    public void hyperlinkUpdate(HyperlinkEvent evt) {
        if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            URL url = evt.getURL();
            String  lastPathComponent = getLastPathComponent(url);
            Object[] path = ((HelpNode) tree.getModel().getRoot()).getPagePath(lastPathComponent);

            if (path == null) {
                showUrl(url);
            } else {
                tree.setSelectionPath(new TreePath(path));
            }
        }
    }

    private void setSelectionPath(String lastPathComponent) {
        Object[] path = ((HelpNode) tree.getModel().getRoot()).getPagePath(lastPathComponent);

        assert path != null;

        if (path != null) {
            settingPath = true;
            tree.setSelectionPath(new TreePath(path));
            settingPath = false;
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
                    URL url = getClass().getResource(baseUrl + "/" + helpPageUrl);

                    setTitle(helpPage.getTitle() + JslBundle.INSTANCE.getString("HelpBrowser.TitlePostfix"));
                    showUrl(url);
                }
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent evt) {}

    @Override
    public void mousePressed(MouseEvent evt) {
        showPopupMenu(evt);
    }

    @Override
    public void mouseReleased(MouseEvent evt) {}

    @Override
    public void mouseEntered(MouseEvent evt) {}

    @Override
    public void mouseExited(MouseEvent evt) {}

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        splitPane = new javax.swing.JSplitPane();
        panelTree = new javax.swing.JPanel();
        scrollPaneTree = new javax.swing.JScrollPane();
        tree = new javax.swing.JTree();
        panelPage = new javax.swing.JPanel();
        scrollPanePage = new javax.swing.JScrollPane();
        editorPanePage = new javax.swing.JEditorPane();
        buttonPrevious = new javax.swing.JButton();
        buttonNext = new javax.swing.JButton();

        setTitle(JslBundle.INSTANCE.getString("HelpBrowser.title")); // NOI18N
        setName("Form"); // NOI18N

        splitPane.setDividerLocation(250);
        splitPane.setDividerSize(2);
        splitPane.setName("splitPane"); // NOI18N

        panelTree.setName("panelTree"); // NOI18N

        scrollPaneTree.setName("scrollPaneTree"); // NOI18N

        tree.setModel(null);
        tree.setCellRenderer(new TreeCellRendererHelpContents());
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

        buttonPrevious.setMnemonic('z');
        buttonPrevious.setText(JslBundle.INSTANCE.getString("HelpBrowser.buttonPrevious.text")); // NOI18N
        buttonPrevious.setToolTipText(JslBundle.INSTANCE.getString("HelpBrowser.buttonPrevious.toolTipText")); // NOI18N
        buttonPrevious.setEnabled(false);
        buttonPrevious.setName("buttonPrevious"); // NOI18N
        buttonPrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPreviousActionPerformed(evt);
            }
        });

        buttonNext.setMnemonic('v');
        buttonNext.setText(JslBundle.INSTANCE.getString("HelpBrowser.buttonNext.text")); // NOI18N
        buttonNext.setToolTipText(JslBundle.INSTANCE.getString("HelpBrowser.buttonNext.toolTipText")); // NOI18N
        buttonNext.setEnabled(false);
        buttonNext.setName("buttonNext"); // NOI18N
        buttonNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonNextActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 573, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(buttonPrevious)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonNext)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonNext)
                    .addComponent(buttonPrevious))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonNextActionPerformed
        goNext();
    }//GEN-LAST:event_buttonNextActionPerformed

    private void buttonPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonPreviousActionPerformed
        goPrevious();
    }//GEN-LAST:event_buttonPreviousActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                INSTANCE.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonNext;
    private javax.swing.JButton buttonPrevious;
    private javax.swing.JEditorPane editorPanePage;
    private javax.swing.JPanel panelPage;
    private javax.swing.JPanel panelTree;
    private javax.swing.JScrollPane scrollPanePage;
    private javax.swing.JScrollPane scrollPaneTree;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JTree tree;
    // End of variables declaration//GEN-END:variables
}
