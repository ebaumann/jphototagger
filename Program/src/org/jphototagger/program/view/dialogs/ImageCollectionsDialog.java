package org.jphototagger.program.view.dialogs;

import java.awt.Container;
import java.awt.event.MouseEvent;

import javax.swing.ListModel;

import org.jphototagger.lib.componentutil.MnemonicUtil;
import org.jphototagger.lib.dialog.Dialog;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.model.ImageCollectionsListModel;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.renderer.ImageCollectionsListCellRenderer;

/**
 * Dialog zum Anzeigen und Auswählen der Namen von Bildsammlungen.
 *
 * @author Elmar Baumann
 */
public final class ImageCollectionsDialog extends Dialog {
    private static final long serialVersionUID = 1314098937293915298L;
    private boolean ok = false;

    public ImageCollectionsDialog() {
        super(GUI.getAppFrame(), true);
        initComponents();
        setHelpPage();
        MnemonicUtil.setMnemonics((Container) this);
    }

    private void setHelpPage() {
        // Has to be localized!
        setHelpContentsUrl("/org/jphototagger/program/resource/doc/de/contents.xml");
        setHelpPageUrl("collection_add_to.html");
    }

    public boolean isCollectionSelected() {
        return ok && (listImageCollectionNames.getSelectedValue() != null);
    }

    public String getSelectedCollectionName() {
        Object value = listImageCollectionNames.getSelectedValue();

        return ((value == null) ||!ok)
               ? null
               : value.toString();
    }

    private void checkDoubleClick(MouseEvent evt) {
        if (evt.getClickCount() == 2) {
            int index = listImageCollectionNames.locationToIndex(evt.getPoint());
            ListModel model = listImageCollectionNames.getModel();
            Object item  = model.getElementAt(index);

            if (item != null) {
                ok = true;
                setVisible(true);
            }
        }
    }

    private void handleButtonOkClicked() {
        ok = true;
        setVisible(false);
    }

    @Override
    protected void escape() {
        setVisible(false);
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    private void initComponents() {//GEN-BEGIN:initComponents

        labelSelectImageCollection = new javax.swing.JLabel();
        scrollPaneImageCollectionNames = new javax.swing.JScrollPane();
        listImageCollectionNames = new org.jdesktop.swingx.JXList();
        buttonOk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/view/dialogs/Bundle"); // NOI18N
        setTitle(bundle.getString("ImageCollectionsDialog.title")); // NOI18N
        setName("Form"); // NOI18N

        labelSelectImageCollection.setLabelFor(listImageCollectionNames);
        labelSelectImageCollection.setText(bundle.getString("ImageCollectionsDialog.labelSelectImageCollection.text")); // NOI18N
        labelSelectImageCollection.setName("labelSelectImageCollection"); // NOI18N

        scrollPaneImageCollectionNames.setName("scrollPaneImageCollectionNames"); // NOI18N

        listImageCollectionNames.setModel(ModelFactory.INSTANCE.getModel(ImageCollectionsListModel.class));
        listImageCollectionNames.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listImageCollectionNames.setCellRenderer(new ImageCollectionsListCellRenderer());
        listImageCollectionNames.setName("listImageCollectionNames"); // NOI18N
        listImageCollectionNames.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listImageCollectionNamesMouseClicked(evt);
            }
        });
        scrollPaneImageCollectionNames.setViewportView(listImageCollectionNames);

        buttonOk.setText(bundle.getString("ImageCollectionsDialog.buttonOk.text")); // NOI18N
        buttonOk.setName("buttonOk"); // NOI18N
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
                    .addComponent(scrollPaneImageCollectionNames, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                    .addComponent(labelSelectImageCollection, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                    .addComponent(buttonOk, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelSelectImageCollection)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPaneImageCollectionNames, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonOk)
                .addContainerGap())
        );

        pack();
    }//GEN-END:initComponents

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
                ImageCollectionsDialog dialog = new ImageCollectionsDialog();

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
    private org.jdesktop.swingx.JXList listImageCollectionNames;
    private javax.swing.JScrollPane scrollPaneImageCollectionNames;
    // End of variables declaration//GEN-END:variables
}
