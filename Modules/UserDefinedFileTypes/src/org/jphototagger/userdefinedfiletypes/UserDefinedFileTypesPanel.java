package org.jphototagger.userdefinedfiletypes;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jphototagger.domain.filetypes.UserDefinedFileType;
import org.jphototagger.domain.repository.UserDefinedFileTypesRepository;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.MouseEventUtil;
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public class UserDefinedFileTypesPanel extends PanelExt {

    private static final long serialVersionUID = 1L;

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
        List<UserDefinedFileType> fileTypes = new ArrayList<>();

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

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        scrollPane = UiFactory.scrollPane();
        list = UiFactory.jxList();
        panelButtons = UiFactory.panel();
        buttonAdd = UiFactory.button();
        buttonEdit = UiFactory.button();
        buttonDelete = UiFactory.button();

        setLayout(new java.awt.GridBagLayout());

        scrollPane.setPreferredSize(new Dimension(300, 200));

        list.setModel(new org.jphototagger.userdefinedfiletypes.UserDefinedFileTypesListModel());
        list.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listMouseClicked(evt);
            }
        });
        list.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
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

        panelButtons.setLayout(new java.awt.GridLayout(1, 0, UiFactory.scale(3), 0));

        buttonAdd.setText(Bundle.getString(getClass(), "UserDefinedFileTypesPanel.buttonAdd.text")); // NOI18N
        buttonAdd.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddActionPerformed(evt);
            }
        });
        panelButtons.add(buttonAdd);

        buttonEdit.setText(Bundle.getString(getClass(), "UserDefinedFileTypesPanel.buttonEdit.text")); // NOI18N
        buttonEdit.setEnabled(false);
        buttonEdit.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEditActionPerformed(evt);
            }
        });
        panelButtons.add(buttonEdit);

        buttonDelete.setText(Bundle.getString(getClass(), "UserDefinedFileTypesPanel.buttonDelete.text")); // NOI18N
        buttonDelete.setEnabled(false);
        buttonDelete.addActionListener(new java.awt.event.ActionListener() {
            @Override
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
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        add(panelButtons, gridBagConstraints);
    }

    private void buttonAddActionPerformed(java.awt.event.ActionEvent evt) {
        addUserDefinedFileType();
    }

    private void buttonEditActionPerformed(java.awt.event.ActionEvent evt) {
        editUserDefinedFileType();
    }

    private void buttonDeleteActionPerformed(java.awt.event.ActionEvent evt) {
        deleteUserDefinedFileType();
    }

    private void listKeyPressed(java.awt.event.KeyEvent evt) {
        checkDelete(evt);
    }

    private void listMouseClicked(java.awt.event.MouseEvent evt) {
        editClickedUserDefinedFileType(evt);
    }

    private javax.swing.JButton buttonAdd;
    private javax.swing.JButton buttonDelete;
    private javax.swing.JButton buttonEdit;
    private org.jdesktop.swingx.JXList list;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JScrollPane scrollPane;
}
