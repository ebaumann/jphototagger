package de.elmar_baumann.lib.dialog;

import de.elmar_baumann.lib.event.HelpBrowserAction;
import de.elmar_baumann.lib.event.HelpBrowserListener;
import de.elmar_baumann.lib.image.icon.IconUtil;
import de.elmar_baumann.lib.model.TreeModelHelpContents;
import de.elmar_baumann.lib.persistence.PersistentAppSizes;
import de.elmar_baumann.lib.persistence.PersistentSettings;
import de.elmar_baumann.lib.persistence.PersistentSettingsHints;
import de.elmar_baumann.lib.renderer.TreeCellRendererHelpContents;
import de.elmar_baumann.lib.resource.Bundle;
import de.elmar_baumann.lib.resource.Settings;
import de.elmar_baumann.lib.util.help.HelpNode;
import de.elmar_baumann.lib.util.help.HelpPage;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * Browser for HTML help files. Usually those are packaged with the application
 * in a JAR file.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/25
 */
public class HelpBrowser extends javax.swing.JFrame
    implements ActionListener, HyperlinkListener, MouseListener, TreeSelectionListener {

    private static HelpBrowser instance = new HelpBrowser();
    private LinkedList<URL> urlHistory = new LinkedList<URL>();
    private int currentHistoryIndex = -1;
    private PopupMenu popupMenu;
    private final String actionPrevious = Bundle.getString("HelpBrowser.Action.Previous");
    private final String actionNext = Bundle.getString("HelpBrowser.Action.Next");
    private Vector<HelpBrowserListener> actionListeners = new Vector<HelpBrowserListener>();
    private MenuItem itemPrevious;
    private MenuItem itemNext;
    private String startUrl;
    private String baseUrl;

    private HelpBrowser() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setIcons();
        initPopupMenu();
        editorPanePage.addHyperlinkListener(this);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(this);
    }

    private void setIcons() {
        Settings settings = Settings.getInstance();
        if (settings.hasIconImages()) {
            setIconImages(IconUtil.getIconImages(settings.getIconImagesPaths()));
        }
    }

    /**
     * Returns the singleton.
     * 
     * @return singleton
     */
    public static HelpBrowser getInstance() {
        return instance;
    }

    /**
     * Adds an action listener.
     * 
     * @param listener  listener
     */
    public void addActionListener(HelpBrowserListener listener) {
        actionListeners.add(listener);
    }

    /**
     * Removes an action listener.
     * 
     * @param listener  listener
     */
    public void removeActionListener(HelpBrowserListener listener) {
        actionListeners.remove(listener);
    }

    private void notifyUrlChanged(URL url) {
        HelpBrowserAction action = new HelpBrowserAction(this, HelpBrowserAction.Type.UrlChanged);
        action.setUrl(url);
        for (HelpBrowserListener listener : actionListeners) {
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
     * Sets the URL of the page to be initial displayed. It has to be relative
     * and exist in the contents XML-File set with
     * {@link #setStartUrl(java.lang.String)}.
     * 
     * @param url  URL, eg. <code>firststeps.html</code>
     */
    public void setStartUrl(String url) {
        startUrl = url;
    }

    /**
     * Sets the URL of the contents XML-File wich validates against <code>helpindex.dtd</code>.
     * <code>helpindex.dtd</code> is in this library:
     * <code>/de/elmar_baumann/lib/resource/helpindex.dtd</code>
     * All paths to help pages within this file have to be relative.
     * 
     * @param url URL, eg. <code>/de/elmar_baumann/imagemetadataviewer/resource/doc/de/contents.xml</code>
     */
    public void setContentsUrl(String url) {
        tree.setModel(new TreeModelHelpContents(url));
        setBaseUrl(url);
    }

    private void setBaseUrl(String url) {
        int index = url.lastIndexOf("/"); // NOI18N
        baseUrl = url.substring(0, index);
    }

    private void selectStartUrl() {
        if (startUrl != null) {
            HelpNode node = (HelpNode) tree.getModel().getRoot();
            Object[] path = node.getPagePath(startUrl);
            if (path != null) {
                tree.setSelectionPath(new TreePath(path));
            }
        }
    }

    private boolean canGoNext() {
        return currentHistoryIndex + 1 > 0 &&
            currentHistoryIndex + 1 < urlHistory.size();
    }

    private boolean canGoPrevious() {
        return currentHistoryIndex - 1 >= 0 &&
            currentHistoryIndex - 1 < urlHistory.size();
    }

    private void goNext() {
        if (currentHistoryIndex + 1 >= 0 && currentHistoryIndex + 1 <
            urlHistory.size()) {
            currentHistoryIndex++;
            setUrl(urlHistory.get(currentHistoryIndex));
            setButtonStatus();
        }
    }

    private void goPrevious() {
        if (currentHistoryIndex - 1 >= 0 && currentHistoryIndex - 1 <
            urlHistory.size()) {
            currentHistoryIndex--;
            setUrl(urlHistory.get(currentHistoryIndex));
            setButtonStatus();
        }
    }

    private void initPopupMenu() {
        popupMenu = new PopupMenu();
        itemPrevious = new MenuItem(actionPrevious);
        itemNext = new MenuItem(actionNext);

        itemPrevious.addActionListener(this);
        itemNext.addActionListener(this);

        editorPanePage.addMouseListener(this);

        popupMenu.add(itemPrevious);
        popupMenu.add(itemNext);
        add(popupMenu);
    }

    private void removeNextHistory() {
        int historyUrlCount = urlHistory.size();
        boolean canRemove = historyUrlCount > 0 && currentHistoryIndex >= 0 &&
            currentHistoryIndex < historyUrlCount;
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

    private void showPopupMenu(MouseEvent e) {
        if (e.isPopupTrigger()) {
            itemPrevious.setEnabled(canGoPrevious());
            itemNext.setEnabled(canGoNext());
            popupMenu.show(editorPanePage, e.getX(), e.getY());
        }
    }

    private void setUrl(URL url) {
        try {
            editorPanePage.setPage(url);
        } catch (IOException ex) {
            Logger.getLogger(HelpBrowser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            readPersistent();
            selectStartUrl();
        } else {
            writePersistent();
        }
        super.setVisible(visible);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == itemPrevious) {
            goPrevious();
        } else if (e.getSource() == itemNext) {
            goNext();
        }
    }

    @Override
    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            URL url = e.getURL();
            String lastPathComponent = getLastPathComponent(url);
            Object[] path = ((HelpNode) tree.getModel().getRoot()).getPagePath(lastPathComponent);
            if (path == null) {
                showUrl(url);
            } else {
                tree.setSelectionPath(new TreePath(path));
            }
        }
    }

    /**
     * Returns the last path component of an URL.
     * 
     * @param  url  URL
     * @return last path component
     */
    public static String getLastPathComponent(URL url) {
        String path = url.getPath();
        int index = path.lastIndexOf("/");
        if (index > 0 && index < path.length() - 1) {
            return path.substring(index + 1);
        }
        return path;
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if (e.isAddedPath()) {
            Object o = e.getNewLeadSelectionPath().getLastPathComponent();
            if (o instanceof HelpPage) {
                HelpPage helpPage = (HelpPage) o;
                String helpPageUrl = helpPage.getUrl();
                URL url = this.getClass().getResource(baseUrl + "/" + helpPageUrl); // NOI18N
                setTitle(helpPage.getTitle() + Bundle.getString("HelpBrowser.TitlePostfix"));
                showUrl(url);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        showPopupMenu(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    private void writePersistent() {
        PersistentAppSizes.setSizeAndLocation(this);
        PersistentSettings.getInstance().setComponent(this, getHints());
    }

    private void readPersistent() {
        PersistentAppSizes.getSizeAndLocation(this);
        PersistentSettings.getInstance().getComponent(this, getHints());
    }

    private PersistentSettingsHints getHints() {
        PersistentSettingsHints hints = new PersistentSettingsHints();
        hints.addExcludedMember(getClass().getName() + ".tree");
        return hints;
    }

    /** This method is called from within the constructor to
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/elmar_baumann/lib/resource/Bundle"); // NOI18N
        setTitle(bundle.getString("HelpBrowser.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        tree.setCellRenderer(new TreeCellRendererHelpContents());
        tree.setModel(null);
        tree.setRootVisible(false);
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

        editorPanePage.setEditable(false);
        scrollPanePage.setViewportView(editorPanePage);

        javax.swing.GroupLayout panelPageLayout = new javax.swing.GroupLayout(panelPage);
        panelPage.setLayout(panelPageLayout);
        panelPageLayout.setHorizontalGroup(
            panelPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPanePage, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)
        );
        panelPageLayout.setVerticalGroup(
            panelPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPanePage, javax.swing.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE)
        );

        splitPane.setRightComponent(panelPage);

        buttonPrevious.setMnemonic('z');
        buttonPrevious.setText(bundle.getString("HelpBrowser.buttonPrevious.text")); // NOI18N
        buttonPrevious.setToolTipText(bundle.getString("HelpBrowser.buttonPrevious.toolTipText")); // NOI18N
        buttonPrevious.setEnabled(false);
        buttonPrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPreviousActionPerformed(evt);
            }
        });

        buttonNext.setMnemonic('v');
        buttonNext.setText(bundle.getString("HelpBrowser.buttonNext.text")); // NOI18N
        buttonNext.setToolTipText(bundle.getString("HelpBrowser.buttonNext.toolTipText")); // NOI18N
        buttonNext.setEnabled(false);
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

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    writePersistent();
}//GEN-LAST:event_formWindowClosing

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                getInstance().setVisible(true);
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
