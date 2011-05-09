package org.jphototagger.program.view.dialogs;

import org.jphototagger.lib.io.filefilter.DirectoryFilter.Option;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.event.listener.ProgressListener;
import org.jphototagger.program.event.ProgressEvent;
import org.jphototagger.program.helper.ConvertIptcToXmp;
import org.jphototagger.program.io.ImageFileFilterer;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;
import org.jphototagger.lib.componentutil.MnemonicUtil;
import org.jphototagger.lib.dialog.Dialog;
import org.jphototagger.lib.dialog.DirectoryChooser;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.util.Settings;

import java.awt.Container;
import java.awt.EventQueue;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.filechooser.FileSystemView;
import org.jphototagger.lib.io.CancelRequest;
import org.jphototagger.program.event.UserSettingsEvent;
import org.jphototagger.program.event.listener.UserSettingsListener;
import org.jphototagger.program.model.IptcCharsetComboBoxModel;
import org.jphototagger.program.view.panels.SelectRootFilesPanel;

/**
 *
 * @author Elmar Baumann
 */
public final class IptcToXmpDialog extends Dialog implements ProgressListener, UserSettingsListener {
    private static final String KEY_DIRECTORY_NAME = "org.jphototagger.program.view.dialogs.IptcToXmpDialog.LastDirectory";
    private static final String KEY_INCLUDE_SUBDIRS = "org.jphototagger.program.view.dialogs.IptcToXmpDialog.IncludeSubdirectories";
    private static final long serialVersionUID = 873528245237986989L;
    private final transient CancelChooseRequest cancelChooseRequest = new CancelChooseRequest();
    private File directory = new File("");
    private boolean cancel = true;
    private List<File> files;

    public IptcToXmpDialog() {
        super(GUI.getAppFrame(), false, UserSettings.INSTANCE.getSettings(), null);
        initComponents();
        setHelpPages();
        MnemonicUtil.setMnemonics((Container) this);
        UserSettings.INSTANCE.addUserSettingsListener(this);
    }

    private void setHelpPages() {
        setHelpContentsUrl(JptBundle.INSTANCE.getString("Help.Url.Contents"));
        setHelpPageUrl(JptBundle.INSTANCE.getString("Help.Url.IptcToXmpDialog"));
    }

    /**
     * Setting files to process rather than letting the user choose a directory.
     * When set, {@link #setVisible(boolean)} starts processing the images.
     *
     * @param files image files to extract IPTC and write them as or into
     *              XMP sidecar files
     */
    public synchronized void setFiles(List<File> files) {
        if (files == null) {
            throw new NullPointerException("files == null");
        }

        this.files = new ArrayList<File>(files);
    }

    private void checkClose() {
        if (cancel) {
            setVisible(false);
        } else {
            errorMessageWaitBeforeClose();
        }
    }

    private void chooseDirectory() {
        List<File> hideRootFiles = SelectRootFilesPanel.readPersistentRootFiles(UserSettings.KEY_HIDE_ROOT_FILES_FROM_DIRECTORIES_TAB);
        DirectoryChooser dlg = new DirectoryChooser(GUI.getAppFrame(), directory, hideRootFiles, UserSettings.INSTANCE.getDirChooserOptionShowHiddenDirs());

        dlg.setSettings(UserSettings.INSTANCE.getSettings(), "IptcToXmpDialog.DirChooser");
        dlg.setVisible(true);

        if (dlg.isAccepted()) {
            directory = dlg.getSelectedDirectories().get(0);
            labelDirectoryName.setText(directory.getAbsolutePath());
            setIconToDirectoryLabel();
            progressBar.setValue(0);
            buttonStart.setEnabled(true);
        }
    }

    private void errorMessageWaitBeforeClose() {
        MessageDisplayer.error(this, "IptcToXmpDialog.Error.CancelBeforeClose");
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            readProperties();

            if (files == null) {
                init();
            } else {
                start();
            }
        } else {
            writeProperties();
            dispose();
        }

        super.setVisible(visible);
    }

    private void setIptcCharsetFromUserSettings() {
        comboBoxIptcCharset.getModel().setSelectedItem(UserSettings.INSTANCE.getIptcCharset());
    }

    private void readProperties() {
        Settings settings = UserSettings.INSTANCE.getSettings();

        settings.applySettings(this, UserSettings.SET_TABBED_PANE_SETTINGS);
        checkBoxIncludeSubdirectories.setSelected(settings.getBoolean(KEY_INCLUDE_SUBDIRS));
        setIptcCharsetFromUserSettings();
        directory = new File(UserSettings.INSTANCE.getSettings().getString(KEY_DIRECTORY_NAME));
        setIconToDirectoryLabel();
    }

    private void writeProperties() {
        Settings settings = UserSettings.INSTANCE.getSettings();

        settings.set(this, UserSettings.SET_TABBED_PANE_SETTINGS);
        settings.set(directory.getAbsolutePath(), KEY_DIRECTORY_NAME);
        settings.set(checkBoxIncludeSubdirectories.isSelected(), KEY_INCLUDE_SUBDIRS);
        UserSettings.INSTANCE.writeToFile();
    }

    private void setIconToDirectoryLabel() {
        if ((directory != null) && directory.isDirectory()) {
            labelDirectoryName.setIcon(FileSystemView.getFileSystemView().getSystemIcon(directory));
        }
    }

    private void init() {
        boolean directoryExists = directory.exists() && directory.isDirectory();

        buttonStart.setEnabled(directoryExists);

        if (directoryExists) {
            labelDirectoryName.setText(directory.getAbsolutePath());
            setIconToDirectoryLabel();
        }
    }

    private void start() {
        new ConvertThread(this).start();
    }

    private void setIptcCharset() {
        UserSettings.INSTANCE.setIptcCharset(comboBoxIptcCharset.getSelectedItem().toString());
    }

    @Override
    public void applySettings(UserSettingsEvent evt) {
        UserSettingsEvent.Type eventType = evt.getType();
        
        if (eventType.equals(UserSettingsEvent.Type.IPTC_CHARSET)) {
            setIptcCharsetFromUserSettings();
        }
    }

    private class ConvertThread extends Thread {

        private final ProgressListener progressListener;

        ConvertThread(ProgressListener progressListener) {
            super("JPhotoTagger: Writing IPTC to XMP sidecar files");
            this.progressListener = progressListener;
        }

        @Override
        public void run() {
            cancel = false;
            setEnabledButtons();

            ConvertIptcToXmp convertIptcToXmp = new ConvertIptcToXmp(getFiles());

            convertIptcToXmp.addProgressListener(progressListener);

            Thread thread = new Thread(convertIptcToXmp, "JPhotoTagger: Writing IPTC to XMP sidecar files");

            thread.start();
        }

    }

    private void cancel() {
        cancel = true;
        cancelChooseRequest.cancel = true;
        setVisible(false);
    }

    private List<File> getFiles() {
        if (files == null) {
            List<File> directories = new ArrayList<File>();

            directories.add(directory);

            if (checkBoxIncludeSubdirectories.isSelected()) {
                Option showHiddenFiles = UserSettings.INSTANCE.getDirFilterOptionShowHiddenFiles();
                directories.addAll(FileUtil.getSubDirectoriesRecursive(directory, cancelChooseRequest, showHiddenFiles));
            }

            return ImageFileFilterer.getImageFilesOfDirectories(directories);
        } else {
            return Collections.unmodifiableList(files);
        }
    }

    private void setEnabledButtons() {
        buttonStart.setEnabled(cancel);
        buttonCancel.setEnabled(!cancel);
        cancelChooseRequest.cancel = cancel;
        buttonChooseDirectory.setEnabled(cancel);
    }

    private void checkCancel(ProgressEvent evt) {
        if (cancel) {
            evt.cancel();
        }
    }

    @Override
    public void progressStarted(final ProgressEvent evt) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                progressBar.setMinimum(evt.getMinimum());
                progressBar.setMaximum(evt.getMaximum());
                progressBar.setValue(evt.getValue());
                checkCancel(evt);
            }
        });
    }

    @Override
    public void progressPerformed(final ProgressEvent evt) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                progressBar.setValue(evt.getValue());
                checkCancel(evt);
            }
        });
    }

    @Override
    public void progressEnded(final ProgressEvent evt) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                progressBar.setValue(evt.getValue());
                cancel = true;
                setEnabledButtons();
                setVisible(false);
            }
        });
    }

    @Override
    protected void escape() {
        checkClose();
    }

    private static class CancelChooseRequest implements CancelRequest {

        boolean cancel;

        @Override
        public boolean isCancel() {
            return cancel;
        }
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

        labelInfo = new javax.swing.JLabel();
        labelDirectoryPrompt = new javax.swing.JLabel();
        buttonChooseDirectory = new javax.swing.JButton();
        labelDirectoryName = new javax.swing.JLabel();
        checkBoxIncludeSubdirectories = new javax.swing.JCheckBox();
        labelIptcCharset = new javax.swing.JLabel();
        comboBoxIptcCharset = new javax.swing.JComboBox();
        progressBar = new javax.swing.JProgressBar();
        buttonCancel = new javax.swing.JButton();
        buttonStart = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(JptBundle.INSTANCE.getString("IptcToXmpDialog.title")); // NOI18N
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        labelInfo.setText(JptBundle.INSTANCE.getString("IptcToXmpDialog.labelInfo.text")); // NOI18N
        labelInfo.setName("labelInfo"); // NOI18N

        labelDirectoryPrompt.setText(JptBundle.INSTANCE.getString("IptcToXmpDialog.labelDirectoryPrompt.text")); // NOI18N
        labelDirectoryPrompt.setName("labelDirectoryPrompt"); // NOI18N

        buttonChooseDirectory.setText(JptBundle.INSTANCE.getString("IptcToXmpDialog.buttonChooseDirectory.text")); // NOI18N
        buttonChooseDirectory.setName("buttonChooseDirectory"); // NOI18N
        buttonChooseDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseDirectoryActionPerformed(evt);
            }
        });

        labelDirectoryName.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelDirectoryName.setName("labelDirectoryName"); // NOI18N

        checkBoxIncludeSubdirectories.setText(JptBundle.INSTANCE.getString("IptcToXmpDialog.checkBoxIncludeSubdirectories.text")); // NOI18N
        checkBoxIncludeSubdirectories.setName("checkBoxIncludeSubdirectories"); // NOI18N

        labelIptcCharset.setLabelFor(comboBoxIptcCharset);
        labelIptcCharset.setText(JptBundle.INSTANCE.getString("IptcToXmpDialog.labelIptcCharset.text")); // NOI18N
        labelIptcCharset.setName("labelIptcCharset"); // NOI18N

        comboBoxIptcCharset.setModel(new IptcCharsetComboBoxModel());
        comboBoxIptcCharset.setName("comboBoxIptcCharset"); // NOI18N
        comboBoxIptcCharset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxIptcCharsetActionPerformed(evt);
            }
        });

        progressBar.setName("progressBar"); // NOI18N

        buttonCancel.setText(JptBundle.INSTANCE.getString("IptcToXmpDialog.buttonCancel.text")); // NOI18N
        buttonCancel.setEnabled(false);
        buttonCancel.setName("buttonCancel"); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        buttonStart.setText(JptBundle.INSTANCE.getString("IptcToXmpDialog.buttonStart.text")); // NOI18N
        buttonStart.setEnabled(false);
        buttonStart.setName("buttonStart"); // NOI18N
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
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 721, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(labelDirectoryPrompt)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 544, Short.MAX_VALUE)
                                .addComponent(buttonChooseDirectory))
                            .addComponent(labelDirectoryName, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 721, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(checkBoxIncludeSubdirectories)
                        .addContainerGap(558, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelIptcCharset)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboBoxIptcCharset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(buttonCancel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buttonStart))
                            .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 715, Short.MAX_VALUE))
                        .addGap(16, 16, 16))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(labelInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelDirectoryPrompt)
                    .addComponent(buttonChooseDirectory))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelDirectoryName, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxIncludeSubdirectories)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelIptcCharset, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboBoxIptcCharset, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonStart)
                    .addComponent(buttonCancel))
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

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        cancel();
    }//GEN-LAST:event_buttonCancelActionPerformed

    private void comboBoxIptcCharsetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxIptcCharsetActionPerformed
        setIptcCharset();
}//GEN-LAST:event_comboBoxIptcCharsetActionPerformed

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
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonChooseDirectory;
    private javax.swing.JButton buttonStart;
    private javax.swing.JCheckBox checkBoxIncludeSubdirectories;
    private javax.swing.JComboBox comboBoxIptcCharset;
    private javax.swing.JLabel labelDirectoryName;
    private javax.swing.JLabel labelDirectoryPrompt;
    private javax.swing.JLabel labelInfo;
    private javax.swing.JLabel labelIptcCharset;
    private javax.swing.JProgressBar progressBar;
    // End of variables declaration//GEN-END:variables
}
