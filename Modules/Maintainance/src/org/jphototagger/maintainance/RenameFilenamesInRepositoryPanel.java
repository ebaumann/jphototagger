package org.jphototagger.maintainance;

import java.awt.Container;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.api.storage.Persistence;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public class RenameFilenamesInRepositoryPanel extends PanelExt implements ProgressListener, Persistence {

    private static final long serialVersionUID = 1L;
    private static final String KEY_SEARCH = "RenameFilenamesInRepositoryPanel.Search";
    private static final String KEY_REPLACEMENT = "RenameFilenamesInRepositoryPanel.Replacement";
    private volatile boolean runs;

    public RenameFilenamesInRepositoryPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        if (prefs != null) {
            prefs.applyComponentSettings(this, null);
        }

        setButtonReplaceEnabled();
        MnemonicUtil.setMnemonics((Container) this);
    }

    public boolean runs() {
        return runs;
    }

    private void replace(final ProgressListener progressListener) {
        if (confirmReplace()) {
            runs = true;
            setInputEnabled(false);

            Thread thread = new Thread(new Runnable() {
                private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);
                @Override
                public void run() {
                    String searchText = textFieldSearch.getText();
                    String replacementText = textFieldReplacement.getText();
                    int count = repo.updateRenameFilenamesStartingWith(searchText, replacementText, progressListener);
                    String message = Bundle.getString(RenameFilenamesInRepositoryPanel.class, "RenameFilenamesInRepositoryPanel.Info.Count", count);

                    MessageDisplayer.information(null, message);
                    runs = false;
                }
            }, "JPhotoTagger: Renaming files in repository");

            thread.start();
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
            }
        });
    }

    @Override
    public void progressPerformed(final ProgressEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                progressBar.setValue(evt.getValue());
            }
        });
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                progressBar.setValue(0);
                setInputEnabled(true);
            }
        });
    }

    private void setInputEnabled(boolean enabled) {
        buttonReplace.setEnabled(enabled);
        textFieldSearch.setEnabled(enabled);
        textFieldReplacement.setEnabled(enabled);
    }

    private void setButtonReplaceEnabled() {
        String searchText = textFieldSearch.getText().trim();
        String replacementText = textFieldReplacement.getText().trim();
        boolean textsEquals = searchText.equals(replacementText);

        buttonReplace.setEnabled(!searchText.isEmpty() &&!replacementText.isEmpty() &&!textsEquals);
    }

    private boolean confirmReplace() {
        String searchText = textFieldSearch.getText();
        String replacementText = textFieldReplacement.getText();
        String message = Bundle.getString(RenameFilenamesInRepositoryPanel.class, "RenameFilenamesInRepositoryPanel.Confirm.Replace", searchText, replacementText);

        return MessageDisplayer.confirmYesNo(this, message);
    }

    @Override
    public void restore() {
        if (this == null) {
            throw new NullPointerException("this == null");
        }

        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        textFieldSearch.setText(prefs.getString(KEY_SEARCH));
        textFieldReplacement.setText(prefs.getString(KEY_REPLACEMENT));
    }

    @Override
    public void persist() {
        if (this == null) {
            throw new NullPointerException("this == null");
        }

        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        prefs.setString(KEY_SEARCH, textFieldSearch.getText());
        prefs.setString(KEY_REPLACEMENT, textFieldReplacement.getText());
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroupPosition = new javax.swing.ButtonGroup();
        labelTitle = UiFactory.label();
        labelSearch = UiFactory.label();
        textFieldSearch = UiFactory.textField();
        labelReplacement = UiFactory.label();
        textFieldReplacement = UiFactory.textField();
        progressBar = UiFactory.progressBar();
        buttonReplace = UiFactory.button();

        
        setLayout(new java.awt.GridBagLayout());

        labelTitle.setText(Bundle.getString(getClass(), "RenameFilenamesInRepositoryPanel.labelTitle.text")); // NOI18N
        labelTitle.setName("labelTitle"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        add(labelTitle, gridBagConstraints);

        labelSearch.setLabelFor(textFieldSearch);
        labelSearch.setText(Bundle.getString(getClass(), "RenameFilenamesInRepositoryPanel.labelSearch.text")); // NOI18N
        labelSearch.setName("labelSearch"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 0);
        add(labelSearch, gridBagConstraints);

        textFieldSearch.setColumns(20);
        textFieldSearch.setName("textFieldSearch"); // NOI18N
        textFieldSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldSearchKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 5);
        add(textFieldSearch, gridBagConstraints);

        labelReplacement.setLabelFor(textFieldReplacement);
        labelReplacement.setText(Bundle.getString(getClass(), "RenameFilenamesInRepositoryPanel.labelReplacement.text")); // NOI18N
        labelReplacement.setName("labelReplacement"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 0);
        add(labelReplacement, gridBagConstraints);

        textFieldReplacement.setColumns(20);
        textFieldReplacement.setName("textFieldReplacement"); // NOI18N
        textFieldReplacement.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldReplacementKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 5);
        add(textFieldReplacement, gridBagConstraints);

        progressBar.setName("progressBar"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 5);
        add(progressBar, gridBagConstraints);

        buttonReplace.setText(Bundle.getString(getClass(), "RenameFilenamesInRepositoryPanel.buttonReplace.text")); // NOI18N
        buttonReplace.setEnabled(false);
        buttonReplace.setName("buttonReplace"); // NOI18N
        buttonReplace.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonReplaceActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 0);
        add(buttonReplace, gridBagConstraints);
    }

    private void buttonReplaceActionPerformed(java.awt.event.ActionEvent evt) {
        replace(this);
    }

    private void textFieldSearchKeyReleased(java.awt.event.KeyEvent evt) {
        setButtonReplaceEnabled();
    }

    private void textFieldReplacementKeyReleased(java.awt.event.KeyEvent evt) {
        setButtonReplaceEnabled();
    }

    private javax.swing.ButtonGroup buttonGroupPosition;
    private javax.swing.JButton buttonReplace;
    private javax.swing.JLabel labelReplacement;
    private javax.swing.JLabel labelSearch;
    private javax.swing.JLabel labelTitle;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JTextField textFieldReplacement;
    private javax.swing.JTextField textFieldSearch;
}
