package de.elmar_baumann.imv.view.dialogs;

import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.controller.filesystem.FilenameFormatDate;
import de.elmar_baumann.imv.controller.filesystem.FilenameFormatEmptyString;
import de.elmar_baumann.imv.controller.filesystem.FilenameFormat;
import de.elmar_baumann.imv.controller.filesystem.FilenameFormatArray;
import de.elmar_baumann.imv.controller.filesystem.FilenameFormatFileName;
import de.elmar_baumann.imv.controller.filesystem.FilenameFormatNumberSequence;
import de.elmar_baumann.imv.controller.filesystem.FilenameFormatConstantString;
import de.elmar_baumann.imv.controller.filesystem.FilenameFormatFilenamePostfix;
import de.elmar_baumann.imv.event.ListenerProvider;
import de.elmar_baumann.imv.event.RenameFileAction;
import de.elmar_baumann.imv.event.RenameFileListener;
import de.elmar_baumann.imv.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imv.image.thumbnail.ThumbnailUtil;
import de.elmar_baumann.imv.io.FileType;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.lib.dialog.Dialog;
import de.elmar_baumann.lib.persistence.PersistentComponentSizes;
import de.elmar_baumann.lib.persistence.PersistentSettings;
import de.elmar_baumann.lib.persistence.PersistentSettingsHints;
import java.awt.Image;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;

/**
 * Dialog for renaming filenames.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 */
public final class RenameDialog extends Dialog {

    private final FilenameFormatArray filenameFormatArray = new FilenameFormatArray();
    private List<File> files = new ArrayList<File>();
    private List<RenameFileListener> renameFileListeners = new LinkedList<RenameFileListener>();
    private ListenerProvider listenerProvider;
    private int fileIndex = 0;
    private boolean lockClose = false;
    private boolean stop = false;

    public RenameDialog() {
        super((java.awt.Frame) null, true);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        listenerProvider = ListenerProvider.getInstance();
        renameFileListeners = listenerProvider.getRenameFileListeners();
        setIconImages(AppIcons.getAppIcons());
        setComboBoxModels();
        setHelpContentsUrl(Bundle.getString("Help.Url.Contents"));
        registerKeyStrokes();
    }

    private void setComboBoxModels() {
        comboBoxAtBegin.setModel(getComboBoxModel());
        comboBoxInTheMiddle.setModel(getComboBoxModel());
        comboBoxAtEnd.setModel(getComboBoxModel());
    }

    private ComboBoxModel getComboBoxModel() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        model.addElement(new FilenameFormatConstantString(""));
        model.addElement(new FilenameFormatNumberSequence(1, 1, 4));
        model.addElement(new FilenameFormatFileName());
        model.addElement(new FilenameFormatDate("-")); // NOI18N
        model.addElement(new FilenameFormatEmptyString());
        return model;
    }

    /**
     * Sets the files to rename;
     * 
     * @param files  files
     */
    public void setFiles(List<File> files) {
        this.files = files;
    }

    public void notifyRenameListeners(File oldFile, File newFile) {
        RenameFileAction action = new RenameFileAction(oldFile, newFile);
        for (RenameFileListener listener : renameFileListeners) {
            listener.actionPerformed(action);
        }
    }

    private boolean renameFile(File oldFile, File newFile) {
        boolean renamed = oldFile.renameTo(newFile);
        if (renamed) {
            renameXmpFile(oldFile.getAbsolutePath(), newFile.getAbsolutePath());
        }
        return renamed;
    }

    private void renameXmpFile(String oldFilenamne, String newFilename) {
        String oldXmpFilename = XmpMetadata.getSidecarFilename(oldFilenamne);
        if (oldXmpFilename != null) {
            String newXmpFilename = XmpMetadata.suggestSidecarFilename(newFilename);
            File newXmpFile = new File(newXmpFilename);
            File oldXmpFile = new File(oldXmpFilename);
            if (newXmpFile.exists()) {
                if (!newXmpFile.delete()) {
                    MessageFormat msg = new MessageFormat(Bundle.getString("RenameDialog.ErrorMessage.XmpFileCouldNotBeDeleted"));
                    Object params[] = {newXmpFilename};
                    AppLog.logWarning(RenameDialog.class, msg.format(params));
                }
            }
            if (!oldXmpFile.renameTo(newXmpFile)) {
                MessageFormat msg = new MessageFormat(Bundle.getString("RenameDialog.ErrorMessage.XmpFileCouldNotBeRenamed"));
                Object params[] = {oldXmpFilename, newXmpFilename};
                AppLog.logWarning(RenameDialog.class, msg.format(params));
            }
        }
    }

    private void refreshThumbnailsPanel(int countRenamed) {
        if (countRenamed > 0) {
            Panels.getInstance().getAppPanel().getPanelThumbnails().refresh();
        }
    }

    private void renameViaTemplate() {
        lockClose = true;
        tabbedPane.setEnabledAt(1, false);
        int countRenamed = 0;
        int size = files.size();
        for (int i = 0; !stop && i < size; i++) {
            fileIndex = i;
            File oldFile = files.get(i);
            String parent = oldFile.getParent();
            File newFile = new File(
                (parent == null ? "" : parent + File.separator) + // NOI18N
                filenameFormatArray.format());
            if (checkNewFileNotExists(newFile) && renameFile(oldFile, newFile)) {
                files.set(i, newFile);
                notifyRenameListeners(oldFile, newFile);
                setCurrentFilenameToInputPanel();
                countRenamed++;
            } else {
                errorMessageNotRenamed(oldFile.getAbsolutePath());
            }
            filenameFormatArray.notifyNext();
        }
        tabbedPane.setEnabledAt(1, true);
        refreshThumbnailsPanel(countRenamed);
        lockClose = false;
    }

    private void renameViaInput() {
        lockClose = true;
        int countRenamed = 0;
        if (fileIndex >= 0 && fileIndex < files.size()) {
            File oldFile = files.get(fileIndex);
            if (canRenameViaInput()) {
                File newFile = getNewFileViaInput();
                if (renameFile(oldFile, newFile)) {
                    files.set(fileIndex, newFile);
                    notifyRenameListeners(oldFile, newFile);
                    setCurrentFilenameToInputPanel();
                    countRenamed++;
                } else {
                    errorMessageNotRenamed(oldFile.getAbsolutePath());
                }
                setNextFileViaInput();
            }
        }
        refreshThumbnailsPanel(countRenamed);
        lockClose = false;
    }

    private void setNextFileViaInput() {
        fileIndex++;
        if (fileIndex > files.size() - 1) {
            setVisible(false);
            dispose();
        } else {
            setCurrentFilenameToInputPanel();
        }
    }

    private File getNewFileViaInput() {
        String directory = labelDirectory.getText();
        return new File(directory + (directory.isEmpty() ? "" : File.separator) + // NOI18N
            textFieldNewName.getText().trim());
    }

    private boolean canRenameViaInput() {
        File oldFile = files.get(fileIndex);
        File newFile = getNewFileViaInput();
        return checkNewFilenameIsDefined() &&
            checkNamesNotEquals(oldFile, newFile) &&
            checkNewFileNotExists(newFile);
    }

    private boolean checkNewFilenameIsDefined() {
        String input = textFieldNewName.getText().trim();
        boolean defined = !input.isEmpty();
        if (!defined) {
            JOptionPane.showMessageDialog(
                null,
                Bundle.getString("RenameDialog.ErrorMessage.InvalidInput"),
                Bundle.getString("RenameDialog.ErrorMessage.InvalidInput.Title"),
                JOptionPane.ERROR_MESSAGE,
                AppIcons.getMediumAppIcon());
        }
        return defined;
    }

    private boolean checkNamesNotEquals(File oldFile, File newFile) {
        boolean equals = newFile.getAbsolutePath().equals(oldFile.getAbsolutePath());
        if (equals) {
            JOptionPane.showMessageDialog(
                null,
                Bundle.getString("RenameDialog.ErrorMessage.FilenamesEquals"),
                Bundle.getString("RenameDialog.ErrorMessage.FilenamesEquals.Title"),
                JOptionPane.ERROR_MESSAGE,
                AppIcons.getMediumAppIcon());
        }
        return !equals;
    }

    private boolean checkNewFileNotExists(File file) {
        boolean exists = file.exists();
        if (exists) {
            MessageFormat msg = new MessageFormat(Bundle.getString("RenameDialog.ErrorMessage.NewFileExists"));
            Object[] params = {file.getName()};
            JOptionPane.showMessageDialog(
                null,
                msg.format(params),
                Bundle.getString("RenameDialog.ErrorMessage.NewFileExists.Title"),
                JOptionPane.ERROR_MESSAGE,
                AppIcons.getMediumAppIcon());
        }
        return !exists;
    }

    private void setCurrentFilenameToInputPanel() {
        if (fileIndex >= 0 && fileIndex < files.size()) {
            File file = files.get(fileIndex);
            setDirectoryNameLabel(file);
            labelOldName.setText(file.getName());
            textFieldNewName.setText(file.getName());
            setThumbnail(file);
            textFieldNewName.requestFocus();
        }
    }

    private void setFileToFilenameFormats(File file) {
        setFilenameFormatToSelectedItem(comboBoxAtBegin, file, textFieldAtBegin.getText().trim());
        setFilenameFormatToSelectedItem(comboBoxInTheMiddle, file, textFieldInTheMiddle.getText().trim());
        setFilenameFormatToSelectedItem(comboBoxAtEnd, file, textFieldAtEnd.getText().trim());
    }

    private void setFilenameFormatToSelectedItem(JComboBox comboBox, File file, String fmt) {
        ComboBoxModel model = comboBox.getModel();
        FilenameFormat format = (FilenameFormat) model.getSelectedItem();
        format.setFile(file);
        format.setFormat(fmt);
        if (format instanceof FilenameFormatNumberSequence) {
            FilenameFormatNumberSequence f = (FilenameFormatNumberSequence) format;
            f.setStart((Integer) spinnerStartNumber.getValue());
            f.setIncrement((Integer) spinnerNumberStepWidth.getValue());
            f.setCountDigits((Integer) spinnerNumberCount.getValue());
        } else if (format instanceof FilenameFormatDate) {
            FilenameFormatDate f = (FilenameFormatDate) format;
            f.setDelimiter(textFieldDateDelim.getText().trim());
        }
    }

    private void setFilenameFormatArray(File file) {
        filenameFormatArray.clear();
        filenameFormatArray.addFormat((FilenameFormat) comboBoxAtBegin.getSelectedItem());
        filenameFormatArray.addFormat(new FilenameFormatConstantString(textFieldDelim1.getText().trim()));
        filenameFormatArray.addFormat((FilenameFormat) comboBoxInTheMiddle.getSelectedItem());
        filenameFormatArray.addFormat(new FilenameFormatConstantString(textFieldDelim2.getText().trim()));
        filenameFormatArray.addFormat((FilenameFormat) comboBoxAtEnd.getSelectedItem());
        FilenameFormatFilenamePostfix postfix = new FilenameFormatFilenamePostfix();
        postfix.setFile(file);
        filenameFormatArray.addFormat(postfix);
    }

    private void setExampleFilename() {
        if (files.size() > 0) {
            File file = files.get(0);
            setFileToFilenameFormats(file);
            setFilenameFormatArray(file);
            labelBeforeFilename.setText(file.getName());
            labelAfterFilename.setText(filenameFormatArray.format());
        }
    }

    private void setDirectoryNameLabel(File file) {
        File dir = file.getParentFile();
        labelDirectory.setText(dir.getAbsolutePath());
    }

    private synchronized void setThumbnail(File file) {
        Image thumbnail = null;
        if (FileType.isJpegFile(file.getName())) {
            thumbnail = ThumbnailUtil.getScaledImage(file, panelThumbnail.getWidth());
            // Imagero locks the displayed file and it couldn't be renamed
            //thumbnail = ThumbnailUtil.getThumbnail(file, panelThumbnail.getWidth(), true);
        }
        panelThumbnail.setImage(thumbnail);
        panelThumbnail.repaint();
    }

    private void errorMessageNotRenamed(String filename) {
        MessageFormat msg = new MessageFormat(Bundle.getString("RenameDialog.ConfirmMessage.RenameNextFile"));
        Object[] params = {filename};
        if (JOptionPane.showConfirmDialog(null,
            msg.format(params),
            Bundle.getString("RenameDialog.ConfirmMessage.RenameNextFile.Title"),
            JOptionPane.YES_NO_OPTION,
            JOptionPane.ERROR_MESSAGE,
            AppIcons.getMediumAppIcon()) == JOptionPane.NO_OPTION) {
            stop = true;
            setVisible(false);
            dispose();
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            PersistentComponentSizes.getSizeAndLocation(this);
            PersistentSettings.getInstance().getComponent(this, getPersistentSettingsHints());
            setCurrentFilenameToInputPanel();
            setExampleFilename();
        } else {
            PersistentComponentSizes.setSizeAndLocation(this);
            PersistentSettings.getInstance().setComponent(this, getPersistentSettingsHints());
        }
        super.setVisible(visible);
    }

    private PersistentSettingsHints getPersistentSettingsHints() {
        PersistentSettingsHints hints = new PersistentSettingsHints();
        hints.addExcludedMember(getClass().getName() + ".labelBeforeFilename"); // NOI18N
        hints.addExcludedMember(getClass().getName() + ".labelAfterFilename"); // NOI18N
        return hints;
    }

    private void setEnabledConstantTextFields() {
        textFieldAtBegin.setEditable(
            comboBoxAtBegin.getSelectedItem() instanceof FilenameFormatConstantString);
        textFieldInTheMiddle.setEditable(
            comboBoxInTheMiddle.getSelectedItem() instanceof FilenameFormatConstantString);
        textFieldAtEnd.setEditable(
            comboBoxAtEnd.getSelectedItem() instanceof FilenameFormatConstantString);
    }

    @Override
    protected void help() {
        help(Bundle.getString("Help.Url.RenameDialog"));
    }

    @Override
    protected void escape() {
        if (!lockClose) {
            setVisible(false);
            dispose();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPane = new javax.swing.JTabbedPane();
        panelInputName = new javax.swing.JPanel();
        labelDirectoryPrompt = new javax.swing.JLabel();
        labelDirectory = new javax.swing.JLabel();
        labelOldNamePrompt = new javax.swing.JLabel();
        labelOldName = new javax.swing.JLabel();
        labelNewNamePrompt = new javax.swing.JLabel();
        textFieldNewName = new javax.swing.JTextField();
        buttonRename = new javax.swing.JButton();
        buttonNextFile = new javax.swing.JButton();
        panelBorder = new javax.swing.JPanel();
        panelThumbnail = new de.elmar_baumann.lib.image.ImagePanel();
        panelTemplates = new javax.swing.JPanel();
        panelNumbers = new javax.swing.JPanel();
        labelStartNumber = new javax.swing.JLabel();
        spinnerStartNumber = new javax.swing.JSpinner();
        labelNumberStepWidth = new javax.swing.JLabel();
        spinnerNumberStepWidth = new javax.swing.JSpinner();
        labelNumberCount = new javax.swing.JLabel();
        spinnerNumberCount = new javax.swing.JSpinner();
        panelOther = new javax.swing.JPanel();
        labelDateDelim = new javax.swing.JLabel();
        textFieldDateDelim = new javax.swing.JTextField();
        panelDefineName = new javax.swing.JPanel();
        labelAtBegin = new javax.swing.JLabel();
        comboBoxAtBegin = new javax.swing.JComboBox();
        textFieldAtBegin = new javax.swing.JTextField();
        labelDelim1 = new javax.swing.JLabel();
        textFieldDelim1 = new javax.swing.JTextField();
        labelInTheMid = new javax.swing.JLabel();
        comboBoxInTheMiddle = new javax.swing.JComboBox();
        textFieldInTheMiddle = new javax.swing.JTextField();
        labelDelim2 = new javax.swing.JLabel();
        textFieldDelim2 = new javax.swing.JTextField();
        labelAtEnd = new javax.swing.JLabel();
        comboBoxAtEnd = new javax.swing.JComboBox();
        textFieldAtEnd = new javax.swing.JTextField();
        panelExample = new javax.swing.JPanel();
        labelBefore = new javax.swing.JLabel();
        labelBeforeFilename = new javax.swing.JLabel();
        labelAfter = new javax.swing.JLabel();
        labelAfterFilename = new javax.swing.JLabel();
        buttonRenameTemplate = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(Bundle.getString("RenameDialog.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        labelDirectoryPrompt.setFont(new java.awt.Font("Dialog", 1, 11));
        labelDirectoryPrompt.setText(Bundle.getString("RenameDialog.labelDirectoryPrompt.text")); // NOI18N

        labelDirectory.setText(Bundle.getString("RenameDialog.labelDirectory.text")); // NOI18N

        labelOldNamePrompt.setFont(new java.awt.Font("Dialog", 1, 11));
        labelOldNamePrompt.setText(Bundle.getString("RenameDialog.labelOldNamePrompt.text")); // NOI18N

        labelOldName.setText(Bundle.getString("RenameDialog.labelOldName.text")); // NOI18N

        labelNewNamePrompt.setFont(new java.awt.Font("Dialog", 1, 11));
        labelNewNamePrompt.setText(Bundle.getString("RenameDialog.labelNewNamePrompt.text")); // NOI18N

        textFieldNewName.setText(Bundle.getString("RenameDialog.textFieldNewName.text")); // NOI18N

        buttonRename.setMnemonic('u');
        buttonRename.setText(Bundle.getString("RenameDialog.buttonRename.text")); // NOI18N
        buttonRename.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRenameActionPerformed(evt);
            }
        });

        buttonNextFile.setMnemonic('b');
        buttonNextFile.setText(Bundle.getString("RenameDialog.buttonNextFile.text")); // NOI18N
        buttonNextFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonNextFileActionPerformed(evt);
            }
        });

        panelBorder.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        panelThumbnail.setEnabled(false);
        panelThumbnail.setFocusable(false);
        panelThumbnail.setPreferredSize(new java.awt.Dimension(170, 170));

        javax.swing.GroupLayout panelThumbnailLayout = new javax.swing.GroupLayout(panelThumbnail);
        panelThumbnail.setLayout(panelThumbnailLayout);
        panelThumbnailLayout.setHorizontalGroup(
            panelThumbnailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 180, Short.MAX_VALUE)
        );
        panelThumbnailLayout.setVerticalGroup(
            panelThumbnailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 170, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelBorderLayout = new javax.swing.GroupLayout(panelBorder);
        panelBorder.setLayout(panelBorderLayout);
        panelBorderLayout.setHorizontalGroup(
            panelBorderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelThumbnail, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
        );
        panelBorderLayout.setVerticalGroup(
            panelBorderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelThumbnail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout panelInputNameLayout = new javax.swing.GroupLayout(panelInputName);
        panelInputName.setLayout(panelInputNameLayout);
        panelInputNameLayout.setHorizontalGroup(
            panelInputNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInputNameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelInputNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelDirectory, javax.swing.GroupLayout.DEFAULT_SIZE, 708, Short.MAX_VALUE)
                    .addGroup(panelInputNameLayout.createSequentialGroup()
                        .addComponent(panelBorder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelInputNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(labelOldNamePrompt, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelNewNamePrompt, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelOldName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
                            .addComponent(textFieldNewName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
                            .addGroup(panelInputNameLayout.createSequentialGroup()
                                .addComponent(buttonNextFile)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buttonRename))))
                    .addComponent(labelDirectoryPrompt))
                .addContainerGap())
        );
        panelInputNameLayout.setVerticalGroup(
            panelInputNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInputNameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelDirectoryPrompt)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelDirectory)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInputNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelBorder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelInputNameLayout.createSequentialGroup()
                        .addComponent(labelOldNamePrompt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelOldName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelNewNamePrompt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textFieldNewName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelInputNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(buttonRename)
                            .addComponent(buttonNextFile))))
                .addContainerGap(135, Short.MAX_VALUE))
        );

        tabbedPane.addTab(Bundle.getString("RenameDialog.panelInputName.TabConstraints.tabTitle"), panelInputName); // NOI18N

        panelNumbers.setBorder(javax.swing.BorderFactory.createTitledBorder(null, Bundle.getString("RenameDialog.panelNumbers.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11))); // NOI18N

        labelStartNumber.setFont(new java.awt.Font("Dialog", 0, 12));
        labelStartNumber.setText(Bundle.getString("RenameDialog.labelStartNumber.text")); // NOI18N

        spinnerStartNumber.setModel(new SpinnerNumberModel(1, 1, 999999, 1));
        spinnerStartNumber.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerStartNumberStateChanged(evt);
            }
        });

        labelNumberStepWidth.setFont(new java.awt.Font("Dialog", 0, 12));
        labelNumberStepWidth.setText(Bundle.getString("RenameDialog.labelNumberStepWidth.text")); // NOI18N

        spinnerNumberStepWidth.setModel(new SpinnerNumberModel(1, 1, 99, 1));
        spinnerNumberStepWidth.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerNumberStepWidthStateChanged(evt);
            }
        });

        labelNumberCount.setFont(new java.awt.Font("Dialog", 0, 12));
        labelNumberCount.setText(Bundle.getString("RenameDialog.labelNumberCount.text")); // NOI18N

        spinnerNumberCount.setModel(new SpinnerNumberModel(3, 1, 7, 1));
        spinnerNumberCount.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerNumberCountStateChanged(evt);
            }
        });

        javax.swing.GroupLayout panelNumbersLayout = new javax.swing.GroupLayout(panelNumbers);
        panelNumbers.setLayout(panelNumbersLayout);
        panelNumbersLayout.setHorizontalGroup(
            panelNumbersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelNumbersLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelStartNumber)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spinnerStartNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelNumberStepWidth)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spinnerNumberStepWidth, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(labelNumberCount)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spinnerNumberCount, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        panelNumbersLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {spinnerNumberCount, spinnerNumberStepWidth, spinnerStartNumber});

        panelNumbersLayout.setVerticalGroup(
            panelNumbersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelNumbersLayout.createSequentialGroup()
                .addGroup(panelNumbersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(spinnerNumberCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelNumberCount)
                    .addComponent(spinnerNumberStepWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelNumberStepWidth)
                    .addComponent(spinnerStartNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelStartNumber))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelOther.setBorder(javax.swing.BorderFactory.createTitledBorder(null, Bundle.getString("RenameDialog.panelOther.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11))); // NOI18N

        labelDateDelim.setFont(new java.awt.Font("Dialog", 0, 12));
        labelDateDelim.setText(Bundle.getString("RenameDialog.labelDateDelim.text")); // NOI18N

        textFieldDateDelim.setColumns(1);
        textFieldDateDelim.setText(Bundle.getString("RenameDialog.textFieldDateDelim.text")); // NOI18N
        textFieldDateDelim.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldDateDelimKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout panelOtherLayout = new javax.swing.GroupLayout(panelOther);
        panelOther.setLayout(panelOtherLayout);
        panelOtherLayout.setHorizontalGroup(
            panelOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOtherLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelDateDelim)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textFieldDateDelim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(305, Short.MAX_VALUE))
        );
        panelOtherLayout.setVerticalGroup(
            panelOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOtherLayout.createSequentialGroup()
                .addGroup(panelOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelDateDelim)
                    .addComponent(textFieldDateDelim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelDefineName.setBorder(javax.swing.BorderFactory.createTitledBorder(null, Bundle.getString("RenameDialog.panelDefineName.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11))); // NOI18N

        labelAtBegin.setFont(new java.awt.Font("Dialog", 0, 12));
        labelAtBegin.setText(Bundle.getString("RenameDialog.labelAtBegin.text")); // NOI18N

        comboBoxAtBegin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxAtBeginActionPerformed(evt);
            }
        });

        textFieldAtBegin.setText(Bundle.getString("RenameDialog.textFieldAtBegin.text")); // NOI18N
        textFieldAtBegin.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldAtBeginKeyReleased(evt);
            }
        });

        labelDelim1.setFont(new java.awt.Font("Dialog", 0, 12));
        labelDelim1.setText(Bundle.getString("RenameDialog.labelDelim1.text")); // NOI18N

        textFieldDelim1.setColumns(1);
        textFieldDelim1.setText(Bundle.getString("RenameDialog.textFieldDelim1.text")); // NOI18N
        textFieldDelim1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldDelim1KeyReleased(evt);
            }
        });

        labelInTheMid.setFont(new java.awt.Font("Dialog", 0, 12));
        labelInTheMid.setText(Bundle.getString("RenameDialog.labelInTheMid.text")); // NOI18N

        comboBoxInTheMiddle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxInTheMiddleActionPerformed(evt);
            }
        });

        textFieldInTheMiddle.setText(Bundle.getString("RenameDialog.textFieldInTheMiddle.text")); // NOI18N
        textFieldInTheMiddle.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldInTheMiddleKeyReleased(evt);
            }
        });

        labelDelim2.setFont(new java.awt.Font("Dialog", 0, 12));
        labelDelim2.setText(Bundle.getString("RenameDialog.labelDelim2.text")); // NOI18N

        textFieldDelim2.setColumns(1);
        textFieldDelim2.setText(Bundle.getString("RenameDialog.textFieldDelim2.text")); // NOI18N
        textFieldDelim2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldDelim2KeyReleased(evt);
            }
        });

        labelAtEnd.setFont(new java.awt.Font("Dialog", 0, 12));
        labelAtEnd.setText(Bundle.getString("RenameDialog.labelAtEnd.text")); // NOI18N

        comboBoxAtEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxAtEndActionPerformed(evt);
            }
        });

        textFieldAtEnd.setText(Bundle.getString("RenameDialog.textFieldAtEnd.text")); // NOI18N
        textFieldAtEnd.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                textFieldAtEndMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout panelDefineNameLayout = new javax.swing.GroupLayout(panelDefineName);
        panelDefineName.setLayout(panelDefineNameLayout);
        panelDefineNameLayout.setHorizontalGroup(
            panelDefineNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDefineNameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDefineNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelAtBegin)
                    .addComponent(comboBoxAtBegin, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textFieldAtBegin, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDefineNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(labelDelim1)
                    .addComponent(textFieldDelim1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDefineNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelInTheMid)
                    .addComponent(comboBoxInTheMiddle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textFieldInTheMiddle, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDefineNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(labelDelim2)
                    .addComponent(textFieldDelim2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDefineNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelAtEnd)
                    .addComponent(comboBoxAtEnd, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textFieldAtEnd, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE))
                .addContainerGap())
        );

        panelDefineNameLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {comboBoxAtBegin, comboBoxAtEnd, comboBoxInTheMiddle, labelInTheMid, textFieldAtBegin, textFieldAtEnd, textFieldInTheMiddle});

        panelDefineNameLayout.setVerticalGroup(
            panelDefineNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDefineNameLayout.createSequentialGroup()
                .addGroup(panelDefineNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelDefineNameLayout.createSequentialGroup()
                        .addGroup(panelDefineNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(labelInTheMid)
                            .addComponent(labelDelim1)
                            .addComponent(labelDelim2)
                            .addComponent(labelAtEnd))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelDefineNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(textFieldDelim1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(comboBoxInTheMiddle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(textFieldDelim2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(comboBoxAtEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelDefineNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(textFieldInTheMiddle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(textFieldAtEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelDefineNameLayout.createSequentialGroup()
                        .addComponent(labelAtBegin)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboBoxAtBegin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textFieldAtBegin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelDefineNameLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {textFieldAtBegin, textFieldAtEnd, textFieldInTheMiddle});

        panelExample.setBorder(javax.swing.BorderFactory.createTitledBorder(null, Bundle.getString("RenameDialog.panelExample.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        labelBefore.setFont(new java.awt.Font("Tahoma", 1, 11));
        labelBefore.setText(Bundle.getString("RenameDialog.labelBefore.text")); // NOI18N

        labelBeforeFilename.setFont(new java.awt.Font("Dialog", 0, 12));
        labelBeforeFilename.setText(Bundle.getString("RenameDialog.labelBeforeFilename.text")); // NOI18N

        labelAfter.setFont(new java.awt.Font("Tahoma", 1, 11));
        labelAfter.setText(Bundle.getString("RenameDialog.labelAfter.text")); // NOI18N

        labelAfterFilename.setFont(new java.awt.Font("Dialog", 0, 12));
        labelAfterFilename.setText(Bundle.getString("RenameDialog.labelAfterFilename.text")); // NOI18N

        javax.swing.GroupLayout panelExampleLayout = new javax.swing.GroupLayout(panelExample);
        panelExample.setLayout(panelExampleLayout);
        panelExampleLayout.setHorizontalGroup(
            panelExampleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelExampleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelExampleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelBefore, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(labelAfter, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelExampleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelAfterFilename, javax.swing.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
                    .addComponent(labelBeforeFilename, javax.swing.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelExampleLayout.setVerticalGroup(
            panelExampleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelExampleLayout.createSequentialGroup()
                .addGroup(panelExampleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelBefore)
                    .addComponent(labelBeforeFilename))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelExampleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelAfter)
                    .addComponent(labelAfterFilename))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelExampleLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {labelAfterFilename, labelBeforeFilename});

        buttonRenameTemplate.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonRenameTemplate.setMnemonic('u');
        buttonRenameTemplate.setText(Bundle.getString("RenameDialog.buttonRenameTemplate.text")); // NOI18N
        buttonRenameTemplate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRenameTemplateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelTemplatesLayout = new javax.swing.GroupLayout(panelTemplates);
        panelTemplates.setLayout(panelTemplatesLayout);
        panelTemplatesLayout.setHorizontalGroup(
            panelTemplatesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTemplatesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelTemplatesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelDefineName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelOther, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelNumbers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelExample, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buttonRenameTemplate, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        panelTemplatesLayout.setVerticalGroup(
            panelTemplatesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTemplatesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelNumbers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelOther, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelDefineName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelExample, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonRenameTemplate)
                .addContainerGap())
        );

        tabbedPane.addTab(Bundle.getString("RenameDialog.panelTemplates.TabConstraints.tabTitle"), panelTemplates); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 737, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void buttonRenameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRenameActionPerformed
    renameViaInput();
}//GEN-LAST:event_buttonRenameActionPerformed

private void buttonNextFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonNextFileActionPerformed
    setNextFileViaInput();
}//GEN-LAST:event_buttonNextFileActionPerformed

private void textFieldAtEndMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_textFieldAtEndMouseReleased
    setExampleFilename();
}//GEN-LAST:event_textFieldAtEndMouseReleased

private void textFieldDelim2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldDelim2KeyReleased
    setExampleFilename();
}//GEN-LAST:event_textFieldDelim2KeyReleased

private void textFieldInTheMiddleKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldInTheMiddleKeyReleased
    setExampleFilename();
}//GEN-LAST:event_textFieldInTheMiddleKeyReleased

private void textFieldDelim1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldDelim1KeyReleased
    setExampleFilename();
}//GEN-LAST:event_textFieldDelim1KeyReleased

private void textFieldAtBeginKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldAtBeginKeyReleased
    setExampleFilename();
}//GEN-LAST:event_textFieldAtBeginKeyReleased

private void spinnerNumberCountStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinnerNumberCountStateChanged
    setExampleFilename();
}//GEN-LAST:event_spinnerNumberCountStateChanged

private void spinnerNumberStepWidthStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinnerNumberStepWidthStateChanged
    setExampleFilename();
}//GEN-LAST:event_spinnerNumberStepWidthStateChanged

private void spinnerStartNumberStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinnerStartNumberStateChanged
    setExampleFilename();
}//GEN-LAST:event_spinnerStartNumberStateChanged

private void buttonRenameTemplateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRenameTemplateActionPerformed
    renameViaTemplate();
}//GEN-LAST:event_buttonRenameTemplateActionPerformed

private void comboBoxAtBeginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxAtBeginActionPerformed
    setExampleFilename();
    setEnabledConstantTextFields();
}//GEN-LAST:event_comboBoxAtBeginActionPerformed

private void comboBoxInTheMiddleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxInTheMiddleActionPerformed
    setExampleFilename();
    setEnabledConstantTextFields();
}//GEN-LAST:event_comboBoxInTheMiddleActionPerformed

private void comboBoxAtEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxAtEndActionPerformed
    setExampleFilename();
    setEnabledConstantTextFields();
}//GEN-LAST:event_comboBoxAtEndActionPerformed

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    if (!lockClose) {
        setVisible(false);
        dispose();
    }
}//GEN-LAST:event_formWindowClosing

private void textFieldDateDelimKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldDateDelimKeyReleased
    setExampleFilename();
}//GEN-LAST:event_textFieldDateDelimKeyReleased

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                RenameDialog dialog = new RenameDialog();
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonNextFile;
    private javax.swing.JButton buttonRename;
    private javax.swing.JButton buttonRenameTemplate;
    private javax.swing.JComboBox comboBoxAtBegin;
    private javax.swing.JComboBox comboBoxAtEnd;
    private javax.swing.JComboBox comboBoxInTheMiddle;
    private javax.swing.JLabel labelAfter;
    private javax.swing.JLabel labelAfterFilename;
    private javax.swing.JLabel labelAtBegin;
    private javax.swing.JLabel labelAtEnd;
    private javax.swing.JLabel labelBefore;
    private javax.swing.JLabel labelBeforeFilename;
    private javax.swing.JLabel labelDateDelim;
    private javax.swing.JLabel labelDelim1;
    private javax.swing.JLabel labelDelim2;
    private javax.swing.JLabel labelDirectory;
    private javax.swing.JLabel labelDirectoryPrompt;
    private javax.swing.JLabel labelInTheMid;
    private javax.swing.JLabel labelNewNamePrompt;
    private javax.swing.JLabel labelNumberCount;
    private javax.swing.JLabel labelNumberStepWidth;
    private javax.swing.JLabel labelOldName;
    private javax.swing.JLabel labelOldNamePrompt;
    private javax.swing.JLabel labelStartNumber;
    private javax.swing.JPanel panelBorder;
    private javax.swing.JPanel panelDefineName;
    private javax.swing.JPanel panelExample;
    private javax.swing.JPanel panelInputName;
    private javax.swing.JPanel panelNumbers;
    private javax.swing.JPanel panelOther;
    private javax.swing.JPanel panelTemplates;
    private de.elmar_baumann.lib.image.ImagePanel panelThumbnail;
    private javax.swing.JSpinner spinnerNumberCount;
    private javax.swing.JSpinner spinnerNumberStepWidth;
    private javax.swing.JSpinner spinnerStartNumber;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTextField textFieldAtBegin;
    private javax.swing.JTextField textFieldAtEnd;
    private javax.swing.JTextField textFieldDateDelim;
    private javax.swing.JTextField textFieldDelim1;
    private javax.swing.JTextField textFieldDelim2;
    private javax.swing.JTextField textFieldInTheMiddle;
    private javax.swing.JTextField textFieldNewName;
    // End of variables declaration//GEN-END:variables
}
