package org.jphototagger.program.module.exportimport;

import java.awt.Container;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openide.util.Lookup;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.event.listener.ListenerSupport;
import org.jphototagger.domain.repository.RepositoryDataExporter;
import org.jphototagger.domain.repository.RepositoryDataImporter;
import org.jphototagger.lib.swing.SelectObjectsPanel;
import org.jphototagger.lib.swing.SelectObjectsPanel.SelectionEvent;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.swing.DirectoryChooser;
import org.jphototagger.lib.swing.DirectoryChooser.Option;
import org.jphototagger.lib.swing.LongMessageDialog;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.CollectionUtil;
import org.jphototagger.lib.comparator.PositionComparatorAscendingOrder;
import org.jphototagger.program.resource.GUI;

/**
 * @author Elmar Baumann
 */
public class ExportImportPanel extends javax.swing.JPanel implements SelectObjectsPanel.SelectionListener {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ExportImportPanel.class.getName());
    private static final String TEXT_EXPORT = Bundle.getString(ExportImportPanel.class, "ExportImportPanel.Button.DisplayName.Export");
    private static final String TEXT_IMPORT = Bundle.getString(ExportImportPanel.class, "ExportImportPanel.Button.DisplayName.Import");
    private static final String KEY_SEL_INDICES_EXPORT = "ExportImportPanel.Export.SelIndices";
    private static final String KEY_SEL_INDICES_IMPORT = "ExportImportPanel.Import.SelIndices";
    private static final String KEY_LAST_DIR = "ExportImportPanel.LastDirectory";
    private ExportImportContext context = ExportImportContext.EXPORT;
    private File dir;
    private final transient ListenerSupport<ExportImportListener> ls = new ListenerSupport<ExportImportListener>();

    public ExportImportPanel() {
        initComponents();
        postInitComponents();

        // Do not call addObjects()!
    }

    public ExportImportPanel(ExportImportContext context) {
        if (context == null) {
            throw new NullPointerException("context == null");
        }

        this.context = context;
        initComponents();
        postInitComponents();
        addObjects();
    }

    private void postInitComponents() {
        MnemonicUtil.setMnemonics((Container) this);
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);
        String lastDirString = storage == null ? "" : storage.getString(KEY_LAST_DIR);
        dir = new File(lastDirString);

        if (dir.isDirectory()) {
            setDirLabel();
        }

        setInfoLabel();
        panelSelectObjects.addSelectionListener(this);
    }

    private void setInfoLabel() {
        labelSelectInfo.setText(isExport()
                                ? Bundle.getString(ExportImportPanel.class, "ExportImportPanel.LabelSelectInfo.Text.Export")
                                : Bundle.getString(ExportImportPanel.class, "ExportImportPanel.LabelSelectInfo.Text.Import"));
    }

    public void setContext(ExportImportContext context) {
        if (context == null) {
            throw new NullPointerException("context == null");
        }

        this.context = context;
        setInfoLabel();
        addObjects();
    }

    private void addObjects() {
        if (isExport()) {
            setExportCheckBoxes();
        } else {
            setImportCheckBoxes();
        }

        panelSelectObjects.setStorageKey(isExport()
                                         ? KEY_SEL_INDICES_EXPORT
                                         : KEY_SEL_INDICES_IMPORT);
        panelSelectObjects.applyPropertiesSelectedIndices();
        buttonExportImport.setText(isExport()
                                   ? TEXT_EXPORT
                                   : TEXT_IMPORT);
        MnemonicUtil.setMnemonics((Container) this);
        setEnabledButtons();
    }

    private boolean isExport() {
        return context.equals(ExportImportContext.EXPORT);
    }

    private void setExportCheckBoxes() {
        List<RepositoryDataExporter> exporters = getJptExporters();

        panelSelectObjects.removeAll();
        panelSelectObjects.setObjectCount(exporters.size());

        for (RepositoryDataExporter exporter : exporters) {
            panelSelectObjects.add(exporter, exporter.getDisplayName());
        }
    }

    private List<RepositoryDataExporter> getJptExporters() {
        Collection<? extends RepositoryDataExporter> allExporters = Lookup.getDefault().lookupAll(RepositoryDataExporter.class);
        List<RepositoryDataExporter> jptExporters = new ArrayList<RepositoryDataExporter>(allExporters.size());

        for (RepositoryDataExporter exporter : allExporters) {
            if (exporter.isJPhotoTaggerData()) {
                jptExporters.add(exporter);
            }
        }

        Collections.sort(jptExporters, PositionComparatorAscendingOrder.INSTANCE);

        return jptExporters;
    }

    private void setImportCheckBoxes() {
        List<RepositoryDataImporter> importers = getJptImporters();

        panelSelectObjects.removeAll();
        panelSelectObjects.setObjectCount(importers.size());

        for (RepositoryDataImporter importer : importers) {
            panelSelectObjects.add(importer, importer.getDisplayName());
        }
    }

    private List<RepositoryDataImporter> getJptImporters() {
        Collection<? extends RepositoryDataImporter> allImporters = Lookup.getDefault().lookupAll(RepositoryDataImporter.class);
        List<RepositoryDataImporter> jptImporters = new ArrayList<RepositoryDataImporter>(allImporters.size());

        for (RepositoryDataImporter importer : allImporters) {
            if (importer.isJPhotoTaggerData()) {
                jptImporters.add(importer);
            }
        }

        Collections.sort(jptImporters, PositionComparatorAscendingOrder.INSTANCE);

        return jptImporters;
    }

    private void selectDirectory() {
        Option showHiddenDirs = getDirChooserOptionShowHiddenDirs();
        DirectoryChooser dlg = new DirectoryChooser(GUI.getAppFrame(), new File(""), showHiddenDirs);

        dlg.setStorageKey("ExportImportPanel.DirChooser");
        dlg.setVisible(true);

        if (dlg.isAccepted()) {
            dir = dlg.getSelectedDirectories().get(0);
            Preferences storage = Lookup.getDefault().lookup(Preferences.class);
            storage.setString(KEY_LAST_DIR, dir.getAbsolutePath());
            setDirLabel();
            setEnabledButtons();
        }
    }

    private DirectoryChooser.Option getDirChooserOptionShowHiddenDirs() {
        return isAcceptHiddenDirectories()
                ? DirectoryChooser.Option.DISPLAY_HIDDEN_DIRECTORIES
                : DirectoryChooser.Option.NO_OPTION;
    }

    private boolean isAcceptHiddenDirectories() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        return storage.containsKey(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES)
                ? storage.getBoolean(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES)
                : false;
    }

    private void setDirLabel() {
        labelDir.setText(dir.getAbsolutePath());
        labelDir.setIcon(IconUtil.getSystemIcon(dir));
    }

    private void exportImport() {
        if (context.equals(ExportImportContext.EXPORT)) {
            exportFiles();
        } else {
            importFiles();
        }

        notifyExportImportListeners();
    }

    private void exportFiles() {
        List<Object> selectedObjects = panelSelectObjects.getSelectedObjects();
        List<File> exportedFiles  = new ArrayList<File>(selectedObjects.size());

        for (Object o : selectedObjects) {
            if (o instanceof RepositoryDataExporter) {
                RepositoryDataExporter exporter = (RepositoryDataExporter) o;
                File exportFile = new File(dir.getAbsolutePath() + File.separator + exporter.getDefaultFilename());
                logExport(exporter, exportFile);
                exporter.exportFile(exportFile);
                exportedFiles.add(exportFile);
            }
        }

        displayFiles(Bundle.getString(ExportImportPanel.class, "ExportImportPanel.Info.ExportedFiles"), exportedFiles);
    }

    private void displayFiles(String shortMessage, Collection<? extends File> files) {
        if (files.size() > 0) {
            LongMessageDialog dialog = new LongMessageDialog(ComponentUtil.findFrameWithIcon(), true);
            String filesString = CollectionUtil.toTokenString(files, "\n", "");

            dialog.setShortMessage(shortMessage);
            dialog.setLongMessage(filesString);
            dialog.setVisible(true);
        }
    }

    private void logExport(RepositoryDataExporter exporter, File exportFile) {
        String exporterDisplayName = exporter.getDisplayName();

        LOGGER.log(Level.INFO, "{0}: Exporting File ''{1}''", new Object[]{exporterDisplayName, exportFile});
    }

    private void importFiles() {
        List<File> importedFiles = new ArrayList<File>();
        List<File> missingFiles = new ArrayList<File>();
        List<Object> selectedObjects = panelSelectObjects.getSelectedObjects();

        for (Object o : selectedObjects) {
            if (o instanceof RepositoryDataImporter) {
                RepositoryDataImporter importer = (RepositoryDataImporter) o;
                File importFile = new File(dir.getAbsolutePath() + File.separator + importer.getDefaultFilename());

                if (importFile.exists()) {
                    logImport(importer, importFile);
                    importer.importFile(importFile);
                    importedFiles.add(importFile);
                } else {
                    logImportErrorFileDoesNotExist(importer, importFile);
                    missingFiles.add(importFile);
                }
            }
        }

        displayFiles(Bundle.getString(ExportImportPanel.class, "ExportImportPanel.Info.ImportedFiles"), importedFiles);
    }

    private void logImport(RepositoryDataImporter importer, File importFile) {
        String importerDisplayName = importer.getDisplayName();

        LOGGER.log(Level.INFO, "{0}: Importing File ''{1}''", new Object[]{importerDisplayName, importFile});
    }

    private void logImportErrorFileDoesNotExist(RepositoryDataImporter importer, File importFile) {
        String importerDisplayName = importer.getDisplayName();

        LOGGER.log(Level.INFO, "{0}: The following file can''t be imported, because it does not exist: ''{1}''",
                new Object[]{importerDisplayName, importFile});
    }

    private void setEnabledButtons() {
        int selCount = panelSelectObjects.getSelectionCount();
        int objectCount = panelSelectObjects.getObjectCount();

        buttonExportImport.setEnabled((dir != null) && dir.isDirectory() && (selCount > 0));
        buttonSelectAll.setEnabled((objectCount > 0) && (objectCount > selCount));
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
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        ls.add(listener);
    }

    public void removeListener(ExportImportListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        ls.remove(listener);
    }

    private void notifyExportImportListeners() {
        for (ExportImportListener listener : ls.get()) {
            listener.done();
        }
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    private void initComponents() {//GEN-BEGIN:initComponents

        labelPromptDir = new javax.swing.JLabel();
        labelDir = new javax.swing.JLabel();
        buttonSelDir = new javax.swing.JButton();
        labelSelectInfo = new javax.swing.JLabel();
        buttonSelectAll = new javax.swing.JButton();
        buttonSelectNone = new javax.swing.JButton();
        scrollPane = new javax.swing.JScrollPane();
        panelSelectObjects = new org.jphototagger.lib.swing.SelectObjectsPanel();
        buttonExportImport = new javax.swing.JButton();

        setName("Form"); // NOI18N

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/module/exportimport/Bundle"); // NOI18N
        labelPromptDir.setText(bundle.getString("ExportImportPanel.labelPromptDir.text")); // NOI18N
        labelPromptDir.setName("labelPromptDir"); // NOI18N

        labelDir.setName("labelDir"); // NOI18N

        buttonSelDir.setText(bundle.getString("ExportImportPanel.buttonSelDir.text")); // NOI18N
        buttonSelDir.setName("buttonSelDir"); // NOI18N
        buttonSelDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSelDirActionPerformed(evt);
            }
        });

        labelSelectInfo.setText("Auswahl:"); // NOI18N
        labelSelectInfo.setName("labelSelectInfo"); // NOI18N

        buttonSelectAll.setText(bundle.getString("ExportImportPanel.buttonSelectAll.text")); // NOI18N
        buttonSelectAll.setEnabled(false);
        buttonSelectAll.setName("buttonSelectAll"); // NOI18N
        buttonSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSelectAllActionPerformed(evt);
            }
        });

        buttonSelectNone.setText(bundle.getString("ExportImportPanel.buttonSelectNone.text")); // NOI18N
        buttonSelectNone.setEnabled(false);
        buttonSelectNone.setName("buttonSelectNone"); // NOI18N
        buttonSelectNone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSelectNoneActionPerformed(evt);
            }
        });

        scrollPane.setName("scrollPane"); // NOI18N

        panelSelectObjects.setName("panelSelectObjects"); // NOI18N
        scrollPane.setViewportView(panelSelectObjects);

        buttonExportImport.setText(TEXT_EXPORT);
        buttonExportImport.setEnabled(false);
        buttonExportImport.setName("buttonExportImport"); // NOI18N
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 114, Short.MAX_VALUE)
                .addComponent(buttonExportImport))
            .addGroup(layout.createSequentialGroup()
                .addComponent(labelPromptDir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelDir, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonSelDir))
            .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE)
            .addComponent(labelSelectInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE)
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

    }//GEN-END:initComponents

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
    private org.jphototagger.lib.swing.SelectObjectsPanel panelSelectObjects;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables
}
