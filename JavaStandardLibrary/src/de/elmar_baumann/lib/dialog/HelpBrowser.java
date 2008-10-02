package de.elmar_baumann.lib.dialog;

import de.elmar_baumann.lib.image.icon.IconUtil;
import de.elmar_baumann.lib.persistence.PersistentAppSizes;
import de.elmar_baumann.lib.resource.Bundle;
import de.elmar_baumann.lib.resource.Settings;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * Browser für Hilfedateien im HTML-Format.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/25
 */
public class HelpBrowser extends javax.swing.JFrame
    implements ActionListener, HyperlinkListener, MouseListener {

    private static HelpBrowser instance = new HelpBrowser();
    private LinkedList<URL> urlHistory = new LinkedList<URL>();
    private int currentHistoryIndex = -1;
    private PopupMenu popupMenu;
    private final String actionPrevious = Bundle.getString("HelpBrowser.Command.Previous");
    private final String actionNext = Bundle.getString("HelpBrowser.Command.Next");
    private MenuItem itemPrevious;
    private MenuItem itemNext;

    private HelpBrowser() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setIcons();
        initPopupMenu();
        editorPaneContent.addHyperlinkListener(this);
    }

    private void setIcons() {
        Settings settings = Settings.getInstance();
        if (settings.hasIconImages()) {
            setIconImages(IconUtil.getIconImages(settings.getIconImagesPaths()));
        }
    }

    /**
     * Liefert die einzige Instanz.
     * 
     * @return Instanz
     */
    public static HelpBrowser getInstance() {
        return instance;
    }

    /**
     * Zeigt einen URL an.
     * 
     * @param url URL
     */
    public void showUrl(URL url) {
        removeNextHistory();
        currentHistoryIndex++;
        urlHistory.add(url);
        setButtonStatus();
        setUrl(url);
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

        editorPaneContent.addMouseListener(this);

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
            popupMenu.show(editorPaneContent, e.getX(), e.getY());
        }
    }

    private void setUrl(URL url) {
        try {
            editorPaneContent.setPage(url);
        } catch (IOException ex) {
            Logger.getLogger(HelpBrowser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            readPersistent();
        } else {
            writePersistent();
        }
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
            showUrl(e.getURL());
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
    }

    private void readPersistent() {
        PersistentAppSizes.getSizeAndLocation(this);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPaneContent = new javax.swing.JScrollPane();
        editorPaneContent = new javax.swing.JEditorPane();
        buttonNext = new javax.swing.JButton();
        buttonPrevious = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/elmar_baumann/lib/resource/Bundle"); // NOI18N
        setTitle(bundle.getString("HelpBrowser.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        editorPaneContent.setEditable(false);
        scrollPaneContent.setViewportView(editorPaneContent);

        buttonNext.setMnemonic('v');
        buttonNext.setText(bundle.getString("HelpBrowser.buttonNext.text")); // NOI18N
        buttonNext.setToolTipText(bundle.getString("HelpBrowser.buttonNext.toolTipText")); // NOI18N
        buttonNext.setEnabled(false);
        buttonNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonNextActionPerformed(evt);
            }
        });

        buttonPrevious.setMnemonic('z');
        buttonPrevious.setText(bundle.getString("HelpBrowser.buttonPrevious.text")); // NOI18N
        buttonPrevious.setToolTipText(bundle.getString("HelpBrowser.buttonPrevious.toolTipText")); // NOI18N
        buttonPrevious.setEnabled(false);
        buttonPrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPreviousActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPaneContent, javax.swing.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(buttonPrevious)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonNext)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPaneContent, javax.swing.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
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
    private javax.swing.JEditorPane editorPaneContent;
    private javax.swing.JScrollPane scrollPaneContent;
    // End of variables declaration//GEN-END:variables

}
