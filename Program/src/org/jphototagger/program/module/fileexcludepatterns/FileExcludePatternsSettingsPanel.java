package org.jphototagger.program.module.fileexcludepatterns;

import java.awt.Container;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.api.storage.Persistence;
import org.jphototagger.domain.DomainPreferencesKeys;
import org.jphototagger.domain.repository.FileExcludePatternsRepository;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.help.HelpPageProvider;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.lib.swing.SelectRootFilesPanel;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class FileExcludePatternsSettingsPanel extends PanelExt
    implements ProgressListener, Persistence, ListSelectionListener, HelpPageProvider {

    private static final long serialVersionUID = 1L;
    private final FileExcludePatternsListModel model = new FileExcludePatternsListModel();
    private boolean isUpdateRepository = false;
    private boolean cancel = false;
    private final FileExcludePatternsRepository repo = Lookup.getDefault().lookup(FileExcludePatternsRepository.class);

    public FileExcludePatternsSettingsPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        textFieldInputPattern.requestFocusInWindow();
        buttonChooseDirs.setText(Bundle.getString(FileExcludePatternsSettingsPanel.class, "FileExcludePatternsSettingsPanel.ButtonChooseDirs.Text"));
        labelInfoChooseDirs.setText(Bundle.getString(FileExcludePatternsSettingsPanel.class, "FileExcludePatternsSettingsPanel.LabelInfoChooseDirs.Text"));
        MnemonicUtil.setMnemonics((Container) this);
        panelSelectRootFiles.setPersistenceKey(DomainPreferencesKeys.KEY_UI_DIRECTORIES_TAB_HIDE_ROOT_FILES);
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
        if (!input.isEmpty() && !model.contains(input) && checkRegex(input)) {
            model.insert(input);
            textFieldInputPattern.setText("");
        }
        setEnabled();
    }

    private boolean checkRegex(String regex) {
        try {
            Pattern.compile(regex);
        } catch (Throwable t) {
            String message = Bundle.getString(FileExcludePatternsSettingsPanel.class, "FileExcludePatternsSettingsPanel.Error.Regex");
            MessageDisplayer.error(this, message);
            return false;
        }
        return true;
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

    @Override
    public String getHelpPageUrl() {
        return Bundle.getString(FileExcludePatternsSettingsPanel.class, "FileExcludePatternsSettingsPanel.HelpPage");
    }

    private void chooseDirPatterns() {
        FileExcludePatternsController ctrl = new FileExcludePatternsController();
        ctrl.setDirChooserOwner(ComponentUtil.findParentDialog(this));

        for (String pattern : ctrl.chooseDirectoryPatterns()) {
            if (!model.contains(pattern) && checkRegex(pattern)) {
                model.insert(pattern);
            }
        }
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        popupMenu = UiFactory.popupMenu();
        menuItemDeletePattern = UiFactory.menuItem();
        tabbedPane = UiFactory.tabbedPane();
        panelFiles = UiFactory.panel();
        labelInfoList = UiFactory.label();
        scrollPane = UiFactory.scrollPane();
        list = UiFactory.jxList();
        labelTextFieldInputPattern = UiFactory.label();
        textFieldInputPattern = UiFactory.textField();
        panelButtons = UiFactory.panel();
        buttonChooseDirs = UiFactory.button();
        buttonDeletePattern = UiFactory.button();
        buttonInsertPattern = UiFactory.button();
        labelInfoChooseDirs = UiFactory.label();
        panelRepository = UiFactory.panel();
        labelInfoRepository = UiFactory.label();
        progressBarUpdateRepository = UiFactory.progressBar();
        panelButtonsRepository = UiFactory.panel();
        buttonCancelUpdateRepository = UiFactory.button();
        buttonUpdateRepository = UiFactory.button();
        panelDirectoryFolder = UiFactory.panel();
        labelInfopanelDirectoryFolder = UiFactory.label();
        scrollPanePanelDirectoryFolder = UiFactory.scrollPane();
        panelSelectRootFiles = new SelectRootFilesPanel();

        popupMenu.setName("popupMenu"); // NOI18N

        menuItemDeletePattern.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        menuItemDeletePattern.setIcon(org.jphototagger.resources.Icons.getIcon("icon_delete.png"));
        menuItemDeletePattern.setText(Bundle.getString(getClass(), "FileExcludePatternsSettingsPanel.menuItemDeletePattern.text")); // NOI18N
        menuItemDeletePattern.setName("menuItemDeletePattern"); // NOI18N
        menuItemDeletePattern.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemDeletePatternActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemDeletePattern);

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        tabbedPane.setName("tabbedPane"); // NOI18N

        panelFiles.setName("panelFiles"); // NOI18N
        panelFiles.setLayout(new java.awt.GridBagLayout());

        labelInfoList.setLabelFor(list);
        labelInfoList.setText(Bundle.getString(getClass(), "FileExcludePatternsSettingsPanel.labelInfoList.text")); // NOI18N
        labelInfoList.setName("labelInfoList"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 5);
        panelFiles.add(labelInfoList, gridBagConstraints);

        scrollPane.setName("scrollPane"); // NOI18N

        list.setModel(model);
        list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        list.setComponentPopupMenu(popupMenu);
        list.setName("list"); // NOI18N
        list.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            @Override
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listValueChanged(evt);
            }
        });
        list.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listKeyPressed(evt);
            }
        });
        scrollPane.setViewportView(list);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(3, 5, 0, 5);
        panelFiles.add(scrollPane, gridBagConstraints);

        labelTextFieldInputPattern.setLabelFor(textFieldInputPattern);
        labelTextFieldInputPattern.setText(Bundle.getString(getClass(), "FileExcludePatternsSettingsPanel.labelTextFieldInputPattern.text")); // NOI18N
        labelTextFieldInputPattern.setName("labelTextFieldInputPattern"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 0);
        panelFiles.add(labelTextFieldInputPattern, gridBagConstraints);

        textFieldInputPattern.setName("textFieldInputPattern"); // NOI18N
        textFieldInputPattern.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldInputPatternFocusGained(evt);
            }
        });
        textFieldInputPattern.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldInputPatternKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 5);
        panelFiles.add(textFieldInputPattern, gridBagConstraints);

        panelButtons.setName("panelButtons"); // NOI18N
        panelButtons.setLayout(new java.awt.GridBagLayout());

        buttonChooseDirs.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseDirsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelButtons.add(buttonChooseDirs, gridBagConstraints);

        buttonDeletePattern.setText(Bundle.getString(getClass(), "FileExcludePatternsSettingsPanel.buttonDeletePattern.text")); // NOI18N
        buttonDeletePattern.setToolTipText(Bundle.getString(getClass(), "FileExcludePatternsSettingsPanel.buttonDeletePattern.toolTipText")); // NOI18N
        buttonDeletePattern.setEnabled(false);
        buttonDeletePattern.setName("buttonDeletePattern"); // NOI18N
        buttonDeletePattern.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeletePatternActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelButtons.add(buttonDeletePattern, gridBagConstraints);

        buttonInsertPattern.setText(Bundle.getString(getClass(), "FileExcludePatternsSettingsPanel.buttonInsertPattern.text")); // NOI18N
        buttonInsertPattern.setToolTipText(Bundle.getString(getClass(), "FileExcludePatternsSettingsPanel.buttonInsertPattern.toolTipText")); // NOI18N
        buttonInsertPattern.setEnabled(false);
        buttonInsertPattern.setName("buttonInsertPattern"); // NOI18N
        buttonInsertPattern.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonInsertPatternActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelButtons.add(buttonInsertPattern, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        panelFiles.add(panelButtons, gridBagConstraints);

        labelInfoChooseDirs.setText(" "); // NOI18N
        labelInfoChooseDirs.setName("labelInfoChooseDirs"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 5);
        panelFiles.add(labelInfoChooseDirs, gridBagConstraints);

        panelRepository.setName("panelRepository"); // NOI18N
        panelRepository.setLayout(new java.awt.GridBagLayout());

        labelInfoRepository.setText(Bundle.getString(getClass(), "FileExcludePatternsSettingsPanel.labelInfoRepository.text")); // NOI18N
        labelInfoRepository.setName("labelInfoRepository"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panelRepository.add(labelInfoRepository, gridBagConstraints);

        progressBarUpdateRepository.setName("progressBarUpdateRepository"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(7, 0, 0, 0);
        panelRepository.add(progressBarUpdateRepository, gridBagConstraints);

        panelButtonsRepository.setName("panelButtonsRepository"); // NOI18N
        panelButtonsRepository.setLayout(new java.awt.GridBagLayout());

        buttonCancelUpdateRepository.setText(Bundle.getString(getClass(), "FileExcludePatternsSettingsPanel.buttonCancelUpdateRepository.text")); // NOI18N
        buttonCancelUpdateRepository.setEnabled(false);
        buttonCancelUpdateRepository.setName("buttonCancelUpdateRepository"); // NOI18N
        buttonCancelUpdateRepository.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelUpdateRepositoryActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelButtonsRepository.add(buttonCancelUpdateRepository, gridBagConstraints);

        buttonUpdateRepository.setText(Bundle.getString(getClass(), "FileExcludePatternsSettingsPanel.buttonUpdateRepository.text")); // NOI18N
        buttonUpdateRepository.setName("buttonUpdateRepository"); // NOI18N
        buttonUpdateRepository.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonUpdateRepositoryActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelButtonsRepository.add(buttonUpdateRepository, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        panelRepository.add(panelButtonsRepository, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 5, 5);
        panelFiles.add(panelRepository, gridBagConstraints);

        tabbedPane.addTab(Bundle.getString(getClass(), "FileExcludePatternsSettingsPanel.panelFiles.TabConstraints.tabTitle"), panelFiles); // NOI18N

        panelDirectoryFolder.setName("panelDirectoryFolder"); // NOI18N
        panelDirectoryFolder.setLayout(new java.awt.GridBagLayout());

        labelInfopanelDirectoryFolder.setText(Bundle.getString(getClass(), "FileExcludePatternsSettingsPanel.labelInfopanelDirectoryFolder.text")); // NOI18N
        labelInfopanelDirectoryFolder.setName("labelInfopanelDirectoryFolder"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 5);
        panelDirectoryFolder.add(labelInfopanelDirectoryFolder, gridBagConstraints);

        scrollPanePanelDirectoryFolder.setName("scrollPanePanelDirectoryFolder"); // NOI18N

        panelSelectRootFiles.setName("panelSelectRootFiles"); // NOI18N
        scrollPanePanelDirectoryFolder.setViewportView(panelSelectRootFiles);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 5, 5);
        panelDirectoryFolder.add(scrollPanePanelDirectoryFolder, gridBagConstraints);

        tabbedPane.addTab(Bundle.getString(getClass(), "FileExcludePatternsSettingsPanel.panelDirectoryFolder.TabConstraints.tabTitle"), panelDirectoryFolder); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(tabbedPane, gridBagConstraints);
    }

    private void listValueChanged(javax.swing.event.ListSelectionEvent evt) {
        handleListValueChanged(evt);
    }

    private void textFieldInputPatternKeyReleased(java.awt.event.KeyEvent evt) {
        handleTextFieldInputPatternKeyReleased(evt);
    }

    private void buttonCancelUpdateRepositoryActionPerformed(java.awt.event.ActionEvent evt) {
        cancelUpdateRepository();
    }

    private void buttonDeletePatternActionPerformed(java.awt.event.ActionEvent evt) {
        deletePattern();
    }

    private void buttonUpdateRepositoryActionPerformed(java.awt.event.ActionEvent evt) {
        updateRepository();
    }

    private void buttonInsertPatternActionPerformed(java.awt.event.ActionEvent evt) {
        insertPattern();
    }

    private void listKeyPressed(java.awt.event.KeyEvent evt) {
        handleListKeyPressed(evt);
    }

    private void menuItemDeletePatternActionPerformed(java.awt.event.ActionEvent evt) {
        deletePattern();
    }

    private void textFieldInputPatternFocusGained(java.awt.event.FocusEvent evt) {
        textFieldInputPattern.selectAll();
    }

    private void buttonChooseDirsActionPerformed(java.awt.event.ActionEvent evt) {
        chooseDirPatterns();
    }

    private javax.swing.JButton buttonCancelUpdateRepository;
    private javax.swing.JButton buttonChooseDirs;
    private javax.swing.JButton buttonDeletePattern;
    private javax.swing.JButton buttonInsertPattern;
    private javax.swing.JButton buttonUpdateRepository;
    private javax.swing.JLabel labelInfoChooseDirs;
    private javax.swing.JLabel labelInfoList;
    private javax.swing.JLabel labelInfoRepository;
    private javax.swing.JLabel labelInfopanelDirectoryFolder;
    private javax.swing.JLabel labelTextFieldInputPattern;
    private org.jdesktop.swingx.JXList list;
    private javax.swing.JMenuItem menuItemDeletePattern;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelButtonsRepository;
    private javax.swing.JPanel panelDirectoryFolder;
    private javax.swing.JPanel panelFiles;
    private javax.swing.JPanel panelRepository;
    private SelectRootFilesPanel panelSelectRootFiles;
    private javax.swing.JPopupMenu popupMenu;
    private javax.swing.JProgressBar progressBarUpdateRepository;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JScrollPane scrollPanePanelDirectoryFolder;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTextField textFieldInputPattern;
}
