package de.elmar_baumann.imv.view.panels;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.lib.renderer.TreeCellRendererDirectories;
import de.elmar_baumann.imv.event.AppExitListener;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.ViewUtil;
import de.elmar_baumann.imv.view.renderer.ListCellRendererCategories;
import de.elmar_baumann.imv.view.renderer.ListCellRendererFavoriteDirectories;
import de.elmar_baumann.imv.view.renderer.ListCellRendererImageCollections;
import de.elmar_baumann.imv.view.renderer.ListCellRendererKeywords;
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
import javax.swing.JSplitPane;
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
public class AppPanel extends javax.swing.JPanel implements AppExitListener {

    private List<JTable> xmpTables = new ArrayList<JTable>();
    private List<JTable> metadataTables = new ArrayList<JTable>();
    private List<JTree> selectionTrees = new ArrayList<JTree>();
    private List<JList> selectionLists = new ArrayList<JList>();
    private EditMetadataPanelsArray editPanelsArray;
    private EditMetadataActionsPanel editActionsPanel;
    private static final String keyDividerLocationMain = "AppPanel.DividerLocationMain";
    private static final String keyDividerLocationThumbnails = "AppPanel.DividerLocationThumbnails";
    private static final int minDividerLocationMain = 100;
    private static final int minDividerLocationThumbnails = 200;

    public AppPanel() {
        Panels.getInstance().setAppPanel(this);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        editPanelsArray = new EditMetadataPanelsArray(panelEditMetadata);
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

    public EditMetadataPanelsArray getMetadataEditPanelsArray() {
        return editPanelsArray;
    }

    public EditMetadataActionsPanel getMetadataEditActionsPanel() {
        if (editActionsPanel == null) {
            editActionsPanel = new EditMetadataActionsPanel();
        }
        return editActionsPanel;
    }

    public JScrollPane getScrollPaneThumbnailsPanel() {
        return scrollPaneThumbnails;
    }

    public JSlider getSliderThumbnailSize() {
        return sliderThumbnailSize;
    }

    public JTabbedPane getTabbedPaneMetadata() {
        return tabbedPaneMetadata;
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
        return panelTabEditMetadata;
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

    public JPanel getTabSelectionKeywords() {
        return panelKeywords;
    }

    public JPanel getTabSelectionCategories() {
        return panelSelectionCategories;
    }

    public JProgressBar getProgressBarCreateMetadataOfCurrentThumbnails() {
        return progressBarCreateMetadataOfCurrentThumbnails;
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

    public JList getListKeywords() {
        return listKeywords;
    }

    public JButton getButtonLogfileDialog() {
        return buttonLogfileDialog;
    }

    public JButton getButtonSaveMetadata() {
        return editActionsPanel.buttonSaveMetadata;
    }

    public JButton getButtonEmptyMetadata() {
        return editActionsPanel.buttonEmptyMetadata;
    }

    public JButton getButtonMetadataTemplateCreate() {
        return editActionsPanel.buttonMetadataTemplateCreate;
    }

    public JButton getButtonMetadataTemplateUpdate() {
        return editActionsPanel.buttonMetadataTemplateUpdate;
    }

    public JButton getButtonMetadataTemplateRename() {
        return editActionsPanel.buttonMetadataTemplateRename;
    }

    public JButton getButtonMetadataTemplateInsert() {
        return editActionsPanel.buttonMetadataTemplateInsert;
    }

    public JButton getButtonMetadataTemplateDelete() {
        return editActionsPanel.buttonMetadataTemplateDelete;
    }

    public JButton getButtonStopScheduledTasks() {
        return buttonStopScheduledTasks;
    }

    public JComboBox getComboBoxMetadataTemplates() {
        return editActionsPanel.comboBoxMetadataTemplates;
    }

    public JLabel getLabelMetadataInfoEditable() {
        return editActionsPanel.labelMetadataInfoEditable;
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

    public EditMetadataPanelsArray getEditPanelsArray() {
        return editPanelsArray;
    }

    public List<JTable> getMetadataTables() {
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
        initMetadataTablesArray();
    }

    private void initMetadataTablesArray() {
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
        selectionLists.add(listKeywords);
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

    public JSplitPane getSplitPaneThumbnailsMetadata() {
        return splitPaneThumbnailsMetadata;
    }

    @Override
    public void appWillExit() {
        PersistentSettings settings = PersistentSettings.getInstance();
        settings.setComponent(this, getPersistentSettingsHints());
        int dividerLocationThumbnails = splitPaneThumbnailsMetadata.getDividerLocation();
        int dividerLocationMain = splitPaneMain.getDividerLocation();
        settings.setInt(dividerLocationMain, keyDividerLocationMain);
        settings.setInt(dividerLocationThumbnails, keyDividerLocationThumbnails);
        ViewUtil.writePersistentTreeDirectories();
    }

    public PersistentSettingsHints getPersistentSettingsHints() {
        PersistentSettingsHints hints = new PersistentSettingsHints();
        String className = getClass().getName();
        hints.addExcludedMember(className + ".textFieldSearch"); // NOI18N
        hints.addExcludedMember(className + ".panelEditMetadata"); // NOI18N
        return hints;
    }

    private int getDividerLocationThumbnails() {
        int location = PersistentSettings.getInstance().getInt(keyDividerLocationThumbnails);
        return location > minDividerLocationThumbnails ? location : minDividerLocationThumbnails;
    }

    private int getDividerLocationMain() {
        int location = PersistentSettings.getInstance().getInt(keyDividerLocationMain);
        return location > minDividerLocationMain ? location : minDividerLocationMain;
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
        splitPaneMain.setDividerLocation(getDividerLocationMain());
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
        panelKeywords = new javax.swing.JPanel();
        scrollPaneKeywords = new javax.swing.JScrollPane();
        listKeywords = new javax.swing.JList();
        panelThumbnailsMetadata = new javax.swing.JPanel();
        splitPaneThumbnailsMetadata = new javax.swing.JSplitPane();
        splitPaneThumbnailsMetadata.setDividerLocation(getDividerLocationThumbnails());
        panelThumbnailsContent = new javax.swing.JPanel();
        scrollPaneThumbnails = new javax.swing.JScrollPane();
        panelThumbnails = new de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel();
        panelMetadata = new javax.swing.JPanel();
        labelMetadataFilename = new javax.swing.JLabel();
        tabbedPaneMetadata = new javax.swing.JTabbedPane();
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
        panelTabEditMetadata = new javax.swing.JPanel();
        panelScrollPaneEditMetadata = new javax.swing.JPanel();
        scrollPaneEditMetadata = new javax.swing.JScrollPane();
        panelEditMetadata = new javax.swing.JPanel();
        panelStatusbar = new javax.swing.JPanel();
        sliderThumbnailSize = new javax.swing.JSlider();
        buttonLogfileDialog = new javax.swing.JButton();
        labelStatusbar = new javax.swing.JLabel();
        progressBarCreateMetadataOfCurrentThumbnails = new javax.swing.JProgressBar();
        progressBarCurrentTasks = new javax.swing.JProgressBar();
        textFieldSearch = new javax.swing.JTextField();
        panelMetadataProgress = new javax.swing.JPanel();
        progressBarScheduledTasks = new javax.swing.JProgressBar();
        buttonStopScheduledTasks = new javax.swing.JButton();
        labelFastSearch = new javax.swing.JLabel();

        treeDirectories.setCellRenderer(new TreeCellRendererDirectories());
        treeDirectories.setDragEnabled(true);
        treeDirectories.setModel(null);
        scrollPaneDirectories.setViewportView(treeDirectories);
        treeDirectories.setTransferHandler(new de.elmar_baumann.imv.datatransfer.TransferHandlerTreeDirectories());

        javax.swing.GroupLayout panelSelectionDirectoriesLayout = new javax.swing.GroupLayout(panelSelectionDirectories);
        panelSelectionDirectories.setLayout(panelSelectionDirectoriesLayout);
        panelSelectionDirectoriesLayout.setHorizontalGroup(
            panelSelectionDirectoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneDirectories, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
        );
        panelSelectionDirectoriesLayout.setVerticalGroup(
            panelSelectionDirectoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneDirectories, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
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
            .addComponent(scrollPaneSavedSearches, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
        );

        tabbedPaneSelection.addTab(Bundle.getString("AppPanel.panelSelectionSavedSearches.TabConstraints.tabTitle"), panelSelectionSavedSearches); // NOI18N

        listImageCollections.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listImageCollections.setCellRenderer(new ListCellRendererImageCollections());
        listImageCollections.setDragEnabled(true);
        scrollPaneImageCollections.setViewportView(listImageCollections);
        listImageCollections.setTransferHandler(new de.elmar_baumann.imv.datatransfer.TransferHandlerListImageCollections());

        javax.swing.GroupLayout panelSelectionImageCollectionsLayout = new javax.swing.GroupLayout(panelSelectionImageCollections);
        panelSelectionImageCollections.setLayout(panelSelectionImageCollectionsLayout);
        panelSelectionImageCollectionsLayout.setHorizontalGroup(
            panelSelectionImageCollectionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneImageCollections, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
        );
        panelSelectionImageCollectionsLayout.setVerticalGroup(
            panelSelectionImageCollectionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneImageCollections, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
        );

        tabbedPaneSelection.addTab(Bundle.getString("AppPanel.panelSelectionImageCollections.TabConstraints.tabTitle"), panelSelectionImageCollections); // NOI18N

        listCategories.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listCategories.setCellRenderer(new ListCellRendererCategories());
        listCategories.setDropMode(javax.swing.DropMode.ON);
        scrollPaneCategories.setViewportView(listCategories);

        javax.swing.GroupLayout panelSelectionCategoriesLayout = new javax.swing.GroupLayout(panelSelectionCategories);
        panelSelectionCategories.setLayout(panelSelectionCategoriesLayout);
        panelSelectionCategoriesLayout.setHorizontalGroup(
            panelSelectionCategoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneCategories, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
        );
        panelSelectionCategoriesLayout.setVerticalGroup(
            panelSelectionCategoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneCategories, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
        );

        tabbedPaneSelection.addTab(Bundle.getString("AppPanel.panelSelectionCategories.TabConstraints.tabTitle"), panelSelectionCategories); // NOI18N

        listFavoriteDirectories.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listFavoriteDirectories.setCellRenderer(new ListCellRendererFavoriteDirectories());
        listFavoriteDirectories.setDragEnabled(true);
        listFavoriteDirectories.setDropMode(javax.swing.DropMode.ON);
        scrollPaneFavoriteDirectories.setViewportView(listFavoriteDirectories);
        listFavoriteDirectories.setTransferHandler(new de.elmar_baumann.imv.datatransfer.TransferHandlerListFavoriteDirectories());

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
            .addGap(0, 293, Short.MAX_VALUE)
            .addGroup(panelSelectionFavoriteDirectoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrollPaneFavoriteDirectories, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE))
        );

        tabbedPaneSelection.addTab(Bundle.getString("AppPanel.panelSelectionFavoriteDirectories.TabConstraints.tabTitle"), panelSelectionFavoriteDirectories); // NOI18N

        listKeywords.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listKeywords.setCellRenderer(new ListCellRendererKeywords());
        scrollPaneKeywords.setViewportView(listKeywords);

        javax.swing.GroupLayout panelKeywordsLayout = new javax.swing.GroupLayout(panelKeywords);
        panelKeywords.setLayout(panelKeywordsLayout);
        panelKeywordsLayout.setHorizontalGroup(
            panelKeywordsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 130, Short.MAX_VALUE)
            .addGroup(panelKeywordsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrollPaneKeywords, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE))
        );
        panelKeywordsLayout.setVerticalGroup(
            panelKeywordsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 293, Short.MAX_VALUE)
            .addGroup(panelKeywordsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrollPaneKeywords, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE))
        );

        tabbedPaneSelection.addTab(Bundle.getString("AppPanel.panelKeywords.TabConstraints.tabTitle"), panelKeywords); // NOI18N

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
            .addGap(0, 410, Short.MAX_VALUE)
            .addGroup(panelSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(tabbedPaneSelection, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE))
        );

        splitPaneMain.setLeftComponent(panelSelection);

        javax.swing.GroupLayout panelThumbnailsLayout = new javax.swing.GroupLayout(panelThumbnails);
        panelThumbnails.setLayout(panelThumbnailsLayout);
        panelThumbnailsLayout.setHorizontalGroup(
            panelThumbnailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 19, Short.MAX_VALUE)
        );
        panelThumbnailsLayout.setVerticalGroup(
            panelThumbnailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 405, Short.MAX_VALUE)
        );

        scrollPaneThumbnails.setViewportView(panelThumbnails);

        javax.swing.GroupLayout panelThumbnailsContentLayout = new javax.swing.GroupLayout(panelThumbnailsContent);
        panelThumbnailsContent.setLayout(panelThumbnailsContentLayout);
        panelThumbnailsContentLayout.setHorizontalGroup(
            panelThumbnailsContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 22, Short.MAX_VALUE)
            .addGroup(panelThumbnailsContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrollPaneThumbnails, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE))
        );
        panelThumbnailsContentLayout.setVerticalGroup(
            panelThumbnailsContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 408, Short.MAX_VALUE)
            .addGroup(panelThumbnailsContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrollPaneThumbnails, javax.swing.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE))
        );

        splitPaneThumbnailsMetadata.setLeftComponent(panelThumbnailsContent);

        labelMetadataFilename.setBackground(new java.awt.Color(255, 255, 255));
        labelMetadataFilename.setFont(new java.awt.Font("Dialog", 0, 10));
        labelMetadataFilename.setText(Bundle.getString("AppPanel.labelMetadataFilename.text")); // NOI18N
        labelMetadataFilename.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelMetadataFilename.setOpaque(true);

        tabbedPaneMetadata.setOpaque(true);

        tableIptc.setAutoCreateRowSorter(true);
        tableIptc.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        scrollPaneIptc.setViewportView(tableIptc);

        tabbedPaneMetadata.addTab(Bundle.getString("AppPanel.scrollPaneIptc.TabConstraints.tabTitle"), scrollPaneIptc); // NOI18N

        tableExif.setAutoCreateRowSorter(true);
        tableExif.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        scrollPaneExif.setViewportView(tableExif);

        tabbedPaneMetadata.addTab(Bundle.getString("AppPanel.scrollPaneExif.TabConstraints.tabTitle"), scrollPaneExif); // NOI18N

        tabbedPaneXmp.setOpaque(true);

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

        tabbedPaneMetadata.addTab(Bundle.getString("AppPanel.tabbedPaneXmp.TabConstraints.tabTitle"), tabbedPaneXmp); // NOI18N

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

        javax.swing.GroupLayout panelScrollPaneEditMetadataLayout = new javax.swing.GroupLayout(panelScrollPaneEditMetadata);
        panelScrollPaneEditMetadata.setLayout(panelScrollPaneEditMetadataLayout);
        panelScrollPaneEditMetadataLayout.setHorizontalGroup(
            panelScrollPaneEditMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneEditMetadata, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
        );
        panelScrollPaneEditMetadataLayout.setVerticalGroup(
            panelScrollPaneEditMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneEditMetadata, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelTabEditMetadataLayout = new javax.swing.GroupLayout(panelTabEditMetadata);
        panelTabEditMetadata.setLayout(panelTabEditMetadataLayout);
        panelTabEditMetadataLayout.setHorizontalGroup(
            panelTabEditMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelScrollPaneEditMetadata, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelTabEditMetadataLayout.setVerticalGroup(
            panelTabEditMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelScrollPaneEditMetadata, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        tabbedPaneMetadata.addTab(Bundle.getString("AppPanel.panelTabEditMetadata.TabConstraints.tabTitle"), panelTabEditMetadata); // NOI18N

        javax.swing.GroupLayout panelMetadataLayout = new javax.swing.GroupLayout(panelMetadata);
        panelMetadata.setLayout(panelMetadataLayout);
        panelMetadataLayout.setHorizontalGroup(
            panelMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labelMetadataFilename, javax.swing.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
            .addComponent(tabbedPaneMetadata, javax.swing.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
        );
        panelMetadataLayout.setVerticalGroup(
            panelMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMetadataLayout.createSequentialGroup()
                .addComponent(labelMetadataFilename)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabbedPaneMetadata, javax.swing.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE))
        );

        splitPaneThumbnailsMetadata.setRightComponent(panelMetadata);

        javax.swing.GroupLayout panelThumbnailsMetadataLayout = new javax.swing.GroupLayout(panelThumbnailsMetadata);
        panelThumbnailsMetadata.setLayout(panelThumbnailsMetadataLayout);
        panelThumbnailsMetadataLayout.setHorizontalGroup(
            panelThumbnailsMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 378, Short.MAX_VALUE)
            .addGroup(panelThumbnailsMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(splitPaneThumbnailsMetadata, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE))
        );
        panelThumbnailsMetadataLayout.setVerticalGroup(
            panelThumbnailsMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 410, Short.MAX_VALUE)
            .addGroup(panelThumbnailsMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(splitPaneThumbnailsMetadata, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE))
        );

        splitPaneMain.setRightComponent(panelThumbnailsMetadata);

        sliderThumbnailSize.setMajorTickSpacing(10);
        sliderThumbnailSize.setMinimum(10);
        sliderThumbnailSize.setSnapToTicks(true);
        sliderThumbnailSize.setToolTipText(Bundle.getString("AppPanel.sliderThumbnailSize.toolTipText")); // NOI18N

        buttonLogfileDialog.setToolTipText(Bundle.getString("AppPanel.buttonLogfileDialog.toolTipText")); // NOI18N
        buttonLogfileDialog.setBorder(null);
        buttonLogfileDialog.setPreferredSize(new java.awt.Dimension(16, 16));

        labelStatusbar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        progressBarCreateMetadataOfCurrentThumbnails.setFont(new java.awt.Font("Dialog", 1, 8)); // NOI18N
        progressBarCreateMetadataOfCurrentThumbnails.setToolTipText(AppSettings.tooltipTextProgressBarDirectory);

        progressBarCurrentTasks.setToolTipText(AppSettings.tooltipTextProgressBarCurrentTasks);

        textFieldSearch.setToolTipText(Bundle.getString("AppPanel.textFieldSearch.toolTipText")); // NOI18N

        progressBarScheduledTasks.setToolTipText(AppSettings.tooltipTextProgressBarScheduledTasks);

        buttonStopScheduledTasks.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icon_stop_scheduled_tasks_enabled.png"))); // NOI18N
        buttonStopScheduledTasks.setAlignmentY(0.0F);
        buttonStopScheduledTasks.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(175, 175, 175)));
        buttonStopScheduledTasks.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icon_stop_scheduled_tasks_disabled.png"))); // NOI18N
        buttonStopScheduledTasks.setEnabled(false);
        buttonStopScheduledTasks.setMaximumSize(new java.awt.Dimension(16, 16));
        buttonStopScheduledTasks.setMinimumSize(new java.awt.Dimension(16, 16));
        buttonStopScheduledTasks.setPreferredSize(new java.awt.Dimension(16, 16));

        javax.swing.GroupLayout panelMetadataProgressLayout = new javax.swing.GroupLayout(panelMetadataProgress);
        panelMetadataProgress.setLayout(panelMetadataProgressLayout);
        panelMetadataProgressLayout.setHorizontalGroup(
            panelMetadataProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMetadataProgressLayout.createSequentialGroup()
                .addComponent(progressBarScheduledTasks, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonStopScheduledTasks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        panelMetadataProgressLayout.setVerticalGroup(
            panelMetadataProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMetadataProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(buttonStopScheduledTasks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(progressBarScheduledTasks, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        labelFastSearch.setText(Bundle.getString("AppPanel.labelFastSearch.text")); // NOI18N

        javax.swing.GroupLayout panelStatusbarLayout = new javax.swing.GroupLayout(panelStatusbar);
        panelStatusbar.setLayout(panelStatusbarLayout);
        panelStatusbarLayout.setHorizontalGroup(
            panelStatusbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelStatusbarLayout.createSequentialGroup()
                .addGroup(panelStatusbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sliderThumbnailSize, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelStatusbar, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(panelStatusbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelStatusbarLayout.createSequentialGroup()
                        .addGap(59, 59, 59)
                        .addComponent(labelFastSearch))
                    .addGroup(panelStatusbarLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(buttonLogfileDialog, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelStatusbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(progressBarCreateMetadataOfCurrentThumbnails, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                    .addComponent(textFieldSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE))
                .addGap(6, 6, 6)
                .addGroup(panelStatusbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(progressBarCurrentTasks, 0, 105, Short.MAX_VALUE)
                    .addComponent(panelMetadataProgress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        panelStatusbarLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {labelStatusbar, sliderThumbnailSize});

        panelStatusbarLayout.setVerticalGroup(
            panelStatusbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStatusbarLayout.createSequentialGroup()
                .addComponent(panelMetadataProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelStatusbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(progressBarCreateMetadataOfCurrentThumbnails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(progressBarCurrentTasks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelStatusbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(buttonLogfileDialog, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(labelStatusbar, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))))
            .addComponent(sliderThumbnailSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(panelStatusbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(textFieldSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(labelFastSearch))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(splitPaneMain, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE)
                    .addComponent(panelStatusbar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(splitPaneMain, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelStatusbar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonLogfileDialog;
    private javax.swing.JButton buttonStopScheduledTasks;
    private javax.swing.JLabel labelFastSearch;
    private javax.swing.JLabel labelMetadataFilename;
    private javax.swing.JLabel labelStatusbar;
    private javax.swing.JList listCategories;
    private javax.swing.JList listFavoriteDirectories;
    private javax.swing.JList listImageCollections;
    private javax.swing.JList listKeywords;
    private javax.swing.JList listSavedSearches;
    private javax.swing.JPanel panelEditMetadata;
    private javax.swing.JPanel panelKeywords;
    private javax.swing.JPanel panelMetadata;
    private javax.swing.JPanel panelMetadataProgress;
    private javax.swing.JPanel panelScrollPaneEditMetadata;
    private javax.swing.JPanel panelSelection;
    private javax.swing.JPanel panelSelectionCategories;
    private javax.swing.JPanel panelSelectionDirectories;
    private javax.swing.JPanel panelSelectionFavoriteDirectories;
    private javax.swing.JPanel panelSelectionImageCollections;
    private javax.swing.JPanel panelSelectionSavedSearches;
    private javax.swing.JPanel panelStatusbar;
    private javax.swing.JPanel panelTabEditMetadata;
    private de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel panelThumbnails;
    private javax.swing.JPanel panelThumbnailsContent;
    private javax.swing.JPanel panelThumbnailsMetadata;
    private javax.swing.JProgressBar progressBarCreateMetadataOfCurrentThumbnails;
    private javax.swing.JProgressBar progressBarCurrentTasks;
    private javax.swing.JProgressBar progressBarScheduledTasks;
    private javax.swing.JScrollPane scrollPaneCategories;
    private javax.swing.JScrollPane scrollPaneDirectories;
    private javax.swing.JScrollPane scrollPaneEditMetadata;
    private javax.swing.JScrollPane scrollPaneExif;
    private javax.swing.JScrollPane scrollPaneFavoriteDirectories;
    private javax.swing.JScrollPane scrollPaneImageCollections;
    private javax.swing.JScrollPane scrollPaneIptc;
    private javax.swing.JScrollPane scrollPaneKeywords;
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
    private javax.swing.JTabbedPane tabbedPaneMetadata;
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
