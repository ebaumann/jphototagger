/*
 * CopyFilenamesToClipboard - plugin for JPhotoTagger
 * Copyright (C) 2009 by Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_bauman.jpt.plugin.cftc;

import java.awt.Component;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * Settings for {@link CopyFilenamesToClipboard}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-27
 */
public class SettingsPanel extends javax.swing.JPanel {

    private static final long              serialVersionUID = -8198418342037889703L;
    private final        DelimiterModel    model            = new DelimiterModel();
    private final        DelimiterRenderer renderer         = new DelimiterRenderer();
    private final        Properties        properties;

    public SettingsPanel(Properties properties) {
        this.properties = properties;
        initComponents();
        setPersistentModelValue();
    }

    private void setPersistentModelValue() {
        if (properties != null) {
            String delim = properties.getProperty(CopyFilenamesToClipboard.KEY_FILENAME_DELIMITER);
            if (delim == null) {
                delim = CopyFilenamesToClipboard.DEFAULT_FILENAME_DELIMITER;
            }
            comboBoxDelimiter.setSelectedItem(delim);
        }
    }

    private class DelimiterModel extends DefaultComboBoxModel {

        private static final long serialVersionUID = 78207540460538249L;

        private final ResourceBundle      rb                     = ResourceBundle.getBundle("de/elmar_bauman/jpt/plugin/cftc/Bundle");
        private final Map<String, String> descriptionOfDelimiter = new LinkedHashMap<String, String>();

        public DelimiterModel() {
            addElements();
        }

        private void addElements() {
            descriptionOfDelimiter.put("\n", rb.getString("DelimiterModel.Description.Newline"));
            descriptionOfDelimiter.put(" " , rb.getString("DelimiterModel.Description.Space"));
            descriptionOfDelimiter.put("\t", rb.getString("DelimiterModel.Description.Tab"));
            descriptionOfDelimiter.put(";" , rb.getString("DelimiterModel.Description.Semicolon"));
            descriptionOfDelimiter.put(":" , rb.getString("DelimiterModel.Description.Colon"));

            for (String key : descriptionOfDelimiter.keySet()) {
                addElement(key);
            }
        }

        public String getDescription(String delimiter) {
            return descriptionOfDelimiter.get(delimiter);
        }
    }

    private void writeDelimiter() {
        if (properties != null) {
            properties.setProperty(
                    CopyFilenamesToClipboard.KEY_FILENAME_DELIMITER,
                    model.getSelectedItem().toString());
        }
    }

    private class DelimiterRenderer extends DefaultListCellRenderer {

        private static final long serialVersionUID = -2721860017412493615L;

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            assert value instanceof String : "Not a String: " + value;
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

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        label = new javax.swing.JLabel();
        comboBoxDelimiter = new javax.swing.JComboBox();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/elmar_bauman/jpt/plugin/cftc/Bundle");
        label.setText(bundle.getString("SettingsPanel.label.text"));

        comboBoxDelimiter.setModel(model);
        comboBoxDelimiter.setRenderer(renderer);
        comboBoxDelimiter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxDelimiterActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboBoxDelimiter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label)
                    .addComponent(comboBoxDelimiter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void comboBoxDelimiterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxDelimiterActionPerformed
        writeDelimiter();
    }//GEN-LAST:event_comboBoxDelimiterActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox comboBoxDelimiter;
    private javax.swing.JLabel label;
    // End of variables declaration//GEN-END:variables
}
