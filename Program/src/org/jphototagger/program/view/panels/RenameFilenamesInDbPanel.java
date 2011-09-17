package org.jphototagger.program.view.panels;

import java.awt.Container;

import javax.swing.JPanel;

import org.openide.util.Lookup;

import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.api.storage.Preferences;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.componentutil.MnemonicUtil;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.types.Persistence;

/**
 * @author Elmar Baumann
 */
public class RenameFilenamesInDbPanel extends JPanel implements ProgressListener, Persistence {

    private static final long serialVersionUID = -4207218985613254920L;
    private static final String KEY_SEARCH = "RenameFilenamesInDbPanel.Search";
    private static final String KEY_REPLACEMENT = "RenameFilenamesInDbPanel.Replacement";
    private volatile boolean runs;

    public RenameFilenamesInDbPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        if (storage != null) {
            storage.applyComponentSettings(this, null);
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
                    String message = Bundle.getString(RenameFilenamesInDbPanel.class, "RenameFilenamesInDbPanel.Info.Count", count);

                    MessageDisplayer.information(null, message);
                    runs = false;
                }
            }, "JPhotoTagger: Renaming files in database");

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
        String message = Bundle.getString(RenameFilenamesInDbPanel.class, "RenameFilenamesInDbPanel.Confirm.Replace", searchText, replacementText);

        return MessageDisplayer.confirmYesNo(this, message);
    }

    @Override
    public void readProperties() {
        if (this == null) {
            throw new NullPointerException("this == null");
        }

        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        textFieldSearch.setText(storage.getString(KEY_SEARCH));
        textFieldReplacement.setText(storage.getString(KEY_REPLACEMENT));
    }

    @Override
    public void writeProperties() {
        if (this == null) {
            throw new NullPointerException("this == null");
        }

        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        storage.setString(KEY_SEARCH, textFieldSearch.getText());
        storage.setString(KEY_REPLACEMENT, textFieldReplacement.getText());
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    private void initComponents() {//GEN-BEGIN:initComponents

        buttonGroupPosition = new javax.swing.ButtonGroup();
        labelTitle = new javax.swing.JLabel();
        labelSearch = new javax.swing.JLabel();
        textFieldSearch = new javax.swing.JTextField();
        labelReplacement = new javax.swing.JLabel();
        textFieldReplacement = new javax.swing.JTextField();
        progressBar = new javax.swing.JProgressBar();
        buttonReplace = new javax.swing.JButton();

        setName("Form"); // NOI18N

        labelTitle.setForeground(new java.awt.Color(0, 0, 255));
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/view/panels/Bundle"); // NOI18N
        labelTitle.setText(bundle.getString("RenameFilenamesInDbPanel.labelTitle.text")); // NOI18N
        labelTitle.setName("labelTitle"); // NOI18N

        labelSearch.setForeground(new java.awt.Color(0, 196, 0));
        labelSearch.setLabelFor(textFieldSearch);
        labelSearch.setText(bundle.getString("RenameFilenamesInDbPanel.labelSearch.text")); // NOI18N
        labelSearch.setName("labelSearch"); // NOI18N

        textFieldSearch.setName("textFieldSearch"); // NOI18N
        textFieldSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldSearchKeyReleased(evt);
            }
        });

        labelReplacement.setForeground(new java.awt.Color(0, 196, 0));
        labelReplacement.setLabelFor(textFieldReplacement);
        labelReplacement.setText(bundle.getString("RenameFilenamesInDbPanel.labelReplacement.text")); // NOI18N
        labelReplacement.setName("labelReplacement"); // NOI18N

        textFieldReplacement.setName("textFieldReplacement"); // NOI18N
        textFieldReplacement.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldReplacementKeyReleased(evt);
            }
        });

        progressBar.setName("progressBar"); // NOI18N

        buttonReplace.setText(bundle.getString("RenameFilenamesInDbPanel.buttonReplace.text")); // NOI18N
        buttonReplace.setEnabled(false);
        buttonReplace.setName("buttonReplace"); // NOI18N
        buttonReplace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonReplaceActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelSearch, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(labelReplacement, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(textFieldSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
                            .addComponent(textFieldReplacement, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)))
                    .addComponent(buttonReplace, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelSearch))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelReplacement)
                    .addComponent(textFieldReplacement, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonReplace)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {textFieldReplacement, textFieldSearch});

    }//GEN-END:initComponents

    private void buttonReplaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonReplaceActionPerformed
        replace(this);
    }//GEN-LAST:event_buttonReplaceActionPerformed

    private void textFieldSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldSearchKeyReleased
        setButtonReplaceEnabled();
    }//GEN-LAST:event_textFieldSearchKeyReleased

    private void textFieldReplacementKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldReplacementKeyReleased
        setButtonReplaceEnabled();
    }//GEN-LAST:event_textFieldReplacementKeyReleased
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupPosition;
    private javax.swing.JButton buttonReplace;
    private javax.swing.JLabel labelReplacement;
    private javax.swing.JLabel labelSearch;
    private javax.swing.JLabel labelTitle;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JTextField textFieldReplacement;
    private javax.swing.JTextField textFieldSearch;
    // End of variables declaration//GEN-END:variables
}
