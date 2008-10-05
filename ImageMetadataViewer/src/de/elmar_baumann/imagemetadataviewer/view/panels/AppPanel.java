package de.elmar_baumann.imagemetadataviewer.view.panels;

import de.elmar_baumann.lib.thirdparty.neil_cochrane.CompleterTextField;
import de.elmar_baumann.imagemetadataviewer.AppSettings;
import de.elmar_baumann.lib.renderer.TreeCellRendererDirectories;
import de.elmar_baumann.imagemetadataviewer.UserSettings;
import de.elmar_baumann.imagemetadataviewer.data.AutoCompleteData;
import de.elmar_baumann.imagemetadataviewer.model.ComboBoxModelMetaDataEditTemplates;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;
import de.elmar_baumann.imagemetadataviewer.view.renderer.ListCellRendererCategories;
import de.elmar_baumann.imagemetadataviewer.view.renderer.ListCellRendererFavoriteDirectories;
import de.elmar_baumann.lib.persistence.PersistentSettings;
import de.elmar_baumann.lib.persistence.PersistentSettingsHints;
import java.awt.Container;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.tree.TreeSelectionModel;

/**
 * Panel der Anwendung.
 * 
 * @author Elmar Baumann <eb@elmar-baumann.de>
 * @version 1.0 2008/02/17
 */
public class AppPanel extends javax.swing.JPanel {

    private static final String keyThumbnailPanelViewportViewPosition = "de.elmar_baumann.imagemetadataviewer.view.panels.AppPanel.scrollPaneThumbnailsPanel"; // NOI18N
    private ArrayList<JTable> xmpTables = new ArrayList<JTable>();
    private ArrayList<JTable> metaDataTables = new ArrayList<JTable>();
    private ArrayList<JTree> selectionTrees = new ArrayList<JTree>();
    private ArrayList<JList> selectionLists = new ArrayList<JList>();
    private MetaDataEditPanelsArray editPanelsArray;

    public AppPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        editPanelsArray = new MetaDataEditPanelsArray(panelEditMetadata);
        panelImageFileThumbnails.setViewport(scrollPaneThumbnailsPanel.getViewport());
        setBackgroundColorTablesScrollPanes();
        disableTreeMultipleSelection();
        initArrays();
    }

    private void initArrays() {
        initTableArrays();
        initSelectionTreeArray();
        initSelectionListArray();
    }

    public JProgressBar getProgressBarCreateMetaDataOfCurrentThumbnails() {
        return progressBarCreateMetaDataOfCurrentThumbnails;
    }

    /**
     * Liefert die Progressbar für die aktuellen Tasks.
     * <em>Auf keinen Fall von hier abholen, sondern von
     * {@link de.elmar_baumann.imagemetadataviewer.resource.ProgressBarCurrentTasks}!
     * </em>
     * 
     * @return  Progressbar
     */
    public JProgressBar getProgressBarCurrentTasks() {
        return progressBarCurrentTasks;
    }

    public JProgressBar getProgressBarScheduledTasks() {
        return progressBarScheduledTasks;
    }

    public ArrayList<JTree> getSelectionTrees() {
        return selectionTrees;
    }

    public ArrayList<JList> getSelectionLists() {
        return selectionLists;
    }

    public ImageFileThumbnailsPanel getPanelImageFileThumbnails() {
        return panelImageFileThumbnails;
    }

    public JTree getTreeImageCollections() {
        return treeImageCollections;
    }

    public JTree getTreeSavedSearches() {
        return treeSavedSearches;
    }

    public JTree getTreeDirectories() {
        return treeDirectories;
    }

    public JList getListFavoriteDirectories() {
        return listFavoriteDirectories;
    }

    public JList getListCategories() {
        return listCategories;
    }

    public JButton getButtonLogfileDialog() {
        return buttonLogfileDialog;
    }

    public JButton getButtonAdvanedSearch() {
        return buttonAdvanedSearch;
    }

    public JButton getButtonSaveMetadata() {
        return buttonSaveMetadata;
    }

    public JButton getButtonMetaDataTemplateCreate() {
        return buttonMetaDataTemplateCreate;
    }

    public JButton getButtonMetaDataTemplateUpdate() {
        return buttonMetaDataTemplateUpdate;
    }

    public JButton getButtonMetaDataTemplateRename() {
        return buttonMetaDataTemplateRename;
    }

    public JButton getButtonMetaDataTemplateInsert() {
        return buttonMetaDataTemplateInsert;
    }

    public JButton getButtonMetaDataTemplateDelete() {
        return buttonMetaDataTemplateDelete;
    }

    public JButton getButtonStopScheduledTasks() {
        return buttonStopScheduledTasks;
    }

    public JComboBox getComboBoxMetaDataTemplates() {
        return comboBoxMetaDataTemplates;
    }

    public JLabel getLabelMetadataInfoEditable() {
        return labelMetadataInfoEditable;
    }

    public JLabel getLabelStatusbar() {
        return labelStatusbar;
    }

    public JLabel getLabelMetadataFilename() {
        return labelMetadataFilename;
    }

    public JTextField getTextFieldSearch() {
        return textFieldSearch;
    }

    public MetaDataEditPanelsArray getEditPanelsArray() {
        return editPanelsArray;
    }

    public ArrayList<JTable> getMetaDataTables() {
        return metaDataTables;
    }

    public ArrayList<JTable> getXmpTables() {
        return xmpTables;
    }

    public JTable getTableIptc() {
        return tableIptc;
    }

    public JTable getTableExif() {
        return tableExif;
    }

    public JTable getTableXmpCameraRawSettings() {
        return tableXmpCameraRawSettings;
    }

    public JTable getTableXmpDc() {
        return tableXmpDc;
    }

    public JTable getTableXmpExif() {
        return tableXmpExif;
    }

    public JTable getTableXmpIptc() {
        return tableXmpIptc;
    }

    public JTable getTableXmpLightroom() {
        return tableXmpLightroom;
    }

    public JTable getTableXmpPhotoshop() {
        return tableXmpPhotoshop;
    }

    public JTable getTableXmpTiff() {
        return tableXmpTiff;
    }

    public JTable getTableXmpXap() {
        return tableXmpXap;
    }

    private void initTableArrays() {
        initXmpTableArray();
        initMetaDataTablesArray();
    }

    private void initMetaDataTablesArray() {
        metaDataTables.addAll(xmpTables);
        metaDataTables.add(tableExif);
        metaDataTables.add(tableIptc);
    }

    private void initXmpTableArray() {
        xmpTables.add(tableXmpCameraRawSettings);
        xmpTables.add(tableXmpDc);
        xmpTables.add(tableXmpExif);
        xmpTables.add(tableXmpIptc);
        xmpTables.add(tableXmpLightroom);
        xmpTables.add(tableXmpPhotoshop);
        xmpTables.add(tableXmpTiff);
        xmpTables.add(tableXmpXap);
    }

    private void initSelectionTreeArray() {
        selectionTrees.add(treeDirectories);
        selectionTrees.add(treeImageCollections);
        selectionTrees.add(treeSavedSearches);
    }

    private void initSelectionListArray() {
        selectionLists.add(listCategories);
        selectionLists.add(listFavoriteDirectories);
    }

    private void setBackgroundColorTablesScrollPanes() {
        for (JTable table : metaDataTables) {
            Container container = table.getParent();
            if (container instanceof JViewport) {
                JViewport viewport = (JViewport) container;
                viewport.setBackground(table.getBackground());
            }
        }
    }

    /**
     * Benachrichtigen bevor das Panel benutzt wird.
     */
    public void beforeStart() {
        readPersistent();
    }

    private void readPersistent() {
        PersistentSettings.getInstance().getComponent(this, getPersistentSettingsHints());
        readPersistentViewportViewPosition();
    }

    private PersistentSettingsHints getPersistentSettingsHints() {
        PersistentSettingsHints hints = new PersistentSettingsHints();
        String className = getClass().getName();
        hints.addExcludedMember(className + ".textFieldSearch"); // NOI18N
        hints.addExcludedMember(className + ".panelEditMetadata"); // NOI18N
        if (!UserSettings.getInstance().isExpandDirectoriesTree()) {
            hints.addExcludedMember(className + ".treeDirectories"); // NOI18N
        }
        return hints;
    }

    private void writePersistent() {
        PersistentSettings.getInstance().setComponent(this, getPersistentSettingsHints());
        writePersistentViewportViewPosition();
    }

    private void readPersistentViewportViewPosition() {
        PersistentSettings.getInstance().getScrollPane(
            scrollPaneThumbnailsPanel,
            keyThumbnailPanelViewportViewPosition);
    }

    private void writePersistentViewportViewPosition() {
        PersistentSettings.getInstance().setScrollPane(
            scrollPaneThumbnailsPanel,
            keyThumbnailPanelViewportViewPosition);
    }

    /**
     * Benachrichtigen bevor die Anwendung geschlossen wird.
     */
    public void beforeQuit() {
        panelImageFileThumbnails.beforeQuit();
        writePersistent();
    }

    private void disableTreeMultipleSelection() {
        int mode = TreeSelectionModel.SINGLE_TREE_SELECTION;
        treeDirectories.getSelectionModel().setSelectionMode(mode);
        treeSavedSearches.getSelectionModel().setSelectionMode(mode);
        treeImageCollections.getSelectionModel().setSelectionMode(mode);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        splitPaneMain = new javax.swing.JSplitPane();
        panelSelection = new javax.swing.JPanel();
        tabbedPaneSelection = new javax.swing.JTabbedPane();
        panelSelectionDirectories = new javax.swing.JPanel();
        scrollPaneTreeDirectories = new javax.swing.JScrollPane();
        treeDirectories = new javax.swing.JTree();
        panelSelectionSavedSearches = new javax.swing.JPanel();
        scrollPaneTreeSavedSearches = new javax.swing.JScrollPane();
        treeSavedSearches = new javax.swing.JTree();
        panelImageCollections = new javax.swing.JPanel();
        scrollPaneTreeImageCollections = new javax.swing.JScrollPane();
        treeImageCollections = new javax.swing.JTree();
        panelCategories = new javax.swing.JPanel();
        scrollPaneListCategories = new javax.swing.JScrollPane();
        listCategories = new javax.swing.JList();
        panelFavoriteDirectories = new javax.swing.JPanel();
        scrollPaneListFavoriteDirectories = new javax.swing.JScrollPane();
        listFavoriteDirectories = new javax.swing.JList();
        panelThumbnailsMetadata = new javax.swing.JPanel();
        splitPaneThumbnailsMetadata = new javax.swing.JSplitPane();
        panelThumbnails = new javax.swing.JPanel();
        scrollPaneThumbnailsPanel = new javax.swing.JScrollPane();
        panelImageFileThumbnails = new de.elmar_baumann.imagemetadataviewer.view.panels.ImageFileThumbnailsPanel();
        panelMetadata = new javax.swing.JPanel();
        labelMetadataFilename = new javax.swing.JLabel();
        labelColorBackgroundTableTextStoredInDatabase = new javax.swing.JLabel();
        labelLegendColorBackgroundTableTetStoredInDatabase = new javax.swing.JLabel();
        tabbedPaneMetaDataTabs = new javax.swing.JTabbedPane();
        scrollPaneTableIptc = new javax.swing.JScrollPane();
        tableIptc = new javax.swing.JTable();
        scrollPaneTableExif = new javax.swing.JScrollPane();
        tableExif = new javax.swing.JTable();
        tabbedPaneXmp = new javax.swing.JTabbedPane();
        scrollPaneTableXmpTiff = new javax.swing.JScrollPane();
        tableXmpTiff = new javax.swing.JTable();
        scrollPaneTableXmpExif = new javax.swing.JScrollPane();
        tableXmpExif = new javax.swing.JTable();
        scrollPaneTableXmpDc = new javax.swing.JScrollPane();
        tableXmpDc = new javax.swing.JTable();
        scrollPaneTableXmpIptc = new javax.swing.JScrollPane();
        tableXmpIptc = new javax.swing.JTable();
        scrollPaneTableXmpPhotoshop = new javax.swing.JScrollPane();
        tableXmpPhotoshop = new javax.swing.JTable();
        scrollPaneTableXmpXap = new javax.swing.JScrollPane();
        tableXmpXap = new javax.swing.JTable();
        scrollPaneTableXmpLightroom = new javax.swing.JScrollPane();
        tableXmpLightroom = new javax.swing.JTable();
        scrollPaneTableXmpCameraRawSettings = new javax.swing.JScrollPane();
        tableXmpCameraRawSettings = new javax.swing.JTable();
        panelTabEditMetaData = new javax.swing.JPanel();
        panelScrollPaneEditMetaData = new javax.swing.JPanel();
        scrollPaneEditMetadata = new javax.swing.JScrollPane();
        panelEditMetadata = new javax.swing.JPanel();
        labelEditTabInfoArrayDelimiter = new javax.swing.JLabel();
        panelGroupMetadataEdit = new javax.swing.JPanel();
        labelMetadataInfoEditable = new javax.swing.JLabel();
        buttonSaveMetadata = new javax.swing.JButton();
        buttonMetaDataTemplateCreate = new javax.swing.JButton();
        panelGroupMetadataTemplates = new javax.swing.JPanel();
        comboBoxMetaDataTemplates = new javax.swing.JComboBox();
        buttonMetaDataTemplateRename = new javax.swing.JButton();
        buttonMetaDataTemplateDelete = new javax.swing.JButton();
        buttonMetaDataTemplateInsert = new javax.swing.JButton();
        buttonMetaDataTemplateUpdate = new javax.swing.JButton();
        panelMetadataProgress = new javax.swing.JPanel();
        progressBarScheduledTasks = new javax.swing.JProgressBar();
        progressBarCurrentTasks = new javax.swing.JProgressBar();
        buttonStopScheduledTasks = new javax.swing.JButton();
        panelStatusbar = new javax.swing.JPanel();
        labelStatusbar = new javax.swing.JLabel();
        textFieldSearch = UserSettings.getInstance().isUseAutocomplete() ? new CompleterTextField(new AutoCompleteData().toArray(), false) : new JTextField();
        progressBarCreateMetaDataOfCurrentThumbnails = new javax.swing.JProgressBar();
        buttonLogfileDialog = new javax.swing.JButton();
        buttonAdvanedSearch = new javax.swing.JButton();

        panelSelection.setMinimumSize(new java.awt.Dimension(150, 250));

        treeDirectories.setCellRenderer(new TreeCellRendererDirectories());
        treeDirectories.setName("treeDirectories"); // NOI18N
        scrollPaneTreeDirectories.setViewportView(treeDirectories);

        javax.swing.GroupLayout panelSelectionDirectoriesLayout = new javax.swing.GroupLayout(panelSelectionDirectories);
        panelSelectionDirectories.setLayout(panelSelectionDirectoriesLayout);
        panelSelectionDirectoriesLayout.setHorizontalGroup(
            panelSelectionDirectoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneTreeDirectories, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
        );
        panelSelectionDirectoriesLayout.setVerticalGroup(
            panelSelectionDirectoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneTreeDirectories, javax.swing.GroupLayout.DEFAULT_SIZE, 603, Short.MAX_VALUE)
        );

        tabbedPaneSelection.addTab(Bundle.getString("AppPanel.panelSelectionDirectories.TabConstraints.tabTitle"), panelSelectionDirectories); // NOI18N

        treeSavedSearches.setCellRenderer(new de.elmar_baumann.imagemetadataviewer.view.renderer.TreeCellRendererSavedSearches());
        treeSavedSearches.setName("treeSavedSearches"); // NOI18N
        scrollPaneTreeSavedSearches.setViewportView(treeSavedSearches);

        javax.swing.GroupLayout panelSelectionSavedSearchesLayout = new javax.swing.GroupLayout(panelSelectionSavedSearches);
        panelSelectionSavedSearches.setLayout(panelSelectionSavedSearchesLayout);
        panelSelectionSavedSearchesLayout.setHorizontalGroup(
            panelSelectionSavedSearchesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 145, Short.MAX_VALUE)
            .addGroup(panelSelectionSavedSearchesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrollPaneTreeSavedSearches, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE))
        );
        panelSelectionSavedSearchesLayout.setVerticalGroup(
            panelSelectionSavedSearchesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 603, Short.MAX_VALUE)
            .addGroup(panelSelectionSavedSearchesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrollPaneTreeSavedSearches, javax.swing.GroupLayout.DEFAULT_SIZE, 603, Short.MAX_VALUE))
        );

        tabbedPaneSelection.addTab(Bundle.getString("AppPanel.panelSelectionSavedSearches.TabConstraints.tabTitle"), panelSelectionSavedSearches); // NOI18N

        treeImageCollections.setCellRenderer(new de.elmar_baumann.imagemetadataviewer.view.renderer.TreeCellRendererCollections());
        treeImageCollections.setName("treeImageCollections"); // NOI18N
        scrollPaneTreeImageCollections.setViewportView(treeImageCollections);

        javax.swing.GroupLayout panelImageCollectionsLayout = new javax.swing.GroupLayout(panelImageCollections);
        panelImageCollections.setLayout(panelImageCollectionsLayout);
        panelImageCollectionsLayout.setHorizontalGroup(
            panelImageCollectionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 145, Short.MAX_VALUE)
            .addGroup(panelImageCollectionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrollPaneTreeImageCollections, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE))
        );
        panelImageCollectionsLayout.setVerticalGroup(
            panelImageCollectionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 603, Short.MAX_VALUE)
            .addGroup(panelImageCollectionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrollPaneTreeImageCollections, javax.swing.GroupLayout.DEFAULT_SIZE, 603, Short.MAX_VALUE))
        );

        tabbedPaneSelection.addTab(Bundle.getString("AppPanel.panelImageCollections.TabConstraints.tabTitle"), panelImageCollections); // NOI18N

        listCategories.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listCategories.setCellRenderer(new ListCellRendererCategories());
        listCategories.setName("listCategories"); // NOI18N
        scrollPaneListCategories.setViewportView(listCategories);

        javax.swing.GroupLayout panelCategoriesLayout = new javax.swing.GroupLayout(panelCategories);
        panelCategories.setLayout(panelCategoriesLayout);
        panelCategoriesLayout.setHorizontalGroup(
            panelCategoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneListCategories, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
        );
        panelCategoriesLayout.setVerticalGroup(
            panelCategoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneListCategories, javax.swing.GroupLayout.DEFAULT_SIZE, 603, Short.MAX_VALUE)
        );

        tabbedPaneSelection.addTab(Bundle.getString("AppPanel.panelCategories.TabConstraints.tabTitle"), panelCategories); // NOI18N

        listFavoriteDirectories.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listFavoriteDirectories.setCellRenderer(new ListCellRendererFavoriteDirectories());
        listFavoriteDirectories.setName("listFavoriteDirectories"); // NOI18N
        scrollPaneListFavoriteDirectories.setViewportView(listFavoriteDirectories);

        javax.swing.GroupLayout panelFavoriteDirectoriesLayout = new javax.swing.GroupLayout(panelFavoriteDirectories);
        panelFavoriteDirectories.setLayout(panelFavoriteDirectoriesLayout);
        panelFavoriteDirectoriesLayout.setHorizontalGroup(
            panelFavoriteDirectoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 145, Short.MAX_VALUE)
            .addGroup(panelFavoriteDirectoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrollPaneListFavoriteDirectories, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE))
        );
        panelFavoriteDirectoriesLayout.setVerticalGroup(
            panelFavoriteDirectoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 603, Short.MAX_VALUE)
            .addGroup(panelFavoriteDirectoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrollPaneListFavoriteDirectories, javax.swing.GroupLayout.DEFAULT_SIZE, 603, Short.MAX_VALUE))
        );

        tabbedPaneSelection.addTab(Bundle.getString("AppPanel.panelFavoriteDirectories.TabConstraints.tabTitle"), panelFavoriteDirectories); // NOI18N

        javax.swing.GroupLayout panelSelectionLayout = new javax.swing.GroupLayout(panelSelection);
        panelSelection.setLayout(panelSelectionLayout);
        panelSelectionLayout.setHorizontalGroup(
            panelSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 150, Short.MAX_VALUE)
            .addGroup(panelSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(tabbedPaneSelection, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
        );
        panelSelectionLayout.setVerticalGroup(
            panelSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 684, Short.MAX_VALUE)
            .addGroup(panelSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(tabbedPaneSelection, javax.swing.GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE))
        );

        splitPaneMain.setLeftComponent(panelSelection);

        splitPaneThumbnailsMetadata.setPreferredSize(new java.awt.Dimension(400, 400));

        javax.swing.GroupLayout panelImageFileThumbnailsLayout = new javax.swing.GroupLayout(panelImageFileThumbnails);
        panelImageFileThumbnails.setLayout(panelImageFileThumbnailsLayout);
        panelImageFileThumbnailsLayout.setHorizontalGroup(
            panelImageFileThumbnailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 599, Short.MAX_VALUE)
        );
        panelImageFileThumbnailsLayout.setVerticalGroup(
            panelImageFileThumbnailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 679, Short.MAX_VALUE)
        );

        scrollPaneThumbnailsPanel.setViewportView(panelImageFileThumbnails);

        javax.swing.GroupLayout panelThumbnailsLayout = new javax.swing.GroupLayout(panelThumbnails);
        panelThumbnails.setLayout(panelThumbnailsLayout);
        panelThumbnailsLayout.setHorizontalGroup(
            panelThumbnailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 602, Short.MAX_VALUE)
            .addGroup(panelThumbnailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrollPaneThumbnailsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 602, Short.MAX_VALUE))
        );
        panelThumbnailsLayout.setVerticalGroup(
            panelThumbnailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 682, Short.MAX_VALUE)
            .addGroup(panelThumbnailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrollPaneThumbnailsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 682, Short.MAX_VALUE))
        );

        splitPaneThumbnailsMetadata.setLeftComponent(panelThumbnails);

        labelMetadataFilename.setBackground(new java.awt.Color(255, 255, 255));
        labelMetadataFilename.setFont(new java.awt.Font("Dialog", 0, 10));
        labelMetadataFilename.setText(Bundle.getString("AppPanel.labelMetadataFilename.text")); // NOI18N
        labelMetadataFilename.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelMetadataFilename.setOpaque(true);

        labelColorBackgroundTableTextStoredInDatabase.setBackground(AppSettings.colorBackgroundTableTextStoredInDatabase);
        labelColorBackgroundTableTextStoredInDatabase.setForeground(AppSettings.colorBackgroundTableTextStoredInDatabase);
        labelColorBackgroundTableTextStoredInDatabase.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(195, 195, 195)));
        labelColorBackgroundTableTextStoredInDatabase.setOpaque(true);
        labelColorBackgroundTableTextStoredInDatabase.setPreferredSize(new java.awt.Dimension(16, 16));

        labelLegendColorBackgroundTableTetStoredInDatabase.setFont(new java.awt.Font("Dialog", 0, 12));
        labelLegendColorBackgroundTableTetStoredInDatabase.setText(Bundle.getString("AppPanel.labelLegendColorBackgroundTableTetStoredInDatabase.text")); // NOI18N

        tabbedPaneMetaDataTabs.setOpaque(true);

        scrollPaneTableIptc.setBackground(new java.awt.Color(255, 255, 255));
        scrollPaneTableIptc.setAutoscrolls(true);
        scrollPaneTableIptc.setDoubleBuffered(true);

        tableIptc.setAutoCreateRowSorter(true);
        tableIptc.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableIptc.setName("tableIptc"); // NOI18N
        tableIptc.setRowSelectionAllowed(false);
        tableIptc.setUpdateSelectionOnSort(false);
        tableIptc.setVerifyInputWhenFocusTarget(false);
        scrollPaneTableIptc.setViewportView(tableIptc);

        tabbedPaneMetaDataTabs.addTab(Bundle.getString("AppPanel.scrollPaneTableIptc.TabConstraints.tabTitle"), scrollPaneTableIptc); // NOI18N

        tableExif.setAutoCreateRowSorter(true);
        tableExif.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableExif.setAutoscrolls(false);
        tableExif.setEnabled(false);
        tableExif.setName("tableExif"); // NOI18N
        tableExif.setUpdateSelectionOnSort(false);
        tableExif.setVerifyInputWhenFocusTarget(false);
        scrollPaneTableExif.setViewportView(tableExif);

        tabbedPaneMetaDataTabs.addTab(Bundle.getString("AppPanel.scrollPaneTableExif.TabConstraints.tabTitle"), scrollPaneTableExif); // NOI18N

        scrollPaneTableXmpTiff.setBackground(new java.awt.Color(255, 255, 255));

        tableXmpTiff.setAutoCreateRowSorter(true);
        tableXmpTiff.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableXmpTiff.setName("tableXmpTiff"); // NOI18N
        scrollPaneTableXmpTiff.setViewportView(tableXmpTiff);

        tabbedPaneXmp.addTab(Bundle.getString("AppPanel.scrollPaneTableXmpTiff.TabConstraints.tabTitle"), scrollPaneTableXmpTiff); // NOI18N

        tableXmpExif.setAutoCreateRowSorter(true);
        tableXmpExif.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableXmpExif.setName("tableXmpExif"); // NOI18N
        scrollPaneTableXmpExif.setViewportView(tableXmpExif);

        tabbedPaneXmp.addTab(Bundle.getString("AppPanel.scrollPaneTableXmpExif.TabConstraints.tabTitle"), scrollPaneTableXmpExif); // NOI18N

        tableXmpDc.setAutoCreateRowSorter(true);
        tableXmpDc.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableXmpDc.setName("tableXmpDc"); // NOI18N
        scrollPaneTableXmpDc.setViewportView(tableXmpDc);

        tabbedPaneXmp.addTab(Bundle.getString("AppPanel.scrollPaneTableXmpDc.TabConstraints.tabTitle"), scrollPaneTableXmpDc); // NOI18N

        tableXmpIptc.setAutoCreateRowSorter(true);
        tableXmpIptc.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableXmpIptc.setName("tableXmpIptc"); // NOI18N
        scrollPaneTableXmpIptc.setViewportView(tableXmpIptc);

        tabbedPaneXmp.addTab(Bundle.getString("AppPanel.scrollPaneTableXmpIptc.TabConstraints.tabTitle"), scrollPaneTableXmpIptc); // NOI18N

        tableXmpPhotoshop.setAutoCreateRowSorter(true);
        tableXmpPhotoshop.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableXmpPhotoshop.setName("tableXmpPhotoshop"); // NOI18N
        scrollPaneTableXmpPhotoshop.setViewportView(tableXmpPhotoshop);

        tabbedPaneXmp.addTab(Bundle.getString("AppPanel.scrollPaneTableXmpPhotoshop.TabConstraints.tabTitle"), scrollPaneTableXmpPhotoshop); // NOI18N

        tableXmpXap.setAutoCreateRowSorter(true);
        tableXmpXap.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableXmpXap.setName("tableXmpXap"); // NOI18N
        scrollPaneTableXmpXap.setViewportView(tableXmpXap);

        tabbedPaneXmp.addTab(Bundle.getString("AppPanel.scrollPaneTableXmpXap.TabConstraints.tabTitle"), scrollPaneTableXmpXap); // NOI18N

        tableXmpLightroom.setAutoCreateRowSorter(true);
        tableXmpLightroom.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableXmpLightroom.setName("tableXmpLightroom"); // NOI18N
        scrollPaneTableXmpLightroom.setViewportView(tableXmpLightroom);

        tabbedPaneXmp.addTab(Bundle.getString("AppPanel.scrollPaneTableXmpLightroom.TabConstraints.tabTitle"), scrollPaneTableXmpLightroom); // NOI18N

        tableXmpCameraRawSettings.setAutoCreateRowSorter(true);
        tableXmpCameraRawSettings.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableXmpCameraRawSettings.setName("tableXmpCameraRawSettings"); // NOI18N
        scrollPaneTableXmpCameraRawSettings.setViewportView(tableXmpCameraRawSettings);

        tabbedPaneXmp.addTab(Bundle.getString("AppPanel.scrollPaneTableXmpCameraRawSettings.TabConstraints.tabTitle"), scrollPaneTableXmpCameraRawSettings); // NOI18N

        tabbedPaneMetaDataTabs.addTab(Bundle.getString("AppPanel.tabbedPaneXmp.TabConstraints.tabTitle"), tabbedPaneXmp); // NOI18N

        javax.swing.GroupLayout panelEditMetadataLayout = new javax.swing.GroupLayout(panelEditMetadata);
        panelEditMetadata.setLayout(panelEditMetadataLayout);
        panelEditMetadataLayout.setHorizontalGroup(
            panelEditMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 923, Short.MAX_VALUE)
        );
        panelEditMetadataLayout.setVerticalGroup(
            panelEditMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 309, Short.MAX_VALUE)
        );

        scrollPaneEditMetadata.setViewportView(panelEditMetadata);

        labelEditTabInfoArrayDelimiter.setFont(new java.awt.Font("Dialog", 0, 12));
        labelEditTabInfoArrayDelimiter.setText(Bundle.getString("AppPanel.labelEditTabInfoArrayDelimiter.text")); // NOI18N

        javax.swing.GroupLayout panelScrollPaneEditMetaDataLayout = new javax.swing.GroupLayout(panelScrollPaneEditMetaData);
        panelScrollPaneEditMetaData.setLayout(panelScrollPaneEditMetaDataLayout);
        panelScrollPaneEditMetaDataLayout.setHorizontalGroup(
            panelScrollPaneEditMetaDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelScrollPaneEditMetaDataLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelEditTabInfoArrayDelimiter, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(scrollPaneEditMetadata, javax.swing.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
        );
        panelScrollPaneEditMetaDataLayout.setVerticalGroup(
            panelScrollPaneEditMetaDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelScrollPaneEditMetaDataLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelEditTabInfoArrayDelimiter, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPaneEditMetadata, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE))
        );

        panelGroupMetadataEdit.setBorder(javax.swing.BorderFactory.createTitledBorder(null, Bundle.getString("AppPanel.panelGroupMetadataEdit.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11))); // NOI18N

        labelMetadataInfoEditable.setFont(new java.awt.Font("Dialog", 0, 12));
        labelMetadataInfoEditable.setText(Bundle.getString("AppPanel.labelMetadataInfoEditable.text")); // NOI18N

        buttonSaveMetadata.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonSaveMetadata.setMnemonic('s');
        buttonSaveMetadata.setText(Bundle.getString("AppPanel.buttonSaveMetadata.text")); // NOI18N
        buttonSaveMetadata.setToolTipText(Bundle.getString("AppPanel.buttonSaveMetadata.toolTipText")); // NOI18N
        buttonSaveMetadata.setEnabled(false);

        buttonMetaDataTemplateCreate.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonMetaDataTemplateCreate.setMnemonic('v');
        buttonMetaDataTemplateCreate.setText(Bundle.getString("AppPanel.buttonMetaDataTemplateCreate.text")); // NOI18N
        buttonMetaDataTemplateCreate.setToolTipText(Bundle.getString("AppPanel.buttonMetaDataTemplateCreate.toolTipText")); // NOI18N

        javax.swing.GroupLayout panelGroupMetadataEditLayout = new javax.swing.GroupLayout(panelGroupMetadataEdit);
        panelGroupMetadataEdit.setLayout(panelGroupMetadataEditLayout);
        panelGroupMetadataEditLayout.setHorizontalGroup(
            panelGroupMetadataEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGroupMetadataEditLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelGroupMetadataEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelMetadataInfoEditable, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
                    .addGroup(panelGroupMetadataEditLayout.createSequentialGroup()
                        .addComponent(buttonMetaDataTemplateCreate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonSaveMetadata, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panelGroupMetadataEditLayout.setVerticalGroup(
            panelGroupMetadataEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGroupMetadataEditLayout.createSequentialGroup()
                .addComponent(labelMetadataInfoEditable)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelGroupMetadataEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonMetaDataTemplateCreate)
                    .addComponent(buttonSaveMetadata, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelGroupMetadataEditLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {buttonMetaDataTemplateCreate, buttonSaveMetadata});

        panelGroupMetadataTemplates.setBorder(javax.swing.BorderFactory.createTitledBorder(null, Bundle.getString("AppPanel.panelGroupMetadataTemplates.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11))); // NOI18N

        comboBoxMetaDataTemplates.setModel(new ComboBoxModelMetaDataEditTemplates());

        buttonMetaDataTemplateRename.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonMetaDataTemplateRename.setMnemonic('m');
        buttonMetaDataTemplateRename.setText(Bundle.getString("AppPanel.buttonMetaDataTemplateRename.text")); // NOI18N
        buttonMetaDataTemplateRename.setToolTipText(Bundle.getString("AppPanel.buttonMetaDataTemplateRename.toolTipText")); // NOI18N
        buttonMetaDataTemplateRename.setEnabled(false);

        buttonMetaDataTemplateDelete.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonMetaDataTemplateDelete.setMnemonic('\u00f6');
        buttonMetaDataTemplateDelete.setText(Bundle.getString("AppPanel.buttonMetaDataTemplateDelete.text")); // NOI18N
        buttonMetaDataTemplateDelete.setToolTipText(Bundle.getString("AppPanel.buttonMetaDataTemplateDelete.toolTipText")); // NOI18N
        buttonMetaDataTemplateDelete.setEnabled(false);

        buttonMetaDataTemplateInsert.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonMetaDataTemplateInsert.setMnemonic('e');
        buttonMetaDataTemplateInsert.setText(Bundle.getString("AppPanel.buttonMetaDataTemplateInsert.text")); // NOI18N
        buttonMetaDataTemplateInsert.setToolTipText(Bundle.getString("AppPanel.buttonMetaDataTemplateInsert.toolTipText")); // NOI18N
        buttonMetaDataTemplateInsert.setEnabled(false);

        buttonMetaDataTemplateUpdate.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonMetaDataTemplateUpdate.setMnemonic('a');
        buttonMetaDataTemplateUpdate.setText(Bundle.getString("AppPanel.buttonMetaDataTemplateUpdate.text")); // NOI18N
        buttonMetaDataTemplateUpdate.setToolTipText(Bundle.getString("AppPanel.buttonMetaDataTemplateUpdate.toolTipText")); // NOI18N
        buttonMetaDataTemplateUpdate.setEnabled(false);

        javax.swing.GroupLayout panelGroupMetadataTemplatesLayout = new javax.swing.GroupLayout(panelGroupMetadataTemplates);
        panelGroupMetadataTemplates.setLayout(panelGroupMetadataTemplatesLayout);
        panelGroupMetadataTemplatesLayout.setHorizontalGroup(
            panelGroupMetadataTemplatesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGroupMetadataTemplatesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelGroupMetadataTemplatesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(comboBoxMetaDataTemplates, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelGroupMetadataTemplatesLayout.createSequentialGroup()
                        .addComponent(buttonMetaDataTemplateUpdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonMetaDataTemplateInsert))
                    .addGroup(panelGroupMetadataTemplatesLayout.createSequentialGroup()
                        .addComponent(buttonMetaDataTemplateDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonMetaDataTemplateRename)))
                .addContainerGap(70, Short.MAX_VALUE))
        );

        panelGroupMetadataTemplatesLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {buttonMetaDataTemplateDelete, buttonMetaDataTemplateInsert, buttonMetaDataTemplateRename, buttonMetaDataTemplateUpdate});

        panelGroupMetadataTemplatesLayout.setVerticalGroup(
            panelGroupMetadataTemplatesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGroupMetadataTemplatesLayout.createSequentialGroup()
                .addComponent(comboBoxMetaDataTemplates, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelGroupMetadataTemplatesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonMetaDataTemplateUpdate)
                    .addComponent(buttonMetaDataTemplateInsert))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelGroupMetadataTemplatesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonMetaDataTemplateDelete)
                    .addComponent(buttonMetaDataTemplateRename))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelTabEditMetaDataLayout = new javax.swing.GroupLayout(panelTabEditMetaData);
        panelTabEditMetaData.setLayout(panelTabEditMetaDataLayout);
        panelTabEditMetaDataLayout.setHorizontalGroup(
            panelTabEditMetaDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelGroupMetadataTemplates, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelGroupMetadataEdit, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelScrollPaneEditMetaData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelTabEditMetaDataLayout.setVerticalGroup(
            panelTabEditMetaDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTabEditMetaDataLayout.createSequentialGroup()
                .addComponent(panelScrollPaneEditMetaData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelGroupMetadataEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelGroupMetadataTemplates, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        tabbedPaneMetaDataTabs.addTab(Bundle.getString("AppPanel.panelTabEditMetaData.TabConstraints.tabTitle"), panelTabEditMetaData); // NOI18N

        progressBarScheduledTasks.setForeground(new java.awt.Color(224, 234, 238));
        progressBarScheduledTasks.setToolTipText(AppSettings.tooltipTextProgressBarScheduledTasks);

        progressBarCurrentTasks.setForeground(new java.awt.Color(237, 238, 224));
        progressBarCurrentTasks.setToolTipText(AppSettings.tooltipTextProgressBarCurrentTasks);

        buttonStopScheduledTasks.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imagemetadataviewer/resource/icon_stop_scheduled_tasks_enabled.png"))); // NOI18N
        buttonStopScheduledTasks.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imagemetadataviewer/resource/icon_stop_scheduled_tasks_disabled.png"))); // NOI18N
        buttonStopScheduledTasks.setPreferredSize(new java.awt.Dimension(16, 16));

        javax.swing.GroupLayout panelMetadataProgressLayout = new javax.swing.GroupLayout(panelMetadataProgress);
        panelMetadataProgress.setLayout(panelMetadataProgressLayout);
        panelMetadataProgressLayout.setHorizontalGroup(
            panelMetadataProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(progressBarCurrentTasks, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMetadataProgressLayout.createSequentialGroup()
                .addComponent(progressBarScheduledTasks, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonStopScheduledTasks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        panelMetadataProgressLayout.setVerticalGroup(
            panelMetadataProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMetadataProgressLayout.createSequentialGroup()
                .addGroup(panelMetadataProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonStopScheduledTasks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(progressBarScheduledTasks, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBarCurrentTasks, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout panelMetadataLayout = new javax.swing.GroupLayout(panelMetadata);
        panelMetadata.setLayout(panelMetadataLayout);
        panelMetadataLayout.setHorizontalGroup(
            panelMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMetadataLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelColorBackgroundTableTextStoredInDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelLegendColorBackgroundTableTetStoredInDatabase)
                .addContainerGap(229, Short.MAX_VALUE))
            .addComponent(labelMetadataFilename, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
            .addGroup(panelMetadataLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelMetadataProgress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(tabbedPaneMetaDataTabs, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
        );
        panelMetadataLayout.setVerticalGroup(
            panelMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMetadataLayout.createSequentialGroup()
                .addComponent(labelMetadataFilename)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER, false)
                    .addGroup(panelMetadataLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(labelLegendColorBackgroundTableTetStoredInDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(labelColorBackgroundTableTextStoredInDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabbedPaneMetaDataTabs, javax.swing.GroupLayout.DEFAULT_SIZE, 573, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelMetadataProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        splitPaneThumbnailsMetadata.setRightComponent(panelMetadata);

        javax.swing.GroupLayout panelThumbnailsMetadataLayout = new javax.swing.GroupLayout(panelThumbnailsMetadata);
        panelThumbnailsMetadata.setLayout(panelThumbnailsMetadataLayout);
        panelThumbnailsMetadataLayout.setHorizontalGroup(
            panelThumbnailsMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 937, Short.MAX_VALUE)
            .addGroup(panelThumbnailsMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(splitPaneThumbnailsMetadata, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 937, Short.MAX_VALUE))
        );
        panelThumbnailsMetadataLayout.setVerticalGroup(
            panelThumbnailsMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 684, Short.MAX_VALUE)
            .addGroup(panelThumbnailsMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(splitPaneThumbnailsMetadata, javax.swing.GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE))
        );

        splitPaneMain.setRightComponent(panelThumbnailsMetadata);

        panelStatusbar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panelStatusbar.setFocusable(false);
        panelStatusbar.setRequestFocusEnabled(false);
        panelStatusbar.setVerifyInputWhenFocusTarget(false);

        labelStatusbar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        textFieldSearch.setToolTipText(Bundle.getString("AppPanel.textFieldSearch.toolTipText")); // NOI18N

        progressBarCreateMetaDataOfCurrentThumbnails.setForeground(new java.awt.Color(255, 254, 246));
        progressBarCreateMetaDataOfCurrentThumbnails.setToolTipText(AppSettings.tooltipTextProgressBarDirectory);
        progressBarCreateMetaDataOfCurrentThumbnails.setBorder(null);

        buttonLogfileDialog.setToolTipText(Bundle.getString("AppPanel.buttonLogfileDialog.toolTipText")); // NOI18N
        buttonLogfileDialog.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        buttonLogfileDialog.setPreferredSize(new java.awt.Dimension(16, 16));

        buttonAdvanedSearch.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonAdvanedSearch.setText(Bundle.getString("AppPanel.buttonAdvanedSearch.text")); // NOI18N

        javax.swing.GroupLayout panelStatusbarLayout = new javax.swing.GroupLayout(panelStatusbar);
        panelStatusbar.setLayout(panelStatusbarLayout);
        panelStatusbarLayout.setHorizontalGroup(
            panelStatusbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStatusbarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelStatusbar, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(buttonLogfileDialog, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonAdvanedSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textFieldSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBarCreateMetaDataOfCurrentThumbnails, javax.swing.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE))
        );
        panelStatusbarLayout.setVerticalGroup(
            panelStatusbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStatusbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(buttonAdvanedSearch)
                .addComponent(buttonLogfileDialog, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(labelStatusbar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(progressBarCreateMetaDataOfCurrentThumbnails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(textFieldSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        panelStatusbarLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {progressBarCreateMetaDataOfCurrentThumbnails, textFieldSearch});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(splitPaneMain, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelStatusbar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(splitPaneMain, javax.swing.GroupLayout.DEFAULT_SIZE, 686, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelStatusbar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAdvanedSearch;
    private javax.swing.JButton buttonLogfileDialog;
    private javax.swing.JButton buttonMetaDataTemplateCreate;
    private javax.swing.JButton buttonMetaDataTemplateDelete;
    private javax.swing.JButton buttonMetaDataTemplateInsert;
    private javax.swing.JButton buttonMetaDataTemplateRename;
    private javax.swing.JButton buttonMetaDataTemplateUpdate;
    private javax.swing.JButton buttonSaveMetadata;
    private javax.swing.JButton buttonStopScheduledTasks;
    private javax.swing.JComboBox comboBoxMetaDataTemplates;
    private javax.swing.JLabel labelColorBackgroundTableTextStoredInDatabase;
    private javax.swing.JLabel labelEditTabInfoArrayDelimiter;
    private javax.swing.JLabel labelLegendColorBackgroundTableTetStoredInDatabase;
    private javax.swing.JLabel labelMetadataFilename;
    private javax.swing.JLabel labelMetadataInfoEditable;
    private javax.swing.JLabel labelStatusbar;
    private javax.swing.JList listCategories;
    private javax.swing.JList listFavoriteDirectories;
    private javax.swing.JPanel panelCategories;
    private javax.swing.JPanel panelEditMetadata;
    private javax.swing.JPanel panelFavoriteDirectories;
    private javax.swing.JPanel panelGroupMetadataEdit;
    private javax.swing.JPanel panelGroupMetadataTemplates;
    private javax.swing.JPanel panelImageCollections;
    private de.elmar_baumann.imagemetadataviewer.view.panels.ImageFileThumbnailsPanel panelImageFileThumbnails;
    private javax.swing.JPanel panelMetadata;
    private javax.swing.JPanel panelMetadataProgress;
    private javax.swing.JPanel panelScrollPaneEditMetaData;
    private javax.swing.JPanel panelSelection;
    private javax.swing.JPanel panelSelectionDirectories;
    private javax.swing.JPanel panelSelectionSavedSearches;
    private javax.swing.JPanel panelStatusbar;
    private javax.swing.JPanel panelTabEditMetaData;
    private javax.swing.JPanel panelThumbnails;
    private javax.swing.JPanel panelThumbnailsMetadata;
    private javax.swing.JProgressBar progressBarCreateMetaDataOfCurrentThumbnails;
    private javax.swing.JProgressBar progressBarCurrentTasks;
    private javax.swing.JProgressBar progressBarScheduledTasks;
    private javax.swing.JScrollPane scrollPaneEditMetadata;
    private javax.swing.JScrollPane scrollPaneListCategories;
    private javax.swing.JScrollPane scrollPaneListFavoriteDirectories;
    private javax.swing.JScrollPane scrollPaneTableExif;
    private javax.swing.JScrollPane scrollPaneTableIptc;
    private javax.swing.JScrollPane scrollPaneTableXmpCameraRawSettings;
    private javax.swing.JScrollPane scrollPaneTableXmpDc;
    private javax.swing.JScrollPane scrollPaneTableXmpExif;
    private javax.swing.JScrollPane scrollPaneTableXmpIptc;
    private javax.swing.JScrollPane scrollPaneTableXmpLightroom;
    private javax.swing.JScrollPane scrollPaneTableXmpPhotoshop;
    private javax.swing.JScrollPane scrollPaneTableXmpTiff;
    private javax.swing.JScrollPane scrollPaneTableXmpXap;
    private javax.swing.JScrollPane scrollPaneThumbnailsPanel;
    private javax.swing.JScrollPane scrollPaneTreeDirectories;
    private javax.swing.JScrollPane scrollPaneTreeImageCollections;
    private javax.swing.JScrollPane scrollPaneTreeSavedSearches;
    private javax.swing.JSplitPane splitPaneMain;
    private javax.swing.JSplitPane splitPaneThumbnailsMetadata;
    private javax.swing.JTabbedPane tabbedPaneMetaDataTabs;
    private javax.swing.JTabbedPane tabbedPaneSelection;
    private javax.swing.JTabbedPane tabbedPaneXmp;
    private javax.swing.JTable tableExif;
    private javax.swing.JTable tableIptc;
    private javax.swing.JTable tableXmpCameraRawSettings;
    private javax.swing.JTable tableXmpDc;
    private javax.swing.JTable tableXmpExif;
    private javax.swing.JTable tableXmpIptc;
    private javax.swing.JTable tableXmpLightroom;
    private javax.swing.JTable tableXmpPhotoshop;
    private javax.swing.JTable tableXmpTiff;
    private javax.swing.JTable tableXmpXap;
    private javax.swing.JTextField textFieldSearch;
    private javax.swing.JTree treeDirectories;
    private javax.swing.JTree treeImageCollections;
    private javax.swing.JTree treeSavedSearches;
    // End of variables declaration//GEN-END:variables
}
