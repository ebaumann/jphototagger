package org.jphototagger.maintainance;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.JToggleButton;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.concurrent.HelperThread;
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;

/**
 * @author Elmar Baumann
 */
public class RepositoryUpdatePanel extends PanelExt implements ActionListener, ProgressListener {

    private static final long serialVersionUID = 1L;
    private static final String BUTTON_TEXT_CANCEL = Bundle.getString(RepositoryUpdatePanel.class, "RepositoryUpdatePanel.DisplayName.Cancel");
    private transient UpdateAllThumbnails thumbnailUpdater;
    private final AbstractButton[] buttons;
    private volatile boolean cancel;

    public RepositoryUpdatePanel() {
        initComponents();
        buttons = new AbstractButton[] {
            toggleButtonRefreshExif,
            toggleButtonRefreshXmp,
            buttonUpdateThumbnails,
            buttonRenameFiles,
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
        startOrCancelHelperThread(toggleButtonRefreshExif, RefreshExifOfKnownFilesInRepository.class);
    }

    private void updateXmp() {
        startOrCancelHelperThread(toggleButtonRefreshXmp, RefreshXmpOfKnownFilesInRepository.class);
    }

    private synchronized void startOrCancelHelperThread(JToggleButton button, Class<?> helperThreadClass) {
        if (button.isSelected()) {
            try {
                HelperThread helperThread = (HelperThread) helperThreadClass.newInstance();

                disableOtherButtons(button);
                helperThread.addProgressListener(this);
                cancel = false;
                helperThread.start();
                button.setText(BUTTON_TEXT_CANCEL);
            } catch (Throwable t) {
                Logger.getLogger(RepositoryUpdatePanel.class.getName()).log(Level.SEVERE, null, t);
            }
        } else {
            cancel = true;
            setEnabledAllButtons(true);
            setStartButtonTexts();
        }
    }

    private void setStartButtonTexts() {
        toggleButtonRefreshExif.setText(Bundle.getString(RepositoryUpdatePanel.class, "RepositoryUpdatePanel.toggleButtonRefreshExif.text"));
        toggleButtonRefreshXmp.setText(Bundle.getString(RepositoryUpdatePanel.class, "RepositoryUpdatePanel.toggleButtonRefreshXmp.text"));
        buttonUpdateThumbnails.setText(Bundle.getString(RepositoryUpdatePanel.class, "RepositoryUpdatePanel.buttonUpdateThumbnails.text"));
        buttonRenameFiles.setText(Bundle.getString(RepositoryUpdatePanel.class, "RepositoryUpdatePanel.buttonRenameFiles.text"));
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
        RenameFilenamesInRepositoryDialog dlg = new RenameFilenamesInRepositoryDialog();

        setEnabledAllButtons(false);
        dlg.setVisible(true);
        ComponentUtil.parentWindowToFront(this);
        setEnabledAllButtons(true);
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

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panelContent = UiFactory.panel();
        panelTasks = UiFactory.panel();
        labelRefreshExif = UiFactory.label();
        toggleButtonRefreshExif = UiFactory.toggleButton();
        labelRefreshXmp = UiFactory.label();
        toggleButtonRefreshXmp = UiFactory.toggleButton();
        labelUpdateThumbnails = UiFactory.label();
        buttonUpdateThumbnails = UiFactory.button();
        labelRenameFiles = UiFactory.label();
        buttonRenameFiles = UiFactory.button();
        panelPadding = UiFactory.panel();

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        panelContent.setName("panelContent"); // NOI18N
        panelContent.setLayout(new java.awt.GridBagLayout());

        panelTasks.setName("panelTasks"); // NOI18N
        panelTasks.setLayout(new java.awt.GridBagLayout());

        labelRefreshExif.setIcon(org.jphototagger.resources.Icons.getIcon("icon_exif.png"));
        labelRefreshExif.setText(Bundle.getString(getClass(), "RepositoryUpdatePanel.labelRefreshExif.text")); // NOI18N
        labelRefreshExif.setName("labelRefreshExif"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelTasks.add(labelRefreshExif, gridBagConstraints);

        toggleButtonRefreshExif.setText(Bundle.getString(getClass(), "RepositoryUpdatePanel.toggleButtonRefreshExif.text")); // NOI18N
        toggleButtonRefreshExif.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        toggleButtonRefreshExif.setName("toggleButtonRefreshExif"); // NOI18N
        toggleButtonRefreshExif.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleButtonRefreshExifActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 10, 0, 0);
        panelTasks.add(toggleButtonRefreshExif, gridBagConstraints);

        labelRefreshXmp.setIcon(org.jphototagger.resources.Icons.getIcon("icon_xmp.png"));
        labelRefreshXmp.setText(Bundle.getString(getClass(), "RepositoryUpdatePanel.labelRefreshXmp.text")); // NOI18N
        labelRefreshXmp.setName("labelRefreshXmp"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(3, 0, 0, 0);
        panelTasks.add(labelRefreshXmp, gridBagConstraints);

        toggleButtonRefreshXmp.setText(Bundle.getString(getClass(), "RepositoryUpdatePanel.toggleButtonRefreshXmp.text")); // NOI18N
        toggleButtonRefreshXmp.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        toggleButtonRefreshXmp.setName("toggleButtonRefreshXmp"); // NOI18N
        toggleButtonRefreshXmp.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleButtonRefreshXmpActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(3, 10, 0, 0);
        panelTasks.add(toggleButtonRefreshXmp, gridBagConstraints);

        labelUpdateThumbnails.setIcon(org.jphototagger.resources.Icons.getIcon("icon_image.png"));
        labelUpdateThumbnails.setText(Bundle.getString(getClass(), "RepositoryUpdatePanel.labelUpdateThumbnails.text")); // NOI18N
        labelUpdateThumbnails.setName("labelUpdateThumbnails"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(3, 0, 0, 0);
        panelTasks.add(labelUpdateThumbnails, gridBagConstraints);

        buttonUpdateThumbnails.setText(Bundle.getString(getClass(), "RepositoryUpdatePanel.buttonUpdateThumbnails.text")); // NOI18N
        buttonUpdateThumbnails.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        buttonUpdateThumbnails.setName("buttonUpdateThumbnails"); // NOI18N
        buttonUpdateThumbnails.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonUpdateThumbnailsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(3, 10, 0, 0);
        panelTasks.add(buttonUpdateThumbnails, gridBagConstraints);

        labelRenameFiles.setIcon(org.jphototagger.resources.Icons.getIcon("icon_rename.png"));
        labelRenameFiles.setText(Bundle.getString(getClass(), "RepositoryUpdatePanel.labelRenameFiles.text")); // NOI18N
        labelRenameFiles.setName("labelRenameFiles"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(3, 0, 0, 0);
        panelTasks.add(labelRenameFiles, gridBagConstraints);

        buttonRenameFiles.setText(Bundle.getString(getClass(), "RepositoryUpdatePanel.buttonRenameFiles.text")); // NOI18N
        buttonRenameFiles.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        buttonRenameFiles.setName("buttonRenameFiles"); // NOI18N
        buttonRenameFiles.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRenameFilesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(3, 10, 0, 0);
        panelTasks.add(buttonRenameFiles, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        panelContent.add(panelTasks, gridBagConstraints);

        panelPadding.setName("panelPadding"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelContent.add(panelPadding, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(6, 6, 6, 6);
        add(panelContent, gridBagConstraints);
    }

    private void toggleButtonRefreshExifActionPerformed(java.awt.event.ActionEvent evt) {
        updateExif();
    }

    private void buttonRenameFilesActionPerformed(java.awt.event.ActionEvent evt) {
        renameFilesInDb();
    }

    private void toggleButtonRefreshXmpActionPerformed(java.awt.event.ActionEvent evt) {
        updateXmp();
    }

    private void buttonUpdateThumbnailsActionPerformed(java.awt.event.ActionEvent evt) {
        updateThumbnails();
    }

    private javax.swing.JButton buttonRenameFiles;
    private javax.swing.JButton buttonUpdateThumbnails;
    private javax.swing.JLabel labelRefreshExif;
    private javax.swing.JLabel labelRefreshXmp;
    private javax.swing.JLabel labelRenameFiles;
    private javax.swing.JLabel labelUpdateThumbnails;
    private javax.swing.JPanel panelContent;
    private javax.swing.JPanel panelPadding;
    private javax.swing.JPanel panelTasks;
    private javax.swing.JToggleButton toggleButtonRefreshExif;
    private javax.swing.JToggleButton toggleButtonRefreshXmp;
}
