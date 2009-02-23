package de.elmar_baumann.imv.view.dialogs;

import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.data.SavedSearch;
import de.elmar_baumann.imv.event.SearchEvent;
import de.elmar_baumann.imv.event.SearchListener;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.dialog.Dialog;
import de.elmar_baumann.lib.persistence.PersistentComponentSizes;

/**
 * Nicht modaler Dialog für eine erweiterte Suche.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class AdvancedSearchDialog extends Dialog implements SearchListener {

    public static final AdvancedSearchDialog INSTANCE = new AdvancedSearchDialog(null, false);

    private AdvancedSearchDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setIconImages(AppIcons.getAppIcons());
        setHelpContentsUrl(Bundle.getString("Help.Url.Contents"));
        registerKeyStrokes();
    }

    public void setSavedSearch(SavedSearch search) {
        panel.setSavedSearch(search);
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            PersistentComponentSizes.getSizeAndLocation(this);
        }
        super.setVisible(visible);
    }

    private void beforeWindowClosing() {
        PersistentComponentSizes.setSizeAndLocation(this);
        panel.willDispose();
        setVisible(false);
    }

    @Override
    protected void help() {
        help(Bundle.getString("Help.Url.AdvancedSearchDialog"));
    }

    @Override
    protected void escape() {
        beforeWindowClosing();
    }

    @Override
    public void actionPerformed(SearchEvent evt) {
        if (evt.getType().equals(SearchEvent.Type.NAME_CHANGED)) {
            String name = evt.getSearchName();
            String separator = name.isEmpty() ? "" : ": "; // NOI18N
            setTitle(Bundle.getString("AdvancedSearchDialog.TitlePrefix") + separator + name);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new de.elmar_baumann.imv.view.panels.AdvancedSearchPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Bundle.getString("AdvancedSearchDialog.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 707, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 210, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    beforeWindowClosing();
}//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                AdvancedSearchDialog dialog = new AdvancedSearchDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.elmar_baumann.imv.view.panels.AdvancedSearchPanel panel;
    // End of variables declaration//GEN-END:variables
}
