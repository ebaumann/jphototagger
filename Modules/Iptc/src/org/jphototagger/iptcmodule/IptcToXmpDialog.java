package org.jphototagger.iptcmodule;

import java.awt.Container;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileSystemView;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.concurrent.CancelRequest;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesChangedEvent;
import org.jphototagger.api.preferences.PreferencesHints;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.domain.DomainPreferencesKeys;
import org.jphototagger.domain.filefilter.FileFilterUtil;
import org.jphototagger.iptc.IptcPreferencesKeys;
import org.jphototagger.lib.api.LookAndFeelChangedEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.io.filefilter.DirectoryFilter;
import org.jphototagger.lib.io.filefilter.DirectoryFilter.Option;
import org.jphototagger.lib.swing.DialogExt;
import org.jphototagger.lib.swing.DirectoryChooser;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.SelectRootFilesPanel;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class IptcToXmpDialog extends DialogExt implements ProgressListener {

    private static final String KEY_DIRECTORY_NAME = "org.jphototagger.program.view.dialogs.IptcToXmpDialog.LastDirectory";
    private static final String KEY_INCLUDE_SUBDIRS = "org.jphototagger.program.view.dialogs.IptcToXmpDialog.IncludeSubdirectories";
    private static final long serialVersionUID = 1L;
    private final transient CancelChooseRequest cancelChooseRequest = new CancelChooseRequest();
    private File directory = new File("");
    private boolean cancel = true;
    private List<File> files;

    public IptcToXmpDialog() {
        super(ComponentUtil.findFrameWithIcon(), false);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setHelpPage();
        MnemonicUtil.setMnemonics((Container) this);
        AnnotationProcessor.process(this);
    }

    private void setHelpPage() {
        setHelpPageUrl(Bundle.getString(IptcToXmpDialog.class, "IptcToXmpDialog.HelpPage"));
    }

    /**
     * Setting files to process rather than letting the user choose a directory.
     * When setTree, {@code #setVisible(boolean)} starts processing the images.
     *
     * @param files image files to extract IPTC and write them as or into
     *              XMP sidecar files
     */
    public synchronized void setFiles(List<File> files) {
        if (files == null) {
            throw new NullPointerException("files == null");
        }
        this.files = new ArrayList<>(files);
    }

    private void checkClose() {
        if (cancel) {
            setVisible(false);
        } else {
            errorMessageWaitBeforeClose();
        }
    }

    private void chooseDirectory() {
        List<File> hideRootFiles = SelectRootFilesPanel.readPersistentRootFiles(DomainPreferencesKeys.KEY_UI_DIRECTORIES_TAB_HIDE_ROOT_FILES);
        DirectoryChooser.Option options = getDirChooserOptionShowHiddenDirs();
        DirectoryChooser dlg = new DirectoryChooser(ComponentUtil.findFrameWithIcon(), directory, hideRootFiles, options);
        dlg.setPreferencesKey("IptcToXmpDialog.DirChooser");
        dlg.setVisible(true);
        toFront();
        if (dlg.isAccepted()) {
            directory = dlg.getSelectedDirectories().get(0);
            labelDirectoryName.setText(directory.getAbsolutePath());
            setIconToDirectoryLabel();
            progressBar.setValue(0);
            buttonStart.setEnabled(true);
        }
    }
    private DirectoryChooser.Option getDirChooserOptionShowHiddenDirs() {
        return isAcceptHiddenDirectories()
                ? DirectoryChooser.Option.DISPLAY_HIDDEN_DIRECTORIES
                : DirectoryChooser.Option.NO_OPTION;
    }

    private boolean isAcceptHiddenDirectories() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        return prefs.containsKey(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES)
                ? prefs.getBoolean(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES)
                : false;
    }


    private void errorMessageWaitBeforeClose() {
        String message = Bundle.getString(IptcToXmpDialog.class, "IptcToXmpDialog.Error.CancelBeforeClose");
        MessageDisplayer.error(this, message);
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
        comboBoxIptcCharset.getModel().setSelectedItem(getIptcCharset());
    }

    private String getIptcCharset() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        String charset = prefs.getString(IptcPreferencesKeys.KEY_IPTC_CHARSET);
        return charset.isEmpty()
                ? "ISO-8859-1"
                : charset;
    }

    private void readProperties() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        prefs.applyComponentSettings(this, new PreferencesHints(PreferencesHints.Option.SET_TABBED_PANE_CONTENT));
        checkBoxIncludeSubdirectories.setSelected(prefs.getBoolean(KEY_INCLUDE_SUBDIRS));
        setIptcCharsetFromUserSettings();
        directory = new File(prefs.getString(KEY_DIRECTORY_NAME));
        setIconToDirectoryLabel();
    }

    private void writeProperties() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        prefs.setComponent(this, new PreferencesHints(PreferencesHints.Option.SET_TABBED_PANE_CONTENT));
        prefs.setString(KEY_DIRECTORY_NAME, directory.getAbsolutePath());
        prefs.setBoolean(KEY_INCLUDE_SUBDIRS, checkBoxIncludeSubdirectories.isSelected());
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
        setIptcCharset(comboBoxIptcCharset.getSelectedItem().toString());
    }

    private void setIptcCharset(String charset) {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        prefs.setString(IptcPreferencesKeys.KEY_IPTC_CHARSET, charset);
    }

    @EventSubscriber(eventClass = PreferencesChangedEvent.class)
    public void applySettings(PreferencesChangedEvent evt) {
        if (IptcPreferencesKeys.KEY_IPTC_CHARSET.equals(evt.getKey())) {
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
            List<File> directories = new ArrayList<>();
            directories.add(directory);
            if (checkBoxIncludeSubdirectories.isSelected()) {
                Option showHiddenFiles = getDirFilterOptionShowHiddenFiles();
                directories.addAll(FileUtil.getSubDirectoriesRecursive(directory, cancelChooseRequest, showHiddenFiles));
            }
            return FileFilterUtil.getImageFilesOfDirectories(directories);
        } else {
            return Collections.unmodifiableList(files);
        }
    }

    private DirectoryFilter.Option getDirFilterOptionShowHiddenFiles() {
        return isAcceptHiddenDirectories()
                ? DirectoryFilter.Option.ACCEPT_HIDDEN_FILES
                : DirectoryFilter.Option.NO_OPTION;
    }

    private void setEnabledButtons() {
        buttonStart.setEnabled(cancel);
        buttonCancel.setEnabled(!cancel);
        cancelChooseRequest.cancel = cancel;
        buttonChooseDirectory.setEnabled(cancel);
    }

    private void checkCancel(ProgressEvent evt) {
        if (cancel) {
            evt.setCancel(true);
        }
    }

    @Override
    public void progressStarted(final ProgressEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

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
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                progressBar.setValue(evt.getValue());
                checkCancel(evt);
            }
        });
    }

    @Override
    public void progressEnded(final ProgressEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

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

    @EventSubscriber(eventClass = LookAndFeelChangedEvent.class)
    public void lookAndFeelChanged(LookAndFeelChangedEvent evt) {
        SwingUtilities.updateComponentTreeUI(this);
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

        labelInfo = UiFactory.label();
        labelDirectoryPrompt = UiFactory.label();
        buttonChooseDirectory = UiFactory.button();
        labelDirectoryName = UiFactory.label();
        checkBoxIncludeSubdirectories = UiFactory.checkBox();
        labelIptcCharset = UiFactory.label();
        comboBoxIptcCharset = UiFactory.comboBox();
        progressBar = UiFactory.progressBar();
        panelButtons = UiFactory.panel();
        buttonCancel = UiFactory.button();
        buttonStart = UiFactory.button();
        panelVfill = UiFactory.panel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(Bundle.getString(getClass(), "IptcToXmpDialog.title")); // NOI18N
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        labelInfo.setText(Bundle.getString(getClass(), "IptcToXmpDialog.labelInfo.text")); // NOI18N
        labelInfo.setName("labelInfo"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(7, 7, 0, 7);
        getContentPane().add(labelInfo, gridBagConstraints);

        labelDirectoryPrompt.setText(Bundle.getString(getClass(), "IptcToXmpDialog.labelDirectoryPrompt.text")); // NOI18N
        labelDirectoryPrompt.setName("labelDirectoryPrompt"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(5, 7, 0, 0);
        getContentPane().add(labelDirectoryPrompt, gridBagConstraints);

        buttonChooseDirectory.setText(Bundle.getString(getClass(), "IptcToXmpDialog.buttonChooseDirectory.text")); // NOI18N
        buttonChooseDirectory.setName("buttonChooseDirectory"); // NOI18N
        buttonChooseDirectory.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseDirectoryActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = UiFactory.insets(5, 7, 0, 7);
        getContentPane().add(buttonChooseDirectory, gridBagConstraints);

        labelDirectoryName.setText(" "); // NOI18N
        labelDirectoryName.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelDirectoryName.setName("labelDirectoryName"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 7, 0, 7);
        getContentPane().add(labelDirectoryName, gridBagConstraints);

        checkBoxIncludeSubdirectories.setText(Bundle.getString(getClass(), "IptcToXmpDialog.checkBoxIncludeSubdirectories.text")); // NOI18N
        checkBoxIncludeSubdirectories.setName("checkBoxIncludeSubdirectories"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(5, 7, 0, 0);
        getContentPane().add(checkBoxIncludeSubdirectories, gridBagConstraints);

        labelIptcCharset.setLabelFor(comboBoxIptcCharset);
        labelIptcCharset.setText(Bundle.getString(getClass(), "IptcToXmpDialog.labelIptcCharset.text")); // NOI18N
        labelIptcCharset.setName("labelIptcCharset"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(5, 7, 0, 0);
        getContentPane().add(labelIptcCharset, gridBagConstraints);

        comboBoxIptcCharset.setModel(new IptcCharsetComboBoxModel());
        comboBoxIptcCharset.setName("comboBoxIptcCharset"); // NOI18N
        comboBoxIptcCharset.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxIptcCharsetActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 0);
        getContentPane().add(comboBoxIptcCharset, gridBagConstraints);

        progressBar.setName("progressBar"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 7, 0, 7);
        getContentPane().add(progressBar, gridBagConstraints);

        panelButtons.setName("panelButtons"); // NOI18N
        panelButtons.setLayout(new java.awt.GridBagLayout());

        buttonCancel.setText(Bundle.getString(getClass(), "IptcToXmpDialog.buttonCancel.text")); // NOI18N
        buttonCancel.setEnabled(false);
        buttonCancel.setName("buttonCancel"); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelButtons.add(buttonCancel, gridBagConstraints);

        buttonStart.setText(Bundle.getString(getClass(), "IptcToXmpDialog.buttonStart.text")); // NOI18N
        buttonStart.setEnabled(false);
        buttonStart.setName("buttonStart"); // NOI18N
        buttonStart.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStartActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelButtons.add(buttonStart, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = UiFactory.insets(10, 7, 7, 7);
        getContentPane().add(panelButtons, gridBagConstraints);

        panelVfill.setName("panelVfill"); // NOI18N
        panelVfill.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(panelVfill, gridBagConstraints);

        pack();
    }//GEN-END:initComponents

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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonChooseDirectory;
    private javax.swing.JButton buttonStart;
    private javax.swing.JCheckBox checkBoxIncludeSubdirectories;
    private javax.swing.JComboBox<Object> comboBoxIptcCharset;
    private javax.swing.JLabel labelDirectoryName;
    private javax.swing.JLabel labelDirectoryPrompt;
    private javax.swing.JLabel labelInfo;
    private javax.swing.JLabel labelIptcCharset;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelVfill;
    private javax.swing.JProgressBar progressBar;
    // End of variables declaration//GEN-END:variables
}
