package de.elmar_baumann.imv.view.panels;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.lib.renderer.TreeCellRendererDirectories;
import de.elmar_baumann.imv.event.AppExitListener;
import de.elmar_baumann.imv.event.AppStartListener;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.renderer.ListCellRendererCategories;
import de.elmar_baumann.imv.view.renderer.ListCellRendererFavoriteDirectories;
import de.elmar_baumann.imv.view.renderer.ListCellRendererImageCollections;
import de.elmar_baumann.imv.view.renderer.ListCellRendererSavedSearches;
import de.elmar_baumann.lib.persistence.PersistentSettings;
import de.elmar_baumann.lib.persistence.PersistentSettingsHints;
import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.tree.TreeSelectionModel;

/**
 * Panel der Anwendung.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class AppPanel extends javax.swing.JPanel
    implements AppStartListener, AppExitListener {

    private List<JTable> xmpTables = new ArrayList<JTable>();
    private List<JTable> metadataTables = new ArrayList<JTable>();
    private List<JTree> selectionTrees = new ArrayList<JTree>();
    private List<JList> selectionLists = new ArrayList<JList>();
    private MetadataEditPanelsArray editPanelsArray;
    private MetaDataEditActionsPanel metadataEditActionsPanel;

    public AppPanel() {
        Panels.getInstance().setAppPanel(this);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        editPanelsArray = new MetadataEditPanelsArray(panelEditMetadata);
        panelThumbnails.setViewport(scrollPaneThumbnails.getViewport());
        setBackgroundColorTablesScrollPanes();
        disableTreeMultipleSelection();
        initArrays();
    }

    private void initArrays() {
        initTableArrays();
        initSelectionTreeArray();
        initSelectionListArray();
    }

    public MetaDataEditActionsPanel getMetaDataEditActionsPanel() {
        if (metadataEditActionsPanel == null) {
            metadataEditActionsPanel = new MetaDataEditActionsPanel();
        }
        return metadataEditActionsPanel;
    }

    public JScrollPane getScrollPaneThumbnailsPanel() {
        return scrollPaneThumbnails;
    }

    public JSlider getSliderThumbnailSize() {
        return sliderThumbnailSize;
    }

    public JTabbedPane getTabbedPaneMetadata() {
        return tabbedPaneMetaData;
    }

    public JTabbedPane getTabbedPaneSelection() {
        return tabbedPaneSelection;
    }

    public Component getTabMetadataIptc() {
        return scrollPaneIptc;
    }

    public Component getTabMetadataExif() {
        return scrollPaneExif;
    }

    public Component getTabMetadataXmp() {
        return tabbedPaneXmp;
    }
    
    public Component getTabMetadataEdit() {
        return panelTabEditMetaData;
    }
    
    public Component getTabSelectionDirectories() {
        return panelSelectionDirectories;
    }

    public Component getTabSelectionFavoriteDirectories() {
        return panelSelectionFavoriteDirectories;
    }

    public Component getTabSelectionImageCollections() {
        return panelSelectionImageCollections;
    }

    public JPanel getTabSelectionSavedSearches() {
        return panelSelectionSavedSearches;
    }

    public JPanel getTabSelectionCategories() {
        return panelSelectionCategories;
    }

    public JProgressBar getProgressBarCreateMetaDataOfCurrentThumbnails() {
        return progressBarCreateMetaDataOfCurrentThumbnails;
    }

    /**
     * Liefert die Progressbar für die aktuellen Tasks.
     * <em>Auf keinen Fall von hier abholen, sondern von
     * {@link de.elmar_baumann.imv.resource.ProgressBarCurrentTasks}!
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

    public List<JTree> getSelectionTrees() {
        return selectionTrees;
    }

    public List<JList> getSelectionLists() {
        return selectionLists;
    }

    public ImageFileThumbnailsPanel getPanelThumbnails() {
        return panelThumbnails;
    }

    public JList getListImageCollections() {
        return listImageCollections;
    }

    public JList getListSavedSearches() {
        return listSavedSearches;
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
        return metadataEditActionsPanel.buttonSaveMetadata;
    }

    public JButton getButtonMetaDataTemplateCreate() {
        return metadataEditActionsPanel.buttonMetaDataTemplateCreate;
    }

    public JButton getButtonMetaDataTemplateUpdate() {
        return metadataEditActionsPanel.buttonMetaDataTemplateUpdate;
    }

    public JButton getButtonMetaDataTemplateRename() {
        return metadataEditActionsPanel.buttonMetaDataTemplateRename;
    }

    public JButton getButtonMetaDataTemplateInsert() {
        return metadataEditActionsPanel.buttonMetaDataTemplateInsert;
    }

    public JButton getButtonMetaDataTemplateDelete() {
        return metadataEditActionsPanel.buttonMetaDataTemplateDelete;
    }

    public JButton getButtonStopScheduledTasks() {
        return buttonStopScheduledTasks;
    }

    public JComboBox getComboBoxMetaDataTemplates() {
        return metadataEditActionsPanel.comboBoxMetaDataTemplates;
    }

    public JLabel getLabelMetadataInfoEditable() {
        return metadataEditActionsPanel.labelMetadataInfoEditable;
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

    public MetadataEditPanelsArray getEditPanelsArray() {
        return editPanelsArray;
    }

    public List<JTable> getMetaDataTables() {
        return metadataTables;
    }

    public List<JTable> getXmpTables() {
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
        metadataTables.addAll(xmpTables);
        metadataTables.add(tableExif);
        metadataTables.add(tableIptc);
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
    }

    private void initSelectionListArray() {
        selectionLists.add(listCategories);
        selectionLists.add(listFavoriteDirectories);
        selectionLists.add(listImageCollections);
        selectionLists.add(listSavedSearches);
    }

    private void setBackgroundColorTablesScrollPanes() {
        for (JTable table : metadataTables) {
            Container container = table.getParent();
            if (container instanceof JViewport) {
                JViewport viewport = (JViewport) container;
                viewport.setBackground(table.getBackground());
            }
        }
    }

    @Override
    public void appWillStart() {
        PersistentSettings.getInstance().getComponent(this, getPersistentSettingsHints());
    }
    
    @Override
    public void appWillExit() {
        PersistentSettings.getInstance().setComponent(this, getPersistentSettingsHints());
    }

    private PersistentSettingsHints getPersistentSettingsHints() {
        PersistentSettingsHints hints = new PersistentSettingsHints();
        String className = getClass().getName();
        hints.addExcludedMember(className + ".textFieldSearch"); // NOI18N
        hints.addExcludedMember(className + ".panelEditMetadata"); // NOI18N
        hints.addExcludedMember(className + ".treeDirectories"); // NOI18N
        return hints;
    }

    private void disableTreeMultipleSelection() {
        int mode = TreeSelectionModel.SINGLE_TREE_SELECTION;
        treeDirectories.getSelectionModel().setSelectionMode(mode);
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
        scrollPaneDirectories = new javax.swing.JScrollPane();
        treeDirectories = new javax.swing.JTree();
        panelSelectionSavedSearches = new javax.swing.JPanel();
        scrollPaneSavedSearches = new javax.swing.JScrollPane();
        listSavedSearches = new javax.swing.JList();
        panelSelectionImageCollections = new javax.swing.JPanel();
        scrollPaneImageCollections = new javax.swing.JScrollPane();
        listImageCollections = new javax.swing.JList();
        panelSelectionCategories = new javax.swing.JPanel();
        scrollPaneCategories = new javax.swing.JScrollPane();
        listCategories = new javax.swing.JList();
        panelSelectionFavoriteDirectories = new javax.swing.JPanel();
        scrollPaneFavoriteDirectories = new javax.swing.JScrollPane();
        listFavoriteDirectories = new javax.swing.JList();
        panelThumbnailsMetadata = new javax.swing.JPanel();
        splitPaneThumbnailsMetadata = new javax.swing.JSplitPane();
        panelThumbnailsContent = new javax.swing.JPanel();
        scrollPaneThumbnails = new javax.swing.JScrollPane();
        panelThumbnails = new de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel();
        panelMetadata = new javax.swing.JPanel();
        labelMetadataFilename = new javax.swing.JLabel();
        labelColorBackgroundTableTextStoredInDatabase = new javax.swing.JLabel();
        labelLegendColorBackgroundTableTetStoredInDatabase = new javax.swing.JLabel();
        tabbedPaneMetaData = new javax.swing.JTabbedPane();
        scrollPaneIptc = new javax.swing.JScrollPane();
        tableIptc = new javax.swing.JTable();
        scrollPaneExif = new javax.swing.JScrollPane();
        tableExif = new javax.swing.JTable();
        tabbedPaneXmp = new javax.swing.JTabbedPane();
        scrollPaneXmpTiff = new javax.swing.JScrollPane();
        tableXmpTiff = new javax.swing.JTable();
        scrollPaneXmpExif = new javax.swing.JScrollPane();
        tableXmpExif = new javax.swing.JTable();
        scrollPaneXmpDc = new javax.swing.JScrollPane();
        tableXmpDc = new javax.swing.JTable();
        scrollPaneXmpIptc = new javax.swing.JScrollPane();
        tableXmpIptc = new javax.swing.JTable();
        scrollPaneXmpPhotoshop = new javax.swing.JScrollPane();
        tableXmpPhotoshop = new javax.swing.JTable();
        scrollPaneXmpXap = new javax.swing.JScrollPane();
        tableXmpXap = new javax.swing.JTable();
        scrollPaneXmpLightroom = new javax.swing.JScrollPane();
        tableXmpLightroom = new javax.swing.JTable();
        scrollPaneXmpCameraRawSettings = new javax.swing.JScrollPane();
        tableXmpCameraRawSettings = new javax.swing.JTable();
        panelTabEditMetaData = new javax.swing.JPanel();
        panelScrollPaneEditMetaData = new javax.swing.JPanel();
        scrollPaneEditMetadata = new javax.swing.JScrollPane();
        panelEditMetadata = new javax.swing.JPanel();
        panelMetadataProgress = new javax.swing.JPanel();
        progressBarScheduledTasks = new javax.swing.JProgressBar();
        progressBarCurrentTasks = new javax.swing.JProgressBar();
        buttonStopScheduledTasks = new javax.swing.JButton();
        panelStatusbar = new javax.swing.JPanel();
        sliderThumbnailSize = new javax.swing.JSlider();
        buttonLogfileDialog = new javax.swing.JButton();
        buttonAdvanedSearch = new javax.swing.JButton();
        textFieldSearch = new javax.swing.JTextField();
        labelStatusbar = new javax.swing.JLabel();
        progressBarCreateMetaDataOfCurrentThumbnails = new javax.swing.JProgressBar();

        treeDirectories.setCellRenderer(new TreeCellRendererDirectories());
        treeDirectories.setExpandsSelectedPaths(false);
        treeDirectories.setModel(null);
        scrollPaneDirectories.setViewportView(treeDirectories);

        javax.swing.GroupLayout panelSelectionDirectoriesLayout = new javax.swing.GroupLayout(panelSelectionDirectories);
        panelSelectionDirectories.setLayout(panelSelectionDirectoriesLayout);
        panelSelectionDirectoriesLayout.setHorizontalGroup(
            panelSelectionDirectoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneDirectories, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
        );
        panelSelectionDirectoriesLayout.setVerticalGroup(
            panelSelectionDirectoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneDirectories, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
        );

        tabbedPaneSelection.addTab(Bundle.getString("AppPanel.panelSelectionDirectories.TabConstraints.tabTitle"), panelSelectionDirectories); // NOI18N

        listSavedSearches.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listSavedSearches.setCellRenderer(new ListCellRendererSavedSearches());
        scrollPaneSavedSearches.setViewportView(listSavedSearches);

        javax.swing.GroupLayout panelSelectionSavedSearchesLayout = new javax.swing.GroupLayout(panelSelectionSavedSearches);
        panelSelectionSavedSearches.setLayout(panelSelectionSavedSearchesLayout);
        panelSelectionSavedSearchesLayout.setHorizontalGroup(
            panelSelectionSavedSearchesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneSavedSearches, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
        );
        panelSelectionSavedSearchesLayout.setVerticalGroup(
            panelSelectionSavedSearchesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneSavedSearches, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
        );

        tabbedPaneSelection.addTab(Bundle.getString("AppPanel.panelSelectionSavedSearches.TabConstraints.tabTitle"), panelSelectionSavedSearches); // NOI18N

        listImageCollections.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listImageCollections.setCellRenderer(new ListCellRendererImageCollections());
        scrollPaneImageCollections.setViewportView(listImageCollections);

        javax.swing.GroupLayout panelSelectionImageCollectionsLayout = new javax.swing.GroupLayout(panelSelectionImageCollections);
        panelSelectionImageCollections.setLayout(panelSelectionImageCollectionsLayout);
        panelSelectionImageCollectionsLayout.setHorizontalGroup(
            panelSelectionImageCollectionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneImageCollections, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
        );
        panelSelectionImageCollectionsLayout.setVerticalGroup(
            panelSelectionImageCollectionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneImageCollections, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
        );

        tabbedPaneSelection.addTab(Bundle.getString("AppPanel.panelSelectionImageCollections.TabConstraints.tabTitle"), panelSelectionImageCollections); // NOI18N

        listCategories.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listCategories.setCellRenderer(new ListCellRendererCategories());
        scrollPaneCategories.setViewportView(listCategories);

        javax.swing.GroupLayout panelSelectionCategoriesLayout = new javax.swing.GroupLayout(panelSelectionCategories);
        panelSelectionCategories.setLayout(panelSelectionCategoriesLayout);
        panelSelectionCategoriesLayout.setHorizontalGroup(
            panelSelectionCategoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneCategories, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
        );
        panelSelectionCategoriesLayout.setVerticalGroup(
            panelSelectionCategoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneCategories, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
        );

        tabbedPaneSelection.addTab(Bundle.getString("AppPanel.panelSelectionCategories.TabConstraints.tabTitle"), panelSelectionCategories); // NOI18N

        listFavoriteDirectories.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listFavoriteDirectories.setCellRenderer(new ListCellRendererFavoriteDirectories());
        scrollPaneFavoriteDirectories.setViewportView(listFavoriteDirectories);

        javax.swing.GroupLayout panelSelectionFavoriteDirectoriesLayout = new javax.swing.GroupLayout(panelSelectionFavoriteDirectories);
        panelSelectionFavoriteDirectories.setLayout(panelSelectionFavoriteDirectoriesLayout);
        panelSelectionFavoriteDirectoriesLayout.setHorizontalGroup(
            panelSelectionFavoriteDirectoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 130, Short.MAX_VALUE)
            .addGroup(panelSelectionFavoriteDirectoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrollPaneFavoriteDirectories, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE))
        );
        panelSelectionFavoriteDirectoriesLayout.setVerticalGroup(
            panelSelectionFavoriteDirectoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 284, Short.MAX_VALUE)
            .addGroup(panelSelectionFavoriteDirectoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrollPaneFavoriteDirectories, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE))
        );

        tabbedPaneSelection.addTab(Bundle.getString("AppPanel.panelSelectionFavoriteDirectories.TabConstraints.tabTitle"), panelSelectionFavoriteDirectories); // NOI18N

        javax.swing.GroupLayout panelSelectionLayout = new javax.swing.GroupLayout(panelSelection);
        panelSelection.setLayout(panelSelectionLayout);
        panelSelectionLayout.setHorizontalGroup(
            panelSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 135, Short.MAX_VALUE)
            .addGroup(panelSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(tabbedPaneSelection, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE))
        );
        panelSelectionLayout.setVerticalGroup(
            panelSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 354, Short.MAX_VALUE)
            .addGroup(panelSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(tabbedPaneSelection, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE))
        );

        splitPaneMain.setLeftComponent(panelSelection);

        panelThumbnails.setMinimumSize(new java.awt.Dimension(200, 212));
        panelThumbnails.setPreferredSize(new java.awt.Dimension(200, 212));

        javax.swing.GroupLayout panelThumbnailsLayout = new javax.swing.GroupLayout(panelThumbnails);
        panelThumbnails.setLayout(panelThumbnailsLayout);
        panelThumbnailsLayout.setHorizontalGroup(
            panelThumbnailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 18, Short.MAX_VALUE)
        );
        panelThumbnailsLayout.setVerticalGroup(
            panelThumbnailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 348, Short.MAX_VALUE)
        );

        scrollPaneThumbnails.setViewportView(panelThumbnails);

        javax.swing.GroupLayout panelThumbnailsContentLayout = new javax.swing.GroupLayout(panelThumbnailsContent);
        panelThumbnailsContent.setLayout(panelThumbnailsContentLayout);
        panelThumbnailsContentLayout.setHorizontalGroup(
            panelThumbnailsContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 22, Short.MAX_VALUE)
            .addGroup(panelThumbnailsContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrollPaneThumbnails, javax.swing.GroupLayout.PREFERRED_SIZE, 22, Short.MAX_VALUE))
        );
        panelThumbnailsContentLayout.setVerticalGroup(
            panelThumbnailsContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 352, Short.MAX_VALUE)
            .addGroup(panelThumbnailsContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrollPaneThumbnails, javax.swing.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE))
        );

        splitPaneThumbnailsMetadata.setLeftComponent(panelThumbnailsContent);

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

        tabbedPaneMetaData.setOpaque(true);

        tableIptc.setAutoCreateRowSorter(true);
        tableIptc.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableIptc.setName("tableIptc"); // NOI18N
        tableIptc.setRowSelectionAllowed(false);
        tableIptc.setUpdateSelectionOnSort(false);
        tableIptc.setVerifyInputWhenFocusTarget(false);
        scrollPaneIptc.setViewportView(tableIptc);

        tabbedPaneMetaData.addTab(Bundle.getString("AppPanel.scrollPaneIptc.TabConstraints.tabTitle"), scrollPaneIptc); // NOI18N

        tableExif.setAutoCreateRowSorter(true);
        tableExif.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableExif.setAutoscrolls(false);
        tableExif.setEnabled(false);
        tableExif.setUpdateSelectionOnSort(false);
        tableExif.setVerifyInputWhenFocusTarget(false);
        scrollPaneExif.setViewportView(tableExif);

        tabbedPaneMetaData.addTab(Bundle.getString("AppPanel.scrollPaneExif.TabConstraints.tabTitle"), scrollPaneExif); // NOI18N

        tableXmpTiff.setAutoCreateRowSorter(true);
        tableXmpTiff.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        scrollPaneXmpTiff.setViewportView(tableXmpTiff);

        tabbedPaneXmp.addTab(Bundle.getString("AppPanel.scrollPaneXmpTiff.TabConstraints.tabTitle"), scrollPaneXmpTiff); // NOI18N

        tableXmpExif.setAutoCreateRowSorter(true);
        tableXmpExif.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        scrollPaneXmpExif.setViewportView(tableXmpExif);

        tabbedPaneXmp.addTab(Bundle.getString("AppPanel.scrollPaneXmpExif.TabConstraints.tabTitle"), scrollPaneXmpExif); // NOI18N

        tableXmpDc.setAutoCreateRowSorter(true);
        tableXmpDc.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        scrollPaneXmpDc.setViewportView(tableXmpDc);

        tabbedPaneXmp.addTab(Bundle.getString("AppPanel.scrollPaneXmpDc.TabConstraints.tabTitle"), scrollPaneXmpDc); // NOI18N

        tableXmpIptc.setAutoCreateRowSorter(true);
        tableXmpIptc.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        scrollPaneXmpIptc.setViewportView(tableXmpIptc);

        tabbedPaneXmp.addTab(Bundle.getString("AppPanel.scrollPaneXmpIptc.TabConstraints.tabTitle"), scrollPaneXmpIptc); // NOI18N

        tableXmpPhotoshop.setAutoCreateRowSorter(true);
        tableXmpPhotoshop.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        scrollPaneXmpPhotoshop.setViewportView(tableXmpPhotoshop);

        tabbedPaneXmp.addTab(Bundle.getString("AppPanel.scrollPaneXmpPhotoshop.TabConstraints.tabTitle"), scrollPaneXmpPhotoshop); // NOI18N

        tableXmpXap.setAutoCreateRowSorter(true);
        tableXmpXap.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        scrollPaneXmpXap.setViewportView(tableXmpXap);

        tabbedPaneXmp.addTab(Bundle.getString("AppPanel.scrollPaneXmpXap.TabConstraints.tabTitle"), scrollPaneXmpXap); // NOI18N

        tableXmpLightroom.setAutoCreateRowSorter(true);
        tableXmpLightroom.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        scrollPaneXmpLightroom.setViewportView(tableXmpLightroom);

        tabbedPaneXmp.addTab(Bundle.getString("AppPanel.scrollPaneXmpLightroom.TabConstraints.tabTitle"), scrollPaneXmpLightroom); // NOI18N

        tableXmpCameraRawSettings.setAutoCreateRowSorter(true);
        tableXmpCameraRawSettings.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        scrollPaneXmpCameraRawSettings.setViewportView(tableXmpCameraRawSettings);

        tabbedPaneXmp.addTab(Bundle.getString("AppPanel.scrollPaneXmpCameraRawSettings.TabConstraints.tabTitle"), scrollPaneXmpCameraRawSettings); // NOI18N

        tabbedPaneMetaData.addTab(Bundle.getString("AppPanel.tabbedPaneXmp.TabConstraints.tabTitle"), tabbedPaneXmp); // NOI18N

        javax.swing.GroupLayout panelEditMetadataLayout = new javax.swing.GroupLayout(panelEditMetadata);
        panelEditMetadata.setLayout(panelEditMetadataLayout);
        panelEditMetadataLayout.setHorizontalGroup(
            panelEditMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 923, Short.MAX_VALUE)
        );
        panelEditMetadataLayout.setVerticalGroup(
            panelEditMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 499, Short.MAX_VALUE)
        );

        scrollPaneEditMetadata.setViewportView(panelEditMetadata);

        javax.swing.GroupLayout panelScrollPaneEditMetaDataLayout = new javax.swing.GroupLayout(panelScrollPaneEditMetaData);
        panelScrollPaneEditMetaData.setLayout(panelScrollPaneEditMetaDataLayout);
        panelScrollPaneEditMetaDataLayout.setHorizontalGroup(
            panelScrollPaneEditMetaDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneEditMetadata, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
        );
        panelScrollPaneEditMetaDataLayout.setVerticalGroup(
            panelScrollPaneEditMetaDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneEditMetadata, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelTabEditMetaDataLayout = new javax.swing.GroupLayout(panelTabEditMetaData);
        panelTabEditMetaData.setLayout(panelTabEditMetaDataLayout);
        panelTabEditMetaDataLayout.setHorizontalGroup(
            panelTabEditMetaDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelScrollPaneEditMetaData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelTabEditMetaDataLayout.setVerticalGroup(
            panelTabEditMetaDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelScrollPaneEditMetaData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        tabbedPaneMetaData.addTab(Bundle.getString("AppPanel.panelTabEditMetaData.TabConstraints.tabTitle"), panelTabEditMetaData); // NOI18N

        progressBarScheduledTasks.setToolTipText(AppSettings.tooltipTextProgressBarScheduledTasks);

        progressBarCurrentTasks.setToolTipText(AppSettings.tooltipTextProgressBarCurrentTasks);

        buttonStopScheduledTasks.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icon_stop_scheduled_tasks_enabled.png"))); // NOI18N
        buttonStopScheduledTasks.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icon_stop_scheduled_tasks_disabled.png"))); // NOI18N
        buttonStopScheduledTasks.setPreferredSize(new java.awt.Dimension(16, 16));

        javax.swing.GroupLayout panelMetadataProgressLayout = new javax.swing.GroupLayout(panelMetadataProgress);
        panelMetadataProgress.setLayout(panelMetadataProgressLayout);
        panelMetadataProgressLayout.setHorizontalGroup(
            panelMetadataProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(progressBarCurrentTasks, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMetadataProgressLayout.createSequentialGroup()
                .addComponent(progressBarScheduledTasks, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
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
                .addContainerGap(197, Short.MAX_VALUE))
            .addComponent(labelMetadataFilename, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
            .addGroup(panelMetadataLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelMetadataProgress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(tabbedPaneMetaData, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
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
                .addComponent(tabbedPaneMetaData, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelMetadataProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        splitPaneThumbnailsMetadata.setRightComponent(panelMetadata);

        javax.swing.GroupLayout panelThumbnailsMetadataLayout = new javax.swing.GroupLayout(panelThumbnailsMetadata);
        panelThumbnailsMetadata.setLayout(panelThumbnailsMetadataLayout);
        panelThumbnailsMetadataLayout.setHorizontalGroup(
            panelThumbnailsMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 317, Short.MAX_VALUE)
            .addGroup(panelThumbnailsMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(splitPaneThumbnailsMetadata, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE))
        );
        panelThumbnailsMetadataLayout.setVerticalGroup(
            panelThumbnailsMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 354, Short.MAX_VALUE)
            .addGroup(panelThumbnailsMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(splitPaneThumbnailsMetadata))
        );

        splitPaneMain.setRightComponent(panelThumbnailsMetadata);

        panelStatusbar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panelStatusbar.setFocusable(false);
        panelStatusbar.setRequestFocusEnabled(false);
        panelStatusbar.setVerifyInputWhenFocusTarget(false);

        sliderThumbnailSize.setMajorTickSpacing(10);
        sliderThumbnailSize.setMinimum(10);
        sliderThumbnailSize.setPaintTicks(true);
        sliderThumbnailSize.setSnapToTicks(true);
        sliderThumbnailSize.setToolTipText(Bundle.getString("AppPanel.sliderThumbnailSize.toolTipText")); // NOI18N

        buttonLogfileDialog.setToolTipText(Bundle.getString("AppPanel.buttonLogfileDialog.toolTipText")); // NOI18N
        buttonLogfileDialog.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        buttonLogfileDialog.setPreferredSize(new java.awt.Dimension(16, 16));

        buttonAdvanedSearch.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonAdvanedSearch.setText(Bundle.getString("AppPanel.buttonAdvanedSearch.text")); // NOI18N

        textFieldSearch.setToolTipText(Bundle.getString("AppPanel.textFieldSearch.toolTipText")); // NOI18N

        labelStatusbar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        progressBarCreateMetaDataOfCurrentThumbnails.setToolTipText(AppSettings.tooltipTextProgressBarDirectory);

        javax.swing.GroupLayout panelStatusbarLayout = new javax.swing.GroupLayout(panelStatusbar);
        panelStatusbar.setLayout(panelStatusbarLayout);
        panelStatusbarLayout.setHorizontalGroup(
            panelStatusbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStatusbarLayout.createSequentialGroup()
                .addGroup(panelStatusbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelStatusbarLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(sliderThumbnailSize, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonLogfileDialog, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonAdvanedSearch))
                    .addGroup(panelStatusbarLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(labelStatusbar, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelStatusbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(progressBarCreateMetaDataOfCurrentThumbnails, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                    .addComponent(textFieldSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelStatusbarLayout.setVerticalGroup(
            panelStatusbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStatusbarLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(panelStatusbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(textFieldSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonAdvanedSearch)
                    .addComponent(buttonLogfileDialog, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sliderThumbnailSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelStatusbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(progressBarCreateMetaDataOfCurrentThumbnails, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labelStatusbar, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );

        panelStatusbarLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {labelStatusbar, progressBarCreateMetaDataOfCurrentThumbnails});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelStatusbar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(splitPaneMain, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(splitPaneMain)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelStatusbar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAdvanedSearch;
    private javax.swing.JButton buttonLogfileDialog;
    private javax.swing.JButton buttonStopScheduledTasks;
    private javax.swing.JLabel labelColorBackgroundTableTextStoredInDatabase;
    private javax.swing.JLabel labelLegendColorBackgroundTableTetStoredInDatabase;
    private javax.swing.JLabel labelMetadataFilename;
    private javax.swing.JLabel labelStatusbar;
    private javax.swing.JList listCategories;
    private javax.swing.JList listFavoriteDirectories;
    private javax.swing.JList listImageCollections;
    private javax.swing.JList listSavedSearches;
    private javax.swing.JPanel panelEditMetadata;
    private javax.swing.JPanel panelMetadata;
    private javax.swing.JPanel panelMetadataProgress;
    private javax.swing.JPanel panelScrollPaneEditMetaData;
    private javax.swing.JPanel panelSelection;
    private javax.swing.JPanel panelSelectionCategories;
    private javax.swing.JPanel panelSelectionDirectories;
    private javax.swing.JPanel panelSelectionFavoriteDirectories;
    private javax.swing.JPanel panelSelectionImageCollections;
    private javax.swing.JPanel panelSelectionSavedSearches;
    private javax.swing.JPanel panelStatusbar;
    private javax.swing.JPanel panelTabEditMetaData;
    private de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel panelThumbnails;
    private javax.swing.JPanel panelThumbnailsContent;
    private javax.swing.JPanel panelThumbnailsMetadata;
    private javax.swing.JProgressBar progressBarCreateMetaDataOfCurrentThumbnails;
    private javax.swing.JProgressBar progressBarCurrentTasks;
    private javax.swing.JProgressBar progressBarScheduledTasks;
    private javax.swing.JScrollPane scrollPaneCategories;
    private javax.swing.JScrollPane scrollPaneDirectories;
    private javax.swing.JScrollPane scrollPaneEditMetadata;
    private javax.swing.JScrollPane scrollPaneExif;
    private javax.swing.JScrollPane scrollPaneFavoriteDirectories;
    private javax.swing.JScrollPane scrollPaneImageCollections;
    private javax.swing.JScrollPane scrollPaneIptc;
    private javax.swing.JScrollPane scrollPaneSavedSearches;
    private javax.swing.JScrollPane scrollPaneThumbnails;
    private javax.swing.JScrollPane scrollPaneXmpCameraRawSettings;
    private javax.swing.JScrollPane scrollPaneXmpDc;
    private javax.swing.JScrollPane scrollPaneXmpExif;
    private javax.swing.JScrollPane scrollPaneXmpIptc;
    private javax.swing.JScrollPane scrollPaneXmpLightroom;
    private javax.swing.JScrollPane scrollPaneXmpPhotoshop;
    private javax.swing.JScrollPane scrollPaneXmpTiff;
    private javax.swing.JScrollPane scrollPaneXmpXap;
    private javax.swing.JSlider sliderThumbnailSize;
    private javax.swing.JSplitPane splitPaneMain;
    private javax.swing.JSplitPane splitPaneThumbnailsMetadata;
    private javax.swing.JTabbedPane tabbedPaneMetaData;
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
    // End of variables declaration//GEN-END:variables
}
