package org.jphototagger.plugin.cftc;

import java.awt.Component;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import org.openide.util.Lookup;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.lib.util.Bundle;

/**
 * Settings for {@code CopyFilenamesToClipboard}.
 *
 * @author Elmar Baumann
 */
public class SettingsPanel extends javax.swing.JPanel {
    private static final long serialVersionUID = -8198418342037889703L;
    private final DelimiterModel model = new DelimiterModel();
    private final DelimiterRenderer renderer = new DelimiterRenderer();
    private final Preferences storage = Lookup.getDefault().lookup(Preferences.class);

    public SettingsPanel() {
        initComponents();
        setPersistentModelValue();
    }

    private void setPersistentModelValue() {
        if (storage != null) {
            String delim = storage.getString(CopyFilenamesToClipboard.KEY_FILENAME_DELIMITER);

            if (delim == null) {
                delim = CopyFilenamesToClipboard.DEFAULT_FILENAME_DELIMITER;
            }

            comboBoxDelimiter.setSelectedItem(delim);
        }
    }

    private static class DelimiterModel extends DefaultComboBoxModel {
        private static final long serialVersionUID = 78207540460538249L;
        private final Map<String, String> descriptionOfDelimiter = new LinkedHashMap<String, String>();

        DelimiterModel() {
            addElements();
        }

        private void addElements() {
            descriptionOfDelimiter.put("\n", Bundle.getString(SettingsPanel.class, "DelimiterModel.Description.Newline"));
            descriptionOfDelimiter.put(" ", Bundle.getString(SettingsPanel.class, "DelimiterModel.Description.Space"));
            descriptionOfDelimiter.put("\t", Bundle.getString(SettingsPanel.class, "DelimiterModel.Description.Tab"));
            descriptionOfDelimiter.put(";", Bundle.getString(SettingsPanel.class, "DelimiterModel.Description.Semicolon"));
            descriptionOfDelimiter.put(":", Bundle.getString(SettingsPanel.class, "DelimiterModel.Description.Colon"));

            for (String key : descriptionOfDelimiter.keySet()) {
                addElement(key);
            }
        }

        public String getDescription(String delimiter) {
            return descriptionOfDelimiter.get(delimiter);
        }
    }


    private void writeDelimiter() {
        if (storage != null) {
            storage.setString(CopyFilenamesToClipboard.KEY_FILENAME_DELIMITER, model.getSelectedItem().toString());
        }
    }

    private class DelimiterRenderer extends DefaultListCellRenderer {
        private static final long serialVersionUID = -2721860017412493615L;

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof String) {
                String delimiter = (String) value;
                String description = model.getDescription(delimiter);

                assert description != null : "No description of delimiter " + delimiter;

                if (description != null) {
                    label.setText(description);
                }
            }

            return label;
        }
    }


    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        label = new javax.swing.JLabel();
        comboBoxDelimiter = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();

        setName("Form"); // NOI18N

        label.setDisplayedMnemonic('t');
        label.setLabelFor(comboBoxDelimiter);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/plugin/cftc/Bundle"); // NOI18N
        label.setText(bundle.getString("SettingsPanel.label.text")); // NOI18N
        label.setName("label"); // NOI18N

        comboBoxDelimiter.setModel(model);
        comboBoxDelimiter.setName("comboBoxDelimiter"); // NOI18N
        comboBoxDelimiter.setRenderer(renderer);
        comboBoxDelimiter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxDelimiterActionPerformed(evt);
            }
        });

        jLabel1.setText(bundle.getString("SettingsPanel.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(label)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboBoxDelimiter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label)
                    .addComponent(comboBoxDelimiter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void comboBoxDelimiterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxDelimiterActionPerformed
        writeDelimiter();
    }//GEN-LAST:event_comboBoxDelimiterActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox comboBoxDelimiter;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel label;
    // End of variables declaration//GEN-END:variables
}
