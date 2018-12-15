package org.jphototagger.program.module.programs;

import java.awt.Container;
import java.awt.event.MouseEvent;
import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.programs.ProgramType;
import org.jphototagger.lib.swing.DialogExt;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.resources.UiFactory;

/**
 * DialogExt to select an {@code org.jphototagger.program.data.Program}.
 *
 * @author Elmar Baumann
 */
public class SelectProgramDialog extends DialogExt {

    private static final long serialVersionUID = 1L;
    private final ProgramsListModel model;
    private final ProgramType type;
    private boolean accepted;

    public SelectProgramDialog(ProgramType type) {
        super(GUI.getAppFrame(), true);
        if (type == null) {
            throw new NullPointerException("type == null");
        }
        this.type = type;
        model = new ProgramsListModel(type);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        MnemonicUtil.setMnemonics((Container) this);
        setTitle();
    }

    private void setTitle() {
        String actionsTitle = Bundle.getString(SelectProgramDialog.class, "SelectProgramDialog.Title.Actions"); // NOI18N
        String programsTitle = Bundle.getString(SelectProgramDialog.class, "SelectProgramDialog.Title.Programs"); // NOI18N
        setTitle(type.equals(ProgramType.ACTION) ? actionsTitle : programsTitle);
    }

    /**
     * Returns whether the dialog was closed throug the button with the meaning:
     * Select this program.
     *
     * @return  true if a program was selected
     */
    public boolean isAccepted() {
        return accepted;
    }

    /**
     * Returns the selected program.
     *
     * @return program or null if no program was selected. You can determine
     *         whether an program was selected through {@code #isAccepted()}.
     */
    public Program getSelectedProgram() {
        Program program  = null;
        int selectedIndex = listPrograms.getSelectedIndex();
        if (selectedIndex >= 0) {
            int modelIndex = listPrograms.convertIndexToModel(selectedIndex);
            program = (Program) model.get(modelIndex);
        }
        return program;
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            accepted = false;
        }
        super.setVisible(visible);
    }

    private void handleMousClicked(MouseEvent evt) {
        int selectedIndex = listPrograms.getSelectedIndex();
        boolean isSelected = selectedIndex >= 0;
        if ((evt.getClickCount() >= 2) && isSelected) {
            handleButtonSelectAction();
        }
        buttonSelect.setEnabled(isSelected);
    }

    private void handleButtonSelectAction() {
        accepted = true;
        setVisible(false);
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panelContent = UiFactory.panel();
        scrollPanePrograms = UiFactory.scrollPane();
        listPrograms = UiFactory.jxList();
        buttonSelect = UiFactory.button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panelContent.setName("panelContent"); // NOI18N
        panelContent.setLayout(new java.awt.GridBagLayout());

        scrollPanePrograms.setFocusable(false);
        scrollPanePrograms.setName("scrollPanePrograms"); // NOI18N

        listPrograms.setModel(model);
        listPrograms.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listPrograms.setCellRenderer(type.equals(ProgramType.ACTION) ? new org.jphototagger.program.module.actions.ActionsListCellRenderer() : new org.jphototagger.program.module.programs.ProgramsListCellRenderer());
        listPrograms.setName("listPrograms"); // NOI18N
        listPrograms.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listProgramsMouseClicked(evt);
            }
        });
        scrollPanePrograms.setViewportView(listPrograms);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelContent.add(scrollPanePrograms, gridBagConstraints);

        buttonSelect.setText(Bundle.getString(getClass(), "SelectProgramDialog.buttonSelect.text")); // NOI18N
        buttonSelect.setEnabled(false);
        buttonSelect.setName("buttonSelect"); // NOI18N
        buttonSelect.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSelectActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(7, 0, 0, 0);
        panelContent.add(buttonSelect, gridBagConstraints);

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

    private void listProgramsMouseClicked(java.awt.event.MouseEvent evt) {
        handleMousClicked(evt);
    }

    private void buttonSelectActionPerformed(java.awt.event.ActionEvent evt) {
        handleButtonSelectAction();
    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {
        setVisible(false);
    }

    private javax.swing.JButton buttonSelect;
    private org.jdesktop.swingx.JXList listPrograms;
    private javax.swing.JPanel panelContent;
    private javax.swing.JScrollPane scrollPanePrograms;
}
