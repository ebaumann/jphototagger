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
package de.elmar_baumann.jpt.view.panels;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.controller.hierarchicalkeywords.ControllerAddHierarchicalKeyword;
import de.elmar_baumann.jpt.controller.hierarchicalkeywords.ControllerAddHierarchicalKeywordsToEditPanel;
import de.elmar_baumann.jpt.controller.hierarchicalkeywords.ControllerRemoveHierarchicalKeywordFromEditPanel;
import de.elmar_baumann.jpt.controller.hierarchicalkeywords.ControllerRemoveHierarchicalKeyword;
import de.elmar_baumann.jpt.controller.hierarchicalkeywords.ControllerRenameHierarchicalKeyword;
import de.elmar_baumann.jpt.controller.hierarchicalkeywords.ControllerToggleRealHierarchicalKeyword;
import de.elmar_baumann.jpt.datatransfer.TransferHandlerListKeywords;
import de.elmar_baumann.jpt.event.listener.AppExitListener;
import de.elmar_baumann.jpt.model.ComboBoxModelFastSearch;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.ViewUtil;
import de.elmar_baumann.jpt.view.renderer.ListCellRendererFastSearchColumns;
import de.elmar_baumann.jpt.view.renderer.ListCellRendererImageCollections;
import de.elmar_baumann.jpt.view.renderer.ListCellRendererKeywords;
import de.elmar_baumann.jpt.view.renderer.ListCellRendererSavedSearches;
import de.elmar_baumann.jpt.view.renderer.TreeCellRendererHierarchicalKeywords;
import de.elmar_baumann.jpt.view.renderer.TreeCellRendererMiscMetadata;
import de.elmar_baumann.jpt.view.renderer.TreeCellRendererTimeline;
import de.elmar_baumann.lib.component.ImageTextField;
import de.elmar_baumann.lib.componentutil.ComponentUtil;
import de.elmar_baumann.lib.componentutil.TreeUtil;
import de.elmar_baumann.lib.event.listener.TableButtonMouseListener;
import de.elmar_baumann.lib.util.Settings;
import de.elmar_baumann.lib.util.SettingsHints;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.EnumSet;
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
public final class AppPanel extends javax.swing.JPanel implements AppExitListener {

    private static final String      KEY_DIVIDER_LOCATION_MAIN           = "AppPanel.DividerLocationMain";
    private static final String      KEY_DIVIDER_LOCATION_THUMBNAILS     = "AppPanel.DividerLocationThumbnails";
    private static final String      KEY_KEYWORDS_VIEW                   = "AppPanel.KeywordsView";
    private static final int         DEFAULT_DIVIDER_LOCATION_MAIN       = 100;
    private static final int         DEFAULT_DIVIDER_LOCATION_THUMBNAILS = 200;
    private final List<JTable>       xmpTables                           = new ArrayList<JTable>();
    private final List<JTable>       metadataTables                      = new ArrayList<JTable>();
    private final List<JTree>        selectionTrees                      = new ArrayList<JTree>();
    private final List<JList>        selectionLists                      = new ArrayList<JList>();
    private EditMetadataPanelsArray  editPanelsArray;
    private EditMetadataActionsPanel editActionsPanel;

    public AppPanel() {
        GUI.INSTANCE.setAppPanel(this);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        displaySearchButton();
        editPanelsArray = new EditMetadataPanelsArray(panelEditMetadata);
        panelThumbnails.setViewport(scrollPaneThumbnails.getViewport());
        setBackgroundColorTablesScrollPanes();
        setTreesSingleSelection();
        initCollections();
        tableExif.addMouseListener(new TableButtonMouseListener(tableExif));
        scrollPaneThumbnails.getVerticalScrollBar().setUnitIncrement(30);
        displayInitKeywordsView();
        setTextFieldSearchImage();
    }

    private void setTextFieldSearchImage() {
        ((ImageTextField) textFieldSearch).setImage(
                AppLookAndFeel.localizedImage(
                    "/de/elmar_baumann/jpt/resource/images/textfield_search.png"));
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

    public EditMetadataPanelsArray getEditMetadataPanelsArray() {
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
        return panelIptc;
    }

    public Component getTabMetadataExif() {
        return scrollPaneExif;
    }

    public Component getTabMetadataXmp() {
        return tabbedPaneXmp;
    }

    public Component getTabMetadataHierarchicaKeywords() {
        return panelEditKeywords;
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

    public JPanel getTabSelectionSavedSearches() {
        return panelSavedSearches;
    }

    public JPanel getTabSelectionKeywords() {
        return panelSelKeywords;
    }

    public JPanel getTabSelectionHierarchicalKeywords() {
        return panelSelKeywordsTree;
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
        return selectionTrees;
    }

    public List<JList> getSelectionLists() {
        return selectionLists;
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

    public JButton getButtonIptcToXmp() {
        return buttonIptcToXmp;
    }

    public JButton getButtonExifToXmp() {
        return buttonExifToXmp;
    }

    public JButton getButtonSearch() {
        return buttonSearch;
    }

    public JComboBox getComboBoxMetadataTemplates() {
        return editActionsPanel.comboBoxMetadataTemplates;
    }

    public JLabel getLabelMetadataInfoEditable() {
        return editActionsPanel.labelMetadataInfoEditable;
    }

    public JLabel getLabelThumbnailInfo() {
        return labelThumbnailInfo;
    }

    public JLabel getLabelMetadataFilename() {
        return labelMetadataFilename;
    }

    public JTextField getTextFieldSearch() {
        return textFieldSearch;
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

    private void setBackgroundColorTablesScrollPanes() {
        for (JTable table : metadataTables) {
            Container container = table.getParent();
            if (container instanceof JViewport) {
                JViewport viewport = (JViewport) container;
                viewport.setBackground(table.getBackground());
            }
        }
    }

    private void displayInitKeywordsView() {
        panelEditKeywords.setKeyCard("AppPanel.Keywords.Card");
        panelEditKeywords.setKeyTree("AppPanel.Keywords.Tree");
        panelEditKeywords.readProperties();

        String name = "keywordsTree";
        if (UserSettings.INSTANCE.getProperties().containsKey(KEY_KEYWORDS_VIEW)) {
            String s = UserSettings.INSTANCE.getSettings().getString(KEY_KEYWORDS_VIEW);
            if (s.equals("flatKeywords") || s.equals("keywordsTree")) {
                name = s;
            }
        }
        displaySelKeywordsCard(name);
    }

    @Override
    public void appWillExit() {
        writeProperties();
    }

    private void writeProperties() {
        Settings settings = UserSettings.INSTANCE.getSettings();

        settings.setComponent(this, getPersistentSettingsHints());
        settings.setInt(splitPaneMain.getDividerLocation(), KEY_DIVIDER_LOCATION_MAIN);
        settings.setInt(splitPaneThumbnailsMetadata.getDividerLocation(), KEY_DIVIDER_LOCATION_THUMBNAILS);
        ViewUtil.writeTreeDirectoriesToProperties();
        panelEditKeywords.writeProperties();
        UserSettings.INSTANCE.writeToFile();
    }

    public SettingsHints getPersistentSettingsHints() {
        SettingsHints hints     = new SettingsHints(EnumSet.of(SettingsHints.Option.SET_TABBED_PANE_CONTENT));
        String        className = getClass().getName();
        hints.addExclude(className + ".textFieldSearch");
        hints.addExclude(className + ".panelEditMetadata");
        hints.addExclude(className + ".treeDirectories");
        hints.addExclude(className + ".treeFavorites");
        //hints.addExclude(className + ".listNoMetadata");
        return hints;
    }

    private int getDividerLocationThumbnails() {
        int location = UserSettings.INSTANCE.getSettings().getInt(
                KEY_DIVIDER_LOCATION_THUMBNAILS);
        return location >= 0
                ? location
                : DEFAULT_DIVIDER_LOCATION_THUMBNAILS;
    }

    private int getDividerLocationMain() {
        int location = UserSettings.INSTANCE.getSettings().getInt(
                KEY_DIVIDER_LOCATION_MAIN);
        return location >= 0
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
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

    public enum MessageType {
        INFO,
        ERROR
        ;

        public boolean isError() {
            return this.equals(ERROR);
        }

        public boolean isInfo() {
            return this.equals(INFO);
        }
    }

    public void showMessage(String message, MessageType type, final long milliseconds) {
        labelInfo.setForeground(type.isError() ? Color.RED : Color.BLACK);
        labelInfo.setText(message);
        Thread thread = new Thread(new HideInfoMessage(milliseconds));
        thread.setName("Hiding message popup @ " + getClass().getSimpleName());
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    private void handleToggleButtonSelKeywords() {
        boolean selected = toggleButtonExpandAllNodesSelKeywords.isSelected();
        TreeUtil.expandAll(treeSelKeywords, selected);
        toggleButtonExpandAllNodesSelKeywords.setText(
                selected
                ? Bundle.getString("HierarchicalKeywordsPanel.ButtonToggleExpandAllNodes.Selected")
                : Bundle.getString("HierarchicalKeywordsPanel.ButtonToggleExpandAllNodes.DeSelected"));
    }

    private void displaySelKeywordsCard(String name) {
        CardLayout cl = (CardLayout)(panelSelKeywords.getLayout());
        cl.show(panelSelKeywords, name);
        UserSettings.INSTANCE.getSettings().setString(name, KEY_KEYWORDS_VIEW);
        UserSettings.INSTANCE.writeToFile();
    }

    public void settingsRead() {
        ComponentUtil.forceRepaint(comboBoxFastSearch);
        textFieldSearch.requestFocusInWindow();
    }

    private class HideInfoMessage implements Runnable {

        private final long milliseconds;

        public HideInfoMessage(long milliseconds) {
            this.milliseconds = milliseconds;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(milliseconds);
            } catch (InterruptedException ex) {
                AppLog.logSevere(ViewUtil.class, ex);
            }
            labelInfo.setText("");
        }
    }

    /** This method is called from within the constructor to
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
        textFieldSearch = new ImageTextField();
        buttonSearch = new javax.swing.JButton();
        tabbedPaneSelection = new javax.swing.JTabbedPane();
        panelDirectories = new javax.swing.JPanel();
        scrollPaneDirectories = new javax.swing.JScrollPane();
        treeDirectories = new javax.swing.JTree();
        panelSavedSearches = new javax.swing.JPanel();
        scrollPaneSavedSearches = new javax.swing.JScrollPane();
        listSavedSearches = new javax.swing.JList();
        panelImageCollections = new javax.swing.JPanel();
        scrollPaneImageCollections = new javax.swing.JScrollPane();
        listImageCollections = new javax.swing.JList();
        panelFavorites = new javax.swing.JPanel();
        scrollPaneFavorites = new javax.swing.JScrollPane();
        treeFavorites = new javax.swing.JTree();
        panelSelKeywords = new javax.swing.JPanel();
        panelSelKeywordsTree = new javax.swing.JPanel();
        scrollPaneSelKeywordsTree = new javax.swing.JScrollPane();
        treeSelKeywords = new javax.swing.JTree();
        toggleButtonExpandAllNodesSelKeywords = new javax.swing.JToggleButton();
        buttonDisplaySelKeywordsList = new javax.swing.JButton();
        panelSelKeywordsList = new javax.swing.JPanel();
        scrollPaneSelKeywordsList = new javax.swing.JScrollPane();
        listSelKeywords = new javax.swing.JList();
        panelSelKeywordsListMultipleSelection = new javax.swing.JPanel();
        radioButtonSelKeywordsMultipleSelAll = new javax.swing.JRadioButton();
        radioButtonSelKeywordsMultipleSelOne = new javax.swing.JRadioButton();
        buttonDisplaySelKeywordsTree = new javax.swing.JButton();
        panelTimeline = new javax.swing.JPanel();
        scrollPaneTimeline = new javax.swing.JScrollPane();
        treeTimeline = new javax.swing.JTree();
        panelMiscMetadata = new javax.swing.JPanel();
        scrollPaneMiscMetadata = new javax.swing.JScrollPane();
        treeMiscMetadata = new javax.swing.JTree();
        panelNoMetadata = new javax.swing.JPanel();
        scrollPaneNoMetadata = new javax.swing.JScrollPane();
        listNoMetadata = new javax.swing.JList();
        panelThumbnailsMetadata = new javax.swing.JPanel();
        splitPaneThumbnailsMetadata = new javax.swing.JSplitPane();
        splitPaneThumbnailsMetadata.setDividerLocation(getDividerLocationThumbnails());
        panelThumbnailsContent = new javax.swing.JPanel();
        scrollPaneThumbnails = new javax.swing.JScrollPane();
        panelThumbnails = new de.elmar_baumann.jpt.view.panels.ThumbnailsPanel();
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
        panelEditKeywords = new de.elmar_baumann.jpt.view.panels.KeywordsPanel();
        panelStatusbar = new javax.swing.JPanel();
        labelThumbnailInfo = new javax.swing.JLabel();
        labelInfo = new javax.swing.JLabel();
        sliderThumbnailSize = new javax.swing.JSlider();
        labelError = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        setLayout(new java.awt.GridBagLayout());

        splitPaneMain.setDividerSize(6);
        splitPaneMain.setOneTouchExpandable(true);

        panelSelection.setLayout(new java.awt.GridBagLayout());

        panelSearch.setLayout(new java.awt.GridBagLayout());

        comboBoxFastSearch.setModel(new ComboBoxModelFastSearch());
        comboBoxFastSearch.setRenderer(new ListCellRendererFastSearchColumns());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panelSearch.add(comboBoxFastSearch, gridBagConstraints);

        textFieldSearch.setToolTipText(Bundle.getString("AppPanel.textFieldSearch.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        panelSearch.add(textFieldSearch, gridBagConstraints);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/elmar_baumann/jpt/resource/properties/Bundle"); // NOI18N
        buttonSearch.setText(bundle.getString("AppPanel.buttonSearch.text")); // NOI18N
        buttonSearch.setMargin(new java.awt.Insets(0, 2, 0, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
        panelSearch.add(buttonSearch, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panelSelection.add(panelSearch, gridBagConstraints);

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Lade...");
        treeDirectories.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        treeDirectories.setCellRenderer(new de.elmar_baumann.lib.renderer.TreeCellRendererAllSystemDirectories());
        treeDirectories.setDragEnabled(true);
        treeDirectories.setName("treeDirectories"); // NOI18N
        scrollPaneDirectories.setViewportView(treeDirectories);
        treeDirectories.setTransferHandler(new de.elmar_baumann.jpt.datatransfer.TransferHandlerTreeDirectories());
        treeFavorites.setTransferHandler(new de.elmar_baumann.jpt.datatransfer.TransferHandlerTreeDirectories());

        javax.swing.GroupLayout panelDirectoriesLayout = new javax.swing.GroupLayout(panelDirectories);
        panelDirectories.setLayout(panelDirectoriesLayout);
        panelDirectoriesLayout.setHorizontalGroup(
            panelDirectoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneDirectories, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
        );
        panelDirectoriesLayout.setVerticalGroup(
            panelDirectoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneDirectories, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
        );

        tabbedPaneSelection.addTab(Bundle.getString("AppPanel.panelDirectories.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_folder.png")), panelDirectories); // NOI18N

        listSavedSearches.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Lade..." };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        listSavedSearches.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listSavedSearches.setCellRenderer(new ListCellRendererSavedSearches());
        listSavedSearches.setName("listSavedSearches"); // NOI18N
        scrollPaneSavedSearches.setViewportView(listSavedSearches);

        javax.swing.GroupLayout panelSavedSearchesLayout = new javax.swing.GroupLayout(panelSavedSearches);
        panelSavedSearches.setLayout(panelSavedSearchesLayout);
        panelSavedSearchesLayout.setHorizontalGroup(
            panelSavedSearchesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneSavedSearches, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
        );
        panelSavedSearchesLayout.setVerticalGroup(
            panelSavedSearchesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneSavedSearches, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
        );

        tabbedPaneSelection.addTab(Bundle.getString("AppPanel.panelSavedSearches.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_search.png")), panelSavedSearches); // NOI18N

        listImageCollections.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Lade..." };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        listImageCollections.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listImageCollections.setCellRenderer(new ListCellRendererImageCollections());
        listImageCollections.setDragEnabled(true);
        listImageCollections.setName("listImageCollections"); // NOI18N
        scrollPaneImageCollections.setViewportView(listImageCollections);
        listImageCollections.setTransferHandler(new de.elmar_baumann.jpt.datatransfer.TransferHandlerListImageCollections());

        javax.swing.GroupLayout panelImageCollectionsLayout = new javax.swing.GroupLayout(panelImageCollections);
        panelImageCollections.setLayout(panelImageCollectionsLayout);
        panelImageCollectionsLayout.setHorizontalGroup(
            panelImageCollectionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneImageCollections, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
        );
        panelImageCollectionsLayout.setVerticalGroup(
            panelImageCollectionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneImageCollections, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
        );

        tabbedPaneSelection.addTab(Bundle.getString("AppPanel.panelImageCollections.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_imagecollection.png")), panelImageCollections); // NOI18N

        treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        javax.swing.tree.DefaultMutableTreeNode treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Bitte warten");
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Lade...");
        treeNode1.add(treeNode2);
        treeFavorites.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        treeFavorites.setCellRenderer(new de.elmar_baumann.jpt.view.renderer.TreeCellRendererFavorites());
        treeFavorites.setDragEnabled(true);
        treeFavorites.setRootVisible(false);
        treeFavorites.setShowsRootHandles(true);
        scrollPaneFavorites.setViewportView(treeFavorites);

        javax.swing.GroupLayout panelFavoritesLayout = new javax.swing.GroupLayout(panelFavorites);
        panelFavorites.setLayout(panelFavoritesLayout);
        panelFavoritesLayout.setHorizontalGroup(
            panelFavoritesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneFavorites, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
        );
        panelFavoritesLayout.setVerticalGroup(
            panelFavoritesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneFavorites, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
        );

        tabbedPaneSelection.addTab(Bundle.getString("AppPanel.panelFavorites.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_favorite.png")), panelFavorites); // NOI18N

        panelSelKeywords.setLayout(new java.awt.CardLayout());

        panelSelKeywordsTree.setLayout(new java.awt.GridBagLayout());

        treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Laden...");
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Laden...");
        treeNode1.add(treeNode2);
        treeSelKeywords.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        treeSelKeywords.setCellRenderer(new TreeCellRendererHierarchicalKeywords());
        treeSelKeywords.setShowsRootHandles(true);
        scrollPaneSelKeywordsTree.setViewportView(treeSelKeywords);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelSelKeywordsTree.add(scrollPaneSelKeywordsTree, gridBagConstraints);

        toggleButtonExpandAllNodesSelKeywords.setText(bundle.getString("AppPanel.toggleButtonExpandAllNodesSelKeywords.text")); // NOI18N
        toggleButtonExpandAllNodesSelKeywords.setMargin(new java.awt.Insets(1, 1, 1, 1));
        toggleButtonExpandAllNodesSelKeywords.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleButtonExpandAllNodesSelKeywordsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 1, 0);
        panelSelKeywordsTree.add(toggleButtonExpandAllNodesSelKeywords, gridBagConstraints);

        buttonDisplaySelKeywordsList.setText(bundle.getString("AppPanel.buttonDisplaySelKeywordsList.text")); // NOI18N
        buttonDisplaySelKeywordsList.setMargin(new java.awt.Insets(1, 1, 1, 1));
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

        panelSelKeywords.add(panelSelKeywordsTree, "keywordsTree");

        panelSelKeywordsList.setLayout(new java.awt.GridBagLayout());

        listSelKeywords.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Lade..." };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        listSelKeywords.setCellRenderer(new ListCellRendererKeywords());
        listSelKeywords.setDragEnabled(true);
        listSelKeywords.setName("listSelKeywords"); // NOI18N
        scrollPaneSelKeywordsList.setViewportView(listSelKeywords);
        listSelKeywords.setTransferHandler(new TransferHandlerListKeywords());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelSelKeywordsList.add(scrollPaneSelKeywordsList, gridBagConstraints);

        panelSelKeywordsListMultipleSelection.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("AppPanel.panelSelKeywordsListMultipleSelection.border.title"))); // NOI18N
        panelSelKeywordsListMultipleSelection.setLayout(new java.awt.GridBagLayout());

        buttonGroupKeywordsMultipleSel.add(radioButtonSelKeywordsMultipleSelAll);
        radioButtonSelKeywordsMultipleSelAll.setText(bundle.getString("AppPanel.radioButtonSelKeywordsMultipleSelAll.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        panelSelKeywordsListMultipleSelection.add(radioButtonSelKeywordsMultipleSelAll, gridBagConstraints);

        buttonGroupKeywordsMultipleSel.add(radioButtonSelKeywordsMultipleSelOne);
        radioButtonSelKeywordsMultipleSelOne.setText(bundle.getString("AppPanel.radioButtonSelKeywordsMultipleSelOne.text")); // NOI18N
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

        buttonDisplaySelKeywordsTree.setText(bundle.getString("AppPanel.buttonDisplaySelKeywordsTree.text")); // NOI18N
        buttonDisplaySelKeywordsTree.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonDisplaySelKeywordsTree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDisplaySelKeywordsTreeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 0);
        panelSelKeywordsList.add(buttonDisplaySelKeywordsTree, gridBagConstraints);

        panelSelKeywords.add(panelSelKeywordsList, "flatKeywords");

        tabbedPaneSelection.addTab(Bundle.getString("AppPanel.panelSelKeywords.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_keyword.png")), panelSelKeywords); // NOI18N

        treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Bitte warten");
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Lade...");
        treeNode1.add(treeNode2);
        treeTimeline.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        treeTimeline.setCellRenderer(new TreeCellRendererTimeline());
        treeTimeline.setRootVisible(false);
        treeTimeline.setShowsRootHandles(true);
        scrollPaneTimeline.setViewportView(treeTimeline);

        javax.swing.GroupLayout panelTimelineLayout = new javax.swing.GroupLayout(panelTimeline);
        panelTimeline.setLayout(panelTimelineLayout);
        panelTimelineLayout.setHorizontalGroup(
            panelTimelineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneTimeline, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
        );
        panelTimelineLayout.setVerticalGroup(
            panelTimelineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneTimeline, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
        );

        tabbedPaneSelection.addTab(Bundle.getString("AppPanel.panelTimeline.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_timeline.png")), panelTimeline); // NOI18N

        treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Bitte warten");
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Lade...");
        treeNode1.add(treeNode2);
        treeMiscMetadata.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        treeMiscMetadata.setCellRenderer(new TreeCellRendererMiscMetadata());
        treeMiscMetadata.setRootVisible(false);
        treeMiscMetadata.setShowsRootHandles(true);
        scrollPaneMiscMetadata.setViewportView(treeMiscMetadata);

        javax.swing.GroupLayout panelMiscMetadataLayout = new javax.swing.GroupLayout(panelMiscMetadata);
        panelMiscMetadata.setLayout(panelMiscMetadataLayout);
        panelMiscMetadataLayout.setHorizontalGroup(
            panelMiscMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneMiscMetadata, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
        );
        panelMiscMetadataLayout.setVerticalGroup(
            panelMiscMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneMiscMetadata, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
        );

        tabbedPaneSelection.addTab(Bundle.getString("AppPanel.panelMiscMetadata.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_misc_metadata.png")), panelMiscMetadata); // NOI18N

        listNoMetadata.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listNoMetadata.setCellRenderer(new de.elmar_baumann.jpt.view.renderer.ListCellRendererNoMetadata());
        scrollPaneNoMetadata.setViewportView(listNoMetadata);

        javax.swing.GroupLayout panelNoMetadataLayout = new javax.swing.GroupLayout(panelNoMetadata);
        panelNoMetadata.setLayout(panelNoMetadataLayout);
        panelNoMetadataLayout.setHorizontalGroup(
            panelNoMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 203, Short.MAX_VALUE)
            .addGroup(panelNoMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrollPaneNoMetadata, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE))
        );
        panelNoMetadataLayout.setVerticalGroup(
            panelNoMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 276, Short.MAX_VALUE)
            .addGroup(panelNoMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrollPaneNoMetadata, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE))
        );

        tabbedPaneSelection.addTab(bundle.getString("AppPanel.panelNoMetadata.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_no_metadata.png")), panelNoMetadata); // NOI18N

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

        splitPaneThumbnailsMetadata.setDividerSize(6);
        splitPaneThumbnailsMetadata.setResizeWeight(1.0);
        splitPaneThumbnailsMetadata.setOneTouchExpandable(true);

        panelThumbnailsContent.setMinimumSize(new java.awt.Dimension(180, 0));

        javax.swing.GroupLayout panelThumbnailsLayout = new javax.swing.GroupLayout(panelThumbnails);
        panelThumbnails.setLayout(panelThumbnailsLayout);
        panelThumbnailsLayout.setHorizontalGroup(
            panelThumbnailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 526, Short.MAX_VALUE)
        );
        panelThumbnailsLayout.setVerticalGroup(
            panelThumbnailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 466, Short.MAX_VALUE)
        );

        scrollPaneThumbnails.setViewportView(panelThumbnails);

        javax.swing.GroupLayout panelThumbnailsContentLayout = new javax.swing.GroupLayout(panelThumbnailsContent);
        panelThumbnailsContent.setLayout(panelThumbnailsContentLayout);
        panelThumbnailsContentLayout.setHorizontalGroup(
            panelThumbnailsContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 529, Short.MAX_VALUE)
            .addGroup(panelThumbnailsContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrollPaneThumbnails, javax.swing.GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE))
        );
        panelThumbnailsContentLayout.setVerticalGroup(
            panelThumbnailsContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 469, Short.MAX_VALUE)
            .addGroup(panelThumbnailsContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrollPaneThumbnails, javax.swing.GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE))
        );

        splitPaneThumbnailsMetadata.setLeftComponent(panelThumbnailsContent);

        labelMetadataFilename.setBackground(new java.awt.Color(255, 255, 255));
        labelMetadataFilename.setText(Bundle.getString("AppPanel.labelMetadataFilename.text")); // NOI18N
        labelMetadataFilename.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelMetadataFilename.setOpaque(true);

        tabbedPaneMetadata.setOpaque(true);

        panelExif.setLayout(new java.awt.GridBagLayout());

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

        buttonExifToXmp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_xmp.png"))); // NOI18N
        buttonExifToXmp.setText(bundle.getString("AppPanel.buttonExifToXmp.text")); // NOI18N
        buttonExifToXmp.setMargin(new java.awt.Insets(2, 2, 2, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
        panelExif.add(buttonExifToXmp, gridBagConstraints);

        tabbedPaneMetadata.addTab(bundle.getString("AppPanel.panelExif.TabConstraints.tabTitle"), panelExif); // NOI18N

        panelIptc.setLayout(new java.awt.GridBagLayout());

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

        buttonIptcToXmp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_xmp.png"))); // NOI18N
        buttonIptcToXmp.setMnemonic('x');
        buttonIptcToXmp.setText(Bundle.getString("AppPanel.buttonIptcToXmp.text")); // NOI18N
        buttonIptcToXmp.setToolTipText(Bundle.getString("AppPanel.buttonIptcToXmp.toolTipText")); // NOI18N
        buttonIptcToXmp.setMargin(new java.awt.Insets(2, 2, 2, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
        panelIptc.add(buttonIptcToXmp, gridBagConstraints);

        tabbedPaneMetadata.addTab(Bundle.getString("AppPanel.panelIptc.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_iptc.png")), panelIptc); // NOI18N

        tabbedPaneXmp.setOpaque(true);

        tableXmpTiff.setAutoCreateRowSorter(true);
        tableXmpTiff.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableXmpTiff.setName("tableXmpTiff"); // NOI18N
        scrollPaneXmpTiff.setViewportView(tableXmpTiff);

        tabbedPaneXmp.addTab(Bundle.getString("AppPanel.scrollPaneXmpTiff.TabConstraints.tabTitle"), scrollPaneXmpTiff); // NOI18N

        tableXmpExif.setAutoCreateRowSorter(true);
        tableXmpExif.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableXmpExif.setName("tableXmpExif"); // NOI18N
        scrollPaneXmpExif.setViewportView(tableXmpExif);

        tabbedPaneXmp.addTab(Bundle.getString("AppPanel.scrollPaneXmpExif.TabConstraints.tabTitle"), scrollPaneXmpExif); // NOI18N

        tableXmpDc.setAutoCreateRowSorter(true);
        tableXmpDc.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableXmpDc.setName("tableXmpDc"); // NOI18N
        scrollPaneXmpDc.setViewportView(tableXmpDc);

        tabbedPaneXmp.addTab(Bundle.getString("AppPanel.scrollPaneXmpDc.TabConstraints.tabTitle"), scrollPaneXmpDc); // NOI18N

        tableXmpIptc.setAutoCreateRowSorter(true);
        tableXmpIptc.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableXmpIptc.setName("tableXmpIptc"); // NOI18N
        scrollPaneXmpIptc.setViewportView(tableXmpIptc);

        tabbedPaneXmp.addTab(Bundle.getString("AppPanel.scrollPaneXmpIptc.TabConstraints.tabTitle"), scrollPaneXmpIptc); // NOI18N

        tableXmpPhotoshop.setAutoCreateRowSorter(true);
        tableXmpPhotoshop.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableXmpPhotoshop.setName("tableXmpPhotoshop"); // NOI18N
        scrollPaneXmpPhotoshop.setViewportView(tableXmpPhotoshop);

        tabbedPaneXmp.addTab(Bundle.getString("AppPanel.scrollPaneXmpPhotoshop.TabConstraints.tabTitle"), scrollPaneXmpPhotoshop); // NOI18N

        tableXmpXap.setAutoCreateRowSorter(true);
        tableXmpXap.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableXmpXap.setName("tableXmpXap"); // NOI18N
        scrollPaneXmpXap.setViewportView(tableXmpXap);

        tabbedPaneXmp.addTab(Bundle.getString("AppPanel.scrollPaneXmpXap.TabConstraints.tabTitle"), scrollPaneXmpXap); // NOI18N

        tableXmpLightroom.setAutoCreateRowSorter(true);
        tableXmpLightroom.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableXmpLightroom.setName("tableXmpLightroom"); // NOI18N
        scrollPaneXmpLightroom.setViewportView(tableXmpLightroom);

        tabbedPaneXmp.addTab(Bundle.getString("AppPanel.scrollPaneXmpLightroom.TabConstraints.tabTitle"), scrollPaneXmpLightroom); // NOI18N

        tableXmpCameraRawSettings.setAutoCreateRowSorter(true);
        tableXmpCameraRawSettings.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableXmpCameraRawSettings.setName("tableXmpCameraRawSettings"); // NOI18N
        scrollPaneXmpCameraRawSettings.setViewportView(tableXmpCameraRawSettings);

        tabbedPaneXmp.addTab(Bundle.getString("AppPanel.scrollPaneXmpCameraRawSettings.TabConstraints.tabTitle"), scrollPaneXmpCameraRawSettings); // NOI18N

        tabbedPaneMetadata.addTab(Bundle.getString("AppPanel.tabbedPaneXmp.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_xmp.png")), tabbedPaneXmp); // NOI18N

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
            .addComponent(scrollPaneEditMetadata, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 363, Short.MAX_VALUE)
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

        tabbedPaneMetadata.addTab(Bundle.getString("AppPanel.panelTabEditMetadata.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_workspace.png")), panelTabEditMetadata); // NOI18N
        tabbedPaneMetadata.addTab(bundle.getString("AppPanel.panelEditKeywords.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_keyword.png")), panelEditKeywords); // NOI18N
        new ControllerToggleRealHierarchicalKeyword(panelEditKeywords);
        new ControllerRenameHierarchicalKeyword(panelEditKeywords);
        new ControllerAddHierarchicalKeyword(panelEditKeywords);
        new ControllerRemoveHierarchicalKeyword(panelEditKeywords);
        new ControllerAddHierarchicalKeywordsToEditPanel(panelEditKeywords);
        new ControllerRemoveHierarchicalKeywordFromEditPanel(panelEditKeywords);
        new de.elmar_baumann.jpt.controller.hierarchicalkeywords.ControllerCopyCutPasteHierarchicalKeyword(panelEditKeywords);
        new de.elmar_baumann.jpt.controller.hierarchicalkeywords.ControllerHierarchicalKeywordsDisplayImages();

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
                .addComponent(tabbedPaneMetadata, javax.swing.GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE))
        );

        splitPaneThumbnailsMetadata.setRightComponent(panelMetadata);

        javax.swing.GroupLayout panelThumbnailsMetadataLayout = new javax.swing.GroupLayout(panelThumbnailsMetadata);
        panelThumbnailsMetadata.setLayout(panelThumbnailsMetadataLayout);
        panelThumbnailsMetadataLayout.setHorizontalGroup(
            panelThumbnailsMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 679, Short.MAX_VALUE)
            .addGroup(panelThumbnailsMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(splitPaneThumbnailsMetadata, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 679, Short.MAX_VALUE))
        );
        panelThumbnailsMetadataLayout.setVerticalGroup(
            panelThumbnailsMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 471, Short.MAX_VALUE)
            .addGroup(panelThumbnailsMetadataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(splitPaneThumbnailsMetadata, javax.swing.GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE))
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

        labelThumbnailInfo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        sliderThumbnailSize.setMajorTickSpacing(5);
        sliderThumbnailSize.setMinimum(10);
        sliderThumbnailSize.setSnapToTicks(true);
        sliderThumbnailSize.setToolTipText(Bundle.getString("AppPanel.sliderThumbnailSize.toolTipText")); // NOI18N

        labelError.setOpaque(true);
        labelError.setPreferredSize(new java.awt.Dimension(12, 12));

        progressBar.setMaximumSize(new java.awt.Dimension(300, 14));
        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout panelStatusbarLayout = new javax.swing.GroupLayout(panelStatusbar);
        panelStatusbar.setLayout(panelStatusbarLayout);
        panelStatusbarLayout.setHorizontalGroup(
            panelStatusbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStatusbarLayout.createSequentialGroup()
                .addComponent(labelThumbnailInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sliderThumbnailSize, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(labelError, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        panelStatusbarLayout.setVerticalGroup(
            panelStatusbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStatusbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(labelThumbnailInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(labelInfo)
                .addComponent(labelError, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(sliderThumbnailSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        panelStatusbarLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {labelInfo, labelThumbnailInfo});

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 3, 5);
        add(panelStatusbar, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void toggleButtonExpandAllNodesSelKeywordsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleButtonExpandAllNodesSelKeywordsActionPerformed
        handleToggleButtonSelKeywords();
    }//GEN-LAST:event_toggleButtonExpandAllNodesSelKeywordsActionPerformed

    private void buttonDisplaySelKeywordsListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDisplaySelKeywordsListActionPerformed
        displaySelKeywordsCard("flatKeywords");
    }//GEN-LAST:event_buttonDisplaySelKeywordsListActionPerformed

    private void buttonDisplaySelKeywordsTreeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDisplaySelKeywordsTreeActionPerformed
        displaySelKeywordsCard("keywordsTree");
    }//GEN-LAST:event_buttonDisplaySelKeywordsTreeActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonDisplaySelKeywordsList;
    private javax.swing.JButton buttonDisplaySelKeywordsTree;
    private javax.swing.JButton buttonExifToXmp;
    private javax.swing.ButtonGroup buttonGroupKeywordsMultipleSel;
    private javax.swing.JButton buttonIptcToXmp;
    private javax.swing.JButton buttonSearch;
    private javax.swing.JComboBox comboBoxFastSearch;
    private javax.swing.JLabel labelError;
    private javax.swing.JLabel labelInfo;
    private javax.swing.JLabel labelMetadataFilename;
    private javax.swing.JLabel labelThumbnailInfo;
    private javax.swing.JList listImageCollections;
    private javax.swing.JList listNoMetadata;
    private javax.swing.JList listSavedSearches;
    private javax.swing.JList listSelKeywords;
    private javax.swing.JPanel panelDirectories;
    private de.elmar_baumann.jpt.view.panels.KeywordsPanel panelEditKeywords;
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
    private de.elmar_baumann.jpt.view.panels.ThumbnailsPanel panelThumbnails;
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
    private javax.swing.JTextField textFieldSearch;
    private javax.swing.JToggleButton toggleButtonExpandAllNodesSelKeywords;
    private javax.swing.JTree treeDirectories;
    private javax.swing.JTree treeFavorites;
    private javax.swing.JTree treeMiscMetadata;
    private javax.swing.JTree treeSelKeywords;
    private javax.swing.JTree treeTimeline;
    // End of variables declaration//GEN-END:variables
}
