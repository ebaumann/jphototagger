package org.jphototagger.lib.swing;

import java.awt.event.KeyEvent;
import javax.swing.JDialog;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;

/**
 * Modal text input dialog writing it's location to a properties object on demand.
 *
 * @author Elmar Baumann
 */
public final class InputDialog extends DialogExt {

    private static final long  serialVersionUID = 1L;
    private boolean accepted;

    public InputDialog() {
        super(ComponentUtil.findFrameWithIcon(), true);
        init("", "");
    }

    public InputDialog(JDialog owner) {
        super(owner);
        init("", "");
    }

    public InputDialog(String info, String input) {
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

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panelContent = UiFactory.panel();
        labelPrompt = UiFactory.label();
        textFieldInput = UiFactory.textField();
        panelButtons = UiFactory.panel();
        buttonCancel = UiFactory.button();
        buttonOk = UiFactory.button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(Bundle.getString(getClass(), "InputDialog.title")); // NOI18N
        
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panelContent.setName("panelContent"); // NOI18N
        panelContent.setLayout(new java.awt.GridBagLayout());

        labelPrompt.setText(Bundle.getString(getClass(), "InputDialog.labelPrompt.text")); // NOI18N
        labelPrompt.setName("labelPrompt"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panelContent.add(labelPrompt, gridBagConstraints);

        textFieldInput.setColumns(20);
        textFieldInput.setName("textFieldInput"); // NOI18N
        textFieldInput.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                textFieldInputKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(3, 0, 0, 0);
        panelContent.add(textFieldInput, gridBagConstraints);

        panelButtons.setName("panelButtons"); // NOI18N
        panelButtons.setLayout(new java.awt.GridBagLayout());

        buttonCancel.setMnemonic('a');
        buttonCancel.setText(Bundle.getString(getClass(), "InputDialog.buttonCancel.text")); // NOI18N
        buttonCancel.setName("buttonCancel"); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });
        panelButtons.add(buttonCancel, new java.awt.GridBagConstraints());

        buttonOk.setMnemonic('o');
        buttonOk.setText(Bundle.getString(getClass(), "InputDialog.buttonOk.text")); // NOI18N
        buttonOk.setName("buttonOk"); // NOI18N
        buttonOk.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOkActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelButtons.add(buttonOk, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(7, 0, 0, 0);
        panelContent.add(panelButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(7, 7, 7, 7);
        getContentPane().add(panelContent, gridBagConstraints);

        pack();
    }

    private void buttonOkActionPerformed(java.awt.event.ActionEvent evt) {
        ok();
    }

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {
        cancel();
    }

    private void textFieldInputKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            ok();
        }
    }
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonOk;
    private javax.swing.JLabel labelPrompt;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelContent;
    private javax.swing.JTextField textFieldInput;
}
