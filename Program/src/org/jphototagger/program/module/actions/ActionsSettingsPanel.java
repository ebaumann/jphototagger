package org.jphototagger.program.module.actions;

import java.awt.Container;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.List;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.openide.util.Lookup;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.programs.ProgramType;
import org.jphototagger.domain.repository.ActionsAfterRepoUpdatesRepository;
import org.jphototagger.domain.repository.ProgramsRepository;
import org.jphototagger.domain.repository.Repository;
import org.jphototagger.domain.repository.event.programs.ProgramDeletedEvent;
import org.jphototagger.domain.repository.event.programs.ProgramInsertedEvent;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.KeyEventUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.settings.AppPreferencesKeys;
import org.jphototagger.program.module.programs.SelectProgramDialog;
import org.jphototagger.program.types.Persistence;

/**
 * @author Elmar Baumann
 */
public class ActionsSettingsPanel extends javax.swing.JPanel implements ListSelectionListener, Persistence {

    private static final long serialVersionUID = 1L;
    private final ActionsAfterSavesOrUpdatesInRepositoryListModel model = new ActionsAfterSavesOrUpdatesInRepositoryListModel();
    private final ProgramsRepository programsRepo = Lookup.getDefault().lookup(ProgramsRepository.class);
    private volatile boolean listenToModel = true;
    private final ActionsAfterRepoUpdatesRepository actionsRepo = Lookup.getDefault().lookup(ActionsAfterRepoUpdatesRepository.class);

    public ActionsSettingsPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        list.getSelectionModel().addListSelectionListener(this);
        MnemonicUtil.setMnemonics((Container) this);
        addAccelerators();
        setEnabled();
        AnnotationProcessor.process(this);
    }

    private void addAccelerators() {
        menuItemAddAction.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_N));
        menuItemMoveDownAction.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_DOWN));
        menuItemMoveUpAction.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_UP));
    }

    private void setEnabled() {
        boolean actionSelected = isActionSelected();
        boolean canMoveUp = canMoveUp();
        boolean canMoveDown = canMoveDown();

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

        Repository repo = Lookup.getDefault().lookup(Repository.class);

        if (repo != null && repo.isInit()) {
            hasActions = programsRepo.getProgramCount(true) > 0;
        }

        buttonAddAction.setEnabled(hasActions);
        menuItemAddAction.setEnabled(hasActions);
    }

    private void addAction() {
        SelectProgramDialog dlg = new SelectProgramDialog(ProgramType.ACTION);

        dlg.setVisible(true);

        Program action = dlg.getSelectedProgram();

        if (dlg.isAccepted() &&!model.contains(action)) {
            actionsRepo.saveAction(action, model.size());
            setEnabled();
        }
    }

    private void moveActionDown() {
        if (canMoveDown()) {
            listenToModel = false;
            int selectedIndex = list.getSelectedIndex();
            int modelIndex = list.convertIndexToModel(selectedIndex);
            List<Program> actions = model.getActions();
            if (modelIndex < actions.size() - 1) {
                Collections.swap(actions, modelIndex, modelIndex + 1);
                actionsRepo.setActionOrder(actions, 0);
            }
            setEnabled();
            listenToModel = true;
        }
    }

    private void moveActionUp() {
        if (canMoveUp()) {
            listenToModel = false;
            int selectedIndex = list.getSelectedIndex();
            int modelIndex = list.convertIndexToModel(selectedIndex);
            List<Program> actions = model.getActions();
            if (modelIndex > 0) {
                Collections.swap(actions, modelIndex, modelIndex - 1);
                actionsRepo.setActionOrder(actions, 0);
            }
            setEnabled();
            listenToModel = true;
        }
    }

    private void deleteAction() {
        if (isActionSelected()) {
            int selectedIndex = list.getSelectedIndex();
            int modelIndex = list.convertIndexToModel(selectedIndex);
            Program action = (Program) model.get(modelIndex);

            if (confirmDelete(action.getAlias())) {
                actionsRepo.deleteAction(action);
                setEnabled();
            }
        }
    }

    private void executeActionsAlways() {
        setExecuteActionsAfterImageChangeInDbAlways(radioButtonExecuteAlways.isSelected());
    }

    private void setExecuteActionsAfterImageChangeInDbAlways(boolean set) {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        storage.setBoolean(AppPreferencesKeys.KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_ALWAYS, set);
        storage.setBoolean(AppPreferencesKeys.KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_IF_IMAGE_HAS_XMP, !set);
    }

    private void executeActionsIfXmpExists() {
        setExecuteActionsAfterImageChangeInDbIfImageHasXmp(radioButtonExecuteIfImageHasXmp.isSelected());
    }

    private void setExecuteActionsAfterImageChangeInDbIfImageHasXmp(boolean set) {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        storage.setBoolean(AppPreferencesKeys.KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_ALWAYS, !set);
        storage.setBoolean(AppPreferencesKeys.KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_IF_IMAGE_HAS_XMP, set);
    }

    @Override
    public void restore() {
        radioButtonExecuteAlways.setSelected(isExecuteActionsAfterImageChangeInDbAlways());
        radioButtonExecuteIfImageHasXmp.setSelected(isExecuteActionsAfterImageChangeInDbIfImageHasXmp());
    }

    private boolean isExecuteActionsAfterImageChangeInDbIfImageHasXmp() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        return storage.containsKey(AppPreferencesKeys.KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_IF_IMAGE_HAS_XMP)
                ? storage.getBoolean(AppPreferencesKeys.KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_IF_IMAGE_HAS_XMP)
                : false;
    }

    private boolean isExecuteActionsAfterImageChangeInDbAlways() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        return storage.containsKey(AppPreferencesKeys.KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_ALWAYS)
                ? storage.getBoolean(AppPreferencesKeys.KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_ALWAYS)
                : false;
    }

    @Override
    public void persist() {

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
        String message = Bundle.getString(ActionsSettingsPanel.class, "ActionsPanel.Confirm.RemoveAction", actionName);

        return MessageDisplayer.confirmYesNo(this, message);
    }

    private boolean canMoveDown() {
        int selectedIndex = list.getSelectedIndex();
        int modelIndex = list.convertIndexToModel(selectedIndex);

        return (modelIndex < model.getSize() - 1) && (selectedIndex >= 0);
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

    @EventSubscriber(eventClass = ProgramInsertedEvent.class)
    public void programInserted(final ProgramInsertedEvent evt) {
        final Program program = evt.getProgram();

        if (program.isAction()) {
            setEnabledAddAction();
        }
    }

    @EventSubscriber(eventClass = ProgramDeletedEvent.class)
    public void programDeleted(ProgramDeletedEvent evt) {
        Program program = evt.getProgram();
        if (program.isAction()) {
            setEnabledAddAction();
        }
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    private void initComponents() {//GEN-BEGIN:initComponents

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
        list = new org.jdesktop.swingx.JXList();
        buttonMoveUpAction = new javax.swing.JButton();
        buttonMoveDownAction = new javax.swing.JButton();
        buttonAddAction = new javax.swing.JButton();
        buttonDeleteAction = new javax.swing.JButton();
        buttonShowActions = new javax.swing.JButton();
        radioButtonExecuteAlways = new javax.swing.JRadioButton();
        radioButtonExecuteIfImageHasXmp = new javax.swing.JRadioButton();

        popupMenu.setName("popupMenu"); // NOI18N

        menuItemAddAction.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_new.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/module/actions/Bundle"); // NOI18N
        menuItemAddAction.setText(bundle.getString("ActionsSettingsPanel.menuItemAddAction.text")); // NOI18N
        menuItemAddAction.setEnabled(false);
        menuItemAddAction.setName("menuItemAddAction"); // NOI18N
        menuItemAddAction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemAddActionActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemAddAction);

        menuItemDeleteAction.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        menuItemDeleteAction.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_delete.png"))); // NOI18N
        menuItemDeleteAction.setText(bundle.getString("ActionsSettingsPanel.menuItemDeleteAction.text")); // NOI18N
        menuItemDeleteAction.setEnabled(false);
        menuItemDeleteAction.setName("menuItemDeleteAction"); // NOI18N
        menuItemDeleteAction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemDeleteActionActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemDeleteAction);

        jSeparator1.setName("jSeparator1"); // NOI18N
        popupMenu.add(jSeparator1);

        menuItemMoveUpAction.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_arrow_up.png"))); // NOI18N
        menuItemMoveUpAction.setText(bundle.getString("ActionsSettingsPanel.menuItemMoveUpAction.text")); // NOI18N
        menuItemMoveUpAction.setEnabled(false);
        menuItemMoveUpAction.setName("menuItemMoveUpAction"); // NOI18N
        menuItemMoveUpAction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemMoveUpActionActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemMoveUpAction);

        menuItemMoveDownAction.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_arrow_down.png"))); // NOI18N
        menuItemMoveDownAction.setText(bundle.getString("ActionsSettingsPanel.menuItemMoveDownAction.text")); // NOI18N
        menuItemMoveDownAction.setEnabled(false);
        menuItemMoveDownAction.setName("menuItemMoveDownAction"); // NOI18N
        menuItemMoveDownAction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemMoveDownActionActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemMoveDownAction);

        jSeparator2.setName("jSeparator2"); // NOI18N
        popupMenu.add(jSeparator2);

        menuItemShowActions.setText(bundle.getString("ActionsSettingsPanel.menuItemShowActions.text")); // NOI18N
        menuItemShowActions.setName("menuItemShowActions"); // NOI18N
        menuItemShowActions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemShowActionsActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemShowActions);

        setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("ActionsSettingsPanel.border.title"))); // NOI18N
        setName("Form"); // NOI18N

        scrollPane.setName("scrollPane"); // NOI18N

        list.setModel(model);
        list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new org.jphototagger.program.module.actions.ActionsListCellRenderer());
        list.setComponentPopupMenu(popupMenu);
        list.setName("list"); // NOI18N
        list.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listKeyPressed(evt);
            }
        });
        scrollPane.setViewportView(list);

        buttonMoveUpAction.setText(bundle.getString("ActionsSettingsPanel.buttonMoveUpAction.text")); // NOI18N
        buttonMoveUpAction.setEnabled(false);
        buttonMoveUpAction.setName("buttonMoveUpAction"); // NOI18N
        buttonMoveUpAction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonMoveUpActionActionPerformed(evt);
            }
        });

        buttonMoveDownAction.setText(bundle.getString("ActionsSettingsPanel.buttonMoveDownAction.text")); // NOI18N
        buttonMoveDownAction.setEnabled(false);
        buttonMoveDownAction.setName("buttonMoveDownAction"); // NOI18N
        buttonMoveDownAction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonMoveDownActionActionPerformed(evt);
            }
        });

        buttonAddAction.setText(bundle.getString("ActionsSettingsPanel.buttonAddAction.text")); // NOI18N
        buttonAddAction.setEnabled(false);
        buttonAddAction.setName("buttonAddAction"); // NOI18N
        buttonAddAction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddActionActionPerformed(evt);
            }
        });

        buttonDeleteAction.setText(bundle.getString("ActionsSettingsPanel.buttonDeleteAction.text")); // NOI18N
        buttonDeleteAction.setEnabled(false);
        buttonDeleteAction.setName("buttonDeleteAction"); // NOI18N
        buttonDeleteAction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteActionActionPerformed(evt);
            }
        });

        buttonShowActions.setText(bundle.getString("ActionsSettingsPanel.buttonShowActions.text")); // NOI18N
        buttonShowActions.setName("buttonShowActions"); // NOI18N
        buttonShowActions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonShowActionsActionPerformed(evt);
            }
        });

        buttonGroupExecute.add(radioButtonExecuteAlways);
        radioButtonExecuteAlways.setText(bundle.getString("ActionsSettingsPanel.radioButtonExecuteAlways.text")); // NOI18N
        radioButtonExecuteAlways.setName("radioButtonExecuteAlways"); // NOI18N
        radioButtonExecuteAlways.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonExecuteAlwaysActionPerformed(evt);
            }
        });

        buttonGroupExecute.add(radioButtonExecuteIfImageHasXmp);
        radioButtonExecuteIfImageHasXmp.setText(bundle.getString("ActionsSettingsPanel.radioButtonExecuteIfImageHasXmp.text")); // NOI18N
        radioButtonExecuteIfImageHasXmp.setName("radioButtonExecuteIfImageHasXmp"); // NOI18N
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
    }//GEN-END:initComponents

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
    private org.jdesktop.swingx.JXList list;
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
