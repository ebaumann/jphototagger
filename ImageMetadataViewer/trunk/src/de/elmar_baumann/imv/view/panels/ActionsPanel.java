package de.elmar_baumann.imv.view.panels;

import de.elmar_baumann.imv.data.Program;
import de.elmar_baumann.imv.database.DatabaseActionsAfterDbInsertion;
import de.elmar_baumann.imv.event.ProgramActionEvent;
import de.elmar_baumann.imv.event.ProgramActionListener;
import de.elmar_baumann.imv.view.renderer.ListCellRendererActions;
import de.elmar_baumann.imv.model.ListModelPrograms;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.view.dialogs.ProgramPropertiesDialog;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 */
public final class ActionsPanel extends javax.swing.JPanel {

    private final ListModelPrograms model = new ListModelPrograms(true);
    private final List<ProgramActionListener> actionListeners = new ArrayList<ProgramActionListener>();
    private Object progressBarOwner;

    /** Creates new form ActionsPanel */
    public ActionsPanel() {
        initComponents();
    }

    public synchronized JProgressBar getProgressBar(Object owner) {
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

    private Program getSelectedProgram() {
        return (Program) list.getSelectedValue();
    }

    private void execute() {
        if (list.getSelectedIndex() >= 0) {
            Program program = getSelectedProgram();
            notify(new ProgramActionEvent(ProgramActionEvent.Type.ACTION_EXECUTE, program));
        }
    }

    private void handleButtonNewActionPerformed() {
        ProgramPropertiesDialog dialog = new ProgramPropertiesDialog(true);
        dialog.setVisible(true);
        if (dialog.accepted()) {
            Program program = dialog.getProgram();
            model.add(program);
            notify(new ProgramActionEvent(
                    ProgramActionEvent.Type.ACTION_CREATED, program));
        }
        setButtonsEnabled();
    }

    private void handleButtonEditActionPerformed() {
        if (list.getSelectedIndex() >= 0) {
            Program program = getSelectedProgram();
            ProgramPropertiesDialog dialog = new ProgramPropertiesDialog(true);
            dialog.setProgram(program);
            dialog.setVisible(true);
            if (dialog.accepted()) {
                model.update(program);
                notify(new ProgramActionEvent(
                        ProgramActionEvent.Type.ACTION_UPDATED, program));
            }
        }
        setButtonsEnabled();
    }

    private void handleListMouseClicked(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1) {
            if (evt.getClickCount() == 1) {
                setButtonsEnabled();
            } else if (evt.getClickCount() >= 2) {
                execute();
            }
        }
    }

    private void handleButtonDeleteActionPerformed() {
        if (list.getSelectedIndex() >= 0) {
            Program program = getSelectedProgram();
            if (confirmDelete(program)) {
                model.remove(program);
            }
            setButtonsEnabled();
        }
    }

    private boolean confirmDelete(Program program) {
        String programName = program.getAlias();
        boolean existsInActionsAfterDbInsertion = DatabaseActionsAfterDbInsertion.INSTANCE.existsAction(program);
        String msgPrefix = existsInActionsAfterDbInsertion
                ? Bundle.getString("ActionsPanel.ConfirmMessage.Delete.ExistsInOtherDb")
                : "";
        return JOptionPane.showConfirmDialog(
                this,
                msgPrefix +
                Bundle.getString("ActionsPanel.ConfirmMessage.Delete", programName),
                Bundle.getString("ActionsPanel.ConfirmMessage.Delete.Title"),
                JOptionPane.YES_NO_OPTION,
                existsInActionsAfterDbInsertion
                    ? JOptionPane.WARNING_MESSAGE
                    : JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    public synchronized void addActionListener(ProgramActionListener l) {
        actionListeners.add(l);
    }

    private synchronized void notify(ProgramActionEvent evt) {
        for (ProgramActionListener l : actionListeners) {
            l.actionPerformed(evt);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPane = new javax.swing.JScrollPane();
        list = new javax.swing.JList();
        progressBar = new javax.swing.JProgressBar();
        buttonDelete = new javax.swing.JButton();
        buttonEdit = new javax.swing.JButton();
        buttonNew = new javax.swing.JButton();
        buttonExecute = new javax.swing.JButton();

        setFocusable(false);

        scrollPane.setFocusable(false);

        list.setModel(model);
        list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new ListCellRendererActions());
        list.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listMouseClicked(evt);
            }
        });
        scrollPane.setViewportView(list);

        progressBar.setToolTipText(Bundle.getString("ActionsPanel.progressBar.toolTipText")); // NOI18N

        buttonDelete.setMnemonic('l');
        buttonDelete.setText(Bundle.getString("ActionsPanel.buttonDelete.text")); // NOI18N
        buttonDelete.setToolTipText(Bundle.getString("ActionsPanel.buttonDelete.toolTipText")); // NOI18N
        buttonDelete.setEnabled(false);
        buttonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteActionPerformed(evt);
            }
        });

        buttonEdit.setMnemonic('b');
        buttonEdit.setText(Bundle.getString("ActionsPanel.buttonEdit.text")); // NOI18N
        buttonEdit.setToolTipText(Bundle.getString("ActionsPanel.buttonEdit.toolTipText")); // NOI18N
        buttonEdit.setEnabled(false);
        buttonEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEditActionPerformed(evt);
            }
        });

        buttonNew.setMnemonic('n');
        buttonNew.setText(Bundle.getString("ActionsPanel.buttonNew.text")); // NOI18N
        buttonNew.setToolTipText(Bundle.getString("ActionsPanel.buttonNew.toolTipText")); // NOI18N
        buttonNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonNewActionPerformed(evt);
            }
        });

        buttonExecute.setMnemonic('a');
        buttonExecute.setText(Bundle.getString("ActionsPanel.buttonExecute.text")); // NOI18N
        buttonExecute.setToolTipText(Bundle.getString("ActionsPanel.buttonExecute.toolTipText")); // NOI18N
        buttonExecute.setEnabled(false);
        buttonExecute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonExecuteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
                    .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(buttonDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonEdit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonNew)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonExecute)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonExecute)
                    .addComponent(buttonNew)
                    .addComponent(buttonEdit)
                    .addComponent(buttonDelete))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

private void listMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listMouseClicked
    handleListMouseClicked(evt);
}//GEN-LAST:event_listMouseClicked

private void buttonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteActionPerformed
    handleButtonDeleteActionPerformed();
}//GEN-LAST:event_buttonDeleteActionPerformed

private void buttonEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonEditActionPerformed
    handleButtonEditActionPerformed();
}//GEN-LAST:event_buttonEditActionPerformed

private void buttonNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonNewActionPerformed
    handleButtonNewActionPerformed();
}//GEN-LAST:event_buttonNewActionPerformed

private void buttonExecuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonExecuteActionPerformed
    execute();
}//GEN-LAST:event_buttonExecuteActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonDelete;
    private javax.swing.JButton buttonEdit;
    private javax.swing.JButton buttonExecute;
    private javax.swing.JButton buttonNew;
    private javax.swing.JList list;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables
}
