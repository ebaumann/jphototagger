/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
package de.elmar_baumann.imv.view.dialogs;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.app.AppLookAndFeel;
import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.exporter.HierarchicalKeywordsExporter;
import de.elmar_baumann.imv.model.ComboBoxModelHierarchicalKeywordsExporters;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.dialog.Dialog;
import de.elmar_baumann.lib.util.Settings;
import java.io.File;
import javax.swing.JFileChooser;

/**
 * Modal dialog to export hierarchical keywords.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-02
 */
public class HierarchicalKeywordsExportDialog extends Dialog {

    private static final String KEY_PREV_EXPORT_FILE =
            "HierarchicalKeywordsExportDialog.PrevExportFile"; // NOI18N
    private boolean accepted;
    private File file;
    private ComboBoxModelHierarchicalKeywordsExporters comboBoxModelExporter =
            new ComboBoxModelHierarchicalKeywordsExporters();

    public HierarchicalKeywordsExportDialog() {
        super((java.awt.Frame) null, true);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setIconImages(AppLookAndFeel.getAppIcons());
        registerKeyStrokes();
        setHelpContentsUrl(Bundle.getString("Help.Url.Contents")); // NOI18N
    }

    /**
     * Returns wheter keywords shall be exported.
     *
     * @return true if keywords shall be exported
     */
    public boolean isAccepted() {
        return accepted;
    }

    /**
     * Returns the selected exporter.
     *
     * <em>Should be called only when {@link #isAccepted()} is true!</em>
     *
     * @return exporter or null if no exporter was selected.
     */
    public HierarchicalKeywordsExporter getExporter() {
        assert accepted : "Export was not accepted!"; // NOI18N
        if (!accepted) return null;
        Object item = comboBoxExporter.getSelectedItem();
        return item instanceof HierarchicalKeywordsExporter
               ? (HierarchicalKeywordsExporter) item
               : null;
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            readProperties();
        } else {
            writeProperties();
        }
        super.setVisible(visible);
    }

    /**
     * Returns the file to export into.
     *
     * <em>Should be called only when {@link #isAccepted()} is true!</em>
     *
     * @return file or null if no file is to export
     */
    public File getFile() {
        assert accepted : "Export was not accepted!"; // NOI18N
        if (!accepted) return null;
        return file;
    }

    private void chooseFile() {
        Object selItem = comboBoxExporter.getSelectedItem();
        assert selItem instanceof HierarchicalKeywordsExporter :
                "Not a HierarchicalKeywordsExporter: " + selItem; // NOI18N
        if (selItem instanceof HierarchicalKeywordsExporter) {
            JFileChooser fileChooser = new JFileChooser();
            if (file != null) {
                fileChooser.setCurrentDirectory(file.getParentFile());
            }
            fileChooser.setFileFilter(
                    ((HierarchicalKeywordsExporter) selItem).getFileFilter());
            fileChooser.setMultiSelectionEnabled(false);
            fileChooser.setDialogTitle(getTitle());
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File selFile = fileChooser.getSelectedFile();
                file = selFile;
                labelFilename.setText(file.getAbsolutePath());
            }
            buttonExport.setEnabled(file != null);
        }

    }

    private void readProperties() {
        Settings settings = UserSettings.INSTANCE.getSettings();
        settings.getSizeAndLocation(this);
        File prevExpFile = new File(settings.getString(KEY_PREV_EXPORT_FILE));
        if (prevExpFile.getParentFile() != null &&
                prevExpFile.getParentFile().isDirectory()) {
            file = prevExpFile;
            labelFilename.setText(prevExpFile.getAbsolutePath());
            buttonExport.setEnabled(true);
        }
    }

    private void writeProperties() {
        Settings settings = UserSettings.INSTANCE.getSettings();
        settings.setSizeAndLocation(this);
        if (file != null && file.isFile()) {
            settings.setString(file.getAbsolutePath(), KEY_PREV_EXPORT_FILE);
        }
    }

    private void handleExportActionPerformed() {
        if (checkOverwrite()) {
            accepted = true;
            setVisible(false);
        }
    }

    private boolean checkOverwrite() {
        assert file != null : "File is null!"; // NOI18N
        if (file == null) return false;
        if (!file.exists()) return true;
        return MessageDisplayer.confirm(this,
                "HierarchicalKeywordsExportDialog.Confirm.OverwriteFile", // NOI18N
                MessageDisplayer.CancelButton.HIDE, file).equals(
                MessageDisplayer.ConfirmAction.YES);
    }

    private void handleCancelActionPerformed() {
        accepted = false;
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

        labelFormat = new javax.swing.JLabel();
        comboBoxExporter = new javax.swing.JComboBox();
        labelInfoFilename = new javax.swing.JLabel();
        labelFilename = new javax.swing.JLabel();
        buttonChooseFile = new javax.swing.JButton();
        buttonExport = new javax.swing.JButton();
        buttonCancel = new javax.swing.JButton();

        setTitle(Bundle.getString("HierarchicalKeywordsExportDialog.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        labelFormat.setText(Bundle.getString("HierarchicalKeywordsExportDialog.labelFormat.text")); // NOI18N

        comboBoxExporter.setModel(comboBoxModelExporter);
        comboBoxExporter.setRenderer(new de.elmar_baumann.imv.view.renderer.ListCellRendererHierarchicalKeywordsImExporter());

        labelInfoFilename.setText(Bundle.getString("HierarchicalKeywordsExportDialog.labelInfoFilename.text")); // NOI18N

        labelFilename.setForeground(new java.awt.Color(0, 0, 255));

        buttonChooseFile.setMnemonic('d');
        buttonChooseFile.setText(Bundle.getString("HierarchicalKeywordsExportDialog.buttonChooseFile.text")); // NOI18N
        buttonChooseFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseFileActionPerformed(evt);
            }
        });

        buttonExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icons/icon_export.png"))); // NOI18N
        buttonExport.setMnemonic('e');
        buttonExport.setText(Bundle.getString("HierarchicalKeywordsExportDialog.buttonExport.text")); // NOI18N
        buttonExport.setEnabled(false);
        buttonExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonExportActionPerformed(evt);
            }
        });

        buttonCancel.setMnemonic('a');
        buttonCancel.setText(Bundle.getString("HierarchicalKeywordsExportDialog.buttonCancel.text")); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelFormat)
                            .addComponent(labelInfoFilename))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(comboBoxExporter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelFilename, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(buttonChooseFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                        .addComponent(buttonCancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonExport)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelFormat)
                    .addComponent(comboBoxExporter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelInfoFilename)
                    .addComponent(labelFilename))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonExport)
                    .addComponent(buttonCancel)
                    .addComponent(buttonChooseFile))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {labelFilename, labelInfoFilename});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        writeProperties();
    }//GEN-LAST:event_formWindowClosing

    private void buttonExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonExportActionPerformed
        handleExportActionPerformed();
    }//GEN-LAST:event_buttonExportActionPerformed

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        handleCancelActionPerformed();
    }//GEN-LAST:event_buttonCancelActionPerformed

    private void buttonChooseFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseFileActionPerformed
        chooseFile();
    }//GEN-LAST:event_buttonChooseFileActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                HierarchicalKeywordsExportDialog dialog =
                        new HierarchicalKeywordsExportDialog();
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
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonChooseFile;
    private javax.swing.JButton buttonExport;
    private javax.swing.JComboBox comboBoxExporter;
    private javax.swing.JLabel labelFilename;
    private javax.swing.JLabel labelFormat;
    private javax.swing.JLabel labelInfoFilename;
    // End of variables declaration//GEN-END:variables
}
