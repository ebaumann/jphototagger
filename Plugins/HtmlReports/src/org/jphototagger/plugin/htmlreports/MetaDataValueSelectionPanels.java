package org.jphototagger.plugin.htmlreports;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.resources.UiFactory;

/**
 * @author Elmar Baumann
 */
public class MetaDataValueSelectionPanels extends PanelExt implements PropertyChangeListener {

    private static final long serialVersionUID = 1L;
    private boolean setValues;

    public MetaDataValueSelectionPanels() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        addFillPanel();
        firstSelectionPanel.addPropertyChangeListener(this);
        setValues(DefaultMetaDataValues.INSTANCE.getValues());
    }

    private void addFillPanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        panels.add(fillPanel, gbc);
    }

    private void addPanel(MetaDataValueSelectionPanel selectionPanel) {
        selectionPanel.enableRemove();
        selectionPanel.addPropertyChangeListener(this);

        GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        gbc.insets = UiFactory.insets(5, 0, 0, 0);
        panels.remove(fillPanel);
        panels.add(selectionPanel, gbc);
        addFillPanel();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        if ("add".equals(propertyName)) {
            addPanel(new MetaDataValueSelectionPanel());
            repaintPanels();
        } else if ("remove".equals(propertyName)) {
            Object source = evt.getSource();
            if (!firstSelectionPanel.equals(source)) {
                panels.remove((Component) source);
                persistSelectedValues();
                repaintPanels();
            }
        } else if ("metaDataValue".equals(propertyName)) {
            persistSelectedValues();
        }
    }

    private void repaintPanels() {
        ComponentUtil.forceRepaint(panels);
        Container parent = getParent();
        if (parent != null) {
            ComponentUtil.forceRepaint(parent);
        }
    }

    private void persistSelectedValues() {
        if (!setValues) {
            DefaultMetaDataValues.INSTANCE.setValues(getSelectedValues());
        }
    }

    public Collection<MetaDataValue> getSelectedValues() {
        List<MetaDataValue> selectedValues = new LinkedList<>();
        for (Component component : panels.getComponents()) {
            if (component instanceof MetaDataValueSelectionPanel) {
                MetaDataValueSelectionPanel selectionPanel = (MetaDataValueSelectionPanel) component;
                MetaDataValue selectedValue = selectionPanel.getSelectedMetaDataValue();
                if (selectedValue != null) {
                    selectedValues.add(selectedValue);
                }
            }
        }
        return selectedValues;
    }

    void setValues(Collection<MetaDataValue> metaDataValues) {
        setValues = true;
        removeAllPanels();
        firstSelectionPanel.selectNone();
        boolean firstPanelSet = false;
        for (MetaDataValue metaDataValue : metaDataValues) {
            MetaDataValueSelectionPanel selectionPanel = firstPanelSet
                    ? new MetaDataValueSelectionPanel()
                    : firstSelectionPanel;
            selectionPanel.select(metaDataValue);
            if (firstPanelSet) {
                addPanel(selectionPanel);
            }
            firstPanelSet = true;
        }
        setValues = false;
    }

    private void removeAllPanels() {
        Component[] components = getComponents();
        List<MetaDataValueSelectionPanel> panelsToRemove = new ArrayList<>(components.length);
        for (Component component : components) {
            if (component instanceof MetaDataValueSelectionPanel) {
                MetaDataValueSelectionPanel panel = (MetaDataValueSelectionPanel) component;
                if (panel != firstSelectionPanel) {
                    panelsToRemove.add(panel);
                }
            }
        }
        for (MetaDataValueSelectionPanel panel : panelsToRemove) {
            panels.remove(panel);
        }
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        fillPanel = UiFactory.panel();
        scrollPane = UiFactory.scrollPane();
        panels = UiFactory.panel();
        firstSelectionPanel = new org.jphototagger.plugin.htmlreports.MetaDataValueSelectionPanel();

        fillPanel.setName("fillPanel"); // NOI18N
        fillPanel.setLayout(new java.awt.GridBagLayout());

        
        setLayout(new java.awt.GridBagLayout());

        scrollPane.setName("scrollPane"); // NOI18N

        panels.setName("panels"); // NOI18N
        panels.setLayout(new java.awt.GridBagLayout());

        firstSelectionPanel.setName("firstSelectionPanel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panels.add(firstSelectionPanel, gridBagConstraints);

        scrollPane.setViewportView(panels);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(scrollPane, gridBagConstraints);
    }

    private javax.swing.JPanel fillPanel;
    private org.jphototagger.plugin.htmlreports.MetaDataValueSelectionPanel firstSelectionPanel;
    private javax.swing.JPanel panels;
    private javax.swing.JScrollPane scrollPane;
}
