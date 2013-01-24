package org.jphototagger.program.module.programs;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileSystemView;
import org.jphototagger.domain.filetypes.UserDefinedFileType;
import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.repository.ProgramsRepository;
import org.jphototagger.domain.repository.UserDefinedFileTypesRepository;
import org.jphototagger.domain.thumbnails.ThumbnailCreator;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.MouseEventUtil;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public class EditDefaultProgramsPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;
    private final ProgramsRepository programsRepository = Lookup.getDefault().lookup(ProgramsRepository.class);
    private static final FileSystemView FILE_SYSTEM_VIEW = FileSystemView.getFileSystemView();
    private String selectedFilenameSuffix;
    private String selectedProgramName;
    private Icon selectedProgramIcon;

    public EditDefaultProgramsPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        MnemonicUtil.setMnemonics(this);
    }

    public String getSelectedFilenameSuffix() {
        return selectedFilenameSuffix;
    }

    public void setSelectedFilenameSuffix(String selectedFilenameExtension) {
        Object old = selectedFilenameExtension;
        this.selectedFilenameSuffix = selectedFilenameExtension;
        setSelectedProgramName();
        setButtonsEnabled();
        firePropertyChange("selectedFilenameExtension", old, this.selectedFilenameSuffix);
    }

    public String getSelectedProgramName() {
        return selectedProgramName;
    }

    public Icon getSelectedProgramIcon() {
        return selectedProgramIcon;
    }

    private void setSelectedProgramName() {
        Object oldSelectedProgramName = selectedProgramName;
        Object oldSelectedProgramIcon = selectedProgramIcon;
        if (StringUtil.hasContent(selectedFilenameSuffix) && programsRepository != null) {
            Program program = programsRepository.findDefaultProgram(selectedFilenameSuffix);
            selectedProgramName = program == null
                    ? null
                    : program.getAlias();
            selectedProgramIcon = program == null ? null : findIcon(program.getFile());
            } else {
            selectedProgramName = null;
            selectedProgramIcon = null;
        }
        setButtonsEnabled();
        firePropertyChange("selectedProgramName", oldSelectedProgramName, selectedProgramName);
        firePropertyChange("selectedProgramIcon", oldSelectedProgramIcon, selectedProgramIcon);
    }

    private Icon findIcon(File file) {
        synchronized (FILE_SYSTEM_VIEW) {
            try {
                return FILE_SYSTEM_VIEW.getSystemIcon(file);
            } catch (Exception ex) {
                Logger.getLogger(EditDefaultProgramsPanel.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
    }

    private void setButtonsEnabled() {
        boolean filenameSuffixSelected = StringUtil.hasContent(selectedFilenameSuffix);
        boolean programNameSelected = StringUtil.hasContent(selectedProgramName);
        buttonRemoveDefaultProgram.setEnabled(programNameSelected);
        buttonSetDefaultProgram.setEnabled(filenameSuffixSelected);
    }

    private void setDefaultProgram() {
        if (!StringUtil.hasContent(selectedFilenameSuffix)) {
            return;
        }
        Program program = chooseProgram();
        ComponentUtil.parentWindowToFront(this);
        if (program != null) {
            if (programsRepository.setDefaultProgram(selectedFilenameSuffix, program.getId())) {
                setSelectedProgramName();
            } else {
                String message = Bundle.getString(EditDefaultProgramsPanel.class, "EditDefaultProgramsPanel.Error.SetDefaultProgram",
                        selectedFilenameSuffix);
                MessageDisplayer.error(this, message);
            }
        }
    }

    private Program chooseProgram() {
        Program selectedProgram = selectedFilenameSuffix == null
                ? null
                : programsRepository.findDefaultProgram(selectedFilenameSuffix);
        ProgramChooseDialog dialog = new ProgramChooseDialog(selectedProgram);
        dialog.setVisible(true);
        return dialog.isAccepted()
                ? dialog.getSelectedProgram()
                : null;
    }

    private void removeDefaultProgram() {
        if (!StringUtil.hasContent(selectedFilenameSuffix)) {
            return;
        }
        String message = Bundle.getString(EditDefaultProgramsPanel.class, "EditDefaultProgramsPanel.Confirm.RemoveProgram",
                selectedFilenameSuffix);
        if (MessageDisplayer.confirmYesNo(this, message)) {
            if (programsRepository.removeDefaultProgram(selectedFilenameSuffix)) {
                setSelectedProgramName();
            } else {
                message = Bundle.getString(EditDefaultProgramsPanel.class, "EditDefaultProgramsPanel.Error.RemoveDefaultProgram",
                        selectedFilenameSuffix);
                MessageDisplayer.error(this, message);
            }
        }
    }

    private static class FilenameSuffixesListModel extends DefaultListModel<Object> {

        private static final long serialVersionUID = 1L;

        private FilenameSuffixesListModel() {
            addElements();
        }

        private void addElements() {
            Collection<? extends ThumbnailCreator> tnCreators = Lookup.getDefault().lookupAll(ThumbnailCreator.class);
            UserDefinedFileTypesRepository udfRepo = Lookup.getDefault().lookup(UserDefinedFileTypesRepository.class);
            if (tnCreators == null || udfRepo == null) {
                return;
            }
            List<String> filenameSuffixes = new LinkedList<>();
            List<UserDefinedFileType> fileTypes = udfRepo.findAllUserDefinedFileTypes();
            for (UserDefinedFileType fileType : fileTypes) {
                filenameSuffixes.add(fileType.getSuffix());
            }
            for (ThumbnailCreator tnCreator : tnCreators) {
                for (String filenameSuffix : tnCreator.getAllSupportedFileTypeSuffixes()) {
                    if (!filenameSuffixes.contains(filenameSuffix)) {
                        filenameSuffixes.add(filenameSuffix);
                    }
                }
            }
            Collections.sort(filenameSuffixes);
            for (String suffix : filenameSuffixes) {
                addElement(suffix);
            }
        }
    }

    private final ListCellRenderer<String> suffixProgramListCellRenderer = new ListCellRenderer<String>() {

        private final DefaultListCellRenderer delegate = new DefaultListCellRenderer();

        @Override
        public Component getListCellRendererComponent(JList<? extends String> list, String filesuffix, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) delegate.getListCellRendererComponent(list, filesuffix, index, isSelected, cellHasFocus);
            Program program = null;
            if (programsRepository != null) {
                program = programsRepository.findDefaultProgram(filesuffix);
            }
            label.setText(program == null ? filesuffix : filesuffix + " - " + program.getAlias());
            label.setHorizontalTextPosition(SwingConstants.LEFT);
            label.setIcon(program == null ? null : findIcon(program.getFile()));
            return label;
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

        labelListFilenameSuffixes = new javax.swing.JLabel();
        scrollPaneFilenameSuffixes = new javax.swing.JScrollPane();
        listFilenameSuffixes = new org.jdesktop.swingx.JXList();
        panelDefaultProgram = new javax.swing.JPanel();
        labelInfoDefaultProgram = new javax.swing.JLabel();
        labelDefaultProgram = new org.jdesktop.swingx.JXLabel();
        buttonRemoveDefaultProgram = new javax.swing.JButton();
        buttonSetDefaultProgram = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        labelListFilenameSuffixes.setLabelFor(listFilenameSuffixes);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/module/programs/Bundle"); // NOI18N
        labelListFilenameSuffixes.setText(bundle.getString("EditDefaultProgramsPanel.labelListFilenameSuffixes.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(labelListFilenameSuffixes, gridBagConstraints);

        listFilenameSuffixes.setModel(new FilenameSuffixesListModel());
        listFilenameSuffixes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listFilenameSuffixes.setCellRenderer(suffixProgramListCellRenderer);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedFilenameSuffix}"), listFilenameSuffixes, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        listFilenameSuffixes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listFilenameSuffixesMouseClicked(evt);
            }
        });
        listFilenameSuffixes.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listFilenameSuffixesKeyPressed(evt);
            }
        });
        scrollPaneFilenameSuffixes.setViewportView(listFilenameSuffixes);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(scrollPaneFilenameSuffixes, gridBagConstraints);

        panelDefaultProgram.setLayout(new java.awt.GridBagLayout());

        labelInfoDefaultProgram.setLabelFor(buttonSetDefaultProgram);
        labelInfoDefaultProgram.setText(bundle.getString("EditDefaultProgramsPanel.labelInfoDefaultProgram.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panelDefaultProgram.add(labelInfoDefaultProgram, gridBagConstraints);

        labelDefaultProgram.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedProgramIcon}"), labelDefaultProgram, org.jdesktop.beansbinding.BeanProperty.create("icon"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedProgramName}"), labelDefaultProgram, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        panelDefaultProgram.add(labelDefaultProgram, gridBagConstraints);

        buttonRemoveDefaultProgram.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/module/programs/delete.png"))); // NOI18N
        buttonRemoveDefaultProgram.setToolTipText(bundle.getString("EditDefaultProgramsPanel.buttonRemoveDefaultProgram.toolTipText")); // NOI18N
        buttonRemoveDefaultProgram.setEnabled(false);
        buttonRemoveDefaultProgram.setMargin(new java.awt.Insets(0, 0, 0, 0));
        buttonRemoveDefaultProgram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveDefaultProgramActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panelDefaultProgram.add(buttonRemoveDefaultProgram, gridBagConstraints);

        buttonSetDefaultProgram.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/module/programs/edit.png"))); // NOI18N
        buttonSetDefaultProgram.setToolTipText(bundle.getString("EditDefaultProgramsPanel.buttonSetDefaultProgram.toolTipText")); // NOI18N
        buttonSetDefaultProgram.setMargin(new java.awt.Insets(0, 0, 0, 0));
        buttonSetDefaultProgram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSetDefaultProgramActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panelDefaultProgram.add(buttonSetDefaultProgram, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(panelDefaultProgram, gridBagConstraints);

        bindingGroup.bind();
    }//GEN-END:initComponents

    private void buttonSetDefaultProgramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSetDefaultProgramActionPerformed
        setDefaultProgram();
    }//GEN-LAST:event_buttonSetDefaultProgramActionPerformed

    private void buttonRemoveDefaultProgramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRemoveDefaultProgramActionPerformed
        removeDefaultProgram();
    }//GEN-LAST:event_buttonRemoveDefaultProgramActionPerformed

    private void listFilenameSuffixesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listFilenameSuffixesMouseClicked
        if (MouseEventUtil.isDoubleClick(evt) && listFilenameSuffixes.getSelectedIndex() >= 0) {
            setSelectedFilenameSuffix((String) listFilenameSuffixes.getSelectedValue());
            setDefaultProgram();
        }
    }//GEN-LAST:event_listFilenameSuffixesMouseClicked

    private void listFilenameSuffixesKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listFilenameSuffixesKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER && listFilenameSuffixes.getSelectedIndex() >= 0) {
            setSelectedFilenameSuffix((String) listFilenameSuffixes.getSelectedValue());
            setDefaultProgram();
        } else if (evt.getKeyCode() == KeyEvent.VK_DELETE && listFilenameSuffixes.getSelectedIndex() >= 0) {
            setSelectedFilenameSuffix((String) listFilenameSuffixes.getSelectedValue());
            removeDefaultProgram();
        }
    }//GEN-LAST:event_listFilenameSuffixesKeyPressed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonRemoveDefaultProgram;
    private javax.swing.JButton buttonSetDefaultProgram;
    private org.jdesktop.swingx.JXLabel labelDefaultProgram;
    private javax.swing.JLabel labelInfoDefaultProgram;
    private javax.swing.JLabel labelListFilenameSuffixes;
    private org.jdesktop.swingx.JXList listFilenameSuffixes;
    private javax.swing.JPanel panelDefaultProgram;
    private javax.swing.JScrollPane scrollPaneFilenameSuffixes;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
