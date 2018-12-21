package org.jphototagger.program.module.keywords;

import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JTable;
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.resources.UiFactory;

/**
 * UI for adding Keywords via Shortcuts.
 *
 * @author Elmar Baumann
 */
public class AddKeywortsViaShortcutsPanel extends PanelExt {

    private static final long serialVersionUID = 1L;

    public AddKeywortsViaShortcutsPanel() {
        initComponents();
    }

    public JLabel getLabelInfo() {
        return labelInfo;
    }

    public JTable getTable() {
        return table;
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        labelInfo = UiFactory.label();
        scrollPane = UiFactory.scrollPane();
        table = UiFactory.table();

        setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(labelInfo, gridBagConstraints);

        scrollPane.setPreferredSize(new Dimension(400, 300));
        scrollPane.setViewportView(table);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        add(scrollPane, gridBagConstraints);
    }

    private javax.swing.JLabel labelInfo;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTable table;
}
