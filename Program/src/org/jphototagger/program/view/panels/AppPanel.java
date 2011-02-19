package org.jphototagger.program.view.panels;

import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.model.ComboBoxModelFastSearch;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.renderer.ListCellRendererFastSearchColumns;
import org.jphototagger.program.view.renderer.ListCellRendererImageCollections;
import org.jphototagger.program.view.renderer.ListCellRendererKeywords;
import org.jphototagger.program.view.renderer.ListCellRendererSavedSearches;
import org.jphototagger.program.view.renderer.TreeCellRendererMiscMetadata;
import org.jphototagger.program.view.renderer.TreeCellRendererTimeline;
import org.jphototagger.lib.component.ImageTextArea;
import org.jphototagger.lib.componentutil.MessageLabel;
import org.jphototagger.lib.componentutil.MnemonicUtil;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.tree.TreeSelectionModel;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.datatransfer.TransferHandlerKeywordsList;
import org.jphototagger.program.datatransfer.TransferHandlerKeywordsTree;
import org.jphototagger.program.datatransfer.TransferHandlerMiscMetadataTree;
import org.jphototagger.program.model.ListModelWait;
import org.jphototagger.program.model.TreeModelWait;
import org.jphototagger.program.view.renderer.ListCellRendererFileFilters;

/**
 * Panel der Anwendung.
 *
 * @author Elmar Baumann, Tobias Stening
 */
public final class AppPanel extends javax.swing.JPanel {
    private static final String KEY_DIVIDER_LOCATION_MAIN =
        "AppPanel.DividerLocationMain";
    private static final String KEY_DIVIDER_LOCATION_THUMBNAILS =
        "AppPanel.DividerLocationThumbnails";
    private static final int                   DEFAULT_DIVIDER_LOCATION_MAIN       =
        100;
    private static final int                   DEFAULT_DIVIDER_LOCATION_THUMBNAILS =
        200;
    private static final long                  serialVersionUID                    =
        -7555272441595172631L;
    private final transient MessageLabel       messageLabel;
    private final List<JTable>                 xmpTables      =
        new ArrayList<JTable>();
    private final List<JTable>                 metadataTables =
        new ArrayList<JTable>();
    private final List<JTree>                  selectionTrees =
        new ArrayList<JTree>();
    private final List<JList>                  selectionLists =
        new ArrayList<JList>();
    private transient EditMetadataPanels       editMetadtaPanels;
    private transient EditMetadataActionsPanel panelEditActions;

    public AppPanel() {
        initComponents();
        messageLabel = new MessageLabel(labelStatusbarText);
        GUI.setAppPanel(this);
        postInitComponents();
    }

    private void postInitComponents() {
        displaySearchButton();
        editMetadtaPanels = new EditMetadataPanels(panelEditMetadata);
        panelThumbnails.setViewport(scrollPaneThumbnails.getViewport());
        setBackgroundColorToTablesViewports();
        setTreesSingleSelection();
        initCollections();
        scrollPaneThumbnails.getVerticalScrollBar().setUnitIncrement(30);
        setMnemonics();
    }

    private void setMnemonics() {

        // Do not set mnemonics to left panel because it can trigger edit actions!
        MnemonicUtil.setMnemonics((Container) panelExif);
        MnemonicUtil.setMnemonics((Container) panelIptc);
        MnemonicUtil.setMnemonics((Container) panelSearch);
    }

    private void displaySearchButton() {
        if (!UserSettings.INSTANCE.isDisplaySearchButton()) {
            panelSearch.remove(buttonSearch);
        }
    }

    private void initCollections() {
        initTablesCollection();
        initSelectionTreesCollection();
        initSelectionListsCollection();
    }

    private void initTablesCollection() {
        initXmpTablesCollection();
        initMetadataTablesCollection();
    }

    private void initMetadataTablesCollection() {
        metadataTables.addAll(xmpTables);
        metadataTables.add(tableExif);
        metadataTables.add(tableIptc);
    }

    private void initXmpTablesCollection() {
        xmpTables.add(tableXmpCameraRawSettings);
        xmpTables.add(tableXmpDc);
        xmpTables.add(tableXmpExif);
        xmpTables.add(tableXmpIptc);
        xmpTables.add(tableXmpLightroom);
        xmpTables.add(tableXmpPhotoshop);
        xmpTables.add(tableXmpTiff);
        xmpTables.add(tableXmpXap);
    }

    private void initSelectionTreesCollection() {
        selectionTrees.add(treeDirectories);
        selectionTrees.add(treeFavorites);
        selectionTrees.add(treeMiscMetadata);
        selectionTrees.add(treeTimeline);
        selectionTrees.add(treeSelKeywords);
    }

    private void initSelectionListsCollection() {
        selectionLists.add(listImageCollections);
        selectionLists.add(listSelKeywords);
        selectionLists.add(listSavedSearches);
        selectionLists.add(listNoMetadata);
    }

    private void setBackgroundColorToTablesViewports() {
        for (JTable table : metadataTables) {
            Container container = table.getParent();

            if (container instanceof JViewport) {
                JViewport viewport = (JViewport) container;

                viewport.setBackground(table.getBackground());
            }
        }
    }

    private int getDividerLocationMain() {
        int location = UserSettings.INSTANCE.getSettings().getInt(
                           KEY_DIVIDER_LOCATION_MAIN);

        return (location >= 0)
               ? location
               : DEFAULT_DIVIDER_LOCATION_MAIN;
    }

    private void setTreesSingleSelection() {
        setSingleSelection(treeDirectories);
        setSingleSelection(treeFavorites);
        setSingleSelection(treeMiscMetadata);
        setSingleSelection(treeTimeline);
        setSingleSelection(treeSelKeywords);
    }

    private void setSingleSelection(JTree tree) {
        tree.getSelectionModel().setSelectionMode(
            TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

    public enum SelectAlso { NOTHING_ELSE, SEL_KEYWORDS_TAB }

    public void displaySelKeywordsTree(SelectAlso select) {
        if (select == null) {
            throw new NullPointerException("select == null");
        }

        if (select.equals(SelectAlso.SEL_KEYWORDS_TAB)) {
            tabbedPaneSelection.setSelectedComponent(panelSelKeywords);
        }

        displaySelKeywordsCard("keywordsTree");
    }

    public void displaySelKeywordsList(SelectAlso select) {
        if (select == null) {
            throw new NullPointerException("select == null");
        }

        if (select.equals(SelectAlso.SEL_KEYWORDS_TAB)) {
            tabbedPaneSelection.setSelectedComponent(panelSelKeywords);
        }

        displaySelKeywordsCard("flatKeywords");
    }

    private void displaySelKeywordsCard(String name) {
        CardLayout cl = (CardLayout) (panelSelKeywords.getLayout());

        cl.show(panelSelKeywords, name);
    }

    private int getDividerLocationThumbnails() {
        int location = UserSettings.INSTANCE.getSettings().getInt(
                           KEY_DIVIDER_LOCATION_THUMBNAILS);

        return (location >= 0)
               ? location
               : DEFAULT_DIVIDER_LOCATION_THUMBNAILS;
    }

    public void setEnabledIptcTab(boolean enabled) {
        int index = tabbedPaneMetadata.indexOfComponent(panelIptc);

        tabbedPaneMetadata.setEnabledAt(index, enabled);
        tabbedPaneMetadata.setToolTipTextAt(index, enabled
                ? ""
                : JptBundle.INSTANCE.getString(
                    "AppPanel.TabMetadataIptc.TooltipText.Disabled"));
        if (!enabled && index == tabbedPaneMetadata.getSelectedIndex()) {
            tabbedPaneMetadata.setSelectedIndex(0);
        }
    }

    /**
     * Returns components with mnemonics set that can interfer with the
     * edit metadata panel mnemonics.
     * <p>
     * Panels hiding the edit metadata panel are <em>not</em> included.
     *
     * @return components
     */
    public Component[] getMnemonizedComponents() {
        return new Component[] {
          buttonSearch,
          labelFileFilters
        };
    }

    /**
     * Sets text to display in the status bar.
     *
     * @param text         text to display or empty string to remove text
     * @param type         changes the text color (red on errors)
     * @param milliseconds if greater than zero the message will be deleted
     *                     automatically after that time
     */
    public void setStatusbarText(String text, MessageLabel.MessageType type,
                                 final long milliseconds) {
        if (text == null) {
            throw new NullPointerException("text == null");
        }

        if (type == null) {
            throw new NullPointerException("type == null");
        }

        if (milliseconds > 0) {
            messageLabel.showMessage(text, type, milliseconds);
        } else {
            labelStatusbarText.setText(text);
        }

        AppLogger.logInfo(AppPanel.class, AppLogger.USE_STRING, text);
    }

    public EditMetadataPanels getEditMetadataPanels() {
        return editMetadtaPanels;
    }

    public EditMetadataActionsPanel getPanelEditMetadataActions() {
        if (panelEditActions == null) {
            panelEditActions = new EditMetadataActionsPanel();
        }

        return panelEditActions;
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
        return panelIptc;
    }

    public Component getTabMetadataExif() {
        return panelExif;
    }

    public Component getTabMetadataXmp() {
        return tabbedPaneXmp;
    }

    public Component getTabEditKeywords() {
        return panelEditKeywords;
    }

    public Component getTabSelectionKeywords() {
        return panelSelKeywords;
    }

    public Component getCardSelKeywordsList() {
        return panelSelKeywordsList;
    }

    public Component getCardSelKeywordsTree() {
        return panelSelKeywordsTree;
    }

    public Component getTabMetadataEdit() {
        return panelTabEditMetadata;
    }

    public Component getTabSelectionDirectories() {
        return panelDirectories;
    }

    public Component getTabSelectionFavoriteDirectories() {
        return panelFavorites;
    }

    public Component getTabSelectionImageCollections() {
        return panelImageCollections;
    }

    public Component getTabSelectionTimeline() {
        return panelTimeline;
    }

    public Component getTabSelectionMiscMetadata() {
        return panelMiscMetadata;
    }

    public JComboBox getComboBoxFileFilters() {
        return comboBoxFileFilters;
    }

    public Component getTabSelectionNoMetadata() {
        return panelNoMetadata;
    }

    public JPanel getTabSelectionSavedSearches() {
        return panelSavedSearches;
    }

    public JTree getTreeTimeline() {
        return treeTimeline;
    }

    public JTree getTreeMiscMetadata() {
        return treeMiscMetadata;
    }

    public JTree getTreeEditKeywords() {
        return panelEditKeywords.getTree();
    }

    public JTree getTreeSelKeywords() {
        return treeSelKeywords;
    }

    JProgressBar getProgressBar() {
        return progressBar;
    }

    public List<JTree> getSelectionTrees() {
        return Collections.unmodifiableList(selectionTrees);
    }

    public List<JList> getSelectionLists() {
        return Collections.unmodifiableList(selectionLists);
    }

    public ThumbnailsPanel getPanelThumbnails() {
        return panelThumbnails;
    }

    public KeywordsPanel getPanelEditKeywords() {
        return panelEditKeywords;
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

    public JTree getTreeFavorites() {
        return treeFavorites;
    }

    public JList getListSelKeywords() {
        return listSelKeywords;
    }

    public JList getListEditKeywords() {
        return panelEditKeywords.getList();
    }

    public JList getListNoMetadata() {
        return listNoMetadata;
    }

    public JButton getButtonEmptyMetadata() {
        return panelEditActions.buttonEmptyMetadata;
    }

    public JButton getButtonMetadataTemplateAdd() {
        return panelEditActions.buttonMetadataTemplateAdd;
    }

    public JButton getButtonMetadataTemplateCreate() {
        return panelEditActions.buttonMetadataTemplateCreate;
    }

    public JButton getButtonMetadataTemplateUpdate() {
        return panelEditActions.buttonMetadataTemplateUpdate;
    }

    public JButton getButtonMetadataTemplateRename() {
        return panelEditActions.buttonMetadataTemplateRename;
    }

    public JButton getButtonMetadataTemplateEdit() {
        return panelEditActions.buttonMetadataTemplateEdit;
    }

    public JButton getButtonMetadataTemplateInsert() {
        return panelEditActions.buttonMetadataTemplateInsert;
    }

    public JButton getButtonMetadataTemplateDelete() {
        return panelEditActions.buttonMetadataTemplateDelete;
    }

    public JButton getButtonIptcToXmp() {
        return buttonIptcToXmp;
    }

    public JButton getButtonExifToXmp() {
        return buttonExifToXmp;
    }

    public JButton getButtonSearch() {
        return buttonSearch;
    }

    JButton getButtonCancelProgress() {
        return buttonCancelProgress;
    }

    public JToggleButton getToggleButtonSelKeywords() {
        return toggleButtonExpandAllNodesSelKeywords;
    }

    public JComboBox getComboBoxMetadataTemplates() {
        return panelEditActions.comboBoxMetadataTemplates;
    }

    public JLabel getLabelMetadataInfoEditable() {
        return panelEditActions.labelMetadataInfoEditable;
    }

    public JLabel getLabelThumbnailInfo() {
        return labelThumbnailInfo;
    }

    public JLabel getLabelMetadataFilename() {
        return labelMetadataFilename;
    }

    public JTextArea getTextAreaSearch() {
        return textAreaSearch;
    }

    public List<JTable> getMetadataTables() {
        return Collections.unmodifiableList(metadataTables);
    }

    public List<JTable> getXmpTables() {
        return Collections.unmodifiableList(xmpTables);
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

    public JComboBox getComboBoxFastSearch() {
        return comboBoxFastSearch;
    }

    public JRadioButton getRadioButtonSelKeywordsMultipleSelAll() {
        return radioButtonSelKeywordsMultipleSelAll;
    }

    public JRadioButton getRadioButtonSelKeywordsMultipleSelOne() {
        return radioButtonSelKeywordsMultipleSelOne;
    }

    public JLabel getLabelError() {
        return labelError;
    }

    public JSplitPane getSplitPaneMain() {
        return splitPaneMain;
    }

    public JSplitPane getSplitPaneThumbnailsMetadata() {
        return splitPaneThumbnailsMetadata;
    }

    @SuppressWarnings("serial")

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroupKeywordsMultipleSel = new javax.swing.ButtonGroup();
        splitPaneMain = new javax.swing.JSplitPane();
        splitPaneMain.setDividerLocation(getDividerLocationMain());
        panelSelection = new javax.swing.JPanel();
        panelSearch = new javax.swing.JPanel();
        comboBoxFastSearch = new javax.swing.JComboBox();
        buttonSearch = new javax.swing.JButton();
        scrollPaneTextAreaSearch = new javax.swing.JScrollPane();
        textAreaSearch = new ImageTextArea();
        ((ImageTextArea) textAreaSearch).setImage(
            AppLookAndFeel.getLocalizedImage(
                "/org/jphototagger/program/resource/images/textfield_search.png"));
        ((ImageTextArea) textAreaSearch).setConsumeEnter(true);
        labelFileFilters = new javax.swing.JLabel();
        comboBoxFileFilters = new javax.swing.JComboBox();
        tabbedPaneSelection = new javax.swing.JTabbedPane();
        panelDirectories = new javax.swing.JPanel();
        scrollPaneDirectories = new javax.swing.JScrollPane();
        treeDirectories = new javax.swing.JTree();
        treeDirectories.setShowsRootHandles(true);
        panelSavedSearches = new javax.swing.JPanel();
        scrollPaneSavedSearches = new javax.swing.JScrollPane();
        listSavedSearches = new javax.swing.JList();
        panelImageCollections = new javax.swing.JPanel();
        scrollPaneImageCollections = new javax.swing.JScrollPane();
        listImageCollections = new javax.swing.JList();
        panelFavorites = new javax.swing.JPanel();
        scrollPaneFavorites = new javax.swing.JScrollPane();
        treeFavorites = new javax.swing.JTree();
        treeFavorites.setTransferHandler(new org.jphototagger.program.datatransfer.TransferHandlerDirectoryTree());
        treeFavorites.setShowsRootHandles(true);
        panelSelKeywords = new javax.swing.JPanel();
        panelSelKeywordsTree = new javax.swing.JPanel();
        scrollPaneSelKeywordsTree = new javax.swing.JScrollPane();
        treeSelKeywords = new javax.swing.JTree();
        treeSelKeywords.setTransferHandler(new TransferHandlerKeywordsTree());
        treeSelKeywords.setShowsRootHandles(true);
        buttonDisplaySelKeywordsList = new javax.swing.JButton();
        toggleButtonExpandAllNodesSelKeywords = new javax.swing.JToggleButton();
        panelSelKeywordsList = new javax.swing.JPanel();
        scrollPaneSelKeywordsList = new javax.swing.JScrollPane();
        listSelKeywords = new javax.swing.JList();
        listSelKeywords.setTransferHandler(new TransferHandlerKeywordsList());
        panelSelKeywordsListMultipleSelection = new javax.swing.JPanel();
        radioButtonSelKeywordsMultipleSelAll = new javax.swing.JRadioButton();
        radioButtonSelKeywordsMultipleSelOne = new javax.swing.JRadioButton();
        buttonDisplaySelKeywordsTree = new javax.swing.JButton();
        panelTimeline = new javax.swing.JPanel();
        scrollPaneTimeline = new javax.swing.JScrollPane();
        treeTimeline = new javax.swing.JTree();
        treeTimeline.setShowsRootHandles(true);
        panelMiscMetadata = new javax.swing.JPanel();
        scrollPaneMiscMetadata = new javax.swing.JScrollPane();
        treeMiscMetadata = new javax.swing.JTree();
        treeMiscMetadata.setTransferHandler(new TransferHandlerMiscMetadataTree());
        treeMiscMetadata.setShowsRootHandles(true);
        panelNoMetadata = new javax.swing.JPanel();
        scrollPaneNoMetadata = new javax.swing.JScrollPane();
        listNoMetadata = new javax.swing.JList();
        panelThumbnailsMetadata = new javax.swing.JPanel();
        splitPaneThumbnailsMetadata = new javax.swing.JSplitPane();
        splitPaneThumbnailsMetadata.setDividerLocation(getDividerLocationThumbnails());
        panelThumbnailsContent = new javax.swing.JPanel();
        scrollPaneThumbnails = new javax.swing.JScrollPane();
        panelThumbnails = new org.jphototagger.program.view.panels.ThumbnailsPanel();
        panelMetadata = new javax.swing.JPanel();
        labelMetadataFilename = new javax.swing.JLabel();
        tabbedPaneMetadata = new javax.swing.JTabbedPane();
        panelExif = new javax.swing.JPanel();
        scrollPaneExif = new javax.swing.JScrollPane();
        tableExif = new javax.swing.JTable();
        buttonExifToXmp = new javax.swing.JButton();
        panelIptc = new javax.swing.JPanel();
        scrollPaneIptc = new javax.swing.JScrollPane();
        tableIptc = new javax.swing.JTable();
        buttonIptcToXmp = new javax.swing.JButton();
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
        panelEditKeywords = new org.jphototagger.program.view.panels.KeywordsPanel();
        panelStatusbar = new javax.swing.JPanel();
        labelThumbnailInfo = new javax.swing.JLabel();
        labelStatusbarText = new javax.swing.JLabel();
        sliderThumbnailSize = new javax.swing.JSlider();
        labelError = new javax.swing.JLabel();
        buttonCancelProgress = new javax.swing.JButton();
        progressBar = new javax.swing.JProgressBar();

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        splitPaneMain.setDividerSize(6);
        splitPaneMain.setName("splitPaneMain"); // NOI18N
        splitPaneMain.setOneTouchExpandable(true);

        panelSelection.setName("panelSelection"); // NOI18N
        panelSelection.setLayout(new java.awt.GridBagLayout());

        panelSearch.setName("panelSearch"); // NOI18N
        panelSearch.setLayout(new java.awt.GridBagLayout());

        comboBoxFastSearch.setModel(new ComboBoxModelFastSearch());
        comboBoxFastSearch.setName("comboBoxFastSearch"); // NOI18N
        comboBoxFastSearch.setRenderer(new ListCellRendererFastSearchColumns());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panelSearch.add(comboBoxFastSearch, gridBagConstraints);

        buttonSearch.setText(JptBundle.INSTANCE.getString("AppPanel.buttonSearch.text")); // NOI18N
        buttonSearch.setMargin(new java.awt.Insets(0, 2, 0, 2));
        buttonSearch.setName("buttonSearch"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
        panelSearch.add(buttonSearch, gridBagConstraints);

        scrollPaneTextAreaSearch.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPaneTextAreaSearch.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPaneTextAreaSearch.setMinimumSize(new java.awt.Dimension(7, 20));
        scrollPaneTextAreaSearch.setName("scrollPaneTextAreaSearch"); // NOI18N

        textAreaSearch.setRows(1);
        textAreaSearch.setMinimumSize(new java.awt.Dimension(0, 18));
        textAreaSearch.setName("JPhotoTagger Fast Search Text Area"); // NOI18N
        scrollPaneTextAreaSearch.setViewportView(textAreaSearch);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        panelSearch.add(scrollPaneTextAreaSearch, gridBagConstraints);

        labelFileFilters.setLabelFor(comboBoxFileFilters);
        labelFileFilters.setText(JptBundle.INSTANCE.getString("AppPanel.labelFileFilters.text")); // NOI18N
        labelFileFilters.setName("labelFileFilters"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        panelSearch.add(labelFileFilters, gridBagConstraints);

        comboBoxFileFilters.setName("comboBoxFileFilters"); // NOI18N
        comboBoxFileFilters.setRenderer(new ListCellRendererFileFilters());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        panelSearch.add(comboBoxFileFilters, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panelSelection.add(panelSearch, gridBagConstraints);

        tabbedPaneSelection.setName("tabbedPaneSelection"); // NOI18N

        panelDirectories.setName("panelDirectories"); // NOI18N

        scrollPaneDirectories.setName("scrollPaneDirectories"); // NOI18N

        treeDirectories.setModel(TreeModelWait.INSTANCE);
        treeDirectories.setCellRenderer(new org.jphototagger.lib.renderer.TreeCellRendererAllSystemDirectories());
        treeDirectories.setDragEnabled(true);
        treeDirectories.setName("treeDirectories"); // NOI18N
        treeDirectories.setRootVisible(false);
        scrollPaneDirectories.setViewportView(treeDirectories);
        treeDirectories.setTransferHandler(new org.jphototagger.program.datatransfer.TransferHandlerDirectoryTree());
        treeDirectories.setShowsRootHandles(true);

        javax.swing.GroupLayout panelDirectoriesLayout = new javax.swing.GroupLayout(panelDirectories);
        panelDirectories.setLayout(panelDirectoriesLayout);
        panelDirectoriesLayout.setHorizontalGroup(
            panelDirectoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneDirectories, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
        );
        panelDirectoriesLayout.setVerticalGroup(
            panelDirectoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneDirectories, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
        );

        tabbedPaneSelection.addTab(JptBundle.INSTANCE.getString("AppPanel.panelDirectories.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_folder.png")), panelDirectories); // NOI18N

        panelSavedSearches.setName("panelSavedSearches"); // NOI18N

        scrollPaneSavedSearches.setName("scrollPaneSavedSearches"); // NOI18N

        listSavedSearches.setModel(ListModelWait.INSTANCE);
        listSavedSearches.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listSavedSearches.setCellRenderer(new ListCellRendererSavedSearches());
        listSavedSearches.setName("listSavedSearches"); // NOI18N
        scrollPaneSavedSearches.setViewportView(listSavedSearches);

        javax.swing.GroupLayout panelSavedSearchesLayout = new javax.swing.GroupLayout(panelSavedSearches);
        panelSavedSearches.setLayout(panelSavedSearchesLayout);
        panelSavedSearchesLayout.setHorizontalGroup(
            panelSavedSearchesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneSavedSearches, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
        );
        panelSavedSearchesLayout.setVerticalGroup(
            panelSavedSearchesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneSavedSearches, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
        );

        tabbedPaneSelection.addTab(JptBundle.INSTANCE.getString("AppPanel.panelSavedSearches.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_search.png")), panelSavedSearches); // NOI18N

        panelImageCollections.setName("panelImageCollections"); // NOI18N

        scrollPaneImageCollections.setName("scrollPaneImageCollections"); // NOI18N

        listImageCollections.setModel(ListModelWait.INSTANCE);
        listImageCollections.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listImageCollections.setCellRenderer(new ListCellRendererImageCollections());
        listImageCollections.setDragEnabled(true);
        listImageCollections.setName("listImageCollections"); // NOI18N
        scrollPaneImageCollections.setViewportView(listImageCollections);
        listImageCollections.setTransferHandler(new org.jphototagger.program.datatransfer.TransferHandlerImageCollectionsList());

        javax.swing.GroupLayout panelImageCollectionsLayout = new javax.swing.GroupLayout(panelImageCollections);
        panelImageCollections.setLayout(panelImageCollectionsLayout);
        panelImageCollectionsLayout.setHorizontalGroup(
            panelImageCollectionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneImageCollections, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
        );
        panelImageCollectionsLayout.setVerticalGroup(
            panelImageCollectionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneImageCollections, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
        );

        tabbedPaneSelection.addTab(JptBundle.INSTANCE.getString("AppPanel.panelImageCollections.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_imagecollection.png")), panelImageCollections); // NOI18N

        panelFavorites.setName("panelFavorites"); // NOI18N

        scrollPaneFavorites.setName("scrollPaneFavorites"); // NOI18N

        treeFavorites.setModel(TreeModelWait.INSTANCE);
        treeFavorites.setCellRenderer(new org.jphototagger.program.view.renderer.TreeCellRendererFavorites());
        treeFavorites.setDragEnabled(true);
        treeFavorites.setName("treeFavorites"); // NOI18N
        treeFavorites.setRootVisible(false);
        scrollPaneFavorites.setViewportView(treeFavorites);

        javax.swing.GroupLayout panelFavoritesLayout = new javax.swing.GroupLayout(panelFavorites);
        panelFavorites.setLayout(panelFavoritesLayout);
        panelFavoritesLayout.setHorizontalGroup(
            panelFavoritesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneFavorites, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
        );
        panelFavoritesLayout.setVerticalGroup(
            panelFavoritesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneFavorites, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
        );

        tabbedPaneSelection.addTab(JptBundle.INSTANCE.getString("AppPanel.panelFavorites.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_favorite.png")), panelFavorites); // NOI18N

        panelSelKeywords.setName("panelSelKeywords"); // NOI18N
        panelSelKeywords.setLayout(new java.awt.CardLayout());

        panelSelKeywordsTree.setName("panelSelKeywordsTree"); // NOI18N
        panelSelKeywordsTree.setLayout(new java.awt.GridBagLayout());

        scrollPaneSelKeywordsTree.setName("scrollPaneSelKeywordsTree"); // NOI18N

        treeSelKeywords.setModel(TreeModelWait.INSTANCE);
        treeSelKeywords.setCellRenderer(new org.jphototagger.program.view.renderer.TreeCellRendererKeywords());
        treeSelKeywords.setName("treeSelKeywords"); // NOI18N
        treeSelKeywords.setRootVisible(false);
        scrollPaneSelKeywordsTree.setViewportView(treeSelKeywords);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelSelKeywordsTree.add(scrollPaneSelKeywordsTree, gridBagConstraints);

        buttonDisplaySelKeywordsList.setText(JptBundle.INSTANCE.getString("AppPanel.buttonDisplaySelKeywordsList.text")); // NOI18N
        buttonDisplaySelKeywordsList.setMargin(new java.awt.Insets(1, 1, 1, 1));
        buttonDisplaySelKeywordsList.setName("buttonDisplaySelKeywordsList"); // NOI18N
        buttonDisplaySelKeywordsList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDisplaySelKeywordsListActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 1, 0);
        panelSelKeywordsTree.add(buttonDisplaySelKeywordsList, gridBagConstraints);

        toggleButtonExpandAllNodesSelKeywords.setText(JptBundle.INSTANCE.getString("AppPanel.toggleButtonExpandAllNodesSelKeywords.text")); // NOI18N
        toggleButtonExpandAllNodesSelKeywords.setMargin(new java.awt.Insets(1, 1, 1, 1));
        toggleButtonExpandAllNodesSelKeywords.setName("toggleButtonExpandAllNodesSelKeywords"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 1, 0);
        panelSelKeywordsTree.add(toggleButtonExpandAllNodesSelKeywords, gridBagConstraints);

        panelSelKeywords.add(panelSelKeywordsTree, "keywordsTree");

        panelSelKeywordsList.setName("panelSelKeywordsList"); // NOI18N
        panelSelKeywordsList.setLayout(new java.awt.GridBagLayout());

        scrollPaneSelKeywordsList.setName("scrollPaneSelKeywordsList"); // NOI18N

        listSelKeywords.setModel(ListModelWait.INSTANCE);
        listSelKeywords.setCellRenderer(new ListCellRendererKeywords());
        listSelKeywords.setDragEnabled(true);
        listSelKeywords.setName("listSelKeywords"); // NOI18N
        scrollPaneSelKeywordsList.setViewportView(listSelKeywords);
        listSelKeywords.setTransferHandler(new org.jphototagger.program.datatransfer.TransferHandlerKeywordsList());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelSelKeywordsList.add(scrollPaneSelKeywordsList, gridBagConstraints);

        panelSelKeywordsListMultipleSelection.setBorder(javax.swing.BorderFactory.createTitledBorder(JptBundle.INSTANCE.getString("AppPanel.panelSelKeywordsListMultipleSelection.border.title"))); // NOI18N
        panelSelKeywordsListMultipleSelection.setName("panelSelKeywordsListMultipleSelection"); // NOI18N
        panelSelKeywordsListMultipleSelection.setLayout(new java.awt.GridBagLayout());

        buttonGroupKeywordsMultipleSel.add(radioButtonSelKeywordsMultipleSelAll);
        radioButtonSelKeywordsMultipleSelAll.setText(JptBundle.INSTANCE.getString("AppPanel.radioButtonSelKeywordsMultipleSelAll.text")); // NOI18N
        radioButtonSelKeywordsMultipleSelAll.setName("radioButtonSelKeywordsMultipleSelAll"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        panelSelKeywordsListMultipleSelection.add(radioButtonSelKeywordsMultipleSelAll, gridBagConstraints);

        buttonGroupKeywordsMultipleSel.add(radioButtonSelKeywordsMultipleSelOne);
        radioButtonSelKeywordsMultipleSelOne.setText(JptBundle.INSTANCE.getString("AppPanel.radioButtonSelKeywordsMultipleSelOne.text")); // NOI18N
        radioButtonSelKeywordsMultipleSelOne.setName("radioButtonSelKeywordsMultipleSelOne"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 2, 0);
        panelSelKeywordsListMultipleSelection.add(radioButtonSelKeywordsMultipleSelOne, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 0);
        panelSelKeywordsList.add(panelSelKeywordsListMultipleSelection, gridBagConstraints);

        buttonDisplaySelKeywordsTree.setText(JptBundle.INSTANCE.getString("AppPanel.buttonDisplaySelKeywordsTree.text")); // NOI18N
        buttonDisplaySelKeywordsTree.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonDisplaySelKeywordsTree.setName("buttonDisplaySelKeywordsTree"); // NOI18N
        buttonDisplaySelKeywordsTree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDisplaySelKeywordsTreeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 2);
        panelSelKeywordsList.add(buttonDisplaySelKeywordsTree, gridBagConstraints);

        panelSelKeywords.add(panelSelKeywordsList, "flatKeywords");

        tabbedPaneSelection.addTab(JptBundle.INSTANCE.getString("AppPanel.panelSelKeywords.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_keyword.png")), panelSelKeywords); // NOI18N

        panelTimeline.setName("panelTimeline"); // NOI18N

        scrollPaneTimeline.setName("scrollPaneTimeline"); // NOI18N

        treeTimeline.setModel(TreeModelWait.INSTANCE);
        treeTimeline.setCellRenderer(new TreeCellRendererTimeline());
        treeTimeline.setName("treeTimeline"); // NOI18N
        treeTimeline.setRootVisible(false);
        scrollPaneTimeline.setViewportView(treeTimeline);

        javax.swing.GroupLayout panelTimelineLayout = new javax.swing.GroupLayout(panelTimeline);
        panelTimeline.setLayout(panelTimelineLayout);
        panelTimelineLayout.setHorizontalGroup(
            panelTimelineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneTimeline, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
        );
        panelTimelineLayout.setVerticalGroup(
            panelTimelineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneTimeline, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
        );

        tabbedPaneSelection.addTab(JptBundle.INSTANCE.getString("AppPanel.panelTimeline.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_timeline.png")), panelTimeline); // NOI18N

        panelMiscMetadata.setName("panelMiscMetadata"); // NOI18N

        scrollPaneMiscMetadata.setName("scrollPaneMiscMetadata"); // NOI18N

        treeMiscMetadata.setModel(TreeModelWait.INSTANCE);
        treeMiscMetadata.setCellRenderer(new TreeCellRendererMiscMetadata());
        treeMiscMetadata.setDragEnabled(true);
        treeMiscMetadata.setName("treeMiscMetadata"); // NOI18N
        treeMiscMetadata.setRootVisible(false);
        scrollPaneMiscMetadata.setViewportView(treeMiscMetadata);

        javax.swing.GroupLayout panelMiscMetadataLayout = new javax.swing.GroupLayout(panelMiscMetadata);
        panelMiscMetadata.setLayout(panelMiscMetadataLayout);
        panelMiscMetadataLayout.setHorizontalGroup(
            panelMiscMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneMiscMetadata, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
        );
        panelMiscMetadataLayout.setVerticalGroup(
            panelMiscMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneMiscMetadata, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
        );

        tabbedPaneSelection.addTab(JptBundle.INSTANCE.getString("AppPanel.panelMiscMetadata.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_misc_metadata.png")), panelMiscMetadata); // NOI18N

        panelNoMetadata.setName("panelNoMetadata"); // NOI18N

        scrollPaneNoMetadata.setName("scrollPaneNoMetadata"); // NOI18N

        listNoMetadata.setModel(ListModelWait.INSTANCE);
        listNoMetadata.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listNoMetadata.setCellRenderer(new org.jphototagger.program.view.renderer.ListCellRendererNoMetadata());
        listNoMetadata.setName("listNoMetadata"); // NOI18N
        scrollPaneNoMetadata.setViewportView(listNoMetadata);

        javax.swing.GroupLayout panelNoMetadataLayout = new javax.swing.GroupLayout(panelNoMetadata);
        panelNoMetadata.setLayout(panelNoMetadataLayout);
        panelNoMetadataLayout.setHorizontalGroup(
            panelNoMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 154, Short.MAX_VALUE)
            .addGroup(panelNoMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrollPaneNoMetadata, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE))
        );
        panelNoMetadataLayout.setVerticalGroup(
            panelNoMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 233, Short.MAX_VALUE)
            .addGroup(panelNoMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrollPaneNoMetadata, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE))
        );

        tabbedPaneSelection.addTab(JptBundle.INSTANCE.getString("AppPanel.panelNoMetadata.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_no_metadata.png")), panelNoMetadata); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 0, 0);
        panelSelection.add(tabbedPaneSelection, gridBagConstraints);

        splitPaneMain.setLeftComponent(panelSelection);

        panelThumbnailsMetadata.setName("panelThumbnailsMetadata"); // NOI18N

        splitPaneThumbnailsMetadata.setDividerSize(6);
        splitPaneThumbnailsMetadata.setResizeWeight(1.0);
        splitPaneThumbnailsMetadata.setName("splitPaneThumbnailsMetadata"); // NOI18N
        splitPaneThumbnailsMetadata.setOneTouchExpandable(true);

        panelThumbnailsContent.setMinimumSize(new java.awt.Dimension(180, 0));
        panelThumbnailsContent.setName("panelThumbnailsContent"); // NOI18N

        scrollPaneThumbnails.setName("scrollPaneThumbnails"); // NOI18N

        panelThumbnails.setName("panelThumbnails"); // NOI18N

        javax.swing.GroupLayout panelThumbnailsLayout = new javax.swing.GroupLayout(panelThumbnails);
        panelThumbnails.setLayout(panelThumbnailsLayout);
        panelThumbnailsLayout.setHorizontalGroup(
            panelThumbnailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 576, Short.MAX_VALUE)
        );
        panelThumbnailsLayout.setVerticalGroup(
            panelThumbnailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 460, Short.MAX_VALUE)
        );

        scrollPaneThumbnails.setViewportView(panelThumbnails);

        javax.swing.GroupLayout panelThumbnailsContentLayout = new javax.swing.GroupLayout(panelThumbnailsContent);
        panelThumbnailsContent.setLayout(panelThumbnailsContentLayout);
        panelThumbnailsContentLayout.setHorizontalGroup(
            panelThumbnailsContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 578, Short.MAX_VALUE)
            .addGroup(panelThumbnailsContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrollPaneThumbnails, javax.swing.GroupLayout.DEFAULT_SIZE, 578, Short.MAX_VALUE))
        );
        panelThumbnailsContentLayout.setVerticalGroup(
            panelThumbnailsContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 462, Short.MAX_VALUE)
            .addGroup(panelThumbnailsContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrollPaneThumbnails, javax.swing.GroupLayout.DEFAULT_SIZE, 462, Short.MAX_VALUE))
        );

        splitPaneThumbnailsMetadata.setLeftComponent(panelThumbnailsContent);

        panelMetadata.setName("panelMetadata"); // NOI18N

        labelMetadataFilename.setBackground(new java.awt.Color(255, 255, 255));
        labelMetadataFilename.setText(JptBundle.INSTANCE.getString("AppPanel.labelMetadataFilename.text")); // NOI18N
        labelMetadataFilename.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelMetadataFilename.setName("labelMetadataFilename"); // NOI18N
        labelMetadataFilename.setOpaque(true);

        tabbedPaneMetadata.setName("tabbedPaneMetadata"); // NOI18N
        tabbedPaneMetadata.setOpaque(true);

        panelExif.setName("panelExif"); // NOI18N
        panelExif.setLayout(new java.awt.GridBagLayout());

        scrollPaneExif.setName("scrollPaneExif"); // NOI18N

        tableExif.setAutoCreateRowSorter(true);
        tableExif.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableExif.setName("tableExif"); // NOI18N
        scrollPaneExif.setViewportView(tableExif);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelExif.add(scrollPaneExif, gridBagConstraints);

        buttonExifToXmp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_xmp.png"))); // NOI18N
        buttonExifToXmp.setText(JptBundle.INSTANCE.getString("AppPanel.buttonExifToXmp.text")); // NOI18N
        buttonExifToXmp.setToolTipText(JptBundle.INSTANCE.getString("AppPanel.buttonExifToXmp.toolTipText")); // NOI18N
        buttonExifToXmp.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonExifToXmp.setName("buttonExifToXmp"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
        panelExif.add(buttonExifToXmp, gridBagConstraints);

        tabbedPaneMetadata.addTab(JptBundle.INSTANCE.getString("AppPanel.panelExif.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_exif.png")), panelExif); // NOI18N

        panelIptc.setName("panelIptc"); // NOI18N
        panelIptc.setLayout(new java.awt.GridBagLayout());

        scrollPaneIptc.setName("scrollPaneIptc"); // NOI18N

        tableIptc.setAutoCreateRowSorter(true);
        tableIptc.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableIptc.setName("tableIptc"); // NOI18N
        scrollPaneIptc.setViewportView(tableIptc);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelIptc.add(scrollPaneIptc, gridBagConstraints);

        buttonIptcToXmp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_xmp.png"))); // NOI18N
        buttonIptcToXmp.setText(JptBundle.INSTANCE.getString("AppPanel.buttonIptcToXmp.text")); // NOI18N
        buttonIptcToXmp.setToolTipText(JptBundle.INSTANCE.getString("AppPanel.buttonIptcToXmp.toolTipText")); // NOI18N
        buttonIptcToXmp.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonIptcToXmp.setName("buttonIptcToXmp"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
        panelIptc.add(buttonIptcToXmp, gridBagConstraints);

        tabbedPaneMetadata.addTab(JptBundle.INSTANCE.getString("AppPanel.panelIptc.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_iptc.png")), panelIptc); // NOI18N

        tabbedPaneXmp.setName("tabbedPaneXmp"); // NOI18N
        tabbedPaneXmp.setOpaque(true);

        scrollPaneXmpTiff.setName("scrollPaneXmpTiff"); // NOI18N

        tableXmpTiff.setAutoCreateRowSorter(true);
        tableXmpTiff.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableXmpTiff.setName("tableXmpTiff"); // NOI18N
        scrollPaneXmpTiff.setViewportView(tableXmpTiff);

        tabbedPaneXmp.addTab(JptBundle.INSTANCE.getString("AppPanel.scrollPaneXmpTiff.TabConstraints.tabTitle"), scrollPaneXmpTiff); // NOI18N

        scrollPaneXmpExif.setName("scrollPaneXmpExif"); // NOI18N

        tableXmpExif.setAutoCreateRowSorter(true);
        tableXmpExif.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableXmpExif.setName("tableXmpExif"); // NOI18N
        scrollPaneXmpExif.setViewportView(tableXmpExif);

        tabbedPaneXmp.addTab(JptBundle.INSTANCE.getString("AppPanel.scrollPaneXmpExif.TabConstraints.tabTitle"), scrollPaneXmpExif); // NOI18N

        scrollPaneXmpDc.setName("scrollPaneXmpDc"); // NOI18N

        tableXmpDc.setAutoCreateRowSorter(true);
        tableXmpDc.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableXmpDc.setName("tableXmpDc"); // NOI18N
        scrollPaneXmpDc.setViewportView(tableXmpDc);

        tabbedPaneXmp.addTab(JptBundle.INSTANCE.getString("AppPanel.scrollPaneXmpDc.TabConstraints.tabTitle"), scrollPaneXmpDc); // NOI18N

        scrollPaneXmpIptc.setName("scrollPaneXmpIptc"); // NOI18N

        tableXmpIptc.setAutoCreateRowSorter(true);
        tableXmpIptc.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableXmpIptc.setName("tableXmpIptc"); // NOI18N
        scrollPaneXmpIptc.setViewportView(tableXmpIptc);

        tabbedPaneXmp.addTab(JptBundle.INSTANCE.getString("AppPanel.scrollPaneXmpIptc.TabConstraints.tabTitle"), scrollPaneXmpIptc); // NOI18N

        scrollPaneXmpPhotoshop.setName("scrollPaneXmpPhotoshop"); // NOI18N

        tableXmpPhotoshop.setAutoCreateRowSorter(true);
        tableXmpPhotoshop.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableXmpPhotoshop.setName("tableXmpPhotoshop"); // NOI18N
        scrollPaneXmpPhotoshop.setViewportView(tableXmpPhotoshop);

        tabbedPaneXmp.addTab(JptBundle.INSTANCE.getString("AppPanel.scrollPaneXmpPhotoshop.TabConstraints.tabTitle"), scrollPaneXmpPhotoshop); // NOI18N

        scrollPaneXmpXap.setName("scrollPaneXmpXap"); // NOI18N

        tableXmpXap.setAutoCreateRowSorter(true);
        tableXmpXap.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableXmpXap.setName("tableXmpXap"); // NOI18N
        scrollPaneXmpXap.setViewportView(tableXmpXap);

        tabbedPaneXmp.addTab(JptBundle.INSTANCE.getString("AppPanel.scrollPaneXmpXap.TabConstraints.tabTitle"), scrollPaneXmpXap); // NOI18N

        scrollPaneXmpLightroom.setName("scrollPaneXmpLightroom"); // NOI18N

        tableXmpLightroom.setAutoCreateRowSorter(true);
        tableXmpLightroom.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableXmpLightroom.setName("tableXmpLightroom"); // NOI18N
        scrollPaneXmpLightroom.setViewportView(tableXmpLightroom);

        tabbedPaneXmp.addTab(JptBundle.INSTANCE.getString("AppPanel.scrollPaneXmpLightroom.TabConstraints.tabTitle"), scrollPaneXmpLightroom); // NOI18N

        scrollPaneXmpCameraRawSettings.setName("scrollPaneXmpCameraRawSettings"); // NOI18N

        tableXmpCameraRawSettings.setAutoCreateRowSorter(true);
        tableXmpCameraRawSettings.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableXmpCameraRawSettings.setName("tableXmpCameraRawSettings"); // NOI18N
        scrollPaneXmpCameraRawSettings.setViewportView(tableXmpCameraRawSettings);

        tabbedPaneXmp.addTab(JptBundle.INSTANCE.getString("AppPanel.scrollPaneXmpCameraRawSettings.TabConstraints.tabTitle"), scrollPaneXmpCameraRawSettings); // NOI18N

        tabbedPaneMetadata.addTab(JptBundle.INSTANCE.getString("AppPanel.tabbedPaneXmp.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_xmp.png")), tabbedPaneXmp); // NOI18N

        panelTabEditMetadata.setName("panelTabEditMetadata"); // NOI18N

        panelScrollPaneEditMetadata.setName("panelScrollPaneEditMetadata"); // NOI18N

        scrollPaneEditMetadata.setName("scrollPaneEditMetadata"); // NOI18N

        panelEditMetadata.setName("panelEditMetadata"); // NOI18N

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
            .addComponent(scrollPaneEditMetadata, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
        );
        panelScrollPaneEditMetadataLayout.setVerticalGroup(
            panelScrollPaneEditMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneEditMetadata, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
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

        tabbedPaneMetadata.addTab(JptBundle.INSTANCE.getString("AppPanel.panelTabEditMetadata.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_edit.png")), panelTabEditMetadata); // NOI18N

        panelEditKeywords.setName("panelEditKeywords"); // NOI18N
        tabbedPaneMetadata.addTab(JptBundle.INSTANCE.getString("AppPanel.panelEditKeywords.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_keyword.png")), panelEditKeywords); // NOI18N

        javax.swing.GroupLayout panelMetadataLayout = new javax.swing.GroupLayout(panelMetadata);
        panelMetadata.setLayout(panelMetadataLayout);
        panelMetadataLayout.setHorizontalGroup(
            panelMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labelMetadataFilename, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
            .addComponent(tabbedPaneMetadata, javax.swing.GroupLayout.PREFERRED_SIZE, 142, Short.MAX_VALUE)
        );
        panelMetadataLayout.setVerticalGroup(
            panelMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMetadataLayout.createSequentialGroup()
                .addComponent(labelMetadataFilename)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabbedPaneMetadata, javax.swing.GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE))
        );

        splitPaneThumbnailsMetadata.setRightComponent(panelMetadata);

        javax.swing.GroupLayout panelThumbnailsMetadataLayout = new javax.swing.GroupLayout(panelThumbnailsMetadata);
        panelThumbnailsMetadata.setLayout(panelThumbnailsMetadataLayout);
        panelThumbnailsMetadataLayout.setHorizontalGroup(
            panelThumbnailsMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 728, Short.MAX_VALUE)
            .addGroup(panelThumbnailsMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(splitPaneThumbnailsMetadata, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 728, Short.MAX_VALUE))
        );
        panelThumbnailsMetadataLayout.setVerticalGroup(
            panelThumbnailsMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 464, Short.MAX_VALUE)
            .addGroup(panelThumbnailsMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(splitPaneThumbnailsMetadata, javax.swing.GroupLayout.DEFAULT_SIZE, 464, Short.MAX_VALUE))
        );

        splitPaneMain.setRightComponent(panelThumbnailsMetadata);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(splitPaneMain, gridBagConstraints);

        panelStatusbar.setName("panelStatusbar"); // NOI18N

        labelThumbnailInfo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelThumbnailInfo.setName("labelThumbnailInfo"); // NOI18N

        labelStatusbarText.setName("labelStatusbarText"); // NOI18N

        sliderThumbnailSize.setMajorTickSpacing(5);
        sliderThumbnailSize.setMinimum(10);
        sliderThumbnailSize.setSnapToTicks(true);
        sliderThumbnailSize.setToolTipText(JptBundle.INSTANCE.getString("AppPanel.sliderThumbnailSize.toolTipText")); // NOI18N
        sliderThumbnailSize.setName("sliderThumbnailSize"); // NOI18N

        labelError.setName("labelError"); // NOI18N
        labelError.setOpaque(true);
        labelError.setPreferredSize(new java.awt.Dimension(16, 16));

        buttonCancelProgress.setBorder(null);
        buttonCancelProgress.setContentAreaFilled(false);
        buttonCancelProgress.setName("buttonCancelProgress"); // NOI18N
        buttonCancelProgress.setPreferredSize(new java.awt.Dimension(16, 16));

        progressBar.setMaximumSize(new java.awt.Dimension(300, 14));
        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout panelStatusbarLayout = new javax.swing.GroupLayout(panelStatusbar);
        panelStatusbar.setLayout(panelStatusbarLayout);
        panelStatusbarLayout.setHorizontalGroup(
            panelStatusbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelStatusbarLayout.createSequentialGroup()
                .addComponent(labelThumbnailInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(labelStatusbarText, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sliderThumbnailSize, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(labelError, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonCancelProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        panelStatusbarLayout.setVerticalGroup(
            panelStatusbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStatusbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(labelThumbnailInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(labelStatusbarText)
                .addComponent(labelError, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(sliderThumbnailSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(buttonCancelProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        panelStatusbarLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {labelStatusbarText, labelThumbnailInfo});

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 3, 5);
        add(panelStatusbar, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void buttonDisplaySelKeywordsListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDisplaySelKeywordsListActionPerformed
        displaySelKeywordsCard("flatKeywords");
    }//GEN-LAST:event_buttonDisplaySelKeywordsListActionPerformed

    private void buttonDisplaySelKeywordsTreeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDisplaySelKeywordsTreeActionPerformed
        displaySelKeywordsCard("keywordsTree");
    }//GEN-LAST:event_buttonDisplaySelKeywordsTreeActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCancelProgress;
    private javax.swing.JButton buttonDisplaySelKeywordsList;
    private javax.swing.JButton buttonDisplaySelKeywordsTree;
    private javax.swing.JButton buttonExifToXmp;
    private javax.swing.ButtonGroup buttonGroupKeywordsMultipleSel;
    private javax.swing.JButton buttonIptcToXmp;
    private javax.swing.JButton buttonSearch;
    private javax.swing.JComboBox comboBoxFastSearch;
    private javax.swing.JComboBox comboBoxFileFilters;
    private javax.swing.JLabel labelError;
    private javax.swing.JLabel labelFileFilters;
    private javax.swing.JLabel labelMetadataFilename;
    private javax.swing.JLabel labelStatusbarText;
    private javax.swing.JLabel labelThumbnailInfo;
    private javax.swing.JList listImageCollections;
    private javax.swing.JList listNoMetadata;
    private javax.swing.JList listSavedSearches;
    private javax.swing.JList listSelKeywords;
    private javax.swing.JPanel panelDirectories;
    private org.jphototagger.program.view.panels.KeywordsPanel panelEditKeywords;
    private javax.swing.JPanel panelEditMetadata;
    private javax.swing.JPanel panelExif;
    private javax.swing.JPanel panelFavorites;
    private javax.swing.JPanel panelImageCollections;
    private javax.swing.JPanel panelIptc;
    private javax.swing.JPanel panelMetadata;
    private javax.swing.JPanel panelMiscMetadata;
    private javax.swing.JPanel panelNoMetadata;
    private javax.swing.JPanel panelSavedSearches;
    private javax.swing.JPanel panelScrollPaneEditMetadata;
    private javax.swing.JPanel panelSearch;
    private javax.swing.JPanel panelSelKeywords;
    private javax.swing.JPanel panelSelKeywordsList;
    private javax.swing.JPanel panelSelKeywordsListMultipleSelection;
    private javax.swing.JPanel panelSelKeywordsTree;
    private javax.swing.JPanel panelSelection;
    private javax.swing.JPanel panelStatusbar;
    private javax.swing.JPanel panelTabEditMetadata;
    private org.jphototagger.program.view.panels.ThumbnailsPanel panelThumbnails;
    private javax.swing.JPanel panelThumbnailsContent;
    private javax.swing.JPanel panelThumbnailsMetadata;
    private javax.swing.JPanel panelTimeline;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JRadioButton radioButtonSelKeywordsMultipleSelAll;
    private javax.swing.JRadioButton radioButtonSelKeywordsMultipleSelOne;
    private javax.swing.JScrollPane scrollPaneDirectories;
    private javax.swing.JScrollPane scrollPaneEditMetadata;
    private javax.swing.JScrollPane scrollPaneExif;
    private javax.swing.JScrollPane scrollPaneFavorites;
    private javax.swing.JScrollPane scrollPaneImageCollections;
    private javax.swing.JScrollPane scrollPaneIptc;
    private javax.swing.JScrollPane scrollPaneMiscMetadata;
    private javax.swing.JScrollPane scrollPaneNoMetadata;
    private javax.swing.JScrollPane scrollPaneSavedSearches;
    private javax.swing.JScrollPane scrollPaneSelKeywordsList;
    private javax.swing.JScrollPane scrollPaneSelKeywordsTree;
    private javax.swing.JScrollPane scrollPaneTextAreaSearch;
    private javax.swing.JScrollPane scrollPaneThumbnails;
    private javax.swing.JScrollPane scrollPaneTimeline;
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
    private javax.swing.JTextArea textAreaSearch;
    private javax.swing.JToggleButton toggleButtonExpandAllNodesSelKeywords;
    private javax.swing.JTree treeDirectories;
    private javax.swing.JTree treeFavorites;
    private javax.swing.JTree treeMiscMetadata;
    private javax.swing.JTree treeSelKeywords;
    private javax.swing.JTree treeTimeline;
    // End of variables declaration//GEN-END:variables
}
