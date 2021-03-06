package org.jphototagger.maintainance;

import java.awt.Container;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import org.jphototagger.api.concurrent.Cancelable;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class RepositoryMaintainancePanel extends PanelExt implements ProgressListener {

    private static final long serialVersionUID = 1L;
    private static final Icon ICON_FINISHED = org.jphototagger.resources.Icons.getIcon("icon_finished.png");
    private static final String KEY_DEL_RECORDS_OF_NOT_EX_FILES = "RepositoryMaintainancePanel.CheckBox.DeleteNotExistingFilesFromDb";
    private static final String KEY_COMPRESS_DB = "RepositoryMaintainancePanel.CheckBox.CompressDb";
    private static final String KEY_DEL_ORPHANED_THUMBS = "RepositoryMaintainancePanel.CheckBox.DeleteOrphanedThumbnails";
    private final String KEY_DEL_UNUSED_KEYWORDS = "RepositoryMaintainancePanel.CheckBox.DeleteUnusedKeywords";
    private static final String KEY_DEL_NOT_REF_1_N = "RepositoryMaintainancePanel.CheckBox.DeleteNotRef1n";
    private final Stack<Runnable> runnables = new Stack<>();
    private final Map<Class<?>, JLabel> finishedLabelOfRunnable = new HashMap<>();
    private final Set<JCheckBox> checkBoxes = new HashSet<>();
    private final Map<JCheckBox, JLabel> labelOfCheckBox = new HashMap<>();
    private final HTMLDocument messagesDocument = new HTMLDocument();
    private final HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
    private volatile Runnable currentRunnable;
    private volatile boolean containsMessages;
    private volatile boolean cancel;
    private volatile boolean canClose = true;

    public RepositoryMaintainancePanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        textPaneMessages.setEditorKit(htmlEditorKit);
        textPaneMessages.setDocument(messagesDocument);
        finishedLabelOfRunnable.put(CompressRepository.class, labelFinishedCompressRepository);
        finishedLabelOfRunnable.put(DeleteOrphanedXmp.class, labelFinishedDeleteRecordsOfNotExistingFilesInRepository);
        finishedLabelOfRunnable.put(DeleteOrphanedThumbnails.class, labelFinishedDeleteOrphanedThumbnails);
        finishedLabelOfRunnable.put(DeleteUnusedKeywordsFromRepository.class, labelFinishedDeleteUnusedKeywords);
        finishedLabelOfRunnable.put(DeleteNotReferenced1n.class, labelFinishedDeleteNotReferenced1n);
        initCheckBoxes();
        MnemonicUtil.setMnemonics((Container) this);
    }

    private void initCheckBoxes() {
        checkBoxes.add(checkBoxCompressRepository);
        checkBoxes.add(checkBoxDeleteOrphanedThumbnails);
        checkBoxes.add(checkBoxDeleteRecordsOfNotExistingFilesInRepository);
        checkBoxes.add(checkBoxDeleteUnusedKeywords);
        checkBoxes.add(checkBoxDeleteNotReferenced1n);
        labelOfCheckBox.put(checkBoxCompressRepository, labelFinishedCompressRepository);
        labelOfCheckBox.put(checkBoxDeleteOrphanedThumbnails, labelFinishedDeleteOrphanedThumbnails);
        labelOfCheckBox.put(checkBoxDeleteRecordsOfNotExistingFilesInRepository, labelFinishedDeleteRecordsOfNotExistingFilesInRepository);
        labelOfCheckBox.put(checkBoxDeleteUnusedKeywords, labelFinishedDeleteUnusedKeywords);
        labelOfCheckBox.put(checkBoxDeleteNotReferenced1n, labelFinishedDeleteNotReferenced1n);

        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        if (prefs != null) {
            checkBoxCompressRepository.setSelected(prefs.getBoolean(KEY_COMPRESS_DB));
            checkBoxDeleteOrphanedThumbnails.setSelected(prefs.getBoolean(KEY_DEL_ORPHANED_THUMBS));
            checkBoxDeleteRecordsOfNotExistingFilesInRepository.setSelected(prefs.getBoolean(KEY_DEL_RECORDS_OF_NOT_EX_FILES));
            checkBoxDeleteUnusedKeywords.setSelected(prefs.getBoolean(KEY_DEL_UNUSED_KEYWORDS));
            checkBoxDeleteNotReferenced1n.setSelected(prefs.getBoolean(KEY_DEL_NOT_REF_1_N));
        }
    }

    private void setProgressbarStart(ProgressEvent evt) {
        final boolean indeterminate = evt.isIndeterminate();
        final int minimum = evt.getMinimum();
        final int maximum = evt.getMaximum();
        final int value = evt.getValue();
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                if (indeterminate) {
                    progressBar.setIndeterminate(true);
                } else {
                    progressBar.setMinimum(minimum);
                    progressBar.setMaximum(maximum);
                    progressBar.setValue(value);
                }
            }
        });
    }

    private void setProgressbarEnd(ProgressEvent evt) {
        final int value = evt.getValue();
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                if (progressBar.isIndeterminate()) {
                    progressBar.setIndeterminate(false);
                } else {
                    progressBar.setValue(value);
                }
            }
        });
    }

    public void getsVisible(boolean visible) {
        if (visible) {
            resetIcons();
            checkCheckboxes();
        }
    }

    private void resetIcons() {
        labelFinishedCompressRepository.setIcon(null);
        labelFinishedDeleteRecordsOfNotExistingFilesInRepository.setIcon(null);
        labelFinishedDeleteOrphanedThumbnails.setIcon(null);
        progressBar.setValue(0);
    }

    private void checkCheckboxes() {
        buttonStartMaintain.setEnabled(isACheckBoxSelected());
        removeFinishedIcons();
    }

    private boolean isACheckBoxSelected() {
        for (JCheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                return true;
            }
        }

        return false;
    }

    private void removeFinishedIcons() {
        for (JCheckBox checkBox : checkBoxes) {
            if (!checkBox.isSelected()) {
                labelOfCheckBox.get(checkBox).setIcon(null);
            }
        }
    }

    private void setCanClose(boolean can) {
        canClose = can;
    }

    public boolean canClose() {
        return canClose;
    }

    private synchronized void startNextThread() {
        if (runnables.size() > 0) {
            currentRunnable = runnables.pop();
            String threadName = "JPhotoTagger: Repository maintainance next task @ " + currentRunnable.getClass().getSimpleName();

            Thread thread = new Thread(currentRunnable, threadName);

            thread.start();
        }
    }

    private void setRunnablesAreRunning(boolean running) {
        buttonCancelAction.setEnabled(running);
        buttonStartMaintain.setEnabled(!running);
        buttonDeleteMessages.setEnabled(!running);
        setCanClose(!running);
    }

    private void cancelCurrentRunnable() {
        if (currentRunnable == null) {
            return;
        }

        Method methodCancel = null;

        if (currentRunnable instanceof Cancelable) {
                ((Cancelable) currentRunnable).cancel();
        }

        if ((methodCancel == null) && (currentRunnable instanceof Thread)) {
            ((Thread) currentRunnable).interrupt();
        }
    }

    private void startMaintain() {
        resetIcons();
        addRunnables();
        setRunnablesAreRunning(true);
        cancel = false;
        startNextThread();
    }

    private synchronized void addRunnables() {
        runnables.clear();

        // reverse order of checkboxes because the runnables are in a stack
        if (checkBoxDeleteOrphanedThumbnails.isSelected()) {
            DeleteOrphanedThumbnails runnable = new DeleteOrphanedThumbnails();

            runnable.addProgressListener(this);
            runnables.push(runnable);
        }

        if (checkBoxCompressRepository.isSelected()) {
            CompressRepository runnable = new CompressRepository();

            runnable.addProgressListener(this);
            runnables.push(runnable);
        }

        if (checkBoxDeleteRecordsOfNotExistingFilesInRepository.isSelected()) {
            DeleteOrphanedXmp runnable = new DeleteOrphanedXmp();

            runnable.addProgressListener(this);
            runnables.push(runnable);
        }

        if (checkBoxDeleteUnusedKeywords.isSelected()) {
            DeleteUnusedKeywordsFromRepository runnable = new DeleteUnusedKeywordsFromRepository();

            runnable.addProgressListener(this);
            runnables.push(runnable);
        }

        if (checkBoxDeleteNotReferenced1n.isSelected()) {
            DeleteNotReferenced1n runnable = new DeleteNotReferenced1n();

            runnable.addProgressListener(this);
            runnables.push(runnable);
        }
    }

    private void checkCancel(ProgressEvent evt) {
        if (cancel) {
            evt.setCancel(true);
        }
    }

    private void appendMessage(Object info, String formatPattern) {
        if (info != null) {
            String message = info.toString().trim();
            if (!message.isEmpty()) {
                appendMessage(MessageFormat.format(formatPattern, message));
            }
        }
    }

    private void appendMessage(String message) {
        int length = messagesDocument.getLength();
        try {
            htmlEditorKit.insertHTML(messagesDocument, length, message, 0, 0, null);
            buttonDeleteMessages.setEnabled(true);
            containsMessages = true;
        } catch (Throwable t) {
            Logger.getLogger(RepositoryMaintainancePanel.class.getName()).log(Level.SEVERE, null, t);
        }
    }

    @Override
    public void progressStarted(final ProgressEvent evt) {
        final Object info = evt.getInfo();
        checkCancel(evt);
        setProgressbarStart(evt);
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                appendMessage(info,
                        (containsMessages ? "<hr>" : "")
                        + "<p><font color=\"#008800\">{0}</font>");
                buttonDeleteMessages.setEnabled(false);
                buttonCancelAction.setEnabled(!(evt.getSource() instanceof CompressRepository));
            }
        });
    }

    @Override
    public void progressPerformed(final ProgressEvent evt) {
        final Object info = evt.getInfo();
        final int value = evt.getValue();
        checkCancel(evt);
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                progressBar.setValue(value);
                appendMessage(info, "{0}");
            }
        });
    }

    @Override
    public void progressEnded(final ProgressEvent evt) {
        final Object info = evt.getInfo();
        setProgressbarEnd(evt);
        final Object source = evt.getSource();
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                appendMessage(info, "<font color=\"#0000dd\">{0}</font>");
                Class<?> sourceClass = source.getClass();
                JLabel labelFinished = finishedLabelOfRunnable.get(sourceClass);
                progressBar.setValue(0);
                if (labelFinished != null) {
                    labelFinished.setIcon(ICON_FINISHED);
                } else if (sourceClass.getName().contains("ImageFilesDatabase")) {
                    labelFinishedDeleteRecordsOfNotExistingFilesInRepository.setIcon(ICON_FINISHED);
                }
                if (runnables.size() > 0) {
                    startNextThread();
                } else {
                    currentRunnable = null;
                    setRunnablesAreRunning(false);
                }
            }
        });
    }

    private void handleButtonCancelActionPerformed() {
        cancel = true;

        synchronized (runnables) {
            cancelCurrentRunnable();
            runnables.clear();
            setRunnablesAreRunning(false);
            currentRunnable = null;
        }
    }

    private void deleteMessages() {
        textPaneMessages.setText("");
        containsMessages = false;
        buttonDeleteMessages.setEnabled(false);
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panelContent = UiFactory.panel();
        panelTasks = UiFactory.panel();
        checkBoxDeleteRecordsOfNotExistingFilesInRepository = UiFactory.checkBox();
        labelFinishedDeleteRecordsOfNotExistingFilesInRepository = UiFactory.label();
        checkBoxCompressRepository = UiFactory.checkBox();
        labelFinishedCompressRepository = UiFactory.label();
        checkBoxDeleteOrphanedThumbnails = UiFactory.checkBox();
        labelFinishedDeleteOrphanedThumbnails = UiFactory.label();
        checkBoxDeleteUnusedKeywords = UiFactory.checkBox();
        labelFinishedDeleteUnusedKeywords = UiFactory.label();
        checkBoxDeleteNotReferenced1n = UiFactory.checkBox();
        labelFinishedDeleteNotReferenced1n = UiFactory.label();
        panelMessages = UiFactory.panel();
        labelMessages = UiFactory.label();
        scrollPaneMessages = UiFactory.scrollPane();
        textPaneMessages = UiFactory.textPane();
        progressBar = UiFactory.progressBar();
        panelButtons = UiFactory.panel();
        buttonDeleteMessages = UiFactory.button();
        buttonCancelAction = UiFactory.button();
        buttonStartMaintain = UiFactory.button();

        setLayout(new java.awt.GridBagLayout());

        panelContent.setLayout(new java.awt.GridBagLayout());

        panelTasks.setLayout(new java.awt.GridBagLayout());

        checkBoxDeleteRecordsOfNotExistingFilesInRepository.setText(Bundle.getString(getClass(), "RepositoryMaintainancePanel.checkBoxDeleteRecordsOfNotExistingFilesInRepository.text")); // NOI18N
        checkBoxDeleteRecordsOfNotExistingFilesInRepository.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxDeleteRecordsOfNotExistingFilesInRepositoryActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        panelTasks.add(checkBoxDeleteRecordsOfNotExistingFilesInRepository, gridBagConstraints);

        labelFinishedDeleteRecordsOfNotExistingFilesInRepository.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelFinishedDeleteRecordsOfNotExistingFilesInRepository.setIconTextGap(0);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        panelTasks.add(labelFinishedDeleteRecordsOfNotExistingFilesInRepository, gridBagConstraints);

        checkBoxCompressRepository.setText(Bundle.getString(getClass(), "RepositoryMaintainancePanel.checkBoxCompressRepository.text")); // NOI18N
        checkBoxCompressRepository.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxCompressRepositoryActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(3, 0, 0, 0);
        panelTasks.add(checkBoxCompressRepository, gridBagConstraints);

        labelFinishedCompressRepository.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelFinishedCompressRepository.setIconTextGap(0);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = UiFactory.insets(3, 0, 0, 0);
        panelTasks.add(labelFinishedCompressRepository, gridBagConstraints);

        checkBoxDeleteOrphanedThumbnails.setText(Bundle.getString(getClass(), "RepositoryMaintainancePanel.checkBoxDeleteOrphanedThumbnails.text")); // NOI18N
        checkBoxDeleteOrphanedThumbnails.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxDeleteOrphanedThumbnailsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(3, 0, 0, 0);
        panelTasks.add(checkBoxDeleteOrphanedThumbnails, gridBagConstraints);

        labelFinishedDeleteOrphanedThumbnails.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelFinishedDeleteOrphanedThumbnails.setIconTextGap(0);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = UiFactory.insets(3, 0, 0, 0);
        panelTasks.add(labelFinishedDeleteOrphanedThumbnails, gridBagConstraints);

        checkBoxDeleteUnusedKeywords.setText(Bundle.getString(getClass(), "RepositoryMaintainancePanel.checkBoxDeleteUnusedKeywords.text")); // NOI18N
        checkBoxDeleteUnusedKeywords.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxDeleteUnusedKeywordsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(3, 0, 0, 0);
        panelTasks.add(checkBoxDeleteUnusedKeywords, gridBagConstraints);

        labelFinishedDeleteUnusedKeywords.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelFinishedDeleteUnusedKeywords.setIconTextGap(0);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = UiFactory.insets(3, 0, 0, 0);
        panelTasks.add(labelFinishedDeleteUnusedKeywords, gridBagConstraints);

        checkBoxDeleteNotReferenced1n.setText(Bundle.getString(getClass(), "RepositoryMaintainancePanel.checkBoxDeleteNotReferenced1n.text")); // NOI18N
        checkBoxDeleteNotReferenced1n.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxDeleteNotReferenced1nActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(3, 0, 0, 0);
        panelTasks.add(checkBoxDeleteNotReferenced1n, gridBagConstraints);

        labelFinishedDeleteNotReferenced1n.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelFinishedDeleteNotReferenced1n.setIconTextGap(0);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = UiFactory.insets(3, 0, 0, 0);
        panelTasks.add(labelFinishedDeleteNotReferenced1n, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        panelContent.add(panelTasks, gridBagConstraints);

        panelMessages.setLayout(new java.awt.GridBagLayout());

        labelMessages.setText(Bundle.getString(getClass(), "RepositoryMaintainancePanel.labelMessages.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        panelMessages.add(labelMessages, gridBagConstraints);

        scrollPaneMessages.setPreferredSize(UiFactory.dimension(200, 150));

        textPaneMessages.setEditable(false);
        putClientProperty(JTextPane.HONOR_DISPLAY_PROPERTIES, true);
        textPaneMessages.setContentType("text/html"); // NOI18N
        scrollPaneMessages.setViewportView(textPaneMessages);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(3, 0, 0, 0);
        panelMessages.add(scrollPaneMessages, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        panelContent.add(panelMessages, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        panelContent.add(progressBar, gridBagConstraints);

        panelButtons.setLayout(new java.awt.GridBagLayout());

        buttonDeleteMessages.setText(Bundle.getString(getClass(), "RepositoryMaintainancePanel.buttonDeleteMessages.text")); // NOI18N
        buttonDeleteMessages.setEnabled(false);
        buttonDeleteMessages.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteMessagesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panelButtons.add(buttonDeleteMessages, gridBagConstraints);

        buttonCancelAction.setText(Bundle.getString(getClass(), "RepositoryMaintainancePanel.buttonCancelAction.text")); // NOI18N
        buttonCancelAction.setEnabled(false);
        buttonCancelAction.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        panelButtons.add(buttonCancelAction, gridBagConstraints);

        buttonStartMaintain.setText(Bundle.getString(getClass(), "RepositoryMaintainancePanel.buttonStartMaintain.text")); // NOI18N
        buttonStartMaintain.setEnabled(false);
        buttonStartMaintain.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStartMaintainActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = UiFactory.insets(0, 3, 0, 0);
        panelButtons.add(buttonStartMaintain, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        panelContent.add(panelButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(7, 7, 7, 7);
        add(panelContent, gridBagConstraints);
    }

    private void checkBoxDeleteRecordsOfNotExistingFilesInRepositoryActionPerformed(java.awt.event.ActionEvent evt) {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        prefs.setBoolean(KEY_DEL_RECORDS_OF_NOT_EX_FILES, checkBoxDeleteRecordsOfNotExistingFilesInRepository.isSelected());
        checkCheckboxes();
    }

    private void checkBoxCompressRepositoryActionPerformed(java.awt.event.ActionEvent evt) {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        prefs.setBoolean(KEY_COMPRESS_DB, checkBoxCompressRepository.isSelected());
        checkCheckboxes();
    }

    private void buttonStartMaintainActionPerformed(java.awt.event.ActionEvent evt) {
        startMaintain();
    }

    private void buttonCancelActionActionPerformed(java.awt.event.ActionEvent evt) {
        handleButtonCancelActionPerformed();
    }

    private void buttonDeleteMessagesActionPerformed(java.awt.event.ActionEvent evt) {
        deleteMessages();
    }

    private void checkBoxDeleteOrphanedThumbnailsActionPerformed(java.awt.event.ActionEvent evt) {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        prefs.setBoolean(KEY_DEL_ORPHANED_THUMBS, checkBoxDeleteOrphanedThumbnails.isSelected());
        checkCheckboxes();
    }

    private void checkBoxDeleteUnusedKeywordsActionPerformed(java.awt.event.ActionEvent evt) {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        prefs.setBoolean(KEY_DEL_UNUSED_KEYWORDS, checkBoxDeleteUnusedKeywords.isSelected());
        checkCheckboxes();
    }

    private void checkBoxDeleteNotReferenced1nActionPerformed(java.awt.event.ActionEvent evt) {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        prefs.setBoolean(KEY_DEL_NOT_REF_1_N, checkBoxDeleteNotReferenced1n.isSelected());
        checkCheckboxes();
    }

    private javax.swing.JButton buttonCancelAction;
    private javax.swing.JButton buttonDeleteMessages;
    private javax.swing.JButton buttonStartMaintain;
    private javax.swing.JCheckBox checkBoxCompressRepository;
    private javax.swing.JCheckBox checkBoxDeleteNotReferenced1n;
    private javax.swing.JCheckBox checkBoxDeleteOrphanedThumbnails;
    private javax.swing.JCheckBox checkBoxDeleteRecordsOfNotExistingFilesInRepository;
    private javax.swing.JCheckBox checkBoxDeleteUnusedKeywords;
    private javax.swing.JLabel labelFinishedCompressRepository;
    private javax.swing.JLabel labelFinishedDeleteNotReferenced1n;
    private javax.swing.JLabel labelFinishedDeleteOrphanedThumbnails;
    private javax.swing.JLabel labelFinishedDeleteRecordsOfNotExistingFilesInRepository;
    private javax.swing.JLabel labelFinishedDeleteUnusedKeywords;
    private javax.swing.JLabel labelMessages;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelContent;
    private javax.swing.JPanel panelMessages;
    private javax.swing.JPanel panelTasks;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JScrollPane scrollPaneMessages;
    private javax.swing.JTextPane textPaneMessages;
}
