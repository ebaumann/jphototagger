package de.elmar_baumann.imv.view.dialogs;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.data.Program;
import de.elmar_baumann.imv.event.DialogActionsEvent;
import de.elmar_baumann.imv.event.DialogActionsListener;
import de.elmar_baumann.imv.model.ListModelPrograms;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.view.renderer.ListCellRendererActions;
import de.elmar_baumann.lib.dialog.Dialog;
import de.elmar_baumann.lib.persistence.PersistentAppSizes;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

/**
 * Non modal dialog for actions: {@link de.elmar_baumann.imv.data.Program}
 * where {@link de.elmar_baumann.imv.data.Program#isAction()} is true.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 */
public class ActionsDialog extends Dialog {

    private ListModelPrograms model = new ListModelPrograms(true);
    private static ActionsDialog instance = new ActionsDialog();
    private List<DialogActionsListener> actionListeners = new ArrayList<DialogActionsListener>();
    private Object progressBarOwner;

    private ActionsDialog() {
        super((java.awt.Frame) null, false);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setIconImages(AppSettings.getAppIcons());
        registerKeyStrokes();
    }

    public static ActionsDialog getInstance() {
        return instance;
    }

    synchronized public JProgressBar getProgressBar(Object owner) {
        if (progressBarOwner == null) {
            progressBarOwner = owner;
            return progressBar;
        }
        return null;
    }

    synchronized public boolean isProgressBarAvailable() {
        return progressBarOwner == null;
    }

    synchronized public void releaseProgressBar(Object owner) {
        if (progressBarOwner == owner) {
            progressBarOwner = null;
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            setButtonsEnabled();
            PersistentAppSizes.getSizeAndLocation(this);
        } else {
            PersistentAppSizes.setSizeAndLocation(this);
        }
        super.setVisible(visible);
    }

    public void addActionListener(DialogActionsListener l) {
        actionListeners.add(l);
    }

    private void notify(DialogActionsEvent evt) {
        for (DialogActionsListener l : actionListeners) {
            l.actionPerformed(evt);
        }
    }

    private void setButtonsEnabled() {
        boolean selected = list.getSelectedIndex() >= 0;
        buttonDelete.setEnabled(selected);
        buttonEdit.setEnabled(selected);
        buttonExecute.setEnabled(selected);
    }

    private Program getSelectedProgram() {
        return (Program) list.getSelectedValue();
    }

    private void execute() {
        if (list.getSelectedIndex() >= 0) {
            Program program = getSelectedProgram();
            notify(new DialogActionsEvent(DialogActionsEvent.Type.ActionExecute, program));
        }
    }

    private void handleButtonNewActionPerformed() {
        ProgramPropertiesDialog dialog = new ProgramPropertiesDialog(true);
        dialog.setVisible(true);
        if (dialog.isAccepted()) {
            Program program = dialog.getProgram();
            model.add(program);
            notify(new DialogActionsEvent(DialogActionsEvent.Type.ActionCreated, program));
        }
        setButtonsEnabled();
        toFront();
    }

    private void handleButtonEditActionPerformed() {
        if (list.getSelectedIndex() >= 0) {
            Program program = getSelectedProgram();
            ProgramPropertiesDialog dialog = new ProgramPropertiesDialog(true);
            dialog.setProgram(program);
            dialog.setVisible(true);
            if (dialog.isAccepted()) {
                model.update(program);
                notify(new DialogActionsEvent(DialogActionsEvent.Type.ActionUpdated, program));
            }
        }
        setButtonsEnabled();
        toFront();
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
            if (confirmDelete(program.getAlias())) {
                model.remove(program);
            }
            setButtonsEnabled();
        }
    }

    private boolean confirmDelete(String actionName) {
        MessageFormat msg = new MessageFormat("Aktion {0} löschen?");
        return JOptionPane.showConfirmDialog(
            this,
            msg.format(new Object[]{actionName}),
            "Frage",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            AppSettings.getMediumAppIcon()) == JOptionPane.YES_OPTION;
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
        buttonExecute = new javax.swing.JButton();
        buttonNew = new javax.swing.JButton();
        buttonEdit = new javax.swing.JButton();
        buttonDelete = new javax.swing.JButton();
        progressBar = new javax.swing.JProgressBar();

        setTitle(Bundle.getString("ActionsDialog.title")); // NOI18N

        list.setModel(model);
        list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new ListCellRendererActions());
        list.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listMouseClicked(evt);
            }
        });
        scrollPane.setViewportView(list);

        buttonExecute.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonExecute.setMnemonic('a');
        buttonExecute.setText(Bundle.getString("ActionsDialog.buttonExecute.text")); // NOI18N
        buttonExecute.setToolTipText(Bundle.getString("ActionsDialog.buttonExecute.toolTipText")); // NOI18N
        buttonExecute.setEnabled(false);
        buttonExecute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonExecuteActionPerformed(evt);
            }
        });

        buttonNew.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonNew.setMnemonic('n');
        buttonNew.setText(Bundle.getString("ActionsDialog.buttonNew.text")); // NOI18N
        buttonNew.setToolTipText(Bundle.getString("ActionsDialog.buttonNew.toolTipText")); // NOI18N
        buttonNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonNewActionPerformed(evt);
            }
        });

        buttonEdit.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonEdit.setMnemonic('b');
        buttonEdit.setText(Bundle.getString("ActionsDialog.buttonEdit.text")); // NOI18N
        buttonEdit.setToolTipText(Bundle.getString("ActionsDialog.buttonEdit.toolTipText")); // NOI18N
        buttonEdit.setEnabled(false);
        buttonEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEditActionPerformed(evt);
            }
        });

        buttonDelete.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonDelete.setMnemonic('l');
        buttonDelete.setText(Bundle.getString("ActionsDialog.buttonDelete.text")); // NOI18N
        buttonDelete.setToolTipText(Bundle.getString("ActionsDialog.buttonDelete.toolTipText")); // NOI18N
        buttonDelete.setEnabled(false);
        buttonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteActionPerformed(evt);
            }
        });

        progressBar.setToolTipText(Bundle.getString("ActionsDialog.progressBar.toolTipText")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrollPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(buttonDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonEdit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonNew)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonExecute))
                    .addComponent(progressBar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonExecute)
                    .addComponent(buttonNew)
                    .addComponent(buttonEdit)
                    .addComponent(buttonDelete))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void buttonExecuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonExecuteActionPerformed
    execute();
}//GEN-LAST:event_buttonExecuteActionPerformed

private void buttonNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonNewActionPerformed
    handleButtonNewActionPerformed();
}//GEN-LAST:event_buttonNewActionPerformed

private void buttonEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonEditActionPerformed
    handleButtonEditActionPerformed();
}//GEN-LAST:event_buttonEditActionPerformed

private void buttonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteActionPerformed
    handleButtonDeleteActionPerformed();
}//GEN-LAST:event_buttonDeleteActionPerformed

private void listMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listMouseClicked
    handleListMouseClicked(evt);
}//GEN-LAST:event_listMouseClicked

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ActionsDialog dialog = new ActionsDialog();
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

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
