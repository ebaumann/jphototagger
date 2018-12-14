package org.jphototagger.program.module.thumbnails;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
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
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesChangedEvent;
import org.jphototagger.api.windows.WaitDisplayer;
import org.jphototagger.domain.filefilter.UserDefinedFileFilter;
import org.jphototagger.domain.thumbnails.ThumbnailsPanelBottomComponentProvider;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.swing.ExpandCollapseComponentPanel;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.program.settings.AppPreferencesKeys;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public class ThumbnailsAreaPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;
    private static final String KEY_THUMBNAIL_PANEL_VIEWPORT_VIEW_POSITION = "org.jphototagger.program.view.panels.controller.ViewportViewPosition";
    private final FileFiltersComboBoxModel fileFiltersComboBoxModel = new FileFiltersComboBoxModel();
    private final ThumbnailsSortComboBoxModel thumbnailsSortComboBoxModel = new ThumbnailsSortComboBoxModel();
    private final Preferences prefs = Lookup.getDefault().lookup(Preferences.class);;
    private ExpandCollapseComponentPanel expandCollapseBottomComponentsPanel;
    private boolean bottomComponentsPanelAdded;

    public ThumbnailsAreaPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        createBottomPanel();
        thumbnailsPanelScrollPane.getVerticalScrollBar().setUnitIncrement(30);
        comboBoxFileSort.addItemListener(fileSortChangedListener);
        comboBoxFileFilters.addItemListener(fileFilterChangedListener);
        fileFiltersComboBoxModel.addListDataListener(fileFilterListDataListener);
        thumbnailsPanel.setViewport(thumbnailsPanelScrollPane.getViewport());
        MnemonicUtil.setMnemonics(this);
        fileFiltersComboBoxModel.selectPersistedItem();
        thumbnailsSortComboBoxModel.selectPersistedItem();
        thumbnailsPanel.setFileSortComparator(getFileSortComparator());
        initSetFileFilter(comboBoxFileFilters.getSelectedItem());
        AnnotationProcessor.process(this);
    }

    private void initSetFileFilter(Object item) {
        if (item instanceof FileFilter) {
            thumbnailsPanel.setFileFilter((FileFilter) item);
        } else if (item instanceof UserDefinedFileFilter) {
            thumbnailsPanel.setFileFilter(((UserDefinedFileFilter) item).getFileFilter());
        }
    }

    private void createBottomPanel() {
        lookupAndAddBottomComponents();
        createExpandCollapseBottomComponentsPanel();
        boolean isAdd = prefs == null || !prefs.containsKey(AppPreferencesKeys.KEY_UI_DISPLAY_THUMBNAILS_BOTTOM_PANEL)
                || (prefs.containsKey(AppPreferencesKeys.KEY_UI_DISPLAY_THUMBNAILS_BOTTOM_PANEL)
                && prefs.getBoolean(AppPreferencesKeys.KEY_UI_DISPLAY_THUMBNAILS_BOTTOM_PANEL));
        if (isAdd) {
            addBottomComponentsPanel();
        }
    }

    private final ItemListener fileSortChangedListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                persistSortOrder();
                WaitDisplayer waitDisplayer = Lookup.getDefault().lookup(WaitDisplayer.class);
                waitDisplayer.show();
                thumbnailsPanel.setFileSortComparator(getFileSortComparator());
                waitDisplayer.hide();
            }
        }

        private void persistSortOrder() {
            prefs.setInt(ThumbnailsSortComboBoxModel.PERSISTED_SELECTED_ITEM_KEY, comboBoxFileSort.getSelectedIndex());
        }
    };

    private Comparator<File> getFileSortComparator() {
        ThumbnailsSortComboBoxModel.FileSorter fileSorter = (ThumbnailsSortComboBoxModel.FileSorter) comboBoxFileSort.getSelectedItem();
        return fileSorter.getComparator();
    }

    private final ItemListener fileFilterChangedListener = new ItemListener() {

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                setFileFilter(e.getItem());
            }
        }
    };

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
        prefs.setInt(FileFiltersComboBoxModel.PERSISTED_SELECTED_ITEM_KEY, comboBoxFileFilters.getSelectedIndex());
    }

    private final ListDataListener fileFilterListDataListener = new ListDataListener() {


        @Override
        public void contentsChanged(ListDataEvent e) {
            int index0 = e.getIndex0();
            int index1 = e.getIndex1();
            if (index0 != index1 || index0 < 0) {
                return;
            }
            int selectedIndex = comboBoxFileFilters.getSelectedIndex();
            if (selectedIndex == index0) {
                Object selectedItem = comboBoxFileFilters.getSelectedItem();
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
    };

    ThumbnailsPanel getThumbnailsPanel() {
        return thumbnailsPanel;
    }

    void persistViewportPosition() {
        prefs.setScrollPane(KEY_THUMBNAIL_PANEL_VIEWPORT_VIEW_POSITION, thumbnailsPanelScrollPane);
    }

    void restoreViewportPosition() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    // Waiting until TN panel size was calculated
                    Thread.sleep(2000);
                } catch (Throwable t) {
                    Logger.getLogger(ThumbnailsAreaPanel.class.getName()).log(Level.SEVERE, null, t);
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

    private void removeBottomComponentsPanel() {
        if (bottomComponentsPanelAdded) {
            remove(expandCollapseBottomComponentsPanel);
            bottomComponentsPanelAdded = false;
        }
    }

    private void addBottomComponentsPanel() {
        if (!bottomComponentsPanelAdded) {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            gbc.anchor = GridBagConstraints.WEST;
            add(expandCollapseBottomComponentsPanel, gbc);
            expandCollapseBottomComponentsPanel.readExpandedState();
            bottomComponentsPanelAdded = true;
        }
    }

    private void lookupAndAddBottomComponents() {
        Collection<? extends ThumbnailsPanelBottomComponentProvider> providers = Lookup.getDefault().lookupAll(ThumbnailsPanelBottomComponentProvider.class);
        for (ThumbnailsPanelBottomComponentProvider provider : providers) {
            Component component = provider.getComponent();
            GridBagConstraints constraints = createBottomComponentConstraints();
            panelBottomComponents.add(component, constraints);
        }
    }

    private void createExpandCollapseBottomComponentsPanel() {
        expandCollapseBottomComponentsPanel = new ExpandCollapseComponentPanel(panelBottomComponents);
        expandCollapseBottomComponentsPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(95, 95, 95)));
    }

    private GridBagConstraints createBottomComponentConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 5, 5);
        return gbc;
    }

    @EventSubscriber(eventClass = PreferencesChangedEvent.class)
    public void preferencesChanged(PreferencesChangedEvent evt) {
        if (AppPreferencesKeys.KEY_UI_DISPLAY_THUMBNAILS_BOTTOM_PANEL.equals(evt.getKey())) {
            Boolean display = (Boolean) evt.getNewValue();
            if (display) {
                addBottomComponentsPanel();
            } else {
                removeBottomComponentsPanel();
            }
            ComponentUtil.forceRepaint(this);
        }
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
        comboBoxFileFilters = new javax.swing.JComboBox<>();
        labelFileSort = new javax.swing.JLabel();
        comboBoxFileSort = new javax.swing.JComboBox<>();
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
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelDisplayedThumbnailFilters.add(labelFileFilters, gridBagConstraints);

        comboBoxFileFilters.setModel(fileFiltersComboBoxModel);
        comboBoxFileFilters.setName("comboBoxFileFilters"); // NOI18N
        comboBoxFileFilters.setRenderer(new org.jphototagger.program.module.thumbnails.FileFiltersListCellRenderer());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelDisplayedThumbnailFilters.add(comboBoxFileFilters, gridBagConstraints);

        labelFileSort.setText(bundle.getString("ThumbnailsAreaPanel.labelFileSort.text")); // NOI18N
        labelFileSort.setName("labelFileSort"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 10, 0, 0);
        panelDisplayedThumbnailFilters.add(labelFileSort, gridBagConstraints);

        comboBoxFileSort.setModel(thumbnailsSortComboBoxModel);
        comboBoxFileSort.setName("comboBoxFileSort"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelDisplayedThumbnailFilters.add(comboBoxFileSort, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 5, 0);
        add(panelDisplayedThumbnailFilters, gridBagConstraints);

        thumbnailsPanelScrollPane.setName("thumbnailsPanelScrollPane"); // NOI18N

        thumbnailsPanel.setName("thumbnailsPanel"); // NOI18N
        thumbnailsPanel.setLayout(new java.awt.GridBagLayout());
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
    private javax.swing.JComboBox<Object> comboBoxFileFilters;
    private javax.swing.JComboBox<Object> comboBoxFileSort;
    private javax.swing.JLabel labelFileFilters;
    private javax.swing.JLabel labelFileSort;
    private javax.swing.JLabel labelInfo;
    private javax.swing.JPanel panelBottomComponents;
    private javax.swing.JPanel panelDisplayedThumbnailFilters;
    private org.jphototagger.program.module.thumbnails.ThumbnailsPanel thumbnailsPanel;
    private javax.swing.JScrollPane thumbnailsPanelScrollPane;
    // End of variables declaration//GEN-END:variables
}
