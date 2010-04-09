/*
 * @(#)ActionsPanel.java    Created on 
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.view.panels;

import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.data.Program;
import org.jphototagger.program.database.DatabaseActionsAfterDbInsertion;
import org.jphototagger.program.database.DatabasePrograms.Type;
import org.jphototagger.program.event.listener.impl.ListenerSupport;
import org.jphototagger.program.event.listener.ProgramActionListener;
import org.jphototagger.program.model.ListModelPrograms;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.view.dialogs.ProgramPropertiesDialog;
import org.jphototagger.program.view.renderer.ListCellRendererActions;
import org.jphototagger.lib.componentutil.MnemonicUtil;

import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import java.util.Set;

import javax.swing.JProgressBar;
import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.lib.event.util.MouseEventUtil;
import org.jphototagger.program.database.DatabasePrograms;

/**
 *
 * @author  Elmar Baumann
 */
public final class ActionsPanel extends javax.swing.JPanel {
    private static final long                            serialVersionUID =
        8875330844851092391L;
    private final ListModelPrograms                      model            =
        new ListModelPrograms(Type.ACTION);
    private final ListenerSupport<ProgramActionListener> listenerSupport  =
        new ListenerSupport<ProgramActionListener>();
    private Object progressBarOwner;

    public ActionsPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        MnemonicUtil.setMnemonics((Container) this);
        menuItemCreate.setAccelerator(
                KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_N));
        menuItemEdit.setAccelerator(
                KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_E));
        if (list.getModel().getSize() > 0) {
            list.setSelectedIndex(0);
        }
    }

    public synchronized JProgressBar getProgressBar(Object owner) {
        if (owner == null) {
            throw new NullPointerException("owner == null");
        }

        if (progressBarOwner == null) {
            progressBarOwner = owner;

            return progressBar;
        }

        return null;
    }

    public synchronized boolean isProgressBarAvailable() {
        return progressBarOwner == null;
    }

    public synchronized void releaseProgressBar(Object owner) {
        if (owner == null) {
            throw new NullPointerException("owner == null");
        }

        if (progressBarOwner == owner) {
            progressBarOwner = null;
        }
    }

    public void setButtonsEnabled() {
        boolean selectedIndex = list.getSelectedIndex() >= 0;

        buttonDelete.setEnabled(selectedIndex);
        buttonEdit.setEnabled(selectedIndex);
        buttonExecute.setEnabled(selectedIndex);
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

        dlg.setVisible(true);

        if (dlg.isAccepted()) {
            Program program = dlg.getProgram();

            if (DatabasePrograms.INSTANCE.insert(program)) {
                selectLastListItem();
            }
        }

        setButtonsEnabled();
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
            Program                 program = getSelectedAction();
            ProgramPropertiesDialog dlg     = new ProgramPropertiesDialog(true);

            dlg.setProgram(program);
            dlg.setVisible(true);

            if (dlg.isAccepted()) {
                DatabasePrograms.INSTANCE.update(program);
            }
        }

        setButtonsEnabled();
        list.requestFocusInWindow();
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
        }
    }

    private void handleListMouseClicked(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1) {
            if (evt.getClickCount() == 1) {
                setButtonsEnabled();
            } else if (MouseEventUtil.isDoubleClick(evt)) {
                executeAction();
            }
        }
    }

    private void deleteAction() {
        if (list.getSelectedIndex() >= 0) {
            Program program = getSelectedAction();

            if (confirmDelete(program)) {
                DatabasePrograms.INSTANCE.delete(program);
            }

            setButtonsEnabled();
            list.requestFocusInWindow();
        }
    }

    private boolean confirmDelete(Program program) {
        String  programName                     = program.getAlias();
        boolean existsInActionsAfterDbInsertion =
            DatabaseActionsAfterDbInsertion.INSTANCE.exists(program);

        return existsInActionsAfterDbInsertion
               ? MessageDisplayer.confirmYesNo(this,
                "ActionsPanel.Confirm.Delete.ExistsInOtherDb", programName)
               : MessageDisplayer.confirmYesNo(this,
                "ActionsPanel.Confirm.Delete", programName);
    }

    public synchronized void addListener(ProgramActionListener l) {
        if (l == null) {
            throw new NullPointerException("l == null");
        }

        listenerSupport.add(l);
    }

    private synchronized void notifyProgramExecuted(Program program) {
        Set<ProgramActionListener> listeners = listenerSupport.get();

        synchronized (listeners) {
            for (ProgramActionListener l : listeners) {
                l.programShallBeExecuted(program);
            }
        }
    }

    private void setEnabledPopupMenuItems() {
        boolean isSelected = list.getSelectedIndex() >= 0;

        menuItemDelete.setEnabled(isSelected);
        menuItemEdit.setEnabled(isSelected);
        menuItemExecute.setEnabled(isSelected);
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
        java.awt.GridBagConstraints gridBagConstraints;

        popupMenu = new javax.swing.JPopupMenu();
        menuItemExecute = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        menuItemCreate = new javax.swing.JMenuItem();
        menuItemEdit = new javax.swing.JMenuItem();
        menuItemDelete = new javax.swing.JMenuItem();
        labelActionList = new javax.swing.JLabel();
        scrollPane = new javax.swing.JScrollPane();
        list = new javax.swing.JList();
        progressBar = new javax.swing.JProgressBar();
        panelButtons = new javax.swing.JPanel();
        buttonDelete = new javax.swing.JButton();
        buttonEdit = new javax.swing.JButton();
        buttonCreate = new javax.swing.JButton();
        buttonExecute = new javax.swing.JButton();

        popupMenu.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                popupMenuPopupMenuWillBecomeVisible(evt);
            }
        });

        menuItemExecute.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
        menuItemExecute.setText(JptBundle.INSTANCE.getString("ActionsPanel.menuItemExecute.text")); // NOI18N
        menuItemExecute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemExecuteActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemExecute);
        popupMenu.add(jSeparator1);

        menuItemCreate.setText(JptBundle.INSTANCE.getString("ActionsPanel.menuItemCreate.text")); // NOI18N
        menuItemCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemCreateActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemCreate);

        menuItemEdit.setText(JptBundle.INSTANCE.getString("ActionsPanel.menuItemEdit.text")); // NOI18N
        menuItemEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemEditActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemEdit);

        menuItemDelete.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        menuItemDelete.setText(JptBundle.INSTANCE.getString("ActionsPanel.menuItemDelete.text")); // NOI18N
        menuItemDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemDeleteActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemDelete);

        setFocusable(false);
        setLayout(new java.awt.GridBagLayout());

        labelActionList.setLabelFor(list);
        labelActionList.setText(JptBundle.INSTANCE.getString("ActionsPanel.labelActionList.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(labelActionList, gridBagConstraints);

        scrollPane.setFocusable(false);

        list.setModel(model);
        list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new ListCellRendererActions());
        list.setComponentPopupMenu(popupMenu);
        list.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listMouseClicked(evt);
            }
        });
        list.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listValueChanged(evt);
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
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(scrollPane, gridBagConstraints);

        progressBar.setToolTipText(JptBundle.INSTANCE.getString("ActionsPanel.progressBar.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(progressBar, gridBagConstraints);

        buttonDelete.setText(JptBundle.INSTANCE.getString("ActionsPanel.buttonDelete.text")); // NOI18N
        buttonDelete.setToolTipText(JptBundle.INSTANCE.getString("ActionsPanel.buttonDelete.toolTipText")); // NOI18N
        buttonDelete.setEnabled(false);
        buttonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteActionPerformed(evt);
            }
        });

        buttonEdit.setText(JptBundle.INSTANCE.getString("ActionsPanel.buttonEdit.text")); // NOI18N
        buttonEdit.setToolTipText(JptBundle.INSTANCE.getString("ActionsPanel.buttonEdit.toolTipText")); // NOI18N
        buttonEdit.setEnabled(false);
        buttonEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEditActionPerformed(evt);
            }
        });

        buttonCreate.setText(JptBundle.INSTANCE.getString("ActionsPanel.buttonCreate.text")); // NOI18N
        buttonCreate.setToolTipText(JptBundle.INSTANCE.getString("ActionsPanel.buttonCreate.toolTipText")); // NOI18N
        buttonCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCreateActionPerformed(evt);
            }
        });

        buttonExecute.setText(JptBundle.INSTANCE.getString("ActionsPanel.buttonExecute.text")); // NOI18N
        buttonExecute.setToolTipText(JptBundle.INSTANCE.getString("ActionsPanel.buttonExecute.toolTipText")); // NOI18N
        buttonExecute.setEnabled(false);
        buttonExecute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonExecuteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelButtonsLayout = new javax.swing.GroupLayout(panelButtons);
        panelButtons.setLayout(panelButtonsLayout);
        panelButtonsLayout.setHorizontalGroup(
            panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelButtonsLayout.createSequentialGroup()
                .addComponent(buttonDelete)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonEdit)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonCreate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonExecute))
        );
        panelButtonsLayout.setVerticalGroup(
            panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(buttonExecute)
                .addComponent(buttonCreate)
                .addComponent(buttonEdit)
                .addComponent(buttonDelete))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 3, 5);
        add(panelButtons, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void listValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listValueChanged
        if (!evt.getValueIsAdjusting()) {
            setButtonsEnabled();
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

    private void popupMenuPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_popupMenuPopupMenuWillBecomeVisible
        setEnabledPopupMenuItems();
    }//GEN-LAST:event_popupMenuPopupMenuWillBecomeVisible

    private void listKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listKeyPressed
        handleListKeyPressed(evt);
    }//GEN-LAST:event_listKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCreate;
    private javax.swing.JButton buttonDelete;
    private javax.swing.JButton buttonEdit;
    private javax.swing.JButton buttonExecute;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JLabel labelActionList;
    private javax.swing.JList list;
    private javax.swing.JMenuItem menuItemCreate;
    private javax.swing.JMenuItem menuItemDelete;
    private javax.swing.JMenuItem menuItemEdit;
    private javax.swing.JMenuItem menuItemExecute;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPopupMenu popupMenu;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables
}
