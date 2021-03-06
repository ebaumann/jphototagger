package org.jphototagger.maintainance.browse;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.resources.UiFactory;

/**
 * @author Elmar Baumann
 */
public class ResultSetBrowserPanel extends PanelExt {

    private static final long serialVersionUID = 1L;

    public ResultSetBrowserPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        progressBar.setVisible(false);
        MnemonicUtil.setMnemonics(labelFilter);
    }

    public JButton getButtonExecuteSql() {
        return buttonExecuteSql;
    }

    public JButton getButtonLoad() {
        return buttonLoad;
    }

    public JButton getButtonSave() {
        return buttonSave;
    }

    public JLabel getLabelDescription() {
        return labelDescription;
    }

    public JLabel getLabelFilter() {
        return labelFilter;
    }

    public JTextField getTextFieldFilter() {
        return textFieldFilter;
    }

    public JTable getTable() {
        return table;
    }

    public JTextArea getTextAreaSql() {
        return textAreaSql;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        labelSql = UiFactory.label();
        labelDescription = UiFactory.label();
        panelButtons = UiFactory.panel();
        buttonLoad = UiFactory.button();
        buttonSave = UiFactory.button();
        buttonExecuteSql = UiFactory.button();
        srollPaneSql = UiFactory.scrollPane();
        textAreaSql = UiFactory.textArea();
        panelFilter = UiFactory.panel();
        labelFilter = UiFactory.label();
        textFieldFilter = UiFactory.textField();
        scrollPaneTable = UiFactory.scrollPane();
        table = UiFactory.table();
        progressBar = UiFactory.progressBar();

        setLayout(new java.awt.GridBagLayout());

        labelSql.setText(org.jphototagger.lib.util.Bundle.getString(ResultSetBrowserPanel.class, "ResultSetBrowserPanel.labelSql.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(labelSql, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        add(labelDescription, gridBagConstraints);

        panelButtons.setLayout(new java.awt.GridBagLayout());
        panelButtons.add(buttonLoad, new java.awt.GridBagConstraints());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelButtons.add(buttonSave, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelButtons.add(buttonExecuteSql, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        add(panelButtons, gridBagConstraints);

        srollPaneSql.setPreferredSize(UiFactory.dimension(600, 150));

        textAreaSql.setColumns(40);
        textAreaSql.setRows(5);
        srollPaneSql.setViewportView(textAreaSql);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.15;
        gridBagConstraints.insets = UiFactory.insets(3, 0, 0, 0);
        add(srollPaneSql, gridBagConstraints);

        panelFilter.setLayout(new java.awt.GridBagLayout());

        labelFilter.setLabelFor(textFieldFilter);
        labelFilter.setText(org.jphototagger.lib.util.Bundle.getString(ResultSetBrowserPanel.class, "ResultSetBrowserPanel.labelFilter.text")); // NOI18N
        panelFilter.add(labelFilter, new java.awt.GridBagConstraints());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelFilter.add(textFieldFilter, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        add(panelFilter, gridBagConstraints);

        scrollPaneTable.setPreferredSize(UiFactory.dimension(800, 400));

        table.setAutoCreateRowSorter(true);
        table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        scrollPaneTable.setViewportView(table);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.85;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        add(scrollPaneTable, gridBagConstraints);

        progressBar.setIndeterminate(true);
        progressBar.setString(org.jphototagger.lib.util.Bundle.getString(ResultSetBrowserPanel.class, "ResultSetBrowserPanel.progressBar.string")); // NOI18N
        progressBar.setStringPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        add(progressBar, gridBagConstraints);
    }

    private javax.swing.JButton buttonExecuteSql;
    private javax.swing.JButton buttonLoad;
    private javax.swing.JButton buttonSave;
    private javax.swing.JLabel labelDescription;
    private javax.swing.JLabel labelFilter;
    private javax.swing.JLabel labelSql;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelFilter;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JScrollPane scrollPaneTable;
    private javax.swing.JScrollPane srollPaneSql;
    private javax.swing.JTable table;
    private javax.swing.JTextArea textAreaSql;
    private javax.swing.JTextField textFieldFilter;
}
