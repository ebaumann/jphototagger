package org.jphototagger.program.view.panels;

import java.awt.event.KeyEvent;
import javax.swing.event.ListDataEvent;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.data.Program;
import org.jphototagger.program.database.DatabasePrograms.Type;
import org.jphototagger.program.model.ListModelActionsAfterDbInsertion;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.types.Persistence;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.dialogs.ActionsDialog;
import org.jphototagger.program.view.dialogs.ProgramSelectDialog;
import org.jphototagger.program.view.renderer.ListCellRendererActions;
import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.lib.componentutil.MnemonicUtil;
import java.awt.Container;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.program.database.ConnectionPool;
import org.jphototagger.program.database.DatabaseActionsAfterDbInsertion;
import org.jphototagger.program.database.DatabasePrograms;
import org.jphototagger.program.event.listener.DatabaseProgramsListener;

/**
 *
 * @author Elmar Baumann
 */
public class SettingsActionsPanel extends javax.swing.JPanel
        implements DatabaseProgramsListener, ListSelectionListener,
                   Persistence {
    private static final long                      serialVersionUID =
        6440789488453905704L;
    private final ListModelActionsAfterDbInsertion model            =
        new ListModelActionsAfterDbInsertion();
    private volatile boolean listenToModel                          = true;

    public SettingsActionsPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        list.getSelectionModel().addListSelectionListener(this);
        MnemonicUtil.setMnemonics((Container) this);
        addAccelerators();
        setEnabled();
        listenToModel();
        DatabasePrograms.INSTANCE.addListener(this);
    }

    private void addAccelerators() {
        menuItemAddAction.setAccelerator(
                KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_N));
        menuItemMoveDownAction.setAccelerator(
                KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_DOWN));
        menuItemMoveUpAction.setAccelerator(
                KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_UP));
    }

    private void setEnabled() {
        boolean actionSelected = isActionSelected();
        boolean canMoveUp      = canMoveUp();
        boolean canMoveDown    = canMoveDown();

        setEnabledAddAction();
        buttonDeleteAction.setEnabled(actionSelected);
        menuItemDeleteAction.setEnabled(actionSelected);
        buttonMoveUpAction.setEnabled(canMoveUp);
        menuItemMoveUpAction.setEnabled(canMoveUp);
        buttonMoveDownAction.setEnabled(canMoveDown);
        menuItemMoveDownAction.setEnabled(canMoveDown);
    }

    private void setEnabledAddAction() {
        boolean hasActions = false;

        if (ConnectionPool.INSTANCE.isInit()) {
            hasActions = DatabasePrograms.INSTANCE.getCount(true) > 0;
        }
        
        buttonAddAction.setEnabled(hasActions);
        menuItemAddAction.setEnabled(hasActions);
    }

    private void listenToModel() {
        list.getModel().addListDataListener(new ListDataListener() {

            @Override
            public void intervalAdded(ListDataEvent e) {
                reorder();
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
                reorder();
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
                reorder();
            }

            private void reorder() {
                if (listenToModel) {
                    listenToModel = false;
                    reorderPrograms();
                    listenToModel = true;
                }
            }
        });
    }

    private void addAction() {
        ProgramSelectDialog dlg = new ProgramSelectDialog(Type.ACTION);

        dlg.setVisible(true);

        Program action = dlg.getSelectedProgram();

        if (dlg.isAccepted() &&!model.contains(action)) {
            model.insert(action);
            setEnabled();
        }
    }

    private void moveActionDown() {
        if (canMoveDown()) {
            listenToModel = false;
            model.moveDown(list.getSelectedIndex());
            setEnabled();
            listenToModel = true;
        }
    }

    private void moveActionUp() {
        if (canMoveUp()) {
            listenToModel = false;
            model.moveUp(list.getSelectedIndex());
            setEnabled();
            listenToModel = true;
        }
    }

    private void deleteAction() {
        if (isActionSelected()) {
            Program action = (Program) model.get(list.getSelectedIndex());

            if (confirmDelete(action.getAlias())) {
                model.delete(action);
                setEnabled();
            }
        }
    }

    private void executeActionsAlways() {
        UserSettings.INSTANCE.setExecuteActionsAfterImageChangeInDbAlways(
            radioButtonExecuteAlways.isSelected());
    }

    private void executeActionsIfXmpExists() {
        UserSettings.INSTANCE
            .setExecuteActionsAfterImageChangeInDbIfImageHasXmp(
                radioButtonExecuteIfImageHasXmp.isSelected());
    }

    @Override
    public void readProperties() {
        UserSettings settings = UserSettings.INSTANCE;

        radioButtonExecuteAlways.setSelected(
            settings.isExecuteActionsAfterImageChangeInDbAlways());
        radioButtonExecuteIfImageHasXmp
            .setSelected(settings
                .isExecuteActionsAfterImageChangeInDbIfImageHasXmp());
    }

    @Override
    public void writeProperties() {

        // ignore
    }

    private void showActions() {
        ComponentUtil.show(ActionsDialog.INSTANCE);
    }

    @Override
    public void valueChanged(ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting()) {
            setEnabled();
        }
    }

    private boolean confirmDelete(String actionName) {
        return MessageDisplayer.confirmYesNo(
            this,
            "SettingsActionsPanel.Confirm.RemoveActionAfterDatabaseInsertion",
            actionName);
    }

    private boolean canMoveDown() {
        int selIndex = list.getSelectedIndex();

        return (selIndex < model.getSize() - 1) && (selIndex >= 0);
    }

    private boolean canMoveUp() {
        return list.getSelectedIndex() > 0;
    }

    private boolean isActionSelected() {
        return list.getSelectedIndex() >= 0;
    }

    private void handleListKeyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            deleteAction();
        } else if (KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_N)) {
            addAction();
        } else if (KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_DOWN)) {
            moveActionDown();
        } else if (KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_UP)) {
            moveActionUp();
        }
    }
    private void reorderPrograms() {
        int           size     = model.getSize();
        List<Program> programs = new ArrayList<Program>(size);

        for (int i = 0; i < size; i++) {
            programs.add((Program) model.get(i));
        }

        DatabaseActionsAfterDbInsertion.INSTANCE.setOrder(programs, 0);
    }

    @Override
    public void programInserted(Program program) {
        if (program.isAction()) {
            EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
            setEnabledAddAction();
        }
            });
    }
    }

    @Override
    public void programDeleted(Program program) {
        if (program.isAction()) {
            EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
            setEnabledAddAction();
        }
            });
    }
    }

    @Override
    public void programUpdated(Program program) {

        // ignore
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupExecute = new javax.swing.ButtonGroup();
        popupMenu = new javax.swing.JPopupMenu();
        menuItemAddAction = new javax.swing.JMenuItem();
        menuItemDeleteAction = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        menuItemMoveUpAction = new javax.swing.JMenuItem();
        menuItemMoveDownAction = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        menuItemShowActions = new javax.swing.JMenuItem();
        scrollPane = new javax.swing.JScrollPane();
        list = new javax.swing.JList();
        buttonMoveUpAction = new javax.swing.JButton();
        buttonMoveDownAction = new javax.swing.JButton();
        buttonAddAction = new javax.swing.JButton();
        buttonDeleteAction = new javax.swing.JButton();
        buttonShowActions = new javax.swing.JButton();
        radioButtonExecuteAlways = new javax.swing.JRadioButton();
        radioButtonExecuteIfImageHasXmp = new javax.swing.JRadioButton();

        menuItemAddAction.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_new.png"))); // NOI18N
        menuItemAddAction.setText(JptBundle.INSTANCE.getString("SettingsActionsPanel.menuItemAddAction.text")); // NOI18N
        menuItemAddAction.setEnabled(false);
        menuItemAddAction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemAddActionActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemAddAction);

        menuItemDeleteAction.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        menuItemDeleteAction.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_delete.png"))); // NOI18N
        menuItemDeleteAction.setText(JptBundle.INSTANCE.getString("SettingsActionsPanel.menuItemDeleteAction.text")); // NOI18N
        menuItemDeleteAction.setEnabled(false);
        menuItemDeleteAction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemDeleteActionActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemDeleteAction);
        popupMenu.add(jSeparator1);

        menuItemMoveUpAction.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_arrow_up.png"))); // NOI18N
        menuItemMoveUpAction.setText(JptBundle.INSTANCE.getString("SettingsActionsPanel.menuItemMoveUpAction.text")); // NOI18N
        menuItemMoveUpAction.setEnabled(false);
        menuItemMoveUpAction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemMoveUpActionActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemMoveUpAction);

        menuItemMoveDownAction.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_arrow_down.png"))); // NOI18N
        menuItemMoveDownAction.setText(JptBundle.INSTANCE.getString("SettingsActionsPanel.menuItemMoveDownAction.text")); // NOI18N
        menuItemMoveDownAction.setEnabled(false);
        menuItemMoveDownAction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemMoveDownActionActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemMoveDownAction);
        popupMenu.add(jSeparator2);

        menuItemShowActions.setText(JptBundle.INSTANCE.getString("SettingsActionsPanel.menuItemShowActions.text")); // NOI18N
        menuItemShowActions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemShowActionsActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemShowActions);

        setBorder(javax.swing.BorderFactory.createTitledBorder(JptBundle.INSTANCE.getString("SettingsActionsPanel.border.title"))); // NOI18N

        list.setModel(model);
        list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new ListCellRendererActions());
        list.setComponentPopupMenu(popupMenu);
        list.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listKeyPressed(evt);
            }
        });
        scrollPane.setViewportView(list);

        buttonMoveUpAction.setText(JptBundle.INSTANCE.getString("SettingsActionsPanel.buttonMoveUpAction.text")); // NOI18N
        buttonMoveUpAction.setEnabled(false);
        buttonMoveUpAction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonMoveUpActionActionPerformed(evt);
            }
        });

        buttonMoveDownAction.setText(JptBundle.INSTANCE.getString("SettingsActionsPanel.buttonMoveDownAction.text")); // NOI18N
        buttonMoveDownAction.setEnabled(false);
        buttonMoveDownAction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonMoveDownActionActionPerformed(evt);
            }
        });

        buttonAddAction.setText(JptBundle.INSTANCE.getString("SettingsActionsPanel.buttonAddAction.text")); // NOI18N
        buttonAddAction.setEnabled(false);
        buttonAddAction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddActionActionPerformed(evt);
            }
        });

        buttonDeleteAction.setText(JptBundle.INSTANCE.getString("SettingsActionsPanel.buttonDeleteAction.text")); // NOI18N
        buttonDeleteAction.setEnabled(false);
        buttonDeleteAction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteActionActionPerformed(evt);
            }
        });

        buttonShowActions.setText(JptBundle.INSTANCE.getString("SettingsActionsPanel.buttonShowActions.text")); // NOI18N
        buttonShowActions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonShowActionsActionPerformed(evt);
            }
        });

        buttonGroupExecute.add(radioButtonExecuteAlways);
        radioButtonExecuteAlways.setText(JptBundle.INSTANCE.getString("SettingsActionsPanel.radioButtonExecuteAlways.text")); // NOI18N
        radioButtonExecuteAlways.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonExecuteAlwaysActionPerformed(evt);
            }
        });

        buttonGroupExecute.add(radioButtonExecuteIfImageHasXmp);
        radioButtonExecuteIfImageHasXmp.setText(JptBundle.INSTANCE.getString("SettingsActionsPanel.radioButtonExecuteIfImageHasXmp.text")); // NOI18N
        radioButtonExecuteIfImageHasXmp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonExecuteIfImageHasXmpActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(radioButtonExecuteIfImageHasXmp)
                        .addContainerGap())
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(radioButtonExecuteAlways)
                            .addContainerGap())
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(buttonDeleteAction)
                                    .addComponent(buttonMoveUpAction)
                                    .addComponent(buttonMoveDownAction)
                                    .addComponent(buttonAddAction))
                                .addComponent(buttonShowActions, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addGap(12, 12, 12)))))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {buttonAddAction, buttonDeleteAction, buttonMoveDownAction, buttonMoveUpAction, buttonShowActions});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(buttonMoveUpAction)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonMoveDownAction)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonAddAction)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonDeleteAction)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonShowActions)
                        .addGap(8, 8, 8))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(radioButtonExecuteAlways)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonExecuteIfImageHasXmp)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void buttonShowActionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonShowActionsActionPerformed
        showActions();
    }//GEN-LAST:event_buttonShowActionsActionPerformed

    private void buttonMoveUpActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonMoveUpActionActionPerformed
        moveActionUp();
    }//GEN-LAST:event_buttonMoveUpActionActionPerformed

    private void buttonMoveDownActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonMoveDownActionActionPerformed
        moveActionDown();
    }//GEN-LAST:event_buttonMoveDownActionActionPerformed

    private void buttonDeleteActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteActionActionPerformed
        deleteAction();
    }//GEN-LAST:event_buttonDeleteActionActionPerformed

    private void buttonAddActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddActionActionPerformed
        addAction();
    }//GEN-LAST:event_buttonAddActionActionPerformed

    private void radioButtonExecuteAlwaysActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonExecuteAlwaysActionPerformed
        executeActionsAlways();
    }//GEN-LAST:event_radioButtonExecuteAlwaysActionPerformed

    private void radioButtonExecuteIfImageHasXmpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonExecuteIfImageHasXmpActionPerformed
        executeActionsIfXmpExists();
    }//GEN-LAST:event_radioButtonExecuteIfImageHasXmpActionPerformed

    private void menuItemAddActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemAddActionActionPerformed
        addAction();
    }//GEN-LAST:event_menuItemAddActionActionPerformed

    private void menuItemDeleteActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemDeleteActionActionPerformed
        deleteAction();
    }//GEN-LAST:event_menuItemDeleteActionActionPerformed

    private void menuItemMoveUpActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemMoveUpActionActionPerformed
        moveActionUp();
    }//GEN-LAST:event_menuItemMoveUpActionActionPerformed

    private void menuItemMoveDownActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemMoveDownActionActionPerformed
        moveActionDown();
    }//GEN-LAST:event_menuItemMoveDownActionActionPerformed

    private void listKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listKeyPressed
        handleListKeyPressed(evt);
    }//GEN-LAST:event_listKeyPressed

    private void menuItemShowActionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemShowActionsActionPerformed
        showActions();
    }//GEN-LAST:event_menuItemShowActionsActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAddAction;
    private javax.swing.JButton buttonDeleteAction;
    private javax.swing.ButtonGroup buttonGroupExecute;
    private javax.swing.JButton buttonMoveDownAction;
    private javax.swing.JButton buttonMoveUpAction;
    private javax.swing.JButton buttonShowActions;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JList list;
    private javax.swing.JMenuItem menuItemAddAction;
    private javax.swing.JMenuItem menuItemDeleteAction;
    private javax.swing.JMenuItem menuItemMoveDownAction;
    private javax.swing.JMenuItem menuItemMoveUpAction;
    private javax.swing.JMenuItem menuItemShowActions;
    private javax.swing.JPopupMenu popupMenu;
    private javax.swing.JRadioButton radioButtonExecuteAlways;
    private javax.swing.JRadioButton radioButtonExecuteIfImageHasXmp;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables
}
