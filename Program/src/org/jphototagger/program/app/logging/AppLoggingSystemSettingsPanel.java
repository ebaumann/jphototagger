package org.jphototagger.program.app.logging;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.windows.OptionPageProvider;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.MouseEventUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = OptionPageProvider.class)
public class AppLoggingSystemSettingsPanel extends javax.swing.JPanel implements OptionPageProvider {

    private static final long serialVersionUID = 1L;
    private final Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
    private final DefaultListModel<String> errorTextsListModel = new DefaultListModel<>();

    public AppLoggingSystemSettingsPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        initErrorTextsListModel();
        comboBoxLogLevel.setSelectedItem(AppLoggingSystem.getLogLevel());
        textFieldErrorText.getDocument().addDocumentListener(addErrorTextListener);
        textFieldErrorText.addKeyListener(addErrorTextKeyListener);
        listErrorTexts.addListSelectionListener(errorTextSelectionListener);
        listErrorTexts.addMouseListener(editErrorTextMouseListener);
        setAddErrorTextButtonEnabled();
        setRemoveErrorTextButtonEnabled();
        setEditErrorTextButtonEnabled();
    }

    private void initErrorTextsListModel() {
        if (prefs != null) {
            for (String text : prefs.getStringCollection(ErrorLogHandler.PREF_KEY_ERROR_TEXTS_NOT_IN_GUI)) {
                errorTextsListModel.addElement(text);
            }
        }
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public String getTitle() {
        return Bundle.getString(AppLoggingSystem.class, "AppLoggingSystemSettingsPanel.Title");
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public boolean isMiscOptionPage() {
        return true;
    }

    @Override
    public int getPosition() {
        return 1000;
    }

    private final DocumentListener addErrorTextListener = new DocumentListener() {

        @Override
        public void insertUpdate(DocumentEvent e) {
            setAddErrorTextButtonEnabled();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            setAddErrorTextButtonEnabled();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            setAddErrorTextButtonEnabled();
        }
    };

    private final ListSelectionListener errorTextSelectionListener = new ListSelectionListener() {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                setRemoveErrorTextButtonEnabled();
                setEditErrorTextButtonEnabled();
            }
        }
    };

    private void setRemoveErrorTextButtonEnabled() {
        boolean textSelected = listErrorTexts.getSelectedIndex() >= 0;
        buttonRemoveErrorText.setEnabled(textSelected);
    }

    private void setEditErrorTextButtonEnabled() {
        boolean textSelected = listErrorTexts.getSelectedIndex() >= 0;
        buttonEditErrorText.setEnabled(textSelected);
    }

    private void setAddErrorTextButtonEnabled() {
        String errorText = textFieldErrorText.getText().trim();
        boolean hasErrorText = !errorText.isEmpty();
        buttonAddErrorText.setEnabled(hasErrorText && !containsErrorText(errorText));
    }

    private boolean containsErrorText(String errorText) {
        for (Enumeration<String> e = errorTextsListModel.elements(); e.hasMoreElements(); ) {
            if (e.nextElement().contains(errorText)) {
                return true;
            }
        }
        return false;
    }

    private void addErrorText() {
        String errorText = textFieldErrorText.getText().trim();
        if (StringUtil.hasContent(errorText) && !containsErrorText(errorText)) {
            errorTextsListModel.addElement(errorText);
            prefs.setStringCollection(ErrorLogHandler.PREF_KEY_ERROR_TEXTS_NOT_IN_GUI, getErrorTexts());
        }
    }

    private void removeErrorText() {
        List<String> selectedErrorTexts = listErrorTexts.getSelectedValuesList();
        for (String errorText : selectedErrorTexts) {
            errorTextsListModel.removeElement(errorText);
        }
        prefs.setStringCollection(ErrorLogHandler.PREF_KEY_ERROR_TEXTS_NOT_IN_GUI, getErrorTexts());
    }

    private List<String> getErrorTexts() {
        List<String> errorTexts = new ArrayList<>(errorTextsListModel.getSize());
        for (Enumeration<String> e = errorTextsListModel.elements(); e.hasMoreElements(); ) {
            errorTexts.add(e.nextElement());
        }
        return errorTexts;
    }

    private void editErrorText() {
        for (int selIndex : listErrorTexts.getSelectedIndices()) {
            String errorText = errorTextsListModel.get(selIndex);
            String input = MessageDisplayer.input(
                                   Bundle.getString(AppLoggingSystemSettingsPanel.class, "AppLoggingSystemSettingsPanel.InputErrorTextPrompt"),
                                   errorText);
            if (StringUtil.hasContent(input)) {
                errorTextsListModel.set(selIndex, input);
                prefs.setStringCollection(ErrorLogHandler.PREF_KEY_ERROR_TEXTS_NOT_IN_GUI, getErrorTexts());
            }
        }
    }

    private final MouseListener editErrorTextMouseListener = new MouseAdapter() {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (MouseEventUtil.isDoubleClick(e)) {
                editErrorText();
            }
        }
    };

    private final KeyListener addErrorTextKeyListener = new KeyAdapter() {

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                addErrorText();
            }
        }
    };

    private void persistLogLevel() {
        Level logLevel = (Level) comboBoxLogLevel.getSelectedItem();
        prefs.setString(Preferences.KEY_LOG_LEVEL, logLevel.getName());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        tabbedPane = org.jphototagger.resources.UiFactory.tabbedPane();
        panelMisc = new javax.swing.JPanel();
        panelMiscContent = new javax.swing.JPanel();
        panelLogLevel = new javax.swing.JPanel();
        labelLogLevel = org.jphototagger.resources.UiFactory.label();
        comboBoxLogLevel = new javax.swing.JComboBox<>();
        panelFillMisc = new javax.swing.JPanel();
        panelIgnoreMessages = new javax.swing.JPanel();
        panelIgnoreMessagesContent = new javax.swing.JPanel();
        labelInfoErrorTexts = new org.jdesktop.swingx.JXLabel();
        panelErrorTexts = new javax.swing.JPanel();
        scrollPaneErrorTexts = org.jphototagger.resources.UiFactory.scrollPane();
        listErrorTexts = new javax.swing.JList<>();
        buttonRemoveErrorText = org.jphototagger.resources.UiFactory.button();
        buttonEditErrorText = org.jphototagger.resources.UiFactory.button();
        panelAddErrorText = new javax.swing.JPanel();
        labelErrorText = org.jphototagger.resources.UiFactory.label();
        textFieldErrorText = org.jphototagger.resources.UiFactory.textField();
        buttonAddErrorText = org.jphototagger.resources.UiFactory.button();

        setLayout(new java.awt.GridBagLayout());

        panelMisc.setLayout(new java.awt.GridBagLayout());

        panelMiscContent.setLayout(new java.awt.GridBagLayout());

        panelLogLevel.setLayout(new java.awt.GridBagLayout());

        labelLogLevel.setText(Bundle.getString(getClass(), "AppLoggingSystemSettingsPanel.labelLogLevel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 10, 0, 10);
        panelLogLevel.add(labelLogLevel, gridBagConstraints);

        comboBoxLogLevel.setModel(new org.jphototagger.program.app.logging.LogLevelComboBoxModel());
        comboBoxLogLevel.setRenderer(org.jphototagger.program.app.logging.LogLevelComboBoxModel.createRenderer());
        comboBoxLogLevel.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxLogLevelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelLogLevel.add(comboBoxLogLevel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panelMiscContent.add(panelLogLevel, gridBagConstraints);

        panelFillMisc.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        panelMiscContent.add(panelFillMisc, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(10, 10, 10, 10);
        panelMisc.add(panelMiscContent, gridBagConstraints);

        tabbedPane.addTab(Bundle.getString(getClass(), "AppLoggingSystemSettingsPanel.panelMisc.TabConstraints.tabTitle"), panelMisc); // NOI18N

        panelIgnoreMessages.setLayout(new java.awt.GridBagLayout());

        panelIgnoreMessagesContent.setLayout(new java.awt.GridBagLayout());

        labelInfoErrorTexts.setText(Bundle.getString(getClass(), "AppLoggingSystemSettingsPanel.labelInfoErrorTexts.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelIgnoreMessagesContent.add(labelInfoErrorTexts, gridBagConstraints);

        panelErrorTexts.setLayout(new java.awt.GridBagLayout());

        listErrorTexts.setModel(errorTextsListModel);
        scrollPaneErrorTexts.setViewportView(listErrorTexts);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelErrorTexts.add(scrollPaneErrorTexts, gridBagConstraints);

        buttonRemoveErrorText.setText("-"); // NOI18N
        buttonRemoveErrorText.setToolTipText(Bundle.getString(getClass(), "AppLoggingSystemSettingsPanel.buttonRemoveErrorText.toolTipText")); // NOI18N
        buttonRemoveErrorText.setEnabled(false);
        buttonRemoveErrorText.setMargin(org.jphototagger.resources.UiFactory.insets(2, 2, 2, 2));
        buttonRemoveErrorText.setPreferredSize(org.jphototagger.resources.UiFactory.dimension(22, 22));
        buttonRemoveErrorText.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveErrorTextActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelErrorTexts.add(buttonRemoveErrorText, gridBagConstraints);

        buttonEditErrorText.setIcon(org.jphototagger.resources.Icons.getIcon("icon_edit.png"));
        buttonEditErrorText.setToolTipText(Bundle.getString(getClass(), "AppLoggingSystemSettingsPanel.buttonEditErrorText.toolTipText")); // NOI18N
        buttonEditErrorText.setEnabled(false);
        buttonEditErrorText.setMargin(org.jphototagger.resources.UiFactory.insets(2, 2, 2, 2));
        buttonEditErrorText.setPreferredSize(org.jphototagger.resources.UiFactory.dimension(22, 22));
        buttonEditErrorText.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEditErrorTextActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 0, 0);
        panelErrorTexts.add(buttonEditErrorText, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        panelIgnoreMessagesContent.add(panelErrorTexts, gridBagConstraints);

        panelAddErrorText.setLayout(new java.awt.GridBagLayout());

        labelErrorText.setText(Bundle.getString(getClass(), "AppLoggingSystemSettingsPanel.labelErrorText.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelAddErrorText.add(labelErrorText, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelAddErrorText.add(textFieldErrorText, gridBagConstraints);

        buttonAddErrorText.setText("+"); // NOI18N
        buttonAddErrorText.setToolTipText(Bundle.getString(getClass(), "AppLoggingSystemSettingsPanel.buttonAddErrorText.toolTipText")); // NOI18N
        buttonAddErrorText.setEnabled(false);
        buttonAddErrorText.setMargin(org.jphototagger.resources.UiFactory.insets(2, 2, 2, 2));
        buttonAddErrorText.setPreferredSize(org.jphototagger.resources.UiFactory.dimension(22, 22));
        buttonAddErrorText.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddErrorTextActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelAddErrorText.add(buttonAddErrorText, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        panelIgnoreMessagesContent.add(panelAddErrorText, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(10, 10, 10, 10);
        panelIgnoreMessages.add(panelIgnoreMessagesContent, gridBagConstraints);

        tabbedPane.addTab(Bundle.getString(getClass(), "AppLoggingSystemSettingsPanel.panelIgnoreMessages.TabConstraints.tabTitle"), panelIgnoreMessages); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(tabbedPane, gridBagConstraints);
    }//GEN-END:initComponents

    private void buttonAddErrorTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddErrorTextActionPerformed
        addErrorText();
    }//GEN-LAST:event_buttonAddErrorTextActionPerformed

    private void buttonRemoveErrorTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRemoveErrorTextActionPerformed
        removeErrorText();
    }//GEN-LAST:event_buttonRemoveErrorTextActionPerformed

    private void buttonEditErrorTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonEditErrorTextActionPerformed
        editErrorText();
    }//GEN-LAST:event_buttonEditErrorTextActionPerformed

    private void comboBoxLogLevelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxLogLevelActionPerformed
        persistLogLevel();
    }//GEN-LAST:event_comboBoxLogLevelActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAddErrorText;
    private javax.swing.JButton buttonEditErrorText;
    private javax.swing.JButton buttonRemoveErrorText;
    private javax.swing.JComboBox<Object> comboBoxLogLevel;
    private javax.swing.JLabel labelErrorText;
    private org.jdesktop.swingx.JXLabel labelInfoErrorTexts;
    private javax.swing.JLabel labelLogLevel;
    private javax.swing.JList<String> listErrorTexts;
    private javax.swing.JPanel panelAddErrorText;
    private javax.swing.JPanel panelErrorTexts;
    private javax.swing.JPanel panelFillMisc;
    private javax.swing.JPanel panelIgnoreMessages;
    private javax.swing.JPanel panelIgnoreMessagesContent;
    private javax.swing.JPanel panelLogLevel;
    private javax.swing.JPanel panelMisc;
    private javax.swing.JPanel panelMiscContent;
    private javax.swing.JScrollPane scrollPaneErrorTexts;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTextField textFieldErrorText;
    // End of variables declaration//GEN-END:variables
}
