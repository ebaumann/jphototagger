/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.view.dialogs;

import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.event.ProgressEvent;
import de.elmar_baumann.jpt.event.listener.ProgressListener;
import de.elmar_baumann.jpt.io.ImageFilteredDirectory;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.helper.ConvertIptcToXmp;
import de.elmar_baumann.lib.dialog.Dialog;
import de.elmar_baumann.lib.dialog.DirectoryChooser;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.util.SettingsHints;
import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class IptcToXmpDialog extends Dialog
        implements ProgressListener {

    private static final String KEY_DIRECTORY_NAME =
            "de.elmar_baumann.jpt.view.dialogs.IptcToXmpDialog.LastDirectory"; //
    private File directory = new File(""); //
    private boolean stop = true;
    private List<File> files;

    public IptcToXmpDialog() {
        super((java.awt.Frame) null, false);
        initComponents();
        postInitComponents();
    }

    /**
     * Setting files to process rather than letting the user choose a directory.
     * When set, {@link #setVisible(boolean)} starts processing the images.
     *
     * @param files image files to extract IPTC and write them as or into
     *              XMP sidecar files
     */
    public synchronized void setFiles(List<File> files) {
        this.files = new ArrayList<File>(files);
    }

    private void checkClose() {
        if (stop) {
            setVisible(false);
        } else {
            errorMessageWaitBeforeClose();
        }
    }

    private void chooseDirectory() {
        DirectoryChooser dialog = new DirectoryChooser(null, directory,
                UserSettings.INSTANCE.getDefaultDirectoryChooserOptions());
        dialog.setVisible(true);
        if (dialog.accepted()) {
            directory = dialog.getSelectedDirectories().get(0);
            labelDirectoryName.setText(directory.getAbsolutePath());
            setIconToDirectoryLabel();
            progressBar.setValue(0);
            buttonStart.setEnabled(true);
        }
    }

    private void errorMessageWaitBeforeClose() {
        MessageDisplayer.error(this, "IptcToXmpDialog.Error.CancelBeforeClose"); //
    }

    private void postInitComponents() {
        setIconImages(AppLookAndFeel.getAppIcons());
        setHelpContentsUrl(Bundle.getString("Help.Url.Contents")); //
        registerKeyStrokes();
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

    private void readProperties() {
        UserSettings.INSTANCE.getSettings().getComponent(this,
                new SettingsHints(EnumSet.of(
                SettingsHints.Option.SET_TABBED_PANE_CONTENT)));
        directory = new File(UserSettings.INSTANCE.getSettings().getString(
                KEY_DIRECTORY_NAME));
        setIconToDirectoryLabel();
        UserSettings.INSTANCE.getSettings().getSizeAndLocation(this);
    }

    private void writeProperties() {
        UserSettings.INSTANCE.getSettings().setSizeAndLocation(this);
        UserSettings.INSTANCE.getSettings().setComponent(this,
                new SettingsHints(EnumSet.of(
                SettingsHints.Option.SET_TABBED_PANE_CONTENT)));
        UserSettings.INSTANCE.getSettings().setString(
                directory.getAbsolutePath(), KEY_DIRECTORY_NAME);
        UserSettings.INSTANCE.writeToFile();
    }

    private void setIconToDirectoryLabel() {
        if (directory != null && directory.isDirectory()) {
            labelDirectoryName.setIcon(
                    FileSystemView.getFileSystemView().getSystemIcon(directory));
        }
    }

    private void init() {
        boolean directoryExists = directory.exists() && directory.isDirectory();
        buttonStart.setEnabled(directoryExists);
        if (directoryExists) {
            labelDirectoryName.setText(directory.getAbsolutePath());
            setIconToDirectoryLabel();
        }
        buttonStop.setEnabled(false);
    }

    private void start() {
        stop = false;
        setEnabledButtons();
        ConvertIptcToXmp converter = new ConvertIptcToXmp(FileUtil.
                getAsFilenames(getFiles()));
        converter.addProgressListener(this);
        Thread thread = new Thread(converter);
        thread.setName("Writing IPTC to XMP sidecar files @ " + getClass().getSimpleName());
        thread.start();
        buttonStop.setEnabled(true);
    }

    private void stop() {
        stop = true;
        setVisible(false);
    }

    private List<File> getFiles() {
        if (files == null) {
            List<File> directories = new ArrayList<File>();
            directories.add(directory);
            if (checkBoxSubdirectories.isSelected()) {
                directories.addAll(
                        FileUtil.getSubdirectoriesRecursive(directory,
                        UserSettings.INSTANCE.getDefaultDirectoryFilterOptions()));
            }
            return ImageFilteredDirectory.getImageFilesOfDirectories(directories);
        } else {
            return files;
        }
    }

    private void setEnabledButtons() {
        buttonStop.setEnabled(!stop);
        buttonStart.setEnabled(stop);
        buttonChooseDirectory.setEnabled(stop);
    }

    private void checkStopEvent(ProgressEvent evt) {
        if (stop) {
            evt.stop();
        }
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        progressBar.setMinimum(evt.getMinimum());
        progressBar.setMaximum(evt.getMaximum());
        progressBar.setValue(evt.getValue());
        checkStopEvent(evt);
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        progressBar.setValue(evt.getValue());
        checkStopEvent(evt);
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        progressBar.setValue(evt.getValue());
        stop = true;
        setEnabledButtons();
        setVisible(false);
    }

    @Override
    protected void help() {
        help(Bundle.getString("Help.Url.IptcToXmpDialog")); //
    }

    @Override
    protected void escape() {
        checkClose();
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
        buttonChooseDirectory = new javax.swing.JButton();
        labelDirectoryName = new javax.swing.JLabel();
        checkBoxSubdirectories = new javax.swing.JCheckBox();
        progressBar = new javax.swing.JProgressBar();
        buttonStop = new javax.swing.JButton();
        buttonStart = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(Bundle.getString("IptcToXmpDialog.title")); //
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        labelInfo.setText(Bundle.getString("IptcToXmpDialog.labelInfo.text")); //

        labelDirectoryPrompt.setText(Bundle.getString("IptcToXmpDialog.labelDirectoryPrompt.text")); //

        buttonChooseDirectory.setMnemonic('a');
        buttonChooseDirectory.setText(Bundle.getString("IptcToXmpDialog.buttonChooseDirectory.text")); //
        buttonChooseDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseDirectoryActionPerformed(evt);
            }
        });

        labelDirectoryName.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        checkBoxSubdirectories.setMnemonic('u');
        checkBoxSubdirectories.setText(Bundle.getString("IptcToXmpDialog.checkBoxSubdirectories.text")); //

        buttonStop.setMnemonic('o');
        buttonStop.setText(Bundle.getString("IptcToXmpDialog.buttonStop.text")); //
        buttonStop.setEnabled(false);
        buttonStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStopActionPerformed(evt);
            }
        });

        buttonStart.setMnemonic('s');
        buttonStart.setText(Bundle.getString("IptcToXmpDialog.buttonStart.text")); //
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
                    .addComponent(checkBoxSubdirectories)
                    .addComponent(labelDirectoryName, javax.swing.GroupLayout.DEFAULT_SIZE, 631, Short.MAX_VALUE)
                    .addComponent(labelInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 631, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(labelDirectoryPrompt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 458, Short.MAX_VALUE)
                        .addComponent(buttonChooseDirectory))
                    .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 631, Short.MAX_VALUE)
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(buttonChooseDirectory)
                    .addComponent(labelDirectoryPrompt))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelDirectoryName, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(checkBoxSubdirectories)
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
