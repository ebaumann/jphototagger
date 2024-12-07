package org.jphototagger.program.module.programs;

import java.awt.Container;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesHints;
import org.jphototagger.lib.swing.DialogExt;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class ProgramInputParametersDialog extends DialogExt {

    private static final long serialVersionUID = 1L;
    private boolean accepted = false;

    public ProgramInputParametersDialog() {
        super(GUI.getAppFrame(), true);
        initComponents();
        MnemonicUtil.setMnemonics((Container) this);
    }

    public void setProgram(String program) {
        if (program == null) {
            throw new NullPointerException("program == null");
        }

        labelContextProgram.setText(program);
    }

    public void setFilename(String filename) {
        if (filename == null) {
            throw new NullPointerException("filename == null");
        }

        labelContextFile.setText(filename);
    }

    public boolean isAccepted() {
        return accepted;
    }

    public String getParameters() {
        return textAreaParameter.getText();
    }

    public boolean isParametersBeforeFilename() {
        return radioButtonParametersBeforeFilename.isSelected();
    }

    @Override
    public void setVisible(boolean visible) {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        if (visible) {
            prefs.applyComponentSettings(this, new PreferencesHints(PreferencesHints.Option.SET_TABBED_PANE_CONTENT));
        } else {
            prefs.setComponent(this, new PreferencesHints(PreferencesHints.Option.SET_TABBED_PANE_CONTENT));
        }

        super.setVisible(visible);
    }

    @Override
    protected void escape() {
        cancel();
    }

    private void cancel() {
        accepted = false;
        setVisible(false);
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup = new javax.swing.ButtonGroup();
        panelContent = UiFactory.panel();
        panelContext = UiFactory.panel();
        labelContextProgramPrompt = UiFactory.label();
        labelContextProgram = UiFactory.label();
        labelContextFilePrompt = UiFactory.label();
        labelContextFile = UiFactory.label();
        labelPrompt = UiFactory.label();
        scrollPaneAreaParameter = UiFactory.scrollPane();
        textAreaParameter = UiFactory.textArea();
        radioButtonParametersBeforeFilename = UiFactory.radioButton();
        radioButtonParametersAfterFilename = UiFactory.radioButton();
        panelCancelOk = UiFactory.panel();
        buttonCancel = UiFactory.button();
        buttonOk = UiFactory.button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Bundle.getString(getClass(), "ProgramInputParametersDialog.title")); // NOI18N
        
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panelContent.setName("panelContent"); // NOI18N
        panelContent.setLayout(new java.awt.GridBagLayout());

        panelContext.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString(getClass(), "ProgramInputParametersDialog.panelContext.border.title"))); // NOI18N
        panelContext.setName("panelContext"); // NOI18N
        panelContext.setLayout(new java.awt.GridBagLayout());

        labelContextProgramPrompt.setText(Bundle.getString(getClass(), "ProgramInputParametersDialog.labelContextProgramPrompt.text")); // NOI18N
        labelContextProgramPrompt.setName("labelContextProgramPrompt"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 0);
        panelContext.add(labelContextProgramPrompt, gridBagConstraints);

        labelContextProgram.setText(" "); // NOI18N
        labelContextProgram.setName("labelContextProgram"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 5);
        panelContext.add(labelContextProgram, gridBagConstraints);

        labelContextFilePrompt.setText(Bundle.getString(getClass(), "ProgramInputParametersDialog.labelContextFilePrompt.text")); // NOI18N
        labelContextFilePrompt.setName("labelContextFilePrompt"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 5, 0);
        panelContext.add(labelContextFilePrompt, gridBagConstraints);

        labelContextFile.setText(org.jphototagger.lib.util.Bundle.getString(ProgramInputParametersDialog.class, "ProgramInputParametersDialog.labelContextFile.text")); // NOI18N
        labelContextFile.setName("labelContextFile"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 5, 5);
        panelContext.add(labelContextFile, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panelContent.add(panelContext, gridBagConstraints);

        labelPrompt.setLabelFor(textAreaParameter);
        labelPrompt.setText(Bundle.getString(getClass(), "ProgramInputParametersDialog.labelPrompt.text")); // NOI18N
        labelPrompt.setName("labelPrompt"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(7, 0, 0, 0);
        panelContent.add(labelPrompt, gridBagConstraints);

        scrollPaneAreaParameter.setName("scrollPaneAreaParameter"); // NOI18N
        scrollPaneAreaParameter.setPreferredSize(UiFactory.dimension(300, 100));

        textAreaParameter.setColumns(20);
        textAreaParameter.setRows(2);
        textAreaParameter.setName("textAreaParameter"); // NOI18N
        scrollPaneAreaParameter.setViewportView(textAreaParameter);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        panelContent.add(scrollPaneAreaParameter, gridBagConstraints);

        buttonGroup.add(radioButtonParametersBeforeFilename);
        radioButtonParametersBeforeFilename.setSelected(true);
        radioButtonParametersBeforeFilename.setText(Bundle.getString(getClass(), "ProgramInputParametersDialog.radioButtonParametersBeforeFilename.text")); // NOI18N
        radioButtonParametersBeforeFilename.setName("radioButtonParametersBeforeFilename"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        panelContent.add(radioButtonParametersBeforeFilename, gridBagConstraints);

        buttonGroup.add(radioButtonParametersAfterFilename);
        radioButtonParametersAfterFilename.setText(Bundle.getString(getClass(), "ProgramInputParametersDialog.radioButtonParametersAfterFilename.text")); // NOI18N
        radioButtonParametersAfterFilename.setName("radioButtonParametersAfterFilename"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panelContent.add(radioButtonParametersAfterFilename, gridBagConstraints);

        panelCancelOk.setName("panelCancelOk"); // NOI18N
        panelCancelOk.setLayout(new java.awt.GridBagLayout());

        buttonCancel.setText(Bundle.getString(getClass(), "ProgramInputParametersDialog.buttonCancel.text")); // NOI18N
        buttonCancel.setName("buttonCancel"); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });
        panelCancelOk.add(buttonCancel, new java.awt.GridBagConstraints());

        buttonOk.setText(Bundle.getString(getClass(), "ProgramInputParametersDialog.buttonOk.text")); // NOI18N
        buttonOk.setName("buttonOk"); // NOI18N
        buttonOk.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOkActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelCancelOk.add(buttonOk, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(7, 0, 0, 0);
        panelContent.add(panelCancelOk, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(7, 7, 7, 7);
        getContentPane().add(panelContent, gridBagConstraints);

        pack();
    }

    private void buttonOkActionPerformed(java.awt.event.ActionEvent evt) {
        accepted = true;
        setVisible(false);
    }

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {
        cancel();
    }

    private javax.swing.JButton buttonCancel;
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JButton buttonOk;
    private javax.swing.JLabel labelContextFile;
    private javax.swing.JLabel labelContextFilePrompt;
    private javax.swing.JLabel labelContextProgram;
    private javax.swing.JLabel labelContextProgramPrompt;
    private javax.swing.JLabel labelPrompt;
    private javax.swing.JPanel panelCancelOk;
    private javax.swing.JPanel panelContent;
    private javax.swing.JPanel panelContext;
    private javax.swing.JRadioButton radioButtonParametersAfterFilename;
    private javax.swing.JRadioButton radioButtonParametersBeforeFilename;
    private javax.swing.JScrollPane scrollPaneAreaParameter;
    private javax.swing.JTextArea textAreaParameter;
}
