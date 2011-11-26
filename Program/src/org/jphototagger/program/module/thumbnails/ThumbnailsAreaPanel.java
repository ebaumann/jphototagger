package org.jphototagger.program.module.thumbnails;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileFilter;
import java.util.Collection;
import java.util.Comparator;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.openide.util.Lookup;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.windows.WaitDisplayer;
import org.jphototagger.domain.filefilter.UserDefinedFileFilter;
import org.jphototagger.domain.thumbnails.ThumbnailsPanelBottomComponentProvider;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.swing.ExpandCollapseComponentPanel;
import org.jphototagger.lib.swing.util.MnemonicUtil;

/**
 * @author Elmar Baumann
 */
public class ThumbnailsAreaPanel extends javax.swing.JPanel implements ItemListener, ListDataListener {

    private static final long serialVersionUID = 1L;
    private static final String KEY_THUMBNAIL_PANEL_VIEWPORT_VIEW_POSITION = "org.jphototagger.program.view.panels.controller.ViewportViewPosition";
    private final FileFiltersComboBoxModel fileFiltersComboBoxModel = new FileFiltersComboBoxModel();
    private final ThumbnailsSortComboBoxModel thumbnailsSortComboBoxModel = new ThumbnailsSortComboBoxModel();
    private ExpandCollapseComponentPanel expandCollapseBottomPanel;

    public ThumbnailsAreaPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        addBottomComponentsPanel();
        thumbnailsPanelScrollPane.getVerticalScrollBar().setUnitIncrement(30);
        fileSortComboBox.addItemListener(this);
        fileFiltersComboBox.addItemListener(this);
        fileFiltersComboBoxModel.addListDataListener(this);
        thumbnailsPanel.setViewport(thumbnailsPanelScrollPane.getViewport());
        MnemonicUtil.setMnemonics(this);
        fileFiltersComboBoxModel.selectPersistedItem();
        thumbnailsSortComboBoxModel.selectPersistedItem();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        Object item = e.getItem();
        if (item instanceof ThumbnailsSortComboBoxModel.FileSorter) {
            ThumbnailsSortComboBoxModel.FileSorter fileSorter = (ThumbnailsSortComboBoxModel.FileSorter) item;
            Comparator<File> comparator = fileSorter.getComparator();
            sortThumbnails(comparator);
        } else {
            setFileFilter(e.getItem());
        }
    }

    private void sortThumbnails(final Comparator<File> fileSortComparator) {
        WaitDisplayer waitDisplayer = Lookup.getDefault().lookup(WaitDisplayer.class);
        waitDisplayer.show();
        thumbnailsPanel.setFileSortComparator(fileSortComparator);
        thumbnailsPanel.sort();
        persistSortOrder();
        waitDisplayer.hide();
    }

    private void setFileFilter(Object item) {
        WaitDisplayer waitDisplayer = Lookup.getDefault().lookup(WaitDisplayer.class);
        waitDisplayer.show();
        if (item instanceof FileFilter) {
            thumbnailsPanel.setFileFilter((FileFilter) item);
        } else if (item instanceof UserDefinedFileFilter) {
            thumbnailsPanel.setFileFilter(((UserDefinedFileFilter) item).getFileFilter());
        }

        persistFileFilter();
        waitDisplayer.hide();
    }

    private void persistFileFilter() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        storage.setInt(FileFiltersComboBoxModel.PERSISTED_SELECTED_ITEM_KEY, fileFiltersComboBox.getSelectedIndex());
    }

    private void persistSortOrder() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        storage.setInt(ThumbnailsSortComboBoxModel.PERSISTED_SELECTED_ITEM_KEY, fileSortComboBox.getSelectedIndex());
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        int index0 = e.getIndex0();
        int index1 = e.getIndex1();

        if (index0 != index1 || index0 < 0) {
            return;
        }

        int selectedIndex = fileFiltersComboBox.getSelectedIndex();

        if (selectedIndex == index0) {
            Object selectedItem = fileFiltersComboBox.getSelectedItem();
            if (selectedItem != null) {
                setFileFilter(selectedItem);
            }
        }
    }

    @Override
    public void intervalAdded(ListDataEvent e) {
        // ignore
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
        // ignore
    }

    ThumbnailsPanel getThumbnailsPanel() {
        return thumbnailsPanel;
    }

    void persistViewportPosition() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        storage.setScrollPane(KEY_THUMBNAIL_PANEL_VIEWPORT_VIEW_POSITION, thumbnailsPanelScrollPane);
    }

    void restoreViewportPosition() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    // Waiting until TN panel size was calculated
                    Thread.sleep(2000);
                } catch (Exception ex) {
                    Logger.getLogger(ThumbnailsAreaPanel.class.getName()).log(Level.SEVERE, null, ex);
                }

                EventQueueUtil.invokeInDispatchThread(new Runnable() {

                    @Override
                    public void run() {
                        Preferences preferences = Lookup.getDefault().lookup(Preferences.class);
                        preferences.applyScrollPaneSettings(KEY_THUMBNAIL_PANEL_VIEWPORT_VIEW_POSITION, thumbnailsPanelScrollPane);
                    }
                });
            }
        }, "JPhotoTagger: Restoring viewport position").start();
    }

    void validateViewportPosition() {
        // See: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5066771
        thumbnailsPanelScrollPane.validate();
    }

    private void addBottomComponentsPanel() {
        lookupBottomComponents();
        expandCollapseBottomPanel = new ExpandCollapseComponentPanel(panelBottomComponents);
        expandCollapseBottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(95, 95, 95)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        add(expandCollapseBottomPanel, gbc);
        expandCollapseBottomPanel.readExpandedState();
    }

    private void lookupBottomComponents() {
        Collection<? extends ThumbnailsPanelBottomComponentProvider> providers = Lookup.getDefault().lookupAll(ThumbnailsPanelBottomComponentProvider.class);
        for (ThumbnailsPanelBottomComponentProvider provider : providers) {
            Component component = provider.getComponent();
            GridBagConstraints constraints = createBottomComponentConstraints();
            panelBottomComponents.add(component, constraints);
        }
    }

    private GridBagConstraints createBottomComponentConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 5, 5, 5);
        return gbc;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        panelBottomComponents = new javax.swing.JPanel();
        labelInfo = new javax.swing.JLabel();
        panelDisplayedThumbnailFilters = new javax.swing.JPanel();
        labelFileFilters = new javax.swing.JLabel();
        fileFiltersComboBox = new javax.swing.JComboBox();
        labelFileSort = new javax.swing.JLabel();
        fileSortComboBox = new javax.swing.JComboBox();
        thumbnailsPanelScrollPane = new javax.swing.JScrollPane();
        thumbnailsPanel = new org.jphototagger.program.module.thumbnails.ThumbnailsPanel();

        panelBottomComponents.setName("panelBottomComponents"); // NOI18N
        panelBottomComponents.setLayout(new java.awt.GridBagLayout());

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/module/thumbnails/Bundle"); // NOI18N
        labelInfo.setText(bundle.getString("ThumbnailsAreaPanel.labelInfo.text")); // NOI18N
        labelInfo.setName("labelInfo"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panelBottomComponents.add(labelInfo, gridBagConstraints);

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        panelDisplayedThumbnailFilters.setName("panelDisplayedThumbnailFilters"); // NOI18N
        panelDisplayedThumbnailFilters.setLayout(new java.awt.GridBagLayout());

        labelFileFilters.setText(bundle.getString("ThumbnailsAreaPanel.labelFileFilters.text")); // NOI18N
        labelFileFilters.setName("labelFileFilters"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panelDisplayedThumbnailFilters.add(labelFileFilters, gridBagConstraints);

        fileFiltersComboBox.setModel(fileFiltersComboBoxModel);
        fileFiltersComboBox.setName("fileFiltersComboBox"); // NOI18N
        fileFiltersComboBox.setRenderer(new org.jphototagger.program.module.thumbnails.FileFiltersListCellRenderer());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panelDisplayedThumbnailFilters.add(fileFiltersComboBox, gridBagConstraints);

        labelFileSort.setText(bundle.getString("ThumbnailsAreaPanel.labelFileSort.text")); // NOI18N
        labelFileSort.setName("labelFileSort"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        panelDisplayedThumbnailFilters.add(labelFileSort, gridBagConstraints);

        fileSortComboBox.setModel(thumbnailsSortComboBoxModel);
        fileSortComboBox.setName("fileSortComboBox"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panelDisplayedThumbnailFilters.add(fileSortComboBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        add(panelDisplayedThumbnailFilters, gridBagConstraints);

        thumbnailsPanelScrollPane.setName("thumbnailsPanelScrollPane"); // NOI18N

        thumbnailsPanel.setName("thumbnailsPanel"); // NOI18N

        javax.swing.GroupLayout thumbnailsPanelLayout = new javax.swing.GroupLayout(thumbnailsPanel);
        thumbnailsPanel.setLayout(thumbnailsPanelLayout);
        thumbnailsPanelLayout.setHorizontalGroup(
            thumbnailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 397, Short.MAX_VALUE)
        );
        thumbnailsPanelLayout.setVerticalGroup(
            thumbnailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 263, Short.MAX_VALUE)
        );

        thumbnailsPanelScrollPane.setViewportView(thumbnailsPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(thumbnailsPanelScrollPane, gridBagConstraints);
    }//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox fileFiltersComboBox;
    private javax.swing.JComboBox fileSortComboBox;
    private javax.swing.JLabel labelFileFilters;
    private javax.swing.JLabel labelFileSort;
    private javax.swing.JLabel labelInfo;
    private javax.swing.JPanel panelBottomComponents;
    private javax.swing.JPanel panelDisplayedThumbnailFilters;
    private org.jphototagger.program.module.thumbnails.ThumbnailsPanel thumbnailsPanel;
    private javax.swing.JScrollPane thumbnailsPanelScrollPane;
    // End of variables declaration//GEN-END:variables
}
