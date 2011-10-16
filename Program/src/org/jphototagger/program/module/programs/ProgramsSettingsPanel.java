package org.jphototagger.program.module.programs;

import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import org.openide.util.Lookup;

import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.programs.ProgramType;
import org.jphototagger.domain.repository.ProgramsRepository;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.KeyEventUtil;
import org.jphototagger.lib.swing.MouseEventUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.module.programs.ProgramsUtil.ReorderListener;
import org.jphototagger.api.storage.Persistence;

/**
 * @author Elmar Baumann
 */
public final class ProgramsSettingsPanel extends javax.swing.JPanel implements Persistence {

    private static final long serialVersionUID = 1L;
    private final ProgramsListModel model = new ProgramsListModel(ProgramType.PROGRAM);
    private final ReorderListener reorderListener  = new ProgramsUtil.ReorderListener(model);
    private final ProgramsRepository repo = Lookup.getDefault().lookup(ProgramsRepository.class);

    public ProgramsSettingsPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        MnemonicUtil.setMnemonics((Container) this);
        setAccelerators();
        setEnabled();
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

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    private void initComponents() {//GEN-BEGIN:initComponents

        popupMenu = new javax.swing.JPopupMenu();
        menuItemAddProgram = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        menuItemEditProgram = new javax.swing.JMenuItem();
        menuItemRemoveProgram = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        menuItemMoveProgramUp = new javax.swing.JMenuItem();
        menuItemMoveProgramDown = new javax.swing.JMenuItem();
        labelChooseDefaultProgram = new javax.swing.JLabel();
        labelPrograms = new javax.swing.JLabel();
        scrollPanePrograms = new javax.swing.JScrollPane();
        listPrograms = new org.jdesktop.swingx.JXList();
        buttonRemoveProgram = new javax.swing.JButton();
        buttonMoveProgramUp = new javax.swing.JButton();
        buttonMoveProgramDown = new javax.swing.JButton();
        buttonAddProgram = new javax.swing.JButton();
        buttonEditProgram = new javax.swing.JButton();

        popupMenu.setName("popupMenu"); // NOI18N

        menuItemAddProgram.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_new.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/module/programs/Bundle"); // NOI18N
        menuItemAddProgram.setText(bundle.getString("ProgramsSettingsPanel.menuItemAddProgram.text")); // NOI18N
        menuItemAddProgram.setName("menuItemAddProgram"); // NOI18N
        menuItemAddProgram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemAddProgramActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemAddProgram);

        jSeparator1.setName("jSeparator1"); // NOI18N
        popupMenu.add(jSeparator1);

        menuItemEditProgram.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
        menuItemEditProgram.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_edit.png"))); // NOI18N
        menuItemEditProgram.setText(bundle.getString("ProgramsSettingsPanel.menuItemEditProgram.text")); // NOI18N
        menuItemEditProgram.setEnabled(false);
        menuItemEditProgram.setName("menuItemEditProgram"); // NOI18N
        menuItemEditProgram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemEditProgramActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemEditProgram);

        menuItemRemoveProgram.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        menuItemRemoveProgram.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_delete.png"))); // NOI18N
        menuItemRemoveProgram.setText(bundle.getString("ProgramsSettingsPanel.menuItemRemoveProgram.text")); // NOI18N
        menuItemRemoveProgram.setEnabled(false);
        menuItemRemoveProgram.setName("menuItemRemoveProgram"); // NOI18N
        menuItemRemoveProgram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemRemoveProgramActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemRemoveProgram);

        jSeparator2.setName("jSeparator2"); // NOI18N
        popupMenu.add(jSeparator2);

        menuItemMoveProgramUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_arrow_up.png"))); // NOI18N
        menuItemMoveProgramUp.setText(bundle.getString("ProgramsSettingsPanel.menuItemMoveProgramUp.text")); // NOI18N
        menuItemMoveProgramUp.setEnabled(false);
        menuItemMoveProgramUp.setName("menuItemMoveProgramUp"); // NOI18N
        menuItemMoveProgramUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemMoveProgramUpActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemMoveProgramUp);

        menuItemMoveProgramDown.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_arrow_down.png"))); // NOI18N
        menuItemMoveProgramDown.setText(bundle.getString("ProgramsSettingsPanel.menuItemMoveProgramDown.text")); // NOI18N
        menuItemMoveProgramDown.setEnabled(false);
        menuItemMoveProgramDown.setName("menuItemMoveProgramDown"); // NOI18N
        menuItemMoveProgramDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemMoveProgramDownActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemMoveProgramDown);

        setName("Form"); // NOI18N

        labelChooseDefaultProgram.setText(bundle.getString("ProgramsSettingsPanel.labelChooseDefaultProgram.text")); // NOI18N
        labelChooseDefaultProgram.setName("labelChooseDefaultProgram"); // NOI18N

        labelPrograms.setLabelFor(listPrograms);
        labelPrograms.setText(bundle.getString("ProgramsSettingsPanel.labelPrograms.text")); // NOI18N
        labelPrograms.setName("labelPrograms"); // NOI18N

        scrollPanePrograms.setName("scrollPanePrograms"); // NOI18N

        listPrograms.setModel(model);
        listPrograms.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listPrograms.setCellRenderer(new org.jphototagger.program.module.programs.ProgramsListCellRenderer());
        listPrograms.setComponentPopupMenu(popupMenu);
        listPrograms.setDragEnabled(true);
        listPrograms.setDropMode(javax.swing.DropMode.INSERT);
        listPrograms.setName("listPrograms"); // NOI18N
        listPrograms.setTransferHandler(new org.jphototagger.program.datatransfer.ReorderListItemsTransferHandler(listPrograms));
        listPrograms.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listProgramsMouseClicked(evt);
            }
        });
        listPrograms.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listProgramsValueChanged(evt);
            }
        });
        listPrograms.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listProgramsKeyPressed(evt);
            }
        });
        scrollPanePrograms.setViewportView(listPrograms);

        buttonRemoveProgram.setText(bundle.getString("ProgramsSettingsPanel.buttonRemoveProgram.text")); // NOI18N
        buttonRemoveProgram.setToolTipText(bundle.getString("ProgramsSettingsPanel.buttonRemoveProgram.toolTipText")); // NOI18N
        buttonRemoveProgram.setEnabled(false);
        buttonRemoveProgram.setName("buttonRemoveProgram"); // NOI18N
        buttonRemoveProgram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveProgramActionPerformed(evt);
            }
        });

        buttonMoveProgramUp.setText(bundle.getString("ProgramsSettingsPanel.buttonMoveProgramUp.text")); // NOI18N
        buttonMoveProgramUp.setEnabled(false);
        buttonMoveProgramUp.setName("buttonMoveProgramUp"); // NOI18N
        buttonMoveProgramUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonMoveProgramUpActionPerformed(evt);
            }
        });

        buttonMoveProgramDown.setText(bundle.getString("ProgramsSettingsPanel.buttonMoveProgramDown.text")); // NOI18N
        buttonMoveProgramDown.setEnabled(false);
        buttonMoveProgramDown.setName("buttonMoveProgramDown"); // NOI18N
        buttonMoveProgramDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonMoveProgramDownActionPerformed(evt);
            }
        });

        buttonAddProgram.setText(bundle.getString("ProgramsSettingsPanel.buttonAddProgram.text")); // NOI18N
        buttonAddProgram.setToolTipText(bundle.getString("ProgramsSettingsPanel.buttonAddProgram.toolTipText")); // NOI18N
        buttonAddProgram.setName("buttonAddProgram"); // NOI18N
        buttonAddProgram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddProgramActionPerformed(evt);
            }
        });

        buttonEditProgram.setText(bundle.getString("ProgramsSettingsPanel.buttonEditProgram.text")); // NOI18N
        buttonEditProgram.setToolTipText(bundle.getString("ProgramsSettingsPanel.buttonEditProgram.toolTipText")); // NOI18N
        buttonEditProgram.setEnabled(false);
        buttonEditProgram.setName("buttonEditProgram"); // NOI18N
        buttonEditProgram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEditProgramActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPanePrograms, javax.swing.GroupLayout.DEFAULT_SIZE, 614, Short.MAX_VALUE)
                    .addComponent(labelChooseDefaultProgram)
                    .addComponent(labelPrograms)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(buttonMoveProgramUp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonMoveProgramDown)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonRemoveProgram)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonAddProgram)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonEditProgram)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {buttonAddProgram, buttonEditProgram, buttonMoveProgramDown, buttonMoveProgramUp, buttonRemoveProgram});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelPrograms)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPanePrograms, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelChooseDefaultProgram)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonEditProgram)
                    .addComponent(buttonAddProgram)
                    .addComponent(buttonRemoveProgram)
                    .addComponent(buttonMoveProgramDown)
                    .addComponent(buttonMoveProgramUp))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {buttonAddProgram, buttonEditProgram, buttonMoveProgramDown, buttonMoveProgramUp, buttonRemoveProgram});

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
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAddProgram;
    private javax.swing.JButton buttonEditProgram;
    private javax.swing.JButton buttonMoveProgramDown;
    private javax.swing.JButton buttonMoveProgramUp;
    private javax.swing.JButton buttonRemoveProgram;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JLabel labelChooseDefaultProgram;
    private javax.swing.JLabel labelPrograms;
    private org.jdesktop.swingx.JXList listPrograms;
    private javax.swing.JMenuItem menuItemAddProgram;
    private javax.swing.JMenuItem menuItemEditProgram;
    private javax.swing.JMenuItem menuItemMoveProgramDown;
    private javax.swing.JMenuItem menuItemMoveProgramUp;
    private javax.swing.JMenuItem menuItemRemoveProgram;
    private javax.swing.JPopupMenu popupMenu;
    private javax.swing.JScrollPane scrollPanePrograms;
    // End of variables declaration//GEN-END:variables
}
