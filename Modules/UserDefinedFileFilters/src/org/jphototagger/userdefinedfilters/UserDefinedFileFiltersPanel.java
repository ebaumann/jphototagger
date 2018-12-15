package org.jphototagger.userdefinedfilters;

import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jphototagger.domain.filefilter.UserDefinedFileFilter;
import org.jphototagger.domain.repository.UserDefinedFileFiltersRepository;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.MouseEventUtil;
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public class UserDefinedFileFiltersPanel extends PanelExt implements ListSelectionListener {

    private static final long serialVersionUID = 1L;
    private final UserDefinedFileFiltersRepository repo = Lookup.getDefault().lookup(UserDefinedFileFiltersRepository.class);

    public UserDefinedFileFiltersPanel() {
        initComponents();
        MnemonicUtil.setMnemonics((Container) this);
        listen();
    }

    private void listen() {
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
        String message = Bundle.getString(UserDefinedFileFiltersPanel.class, "UserDefinedFileFilterPanel.Error.Insert", filter);

        MessageDisplayer.error(this, message);
    }

    private void updateFilter() {
        for (UserDefinedFileFilter filter : getSelectedFilters()) {
            EditUserDefinedFileFilterDialog dlg = new EditUserDefinedFileFilterDialog(filter);

            dlg.setUpdate(true);
            dlg.setVisible(true);

            if (dlg.isAccepted()) {
                UserDefinedFileFilter updatedFilter = dlg.getFilter();
                boolean equalNames = filter.getName().equals(updatedFilter.getName());
                boolean exists = repo.existsUserDefinedFileFilter(filter.getName());
                boolean updated;
                if (!equalNames && exists) {
                    repo.deleteUserDefinedFileFilter(filter);
                    updated = repo.saveUserDefinedFileFilter(updatedFilter);
                } else {
                    updated = repo.updateUserDefinedFileFilter(updatedFilter);
                }
                if (!updated) {
                    errorMessageUpdate(updatedFilter);
                }
            }
        }
    }

    private void errorMessageUpdate(UserDefinedFileFilter filter) {
        String message = Bundle.getString(UserDefinedFileFiltersPanel.class, "UserDefinedFileFilterPanel.Error.Update", filter);

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
        String message = Bundle.getString(UserDefinedFileFiltersPanel.class, "UserDefinedFileFilterPanel.Error.Delete", filter);

        MessageDisplayer.error(this, message);
    }

    private boolean confirmDelete() {
        String message = Bundle.getString(UserDefinedFileFiltersPanel.class, "UserDefinedFileFilterPanel.Confirm.Delete");

        return MessageDisplayer.confirmYesNo(this, message);
    }

    private List<UserDefinedFileFilter> getSelectedFilters() {
        Object[] selValues = list.getSelectedValues();
        List<UserDefinedFileFilter> filter = new ArrayList<>(selValues.length);
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
        java.awt.GridBagConstraints gridBagConstraints;

        popupMenu = org.jphototagger.resources.UiFactory.popupMenu();
        menuItemInsert = org.jphototagger.resources.UiFactory.menuItem();
        menuItemUpdate = org.jphototagger.resources.UiFactory.menuItem();
        menuItemDelete = org.jphototagger.resources.UiFactory.menuItem();
        scrollPane = org.jphototagger.resources.UiFactory.scrollPane();
        list = org.jphototagger.resources.UiFactory.jxList();
        panelButtons = org.jphototagger.resources.UiFactory.panel();
        buttonInsert = org.jphototagger.resources.UiFactory.button();
        buttonUpdate = org.jphototagger.resources.UiFactory.button();
        buttonDelete = org.jphototagger.resources.UiFactory.button();

        popupMenu.setName("popupMenu"); // NOI18N
        popupMenu.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                popupMenuPopupMenuWillBecomeVisible(evt);
            }
            @Override
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }
            @Override
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
        });

        menuItemInsert.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_INSERT, 0));
        menuItemInsert.setText(Bundle.getString(getClass(), "UserDefinedFileFiltersPanel.menuItemInsert.text")); // NOI18N
        menuItemInsert.setName("menuItemInsert"); // NOI18N
        menuItemInsert.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemInsertActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemInsert);

        menuItemUpdate.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
        menuItemUpdate.setText(Bundle.getString(getClass(), "UserDefinedFileFiltersPanel.menuItemUpdate.text")); // NOI18N
        menuItemUpdate.setName("menuItemUpdate"); // NOI18N
        menuItemUpdate.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemUpdateActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemUpdate);

        menuItemDelete.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        menuItemDelete.setText(Bundle.getString(getClass(), "UserDefinedFileFiltersPanel.menuItemDelete.text")); // NOI18N
        menuItemDelete.setName("menuItemDelete"); // NOI18N
        menuItemDelete.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemDeleteActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemDelete);

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        scrollPane.setName("scrollPane"); // NOI18N

        list.setModel(new org.jphototagger.userdefinedfilters.UserDefinedFileFiltersListModel());
        list.setCellRenderer(new UserDefinedFileFiltersListCellRenderer());
        list.setComponentPopupMenu(popupMenu);
        list.setName("list"); // NOI18N
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
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(scrollPane, gridBagConstraints);

        panelButtons.setName("panelButtons"); // NOI18N
        panelButtons.setLayout(new java.awt.GridBagLayout());

        buttonInsert.setText(Bundle.getString(getClass(), "UserDefinedFileFiltersPanel.buttonInsert.text")); // NOI18N
        buttonInsert.setName("buttonInsert"); // NOI18N
        buttonInsert.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonInsertActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        panelButtons.add(buttonInsert, gridBagConstraints);

        buttonUpdate.setText(Bundle.getString(getClass(), "UserDefinedFileFiltersPanel.buttonUpdate.text")); // NOI18N
        buttonUpdate.setEnabled(false);
        buttonUpdate.setName("buttonUpdate"); // NOI18N
        buttonUpdate.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonUpdateActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelButtons.add(buttonUpdate, gridBagConstraints);

        buttonDelete.setText(Bundle.getString(getClass(), "UserDefinedFileFiltersPanel.buttonDelete.text")); // NOI18N
        buttonDelete.setEnabled(false);
        buttonDelete.setName("buttonDelete"); // NOI18N
        buttonDelete.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelButtons.add(buttonDelete, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        add(panelButtons, gridBagConstraints);
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
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPopupMenu popupMenu;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables
}
