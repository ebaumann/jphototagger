package org.jphototagger.program.module.programs;

import java.awt.event.KeyEvent;
import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.programs.ProgramType;
import org.jphototagger.lib.swing.Dialog;
import org.jphototagger.lib.swing.MouseEventUtil;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;

/**
 * @author Elmar Baumann
 */
public class ProgramChooseDialog extends Dialog {

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

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        scrollPanePrograms = new javax.swing.JScrollPane();
        listPrograms = new org.jdesktop.swingx.JXList();
        buttonChooseProgram = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/module/programs/Bundle"); // NOI18N
        setTitle(bundle.getString("ProgramChooseDialog.title")); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        listPrograms.setModel(model);
        listPrograms.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listPrograms.setCellRenderer(new ProgramsListCellRenderer());

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedProgram}"), listPrograms, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        listPrograms.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listProgramsMouseClicked(evt);
            }
        });
        listPrograms.addKeyListener(new java.awt.event.KeyAdapter() {
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
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 0, 7);
        getContentPane().add(scrollPanePrograms, gridBagConstraints);

        buttonChooseProgram.setText(bundle.getString("ProgramChooseDialog.buttonChooseProgram.text")); // NOI18N
        buttonChooseProgram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseProgramActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        getContentPane().add(buttonChooseProgram, gridBagConstraints);

        bindingGroup.bind();

        pack();
    }//GEN-END:initComponents

    private void buttonChooseProgramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseProgramActionPerformed
        accepted = true;
        dispose();
    }//GEN-LAST:event_buttonChooseProgramActionPerformed

    private void listProgramsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listProgramsMouseClicked
        if (MouseEventUtil.isDoubleClick(evt) && listPrograms.getSelectedIndex() >= 0) {
            setSelectedProgram((Program) listPrograms.getSelectedValue());
            accepted = true;
            dispose();
        }
    }//GEN-LAST:event_listProgramsMouseClicked

    private void listProgramsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listProgramsKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER && listPrograms.getSelectedIndex() >= 0) {
            setSelectedProgram((Program) listPrograms.getSelectedValue());
            accepted = true;
            dispose();
        }
    }//GEN-LAST:event_listProgramsKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonChooseProgram;
    private org.jdesktop.swingx.JXList listPrograms;
    private javax.swing.JScrollPane scrollPanePrograms;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
