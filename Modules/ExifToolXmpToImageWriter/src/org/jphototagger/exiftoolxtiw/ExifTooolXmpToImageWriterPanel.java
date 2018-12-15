package org.jphototagger.exiftoolxtiw;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JProgressBar;
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;

/**
 * @author Elmar Baumann
 */
public class ExifTooolXmpToImageWriterPanel extends PanelExt {

    private static final long serialVersionUID = 1L;

    public ExifTooolXmpToImageWriterPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        MnemonicUtil.setMnemonics(this);
    }

    public JButton getButtonAddDirs() {
        return buttonAddDirs;
    }

    public JButton getButtonConfigure() {
        return buttonConfigure;
    }

    public JButton getButtonExecute() {
        return buttonExecute;
    }

    public JButton getButtonRemoveSelectedDirs() {
        return buttonRemoveSelectedDirs;
    }

    public JButton getButtonCancelExecute() {
        return buttonCancelExecute;
    }

    public JCheckBox getCheckBoxIncludeSubDirs() {
        return checkBoxIncludeSubDirs;
    }

    public JLabel getLabelConfigError() {
        return labelConfigError;
    }

    public JList<java.io.File> getListDirs() {
        return listDirs;
    }

    public JProgressBar getProgressBarExecute() {
        return progressBarExecute;
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panelConfigure = UiFactory.panel();
        buttonConfigure = UiFactory.button();
        labelConfigError = UiFactory.label();
        labelDirs = UiFactory.label();
        scrollPaneDirs = UiFactory.scrollPane();
        listDirs = UiFactory.list();
        checkBoxIncludeSubDirs = UiFactory.checkBox();
        panelDirsButtons = UiFactory.panel();
        buttonRemoveSelectedDirs = UiFactory.button();
        buttonAddDirs = UiFactory.button();
        panelExecute = UiFactory.panel();
        progressBarExecute = UiFactory.progressBar();
        panelExecuteButtons = UiFactory.panel();
        buttonCancelExecute = UiFactory.button();
        buttonExecute = UiFactory.button();

        setLayout(new java.awt.GridBagLayout());

        panelConfigure.setLayout(new java.awt.GridBagLayout());

        buttonConfigure.setText(Bundle.getString(getClass(), "ExifTooolXmpToImageWriterPanel.buttonConfigure.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelConfigure.add(buttonConfigure, gridBagConstraints);

        labelConfigError.setForeground(java.awt.Color.RED);
        labelConfigError.setText(Bundle.getString(getClass(), "ExifTooolXmpToImageWriterPanel.labelConfigError.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 10, 0, 0);
        panelConfigure.add(labelConfigError, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(panelConfigure, gridBagConstraints);

        labelDirs.setText(Bundle.getString(getClass(), "ExifTooolXmpToImageWriterPanel.labelDirs.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(7, 0, 0, 0);
        add(labelDirs, gridBagConstraints);

        scrollPaneDirs.setViewportView(listDirs);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        add(scrollPaneDirs, gridBagConstraints);

        checkBoxIncludeSubDirs.setText(Bundle.getString(getClass(), "ExifTooolXmpToImageWriterPanel.checkBoxIncludeSubDirs.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        add(checkBoxIncludeSubDirs, gridBagConstraints);

        panelDirsButtons.setLayout(new java.awt.GridBagLayout());

        buttonRemoveSelectedDirs.setText(Bundle.getString(getClass(), "ExifTooolXmpToImageWriterPanel.buttonRemoveSelectedDirs.text")); // NOI18N
        panelDirsButtons.add(buttonRemoveSelectedDirs, new java.awt.GridBagConstraints());

        buttonAddDirs.setText(Bundle.getString(getClass(), "ExifTooolXmpToImageWriterPanel.buttonAddDirs.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelDirsButtons.add(buttonAddDirs, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 0);
        add(panelDirsButtons, gridBagConstraints);

        panelExecute.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panelExecute.add(progressBarExecute, gridBagConstraints);

        panelExecuteButtons.setLayout(new java.awt.GridBagLayout());

        buttonCancelExecute.setText(Bundle.getString(getClass(), "ExifTooolXmpToImageWriterPanel.buttonCancelExecute.text")); // NOI18N
        panelExecuteButtons.add(buttonCancelExecute, new java.awt.GridBagConstraints());

        buttonExecute.setText(Bundle.getString(getClass(), "ExifTooolXmpToImageWriterPanel.buttonExecute.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelExecuteButtons.add(buttonExecute, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        panelExecute.add(panelExecuteButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(10, 0, 0, 0);
        add(panelExecute, gridBagConstraints);
    }

    private javax.swing.JButton buttonAddDirs;
    private javax.swing.JButton buttonCancelExecute;
    private javax.swing.JButton buttonConfigure;
    private javax.swing.JButton buttonExecute;
    private javax.swing.JButton buttonRemoveSelectedDirs;
    private javax.swing.JCheckBox checkBoxIncludeSubDirs;
    private javax.swing.JLabel labelConfigError;
    private javax.swing.JLabel labelDirs;
    private javax.swing.JList<java.io.File> listDirs;
    private javax.swing.JPanel panelConfigure;
    private javax.swing.JPanel panelDirsButtons;
    private javax.swing.JPanel panelExecute;
    private javax.swing.JPanel panelExecuteButtons;
    private javax.swing.JProgressBar progressBarExecute;
    private javax.swing.JScrollPane scrollPaneDirs;
}
