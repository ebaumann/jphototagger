package org.jphototagger.fileeventhooks;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.DocumentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public class JPhotoTaggerActionsSettingsPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;
    private final FilenameSuffixesListModel filenameSuffixesListModel = new FilenameSuffixesListModel();
    private String selectedFilenameSuffix;

    public JPhotoTaggerActionsSettingsPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        MnemonicUtil.setMnemonics(this);
        textFieldFilenameSuffix.getDocument().addDocumentListener(filenameSuffixDocumentListener);
    }

    private void filenameSuffixTyped(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            addFilenameSuffix();
        }
    }

    private void addFilenameSuffix() {
        String filenameSuffix = textFieldFilenameSuffix.getText().trim();
        if (!filenameSuffix.isEmpty()) {
            filenameSuffixesListModel.addSuffix(filenameSuffix);
            ComponentUtil.parentWindowToFront(this);
        }
    }

    public String getSelectedFilenameSuffix() {
        return selectedFilenameSuffix;
    }

    public void setSelectedFilenameSuffix(String selectedFilenameSuffix) {
        this.selectedFilenameSuffix = selectedFilenameSuffix;
        boolean suffixSelected = StringUtil.hasContent(selectedFilenameSuffix);
        buttonRenameSelectedFilenameSuffix.setEnabled(suffixSelected);
        buttonRemoveSelectedFilenameSuffix.setEnabled(suffixSelected);
    }

    private void removeSelectedFilenameSuffix() {
        if (selectedFilenameSuffix != null
                && MessageDisplayer.confirmYesNo(this, Bundle.getString(JPhotoTaggerActionsSettingsPanel.class, "SettingsPanel.Confirm.RemoveSelectedFilenameSuffix", selectedFilenameSuffix))) {
            filenameSuffixesListModel.removeSuffix(selectedFilenameSuffix);
            ComponentUtil.parentWindowToFront(this);
            textFieldFilenameSuffix.requestFocusInWindow();
        }
    }

    private void renameSelectedFilenameSuffix() {
        if (selectedFilenameSuffix != null) {
            String message = Bundle.getString(JPhotoTaggerActionsSettingsPanel.class, "SettingsPanel.Input.RenameSelectedFilenameSuffix");
            String input = MessageDisplayer.input(message, selectedFilenameSuffix);
            if (StringUtil.hasContent(input)) {
                if (filenameSuffixesListModel.renameSuffix(selectedFilenameSuffix, input)) {
                    selectedFilenameSuffix = input;
                }
                ComponentUtil.parentWindowToFront(this);
                textFieldFilenameSuffix.requestFocusInWindow();
            }
        }
    }

    private void keyInFilenameSuffixesListPressed(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_DELETE:
                removeSelectedFilenameSuffix();
                listFilenameSuffixes.requestFocusInWindow();
                break;
            case KeyEvent.VK_F2:
                renameSelectedFilenameSuffix();
                listFilenameSuffixes.requestFocusInWindow();
                break;
            default: // Do nothing
        }
    }

    private static class FilenameSuffixesListModel extends DefaultListModel<String> {

        private static final long serialVersionUID = 1L;

        private FilenameSuffixesListModel() {
            addElements();
        }

        private void addElements() {
            Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
            if (prefs != null) {
                Set<String> suffixes = new HashSet<>(prefs.getStringCollection(PreferencesKeys.FILENAME_SUFFIXES_KEY));
                for (String suffix : suffixes) {
                    addElement(suffix);
                }
            }
        }

        public void addSuffix(String suffix) {
            if (suffix == null) {
                throw new NullPointerException("suffix == null");
            }
            String trimmedSuffix = suffix.trim();
            if (trimmedSuffix.length() > 1 && trimmedSuffix.startsWith(".")) {
                trimmedSuffix = trimmedSuffix.replaceFirst("[\\.]+", "");
            }
            if (!trimmedSuffix.isEmpty() && !contains(trimmedSuffix) && !"xmp".equalsIgnoreCase(suffix)) {
                addElement(trimmedSuffix);
                updatePreferences();
            }
        }

        public void removeSuffix(String suffix) {
            if (suffix == null) {
                throw new NullPointerException("suffix == null");
            }
            boolean removed = removeElement(suffix);
            if (removed) {
                updatePreferences();
            }
        }

        public boolean renameSuffix(String oldName, String newName) {
            if (oldName == null) {
                throw new NullPointerException("oldName == null");
            }
            if (newName == null) {
                throw new NullPointerException("newName == null");
            }
            if (oldName.equals(newName)) {
                return false;
            }
            int oldNameIndex = indexOf(oldName);
            if (oldNameIndex >= 0) {
                set(oldNameIndex, newName);
                updatePreferences();
                return true;
            }
            return false;
        }

        @SuppressWarnings("unchecked")
        private void updatePreferences() {
            Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
            prefs.setStringCollection(PreferencesKeys.FILENAME_SUFFIXES_KEY, Collections.list(elements()));
        }
    }

    private final ListCellRenderer<?> suffixesListCellRenderer = new DefaultListCellRenderer() {

        private static final long serialVersionUID = 1L;
        private final Icon ICON_FILE = org.jphototagger.resources.Icons.getIcon("icon_file.png");

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setIcon(ICON_FILE);
            return label;
        }
    };

    private final DocumentListener filenameSuffixDocumentListener = new DocumentListener() {

        @Override
        public void insertUpdate(DocumentEvent e) {
            setAddButtonEnabled(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            setAddButtonEnabled(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            setAddButtonEnabled(e);
        }

        private void setAddButtonEnabled(DocumentEvent e) {
            String suffix = DocumentUtil.getText(e);
            buttonAddFilenameSuffix.setEnabled(StringUtil.hasContent(suffix));
        }
    };

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        labelInfoJPhotoTaggerFilenameSuffixes = new org.jdesktop.swingx.JXLabel();
        panelSuffixes = new javax.swing.JPanel();
        panelListFilenameSuffixes = new javax.swing.JPanel();
        scrollPaneListFilenameSuffixes = new javax.swing.JScrollPane();
        listFilenameSuffixes = new org.jdesktop.swingx.JXList();
        panelEditFilenameSuffix = new javax.swing.JPanel();
        labelFilenameSuffix = new javax.swing.JLabel();
        textFieldFilenameSuffix = new javax.swing.JTextField();
        buttonAddFilenameSuffix = org.jphototagger.resources.UiFactory.button();
        labelFilenameSuffixExample = new javax.swing.JLabel();
        panelButtonsFilenameSuffixes = new javax.swing.JPanel();
        buttonRenameSelectedFilenameSuffix = org.jphototagger.resources.UiFactory.button();
        buttonRemoveSelectedFilenameSuffix = org.jphototagger.resources.UiFactory.button();
        labelAttentionJptActions = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        labelInfoJPhotoTaggerFilenameSuffixes.setText(Bundle.getString(getClass(), "JPhotoTaggerActionsSettingsPanel.labelInfoJPhotoTaggerFilenameSuffixes.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(10, 10, 0, 10);
        add(labelInfoJPhotoTaggerFilenameSuffixes, gridBagConstraints);

        panelSuffixes.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString(getClass(), "JPhotoTaggerActionsSettingsPanel.panelSuffixes.border.title"))); // NOI18N
        panelSuffixes.setLayout(new java.awt.GridBagLayout());

        panelListFilenameSuffixes.setLayout(new java.awt.GridBagLayout());

        listFilenameSuffixes.setModel(filenameSuffixesListModel);
        listFilenameSuffixes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listFilenameSuffixes.setCellRenderer(suffixesListCellRenderer);
        listFilenameSuffixes.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedFilenameSuffix}"), listFilenameSuffixes, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        listFilenameSuffixes.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listFilenameSuffixesKeyPressed(evt);
            }
        });
        scrollPaneListFilenameSuffixes.setViewportView(listFilenameSuffixes);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelListFilenameSuffixes.add(scrollPaneListFilenameSuffixes, gridBagConstraints);

        panelEditFilenameSuffix.setLayout(new java.awt.GridBagLayout());

        labelFilenameSuffix.setLabelFor(textFieldFilenameSuffix);
        labelFilenameSuffix.setText(Bundle.getString(getClass(), "JPhotoTaggerActionsSettingsPanel.labelFilenameSuffix.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelEditFilenameSuffix.add(labelFilenameSuffix, gridBagConstraints);

        textFieldFilenameSuffix.setColumns(5);
        textFieldFilenameSuffix.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                textFieldFilenameSuffixKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelEditFilenameSuffix.add(textFieldFilenameSuffix, gridBagConstraints);

        buttonAddFilenameSuffix.setText(Bundle.getString(getClass(), "JPhotoTaggerActionsSettingsPanel.buttonAddFilenameSuffix.text")); // NOI18N
        buttonAddFilenameSuffix.setEnabled(false);
        buttonAddFilenameSuffix.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddFilenameSuffixActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelEditFilenameSuffix.add(buttonAddFilenameSuffix, gridBagConstraints);

        labelFilenameSuffixExample.setText(Bundle.getString(getClass(), "JPhotoTaggerActionsSettingsPanel.labelFilenameSuffixExample.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        panelEditFilenameSuffix.add(labelFilenameSuffixExample, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        panelListFilenameSuffixes.add(panelEditFilenameSuffix, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 5, 0);
        panelSuffixes.add(panelListFilenameSuffixes, gridBagConstraints);

        panelButtonsFilenameSuffixes.setLayout(new java.awt.GridLayout(2, 0, UiFactory.scale(0), UiFactory.scale(5)));

        buttonRenameSelectedFilenameSuffix.setText(Bundle.getString(getClass(), "JPhotoTaggerActionsSettingsPanel.buttonRenameSelectedFilenameSuffix.text")); // NOI18N
        buttonRenameSelectedFilenameSuffix.setEnabled(false);
        buttonRenameSelectedFilenameSuffix.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRenameSelectedFilenameSuffixActionPerformed(evt);
            }
        });
        panelButtonsFilenameSuffixes.add(buttonRenameSelectedFilenameSuffix);

        buttonRemoveSelectedFilenameSuffix.setText(Bundle.getString(getClass(), "JPhotoTaggerActionsSettingsPanel.buttonRemoveSelectedFilenameSuffix.text")); // NOI18N
        buttonRemoveSelectedFilenameSuffix.setEnabled(false);
        buttonRemoveSelectedFilenameSuffix.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveSelectedFilenameSuffixActionPerformed(evt);
            }
        });
        panelButtonsFilenameSuffixes.add(buttonRemoveSelectedFilenameSuffix);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 0, 5);
        panelSuffixes.add(panelButtonsFilenameSuffixes, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(10, 10, 0, 10);
        add(panelSuffixes, gridBagConstraints);

        labelAttentionJptActions.setText(Bundle.getString(getClass(), "JPhotoTaggerActionsSettingsPanel.labelAttentionJptActions.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(10, 10, 10, 10);
        add(labelAttentionJptActions, gridBagConstraints);

        bindingGroup.bind();
    }//GEN-END:initComponents

    private void buttonAddFilenameSuffixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddFilenameSuffixActionPerformed
        addFilenameSuffix();
        textFieldFilenameSuffix.requestFocusInWindow();
    }//GEN-LAST:event_buttonAddFilenameSuffixActionPerformed

    private void buttonRemoveSelectedFilenameSuffixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRemoveSelectedFilenameSuffixActionPerformed
        removeSelectedFilenameSuffix();
        textFieldFilenameSuffix.requestFocusInWindow();
    }//GEN-LAST:event_buttonRemoveSelectedFilenameSuffixActionPerformed

    private void buttonRenameSelectedFilenameSuffixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRenameSelectedFilenameSuffixActionPerformed
        renameSelectedFilenameSuffix();
    }//GEN-LAST:event_buttonRenameSelectedFilenameSuffixActionPerformed

    private void listFilenameSuffixesKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listFilenameSuffixesKeyPressed
        int keyCode = evt.getKeyCode();
        keyInFilenameSuffixesListPressed(keyCode);
    }//GEN-LAST:event_listFilenameSuffixesKeyPressed

    private void textFieldFilenameSuffixKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldFilenameSuffixKeyTyped
        filenameSuffixTyped(evt);
    }//GEN-LAST:event_textFieldFilenameSuffixKeyTyped

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAddFilenameSuffix;
    private javax.swing.JButton buttonRemoveSelectedFilenameSuffix;
    private javax.swing.JButton buttonRenameSelectedFilenameSuffix;
    private javax.swing.JLabel labelAttentionJptActions;
    private javax.swing.JLabel labelFilenameSuffix;
    private javax.swing.JLabel labelFilenameSuffixExample;
    private org.jdesktop.swingx.JXLabel labelInfoJPhotoTaggerFilenameSuffixes;
    private org.jdesktop.swingx.JXList listFilenameSuffixes;
    private javax.swing.JPanel panelButtonsFilenameSuffixes;
    private javax.swing.JPanel panelEditFilenameSuffix;
    private javax.swing.JPanel panelListFilenameSuffixes;
    private javax.swing.JPanel panelSuffixes;
    private javax.swing.JScrollPane scrollPaneListFilenameSuffixes;
    private javax.swing.JTextField textFieldFilenameSuffix;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
