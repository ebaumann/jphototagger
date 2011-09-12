package org.jphototagger.program.view.panels;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.jphototagger.api.event.ProgressEvent;
import org.jphototagger.api.event.ProgressListener;
import org.jphototagger.domain.repository.KeywordsRepository;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.componentutil.ListUtil;
import org.jphototagger.lib.componentutil.MnemonicUtil;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.helper.HelperThread;
import org.jphototagger.program.helper.InsertKeywords;
import org.jphototagger.program.helper.RefreshExifInDbOfKnownFiles;
import org.jphototagger.program.helper.RefreshXmpInDbOfKnownFiles;
import org.jphototagger.program.helper.SetExifToXmp;
import org.jphototagger.program.helper.UpdateAllThumbnails;
import org.jphototagger.program.model.ListModelKeywords;
import org.jphototagger.program.model.TreeModelKeywords;
import org.jphototagger.program.view.dialogs.RenameFilenamesInDbDialog;
import org.openide.util.Lookup;

/**
 *
 * @author Elmar Baumann
 */
public class DatabaseUpdatePanel extends JPanel implements ActionListener, ProgressListener {

    private static final long serialVersionUID = 3148751698141558616L;
    private static final String BUTTON_TEXT_CANCEL = Bundle.getString(DatabaseUpdatePanel.class, "DatabaseUpdatePanel.DisplayName.Cancel");
    private transient UpdateAllThumbnails thumbnailUpdater;
    private final AbstractButton[] buttons;
    private volatile boolean cancel;

    public DatabaseUpdatePanel() {
        initComponents();
        buttons = new AbstractButton[] {
            toggleButtonRefreshExif,
            toggleButtonRefreshXmp,
            buttonUpdateThumbnails,
            buttonRenameFiles,
            buttonCopyKeywordsToKeywordsTree,
            buttonDeleteKeywordsTree,
            toggleButtonExifDateToXmpDateCreated,
        };
        MnemonicUtil.setMnemonics((Container) this);
    }

    private synchronized void updateThumbnails() {
        setEnabledAllButtons(false);

        synchronized (this) {
            thumbnailUpdater = new UpdateAllThumbnails();
            thumbnailUpdater.addActionListener(this);
            Thread t = new Thread(thumbnailUpdater, "JPhotoTagger: Updating all thumbnails");
            t.start();
        }
    }

    private synchronized void updateExif() {
        startOrCancelHelperThread(toggleButtonRefreshExif, RefreshExifInDbOfKnownFiles.class);
    }

    private void updateXmp() {
        startOrCancelHelperThread(toggleButtonRefreshXmp, RefreshXmpInDbOfKnownFiles.class);
    }

    private void exifDateToXmpDateCreated() {
        startOrCancelHelperThread(toggleButtonExifDateToXmpDateCreated, SetExifToXmp.class);
    }

    private synchronized void startOrCancelHelperThread(JToggleButton button, Class<?> helperThreadClass) {
        if (button.isSelected()) {
            try {
                HelperThread helperThread = (HelperThread) helperThreadClass.newInstance();

                disableOtherButtons(button);
                helperThread.setProgressBar(progressBar);
                helperThread.addProgressListener(this);
                cancel = false;
                helperThread.start();
                button.setText(BUTTON_TEXT_CANCEL);
            } catch (Exception ex) {
                Logger.getLogger(DatabaseUpdatePanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            cancel = true;
            setEnabledAllButtons(true);
            setStartButtonTexts();
        }
    }

    private void setStartButtonTexts() {
        toggleButtonRefreshExif.setText(Bundle.getString(DatabaseUpdatePanel.class, "DatabaseUpdatePanel.toggleButtonRefreshExif.text"));
        toggleButtonRefreshXmp.setText(Bundle.getString(DatabaseUpdatePanel.class, "DatabaseUpdatePanel.toggleButtonRefreshXmp.text"));
        buttonUpdateThumbnails.setText(Bundle.getString(DatabaseUpdatePanel.class, "DatabaseUpdatePanel.buttonUpdateThumbnails.text"));
        buttonRenameFiles.setText(Bundle.getString(DatabaseUpdatePanel.class, "DatabaseUpdatePanel.buttonRenameFiles.text"));
        buttonCopyKeywordsToKeywordsTree.setText(Bundle.getString(DatabaseUpdatePanel.class, "DatabaseUpdatePanel.buttonCopyKeywordsToKeywordsTree.text"));
        toggleButtonExifDateToXmpDateCreated.setText(Bundle.getString(DatabaseUpdatePanel.class, "DatabaseUpdatePanel.toggleButtonExifDateToXmpDateCreated.text"));
        MnemonicUtil.setMnemonics((Container) this);
    }

    private synchronized void disableOtherButtons(JToggleButton buttonEnabled) {
        for (AbstractButton button : buttons) {
            if (button != buttonEnabled) {
                button.setEnabled(false);
            }
        }
    }

    private synchronized void setEnabledAllButtons(boolean enabled) {
        for (AbstractButton button : buttons) {
            button.setEnabled(enabled);
        }
    }

    private synchronized void deselectAllToggleButtons() {
        for (AbstractButton button : buttons) {
            if (button instanceof JToggleButton) {
                ((JToggleButton) button).setSelected(false);
            }
        }
    }

    private void renameFilesInDb() {
        RenameFilenamesInDbDialog dlg = new RenameFilenamesInDbDialog();

        setEnabledAllButtons(false);
        dlg.setVisible(true);
        setEnabledAllButtons(true);
    }

    private void copyKeywordsToKeywordsTree() {
        List<String> keywords = ListUtil.toStringList(ModelFactory.INSTANCE.getModel(ListModelKeywords.class));

        if (keywords.size() > 0) {
            setEnabledAllButtons(false);
            new InsertKeywords(keywords).run();    // run in this thread!
            String message = Bundle.getString(DatabaseUpdatePanel.class, "DatabaseUpdatePanel.Info.CopyKeywordsToTree");
            MessageDisplayer.information(this, message);
            setEnabledAllButtons(true);
        }
    }

    private void deleteAllKeywordsFromKeywordsTree() {
        String message = Bundle.getString(DatabaseUpdatePanel.class, "DatabaseUpdatePanel.Confirm.DeleteAllKeywordsFromKeywordsTree");

        if (MessageDisplayer.confirmYesNo(this, message)) {
            KeywordsRepository repo = Lookup.getDefault().lookup(KeywordsRepository.class);

            setEnabledAllButtons(false);
            int count = repo.deleteAllKeywords();

            if (count > 0) {
                 Collection<TreeModelKeywords> models =
                       ModelFactory.INSTANCE.getModels(TreeModelKeywords.class);

                 if (models != null) {
                     for (final TreeModelKeywords model : models) {
                        EventQueueUtil.invokeInDispatchThread(new Runnable() {

                            @Override
                            public void run() {
                                model.removeAllKeywords();
                            }
                        });
                     }
                 }

                 message = Bundle.getString(DatabaseUpdatePanel.class, "DatabaseUpdatePanel.Info.DeletedKeywords", count);
                 MessageDisplayer.information(this, message);
             }

            setEnabledAllButtons(true);
        }
    }

    private void checkCancel(ProgressEvent evt) {
        if (cancel) {
            evt.setCancel(cancel);
        }
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        checkCancel(evt);
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        checkCancel(evt);
    }

    @Override
    public void progressEnded(final ProgressEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                checkCancel(evt);
                setEnabledAllButtons(true);
                deselectAllToggleButtons();
                setStartButtonTexts();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        synchronized (this) {
            if (evt.getSource() == thumbnailUpdater) {
                thumbnailUpdater = null;
                buttonUpdateThumbnails.setEnabled(true);
                setEnabledAllButtons(true);
            }
        }
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

        panelContent = new javax.swing.JPanel();
        panelTasks = new javax.swing.JPanel();
        labelRefreshExif = new javax.swing.JLabel();
        toggleButtonRefreshExif = new javax.swing.JToggleButton();
        labelRefreshXmp = new javax.swing.JLabel();
        toggleButtonRefreshXmp = new javax.swing.JToggleButton();
        labelUpdateThumbnails = new javax.swing.JLabel();
        buttonUpdateThumbnails = new javax.swing.JButton();
        labelRenameFiles = new javax.swing.JLabel();
        buttonRenameFiles = new javax.swing.JButton();
        labelCopyKeywordsToKeywordsTree = new javax.swing.JLabel();
        buttonCopyKeywordsToKeywordsTree = new javax.swing.JButton();
        labelDeleteKeywordsTree = new javax.swing.JLabel();
        buttonDeleteKeywordsTree = new javax.swing.JButton();
        labelExifDateToXmpDateCreated = new javax.swing.JLabel();
        toggleButtonExifDateToXmpDateCreated = new javax.swing.JToggleButton();
        progressBar = new javax.swing.JProgressBar();

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        panelContent.setName("panelContent"); // NOI18N
        panelContent.setLayout(new java.awt.GridBagLayout());

        panelTasks.setName("panelTasks"); // NOI18N
        panelTasks.setLayout(new java.awt.GridBagLayout());

        labelRefreshExif.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_exif.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/view/panels/Bundle"); // NOI18N
        labelRefreshExif.setText(bundle.getString("DatabaseUpdatePanel.labelRefreshExif.text")); // NOI18N
        labelRefreshExif.setName("labelRefreshExif"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panelTasks.add(labelRefreshExif, gridBagConstraints);

        toggleButtonRefreshExif.setText(bundle.getString("DatabaseUpdatePanel.toggleButtonRefreshExif.text")); // NOI18N
        toggleButtonRefreshExif.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        toggleButtonRefreshExif.setName("toggleButtonRefreshExif"); // NOI18N
        toggleButtonRefreshExif.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleButtonRefreshExifActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panelTasks.add(toggleButtonRefreshExif, gridBagConstraints);

        labelRefreshXmp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_xmp.png"))); // NOI18N
        labelRefreshXmp.setText(bundle.getString("DatabaseUpdatePanel.labelRefreshXmp.text")); // NOI18N
        labelRefreshXmp.setName("labelRefreshXmp"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        panelTasks.add(labelRefreshXmp, gridBagConstraints);

        toggleButtonRefreshXmp.setText(bundle.getString("DatabaseUpdatePanel.toggleButtonRefreshXmp.text")); // NOI18N
        toggleButtonRefreshXmp.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        toggleButtonRefreshXmp.setName("toggleButtonRefreshXmp"); // NOI18N
        toggleButtonRefreshXmp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleButtonRefreshXmpActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 0, 0);
        panelTasks.add(toggleButtonRefreshXmp, gridBagConstraints);

        labelUpdateThumbnails.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_image.png"))); // NOI18N
        labelUpdateThumbnails.setText(bundle.getString("DatabaseUpdatePanel.labelUpdateThumbnails.text")); // NOI18N
        labelUpdateThumbnails.setName("labelUpdateThumbnails"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        panelTasks.add(labelUpdateThumbnails, gridBagConstraints);

        buttonUpdateThumbnails.setText(bundle.getString("DatabaseUpdatePanel.buttonUpdateThumbnails.text")); // NOI18N
        buttonUpdateThumbnails.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        buttonUpdateThumbnails.setName("buttonUpdateThumbnails"); // NOI18N
        buttonUpdateThumbnails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonUpdateThumbnailsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 0, 0);
        panelTasks.add(buttonUpdateThumbnails, gridBagConstraints);

        labelRenameFiles.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_rename.png"))); // NOI18N
        labelRenameFiles.setText(bundle.getString("DatabaseUpdatePanel.labelRenameFiles.text")); // NOI18N
        labelRenameFiles.setName("labelRenameFiles"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        panelTasks.add(labelRenameFiles, gridBagConstraints);

        buttonRenameFiles.setText(bundle.getString("DatabaseUpdatePanel.buttonRenameFiles.text")); // NOI18N
        buttonRenameFiles.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        buttonRenameFiles.setName("buttonRenameFiles"); // NOI18N
        buttonRenameFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRenameFilesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 0, 0);
        panelTasks.add(buttonRenameFiles, gridBagConstraints);

        labelCopyKeywordsToKeywordsTree.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_tree.png"))); // NOI18N
        labelCopyKeywordsToKeywordsTree.setText(bundle.getString("DatabaseUpdatePanel.labelCopyKeywordsToKeywordsTree.text")); // NOI18N
        labelCopyKeywordsToKeywordsTree.setName("labelCopyKeywordsToKeywordsTree"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        panelTasks.add(labelCopyKeywordsToKeywordsTree, gridBagConstraints);

        buttonCopyKeywordsToKeywordsTree.setText(bundle.getString("DatabaseUpdatePanel.buttonCopyKeywordsToKeywordsTree.text")); // NOI18N
        buttonCopyKeywordsToKeywordsTree.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        buttonCopyKeywordsToKeywordsTree.setName("buttonCopyKeywordsToKeywordsTree"); // NOI18N
        buttonCopyKeywordsToKeywordsTree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCopyKeywordsToKeywordsTreeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 0, 0);
        panelTasks.add(buttonCopyKeywordsToKeywordsTree, gridBagConstraints);

        labelDeleteKeywordsTree.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_tree.png"))); // NOI18N
        labelDeleteKeywordsTree.setText(bundle.getString("DatabaseUpdatePanel.labelDeleteKeywordsTree.text")); // NOI18N
        labelDeleteKeywordsTree.setName("labelDeleteKeywordsTree"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        panelTasks.add(labelDeleteKeywordsTree, gridBagConstraints);

        buttonDeleteKeywordsTree.setText(bundle.getString("DatabaseUpdatePanel.buttonDeleteKeywordsTree.text")); // NOI18N
        buttonDeleteKeywordsTree.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        buttonDeleteKeywordsTree.setName("buttonDeleteKeywordsTree"); // NOI18N
        buttonDeleteKeywordsTree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteKeywordsTreeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 0, 0);
        panelTasks.add(buttonDeleteKeywordsTree, gridBagConstraints);

        labelExifDateToXmpDateCreated.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_exif.png"))); // NOI18N
        labelExifDateToXmpDateCreated.setText(bundle.getString("DatabaseUpdatePanel.labelExifDateToXmpDateCreated.text")); // NOI18N
        labelExifDateToXmpDateCreated.setName("labelExifDateToXmpDateCreated"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        panelTasks.add(labelExifDateToXmpDateCreated, gridBagConstraints);

        toggleButtonExifDateToXmpDateCreated.setText(bundle.getString("DatabaseUpdatePanel.toggleButtonExifDateToXmpDateCreated.text")); // NOI18N
        toggleButtonExifDateToXmpDateCreated.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        toggleButtonExifDateToXmpDateCreated.setName("toggleButtonExifDateToXmpDateCreated"); // NOI18N
        toggleButtonExifDateToXmpDateCreated.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleButtonExifDateToXmpDateCreatedActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 0, 0);
        panelTasks.add(toggleButtonExifDateToXmpDateCreated, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        panelContent.add(panelTasks, gridBagConstraints);

        progressBar.setName("progressBar"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        panelContent.add(progressBar, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        add(panelContent, gridBagConstraints);
    }//GEN-END:initComponents

    private void toggleButtonRefreshExifActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleButtonRefreshExifActionPerformed
        updateExif();
    }//GEN-LAST:event_toggleButtonRefreshExifActionPerformed

    private void buttonRenameFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRenameFilesActionPerformed
        renameFilesInDb();
    }//GEN-LAST:event_buttonRenameFilesActionPerformed

    private void toggleButtonRefreshXmpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleButtonRefreshXmpActionPerformed
        updateXmp();
    }//GEN-LAST:event_toggleButtonRefreshXmpActionPerformed

    private void buttonUpdateThumbnailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonUpdateThumbnailsActionPerformed
        updateThumbnails();
    }//GEN-LAST:event_buttonUpdateThumbnailsActionPerformed

    private void buttonCopyKeywordsToKeywordsTreeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCopyKeywordsToKeywordsTreeActionPerformed
        copyKeywordsToKeywordsTree();
    }//GEN-LAST:event_buttonCopyKeywordsToKeywordsTreeActionPerformed

    private void toggleButtonExifDateToXmpDateCreatedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleButtonExifDateToXmpDateCreatedActionPerformed
        exifDateToXmpDateCreated();
    }//GEN-LAST:event_toggleButtonExifDateToXmpDateCreatedActionPerformed

    private void buttonDeleteKeywordsTreeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteKeywordsTreeActionPerformed
        deleteAllKeywordsFromKeywordsTree();
    }//GEN-LAST:event_buttonDeleteKeywordsTreeActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCopyKeywordsToKeywordsTree;
    private javax.swing.JButton buttonDeleteKeywordsTree;
    private javax.swing.JButton buttonRenameFiles;
    private javax.swing.JButton buttonUpdateThumbnails;
    private javax.swing.JLabel labelCopyKeywordsToKeywordsTree;
    private javax.swing.JLabel labelDeleteKeywordsTree;
    private javax.swing.JLabel labelExifDateToXmpDateCreated;
    private javax.swing.JLabel labelRefreshExif;
    private javax.swing.JLabel labelRefreshXmp;
    private javax.swing.JLabel labelRenameFiles;
    private javax.swing.JLabel labelUpdateThumbnails;
    private javax.swing.JPanel panelContent;
    private javax.swing.JPanel panelTasks;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JToggleButton toggleButtonExifDateToXmpDateCreated;
    private javax.swing.JToggleButton toggleButtonRefreshExif;
    private javax.swing.JToggleButton toggleButtonRefreshXmp;
    // End of variables declaration//GEN-END:variables
}
