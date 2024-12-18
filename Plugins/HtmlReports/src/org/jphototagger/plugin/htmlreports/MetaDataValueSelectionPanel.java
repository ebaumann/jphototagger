package org.jphototagger.plugin.htmlreports;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.MetaDataValueProvider;
import org.jphototagger.lib.api.PositionProviderAscendingComparator;
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public class MetaDataValueSelectionPanel extends PanelExt {

    private static final long serialVersionUID = 1L;
    private final ComboBoxModel<Object> comboBoxModel = new MetaDataValuesComboBoxModel();
    private final ListCellRenderer<Object> listCellRenderer = new MetaDataValuesListCellRenderer();

    public MetaDataValueSelectionPanel() {
        initComponents();
    }

    private void addMetaDataValueSelectionPanel() {
        firePropertyChange("add", null, null);
    }

    private void removeMetaDataValueSelectionPanel() {
        firePropertyChange("remove", null, null);
    }

    private void valueChanged() {
        Object selectedItem = comboBoxModel.getSelectedItem();
        firePropertyChange("metaDataValue", null, selectedItem);
        if (MetaDataValuesComboBoxModel.NONE.equals(selectedItem)) {
            firePropertyChange("remove", null, null);
        }
    }

    MetaDataValue getSelectedMetaDataValue() {
        Object selectedItem = comboBoxModel.getSelectedItem();
        return selectedItem instanceof MetaDataValue
                ? (MetaDataValue) selectedItem
                : null;
    }

    void enableRemove() {
        buttonRemove.setEnabled(true);
    }

    void select(MetaDataValue metaDataValue) {
        metaDataValuesComboBox.setSelectedItem(metaDataValue);
    }

    void selectNone() {
        metaDataValuesComboBox.setSelectedItem(MetaDataValuesComboBoxModel.NONE);
    }

    private static class MetaDataValuesListCellRenderer extends DefaultListCellRenderer {

        private static final long serialVersionUID = 1L;

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof MetaDataValue) {
                MetaDataValue metaDataValue = (MetaDataValue) value;
                label.setIcon(metaDataValue.getCategoryIcon());
                label.setText(metaDataValue.getDescription());
            }

            return label;
        }
    }

    private static class MetaDataValuesComboBoxModel extends DefaultComboBoxModel<Object> {

        private static final long serialVersionUID = 1L;
        private static final Collection<? extends MetaDataValueProvider> META_DATA_VALUE_PROVIDERS;
        private static final String NONE = Bundle.getString(MetaDataValuesComboBoxModel.class, "MetaDataValuesComboBoxModel.NoneElement");

        static {
            List<MetaDataValueProvider> sortedMetaDataValueProviders =
                    new ArrayList<MetaDataValueProvider>(Lookup.getDefault().lookupAll(MetaDataValueProvider.class));
            Collections.sort(sortedMetaDataValueProviders, PositionProviderAscendingComparator.INSTANCE);
            META_DATA_VALUE_PROVIDERS = sortedMetaDataValueProviders;
        }

        private MetaDataValuesComboBoxModel() {
            addElements();
        }

        private void addElements() {
            addElement(NONE);
            for (MetaDataValueProvider metaDataValueProvider : META_DATA_VALUE_PROVIDERS) {
                Collection<MetaDataValue> providedMetaDataValues = metaDataValueProvider.getProvidedValues();
                for (MetaDataValue providedMetaDataValue : providedMetaDataValues) {
                    addElement(providedMetaDataValue);
                }
            }
        }
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        metaDataValuesComboBox = UiFactory.comboBox();
        buttonAdd = UiFactory.button();
        buttonRemove = UiFactory.button();

        
        setLayout(new java.awt.GridBagLayout());

        metaDataValuesComboBox.setModel(comboBoxModel);
        metaDataValuesComboBox.setName("metaDataValuesComboBox"); // NOI18N
        metaDataValuesComboBox.setRenderer(listCellRenderer);
        metaDataValuesComboBox.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                metaDataValuesComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(metaDataValuesComboBox, gridBagConstraints);

        buttonAdd.setIcon(org.jphototagger.resources.Icons.getIcon("icon_add.png"));
        buttonAdd.setMargin(UiFactory.insets(0, 0, 0, 0));
        buttonAdd.setName("buttonAdd"); // NOI18N
        buttonAdd.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        add(buttonAdd, gridBagConstraints);

        buttonRemove.setIcon(org.jphototagger.resources.Icons.getIcon("icon_delete.png"));
        buttonRemove.setEnabled(false);
        buttonRemove.setMargin(UiFactory.insets(0, 0, 0, 0));
        buttonRemove.setName("buttonRemove"); // NOI18N
        buttonRemove.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        add(buttonRemove, gridBagConstraints);
    }

    private void buttonAddActionPerformed(java.awt.event.ActionEvent evt) {
        addMetaDataValueSelectionPanel();
    }

    private void buttonRemoveActionPerformed(java.awt.event.ActionEvent evt) {
        removeMetaDataValueSelectionPanel();
    }

    private void metaDataValuesComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
        valueChanged();
    }

    private javax.swing.JButton buttonAdd;
    private javax.swing.JButton buttonRemove;
    private javax.swing.JComboBox<Object> metaDataValuesComboBox;
}
