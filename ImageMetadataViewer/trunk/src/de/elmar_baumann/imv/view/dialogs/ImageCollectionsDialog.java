package de.elmar_baumann.imv.view.dialogs;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.view.renderer.ListCellRendererImageCollections;
import de.elmar_baumann.lib.dialog.Dialog;
import java.awt.event.MouseEvent;
import javax.swing.ListModel;

/**
 * Dialog zum Anzeigen und Auswählen der Namen von Bildsammlungen.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/08
 */
public final class ImageCollectionsDialog extends Dialog {

    private boolean ok = false;

    public ImageCollectionsDialog(java.awt.Frame parent) {
        super(parent, true);
        initComponents();
        postInitComponents();
    }

    public boolean isCollectionSelected() {
        return ok && listImageCollectionNames.getSelectedValue() != null;
    }

    public String getSelectedCollectionName() {
        Object value = listImageCollectionNames.getSelectedValue();
        return value == null || !ok ? null : value.toString();
    }

    private void checkDoubleClick(MouseEvent e) {
        if (e.getClickCount() == 2) {
            int index = listImageCollectionNames.locationToIndex(e.getPoint());
            ListModel model = listImageCollectionNames.getModel();
            Object item = model.getElementAt(index);
            if (item != null) {
                ok = true;
                dispose();
            }
        }
    }

    private void handleButtonOkClicked() {
        ok = true;
        dispose();
    }

    private void postInitComponents() {
        setIconImages(AppIcons.getAppIcons());
        setHelpContentsUrl(Bundle.getString("Help.Url.Contents"));
        registerKeyStrokes();
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            UserSettings.INSTANCE.getComponentSizes().getSizeAndLocation(this);
        } else {
            UserSettings.INSTANCE.getComponentSizes().setSizeAndLocation(this);
        }
        super.setVisible(visible);
    }

    @Override
    protected void help() {
        help(Bundle.getString("Help.Url.ImageCollectionsDialog"));
    }

    @Override
    protected void escape() {
        setVisible(false);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelSelectImageCollection = new javax.swing.JLabel();
        scrollPaneImageCollectionNames = new javax.swing.JScrollPane();
        listImageCollectionNames = new javax.swing.JList();
        buttonOk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Bundle.getString("ImageCollectionsDialog.title")); // NOI18N

        labelSelectImageCollection.setFont(new java.awt.Font("Dialog", 0, 12));
        labelSelectImageCollection.setText(Bundle.getString("ImageCollectionsDialog.labelSelectImageCollection.text")); // NOI18N

        listImageCollectionNames.setModel(new de.elmar_baumann.imv.model.ListModelImageCollections());
        listImageCollectionNames.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listImageCollectionNames.setCellRenderer(new ListCellRendererImageCollections());
        listImageCollectionNames.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listImageCollectionNamesMouseClicked(evt);
            }
        });
        scrollPaneImageCollectionNames.setViewportView(listImageCollectionNames);

        buttonOk.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonOk.setMnemonic('o');
        buttonOk.setText(Bundle.getString("ImageCollectionsDialog.buttonOk.text")); // NOI18N
        buttonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOkActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPaneImageCollectionNames, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                    .addComponent(labelSelectImageCollection, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                    .addComponent(buttonOk, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelSelectImageCollection)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPaneImageCollectionNames, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonOk)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void buttonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOkActionPerformed
    handleButtonOkClicked();
}//GEN-LAST:event_buttonOkActionPerformed

private void listImageCollectionNamesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listImageCollectionNamesMouseClicked
    checkDoubleClick(evt);
}//GEN-LAST:event_listImageCollectionNamesMouseClicked

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ImageCollectionsDialog dialog = new ImageCollectionsDialog(new javax.swing.JFrame());
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
    private javax.swing.JButton buttonOk;
    private javax.swing.JLabel labelSelectImageCollection;
    private javax.swing.JList listImageCollectionNames;
    private javax.swing.JScrollPane scrollPaneImageCollectionNames;
    // End of variables declaration//GEN-END:variables

}
