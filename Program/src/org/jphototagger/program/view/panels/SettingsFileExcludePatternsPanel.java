package org.jphototagger.program.view.panels;

import java.awt.Container;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openide.util.Lookup;

import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.domain.repository.FileExcludePatternsRepository;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.componentutil.MnemonicUtil;
import org.jphototagger.program.app.AppPreferencesKeys;
import org.jphototagger.program.model.FileExcludePatternsListModel;
import org.jphototagger.program.types.Persistence;

/**
 *
 * @author Elmar Baumann
 */
public final class SettingsFileExcludePatternsPanel extends javax.swing.JPanel implements ProgressListener, Persistence, ListSelectionListener {
    private static final long serialVersionUID = -3083582823254767001L;
    private final FileExcludePatternsListModel model = new FileExcludePatternsListModel();
    private boolean isUpdateRepository = false;
    private boolean cancel = false;
    private final FileExcludePatternsRepository repo = Lookup.getDefault().lookup(FileExcludePatternsRepository.class);

    public SettingsFileExcludePatternsPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        textFieldInputPattern.requestFocusInWindow();
        MnemonicUtil.setMnemonics((Container) this);
        panelSelectRootFiles.setPersistenceKey(AppPreferencesKeys.KEY_UI_DIRECTORIES_TAB_HIDE_ROOT_FILES);
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            cancel = false;
            isUpdateRepository = false;
            setEnabled();
        } else {
            cancelUpdateRepository();
        }

        super.setVisible(visible);
    }

    private void deletePattern() {
        if (list.getSelectedIndex() < 0) {
            return;
        }

        String pattern = (String) list.getSelectedValue();

        model.delete(pattern);
        setEnabled();
        list.requestFocusInWindow();
    }

    private void handleListValueChanged(ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting()) {
            setSelectedPatternToInput();
            setEnabled();
        }
    }

    private void handleTextFieldInputPatternKeyReleased(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            insertPattern();
        } else {
            setEnabledButtonInsertPattern();
        }
    }

    private void handleListKeyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            deletePattern();
        }
    }

    private void insertPattern() {
        String input = textFieldInputPattern.getText().trim();

        if (!input.isEmpty() && !model.contains(input)) {
            model.insert(input);
            textFieldInputPattern.setText("");
        }

        setEnabled();
    }

    private void setEnabledButtonInsertPattern() {
        buttonInsertPattern.setEnabled(hasInput() && !existsInput());
    }

    private boolean hasInput() {
        return !textFieldInputPattern.getText().trim().isEmpty();
    }
    private boolean existsInput() {
        String input = textFieldInputPattern.getText().trim();

        return !input.isEmpty() && model.contains(input);
    }

    private void setEnabled() {
        int size = model.getSize();
        boolean itemIsSelected = list.getSelectedIndex() >= 0;

        setEnabledButtonInsertPattern();
        buttonDeletePattern.setEnabled(itemIsSelected);
        menuItemDeletePattern.setEnabled(itemIsSelected);
        buttonUpdateRepository.setEnabled((size > 0) &&!isUpdateRepository);
        buttonCancelUpdateRepository.setEnabled(isUpdateRepository);
    }

    private void setSelectedPatternToInput() {
        String pattern = (String) list.getSelectedValue();

        if (pattern != null) {
            textFieldInputPattern.setText(pattern);
        }
    }

    private void updateRepository() {
        List<String> patterns = model.getPatterns();

        if (patterns.size() > 0) {
            isUpdateRepository = true;
            cancel = false;
            setEnabled();
            repo.deleteMatchingFiles(patterns, this);
        }
    }

    private void cancelUpdateRepository() {
        cancel = true;
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
                progressBarUpdateRepository.setMinimum(evt.getMinimum());
                progressBarUpdateRepository.setMaximum(evt.getMaximum());
                progressBarUpdateRepository.setValue(evt.getValue());
                checkCancel(evt);
            }
        });
    }

    @Override
    public void progressPerformed(final ProgressEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                progressBarUpdateRepository.setValue(evt.getValue());
                checkCancel(evt);
            }
        });
    }

    @Override
    public void progressEnded(final ProgressEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                progressBarUpdateRepository.setValue(evt.getValue());
                isUpdateRepository = false;
                cancel = false;
                setEnabled();
            }
        });
    }

    @Override
    public void restore() {
        panelSelectRootFiles.restore();
    }

    @Override
    public void persist() {
        panelSelectRootFiles.persist();
    }

    @Override
    public void valueChanged(ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting()) {
            setEnabled();
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

        popupMenu = new javax.swing.JPopupMenu();
        menuItemDeletePattern = new javax.swing.JMenuItem();
        tabbedPane = new javax.swing.JTabbedPane();
        panelFiles = new javax.swing.JPanel();
        labelInfoRepository = new javax.swing.JLabel();
        progressBarUpdateRepository = new javax.swing.JProgressBar();
        buttonCancelUpdateRepository = new javax.swing.JButton();
        buttonUpdateRepository = new javax.swing.JButton();
        labelTextFieldInputPattern = new javax.swing.JLabel();
        textFieldInputPattern = new javax.swing.JTextField();
        buttonDeletePattern = new javax.swing.JButton();
        buttonInsertPattern = new javax.swing.JButton();
        labelInfoList = new javax.swing.JLabel();
        scrollPane = new javax.swing.JScrollPane();
        list = new org.jdesktop.swingx.JXList();
        panelDirectoryFolder = new javax.swing.JPanel();
        labelInfopanelDirectoryFolder = new javax.swing.JLabel();
        scrollPanePanelDirectoryFolder = new javax.swing.JScrollPane();
        panelSelectRootFiles = new org.jphototagger.program.view.panels.SelectRootFilesPanel();

        popupMenu.setName("popupMenu"); // NOI18N

        menuItemDeletePattern.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        menuItemDeletePattern.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_delete.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/view/panels/Bundle"); // NOI18N
        menuItemDeletePattern.setText(bundle.getString("SettingsFileExcludePatternsPanel.menuItemDeletePattern.text")); // NOI18N
        menuItemDeletePattern.setName("menuItemDeletePattern"); // NOI18N
        menuItemDeletePattern.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemDeletePatternActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemDeletePattern);

        setName("Form"); // NOI18N

        tabbedPane.setName("tabbedPane"); // NOI18N

        panelFiles.setName("panelFiles"); // NOI18N

        labelInfoRepository.setForeground(new java.awt.Color(0, 0, 255));
        labelInfoRepository.setText(bundle.getString("SettingsFileExcludePatternsPanel.labelInfoRepository.text")); // NOI18N
        labelInfoRepository.setName("labelInfoRepository"); // NOI18N

        progressBarUpdateRepository.setName("progressBarUpdateRepository"); // NOI18N

        buttonCancelUpdateRepository.setText(bundle.getString("SettingsFileExcludePatternsPanel.buttonCancelUpdateRepository.text")); // NOI18N
        buttonCancelUpdateRepository.setEnabled(false);
        buttonCancelUpdateRepository.setName("buttonCancelUpdateRepository"); // NOI18N
        buttonCancelUpdateRepository.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelUpdateRepositoryActionPerformed(evt);
            }
        });

        buttonUpdateRepository.setText(bundle.getString("SettingsFileExcludePatternsPanel.buttonUpdateRepository.text")); // NOI18N
        buttonUpdateRepository.setName("buttonUpdateRepository"); // NOI18N
        buttonUpdateRepository.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonUpdateRepositoryActionPerformed(evt);
            }
        });

        labelTextFieldInputPattern.setLabelFor(textFieldInputPattern);
        labelTextFieldInputPattern.setText(bundle.getString("SettingsFileExcludePatternsPanel.labelTextFieldInputPattern.text")); // NOI18N
        labelTextFieldInputPattern.setName("labelTextFieldInputPattern"); // NOI18N

        textFieldInputPattern.setName("textFieldInputPattern"); // NOI18N
        textFieldInputPattern.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldInputPatternFocusGained(evt);
            }
        });
        textFieldInputPattern.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldInputPatternKeyReleased(evt);
            }
        });

        buttonDeletePattern.setText(bundle.getString("SettingsFileExcludePatternsPanel.buttonDeletePattern.text")); // NOI18N
        buttonDeletePattern.setToolTipText(bundle.getString("SettingsFileExcludePatternsPanel.buttonDeletePattern.toolTipText")); // NOI18N
        buttonDeletePattern.setEnabled(false);
        buttonDeletePattern.setName("buttonDeletePattern"); // NOI18N
        buttonDeletePattern.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeletePatternActionPerformed(evt);
            }
        });

        buttonInsertPattern.setText(bundle.getString("SettingsFileExcludePatternsPanel.buttonInsertPattern.text")); // NOI18N
        buttonInsertPattern.setToolTipText(bundle.getString("SettingsFileExcludePatternsPanel.buttonInsertPattern.toolTipText")); // NOI18N
        buttonInsertPattern.setEnabled(false);
        buttonInsertPattern.setName("buttonInsertPattern"); // NOI18N
        buttonInsertPattern.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonInsertPatternActionPerformed(evt);
            }
        });

        labelInfoList.setLabelFor(list);
        labelInfoList.setText(bundle.getString("SettingsFileExcludePatternsPanel.labelInfoList.text")); // NOI18N
        labelInfoList.setName("labelInfoList"); // NOI18N

        scrollPane.setName("scrollPane"); // NOI18N

        list.setModel(model);
        list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        list.setComponentPopupMenu(popupMenu);
        list.setName("list"); // NOI18N
        list.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listValueChanged(evt);
            }
        });
        list.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listKeyPressed(evt);
            }
        });
        scrollPane.setViewportView(list);

        javax.swing.GroupLayout panelFilesLayout = new javax.swing.GroupLayout(panelFiles);
        panelFiles.setLayout(panelFilesLayout);
        panelFilesLayout.setHorizontalGroup(
            panelFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 740, Short.MAX_VALUE)
            .addGroup(panelFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelFilesLayout.createSequentialGroup()
                    .addGap(7, 7, 7)
                    .addGroup(panelFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(scrollPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 725, Short.MAX_VALUE)
                        .addComponent(labelInfoList, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelFilesLayout.createSequentialGroup()
                            .addComponent(buttonDeletePattern)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(buttonInsertPattern))
                        .addComponent(labelInfoRepository, javax.swing.GroupLayout.DEFAULT_SIZE, 725, Short.MAX_VALUE)
                        .addComponent(progressBarUpdateRepository, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 725, Short.MAX_VALUE)
                        .addGroup(panelFilesLayout.createSequentialGroup()
                            .addComponent(buttonCancelUpdateRepository)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(buttonUpdateRepository))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelFilesLayout.createSequentialGroup()
                            .addComponent(labelTextFieldInputPattern)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(textFieldInputPattern, javax.swing.GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE)))
                    .addGap(8, 8, 8)))
        );
        panelFilesLayout.setVerticalGroup(
            panelFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 313, Short.MAX_VALUE)
            .addGroup(panelFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelFilesLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(labelInfoList)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(panelFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(textFieldInputPattern, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(labelTextFieldInputPattern))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(panelFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(buttonInsertPattern)
                        .addComponent(buttonDeletePattern))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(labelInfoRepository, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(progressBarUpdateRepository, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(panelFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(buttonUpdateRepository)
                        .addComponent(buttonCancelUpdateRepository))
                    .addContainerGap()))
        );

        tabbedPane.addTab(bundle.getString("SettingsFileExcludePatternsPanel.panelFiles.TabConstraints.tabTitle"), panelFiles); // NOI18N

        panelDirectoryFolder.setName("panelDirectoryFolder"); // NOI18N

        labelInfopanelDirectoryFolder.setText(bundle.getString("SettingsFileExcludePatternsPanel.labelInfopanelDirectoryFolder.text")); // NOI18N
        labelInfopanelDirectoryFolder.setName("labelInfopanelDirectoryFolder"); // NOI18N

        scrollPanePanelDirectoryFolder.setName("scrollPanePanelDirectoryFolder"); // NOI18N

        panelSelectRootFiles.setName("panelSelectRootFiles"); // NOI18N
        scrollPanePanelDirectoryFolder.setViewportView(panelSelectRootFiles);

        javax.swing.GroupLayout panelDirectoryFolderLayout = new javax.swing.GroupLayout(panelDirectoryFolder);
        panelDirectoryFolder.setLayout(panelDirectoryFolderLayout);
        panelDirectoryFolderLayout.setHorizontalGroup(
            panelDirectoryFolderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDirectoryFolderLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDirectoryFolderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPanePanelDirectoryFolder, javax.swing.GroupLayout.DEFAULT_SIZE, 716, Short.MAX_VALUE)
                    .addComponent(labelInfopanelDirectoryFolder))
                .addContainerGap())
        );
        panelDirectoryFolderLayout.setVerticalGroup(
            panelDirectoryFolderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDirectoryFolderLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelInfopanelDirectoryFolder)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPanePanelDirectoryFolder, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedPane.addTab(bundle.getString("SettingsFileExcludePatternsPanel.panelDirectoryFolder.TabConstraints.tabTitle"), panelDirectoryFolder); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
        );
    }//GEN-END:initComponents

    private void listValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listValueChanged
        handleListValueChanged(evt);
    }//GEN-LAST:event_listValueChanged

    private void textFieldInputPatternKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldInputPatternKeyReleased
        handleTextFieldInputPatternKeyReleased(evt);
    }//GEN-LAST:event_textFieldInputPatternKeyReleased

    private void buttonCancelUpdateRepositoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelUpdateRepositoryActionPerformed
        cancelUpdateRepository();
    }//GEN-LAST:event_buttonCancelUpdateRepositoryActionPerformed

    private void buttonDeletePatternActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeletePatternActionPerformed
        deletePattern();
    }//GEN-LAST:event_buttonDeletePatternActionPerformed

    private void buttonUpdateRepositoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonUpdateRepositoryActionPerformed
        updateRepository();
    }//GEN-LAST:event_buttonUpdateRepositoryActionPerformed

    private void buttonInsertPatternActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonInsertPatternActionPerformed
        insertPattern();
    }//GEN-LAST:event_buttonInsertPatternActionPerformed

    private void listKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listKeyPressed
        handleListKeyPressed(evt);
    }//GEN-LAST:event_listKeyPressed

    private void menuItemDeletePatternActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemDeletePatternActionPerformed
        deletePattern();
    }//GEN-LAST:event_menuItemDeletePatternActionPerformed

    private void textFieldInputPatternFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textFieldInputPatternFocusGained
        textFieldInputPattern.selectAll();
    }//GEN-LAST:event_textFieldInputPatternFocusGained
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCancelUpdateRepository;
    private javax.swing.JButton buttonDeletePattern;
    private javax.swing.JButton buttonInsertPattern;
    private javax.swing.JButton buttonUpdateRepository;
    private javax.swing.JLabel labelInfoList;
    private javax.swing.JLabel labelInfoRepository;
    private javax.swing.JLabel labelInfopanelDirectoryFolder;
    private javax.swing.JLabel labelTextFieldInputPattern;
    private org.jdesktop.swingx.JXList list;
    private javax.swing.JMenuItem menuItemDeletePattern;
    private javax.swing.JPanel panelDirectoryFolder;
    private javax.swing.JPanel panelFiles;
    private org.jphototagger.program.view.panels.SelectRootFilesPanel panelSelectRootFiles;
    private javax.swing.JPopupMenu popupMenu;
    private javax.swing.JProgressBar progressBarUpdateRepository;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JScrollPane scrollPanePanelDirectoryFolder;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTextField textFieldInputPattern;
    // End of variables declaration//GEN-END:variables
}
