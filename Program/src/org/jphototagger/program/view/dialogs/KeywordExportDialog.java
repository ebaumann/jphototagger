package org.jphototagger.program.view.dialogs;

import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.exporter.Exporter;
import org.jphototagger.program.model.ComboBoxModelKeywordsExporters;
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
 * Modal dialog for exporting keywords.
 *
 * @author Elmar Baumann
 */
public class KeywordExportDialog extends Dialog {
    private static final long serialVersionUID = 5431485480637999486L;
    private static final String KEY_PREV_EXPORT_FILE = "KeywordExportDialog.PrevExportFile";
    private boolean accepted;
    private File file;
    private ComboBoxModelKeywordsExporters comboBoxModelExporter = new ComboBoxModelKeywordsExporters();
    private static final String KEY_SEL_EXPORTER_INDEX = "KeywordExportDialog.SelectedExporterIndex";

    public KeywordExportDialog() {
        super(GUI.getAppFrame(), true, UserSettings.INSTANCE.getSettings(), null);
        initComponents();
        setHelpPages();
        MnemonicUtil.setMnemonics((Container) this);
    }

    private void setHelpPages() {
        setHelpContentsUrl(JptBundle.INSTANCE.getString("Help.Url.Contents"));
        setHelpPageUrl(JptBundle.INSTANCE.getString("Help.Url.KeywordExportDialog"));
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
    public Exporter getExporter() {
        assert accepted : "Export was not accepted!";

        if (!accepted) {
            return null;
        }

        Object item = comboBoxExporter.getSelectedItem();

        return (item instanceof Exporter)
               ? (Exporter) item
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
     * Returns the file to exportFile into.
     *
     * <em>Should be called only when {@link #isAccepted()} is true!</em>
     *
     * @return file or null if no file is to exportFile
     */
    public File getFile() {
        if (!accepted) {
            return null;
        }

        return file;
    }

    private void chooseFile() {
        Object selItem = comboBoxExporter.getSelectedItem();

        if (selItem instanceof Exporter) {
            JFileChooser fileChooser = new JFileChooser();

            if (file != null) {
                fileChooser.setCurrentDirectory(file.getParentFile());
            }

            fileChooser.setFileFilter(((Exporter) selItem).getFileFilter());
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

        if ((prevExpFile.getParentFile() != null) && prevExpFile.getParentFile().isDirectory()) {
            file = prevExpFile;
            labelFilename.setText(prevExpFile.getAbsolutePath());
            buttonExport.setEnabled(true);
        }
    }

    private void writeProperties() {
        Settings settings = UserSettings.INSTANCE.getSettings();

        settings.setSelectedIndex(comboBoxExporter, KEY_SEL_EXPORTER_INDEX);

        if ((file != null) && file.isFile()) {
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
        if (file == null) {
            return false;
        }

        if (!file.exists()) {
            return true;
        }

        return MessageDisplayer.confirmYesNo(this, "KeywordExportDialog.Confirm.OverwriteFile", file);
    }

    @Override
    protected void escape() {
        setVisible(false);
    }

    private void handleCancelActionPerformed() {
        accepted = false;
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
        comboBoxExporter = new javax.swing.JComboBox();
        labelInfoFilename = new javax.swing.JLabel();
        labelFilename = new javax.swing.JLabel();
        buttonChooseFile = new javax.swing.JButton();
        buttonCancel = new javax.swing.JButton();
        buttonExport = new javax.swing.JButton();

        setTitle(JptBundle.INSTANCE.getString("KeywordExportDialog.title")); // NOI18N
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        labelFormat.setLabelFor(comboBoxExporter);
        labelFormat.setText(JptBundle.INSTANCE.getString("KeywordExportDialog.labelFormat.text")); // NOI18N
        labelFormat.setName("labelFormat"); // NOI18N

        comboBoxExporter.setModel(comboBoxModelExporter);
        comboBoxExporter.setName("comboBoxExporter"); // NOI18N
        comboBoxExporter.setRenderer(new org.jphototagger.program.view.renderer.ListCellRendererKeywordImExport());

        labelInfoFilename.setText(JptBundle.INSTANCE.getString("KeywordExportDialog.labelInfoFilename.text")); // NOI18N
        labelInfoFilename.setName("labelInfoFilename"); // NOI18N

        labelFilename.setForeground(new java.awt.Color(0, 0, 255));
        labelFilename.setName("labelFilename"); // NOI18N

        buttonChooseFile.setText(JptBundle.INSTANCE.getString("KeywordExportDialog.buttonChooseFile.text")); // NOI18N
        buttonChooseFile.setName("buttonChooseFile"); // NOI18N
        buttonChooseFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseFileActionPerformed(evt);
            }
        });

        buttonCancel.setText(JptBundle.INSTANCE.getString("KeywordExportDialog.buttonCancel.text")); // NOI18N
        buttonCancel.setName("buttonCancel"); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        buttonExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_export.png"))); // NOI18N
        buttonExport.setText(JptBundle.INSTANCE.getString("KeywordExportDialog.buttonExport.text")); // NOI18N
        buttonExport.setEnabled(false);
        buttonExport.setName("buttonExport"); // NOI18N
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
                KeywordExportDialog dialog = new KeywordExportDialog();

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
