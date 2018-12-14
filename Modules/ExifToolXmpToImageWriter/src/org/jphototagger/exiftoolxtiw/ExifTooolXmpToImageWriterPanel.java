package org.jphototagger.exiftoolxtiw;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JProgressBar;
import org.jphototagger.lib.swing.util.MnemonicUtil;

/**
 * @author Elmar Baumann
 */
public class ExifTooolXmpToImageWriterPanel extends javax.swing.JPanel {

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

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        panelConfigure = new javax.swing.JPanel();
        buttonConfigure = new javax.swing.JButton();
        labelConfigError = new javax.swing.JLabel();
        labelDirs = new javax.swing.JLabel();
        scrollPaneDirs = new javax.swing.JScrollPane();
        listDirs = new javax.swing.JList<java.io.File>();
        checkBoxIncludeSubDirs = new javax.swing.JCheckBox();
        panelDirsButtons = new javax.swing.JPanel();
        buttonRemoveSelectedDirs = new javax.swing.JButton();
        buttonAddDirs = new javax.swing.JButton();
        panelExecute = new javax.swing.JPanel();
        progressBarExecute = new javax.swing.JProgressBar();
        panelExecuteButtons = new javax.swing.JPanel();
        buttonCancelExecute = new javax.swing.JButton();
        buttonExecute = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        panelConfigure.setLayout(new java.awt.GridBagLayout());

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/exiftoolxtiw/Bundle"); // NOI18N
        buttonConfigure.setText(bundle.getString("ExifTooolXmpToImageWriterPanel.buttonConfigure.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelConfigure.add(buttonConfigure, gridBagConstraints);

        labelConfigError.setForeground(java.awt.Color.RED);
        labelConfigError.setText(bundle.getString("ExifTooolXmpToImageWriterPanel.labelConfigError.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 10, 0, 0);
        panelConfigure.add(labelConfigError, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(panelConfigure, gridBagConstraints);

        labelDirs.setText(bundle.getString("ExifTooolXmpToImageWriterPanel.labelDirs.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(7, 0, 0, 0);
        add(labelDirs, gridBagConstraints);

        scrollPaneDirs.setViewportView(listDirs);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        add(scrollPaneDirs, gridBagConstraints);

        checkBoxIncludeSubDirs.setText(bundle.getString("ExifTooolXmpToImageWriterPanel.checkBoxIncludeSubDirs.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        add(checkBoxIncludeSubDirs, gridBagConstraints);

        panelDirsButtons.setLayout(new java.awt.GridBagLayout());

        buttonRemoveSelectedDirs.setText(bundle.getString("ExifTooolXmpToImageWriterPanel.buttonRemoveSelectedDirs.text")); // NOI18N
        panelDirsButtons.add(buttonRemoveSelectedDirs, new java.awt.GridBagConstraints());

        buttonAddDirs.setText(bundle.getString("ExifTooolXmpToImageWriterPanel.buttonAddDirs.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelDirsButtons.add(buttonAddDirs, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 0, 0);
        add(panelDirsButtons, gridBagConstraints);

        panelExecute.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panelExecute.add(progressBarExecute, gridBagConstraints);

        panelExecuteButtons.setLayout(new java.awt.GridBagLayout());

        buttonCancelExecute.setText(bundle.getString("ExifTooolXmpToImageWriterPanel.buttonCancelExecute.text")); // NOI18N
        panelExecuteButtons.add(buttonCancelExecute, new java.awt.GridBagConstraints());

        buttonExecute.setText(bundle.getString("ExifTooolXmpToImageWriterPanel.buttonExecute.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelExecuteButtons.add(buttonExecute, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        panelExecute.add(panelExecuteButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(10, 0, 0, 0);
        add(panelExecute, gridBagConstraints);
    }//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    // End of variables declaration//GEN-END:variables

}
