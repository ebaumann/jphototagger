package org.jphototagger.maintainance.browse;

import java.awt.Dimension;
import java.util.Objects;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jphototagger.lib.swing.DialogExt;
import org.jphototagger.lib.swing.DocumentChangeListener;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.resources.UiFactory;

/**
 * @author Elmar Baumann
 */
public class EditSqlCommandDialog extends DialogExt {

    private static final long serialVersionUID = 1L;
    private boolean accepted;

    public EditSqlCommandDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        textFieldDescription.getDocument().addDocumentListener(inputListener);
        textAreaSql.getDocument().addDocumentListener(inputListener);
        MnemonicUtil.setMnemonics(this);
        setButtonsEnabled();
    }

    public void setSqlCommand(SqlCommand command) {
        Objects.requireNonNull(command, "command == null");

        textFieldDescription.setText(command.getDescription());
        textAreaSql.setText(command.getSql());
    }

    public String getDescription() {
        return textFieldDescription.getText().trim();
    }

    public String getSql() {
        return textAreaSql.getText().trim();
    }

    public void saveInputTo(SqlCommand command) {
        Objects.requireNonNull(command, "command == null");

        command.setDescription(getDescription());
        command.setSql(getSql());
    }

    public boolean isAccepted() {
        return accepted;
    }

    private void setButtonsEnabled() {
        buttonOk.setEnabled(!getDescription().isEmpty() && !getSql().isEmpty());
    }

    private final DocumentListener inputListener = new DocumentChangeListener() {

        @Override
        public void documentChanged(DocumentEvent e) {
            setButtonsEnabled();
        }
    };

    private void ok() {
        accepted = true;
        dispose();
    }

    private void cancel() {
        dispose();
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panelContent = UiFactory.panel();
        labelDescription = UiFactory.label();
        textFieldDescription = UiFactory.textField();
        labelSql = UiFactory.label();
        scrollPaneSql = UiFactory.scrollPane();
        textAreaSql = UiFactory.textArea();
        panelButtons = UiFactory.panel();
        buttonOk = UiFactory.button();
        buttonCancel = UiFactory.button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.jphototagger.lib.util.Bundle.getString(EditSqlCommandDialog.class, "EditSqlCommandDialog.title")); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panelContent.setLayout(new java.awt.GridBagLayout());

        labelDescription.setLabelFor(textFieldDescription);
        labelDescription.setText(org.jphototagger.lib.util.Bundle.getString(EditSqlCommandDialog.class, "EditSqlCommandDialog.labelDescription.text")); // NOI18N
        panelContent.add(labelDescription, new java.awt.GridBagConstraints());

        textFieldDescription.setColumns(15);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelContent.add(textFieldDescription, gridBagConstraints);

        labelSql.setLabelFor(textAreaSql);
        labelSql.setText(org.jphototagger.lib.util.Bundle.getString(EditSqlCommandDialog.class, "EditSqlCommandDialog.labelSql.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        panelContent.add(labelSql, gridBagConstraints);

        scrollPaneSql.setPreferredSize(new Dimension(400, 150));

        textAreaSql.setColumns(20);
        textAreaSql.setRows(5);
        scrollPaneSql.setViewportView(textAreaSql);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(3, 0, 0, 0);
        panelContent.add(scrollPaneSql, gridBagConstraints);

        panelButtons.setLayout(new java.awt.GridBagLayout());

        buttonOk.setText(org.jphototagger.lib.util.Bundle.getString(EditSqlCommandDialog.class, "EditSqlCommandDialog.buttonOk.text")); // NOI18N
        buttonOk.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOkActionPerformed(evt);
            }
        });
        panelButtons.add(buttonOk, new java.awt.GridBagConstraints());

        buttonCancel.setText(org.jphototagger.lib.util.Bundle.getString(EditSqlCommandDialog.class, "EditSqlCommandDialog.buttonCancel.text")); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });
        panelButtons.add(buttonCancel, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = UiFactory.insets(7, 0, 0, 0);
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

    private void buttonOkActionPerformed(java.awt.event.ActionEvent evt) {
        ok();
    }

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {
        cancel();
    }

    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonOk;
    private javax.swing.JLabel labelDescription;
    private javax.swing.JLabel labelSql;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelContent;
    private javax.swing.JScrollPane scrollPaneSql;
    private javax.swing.JTextArea textAreaSql;
    private javax.swing.JTextField textFieldDescription;
}
