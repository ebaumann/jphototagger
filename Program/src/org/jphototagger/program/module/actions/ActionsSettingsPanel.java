package org.jphototagger.program.module.actions;

import java.awt.Container;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.List;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.storage.Persistence;
import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.programs.ProgramType;
import org.jphototagger.domain.repository.ActionsAfterRepoUpdatesRepository;
import org.jphototagger.domain.repository.ProgramsRepository;
import org.jphototagger.domain.repository.Repository;
import org.jphototagger.domain.repository.event.programs.ProgramDeletedEvent;
import org.jphototagger.domain.repository.event.programs.ProgramInsertedEvent;
import org.jphototagger.lib.help.HelpPageProvider;
import org.jphototagger.lib.swing.KeyEventUtil;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.module.programs.SelectProgramDialog;
import org.jphototagger.program.settings.AppPreferencesKeys;
import org.jphototagger.resources.Icons;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public class ActionsSettingsPanel extends PanelExt implements ListSelectionListener, Persistence, HelpPageProvider {

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

        menuItemAddAction.setIcon(Icons.getIcon("icon_new.png")); // NOI18N
        menuItemDeleteAction.setIcon(Icons.getIcon("icon_delete.png")); // NOI18N
        menuItemMoveUpAction.setIcon(Icons.getIcon("icon_arrow_up.png")); // NOI18N
        menuItemMoveDownAction.setIcon(Icons.getIcon("icon_arrow_down.png")); // NOI18N

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
        SelectProgramDialog dlg = new SelectProgramDialog(ComponentUtil.findParentDialog(this), ProgramType.ACTION);

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
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        prefs.setBoolean(AppPreferencesKeys.KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_ALWAYS, set);
        prefs.setBoolean(AppPreferencesKeys.KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_IF_IMAGE_HAS_XMP, !set);
    }

    private void executeActionsIfXmpExists() {
        setExecuteActionsAfterImageChangeInDbIfImageHasXmp(radioButtonExecuteIfImageHasXmp.isSelected());
    }

    private void setExecuteActionsAfterImageChangeInDbIfImageHasXmp(boolean set) {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        prefs.setBoolean(AppPreferencesKeys.KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_ALWAYS, !set);
        prefs.setBoolean(AppPreferencesKeys.KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_IF_IMAGE_HAS_XMP, set);
    }

    @Override
    public void restore() {
        radioButtonExecuteAlways.setSelected(isExecuteActionsAfterImageChangeInDbAlways());
        radioButtonExecuteIfImageHasXmp.setSelected(isExecuteActionsAfterImageChangeInDbIfImageHasXmp());
    }

    private boolean isExecuteActionsAfterImageChangeInDbIfImageHasXmp() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        return prefs.containsKey(AppPreferencesKeys.KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_IF_IMAGE_HAS_XMP)
                ? prefs.getBoolean(AppPreferencesKeys.KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_IF_IMAGE_HAS_XMP)
                : false;
    }

    private boolean isExecuteActionsAfterImageChangeInDbAlways() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        return prefs.containsKey(AppPreferencesKeys.KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_ALWAYS)
                ? prefs.getBoolean(AppPreferencesKeys.KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_ALWAYS)
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

    @Override
    public String getHelpPageUrl() {
        return Bundle.getString(ActionsSettingsPanel.class, "ActionsSettingsPanel.HelpPage");
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroupExecute = new javax.swing.ButtonGroup();
        popupMenu = UiFactory.popupMenu();
        menuItemAddAction = UiFactory.menuItem();
        menuItemDeleteAction = UiFactory.menuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        menuItemMoveUpAction = UiFactory.menuItem();
        menuItemMoveDownAction = UiFactory.menuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        menuItemShowActions = UiFactory.menuItem();
        scrollPane = UiFactory.scrollPane();
        list = UiFactory.jxList();
        panelButtons = UiFactory.panel();
        buttonMoveUpAction = UiFactory.button();
        buttonMoveDownAction = UiFactory.button();
        buttonAddAction = UiFactory.button();
        buttonDeleteAction = UiFactory.button();
        buttonShowActions = UiFactory.button();
        radioButtonExecuteAlways = UiFactory.radioButton();
        radioButtonExecuteIfImageHasXmp = UiFactory.radioButton();

        popupMenu.setName("popupMenu"); // NOI18N

        menuItemAddAction.setText(Bundle.getString(getClass(), "ActionsSettingsPanel.menuItemAddAction.text")); // NOI18N
        menuItemAddAction.setEnabled(false);
        menuItemAddAction.setName("menuItemAddAction"); // NOI18N
        menuItemAddAction.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemAddActionActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemAddAction);

        menuItemDeleteAction.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        menuItemDeleteAction.setText(Bundle.getString(getClass(), "ActionsSettingsPanel.menuItemDeleteAction.text")); // NOI18N
        menuItemDeleteAction.setEnabled(false);
        menuItemDeleteAction.setName("menuItemDeleteAction"); // NOI18N
        menuItemDeleteAction.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemDeleteActionActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemDeleteAction);

        jSeparator1.setName("jSeparator1"); // NOI18N
        popupMenu.add(jSeparator1);

        menuItemMoveUpAction.setText(Bundle.getString(getClass(), "ActionsSettingsPanel.menuItemMoveUpAction.text")); // NOI18N
        menuItemMoveUpAction.setEnabled(false);
        menuItemMoveUpAction.setName("menuItemMoveUpAction"); // NOI18N
        menuItemMoveUpAction.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemMoveUpActionActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemMoveUpAction);

        menuItemMoveDownAction.setText(Bundle.getString(getClass(), "ActionsSettingsPanel.menuItemMoveDownAction.text")); // NOI18N
        menuItemMoveDownAction.setEnabled(false);
        menuItemMoveDownAction.setName("menuItemMoveDownAction"); // NOI18N
        menuItemMoveDownAction.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemMoveDownActionActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemMoveDownAction);

        jSeparator2.setName("jSeparator2"); // NOI18N
        popupMenu.add(jSeparator2);

        menuItemShowActions.setText(Bundle.getString(getClass(), "ActionsSettingsPanel.menuItemShowActions.text")); // NOI18N
        menuItemShowActions.setName("menuItemShowActions"); // NOI18N
        menuItemShowActions.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemShowActionsActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemShowActions);

        setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString(getClass(), "ActionsSettingsPanel.border.title"))); // NOI18N
        
        setLayout(new java.awt.GridBagLayout());

        scrollPane.setName("scrollPane"); // NOI18N

        list.setModel(model);
        list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new org.jphototagger.program.module.actions.ActionsListCellRenderer());
        list.setComponentPopupMenu(popupMenu);
        list.setName("list"); // NOI18N
        list.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listKeyPressed(evt);
            }
        });
        scrollPane.setViewportView(list);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(scrollPane, gridBagConstraints);

        panelButtons.setName("panelButtons"); // NOI18N
        panelButtons.setLayout(new java.awt.GridBagLayout());

        buttonMoveUpAction.setText(Bundle.getString(getClass(), "ActionsSettingsPanel.buttonMoveUpAction.text")); // NOI18N
        buttonMoveUpAction.setEnabled(false);
        buttonMoveUpAction.setName("buttonMoveUpAction"); // NOI18N
        buttonMoveUpAction.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonMoveUpActionActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panelButtons.add(buttonMoveUpAction, gridBagConstraints);

        buttonMoveDownAction.setText(Bundle.getString(getClass(), "ActionsSettingsPanel.buttonMoveDownAction.text")); // NOI18N
        buttonMoveDownAction.setEnabled(false);
        buttonMoveDownAction.setName("buttonMoveDownAction"); // NOI18N
        buttonMoveDownAction.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonMoveDownActionActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        panelButtons.add(buttonMoveDownAction, gridBagConstraints);

        buttonAddAction.setText(Bundle.getString(getClass(), "ActionsSettingsPanel.buttonAddAction.text")); // NOI18N
        buttonAddAction.setEnabled(false);
        buttonAddAction.setName("buttonAddAction"); // NOI18N
        buttonAddAction.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddActionActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        panelButtons.add(buttonAddAction, gridBagConstraints);

        buttonDeleteAction.setText(Bundle.getString(getClass(), "ActionsSettingsPanel.buttonDeleteAction.text")); // NOI18N
        buttonDeleteAction.setEnabled(false);
        buttonDeleteAction.setName("buttonDeleteAction"); // NOI18N
        buttonDeleteAction.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteActionActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        panelButtons.add(buttonDeleteAction, gridBagConstraints);

        buttonShowActions.setText(Bundle.getString(getClass(), "ActionsSettingsPanel.buttonShowActions.text")); // NOI18N
        buttonShowActions.setName("buttonShowActions"); // NOI18N
        buttonShowActions.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonShowActionsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        panelButtons.add(buttonShowActions, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = UiFactory.insets(0, 7, 0, 0);
        add(panelButtons, gridBagConstraints);

        buttonGroupExecute.add(radioButtonExecuteAlways);
        radioButtonExecuteAlways.setText(Bundle.getString(getClass(), "ActionsSettingsPanel.radioButtonExecuteAlways.text")); // NOI18N
        radioButtonExecuteAlways.setName("radioButtonExecuteAlways"); // NOI18N
        radioButtonExecuteAlways.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonExecuteAlwaysActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(7, 0, 0, 0);
        add(radioButtonExecuteAlways, gridBagConstraints);

        buttonGroupExecute.add(radioButtonExecuteIfImageHasXmp);
        radioButtonExecuteIfImageHasXmp.setText(Bundle.getString(getClass(), "ActionsSettingsPanel.radioButtonExecuteIfImageHasXmp.text")); // NOI18N
        radioButtonExecuteIfImageHasXmp.setName("radioButtonExecuteIfImageHasXmp"); // NOI18N
        radioButtonExecuteIfImageHasXmp.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonExecuteIfImageHasXmpActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(radioButtonExecuteIfImageHasXmp, gridBagConstraints);
    }

    private void buttonShowActionsActionPerformed(java.awt.event.ActionEvent evt) {
        showActions();
    }

    private void buttonMoveUpActionActionPerformed(java.awt.event.ActionEvent evt) {
        moveActionUp();
    }

    private void buttonMoveDownActionActionPerformed(java.awt.event.ActionEvent evt) {
        moveActionDown();
    }

    private void buttonDeleteActionActionPerformed(java.awt.event.ActionEvent evt) {
        deleteAction();
    }

    private void buttonAddActionActionPerformed(java.awt.event.ActionEvent evt) {
        addAction();
        ComponentUtil.parentWindowToFront(this);
    }

    private void radioButtonExecuteAlwaysActionPerformed(java.awt.event.ActionEvent evt) {
        executeActionsAlways();
    }

    private void radioButtonExecuteIfImageHasXmpActionPerformed(java.awt.event.ActionEvent evt) {
        executeActionsIfXmpExists();
    }

    private void menuItemAddActionActionPerformed(java.awt.event.ActionEvent evt) {
        addAction();
    }

    private void menuItemDeleteActionActionPerformed(java.awt.event.ActionEvent evt) {
        deleteAction();
    }

    private void menuItemMoveUpActionActionPerformed(java.awt.event.ActionEvent evt) {
        moveActionUp();
    }

    private void menuItemMoveDownActionActionPerformed(java.awt.event.ActionEvent evt) {
        moveActionDown();
    }

    private void listKeyPressed(java.awt.event.KeyEvent evt) {
        handleListKeyPressed(evt);
    }

    private void menuItemShowActionsActionPerformed(java.awt.event.ActionEvent evt) {
        showActions();
    }

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
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPopupMenu popupMenu;
    private javax.swing.JRadioButton radioButtonExecuteAlways;
    private javax.swing.JRadioButton radioButtonExecuteIfImageHasXmp;
    private javax.swing.JScrollPane scrollPane;
}
