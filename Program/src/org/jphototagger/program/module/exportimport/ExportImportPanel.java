package org.jphototagger.program.module.exportimport;

import java.awt.Container;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.event.listener.ListenerSupport;
import org.jphototagger.domain.repository.RepositoryDataExporter;
import org.jphototagger.domain.repository.RepositoryDataImporter;
import org.jphototagger.lib.api.LayerUtil;
import org.jphototagger.lib.api.PositionProviderAscendingComparator;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.swing.DirectoryChooser;
import org.jphototagger.lib.swing.DirectoryChooser.Option;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.swing.LongMessageDialog;
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.lib.swing.SelectObjectsPanel;
import org.jphototagger.lib.swing.SelectObjectsPanel.SelectionEvent;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.CollectionUtil;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public class ExportImportPanel extends PanelExt implements SelectObjectsPanel.SelectionListener {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ExportImportPanel.class.getName());
    private static final String TEXT_EXPORT = Bundle.getString(ExportImportPanel.class, "ExportImportPanel.Button.DisplayName.Export");
    private static final String TEXT_IMPORT = Bundle.getString(ExportImportPanel.class, "ExportImportPanel.Button.DisplayName.Import");
    private static final String KEY_SEL_INDICES_EXPORT = "ExportImportPanel.Export.SelIndices";
    private static final String KEY_SEL_INDICES_IMPORT = "ExportImportPanel.Import.SelIndices";
    private static final String KEY_LAST_DIR = "ExportImportPanel.LastDirectory";
    private boolean working;
    private ExportImportContext context = ExportImportContext.EXPORT;
    private File dir;
    private final transient ListenerSupport<ExportImportListener> ls = new ListenerSupport<>();

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
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        String lastDirString = prefs == null ? "" : prefs.getString(KEY_LAST_DIR);
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

        panelSelectObjects.setPreferencesKeyForSelectedIndices(isExport()
                ? KEY_SEL_INDICES_EXPORT
                : KEY_SEL_INDICES_IMPORT);
        panelSelectObjects.restoreSelectedIndices();
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
        List<RepositoryDataExporter> jptExporters = new ArrayList<>(allExporters.size());
        for (RepositoryDataExporter exporter : allExporters) {
            if (exporter.isJPhotoTaggerData()) {
                jptExporters.add(exporter);
            }
        }
        Collections.sort(jptExporters, PositionProviderAscendingComparator.INSTANCE);
        LayerUtil.logWarningIfNotUniquePositions(jptExporters);

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
        List<RepositoryDataImporter> jptImporters = new ArrayList<>(allImporters.size());
        for (RepositoryDataImporter importer : allImporters) {
            if (importer.isJPhotoTaggerData()) {
                jptImporters.add(importer);
            }
        }
        Collections.sort(jptImporters, PositionProviderAscendingComparator.INSTANCE);
        LayerUtil.logWarningIfNotUniquePositions(jptImporters);
        return jptImporters;
    }

    private void selectDirectory() {
        Option showHiddenDirs = getDirChooserOptionShowHiddenDirs();
        File lastDir = getLastDirForChooser();
        DirectoryChooser dlg = new DirectoryChooser(ComponentUtil.findParentDialog(this), lastDir, showHiddenDirs);
        dlg.setPreferencesKey("ExportImportPanel.DirChooser");
        dlg.setVisible(true);
        ComponentUtil.parentWindowToFront(this);
        if (dlg.isAccepted()) {
            dir = dlg.getSelectedDirectories().get(0);
            Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
            prefs.setString(KEY_LAST_DIR, dir.getAbsolutePath());
            setDirLabel();
            setEnabledButtons();
        }
    }

    private File getLastDirForChooser() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        if (prefs.containsKey(KEY_LAST_DIR)) {
            File lastDir = new File(prefs.getString(KEY_LAST_DIR));
            if (lastDir.isDirectory()) {
                return lastDir;
            }
        }
        return new File("");
    }

    private DirectoryChooser.Option getDirChooserOptionShowHiddenDirs() {
        return isAcceptHiddenDirectories()
                ? DirectoryChooser.Option.DISPLAY_HIDDEN_DIRECTORIES
                : DirectoryChooser.Option.NO_OPTION;
    }

    private boolean isAcceptHiddenDirectories() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        return prefs.containsKey(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES)
                ? prefs.getBoolean(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES)
                : false;
    }

    private void setDirLabel() {
        labelDir.setText(FileUtil.toStringWithMaximumLength(dir, 45));
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
        List<File> exportedFiles = new ArrayList<>(selectedObjects.size());
        for (Object o : selectedObjects) {
            if (o instanceof RepositoryDataExporter) {
                RepositoryDataExporter exporter = (RepositoryDataExporter) o;
                File exportFile = new File(dir.getAbsolutePath() + File.separator + exporter.getDefaultFilename());
                logExport(exporter, exportFile);
                exporter.exportToFile(exportFile);
                exportedFiles.add(exportFile);
            }
        }
        displayFiles(Bundle.getString(ExportImportPanel.class, "ExportImportPanel.Info.ExportedFiles"), exportedFiles);
    }

    private void displayFiles(String shortMessage, Collection<? extends File> files) {
        if (files.size() > 0) {
            LongMessageDialog dialog = new LongMessageDialog(ComponentUtil.findFrameWithIcon(), true);
            String filesString = CollectionUtil.toTokenString(files, "\n", "");
            dialog.setTitle(Bundle.getString(ExportImportPanel.class, "ExportImportPanel.DisplayFiles.DialogTitle"));
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
        if (!working) {
            ImportWorker worker = new ImportWorker(panelSelectObjects.getSelectedObjects());
            worker.execute();
        }
    }

    private final class ImportWorker extends SwingWorker<Collection<File>, Void> {

        private final List<Object> selectedObjects;

        private ImportWorker(List<Object> selectedObjects) {
            this.selectedObjects = selectedObjects;
        }

        @Override
        protected Collection<File> doInBackground() throws Exception {
            Collection<File> importedFiles = new ArrayList<>();
            for (Object o : selectedObjects) {
                if (o instanceof RepositoryDataImporter) {
                    RepositoryDataImporter importer = (RepositoryDataImporter) o;
                    File importFile = new File(dir.getAbsolutePath() + File.separator + importer.getDefaultFilename());
                    if (importFile.exists()) {
                        logImport(importer, importFile);
                        importer.importFromFile(importFile);
                        importedFiles.add(importFile);
                    } else {
                        logImportErrorFileDoesNotExist(importer, importFile);
                    }
                }
            }
            return importedFiles;
        }

        @Override
        protected void done() {
            try {
                Collection<File> importedFiles = get();
                displayFiles(Bundle.getString(ExportImportPanel.class, "ExportImportPanel.Info.ImportedFiles"), importedFiles);
            } catch (Exception ex) {
                Logger.getLogger(ImportWorker.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                working = false;
            }
        }
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

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panelDirectory = UiFactory.panel();
        labelPromptDir = UiFactory.label();
        labelDir = UiFactory.label();
        buttonSelDir = UiFactory.button();
        labelSelectInfo = UiFactory.label();
        scrollPane = UiFactory.scrollPane();
        panelSelectObjects = new org.jphototagger.lib.swing.SelectObjectsPanel();
        panelButtons = UiFactory.panel();
        buttonSelectAll = UiFactory.button();
        buttonSelectNone = UiFactory.button();
        buttonExportImport = UiFactory.button();

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        panelDirectory.setLayout(new java.awt.GridBagLayout());

        labelPromptDir.setText(Bundle.getString(getClass(), "ExportImportPanel.labelPromptDir.text")); // NOI18N
        labelPromptDir.setName("labelPromptDir"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelDirectory.add(labelPromptDir, gridBagConstraints);

        labelDir.setName("labelDir"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelDirectory.add(labelDir, gridBagConstraints);

        buttonSelDir.setText(Bundle.getString(getClass(), "ExportImportPanel.buttonSelDir.text")); // NOI18N
        buttonSelDir.setName("buttonSelDir"); // NOI18N
        buttonSelDir.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSelDirActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelDirectory.add(buttonSelDir, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(panelDirectory, gridBagConstraints);

        labelSelectInfo.setText("Auswahl:"); // NOI18N
        labelSelectInfo.setName("labelSelectInfo"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        add(labelSelectInfo, gridBagConstraints);

        scrollPane.setName("scrollPane"); // NOI18N

        panelSelectObjects.setName("panelSelectObjects"); // NOI18N
        scrollPane.setViewportView(panelSelectObjects);
        scrollPane.setPreferredSize(new Dimension(200, 200));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        add(scrollPane, gridBagConstraints);

        panelButtons.setLayout(new java.awt.GridBagLayout());

        buttonSelectAll.setText(Bundle.getString(getClass(), "ExportImportPanel.buttonSelectAll.text")); // NOI18N
        buttonSelectAll.setEnabled(false);
        buttonSelectAll.setName("buttonSelectAll"); // NOI18N
        buttonSelectAll.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSelectAllActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelButtons.add(buttonSelectAll, gridBagConstraints);

        buttonSelectNone.setText(Bundle.getString(getClass(), "ExportImportPanel.buttonSelectNone.text")); // NOI18N
        buttonSelectNone.setEnabled(false);
        buttonSelectNone.setName("buttonSelectNone"); // NOI18N
        buttonSelectNone.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSelectNoneActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelButtons.add(buttonSelectNone, gridBagConstraints);

        buttonExportImport.setText(TEXT_EXPORT);
        buttonExportImport.setEnabled(false);
        buttonExportImport.setName("buttonExportImport"); // NOI18N
        buttonExportImport.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonExportImportActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelButtons.add(buttonExportImport, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        add(panelButtons, gridBagConstraints);
    }

    private void buttonSelDirActionPerformed(java.awt.event.ActionEvent evt) {
        selectDirectory();
    }

    private void buttonExportImportActionPerformed(java.awt.event.ActionEvent evt) {
        exportImport();
    }

    private void buttonSelectAllActionPerformed(java.awt.event.ActionEvent evt) {
        setSelectedAll(true);
    }

    private void buttonSelectNoneActionPerformed(java.awt.event.ActionEvent evt) {
        setSelectedAll(false);
    }

    private javax.swing.JButton buttonExportImport;
    private javax.swing.JButton buttonSelDir;
    private javax.swing.JButton buttonSelectAll;
    private javax.swing.JButton buttonSelectNone;
    private javax.swing.JLabel labelDir;
    private javax.swing.JLabel labelPromptDir;
    private javax.swing.JLabel labelSelectInfo;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelDirectory;
    private org.jphototagger.lib.swing.SelectObjectsPanel panelSelectObjects;
    private javax.swing.JScrollPane scrollPane;
}
