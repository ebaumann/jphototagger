package de.elmar_baumann.imv.view.panels;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.event.ListenerProvider;
import de.elmar_baumann.imv.event.UserSettingsChangeEvent;
import de.elmar_baumann.imv.model.ListModelPrograms;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.types.Persistence;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/11/02
 */
public class SettingsProgramsPanel extends javax.swing.JPanel
    implements Persistence {

    private String previousDirectory = ""; // NOI18N
    private ListModelPrograms modelPrograms = new ListModelPrograms();
    private ListenerProvider listenerProvider = ListenerProvider.getInstance();

    /** Creates new form SettingsProgramsPanel */
    public SettingsProgramsPanel() {
        initComponents();
    }

    private void moveDownOpenImageApp() {
        if (canMoveDownOpenImageApp()) {
            int selectedIndex = listOtherImageOpenApps.getSelectedIndex();
            int newSelectedIndex = selectedIndex + 1;
            Object element = modelPrograms.get(selectedIndex);
            modelPrograms.remove(selectedIndex);
            modelPrograms.add(newSelectedIndex, element);
            listOtherImageOpenApps.setSelectedIndex(newSelectedIndex);
            setEnabled();
        }
    }

    private boolean canMoveDownOpenImageApp() {
        int selectedIndex = listOtherImageOpenApps.getSelectedIndex();
        int lastIndex = modelPrograms.getSize() - 1;
        return selectedIndex >= 0 && selectedIndex < lastIndex;
    }

    private void moveUpOpenImageApp() {
        if (canMoveUpOpenImageApp()) {
            int selectedIndex = listOtherImageOpenApps.getSelectedIndex();
            int newSelectedIndex = selectedIndex - 1;
            Object element = modelPrograms.get(selectedIndex);
            modelPrograms.remove(selectedIndex);
            modelPrograms.add(newSelectedIndex, element);
            listOtherImageOpenApps.setSelectedIndex(newSelectedIndex);
            setEnabled();
        }
    }

    private boolean canMoveUpOpenImageApp() {
        return listOtherImageOpenApps.getSelectedIndex() > 0;
    }

    private void setEnabled() {
        buttonRemoveOtherImageOpenApp.setEnabled(isOtherOpenImageAppSelected());
    }

    private boolean isOtherOpenImageAppSelected() {
        return listOtherImageOpenApps.getSelectedIndex() >= 0;
    }

    @Override
    public void readPersistent() {
        previousDirectory = labelDefaultImageOpenApp.getText();
        labelDefaultImageOpenApp.setText(UserSettings.getInstance().getDefaultImageOpenApp());
    }

    @Override
    public void writePersistent() {
    }

    private void setDefaultOpenApp() {
        File file = chooseDirectory(labelDefaultImageOpenApp.getText());
        if (file != null && file.exists()) {
            labelDefaultImageOpenApp.setText(file.getAbsolutePath());
            UserSettingsChangeEvent evt = new UserSettingsChangeEvent(
                UserSettingsChangeEvent.Type.DefaultImageOpenApp, this);
            evt.setDefaultImageOpenApp(file);
            notifyChangeListener(evt);
        }
    }

    private void addOtherOpenImageApp() {
        File file = chooseDirectory(previousDirectory);
        if (file != null && modelPrograms.add(file)) {
            setEnabled();
            UserSettingsChangeEvent evt = new UserSettingsChangeEvent(
                UserSettingsChangeEvent.Type.OtherImageOpenApps, this);
            evt.setOtherImageOpenApps(getOtherImageOpenApps());
            notifyChangeListener(evt);
        }
    }

    private List<File> getOtherImageOpenApps() {
        List<File> apps = new ArrayList<File>();
        int size = modelPrograms.getSize();
        for (int i = 0; i < size; i++) {
            apps.add((File) modelPrograms.getElementAt(i));
        }
        return apps;
    }

    private void removeOtherOpenImageApp() {
        int index = listOtherImageOpenApps.getSelectedIndex();
        if (index >= 0 && askRemove(modelPrograms.getElementAt(index).toString()) && modelPrograms.remove(modelPrograms.get(index))) {
            listOtherImageOpenApps.setSelectedIndex(index);
            setEnabled();
            UserSettingsChangeEvent evt = new UserSettingsChangeEvent(
                UserSettingsChangeEvent.Type.OtherImageOpenApps, this);
            evt.setOtherImageOpenApps(getOtherImageOpenApps());
            notifyChangeListener(evt);
        }
    }

    private boolean askRemove(String otherImageOpenApp) {
        MessageFormat msg = new MessageFormat(Bundle.getString("UserSettingsDialog.ConfirmMessage.RemoveImageOpenApp"));
        return JOptionPane.showConfirmDialog(
            this,
            msg.format(new Object[]{otherImageOpenApp}),
            Bundle.getString("UserSettingsDialog.ConfirmMessage.RemoveImageOpenApp.Title"),
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            AppSettings.getMediumAppIcon()) == JOptionPane.YES_OPTION;
    }

    private File chooseDirectory(String startDirectory) {
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

    private void notifyChangeListener(UserSettingsChangeEvent evt) {
        listenerProvider.notifyUserSettingsChangeListener(evt);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelImageOpenApps = new javax.swing.JPanel();
        labelDefaultImageOpenAppPrompt = new javax.swing.JLabel();
        buttonDefaultImageOpenApp = new javax.swing.JButton();
        labelDefaultImageOpenApp = new javax.swing.JLabel();
        labelInfoOtherOpenImageApps = new javax.swing.JLabel();
        scrollPaneListOtherImageOpenApps = new javax.swing.JScrollPane();
        listOtherImageOpenApps = new javax.swing.JList();
        buttonRemoveOtherImageOpenApp = new javax.swing.JButton();
        buttonAddOtherImageOpenApp = new javax.swing.JButton();

        panelImageOpenApps.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Programme zum Öffnen von Bildern", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11))); // NOI18N

        labelDefaultImageOpenAppPrompt.setFont(new java.awt.Font("Dialog", 0, 12));
        labelDefaultImageOpenAppPrompt.setText(Bundle.getString("SettingsProgramsPanel.labelDefaultImageOpenAppPrompt.text")); // NOI18N

        buttonDefaultImageOpenApp.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonDefaultImageOpenApp.setMnemonic('a');
        buttonDefaultImageOpenApp.setText(Bundle.getString("SettingsProgramsPanel.buttonDefaultImageOpenApp.text")); // NOI18N
        buttonDefaultImageOpenApp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDefaultImageOpenAppActionPerformed(evt);
            }
        });

        labelDefaultImageOpenApp.setFont(new java.awt.Font("Dialog", 0, 12));
        labelDefaultImageOpenApp.setForeground(new java.awt.Color(0, 0, 255));
        labelDefaultImageOpenApp.setText(Bundle.getString("SettingsProgramsPanel.labelDefaultImageOpenApp.text")); // NOI18N
        labelDefaultImageOpenApp.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        labelInfoOtherOpenImageApps.setFont(new java.awt.Font("Dialog", 0, 12));
        labelInfoOtherOpenImageApps.setText(Bundle.getString("SettingsProgramsPanel.labelInfoOtherOpenImageApps.text")); // NOI18N

        listOtherImageOpenApps.setModel(modelPrograms);
        listOtherImageOpenApps.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listOtherImageOpenApps.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listOtherImageOpenAppsValueChanged(evt);
            }
        });
        scrollPaneListOtherImageOpenApps.setViewportView(listOtherImageOpenApps);

        buttonRemoveOtherImageOpenApp.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        buttonRemoveOtherImageOpenApp.setMnemonic('e');
        buttonRemoveOtherImageOpenApp.setText(Bundle.getString("SettingsProgramsPanel.buttonRemoveOtherImageOpenApp.text")); // NOI18N
        buttonRemoveOtherImageOpenApp.setEnabled(false);
        buttonRemoveOtherImageOpenApp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveOtherImageOpenAppActionPerformed(evt);
            }
        });

        buttonAddOtherImageOpenApp.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        buttonAddOtherImageOpenApp.setMnemonic('w');
        buttonAddOtherImageOpenApp.setText(Bundle.getString("SettingsProgramsPanel.buttonAddOtherImageOpenApp.text")); // NOI18N
        buttonAddOtherImageOpenApp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddOtherImageOpenAppActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelImageOpenAppsLayout = new javax.swing.GroupLayout(panelImageOpenApps);
        panelImageOpenApps.setLayout(panelImageOpenAppsLayout);
        panelImageOpenAppsLayout.setHorizontalGroup(
            panelImageOpenAppsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelImageOpenAppsLayout.createSequentialGroup()
                .addGroup(panelImageOpenAppsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelImageOpenAppsLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(labelInfoOtherOpenImageApps))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelImageOpenAppsLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelImageOpenAppsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(labelDefaultImageOpenApp, javax.swing.GroupLayout.DEFAULT_SIZE, 581, Short.MAX_VALUE)
                            .addGroup(panelImageOpenAppsLayout.createSequentialGroup()
                                .addComponent(labelDefaultImageOpenAppPrompt)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(buttonDefaultImageOpenApp))))
                    .addGroup(panelImageOpenAppsLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(scrollPaneListOtherImageOpenApps, javax.swing.GroupLayout.DEFAULT_SIZE, 581, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelImageOpenAppsLayout.createSequentialGroup()
                        .addContainerGap(149, Short.MAX_VALUE)
                        .addComponent(buttonRemoveOtherImageOpenApp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonAddOtherImageOpenApp)))
                .addContainerGap())
        );
        panelImageOpenAppsLayout.setVerticalGroup(
            panelImageOpenAppsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelImageOpenAppsLayout.createSequentialGroup()
                .addGroup(panelImageOpenAppsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelDefaultImageOpenAppPrompt)
                    .addComponent(buttonDefaultImageOpenApp))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelDefaultImageOpenApp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelInfoOtherOpenImageApps)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPaneListOtherImageOpenApps, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelImageOpenAppsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonAddOtherImageOpenApp)
                    .addComponent(buttonRemoveOtherImageOpenApp))
                .addGap(11, 11, 11))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 633, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panelImageOpenApps, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 323, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panelImageOpenApps, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
    }// </editor-fold>//GEN-END:initComponents

private void buttonDefaultImageOpenAppActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDefaultImageOpenAppActionPerformed
    setDefaultOpenApp();
}//GEN-LAST:event_buttonDefaultImageOpenAppActionPerformed

private void listOtherImageOpenAppsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listOtherImageOpenAppsValueChanged
    setEnabled();
}//GEN-LAST:event_listOtherImageOpenAppsValueChanged

private void buttonRemoveOtherImageOpenAppActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRemoveOtherImageOpenAppActionPerformed
    removeOtherOpenImageApp();
}//GEN-LAST:event_buttonRemoveOtherImageOpenAppActionPerformed

private void buttonAddOtherImageOpenAppActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddOtherImageOpenAppActionPerformed
    addOtherOpenImageApp();
}//GEN-LAST:event_buttonAddOtherImageOpenAppActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAddOtherImageOpenApp;
    private javax.swing.JButton buttonDefaultImageOpenApp;
    private javax.swing.JButton buttonRemoveOtherImageOpenApp;
    private javax.swing.JLabel labelDefaultImageOpenApp;
    private javax.swing.JLabel labelDefaultImageOpenAppPrompt;
    private javax.swing.JLabel labelInfoOtherOpenImageApps;
    private javax.swing.JList listOtherImageOpenApps;
    private javax.swing.JPanel panelImageOpenApps;
    private javax.swing.JScrollPane scrollPaneListOtherImageOpenApps;
    // End of variables declaration//GEN-END:variables
}
