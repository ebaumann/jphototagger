package org.jphototagger.maintainance.browse;

import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.Objects;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jphototagger.lib.swing.DialogExt;
import org.jphototagger.lib.swing.ListModelExt;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;

/**
 * @author Elmar Baumann
 */
public class SqlCommandsDialog extends DialogExt {

    private static final long serialVersionUID = 1L;
    private final ListModelExt<SqlCommand> model = new ListModelExt<>();
    private boolean accepted;
    private boolean edit;

    public SqlCommandsDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        listCommands.setModel(model);
        listCommands.addListSelectionListener(commandSelListener);
        listCommands.addMouseListener(commandDoubleClickListener);
        listCommands.addKeyListener(commandEnterListener);
        MnemonicUtil.setMnemonics(this);
        setButtonsEnalbed();
    }

    public boolean isAccepted() {
        return accepted;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setSqlCommands(Collection<? extends SqlCommand> commands) {
        Objects.requireNonNull(commands, "commands == null");

        model.setElements(commands);
    }

    public Collection<SqlCommand> getSqlCommands() {
        return model.getElements();
    }

    public SqlCommand getSelectedCommand() {
        return (SqlCommand) listCommands.getSelectedValue();
    }

    private void setButtonsEnalbed() {
        boolean commandSelected = listCommands.getSelectedIndex() >= 0;

        buttonOk.setEnabled(commandSelected);
        buttonEdit.setEnabled(commandSelected);
        buttonRemove.setEnabled(commandSelected);
    }

    private void editSelectedCommands() {
        for (Object selValue : listCommands.getSelectedValues()) {
            SqlCommand command = (SqlCommand) selValue;
            EditSqlCommandDialog dlg = new EditSqlCommandDialog(ComponentUtil.findFrameWithIcon(), true);

            dlg.setSqlCommand(command);
            dlg.pack();
            dlg.setLocationRelativeTo(this);

            dlg.setVisible(true);
            if (dlg.isAccepted()) {
                dlg.saveInputTo(command);
                edit = true;

                int idx = model.indexOf(command);
                if (model.isIndex(idx)) {
                    model.fireChanged(idx, idx);
                }
            }
        }
    }

    private void removeSelectedCommands() {
        if (!MessageDisplayer.confirmYesNo(this, Bundle.getString(SqlCommandsDialog.class, "SqlCommandsDialog.ConfirmRemove"))) {
            return;
        }
        for (Object selValue : listCommands.getSelectedValues()) {
            model.removeElement(selValue);
        }
    }

    private void ok() {
        accepted = true;
        dispose();
    }

    private void cancel() {
        dispose();
    }

    private final ListSelectionListener commandSelListener = new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                setButtonsEnalbed();
            }
        }
    };

    private final MouseListener commandDoubleClickListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() > 1) {
                if (listCommands.getSelectedIndex() >= 0) {
                    ok();
                }
            }
        }
    };

    private final KeyListener commandEnterListener = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER && listCommands.getSelectedIndex() >= 0) {
                ok();
            }
        }
    };

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panelContent = UiFactory.panel();
        scrollPaneCommands = UiFactory.scrollPane();
        listCommands = UiFactory.jxList();
        panelButtons = UiFactory.panel();
        buttonEdit = UiFactory.button();
        buttonRemove = UiFactory.button();
        buttonOk = UiFactory.button();
        buttonCancel = UiFactory.button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.jphototagger.lib.util.Bundle.getString(SqlCommandsDialog.class, "SqlCommandsDialog.title")); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panelContent.setLayout(new java.awt.GridBagLayout());

        scrollPaneCommands.setPreferredSize(new Dimension(200, 250));
        scrollPaneCommands.setViewportView(listCommands);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelContent.add(scrollPaneCommands, gridBagConstraints);

        panelButtons.setLayout(new java.awt.GridBagLayout());

        buttonEdit.setText(org.jphototagger.lib.util.Bundle.getString(SqlCommandsDialog.class, "SqlCommandsDialog.buttonEdit.text")); // NOI18N
        buttonEdit.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEditActionPerformed(evt);
            }
        });
        panelButtons.add(buttonEdit, new java.awt.GridBagConstraints());

        buttonRemove.setText(org.jphototagger.lib.util.Bundle.getString(SqlCommandsDialog.class, "SqlCommandsDialog.buttonRemove.text")); // NOI18N
        buttonRemove.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelButtons.add(buttonRemove, gridBagConstraints);

        buttonOk.setText(org.jphototagger.lib.util.Bundle.getString(SqlCommandsDialog.class, "SqlCommandsDialog.buttonOk.text")); // NOI18N
        buttonOk.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOkActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelButtons.add(buttonOk, gridBagConstraints);

        buttonCancel.setText(org.jphototagger.lib.util.Bundle.getString(SqlCommandsDialog.class, "SqlCommandsDialog.buttonCancel.text")); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelButtons.add(buttonCancel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = UiFactory.insets(10, 0, 0, 0);
        panelContent.add(panelButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(10, 10, 10, 10);
        getContentPane().add(panelContent, gridBagConstraints);

        pack();
    }

    private void buttonEditActionPerformed(java.awt.event.ActionEvent evt) {
        editSelectedCommands();
    }

    private void buttonRemoveActionPerformed(java.awt.event.ActionEvent evt) {
        removeSelectedCommands();
    }

    private void buttonOkActionPerformed(java.awt.event.ActionEvent evt) {
        ok();
    }

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {
        cancel();
    }

    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonEdit;
    private javax.swing.JButton buttonOk;
    private javax.swing.JButton buttonRemove;
    private org.jdesktop.swingx.JXList listCommands;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelContent;
    private javax.swing.JScrollPane scrollPaneCommands;
}
