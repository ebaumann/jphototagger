package org.jphototagger.lib.swing;

import java.awt.event.KeyEvent;
import javax.swing.JDialog;
import org.jphototagger.lib.swing.util.ComponentUtil;

/**
 * Modal text input dialog writing it's location to a properties object on demand.
 *
 * @author Elmar Baumann
 */
public final class InputDialog extends Dialog {

    private static final long  serialVersionUID = 1L;
    private boolean accepted;

    public InputDialog() {
        super(ComponentUtil.findFrameWithIcon(), true);
        init("", "");
    }

    public InputDialog(JDialog owner) {
        super(owner, true);
        init("", "");
    }

    public InputDialog(String info, String input) {
        super(ComponentUtil.findFrameWithIcon(), true);
        init(info, input);
    }

    public InputDialog(JDialog owner, String info, String input) {
        super(owner, true);
        init(info, input);
    }

    private void init(String info, String input) {
        initComponents();
        labelPrompt.setText(info);
        textFieldInput.setText(input);
    }

    public void setInfo(String info) {
        if (info == null) {
            throw new NullPointerException("info == null");
        }
        labelPrompt.setText(info);
    }

    public void setInput(String input) {
        if (input == null) {
            throw new NullPointerException("input == null");
        }
        textFieldInput.setText(input);
    }

    /**
     * @return true if closed with OK
     */
    public boolean isAccepted() {
        return accepted;
    }

    public String getInput() {
        return textFieldInput.getText();
    }
    private void ok() {
        accepted = true;
        setVisible(false);
    }

    private void cancel() {
        accepted = false;
        setVisible(false);
    }

    @Override
    protected void escape() {
        cancel();
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        panelContent = new javax.swing.JPanel();
        labelPrompt = new javax.swing.JLabel();
        textFieldInput = new javax.swing.JTextField();
        panelButtons = new javax.swing.JPanel();
        buttonCancel = new javax.swing.JButton();
        buttonOk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/lib/swing/Bundle"); // NOI18N
        setTitle(bundle.getString("InputDialog.title")); // NOI18N
        setName("Form"); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panelContent.setName("panelContent"); // NOI18N
        panelContent.setLayout(new java.awt.GridBagLayout());

        labelPrompt.setText(bundle.getString("InputDialog.labelPrompt.text")); // NOI18N
        labelPrompt.setName("labelPrompt"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panelContent.add(labelPrompt, gridBagConstraints);

        textFieldInput.setColumns(20);
        textFieldInput.setName("textFieldInput"); // NOI18N
        textFieldInput.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                textFieldInputKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        panelContent.add(textFieldInput, gridBagConstraints);

        panelButtons.setName("panelButtons"); // NOI18N
        panelButtons.setLayout(new java.awt.GridBagLayout());

        buttonCancel.setMnemonic('a');
        buttonCancel.setText(bundle.getString("InputDialog.buttonCancel.text")); // NOI18N
        buttonCancel.setName("buttonCancel"); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });
        panelButtons.add(buttonCancel, new java.awt.GridBagConstraints());

        buttonOk.setMnemonic('o');
        buttonOk.setText(bundle.getString("InputDialog.buttonOk.text")); // NOI18N
        buttonOk.setName("buttonOk"); // NOI18N
        buttonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOkActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panelButtons.add(buttonOk, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 0, 0);
        panelContent.add(panelButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        getContentPane().add(panelContent, gridBagConstraints);

        pack();
    }//GEN-END:initComponents

    private void buttonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOkActionPerformed
        ok();
    }//GEN-LAST:event_buttonOkActionPerformed

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        cancel();
    }//GEN-LAST:event_buttonCancelActionPerformed

    private void textFieldInputKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldInputKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            ok();
        }
    }//GEN-LAST:event_textFieldInputKeyPressed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonOk;
    private javax.swing.JLabel labelPrompt;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelContent;
    private javax.swing.JTextField textFieldInput;
    // End of variables declaration//GEN-END:variables
}
