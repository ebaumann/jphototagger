package de.elmar_baumann.imv.view.dialogs;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.ProgressListener;
import de.elmar_baumann.imv.io.ImageFilteredDirectory;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.tasks.IptcToXmp;
import de.elmar_baumann.lib.dialog.DirectoryChooser;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.persistence.PersistentAppSizes;
import de.elmar_baumann.lib.persistence.PersistentSettings;
import de.elmar_baumann.lib.persistence.PersistentSettingsHints;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class IptcToXmpDialog extends javax.swing.JDialog
    implements ProgressListener {

    private String directoryName = ""; // NOI18N
    private static final String keyDirectoryName = "de.elmar_baumann.imv.view.dialogs.IptcToXmpDialog.LastDirectory"; // NOI18N
    private boolean stop = true;

    /** Creates new form IptcToXmpDialog */
    public IptcToXmpDialog() {
        super((java.awt.Frame) null, false);
        initComponents();
        postInitComponents();
    }

    private void checkClose() {
        if (stop) {
            setVisible(false);
        } else {
            messageWait();
        }
    }

    private void chooseDirectory() {
        DirectoryChooser dialog = new DirectoryChooser(null, UserSettings.getInstance().isAcceptHiddenDirectories());
        dialog.setStartDirectory(new File(directoryName));
        dialog.setMultiSelection(false);
        dialog.setVisible(true);
        if (dialog.accepted()) {
            directoryName = dialog.getSelectedDirectories().get(0).getAbsolutePath();
            labelDirectoryName.setText(directoryName);
            progressBar.setValue(0);
            buttonStart.setEnabled(true);
        }
    }

    private void messageWait() {
        JOptionPane.showMessageDialog(
            null,
            Bundle.getString("IptcToXmpDialog.ErrorMessage.CancelBeforeClose"),
            Bundle.getString("IptcToXmpDialog.ErrorMessage.CancelBeforeClose.Title"),
            JOptionPane.INFORMATION_MESSAGE,
            AppSettings.getMediumAppIcon());
    }

    private void postInitComponents() {
        setIconImages(AppSettings.getAppIcons());
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            PersistentAppSizes.getSizeAndLocation(this);
            PersistentSettings.getInstance().getComponent(this, new PersistentSettingsHints());
            directoryName = PersistentSettings.getInstance().getString(keyDirectoryName);
            init();
        } else {
            PersistentAppSizes.setSizeAndLocation(this);
            PersistentSettings.getInstance().setComponent(this, new PersistentSettingsHints());
            PersistentSettings.getInstance().setString(directoryName, keyDirectoryName);
            dispose();
        }
        super.setVisible(visible);
    }

    private void init() {
        boolean directoryExists = !directoryName.isEmpty() &&
            FileUtil.existsDirectory(directoryName);
        buttonStart.setEnabled(directoryExists);
        if (directoryExists) {
            labelDirectoryName.setText(directoryName);
        }
        buttonStop.setEnabled(false);
    }

    private void start() {
        stop = false;
        setEnabledButtons();
        IptcToXmp converter = new IptcToXmp(getFilenames());
        converter.addProgressListener(this);
        Thread thread = new Thread(converter);
        thread.setPriority(UserSettings.getInstance().getThreadPriority());
        thread.start();
        buttonStop.setEnabled(true);
    }

    private void stop() {
        stop = true;
    }

    private List<String> getFilenames() {
        List<String> directories = new ArrayList<String>();
        directories.add(directoryName);
        if (checkBoxSubdirectories.isSelected()) {
            directories.addAll(FileUtil.getAllSubDirectoryNames(directoryName,
                UserSettings.getInstance().isAcceptHiddenDirectories()));
        }
        return ImageFilteredDirectory.getImageFilenamesOfDirectories(directories);
    }

    private void setEnabledButtons() {
        buttonStop.setEnabled(!stop);
        buttonStart.setEnabled(stop);
        buttonChooseDirectory.setEnabled(stop);
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        progressBar.setMinimum(evt.getMinimum());
        progressBar.setMaximum(evt.getMaximum());
        progressBar.setValue(evt.getValue());
        evt.setStop(stop);
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        progressBar.setValue(evt.getValue());
        evt.setStop(stop);
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        progressBar.setValue(evt.getValue());
        stop = true;
        setEnabledButtons();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelInfo = new javax.swing.JLabel();
        labelDirectoryPrompt = new javax.swing.JLabel();
        labelDirectoryName = new javax.swing.JLabel();
        checkBoxSubdirectories = new javax.swing.JCheckBox();
        buttonChooseDirectory = new javax.swing.JButton();
        progressBar = new javax.swing.JProgressBar();
        buttonStop = new javax.swing.JButton();
        buttonStart = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(Bundle.getString("IptcToXmpDialog.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        labelInfo.setFont(new java.awt.Font("Dialog", 0, 12));
        labelInfo.setText(Bundle.getString("IptcToXmpDialog.labelInfo.text")); // NOI18N

        labelDirectoryPrompt.setText(Bundle.getString("IptcToXmpDialog.labelDirectoryPrompt.text")); // NOI18N

        labelDirectoryName.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        checkBoxSubdirectories.setFont(new java.awt.Font("Dialog", 0, 12));
        checkBoxSubdirectories.setMnemonic('u');
        checkBoxSubdirectories.setText(Bundle.getString("IptcToXmpDialog.checkBoxSubdirectories.text")); // NOI18N

        buttonChooseDirectory.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonChooseDirectory.setMnemonic('a');
        buttonChooseDirectory.setText(Bundle.getString("IptcToXmpDialog.buttonChooseDirectory.text")); // NOI18N
        buttonChooseDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseDirectoryActionPerformed(evt);
            }
        });

        buttonStop.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonStop.setMnemonic('o');
        buttonStop.setText(Bundle.getString("IptcToXmpDialog.buttonStop.text")); // NOI18N
        buttonStop.setEnabled(false);
        buttonStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStopActionPerformed(evt);
            }
        });

        buttonStart.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonStart.setMnemonic('s');
        buttonStart.setText(Bundle.getString("IptcToXmpDialog.buttonStart.text")); // NOI18N
        buttonStart.setEnabled(false);
        buttonStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStartActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(progressBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 613, Short.MAX_VALUE)
                    .addComponent(labelDirectoryName, javax.swing.GroupLayout.DEFAULT_SIZE, 613, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(labelInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(labelDirectoryPrompt))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(checkBoxSubdirectories)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 148, Short.MAX_VALUE)
                        .addComponent(buttonChooseDirectory))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(buttonStop)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonStart)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(labelInfo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelDirectoryPrompt)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelDirectoryName, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonChooseDirectory)
                    .addComponent(checkBoxSubdirectories))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonStart)
                    .addComponent(buttonStop))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    checkClose();
}//GEN-LAST:event_formWindowClosing

private void buttonStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStartActionPerformed
    start();
}//GEN-LAST:event_buttonStartActionPerformed

private void buttonChooseDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseDirectoryActionPerformed
    chooseDirectory();
}//GEN-LAST:event_buttonChooseDirectoryActionPerformed

private void buttonStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStopActionPerformed
    stop();
}//GEN-LAST:event_buttonStopActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                IptcToXmpDialog dialog = new IptcToXmpDialog();
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
    private javax.swing.JButton buttonChooseDirectory;
    private javax.swing.JButton buttonStart;
    private javax.swing.JButton buttonStop;
    private javax.swing.JCheckBox checkBoxSubdirectories;
    private javax.swing.JLabel labelDirectoryName;
    private javax.swing.JLabel labelDirectoryPrompt;
    private javax.swing.JLabel labelInfo;
    private javax.swing.JProgressBar progressBar;
    // End of variables declaration//GEN-END:variables
}
