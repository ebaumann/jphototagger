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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileSystemView;
import org.jphototagger.domain.filetypes.UserDefinedFileType;
import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.repository.ProgramsRepository;
import org.jphototagger.domain.repository.UserDefinedFileTypesRepository;
import org.jphototagger.domain.thumbnails.ThumbnailCreator;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.MouseEventUtil;
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public class EditDefaultProgramsPanel extends PanelExt {

    private static final long serialVersionUID = 1L;
    private static final FileSystemView FILE_SYSTEM_VIEW = FileSystemView.getFileSystemView();
    private final ProgramsRepository programsRepository = Lookup.getDefault().lookup(ProgramsRepository.class);

    public EditDefaultProgramsPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        listFilenameSuffixes.addListSelectionListener(suffixesListListener);
        MnemonicUtil.setMnemonics(this);
    }

    private void setButtonsEnabled() {
        buttonRemoveDefaultPrograms.setEnabled(allSelectedHavingDefaultProgram());
        boolean filenameSuffixSelected = listFilenameSuffixes.getSelectedIndex() >= 0;
        buttonSetDefaultPrograms.setEnabled(filenameSuffixSelected);
    }

    private boolean allSelectedHavingDefaultProgram() {
        final List<String> selectedFilenameSuffixes = listFilenameSuffixes.getSelectedValuesList();
        for (String filenameSuffix : selectedFilenameSuffixes) {
            if (!programsRepository.existsDefaultProgram(filenameSuffix)) {
                return false;
            }
        }
        return !selectedFilenameSuffixes.isEmpty();
    }

    private void setDefaultPrograms() {
        List<String> selectedFilenameSuffixes = listFilenameSuffixes.getSelectedValuesList();
        if (selectedFilenameSuffixes.isEmpty()) {
            return;
        }
        Program program = chooseProgram(selectedFilenameSuffixes.size() == 1 ? selectedFilenameSuffixes.get(0) : null);
        ComponentUtil.parentWindowToFront(this);
        if (program == null) {
            return;
        }
        for (String filenameSuffix : selectedFilenameSuffixes) { // suffixes are distinct
            if (!programsRepository.setDefaultProgram(filenameSuffix, program.getId())) {
                String message = Bundle.getString(EditDefaultProgramsPanel.class, "EditDefaultProgramsPanel.Error.SetDefaultProgram",
                        filenameSuffix);
                MessageDisplayer.error(this, message);
            }
        }
        ComponentUtil.forceRepaint(listFilenameSuffixes);
        setButtonsEnabled();
    }

    private Program chooseProgram(String filenameSuffix) {
        ProgramChooseDialog dialog = new ProgramChooseDialog(filenameSuffix == null ? null : programsRepository.findDefaultProgram(filenameSuffix));
        dialog.setVisible(true);
        return dialog.isAccepted()
                ? dialog.getSelectedProgram()
                : null;
    }

    private void removeDefaultPrograms() {
        List<String> selectedFilenameSuffixes = listFilenameSuffixes.getSelectedValuesList();
        if (selectedFilenameSuffixes.isEmpty()) {
            return;
        }
        String message = Bundle.getString(EditDefaultProgramsPanel.class, "EditDefaultProgramsPanel.Confirm.RemoveProgram");
        if (MessageDisplayer.confirmYesNo(this, message)) {
            for (String filenameSuffix : selectedFilenameSuffixes) {
                if (!programsRepository.removeDefaultProgram(filenameSuffix)) {
                    String errorMessage = Bundle.getString(EditDefaultProgramsPanel.class, "EditDefaultProgramsPanel.Error.RemoveDefaultProgram",
                            filenameSuffix);
                    MessageDisplayer.error(this, errorMessage);
                }
            }
        }
        ComponentUtil.forceRepaint(listFilenameSuffixes);
        setButtonsEnabled();
    }

    private static class FilenameSuffixesListModel extends DefaultListModel<String> {

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

        private Icon findIcon(File file) {
            synchronized (FILE_SYSTEM_VIEW) {
                try {
                    return FILE_SYSTEM_VIEW.getSystemIcon(file);
                } catch (Throwable t){
                    Logger.getLogger(EditDefaultProgramsPanel.class.getName()).log(Level.SEVERE, null, t);
                    return null;
                }
            }
        }
    };

    private final ListSelectionListener suffixesListListener = new ListSelectionListener() {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                setButtonsEnabled();
            }
        }
    };

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        labelListFilenameSuffixes = UiFactory.label();
        panelButtons = UiFactory.panel();
        buttonRemoveDefaultPrograms = UiFactory.button();
        buttonSetDefaultPrograms = UiFactory.button();
        scrollPaneFilenameSuffixes = UiFactory.scrollPane();
        listFilenameSuffixes = UiFactory.list();

        setLayout(new java.awt.GridBagLayout());

        labelListFilenameSuffixes.setLabelFor(listFilenameSuffixes);
        labelListFilenameSuffixes.setText(Bundle.getString(getClass(), "EditDefaultProgramsPanel.labelListFilenameSuffixes.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(labelListFilenameSuffixes, gridBagConstraints);

        panelButtons.setLayout(new java.awt.GridBagLayout());

        buttonRemoveDefaultPrograms.setIcon(org.jphototagger.resources.Icons.getIcon("icon_delete.png"));
        buttonRemoveDefaultPrograms.setToolTipText(Bundle.getString(getClass(), "EditDefaultProgramsPanel.buttonRemoveDefaultPrograms.toolTipText")); // NOI18N
        buttonRemoveDefaultPrograms.setEnabled(false);
        buttonRemoveDefaultPrograms.setMargin(UiFactory.insets(0, 0, 0, 0));
        buttonRemoveDefaultPrograms.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveDefaultProgramsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelButtons.add(buttonRemoveDefaultPrograms, gridBagConstraints);

        buttonSetDefaultPrograms.setIcon(org.jphototagger.resources.Icons.getIcon("icon_edit.png"));
        buttonSetDefaultPrograms.setToolTipText(Bundle.getString(getClass(), "EditDefaultProgramsPanel.buttonSetDefaultPrograms.toolTipText")); // NOI18N
        buttonSetDefaultPrograms.setEnabled(false);
        buttonSetDefaultPrograms.setMargin(UiFactory.insets(0, 0, 0, 0));
        buttonSetDefaultPrograms.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSetDefaultProgramsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelButtons.add(buttonSetDefaultPrograms, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        add(panelButtons, gridBagConstraints);

        listFilenameSuffixes.setModel(new FilenameSuffixesListModel());
        listFilenameSuffixes.setCellRenderer(suffixProgramListCellRenderer);
        listFilenameSuffixes.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listFilenameSuffixesMouseClicked(evt);
            }
        });
        listFilenameSuffixes.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
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
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        add(scrollPaneFilenameSuffixes, gridBagConstraints);
    }

    private void buttonSetDefaultProgramsActionPerformed(java.awt.event.ActionEvent evt) {
        setDefaultPrograms();
    }

    private void buttonRemoveDefaultProgramsActionPerformed(java.awt.event.ActionEvent evt) {
        removeDefaultPrograms();
    }

    private void listFilenameSuffixesMouseClicked(java.awt.event.MouseEvent evt) {
        if (MouseEventUtil.isDoubleClick(evt) && listFilenameSuffixes.getSelectedIndex() >= 0) {
            setDefaultPrograms();
        }
    }

    private void listFilenameSuffixesKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER && listFilenameSuffixes.getSelectedIndex() >= 0) {
            setDefaultPrograms();
        } else if (evt.getKeyCode() == KeyEvent.VK_DELETE && listFilenameSuffixes.getSelectedIndex() >= 0) {
            removeDefaultPrograms();
        }
    }

    private javax.swing.JButton buttonRemoveDefaultPrograms;
    private javax.swing.JButton buttonSetDefaultPrograms;
    private javax.swing.JLabel labelListFilenameSuffixes;
    private javax.swing.JList<String> listFilenameSuffixes;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JScrollPane scrollPaneFilenameSuffixes;
}
