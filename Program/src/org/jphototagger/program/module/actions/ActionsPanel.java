package org.jphototagger.program.module.actions;

import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import org.jphototagger.domain.event.listener.ListenerSupport;
import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.programs.ProgramExecutor;
import org.jphototagger.domain.programs.ProgramType;
import org.jphototagger.domain.repository.ActionsAfterRepoUpdatesRepository;
import org.jphototagger.domain.repository.ProgramsRepository;
import org.jphototagger.lib.swing.KeyEventUtil;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.MouseEventUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.module.programs.ProgramExecutorImpl;
import org.jphototagger.program.module.programs.ProgramPropertiesDialog;
import org.jphototagger.program.module.programs.ProgramsListModel;
import org.jphototagger.program.module.programs.ProgramsUtil;
import org.jphototagger.program.module.programs.ProgramsUtil.ReorderListener;
import org.jphototagger.resources.Icons;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class ActionsPanel extends javax.swing.JPanel {
    private static final long serialVersionUID = 1L;
    private final ProgramsListModel model = new ProgramsListModel(ProgramType.ACTION);
    private final ListenerSupport<ProgramExecutor> ls = new ListenerSupport<>();
    private final ReorderListener reorderListener = new ProgramsUtil.ReorderListener(model);
    private final ActionsAfterRepoUpdatesRepository actionsAfterRepoUpdatesRepo = Lookup.getDefault().lookup(ActionsAfterRepoUpdatesRepository.class);
    private final ProgramsRepository programsRepo = Lookup.getDefault().lookup(ProgramsRepository.class);

    public ActionsPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        MnemonicUtil.setMnemonics((Container) this);
        setAccelerators();
        selectFirstItem();
        addListener(new ProgramExecutorImpl(true));

        menuItemExecute.setIcon(Icons.getIcon("icon_action.png")); // NOI18N
        menuItemCreate.setIcon(Icons.getIcon("icon_new.png")); // NOI18N
        menuItemEdit.setIcon(Icons.getIcon("icon_edit.png")); // NOI18N
        menuItemDelete.setIcon(Icons.getIcon("icon_delete.png")); // NOI18N
        menuItemMoveActionUp.setIcon(Icons.getIcon("icon_arrow_up.png")); // NOI18N
        menuItemMoveActionDown.setIcon(Icons.getIcon("icon_arrow_down.png")); // NOI18N
    }

    private void selectFirstItem() {
        if (list.getModel().getSize() > 0) {
            list.setSelectedIndex(0);
        }
    }

    private void setAccelerators() {
        menuItemCreate.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_N));
        menuItemEdit.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_E));
        menuItemMoveActionDown.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_DOWN));
        menuItemMoveActionUp.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_UP));
    }

    public void setEnabled() {
        int selectedIndex = list.getSelectedIndex();
        int size = list.getModel().getSize();
        boolean isActionSelected = selectedIndex >= 0;
        boolean canMoveDown = isActionSelected && selectedIndex < size - 1;
        boolean canMoveUp = isActionSelected && selectedIndex > 0;

        buttonDelete.setEnabled(isActionSelected);
        menuItemDelete.setEnabled(isActionSelected);
        buttonEdit.setEnabled(isActionSelected);
        menuItemEdit.setEnabled(isActionSelected);
        buttonExecute.setEnabled(isActionSelected);
        menuItemExecute.setEnabled(isActionSelected);
        menuItemMoveActionDown.setEnabled(canMoveDown);
        menuItemMoveActionUp.setEnabled(canMoveUp);
    }

    private Program getSelectedAction() {
        return (Program) list.getSelectedValue();
    }

    private void executeAction() {
        if (list.getSelectedIndex() >= 0) {
            notifyProgramExecuted(getSelectedAction());
        }
    }

    private void createAction() {
        ProgramPropertiesDialog dlg = new ProgramPropertiesDialog(true);

        setActionsAlwaysDialogOnTop(false);
        dlg.toFront();
        dlg.setVisible(true);
        setActionsAlwaysDialogOnTop(true);

        if (dlg.isAccepted()) {
            Program program = dlg.getProgram();

            if (programsRepo.saveProgram(program)) {
                selectLastListItem();
            }
        }

        setEnabled();
        list.requestFocusInWindow();
    }

    private void selectLastListItem() {
        int size = list.getModel().getSize();
        if (size > 0) {
            list.setSelectedIndex(size - 1);
        }
    }

    private void editAction() {
        if (list.getSelectedIndex() >= 0) {
            Program program = getSelectedAction();
            ProgramPropertiesDialog dlg = new ProgramPropertiesDialog(true);

            dlg.setProgram(program);
            setActionsAlwaysDialogOnTop(false);
            dlg.toFront();
            dlg.setVisible(true);
            setActionsAlwaysDialogOnTop(true);

            if (dlg.isAccepted()) {
                programsRepo.updateProgram(program);
            }
        }

        setEnabled();
        list.requestFocusInWindow();
    }

    private void setActionsAlwaysDialogOnTop(boolean onTop) {
        if (ActionsDialog.INSTANCE.isAncestorOf(this)) {
            ActionsDialog.INSTANCE.setAlwaysOnTop(onTop);
            if (onTop) {
                ActionsDialog.INSTANCE.toFront();
                ActionsDialog.INSTANCE.requestFocusInWindow();
            }
        }
    }

    private void handleListKeyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            executeAction();
        } else if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            deleteAction();
        } else if (KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_E)) {
            editAction();
        } else if (KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_N)) {
            createAction();
        } else if (KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_DOWN)) {
            moveActionDown();
        } else if (KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_UP)) {
            moveActionUp();
        }
    }

    private void handleListMouseClicked(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1) {
            if (evt.getClickCount() == 1) {
                setEnabled();
            } else if (MouseEventUtil.isDoubleClick(evt)) {
                executeAction();
            }
        }
    }

    private void deleteAction() {
        if (list.getSelectedIndex() >= 0) {
            Program program = getSelectedAction();

            if (confirmDelete(program)) {
                programsRepo.deleteProgram(program);
            }

            setEnabled();
            list.requestFocusInWindow();
        }
    }

    private boolean confirmDelete(Program program) {
        String  programName = program.getAlias();
        boolean existsInActionsAfterRepoUpdates = actionsAfterRepoUpdatesRepo.existsAction(program);
        String messageExistsInOtherDb = Bundle.getString(ActionsPanel.class, "ActionsPanel.Confirm.Delete.ExistsInOtherDb", programName);
        String messageExists = Bundle.getString(ActionsPanel.class, "ActionsPanel.Confirm.Delete", programName);

        return existsInActionsAfterRepoUpdates
               ? MessageDisplayer.confirmYesNo(this, messageExistsInOtherDb)
               : MessageDisplayer.confirmYesNo(this, messageExists);
    }

    public synchronized void addListener(ProgramExecutor l) {
        if (l == null) {
            throw new NullPointerException("l == null");
        }

        ls.add(l);
    }

    private synchronized void notifyProgramExecuted(Program program) {
        for (ProgramExecutor listener : ls.get()) {
            listener.execute(program);
        }
    }

    private void moveActionDown() {
        reorderListener.setListenToModel(false);
        ProgramsUtil.moveProgramDown(list);
        reorderListener.setListenToModel(true);
    }

    private void moveActionUp() {
        reorderListener.setListenToModel(false);
        ProgramsUtil.moveProgramUp(list);
        reorderListener.setListenToModel(true);
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

        popupMenu = new javax.swing.JPopupMenu();
        menuItemExecute = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        menuItemCreate = new javax.swing.JMenuItem();
        menuItemEdit = new javax.swing.JMenuItem();
        menuItemDelete = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        menuItemMoveActionUp = new javax.swing.JMenuItem();
        menuItemMoveActionDown = new javax.swing.JMenuItem();
        labelActionList = new javax.swing.JLabel();
        scrollPane = new javax.swing.JScrollPane();
        list = new org.jdesktop.swingx.JXList();
        panelButtons = new javax.swing.JPanel();
        buttonDelete = org.jphototagger.resources.UiFactory.button();
        buttonEdit = org.jphototagger.resources.UiFactory.button();
        buttonCreate = org.jphototagger.resources.UiFactory.button();
        buttonExecute = org.jphototagger.resources.UiFactory.button();

        popupMenu.setName("popupMenu"); // NOI18N

        menuItemExecute.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
        menuItemExecute.setText(Bundle.getString(getClass(), "ActionsPanel.menuItemExecute.text")); // NOI18N
        menuItemExecute.setName("menuItemExecute"); // NOI18N
        menuItemExecute.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemExecuteActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemExecute);

        jSeparator1.setName("jSeparator1"); // NOI18N
        popupMenu.add(jSeparator1);

        menuItemCreate.setText(Bundle.getString(getClass(), "ActionsPanel.menuItemCreate.text")); // NOI18N
        menuItemCreate.setName("menuItemCreate"); // NOI18N
        menuItemCreate.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemCreateActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemCreate);

        menuItemEdit.setText(Bundle.getString(getClass(), "ActionsPanel.menuItemEdit.text")); // NOI18N
        menuItemEdit.setName("menuItemEdit"); // NOI18N
        menuItemEdit.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemEditActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemEdit);

        menuItemDelete.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        menuItemDelete.setText(Bundle.getString(getClass(), "ActionsPanel.menuItemDelete.text")); // NOI18N
        menuItemDelete.setName("menuItemDelete"); // NOI18N
        menuItemDelete.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemDeleteActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemDelete);

        jSeparator2.setName("jSeparator2"); // NOI18N
        popupMenu.add(jSeparator2);

        menuItemMoveActionUp.setText(Bundle.getString(getClass(), "ActionsPanel.menuItemMoveActionUp.text")); // NOI18N
        menuItemMoveActionUp.setName("menuItemMoveActionUp"); // NOI18N
        menuItemMoveActionUp.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemMoveActionUpActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemMoveActionUp);

        menuItemMoveActionDown.setText(Bundle.getString(getClass(), "ActionsPanel.menuItemMoveActionDown.text")); // NOI18N
        menuItemMoveActionDown.setName("menuItemMoveActionDown"); // NOI18N
        menuItemMoveActionDown.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemMoveActionDownActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemMoveActionDown);

        setFocusable(false);
        setName("Form"); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        labelActionList.setLabelFor(list);
        labelActionList.setText(Bundle.getString(getClass(), "ActionsPanel.labelActionList.text")); // NOI18N
        labelActionList.setName("labelActionList"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        add(labelActionList, gridBagConstraints);

        scrollPane.setFocusable(false);
        scrollPane.setName("scrollPane"); // NOI18N

        list.setModel(model);
        list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new org.jphototagger.program.module.actions.ActionsListCellRenderer());
        list.setComponentPopupMenu(popupMenu);
        list.setDragEnabled(true);
        list.setDropMode(javax.swing.DropMode.INSERT);
        list.setName("list"); // NOI18N
        list.setTransferHandler(new org.jphototagger.program.datatransfer.ReorderListItemsTransferHandler(list));
        list.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listMouseClicked(evt);
            }
        });
        list.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            @Override
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listValueChanged(evt);
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
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 0, 5);
        add(scrollPane, gridBagConstraints);

        panelButtons.setName("panelButtons"); // NOI18N
        panelButtons.setLayout(new java.awt.GridBagLayout());

        buttonDelete.setText(Bundle.getString(getClass(), "ActionsPanel.buttonDelete.text")); // NOI18N
        buttonDelete.setToolTipText(Bundle.getString(getClass(), "ActionsPanel.buttonDelete.toolTipText")); // NOI18N
        buttonDelete.setEnabled(false);
        buttonDelete.setName("buttonDelete"); // NOI18N
        buttonDelete.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteActionPerformed(evt);
            }
        });
        panelButtons.add(buttonDelete, new java.awt.GridBagConstraints());

        buttonEdit.setText(Bundle.getString(getClass(), "ActionsPanel.buttonEdit.text")); // NOI18N
        buttonEdit.setToolTipText(Bundle.getString(getClass(), "ActionsPanel.buttonEdit.toolTipText")); // NOI18N
        buttonEdit.setEnabled(false);
        buttonEdit.setName("buttonEdit"); // NOI18N
        buttonEdit.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEditActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelButtons.add(buttonEdit, gridBagConstraints);

        buttonCreate.setText(Bundle.getString(getClass(), "ActionsPanel.buttonCreate.text")); // NOI18N
        buttonCreate.setToolTipText(Bundle.getString(getClass(), "ActionsPanel.buttonCreate.toolTipText")); // NOI18N
        buttonCreate.setName("buttonCreate"); // NOI18N
        buttonCreate.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCreateActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelButtons.add(buttonCreate, gridBagConstraints);

        buttonExecute.setText(Bundle.getString(getClass(), "ActionsPanel.buttonExecute.text")); // NOI18N
        buttonExecute.setToolTipText(Bundle.getString(getClass(), "ActionsPanel.buttonExecute.toolTipText")); // NOI18N
        buttonExecute.setEnabled(false);
        buttonExecute.setName("buttonExecute"); // NOI18N
        buttonExecute.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonExecuteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelButtons.add(buttonExecute, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 3, 5);
        add(panelButtons, gridBagConstraints);
    }//GEN-END:initComponents

    private void listValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listValueChanged
        if (!evt.getValueIsAdjusting()) {
            setEnabled();
        }
    }//GEN-LAST:event_listValueChanged

    private void listMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listMouseClicked
        handleListMouseClicked(evt);
    }//GEN-LAST:event_listMouseClicked

    private void buttonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteActionPerformed
        deleteAction();
    }//GEN-LAST:event_buttonDeleteActionPerformed

    private void buttonEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonEditActionPerformed
        editAction();
    }//GEN-LAST:event_buttonEditActionPerformed

    private void buttonCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCreateActionPerformed
        createAction();
    }//GEN-LAST:event_buttonCreateActionPerformed

    private void buttonExecuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonExecuteActionPerformed
        executeAction();
    }//GEN-LAST:event_buttonExecuteActionPerformed

    private void menuItemExecuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemExecuteActionPerformed
        executeAction();
    }//GEN-LAST:event_menuItemExecuteActionPerformed

    private void menuItemEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemEditActionPerformed
        editAction();
    }//GEN-LAST:event_menuItemEditActionPerformed

    private void menuItemCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemCreateActionPerformed
        createAction();
    }//GEN-LAST:event_menuItemCreateActionPerformed

    private void menuItemDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemDeleteActionPerformed
        deleteAction();
    }//GEN-LAST:event_menuItemDeleteActionPerformed

    private void listKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listKeyPressed
        handleListKeyPressed(evt);
    }//GEN-LAST:event_listKeyPressed

    private void menuItemMoveActionUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemMoveActionUpActionPerformed
        moveActionUp();
}//GEN-LAST:event_menuItemMoveActionUpActionPerformed

    private void menuItemMoveActionDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemMoveActionDownActionPerformed
        moveActionDown();
}//GEN-LAST:event_menuItemMoveActionDownActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCreate;
    private javax.swing.JButton buttonDelete;
    private javax.swing.JButton buttonEdit;
    private javax.swing.JButton buttonExecute;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JLabel labelActionList;
    private org.jdesktop.swingx.JXList list;
    private javax.swing.JMenuItem menuItemCreate;
    private javax.swing.JMenuItem menuItemDelete;
    private javax.swing.JMenuItem menuItemEdit;
    private javax.swing.JMenuItem menuItemExecute;
    private javax.swing.JMenuItem menuItemMoveActionDown;
    private javax.swing.JMenuItem menuItemMoveActionUp;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPopupMenu popupMenu;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables
}
