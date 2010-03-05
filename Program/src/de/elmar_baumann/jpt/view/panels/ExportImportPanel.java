/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.view.panels;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.event.listener.impl.ListenerSupport;
import de.elmar_baumann.jpt.exporter.Exporter;
import de.elmar_baumann.jpt.exporter.JptExporters;
import de.elmar_baumann.jpt.importer.Importer;
import de.elmar_baumann.jpt.importer.JptImporters;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.lib.component.SelectObjectsPanel;
import de.elmar_baumann.lib.component.SelectObjectsPanel.SelectionEvent;
import de.elmar_baumann.lib.componentutil.MnemonicUtil;
import de.elmar_baumann.lib.dialog.DirectoryChooser;
import de.elmar_baumann.lib.image.util.IconUtil;
import java.awt.Container;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Elmar Baumann <eb@elmar-baumann.de>
 */
public class ExportImportPanel extends javax.swing.JPanel implements SelectObjectsPanel.SelectionListener {

    private static final    long                                  serialVersionUID        = -4556829908393776160L;
    private static final    String                                TEXT_EXPORT             = JptBundle.INSTANCE.getString("ExportImportPanel.Button.DisplayName.Export");
    private static final    String                                TEXT_IMPORT             = JptBundle.INSTANCE.getString("ExportImportPanel.Button.DisplayName.Import");
    private static final    String                                KEY_SEL_INDICES_EXPORT  = "ExportImportPanel.Export.SelIndices";
    private static final    String                                KEY_SEL_INDICES_IMPORT  = "ExportImportPanel.Import.SelIndices";
    private static final    String                                KEY_LAST_DIR            = "ExportImportPanel.LastDirectory";
    private                 Context                               context                 = Context.EXPORT;
    private                 File                                  dir;
    private final transient ListenerSupport<ExportImportListener> exImportListenerSupport = new ListenerSupport<ExportImportListener>();

    public enum Context {
        EXPORT,
        IMPORT
    }

    public ExportImportPanel() {
        initComponents();
        postInitComponents();
        // Do not call addObjects()!
    }

    public ExportImportPanel(Context context) {
        this.context = context;
        initComponents();
        postInitComponents();
        addObjects();
    }

    private void postInitComponents() {
        MnemonicUtil.setMnemonics((Container)this);
        dir = new File(UserSettings.INSTANCE.getSettings().getString(KEY_LAST_DIR));
        if (dir.isDirectory()) {
            setDirLabel();
        }
        setInfoLabel();
        panelSelectObjects.addSelectionListener(this);
    }

    private void setInfoLabel() {
        labelSelectInfo.setText(isExport()
                ? JptBundle.INSTANCE.getString("ExportImportPanel.LabelSelectInfo.Text.Export")
                : JptBundle.INSTANCE.getString("ExportImportPanel.LabelSelectInfo.Text.Import")
                );
    }

    public void setContext(Context context) {
        this.context = context;
        setInfoLabel();
        addObjects();
    }

    public void addObjects() {
        if (isExport()) {
            setExportCheckBoxes();
        } else {
            setImportCheckBoxes();
        }
        panelSelectObjects.setProperties(UserSettings.INSTANCE.getProperties(),
                  isExport() ? KEY_SEL_INDICES_EXPORT : KEY_SEL_INDICES_IMPORT);
        panelSelectObjects.applyPropertiesSelectedIndices();
        buttonExportImport.setText(isExport() ? TEXT_EXPORT : TEXT_IMPORT);
        MnemonicUtil.setMnemonics((Container)this);
        setEnabledButtons();
    }

    private boolean isExport() {
        return context.equals(Context.EXPORT);
    }

    private void setExportCheckBoxes() {
        List<Exporter> exporters = JptExporters.get();
        
        panelSelectObjects.removeAll();
        panelSelectObjects.setObjectCount(exporters.size());
        for (Exporter exporter : exporters) {
            panelSelectObjects.add(exporter, exporter.getDisplayName());
        }
    }

    private void setImportCheckBoxes() {
        List<Importer> importers = JptImporters.get();
        
        panelSelectObjects.removeAll();
        panelSelectObjects.setObjectCount(importers.size());
        for (Importer importer : importers) {
            panelSelectObjects.add(importer, importer.getDisplayName());
        }
    }

    private void selectDirectory() {
        DirectoryChooser dlg = new DirectoryChooser(GUI.INSTANCE.getAppFrame(), new File(""), UserSettings.INSTANCE.getDirChooserOptionShowHiddenDirs());

        dlg.setVisible(true);
        if (dlg.accepted()) {
            dir = dlg.getSelectedDirectories().get(0);
            UserSettings.INSTANCE.getSettings().set(dir.getAbsolutePath(), KEY_LAST_DIR);
            UserSettings.INSTANCE.writeToFile();
            setDirLabel();
            setEnabledButtons();
        }
    }

    private void setDirLabel() {
        labelDir.setText(dir.getAbsolutePath());
        labelDir.setIcon(IconUtil.getSystemIcon(dir));
    }

    private void exportImport() {
        if (context.equals(Context.EXPORT)) {
            exportFiles();
        } else {
            importFiles();
        }
        notifyExportImportListeners();
    }

    private void exportFiles() {
        List<Object> selectedObjects = panelSelectObjects.getSelectedObjects();
        List<File>   exportedFiles   = new ArrayList<File>(selectedObjects.size());

        for (Object o : selectedObjects) {
            if (o instanceof Exporter) {
                Exporter exporter   = (Exporter) o;
                File     exportFile = new File(dir.getAbsolutePath() + File.separator + exporter.getDefaultFilename());

                AppLogger.logInfo(ExportImportPanel.class, "ExportImportPanel.Info.ExportFile", exporter.getDisplayName(), exportFile);
                exporter.exportFile(exportFile);
                exportedFiles.add(exportFile);
            }
        }
    }

    private void importFiles() {
        List<File>   importedFiles   = new ArrayList<File>();
        List<File>   missingFiles    = new ArrayList<File>();
        List<Object> selectedObjects = panelSelectObjects.getSelectedObjects();

        for (Object o : selectedObjects) {
            if (o instanceof Importer) {
                Importer importer   = (Importer) o;
                File     importFile = new File(dir.getAbsolutePath() + File.separator + importer.getDefaultFilename());

                if (importFile.exists()) {
                    AppLogger.logInfo(ExportImportPanel.class, "ExportImportPanel.Info.ImportFile", importer.getDisplayName(), importFile);
                    importer.importFile(importFile);
                    importedFiles.add(importFile);
                } else {
                    AppLogger.logWarning(ExportImportPanel.class, "ExportImportPanel.Error.ImportFile", importer.getDisplayName(), importFile);
                    missingFiles.add(importFile);
                }
            }
        }
    }

    private void setEnabledButtons() {
        int selCount    = panelSelectObjects.getSelectionCount();
        int objectCount = panelSelectObjects.getObjectCount();
        
        buttonExportImport.setEnabled(dir != null && dir.isDirectory() && selCount > 0);
        buttonSelectAll.setEnabled(objectCount > 0 && objectCount > selCount);
        buttonSelectNone.setEnabled(selCount > 0);
    }

    @Override
    public void objectSelected(SelectionEvent evt) {
        setEnabledButtons();
    }

    private void setSelectedAll(boolean select) {
        panelSelectObjects.setSelectedAll(select);
        setEnabledButtons();
    }

    public static interface ExportImportListener {
        /**
         * Will be called after an ex-/import.
         */
        void done();
    }

    public void addListener(ExportImportListener listener) {
        exImportListenerSupport.add(listener);
    }

    public void removeListener(ExportImportListener listener) {
        exImportListenerSupport.remove(listener);
    }

    private void notifyExportImportListeners() {
        Set<ExportImportListener> listeners = exImportListenerSupport.get();
        synchronized (listeners) {
            for (ExportImportListener listener : listeners) {
                listener.done();
            }
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

        labelPromptDir = new javax.swing.JLabel();
        labelDir = new javax.swing.JLabel();
        buttonSelDir = new javax.swing.JButton();
        labelSelectInfo = new javax.swing.JLabel();
        buttonSelectAll = new javax.swing.JButton();
        buttonSelectNone = new javax.swing.JButton();
        scrollPane = new javax.swing.JScrollPane();
        panelSelectObjects = new de.elmar_baumann.lib.component.SelectObjectsPanel();
        buttonExportImport = new javax.swing.JButton();

        labelPromptDir.setText(JptBundle.INSTANCE.getString("ExportImportPanel.labelPromptDir.text")); // NOI18N

        buttonSelDir.setText(JptBundle.INSTANCE.getString("ExportImportPanel.buttonSelDir.text")); // NOI18N
        buttonSelDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSelDirActionPerformed(evt);
            }
        });

        labelSelectInfo.setText("Auswahl:"); // NOI18N

        buttonSelectAll.setText(JptBundle.INSTANCE.getString("ExportImportPanel.buttonSelectAll.text")); // NOI18N
        buttonSelectAll.setEnabled(false);
        buttonSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSelectAllActionPerformed(evt);
            }
        });

        buttonSelectNone.setText(JptBundle.INSTANCE.getString("ExportImportPanel.buttonSelectNone.text")); // NOI18N
        buttonSelectNone.setEnabled(false);
        buttonSelectNone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSelectNoneActionPerformed(evt);
            }
        });

        scrollPane.setViewportView(panelSelectObjects);

        buttonExportImport.setText(TEXT_EXPORT);
        buttonExportImport.setEnabled(false);
        buttonExportImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonExportImportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(buttonSelectAll)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonSelectNone)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(buttonExportImport))
            .addGroup(layout.createSequentialGroup()
                .addComponent(labelPromptDir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelDir, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonSelDir))
            .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE)
            .addComponent(labelSelectInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE)
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {buttonSelectAll, buttonSelectNone});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(labelDir, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelPromptDir)
                    .addComponent(buttonSelDir))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelSelectInfo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonExportImport)
                    .addComponent(buttonSelectNone)
                    .addComponent(buttonSelectAll)))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {labelDir, labelPromptDir});

    }// </editor-fold>//GEN-END:initComponents

    private void buttonSelDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSelDirActionPerformed
        selectDirectory();
    }//GEN-LAST:event_buttonSelDirActionPerformed

    private void buttonExportImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonExportImportActionPerformed
        exportImport();
    }//GEN-LAST:event_buttonExportImportActionPerformed

    private void buttonSelectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSelectAllActionPerformed
        setSelectedAll(true);
    }//GEN-LAST:event_buttonSelectAllActionPerformed

    private void buttonSelectNoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSelectNoneActionPerformed
        setSelectedAll(false);
    }//GEN-LAST:event_buttonSelectNoneActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonExportImport;
    private javax.swing.JButton buttonSelDir;
    private javax.swing.JButton buttonSelectAll;
    private javax.swing.JButton buttonSelectNone;
    private javax.swing.JLabel labelDir;
    private javax.swing.JLabel labelPromptDir;
    private javax.swing.JLabel labelSelectInfo;
    private de.elmar_baumann.lib.component.SelectObjectsPanel panelSelectObjects;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables
}
