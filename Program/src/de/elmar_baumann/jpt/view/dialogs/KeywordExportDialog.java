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
package de.elmar_baumann.jpt.view.dialogs;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.exporter.KeywordExporter;
import de.elmar_baumann.jpt.model.ComboBoxModelKeywordExporters;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.lib.componentutil.MnemonicUtil;
import de.elmar_baumann.lib.dialog.Dialog;
import de.elmar_baumann.lib.util.Settings;
import java.awt.Container;
import java.io.File;
import javax.swing.JFileChooser;

/**
 * Modal dialog for exporting keywords.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-02
 */
public class KeywordExportDialog extends Dialog {

    private static final String                        KEY_PREV_EXPORT_FILE   = "KeywordExportDialog.PrevExportFile";
    private static final long                          serialVersionUID       = 5431485480637999486L;
    private              boolean                       accepted;
    private              File                          file;
    private              ComboBoxModelKeywordExporters comboBoxModelExporter  = new ComboBoxModelKeywordExporters();
    private static final String                        KEY_SEL_EXPORTER_INDEX = "KeywordExportDialog.SelectedExporterIndex";

    public KeywordExportDialog() {
        super(GUI.INSTANCE.getAppFrame(), true, UserSettings.INSTANCE.getSettings(), null);
        initComponents();
        setHelpPages();
        MnemonicUtil.setMnemonics((Container) this);
    }

    private void setHelpPages() {
        setHelpContentsUrl(Bundle.getString("Help.Url.Contents"));
        setHelpPageUrl(Bundle.getString("Help.Url.KeywordExportDialog"));
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
    public KeywordExporter getExporter() {
        assert accepted : "Export was not accepted!";
        if (!accepted) return null;
        Object item = comboBoxExporter.getSelectedItem();
        return item instanceof KeywordExporter
               ? (KeywordExporter) item
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
        assert accepted : "Export was not accepted!";
        if (!accepted) return null;
        return file;
    }

    private void chooseFile() {
        Object selItem = comboBoxExporter.getSelectedItem();
        assert selItem instanceof KeywordExporter : selItem;
        if (selItem instanceof KeywordExporter) {
            JFileChooser fileChooser = new JFileChooser();
            if (file != null) {
                fileChooser.setCurrentDirectory(file.getParentFile());
            }
            fileChooser.setFileFilter(
                    ((KeywordExporter) selItem).getFileFilter());
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
        settings.applySelectedIndex(comboBoxExporter, KEY_SEL_EXPORTER_INDEX);
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
        settings.setSelectedIndex(comboBoxExporter, KEY_SEL_EXPORTER_INDEX);
        if (file != null && file.isFile()) {
            settings.set(file.getAbsolutePath(), KEY_PREV_EXPORT_FILE);
        }
    }

    private void handleExportActionPerformed() {
        if (checkOverwrite()) {
            accepted = true;
            setVisible(false);
        }
    }

    private boolean checkOverwrite() {
        assert file != null : "File is null!";
        if (file == null) return false;
        if (!file.exists()) return true;
        return MessageDisplayer.confirmYesNo(
                this,
                "KeywordExportDialog.Confirm.OverwriteFile",
                file);
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
        buttonCancel = new javax.swing.JButton();
        buttonExport = new javax.swing.JButton();

        setTitle(Bundle.getString("KeywordExportDialog.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        labelFormat.setLabelFor(comboBoxExporter);
        labelFormat.setText(Bundle.getString("KeywordExportDialog.labelFormat.text")); // NOI18N

        comboBoxExporter.setModel(comboBoxModelExporter);
        comboBoxExporter.setRenderer(new de.elmar_baumann.jpt.view.renderer.ListCellRendererKeywordImExport());

        labelInfoFilename.setText(Bundle.getString("KeywordExportDialog.labelInfoFilename.text")); // NOI18N

        labelFilename.setForeground(new java.awt.Color(0, 0, 255));

        buttonChooseFile.setText(Bundle.getString("KeywordExportDialog.buttonChooseFile.text")); // NOI18N
        buttonChooseFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseFileActionPerformed(evt);
            }
        });

        buttonCancel.setText(Bundle.getString("KeywordExportDialog.buttonCancel.text")); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        buttonExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_export.png"))); // NOI18N
        buttonExport.setText(Bundle.getString("KeywordExportDialog.buttonExport.text")); // NOI18N
        buttonExport.setEnabled(false);
        buttonExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonExportActionPerformed(evt);
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
                            .addComponent(labelFilename, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(buttonChooseFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                KeywordExportDialog dialog =
                        new KeywordExportDialog();
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
