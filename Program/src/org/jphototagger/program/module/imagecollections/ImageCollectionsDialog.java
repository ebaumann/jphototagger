package org.jphototagger.program.module.imagecollections;

import java.awt.Container;
import java.awt.event.MouseEvent;
import javax.swing.ListModel;
import org.jphototagger.lib.swing.Dialog;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.resource.GUI;

/**
 * Dialog zum Anzeigen und Auswählen der Namen von Bildsammlungen.
 *
 * @author Elmar Baumann
 */
public final class ImageCollectionsDialog extends Dialog {

    private static final long serialVersionUID = 1L;
    private boolean ok = false;

    public ImageCollectionsDialog() {
        super(GUI.getAppFrame(), true);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setHelpPageUrl(Bundle.getString(ImageCollectionsDialog.class, "ImageCollectionsDialog.HelpPage"));
        MnemonicUtil.setMnemonics((Container) this);
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
            ListModel<?> model = listImageCollectionNames.getModel();
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
        java.awt.GridBagConstraints gridBagConstraints;

        panelContent = new javax.swing.JPanel();
        labelSelectImageCollection = org.jphototagger.resources.UiFactory.label();
        scrollPaneImageCollectionNames = org.jphototagger.resources.UiFactory.scrollPane();
        listImageCollectionNames = new org.jdesktop.swingx.JXList();
        buttonOk = org.jphototagger.resources.UiFactory.button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Bundle.getString(getClass(), "ImageCollectionsDialog.title")); // NOI18N
        setName("Form"); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panelContent.setName("panelContent"); // NOI18N
        panelContent.setLayout(new java.awt.GridBagLayout());

        labelSelectImageCollection.setLabelFor(listImageCollectionNames);
        labelSelectImageCollection.setText(Bundle.getString(getClass(), "ImageCollectionsDialog.labelSelectImageCollection.text")); // NOI18N
        labelSelectImageCollection.setName("labelSelectImageCollection"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panelContent.add(labelSelectImageCollection, gridBagConstraints);

        scrollPaneImageCollectionNames.setName("scrollPaneImageCollectionNames"); // NOI18N

        listImageCollectionNames.setModel(ModelFactory.INSTANCE.getModel(org.jphototagger.program.module.imagecollections.ImageCollectionsListModel.class));
        listImageCollectionNames.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listImageCollectionNames.setCellRenderer(new org.jphototagger.program.module.imagecollections.ImageCollectionsListCellRenderer());
        listImageCollectionNames.setName("listImageCollectionNames"); // NOI18N
        listImageCollectionNames.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listImageCollectionNamesMouseClicked(evt);
            }
        });
        scrollPaneImageCollectionNames.setViewportView(listImageCollectionNames);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        panelContent.add(scrollPaneImageCollectionNames, gridBagConstraints);

        buttonOk.setText(Bundle.getString(getClass(), "ImageCollectionsDialog.buttonOk.text")); // NOI18N
        buttonOk.setName("buttonOk"); // NOI18N
        buttonOk.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOkActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(7, 0, 0, 0);
        panelContent.add(buttonOk, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(7, 7, 7, 7);
        getContentPane().add(panelContent, gridBagConstraints);

        pack();
    }//GEN-END:initComponents

    private void buttonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOkActionPerformed
        handleButtonOkClicked();
    }//GEN-LAST:event_buttonOkActionPerformed

    private void listImageCollectionNamesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listImageCollectionNamesMouseClicked
        checkDoubleClick(evt);
    }//GEN-LAST:event_listImageCollectionNamesMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonOk;
    private javax.swing.JLabel labelSelectImageCollection;
    private org.jdesktop.swingx.JXList listImageCollectionNames;
    private javax.swing.JPanel panelContent;
    private javax.swing.JScrollPane scrollPaneImageCollectionNames;
    // End of variables declaration//GEN-END:variables
}
