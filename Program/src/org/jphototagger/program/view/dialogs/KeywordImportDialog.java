package org.jphototagger.program.view.dialogs;

import org.jphototagger.program.importer.KeywordsImporter;
import org.jphototagger.program.model.ComboBoxModelKeywordsImporters;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;
import org.jphototagger.lib.componentutil.MnemonicUtil;
import org.jphototagger.lib.dialog.Dialog;
import org.jphototagger.lib.util.Settings;

import java.awt.Container;

import java.io.File;

import javax.swing.JFileChooser;

/**
 * Modal dialog for importing keywords.
 *
 * @author Elmar Baumann
 */
public class KeywordImportDialog extends Dialog {
    private static final String KEY_PREV_IMPORT_FILE =
        "KeywordImportDialog.PrevImportFile";
    private static final String KEY_SEL_IMPORTER_INDEX =
        "KeywordImportDialog.SelectedImporterIndex";
    private static final long              serialVersionUID =
        7441650879878050560L;
    private boolean                        accepted;
    private File                           file;
    private ComboBoxModelKeywordsImporters comboBoxModelImporter =
        new ComboBoxModelKeywordsImporters();

    public KeywordImportDialog() {
        super(GUI.getAppFrame(), true,
              UserSettings.INSTANCE.getSettings(), null);
        initComponents();
        setHelpPages();
        MnemonicUtil.setMnemonics((Container) this);
    }

    private void setHelpPages() {
        setHelpContentsUrl(JptBundle.INSTANCE.getString("Help.Url.Contents"));
        setHelpPageUrl(
            JptBundle.INSTANCE.getString("Help.Url.KeywordImportDialog"));
    }

    /**
     * Returns wheter keywords shall be imported.
     *
     * @return true if keywords shall be imported
     */
    public boolean isAccepted() {
        return accepted;
    }

    /**
     * Returns the selected importer.
     *
     * <em>Should be called only when {@link #isAccepted()} is true!</em>
     *
     * @return importer or null if no importer was selected.
     */
    public KeywordsImporter getImporter() {
        assert accepted : "Import was not accepted!";

        if (!accepted) {
            return null;
        }

        Object item = comboBoxImporter.getSelectedItem();

        return (item instanceof KeywordsImporter)
               ? (KeywordsImporter) item
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
     * Returns the file to import.
     *
     * <em>Should be called only when {@link #isAccepted()} is true!</em>
     *
     * @return file or null if no file is to import
     */
    public File getFile() {
        assert accepted : "Import was not accepted!";

        if (!accepted) {
            return null;
        }

        return file;
    }

    private void chooseFile() {
        Object selItem = comboBoxImporter.getSelectedItem();

        if (selItem instanceof KeywordsImporter) {
            JFileChooser fileChooser = new JFileChooser();

            if ((file != null) && file.isFile()) {
                fileChooser.setCurrentDirectory(file.getParentFile());
            }

            fileChooser.setFileFilter(
                ((KeywordsImporter) selItem).getFileFilter());
            fileChooser.setMultiSelectionEnabled(false);
            fileChooser.setDialogTitle(getTitle());

            if (fileChooser.showOpenDialog(this)
                    == JFileChooser.APPROVE_OPTION) {
                File selFile = fileChooser.getSelectedFile();

                if (selFile.isFile()) {
                    file = selFile;
                    labelFilename.setText(file.getAbsolutePath());
                }
            }

            buttonImport.setEnabled((file != null) && file.isFile());
        }
    }

    private void readProperties() {
        Settings settings = UserSettings.INSTANCE.getSettings();

        settings.applySelectedIndex(comboBoxImporter, KEY_SEL_IMPORTER_INDEX);

        File prevImpFile = new File(settings.getString(KEY_PREV_IMPORT_FILE));

        if (prevImpFile.isFile()) {
            file = prevImpFile;
            labelFilename.setText(prevImpFile.getAbsolutePath());
            buttonImport.setEnabled(true);
        }
    }

    private void writeProperties() {
        Settings settings = UserSettings.INSTANCE.getSettings();

        settings.setSelectedIndex(comboBoxImporter, KEY_SEL_IMPORTER_INDEX);

        if ((file != null) && file.isFile()) {
            settings.set(file.getAbsolutePath(), KEY_PREV_IMPORT_FILE);
        }
    }

    private void handleImportActionPerformed() {
        accepted = true;
        setVisible(false);
    }

    private void handleCancelActionPerformed() {
        accepted = false;
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

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelFormat = new javax.swing.JLabel();
        comboBoxImporter = new javax.swing.JComboBox();
        labelInfoFilename = new javax.swing.JLabel();
        labelFilename = new javax.swing.JLabel();
        buttonChooseFile = new javax.swing.JButton();
        buttonCancel = new javax.swing.JButton();
        buttonImport = new javax.swing.JButton();

        setTitle(JptBundle.INSTANCE.getString("KeywordImportDialog.title")); // NOI18N
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        labelFormat.setLabelFor(comboBoxImporter);
        labelFormat.setText(JptBundle.INSTANCE.getString("KeywordImportDialog.labelFormat.text")); // NOI18N
        labelFormat.setName("labelFormat"); // NOI18N

        comboBoxImporter.setModel(comboBoxModelImporter);
        comboBoxImporter.setName("comboBoxImporter"); // NOI18N
        comboBoxImporter.setRenderer(new org.jphototagger.program.view.renderer.ListCellRendererKeywordImExport());

        labelInfoFilename.setText(JptBundle.INSTANCE.getString("KeywordImportDialog.labelInfoFilename.text")); // NOI18N
        labelInfoFilename.setName("labelInfoFilename"); // NOI18N

        labelFilename.setForeground(new java.awt.Color(0, 0, 255));
        labelFilename.setName("labelFilename"); // NOI18N

        buttonChooseFile.setText(JptBundle.INSTANCE.getString("KeywordImportDialog.buttonChooseFile.text")); // NOI18N
        buttonChooseFile.setName("buttonChooseFile"); // NOI18N
        buttonChooseFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseFileActionPerformed(evt);
            }
        });

        buttonCancel.setText(JptBundle.INSTANCE.getString("KeywordImportDialog.buttonCancel.text")); // NOI18N
        buttonCancel.setName("buttonCancel"); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        buttonImport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_import.png"))); // NOI18N
        buttonImport.setText(JptBundle.INSTANCE.getString("KeywordImportDialog.buttonImport.text")); // NOI18N
        buttonImport.setEnabled(false);
        buttonImport.setName("buttonImport"); // NOI18N
        buttonImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonImportActionPerformed(evt);
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
                            .addComponent(comboBoxImporter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelFilename, javax.swing.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(buttonChooseFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonCancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonImport)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelFormat)
                    .addComponent(comboBoxImporter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelInfoFilename)
                    .addComponent(labelFilename))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonImport)
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

    private void buttonImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonImportActionPerformed
        handleImportActionPerformed();
    }//GEN-LAST:event_buttonImportActionPerformed

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
                KeywordImportDialog dialog = new KeywordImportDialog();

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
    private javax.swing.JButton buttonImport;
    private javax.swing.JComboBox comboBoxImporter;
    private javax.swing.JLabel labelFilename;
    private javax.swing.JLabel labelFormat;
    private javax.swing.JLabel labelInfoFilename;
    // End of variables declaration//GEN-END:variables
}
