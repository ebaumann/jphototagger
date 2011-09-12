package org.jphototagger.program.view.panels;

import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jphototagger.domain.filefilter.UserDefinedFileFilter;
import org.jphototagger.domain.repository.UserDefinedFileFiltersRepository;
import org.jphototagger.lib.componentutil.MnemonicUtil;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.event.util.MouseEventUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.model.ListModelUserDefinedFileFilter;
import org.jphototagger.program.view.dialogs.EditUserDefinedFileFilterDialog;
import org.openide.util.Lookup;

/**
 *
 *
 * @author Elmar Baumann
 */
public class UserDefinedFileFilterPanel extends javax.swing.JPanel implements ListSelectionListener {

    private static final long serialVersionUID = 4313288636752584356L;
    private final UserDefinedFileFiltersRepository repo = Lookup.getDefault().lookup(UserDefinedFileFiltersRepository.class);

    public UserDefinedFileFilterPanel() {
        initComponents();
        MnemonicUtil.setMnemonics((Container) this);
        list.addListSelectionListener(this);
    }

    private void insertFilter() {
        EditUserDefinedFileFilterDialog dlg = new EditUserDefinedFileFilterDialog();

        dlg.setVisible(true);

        if (dlg.isAccepted()) {
            UserDefinedFileFilter newFilter = dlg.getFilter();
            if (!repo.saveUserDefinedFileFilter(newFilter)) {
                errorMessageInsert(newFilter);
            }
        }
    }

    private void errorMessageInsert(UserDefinedFileFilter filter) {
        String message = Bundle.getString(UserDefinedFileFilterPanel.class, "UserDefinedFileFilterPanel.Error.Insert", filter);

        MessageDisplayer.error(this, message);
    }

    private void updateFilter() {
        for (UserDefinedFileFilter filter : getSelectedFilters()) {
            EditUserDefinedFileFilterDialog dlg = new EditUserDefinedFileFilterDialog(filter);

            dlg.setVisible(true);

            if (dlg.isAccepted()) {
                UserDefinedFileFilter updatedFilter = dlg.getFilter();
                if (!repo.updateUserDefinedFileFilter(updatedFilter)) {
                    errorMessageUpdate(updatedFilter);
                }
            }
        }
    }

    private void errorMessageUpdate(UserDefinedFileFilter filter) {
        String message = Bundle.getString(UserDefinedFileFilterPanel.class, "UserDefinedFileFilterPanel.Error.Update", filter);

        MessageDisplayer.error(this, message);
    }

    private void deleteFilter() {
        if (confirmDelete()) {
            for (UserDefinedFileFilter filter : getSelectedFilters()) {
                if (!repo.deleteUserDefinedFileFilter(filter)) {
                    errorMessageDelete(filter);
                }
            }
        }
    }

    private void errorMessageDelete(UserDefinedFileFilter filter) {
        String message = Bundle.getString(UserDefinedFileFilterPanel.class, "UserDefinedFileFilterPanel.Error.Delete", filter);

        MessageDisplayer.error(this, message);
    }

    private boolean confirmDelete() {
        String message = Bundle.getString(UserDefinedFileFilterPanel.class, "UserDefinedFileFilterPanel.Confirm.Delete");

        return MessageDisplayer.confirmYesNo(this, message);
    }

    private List<UserDefinedFileFilter> getSelectedFilters() {
        Object[] selValues = list.getSelectedValues();
        List<UserDefinedFileFilter> filter = new ArrayList<UserDefinedFileFilter>(selValues.length);

        for (Object selValue : selValues) {
            if (selValue instanceof UserDefinedFileFilter) {
                filter.add((UserDefinedFileFilter) selValue);
            }
        }

        return filter;
    }

    @Override
    public void valueChanged(ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting()) {
            setEnabledButtons();
        }
    }

    private void handleMouseClickedInList(MouseEvent evt) {
        if (MouseEventUtil.isDoubleClick(evt)) {
            updateFilter();
        }
    }

    private boolean isItemSelected() {
        return list.getSelectedIndex() >= 0;
    }

    private void setEnabledButtons() {
        boolean selected = isItemSelected();
        buttonDelete.setEnabled(selected);
        buttonUpdate.setEnabled(selected);
    }

    private void setEnabledPopupMenuItems() {
        boolean selected = isItemSelected();
        menuItemDelete.setEnabled(selected);
        menuItemUpdate.setEnabled(selected);
    }

    private void handleListKeyPressed(KeyEvent evt) {
        int keyCode = evt.getKeyCode();

        if (keyCode == KeyEvent.VK_INSERT) {
            insertFilter();
        } else if (keyCode == KeyEvent.VK_ENTER) {
            updateFilter();
        } else if (keyCode == KeyEvent.VK_DELETE) {
            deleteFilter();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents

        popupMenu = new javax.swing.JPopupMenu();
        menuItemInsert = new javax.swing.JMenuItem();
        menuItemUpdate = new javax.swing.JMenuItem();
        menuItemDelete = new javax.swing.JMenuItem();
        scrollPane = new javax.swing.JScrollPane();
        list = new org.jdesktop.swingx.JXList();
        buttonInsert = new javax.swing.JButton();
        buttonUpdate = new javax.swing.JButton();
        buttonDelete = new javax.swing.JButton();

        popupMenu.setName("popupMenu"); // NOI18N
        popupMenu.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                popupMenuPopupMenuWillBecomeVisible(evt);
            }
        });

        menuItemInsert.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_INSERT, 0));
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/view/panels/Bundle"); // NOI18N
        menuItemInsert.setText(bundle.getString("UserDefinedFileFilterPanel.menuItemInsert.text")); // NOI18N
        menuItemInsert.setName("menuItemInsert"); // NOI18N
        menuItemInsert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemInsertActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemInsert);

        menuItemUpdate.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
        menuItemUpdate.setText(bundle.getString("UserDefinedFileFilterPanel.menuItemUpdate.text")); // NOI18N
        menuItemUpdate.setName("menuItemUpdate"); // NOI18N
        menuItemUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemUpdateActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemUpdate);

        menuItemDelete.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        menuItemDelete.setText(bundle.getString("UserDefinedFileFilterPanel.menuItemDelete.text")); // NOI18N
        menuItemDelete.setName("menuItemDelete"); // NOI18N
        menuItemDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemDeleteActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemDelete);

        setName("Form"); // NOI18N

        scrollPane.setName("scrollPane"); // NOI18N

        list.setModel(new ListModelUserDefinedFileFilter());
        list.setComponentPopupMenu(popupMenu);
        list.setName("list"); // NOI18N
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

        buttonInsert.setText(bundle.getString("UserDefinedFileFilterPanel.buttonInsert.text")); // NOI18N
        buttonInsert.setName("buttonInsert"); // NOI18N
        buttonInsert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonInsertActionPerformed(evt);
            }
        });

        buttonUpdate.setText(bundle.getString("UserDefinedFileFilterPanel.buttonUpdate.text")); // NOI18N
        buttonUpdate.setEnabled(false);
        buttonUpdate.setName("buttonUpdate"); // NOI18N
        buttonUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonUpdateActionPerformed(evt);
            }
        });

        buttonDelete.setText(bundle.getString("UserDefinedFileFilterPanel.buttonDelete.text")); // NOI18N
        buttonDelete.setEnabled(false);
        buttonDelete.setName("buttonDelete"); // NOI18N
        buttonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buttonInsert)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonUpdate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonDelete))
            .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonInsert)
                    .addComponent(buttonUpdate)
                    .addComponent(buttonDelete)))
        );
    }//GEN-END:initComponents

    private void buttonInsertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonInsertActionPerformed
        insertFilter();
    }//GEN-LAST:event_buttonInsertActionPerformed

    private void buttonUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonUpdateActionPerformed
        updateFilter();
    }//GEN-LAST:event_buttonUpdateActionPerformed

    private void buttonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteActionPerformed
        deleteFilter();
    }//GEN-LAST:event_buttonDeleteActionPerformed

    private void listMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listMouseClicked
        handleMouseClickedInList(evt);
    }//GEN-LAST:event_listMouseClicked

    private void menuItemInsertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemInsertActionPerformed
        insertFilter();
    }//GEN-LAST:event_menuItemInsertActionPerformed

    private void menuItemUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemUpdateActionPerformed
        updateFilter();
    }//GEN-LAST:event_menuItemUpdateActionPerformed

    private void menuItemDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemDeleteActionPerformed
        deleteFilter();
    }//GEN-LAST:event_menuItemDeleteActionPerformed

    private void popupMenuPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_popupMenuPopupMenuWillBecomeVisible
        setEnabledPopupMenuItems();
    }//GEN-LAST:event_popupMenuPopupMenuWillBecomeVisible

    private void listKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listKeyPressed
        handleListKeyPressed(evt);
    }//GEN-LAST:event_listKeyPressed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonDelete;
    private javax.swing.JButton buttonInsert;
    private javax.swing.JButton buttonUpdate;
    private org.jdesktop.swingx.JXList list;
    private javax.swing.JMenuItem menuItemDelete;
    private javax.swing.JMenuItem menuItemInsert;
    private javax.swing.JMenuItem menuItemUpdate;
    private javax.swing.JPopupMenu popupMenu;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables
}
