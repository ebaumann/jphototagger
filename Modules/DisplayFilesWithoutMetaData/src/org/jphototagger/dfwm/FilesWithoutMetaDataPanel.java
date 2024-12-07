package org.jphototagger.dfwm;

import java.awt.Component;
import java.util.Arrays;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.windows.SelectionItemSelectedEvent;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.selections.NoMetadataValues;
import org.jphototagger.lib.lookup.SelectedListItemsLookup;
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class FilesWithoutMetaDataPanel extends PanelExt {

    private static final long serialVersionUID = 1L;
    private final SelectedListItemsLookup selectedListItemsLookup;
    private final DisplayFilesWithoutMetaDataAction displayFilesWithoutMetaDataAction;
    private final FilesWithoutMetaDataMetadataListModel listModel = new FilesWithoutMetaDataMetadataListModel();
    private static final String PREFERENCES_KEY = "org.jphototagger.program.app.ui.AppPanel.listNoMetadata";
    private volatile boolean clearingSelection;

    public FilesWithoutMetaDataPanel() {
        initComponents();
        selectedListItemsLookup = new SelectedListItemsLookup(list);
        displayFilesWithoutMetaDataAction = new DisplayFilesWithoutMetaDataAction(selectedListItemsLookup.getLookup());
        postInitComponents();
    }

    private void postInitComponents() {
        readPreferences();
        list.addListSelectionListener(sectionItemSelectedEventPublisher);
        AnnotationProcessor.process(this);
    }

    public void readPreferences() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        prefs.applySelectedIndices(PREFERENCES_KEY, list);
    }

    public void writePreferences() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        prefs.setSelectedIndices(PREFERENCES_KEY, list);
    }

    private class FilesWithoutMetaDataMetadataListModel extends DefaultListModel<Object> {

        private static final long serialVersionUID = 1L;

        private FilesWithoutMetaDataMetadataListModel() {
            addMetaDataValues();
        }

        private void addMetaDataValues() {
            for (MetaDataValue value : NoMetadataValues.get()) {
                addElement(value);
            }
        }
    }

    @EventSubscriber(eventClass = SelectionItemSelectedEvent.class)
    public void sectionItemSelected(SelectionItemSelectedEvent evt) {
        Object source = evt.getSource();
        if (source != list) {
            clearingSelection = true;
            list.clearSelection();
            clearingSelection = false;
        }
    }

    private final ListSelectionListener sectionItemSelectedEventPublisher = new ListSelectionListener() {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!clearingSelection && !e.getValueIsAdjusting()) {
                EventBus.publish(new SelectionItemSelectedEvent(list, Arrays.asList(list.getSelectedValues())));
            }
        }
    };

    private static class FilesWithoutMetaDataListCellRenderer extends DefaultListCellRenderer {

        private static final long serialVersionUID = 1L;

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof MetaDataValue) {
                MetaDataValue metaDataValue = (MetaDataValue) value;

                label.setText(metaDataValue.getDescription());
                label.setIcon(metaDataValue.getCategoryIcon());
            }

            return label;
        }
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        scrollPane = UiFactory.scrollPane();
        list = UiFactory.jxList();

        
        setLayout(new java.awt.GridBagLayout());

        scrollPane.setName("scrollPane"); // NOI18N
        scrollPane.setPreferredSize(UiFactory.dimension(400, 300));

        list.setModel(listModel);
        list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new FilesWithoutMetaDataListCellRenderer());
        list.setName("list"); // NOI18N
        scrollPane.setViewportView(list);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(scrollPane, gridBagConstraints);
    }

    private org.jdesktop.swingx.JXList list;
    private javax.swing.JScrollPane scrollPane;
}
