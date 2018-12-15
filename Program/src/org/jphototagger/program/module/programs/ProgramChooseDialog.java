package org.jphototagger.program.module.programs;

import java.awt.event.KeyEvent;
import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.programs.ProgramType;
import org.jphototagger.lib.swing.DialogExt;
import org.jphototagger.lib.swing.MouseEventUtil;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;

/**
 * @author Elmar Baumann
 */
public class ProgramChooseDialog extends DialogExt {

    private static final long serialVersionUID = 1L;
    private final ProgramsListModel model = new ProgramsListModel(ProgramType.PROGRAM);
    private boolean accepted;
    private Program selectedProgram;

    public ProgramChooseDialog() {
        this(null);
    }

    /**
     * @param selectedProgram maybe null
     */
    public ProgramChooseDialog(Program selectedProgram) {
        super(ComponentUtil.findFrameWithIcon(), true);
        initComponents();
        this.selectedProgram = selectedProgram;
        postInitComponents();
    }

    private void postInitComponents() {
        if (selectedProgram != null) {
            listPrograms.setSelectedValue(selectedProgram, true);
        }
        MnemonicUtil.setMnemonics(this);
    }

    public boolean isAccepted() {
        return accepted;
    }

    public Program getSelectedProgram() {
        return selectedProgram;
    }

    public void setSelectedProgram(Program selectedProgram) {
        this.selectedProgram = selectedProgram;
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        scrollPanePrograms = UiFactory.scrollPane();
        listPrograms = UiFactory.jxList();
        buttonChooseProgram = UiFactory.button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Bundle.getString(getClass(), "ProgramChooseDialog.title")); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        listPrograms.setModel(model);
        listPrograms.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listPrograms.setCellRenderer(new ProgramsListCellRenderer());

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedProgram}"), listPrograms, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        listPrograms.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listProgramsMouseClicked(evt);
            }
        });
        listPrograms.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listProgramsKeyPressed(evt);
            }
        });
        scrollPanePrograms.setViewportView(listPrograms);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(7, 7, 0, 7);
        getContentPane().add(scrollPanePrograms, gridBagConstraints);

        buttonChooseProgram.setText(Bundle.getString(getClass(), "ProgramChooseDialog.buttonChooseProgram.text")); // NOI18N
        buttonChooseProgram.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseProgramActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(7, 7, 7, 7);
        getContentPane().add(buttonChooseProgram, gridBagConstraints);

        bindingGroup.bind();

        pack();
    }

    private void buttonChooseProgramActionPerformed(java.awt.event.ActionEvent evt) {
        accepted = true;
        dispose();
    }

    private void listProgramsMouseClicked(java.awt.event.MouseEvent evt) {
        if (MouseEventUtil.isDoubleClick(evt) && listPrograms.getSelectedIndex() >= 0) {
            setSelectedProgram((Program) listPrograms.getSelectedValue());
            accepted = true;
            dispose();
        }
    }

    private void listProgramsKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER && listPrograms.getSelectedIndex() >= 0) {
            setSelectedProgram((Program) listPrograms.getSelectedValue());
            accepted = true;
            dispose();
        }
    }

    private javax.swing.JButton buttonChooseProgram;
    private org.jdesktop.swingx.JXList listPrograms;
    private javax.swing.JScrollPane scrollPanePrograms;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
}
