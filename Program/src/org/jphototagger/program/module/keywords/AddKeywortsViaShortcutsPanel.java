package org.jphototagger.program.module.keywords;

import javax.swing.JLabel;
import javax.swing.JTable;

/**
 * UI for adding Keywords via Shortcuts.
 *
 * @author Elmar Baumann
 */
public class AddKeywortsViaShortcutsPanel extends javax.swing.JPanel {

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

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        labelInfo = org.jphototagger.resources.UiFactory.label();
        scrollPane = org.jphototagger.resources.UiFactory.scrollPane();
        table = org.jphototagger.resources.UiFactory.table();

        setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(labelInfo, gridBagConstraints);

        scrollPane.setPreferredSize(org.jphototagger.resources.UiFactory.dimension(400, 300));
        scrollPane.setViewportView(table);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        add(scrollPane, gridBagConstraints);
    }//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel labelInfo;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables

}
