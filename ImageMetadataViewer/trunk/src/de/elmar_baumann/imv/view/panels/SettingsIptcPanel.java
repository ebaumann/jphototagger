package de.elmar_baumann.imv.view.panels;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.event.ListenerProvider;
import de.elmar_baumann.imv.event.UserSettingsChangeEvent;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.types.Persistence;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/11/02
 */
public final class SettingsIptcPanel extends javax.swing.JPanel
    implements Persistence {

    private final ListenerProvider listenerProvider = ListenerProvider.getInstance();

    /** Creates new form SettingsIptcPanel */
    public SettingsIptcPanel() {
        initComponents();
    }

    private void handleActionComboBoxIptcCharset() {
        UserSettingsChangeEvent evt = new UserSettingsChangeEvent(
            UserSettingsChangeEvent.Type.IPTC_CHARSET, this);
        evt.setIptcCharset(comboBoxIptcCharset.getSelectedItem().toString());
        listenerProvider.notifyUserSettingsChangeListener(evt);
    }

    @Override
    public void readPersistent() {
        comboBoxIptcCharset.getModel().setSelectedItem(
            UserSettings.getInstance().getIptcCharset());
    }

    @Override
    public void writePersistent() {
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelIptcCharset = new javax.swing.JLabel();
        comboBoxIptcCharset = new javax.swing.JComboBox();

        labelIptcCharset.setFont(new java.awt.Font("Dialog", 0, 12));
        labelIptcCharset.setText(Bundle.getString("SettingsIptcPanel.labelIptcCharset.text")); // NOI18N

        comboBoxIptcCharset.setFont(new java.awt.Font("Dialog", 0, 12));
        comboBoxIptcCharset.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ISO-8859-1", "UTF-8" }));
        comboBoxIptcCharset.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                comboBoxIptcCharsetPropertyChange(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelIptcCharset)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboBoxIptcCharset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelIptcCharset)
                    .addComponent(comboBoxIptcCharset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void comboBoxIptcCharsetPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_comboBoxIptcCharsetPropertyChange
    handleActionComboBoxIptcCharset();
}//GEN-LAST:event_comboBoxIptcCharsetPropertyChange
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox comboBoxIptcCharset;
    private javax.swing.JLabel labelIptcCharset;
    // End of variables declaration//GEN-END:variables
}
