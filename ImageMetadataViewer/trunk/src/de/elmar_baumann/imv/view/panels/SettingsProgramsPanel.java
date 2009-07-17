package de.elmar_baumann.imv.view.panels;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.data.Program;
import de.elmar_baumann.imv.event.listener.impl.ListenerProvider;
import de.elmar_baumann.imv.event.UserSettingsChangeEvent;
import de.elmar_baumann.imv.model.ListModelPrograms;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.types.Persistence;
import de.elmar_baumann.imv.view.dialogs.ProgramPropertiesDialog;
import de.elmar_baumann.imv.view.renderer.ListCellRendererPrograms;
import de.elmar_baumann.lib.image.util.IconUtil;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.JFileChooser;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/11/02
 */
public final class SettingsProgramsPanel extends javax.swing.JPanel
        implements Persistence {

    private final ListModelPrograms model = new ListModelPrograms(false);
    private final ListenerProvider listenerProvider = ListenerProvider.INSTANCE;

    /** Creates new form SettingsProgramsPanel */
    public SettingsProgramsPanel() {
        initComponents();
        setEnabled();
    }

    @Override
    public void readProperties() {
        String filename = UserSettings.INSTANCE.getDefaultImageOpenApp();
        labelDefaultProgramFile.setText(filename);
        File file = new File(filename);
        if (file.exists()) {
            labelDefaultProgramFile.setIcon(IconUtil.getSystemIcon(file));
        }
    }

    @Override
    public void writeProperties() {
    }

    private void setDefaultProgram() {
        File file = chooseFile(labelDefaultProgramFile.getText());
        if (file != null && file.exists()) {
            labelDefaultProgramFile.setText(file.getAbsolutePath());
            notifyChangeListenerDefault();
        }
    }

    private File chooseFile(String startDirectory) {
        JFileChooser fileChooser = new JFileChooser(new File(startDirectory));
        fileChooser.setMultiSelectionEnabled(false);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.isDirectory()) {
                return file;
            }
        }
        return null;
    }

    private void addOtherProgram() {
        ProgramPropertiesDialog dialog = new ProgramPropertiesDialog(false);
        dialog.setVisible(true);
        if (dialog.accepted()) {
            model.add(dialog.getProgram());
            notifyChangeListenerOther();
        }
    }

    private void updateOtherProgram() {
        if (listOtherPrograms.getSelectedIndex() >= 0) {
            ProgramPropertiesDialog dialog = new ProgramPropertiesDialog(false);
            dialog.setProgram((Program) listOtherPrograms.getSelectedValue());
            dialog.setVisible(true);
            if (dialog.accepted()) {
                model.update(dialog.getProgram());
                notifyChangeListenerOther();
            }
        }
    }

    private void removeOtherProgram() {
        int index = listOtherPrograms.getSelectedIndex();
        if (index >= 0 && askRemove(model.getElementAt(index).toString())) {
            model.remove((Program) model.get(index));
            setEnabled();
            notifyChangeListenerOther();
        }
    }

    private boolean askRemove(String otherImageOpenApp) {
        return MessageDisplayer.confirm(
                "UserSettingsDialog.Confirm.RemoveImageOpenApp", // NOI18N
                MessageDisplayer.CancelButton.HIDE, otherImageOpenApp).equals(
                MessageDisplayer.ConfirmAction.YES);
    }

    private synchronized void notifyChangeListenerOther() {
        UserSettingsChangeEvent evt = new UserSettingsChangeEvent(
                UserSettingsChangeEvent.Type.OTHER_IMAGE_OPEN_APPS, this);
        listenerProvider.notifyUserSettingsChangeListener(evt);
    }

    private synchronized void notifyChangeListenerDefault() {
        UserSettingsChangeEvent evt = new UserSettingsChangeEvent(
                UserSettingsChangeEvent.Type.DEFAULT_IMAGE_OPEN_APP, this);
        evt.setDefaultImageOpenApp(new File(labelDefaultProgramFile.getText()));
        listenerProvider.notifyUserSettingsChangeListener(evt);
    }

    private void setEnabled() {
        boolean programSelected = isProgramSelected();
        buttonEditOtherProgram.setEnabled(programSelected);
        buttonRemoveOtherProgram.setEnabled(programSelected);
    }

    private boolean isProgramSelected() {
        return listOtherPrograms.getSelectedIndex() >= 0;
    }

    private void handleListOtherProgramsMouseClicked(MouseEvent evt) {
        if (evt.getClickCount() >= 2) {
            updateOtherProgram();
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

        panelBorder = new javax.swing.JPanel();
        labelChooseDefaultProgram = new javax.swing.JLabel();
        buttonChooseDefaultProgram = new javax.swing.JButton();
        labelDefaultProgramFile = new javax.swing.JLabel();
        labelOtherPrograms = new javax.swing.JLabel();
        scrollPaneOtherPrograms = new javax.swing.JScrollPane();
        listOtherPrograms = new javax.swing.JList();
        buttonRemoveOtherProgram = new javax.swing.JButton();
        buttonEditOtherProgram = new javax.swing.JButton();
        buttonAddOtherProgram = new javax.swing.JButton();

        panelBorder.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString("SettingsProgramsPanel.panelBorder.border.title"))); // NOI18N

        labelChooseDefaultProgram.setText(Bundle.getString("SettingsProgramsPanel.labelChooseDefaultProgram.text")); // NOI18N

        buttonChooseDefaultProgram.setMnemonic('a');
        buttonChooseDefaultProgram.setText(Bundle.getString("SettingsProgramsPanel.buttonChooseDefaultProgram.text")); // NOI18N
        buttonChooseDefaultProgram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseDefaultProgramActionPerformed(evt);
            }
        });

        labelDefaultProgramFile.setForeground(new java.awt.Color(0, 0, 255));
        labelDefaultProgramFile.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        labelOtherPrograms.setText(Bundle.getString("SettingsProgramsPanel.labelOtherPrograms.text")); // NOI18N

        listOtherPrograms.setModel(model);
        listOtherPrograms.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listOtherPrograms.setCellRenderer(new ListCellRendererPrograms());
        listOtherPrograms.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listOtherProgramsMouseClicked(evt);
            }
        });
        listOtherPrograms.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listOtherProgramsValueChanged(evt);
            }
        });
        scrollPaneOtherPrograms.setViewportView(listOtherPrograms);

        buttonRemoveOtherProgram.setMnemonic('e');
        buttonRemoveOtherProgram.setText(Bundle.getString("SettingsProgramsPanel.buttonRemoveOtherProgram.text")); // NOI18N
        buttonRemoveOtherProgram.setToolTipText(Bundle.getString("SettingsProgramsPanel.buttonRemoveOtherProgram.toolTipText")); // NOI18N
        buttonRemoveOtherProgram.setEnabled(false);
        buttonRemoveOtherProgram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveOtherProgramActionPerformed(evt);
            }
        });

        buttonEditOtherProgram.setMnemonic('b');
        buttonEditOtherProgram.setText(Bundle.getString("SettingsProgramsPanel.buttonEditOtherProgram.text")); // NOI18N
        buttonEditOtherProgram.setToolTipText(Bundle.getString("SettingsProgramsPanel.buttonEditOtherProgram.toolTipText")); // NOI18N
        buttonEditOtherProgram.setEnabled(false);
        buttonEditOtherProgram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEditOtherProgramActionPerformed(evt);
            }
        });

        buttonAddOtherProgram.setMnemonic('h');
        buttonAddOtherProgram.setText(Bundle.getString("SettingsProgramsPanel.buttonAddOtherProgram.text")); // NOI18N
        buttonAddOtherProgram.setToolTipText(Bundle.getString("SettingsProgramsPanel.buttonAddOtherProgram.toolTipText")); // NOI18N
        buttonAddOtherProgram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddOtherProgramActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelBorderLayout = new javax.swing.GroupLayout(panelBorder);
        panelBorder.setLayout(panelBorderLayout);
        panelBorderLayout.setHorizontalGroup(
            panelBorderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBorderLayout.createSequentialGroup()
                .addGroup(panelBorderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelBorderLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(scrollPaneOtherPrograms, javax.swing.GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE))
                    .addGroup(panelBorderLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(labelDefaultProgramFile, javax.swing.GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelBorderLayout.createSequentialGroup()
                        .addGroup(panelBorderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelBorderLayout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(labelOtherPrograms))
                            .addGroup(panelBorderLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(labelChooseDefaultProgram)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(buttonChooseDefaultProgram))
                    .addGroup(panelBorderLayout.createSequentialGroup()
                        .addContainerGap(175, Short.MAX_VALUE)
                        .addComponent(buttonRemoveOtherProgram)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonEditOtherProgram)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonAddOtherProgram)))
                .addContainerGap())
        );
        panelBorderLayout.setVerticalGroup(
            panelBorderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorderLayout.createSequentialGroup()
                .addGroup(panelBorderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelChooseDefaultProgram)
                    .addComponent(buttonChooseDefaultProgram))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelDefaultProgramFile, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelOtherPrograms)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPaneOtherPrograms, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelBorderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonAddOtherProgram)
                    .addComponent(buttonEditOtherProgram)
                    .addComponent(buttonRemoveOtherProgram))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 500, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panelBorder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 194, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panelBorder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
    }// </editor-fold>//GEN-END:initComponents

private void buttonChooseDefaultProgramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseDefaultProgramActionPerformed
    setDefaultProgram();
}//GEN-LAST:event_buttonChooseDefaultProgramActionPerformed

private void listOtherProgramsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listOtherProgramsValueChanged
    setEnabled();
}//GEN-LAST:event_listOtherProgramsValueChanged

private void buttonRemoveOtherProgramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRemoveOtherProgramActionPerformed
    removeOtherProgram();
}//GEN-LAST:event_buttonRemoveOtherProgramActionPerformed

private void buttonAddOtherProgramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddOtherProgramActionPerformed
    addOtherProgram();
}//GEN-LAST:event_buttonAddOtherProgramActionPerformed

private void buttonEditOtherProgramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonEditOtherProgramActionPerformed
    updateOtherProgram();
}//GEN-LAST:event_buttonEditOtherProgramActionPerformed

private void listOtherProgramsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listOtherProgramsMouseClicked
    handleListOtherProgramsMouseClicked(evt);
}//GEN-LAST:event_listOtherProgramsMouseClicked
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAddOtherProgram;
    private javax.swing.JButton buttonChooseDefaultProgram;
    private javax.swing.JButton buttonEditOtherProgram;
    private javax.swing.JButton buttonRemoveOtherProgram;
    private javax.swing.JLabel labelChooseDefaultProgram;
    private javax.swing.JLabel labelDefaultProgramFile;
    private javax.swing.JLabel labelOtherPrograms;
    private javax.swing.JList listOtherPrograms;
    private javax.swing.JPanel panelBorder;
    private javax.swing.JScrollPane scrollPaneOtherPrograms;
    // End of variables declaration//GEN-END:variables
}
