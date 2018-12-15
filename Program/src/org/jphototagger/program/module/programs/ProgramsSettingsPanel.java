package org.jphototagger.program.module.programs;

import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.jphototagger.api.storage.Persistence;
import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.programs.ProgramType;
import org.jphototagger.domain.repository.ProgramsRepository;
import org.jphototagger.lib.help.HelpPageProvider;
import org.jphototagger.lib.swing.KeyEventUtil;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.MouseEventUtil;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.module.programs.ProgramsUtil.ReorderListener;
import org.jphototagger.resources.Icons;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class ProgramsSettingsPanel extends javax.swing.JPanel implements Persistence, HelpPageProvider {

    private static final long serialVersionUID = 1L;
    private final ProgramsListModel model = new ProgramsListModel(ProgramType.PROGRAM);
    private final ReorderListener reorderListener  = new ProgramsUtil.ReorderListener(model);
    private final ProgramsRepository repo = Lookup.getDefault().lookup(ProgramsRepository.class);

    public ProgramsSettingsPanel() {
        org.jphototagger.resources.UiFactory.configure(this);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        MnemonicUtil.setMnemonics((Container) this);
        setAccelerators();
        setEnabled();
        setDefaultProgramsButtonEnabled();
        model.addListDataListener(buttonDefaultProgramsEnabler);

        menuItemAddProgram.setIcon(Icons.getIcon("icon_new.png")); // NOI18N
        menuItemEditProgram.setIcon(Icons.getIcon("icon_edit.png")); // NOI18N
        menuItemRemoveProgram.setIcon(Icons.getIcon("icon_delete.png")); // NOI18N
        menuItemMoveProgramUp.setIcon(Icons.getIcon("icon_arrow_up.png")); // NOI18N
        menuItemMoveProgramDown.setIcon(Icons.getIcon("icon_arrow_down.png")); // NOI18N
    }

    private void setAccelerators() {
        menuItemAddProgram.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_N));
        menuItemMoveProgramUp.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_UP));
        menuItemMoveProgramDown.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_DOWN));
    }

    @Override
    public void restore() {
    }

    @Override
    public void persist() {
    }

    private void addProgram() {
        ProgramPropertiesDialog dlg = new ProgramPropertiesDialog(false);

        dlg.setVisible(true);

        if (dlg.isAccepted()) {
            repo.saveProgram(dlg.getProgram());
        }
    }

    private void editProgram() {
        if (listPrograms.getSelectedIndex() >= 0) {
            ProgramPropertiesDialog dlg = new ProgramPropertiesDialog(false);
            dlg.setProgram((Program) listPrograms.getSelectedValue());
            dlg.setVisible(true);
            if (dlg.isAccepted()) {
                repo.updateProgram(dlg.getProgram());
            }
        }
    }

    private void removeProgram() {
        int selectedIndex = listPrograms.getSelectedIndex();
        int modelIndex = listPrograms.convertIndexToModel(selectedIndex);
        if ((selectedIndex >= 0) && askRemove(model.getElementAt(modelIndex).toString())) {
            repo.deleteProgram((Program) model.get(modelIndex));
            setEnabled();
        }
    }

    private boolean askRemove(String otherImageOpenApp) {
        String message = Bundle.getString(ProgramsSettingsPanel.class, "ProgramsSettingsPanel.Confirm.RemoveImageOpenApp", otherImageOpenApp);
        return MessageDisplayer.confirmYesNo(this, message);
    }

    private void setEnabled() {
        boolean programSelected = isProgramSelected();
        int selectedIndex = listPrograms.getSelectedIndex();
        int size = listPrograms.getModel().getSize();
        boolean canMoveDown = programSelected && selectedIndex < size - 1;
        boolean canMoveUp = programSelected && selectedIndex > 0;
        buttonEditProgram.setEnabled(programSelected);
        menuItemEditProgram.setEnabled(programSelected);
        buttonRemoveProgram.setEnabled(programSelected);
        menuItemRemoveProgram.setEnabled(programSelected);
        buttonMoveProgramDown.setEnabled(canMoveDown);
        menuItemMoveProgramDown.setEnabled(canMoveDown);
        buttonMoveProgramUp.setEnabled(canMoveUp);
        menuItemMoveProgramUp.setEnabled(canMoveUp);
    }

    private void handleListProgramsKeyPressed(KeyEvent evt) {
        int keyCode = evt.getKeyCode();

        if (keyCode == KeyEvent.VK_DELETE) {
            removeProgram();
        } else if (keyCode == KeyEvent.VK_ENTER) {
            editProgram();
        } else if (KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_N)) {
            addProgram();
        } else if (KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_DOWN)) {
            moveProgramDown();
        } else if (KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_UP)) {
            moveProgramUp();
        }
    }

    private boolean isProgramSelected() {
        return listPrograms.getSelectedIndex() >= 0;
    }

    private void handleListOtherProgramsMouseClicked(MouseEvent evt) {
        if (MouseEventUtil.isDoubleClick(evt)) {
            editProgram();
        }
    }

    private void moveProgramDown() {
        reorderListener.setListenToModel(false);
        ProgramsUtil.moveProgramDown(listPrograms);
        reorderListener.setListenToModel(true);
    }

    private void moveProgramUp() {
        reorderListener.setListenToModel(false);
        ProgramsUtil.moveProgramUp(listPrograms);
        reorderListener.setListenToModel(true);
    }

    @Override
    public String getHelpPageUrl() {
        return Bundle.getString(ProgramsSettingsPanel.class, "ProgramsSettingsPanel.HelpPage");
    }

    private void editDefaultPrograms() {
        EditDefaultProgramsDialog dialog = new EditDefaultProgramsDialog();
        dialog.setVisible(true);
        ComponentUtil.parentWindowToFront(this);
    }

    private final ListDataListener buttonDefaultProgramsEnabler = new ListDataListener() {

        @Override
        public void intervalAdded(ListDataEvent e) {
            setDefaultProgramsButtonEnabled();
        }

        @Override
        public void intervalRemoved(ListDataEvent e) {
            setDefaultProgramsButtonEnabled();
        }

        @Override
        public void contentsChanged(ListDataEvent e) {
            setDefaultProgramsButtonEnabled();
        }
    };

    private void setDefaultProgramsButtonEnabled() {
        int programCount = model.getSize();
        buttonDefaultPrograms.setEnabled(programCount > 0);
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
        menuItemAddProgram = org.jphototagger.resources.UiFactory.menuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        menuItemEditProgram = org.jphototagger.resources.UiFactory.menuItem();
        menuItemRemoveProgram = org.jphototagger.resources.UiFactory.menuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        menuItemMoveProgramUp = org.jphototagger.resources.UiFactory.menuItem();
        menuItemMoveProgramDown = org.jphototagger.resources.UiFactory.menuItem();
        labelPrograms = org.jphototagger.resources.UiFactory.label();
        buttonDefaultPrograms = org.jphototagger.resources.UiFactory.button();
        scrollPanePrograms = org.jphototagger.resources.UiFactory.scrollPane();
        listPrograms = new org.jdesktop.swingx.JXList();
        labelInfoDefaultProgramFirstInList = org.jphototagger.resources.UiFactory.label();
        panelProgramButtons = org.jphototagger.resources.UiFactory.panel();
        buttonMoveProgramUp = org.jphototagger.resources.UiFactory.button();
        buttonMoveProgramDown = org.jphototagger.resources.UiFactory.button();
        buttonRemoveProgram = org.jphototagger.resources.UiFactory.button();
        buttonAddProgram = org.jphototagger.resources.UiFactory.button();
        buttonEditProgram = org.jphototagger.resources.UiFactory.button();

        menuItemAddProgram.setText(Bundle.getString(getClass(), "ProgramsSettingsPanel.menuItemAddProgram.text")); // NOI18N
        menuItemAddProgram.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemAddProgramActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemAddProgram);
        popupMenu.add(jSeparator1);

        menuItemEditProgram.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
        menuItemEditProgram.setText(Bundle.getString(getClass(), "ProgramsSettingsPanel.menuItemEditProgram.text")); // NOI18N
        menuItemEditProgram.setEnabled(false);
        menuItemEditProgram.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemEditProgramActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemEditProgram);

        menuItemRemoveProgram.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        menuItemRemoveProgram.setText(Bundle.getString(getClass(), "ProgramsSettingsPanel.menuItemRemoveProgram.text")); // NOI18N
        menuItemRemoveProgram.setEnabled(false);
        menuItemRemoveProgram.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemRemoveProgramActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemRemoveProgram);
        popupMenu.add(jSeparator2);

        menuItemMoveProgramUp.setText(Bundle.getString(getClass(), "ProgramsSettingsPanel.menuItemMoveProgramUp.text")); // NOI18N
        menuItemMoveProgramUp.setEnabled(false);
        menuItemMoveProgramUp.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemMoveProgramUpActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemMoveProgramUp);

        menuItemMoveProgramDown.setText(Bundle.getString(getClass(), "ProgramsSettingsPanel.menuItemMoveProgramDown.text")); // NOI18N
        menuItemMoveProgramDown.setEnabled(false);
        menuItemMoveProgramDown.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemMoveProgramDownActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemMoveProgramDown);

        setLayout(new java.awt.GridBagLayout());

        labelPrograms.setLabelFor(listPrograms);
        labelPrograms.setText(Bundle.getString(getClass(), "ProgramsSettingsPanel.labelPrograms.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(10, 10, 0, 0);
        add(labelPrograms, gridBagConstraints);

        buttonDefaultPrograms.setText(Bundle.getString(getClass(), "ProgramsSettingsPanel.buttonDefaultPrograms.text")); // NOI18N
        buttonDefaultPrograms.setEnabled(false);
        buttonDefaultPrograms.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDefaultProgramsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(10, 10, 0, 10);
        add(buttonDefaultPrograms, gridBagConstraints);

        listPrograms.setModel(model);
        listPrograms.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listPrograms.setCellRenderer(new org.jphototagger.program.module.programs.ProgramsListCellRenderer());
        listPrograms.setComponentPopupMenu(popupMenu);
        listPrograms.setDragEnabled(true);
        listPrograms.setDropMode(javax.swing.DropMode.INSERT);
        listPrograms.setTransferHandler(new org.jphototagger.program.datatransfer.ReorderListItemsTransferHandler(listPrograms));
        listPrograms.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listProgramsMouseClicked(evt);
            }
        });
        listPrograms.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            @Override
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listProgramsValueChanged(evt);
            }
        });
        listPrograms.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listProgramsKeyPressed(evt);
            }
        });
        scrollPanePrograms.setViewportView(listPrograms);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(10, 10, 0, 10);
        add(scrollPanePrograms, gridBagConstraints);

        labelInfoDefaultProgramFirstInList.setText(Bundle.getString(getClass(), "ProgramsSettingsPanel.labelInfoDefaultProgramFirstInList.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(10, 10, 0, 10);
        add(labelInfoDefaultProgramFirstInList, gridBagConstraints);

        panelProgramButtons.setLayout(new java.awt.GridBagLayout());

        buttonMoveProgramUp.setText(Bundle.getString(getClass(), "ProgramsSettingsPanel.buttonMoveProgramUp.text")); // NOI18N
        buttonMoveProgramUp.setEnabled(false);
        buttonMoveProgramUp.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonMoveProgramUpActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelProgramButtons.add(buttonMoveProgramUp, gridBagConstraints);

        buttonMoveProgramDown.setText(Bundle.getString(getClass(), "ProgramsSettingsPanel.buttonMoveProgramDown.text")); // NOI18N
        buttonMoveProgramDown.setEnabled(false);
        buttonMoveProgramDown.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonMoveProgramDownActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelProgramButtons.add(buttonMoveProgramDown, gridBagConstraints);

        buttonRemoveProgram.setText(Bundle.getString(getClass(), "ProgramsSettingsPanel.buttonRemoveProgram.text")); // NOI18N
        buttonRemoveProgram.setToolTipText(Bundle.getString(getClass(), "ProgramsSettingsPanel.buttonRemoveProgram.toolTipText")); // NOI18N
        buttonRemoveProgram.setEnabled(false);
        buttonRemoveProgram.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveProgramActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelProgramButtons.add(buttonRemoveProgram, gridBagConstraints);

        buttonAddProgram.setText(Bundle.getString(getClass(), "ProgramsSettingsPanel.buttonAddProgram.text")); // NOI18N
        buttonAddProgram.setToolTipText(Bundle.getString(getClass(), "ProgramsSettingsPanel.buttonAddProgram.toolTipText")); // NOI18N
        buttonAddProgram.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddProgramActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelProgramButtons.add(buttonAddProgram, gridBagConstraints);

        buttonEditProgram.setText(Bundle.getString(getClass(), "ProgramsSettingsPanel.buttonEditProgram.text")); // NOI18N
        buttonEditProgram.setToolTipText(Bundle.getString(getClass(), "ProgramsSettingsPanel.buttonEditProgram.toolTipText")); // NOI18N
        buttonEditProgram.setEnabled(false);
        buttonEditProgram.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEditProgramActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelProgramButtons.add(buttonEditProgram, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(10, 10, 10, 10);
        add(panelProgramButtons, gridBagConstraints);
    }//GEN-END:initComponents

    private void listProgramsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listProgramsValueChanged
        setEnabled();
    }//GEN-LAST:event_listProgramsValueChanged

    private void buttonRemoveProgramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRemoveProgramActionPerformed
        removeProgram();
    }//GEN-LAST:event_buttonRemoveProgramActionPerformed

    private void buttonAddProgramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddProgramActionPerformed
        addProgram();
    }//GEN-LAST:event_buttonAddProgramActionPerformed

    private void buttonEditProgramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonEditProgramActionPerformed
        editProgram();
    }//GEN-LAST:event_buttonEditProgramActionPerformed

    private void listProgramsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listProgramsMouseClicked
        handleListOtherProgramsMouseClicked(evt);
    }//GEN-LAST:event_listProgramsMouseClicked

    private void buttonMoveProgramDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonMoveProgramDownActionPerformed
        moveProgramDown();
    }//GEN-LAST:event_buttonMoveProgramDownActionPerformed

    private void buttonMoveProgramUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonMoveProgramUpActionPerformed
        moveProgramUp();
    }//GEN-LAST:event_buttonMoveProgramUpActionPerformed

    private void menuItemAddProgramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemAddProgramActionPerformed
        addProgram();
    }//GEN-LAST:event_menuItemAddProgramActionPerformed

    private void menuItemEditProgramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemEditProgramActionPerformed
        editProgram();
    }//GEN-LAST:event_menuItemEditProgramActionPerformed

    private void menuItemRemoveProgramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemRemoveProgramActionPerformed
        removeProgram();
    }//GEN-LAST:event_menuItemRemoveProgramActionPerformed

    private void menuItemMoveProgramUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemMoveProgramUpActionPerformed
        moveProgramUp();
    }//GEN-LAST:event_menuItemMoveProgramUpActionPerformed

    private void menuItemMoveProgramDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemMoveProgramDownActionPerformed
        moveProgramDown();
    }//GEN-LAST:event_menuItemMoveProgramDownActionPerformed

    private void listProgramsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listProgramsKeyPressed
        handleListProgramsKeyPressed(evt);
    }//GEN-LAST:event_listProgramsKeyPressed

    private void buttonDefaultProgramsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDefaultProgramsActionPerformed
        editDefaultPrograms();
    }//GEN-LAST:event_buttonDefaultProgramsActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAddProgram;
    private javax.swing.JButton buttonDefaultPrograms;
    private javax.swing.JButton buttonEditProgram;
    private javax.swing.JButton buttonMoveProgramDown;
    private javax.swing.JButton buttonMoveProgramUp;
    private javax.swing.JButton buttonRemoveProgram;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JLabel labelInfoDefaultProgramFirstInList;
    private javax.swing.JLabel labelPrograms;
    private org.jdesktop.swingx.JXList listPrograms;
    private javax.swing.JMenuItem menuItemAddProgram;
    private javax.swing.JMenuItem menuItemEditProgram;
    private javax.swing.JMenuItem menuItemMoveProgramDown;
    private javax.swing.JMenuItem menuItemMoveProgramUp;
    private javax.swing.JMenuItem menuItemRemoveProgram;
    private javax.swing.JPanel panelProgramButtons;
    private javax.swing.JPopupMenu popupMenu;
    private javax.swing.JScrollPane scrollPanePrograms;
    // End of variables declaration//GEN-END:variables
}
