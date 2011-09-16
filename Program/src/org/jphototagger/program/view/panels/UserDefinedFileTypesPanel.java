package org.jphototagger.program.view.panels;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openide.util.Lookup;

import org.jphototagger.domain.filetypes.UserDefinedFileType;
import org.jphototagger.domain.repository.UserDefinedFileTypesRepository;
import org.jphototagger.lib.componentutil.MnemonicUtil;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.event.util.MouseEventUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.model.UserDefinedFileTypesListModel;
import org.jphototagger.program.view.dialogs.EditUserDefinedFileTypeDialog;

/**
 *
 *
 * @author Elmar Baumann
 */
public class UserDefinedFileTypesPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = -7747694022574930356L;

    public UserDefinedFileTypesPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        MnemonicUtil.setMnemonics(this);
        list.addListSelectionListener(new ButtonEnabler());
    }

    private void addUserDefinedFileType() {
        EditUserDefinedFileTypeDialog dlg = new EditUserDefinedFileTypeDialog();

        dlg.setVisible(true);
    }

    private void editUserDefinedFileType() {
        List<UserDefinedFileType> fileTypes = getSelectedUserDefinedFileTypes();

        for (UserDefinedFileType fileType : fileTypes) {
            EditUserDefinedFileTypeDialog dlg = new EditUserDefinedFileTypeDialog();

            dlg.setUserDefinedFileType(fileType);
            dlg.setVisible(true);
        }
    }

    private void deleteUserDefinedFileType() {
        String message = Bundle.getString(UserDefinedFileTypesPanel.class, "UserDefinedFileTypesPanel.Confirm.Delete");

        if (MessageDisplayer.confirmYesNo(this, message)) {
            List<UserDefinedFileType> fileTypes = getSelectedUserDefinedFileTypes();
            UserDefinedFileTypesRepository repo = Lookup.getDefault().lookup(UserDefinedFileTypesRepository.class);

            for (UserDefinedFileType fileType : fileTypes) {
                repo.deleteUserDefinedFileType(fileType);
            }
        }
    }

    private void editClickedUserDefinedFileType(MouseEvent evt) {
        if (MouseEventUtil.isDoubleClick(evt)) {
            editUserDefinedFileType();
        }
    }

    private List<UserDefinedFileType> getSelectedUserDefinedFileTypes() {
        List<UserDefinedFileType> fileTypes = new ArrayList<UserDefinedFileType>();

        for (Object selectedValue : list.getSelectedValues()) {
            if (selectedValue instanceof UserDefinedFileType) {
                fileTypes.add((UserDefinedFileType) selectedValue);
            }
        }

        return fileTypes;
    }

    private boolean isListItemSelected() {
        int selectedIndex = list.getSelectedIndex();

        return selectedIndex >= 0;
    }

    private void checkDelete(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_DELETE && isListItemSelected()) {
            deleteUserDefinedFileType();
        }
    }

    private class ButtonEnabler implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                boolean itemSelected = isListItemSelected();

                buttonDelete.setEnabled(itemSelected);
                buttonEdit.setEnabled(itemSelected);
            }
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        scrollPane = new javax.swing.JScrollPane();
        list = new org.jdesktop.swingx.JXList();
        panelButtons = new javax.swing.JPanel();
        buttonAdd = new javax.swing.JButton();
        buttonEdit = new javax.swing.JButton();
        buttonDelete = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        list.setModel(new UserDefinedFileTypesListModel());
        list.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listMouseClicked(evt);
            }
        });
        list.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listKeyPressed(evt);
            }
        });
        scrollPane.setViewportView(list);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(scrollPane, gridBagConstraints);

        panelButtons.setLayout(new java.awt.GridLayout(1, 0, 3, 0));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/view/panels/Bundle"); // NOI18N
        buttonAdd.setText(bundle.getString("UserDefinedFileTypesPanel.buttonAdd.text")); // NOI18N
        buttonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddActionPerformed(evt);
            }
        });
        panelButtons.add(buttonAdd);

        buttonEdit.setText(bundle.getString("UserDefinedFileTypesPanel.buttonEdit.text")); // NOI18N
        buttonEdit.setEnabled(false);
        buttonEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEditActionPerformed(evt);
            }
        });
        panelButtons.add(buttonEdit);

        buttonDelete.setText(bundle.getString("UserDefinedFileTypesPanel.buttonDelete.text")); // NOI18N
        buttonDelete.setEnabled(false);
        buttonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteActionPerformed(evt);
            }
        });
        panelButtons.add(buttonDelete);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(panelButtons, gridBagConstraints);
    }//GEN-END:initComponents

    private void buttonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddActionPerformed
        addUserDefinedFileType();
    }//GEN-LAST:event_buttonAddActionPerformed

    private void buttonEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonEditActionPerformed
        editUserDefinedFileType();
    }//GEN-LAST:event_buttonEditActionPerformed

    private void buttonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteActionPerformed
        deleteUserDefinedFileType();
    }//GEN-LAST:event_buttonDeleteActionPerformed

    private void listKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listKeyPressed
        checkDelete(evt);
    }//GEN-LAST:event_listKeyPressed

    private void listMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listMouseClicked
        editClickedUserDefinedFileType(evt);
    }//GEN-LAST:event_listMouseClicked
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAdd;
    private javax.swing.JButton buttonDelete;
    private javax.swing.JButton buttonEdit;
    private org.jdesktop.swingx.JXList list;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables
}
