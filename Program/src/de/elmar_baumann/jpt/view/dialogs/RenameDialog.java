/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.view.dialogs;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.controller.filesystem.FilenameFormatDate;
import de.elmar_baumann.jpt.controller.filesystem.FilenameFormatEmptyString;
import de.elmar_baumann.jpt.controller.filesystem.FilenameFormat;
import de.elmar_baumann.jpt.controller.filesystem.FilenameFormatArray;
import de.elmar_baumann.jpt.controller.filesystem.FilenameFormatFileName;
import de.elmar_baumann.jpt.controller.filesystem.FilenameFormatNumberSequence;
import de.elmar_baumann.jpt.controller.filesystem.FilenameFormatConstantString;
import de.elmar_baumann.jpt.controller.filesystem.FilenameFormatFilenamePostfix;
import de.elmar_baumann.jpt.event.listener.impl.ListenerProvider;
import de.elmar_baumann.jpt.event.RenameFileEvent;
import de.elmar_baumann.jpt.event.listener.RenameFileListener;
import de.elmar_baumann.jpt.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.jpt.image.thumbnail.ThumbnailUtil;
import de.elmar_baumann.jpt.types.FileType;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.lib.dialog.Dialog;
import de.elmar_baumann.lib.util.SettingsHints;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileSystemView;

/**
 * Dialog for renaming filenames.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 */
public final class RenameDialog extends Dialog {

    private final FilenameFormatArray filenameFormatArray =
            new FilenameFormatArray();
    private List<File> files = new ArrayList<File>();
    private List<RenameFileListener> renameFileListeners =
            new LinkedList<RenameFileListener>();
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
        listenerProvider = ListenerProvider.INSTANCE;
        renameFileListeners = listenerProvider.getRenameFileListeners();
        setIconImages(AppLookAndFeel.getAppIcons());
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
        model.addElement(new FilenameFormatDate("-"));
        model.addElement(new FilenameFormatEmptyString());
        return model;
    }

    /**
     * En- or disables the panel for renaming via templates.
     *
     * @param enabled true if enabled
     */
    public void setEnabledTemplates(boolean enabled) {
        tabbedPane.setEnabledAt(1, enabled);
        if (!enabled) {
            tabbedPane.setSelectedComponent(panelInputName);
        }
    }

    /**
     * Sets the files to rename;
     * 
     * @param files  files
     */
    public void setFiles(List<File> files) {
        this.files = files;
    }

    public synchronized void notifyRenameListeners(File oldFile, File newFile) {
        RenameFileEvent action = new RenameFileEvent(oldFile, newFile);
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
        String oldXmpFilename = XmpMetadata.
                getSidecarFilenameOfImageFileIfExists(oldFilenamne);
        if (oldXmpFilename != null) {
            String newXmpFilename = XmpMetadata.
                    suggestSidecarFilenameForImageFile(newFilename);
            File newXmpFile = new File(newXmpFilename);
            File oldXmpFile = new File(oldXmpFilename);
            if (newXmpFile.exists()) {
                if (!newXmpFile.delete()) {
                    AppLog.logWarning(RenameDialog.class,
                            "RenameDialog.Error.XmpFileCouldNotBeDeleted",
                            newXmpFilename);
                }
            }
            if (!oldXmpFile.renameTo(newXmpFile)) {
                AppLog.logWarning(RenameDialog.class,
                        "RenameDialog.Error.XmpFileCouldNotBeRenamed",
                        oldXmpFilename, newXmpFilename);
            }
        }
    }

    private void refreshThumbnailsPanel(int countRenamed) {
        if (countRenamed > 0) {
            GUI.INSTANCE.getAppPanel().getPanelThumbnails().refresh();
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
            filenameFormatArray.setFile(oldFile);
            File newFile = new File(
                    (parent == null
                     ? ""
                     : parent + File.separator) +
                    filenameFormatArray.format());
            if (checkNewFileNotExists(newFile) && renameFile(oldFile, newFile)) {
                files.set(i, newFile);
                notifyRenameListeners(oldFile, newFile);
                countRenamed++;
            } else {
                errorMessageNotRenamed(oldFile.getAbsolutePath());
            }
            filenameFormatArray.notifyNext();
        }
        refreshThumbnailsPanel(countRenamed);
        lockClose = false;
        setVisible(false);
        dispose();
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
        return new File(directory + (directory.isEmpty()
                                     ? ""
                                     : File.separator) +
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
            MessageDisplayer.error(this, "RenameDialog.Error.InvalidInput");
        }
        return defined;
    }

    private boolean checkNamesNotEquals(File oldFile, File newFile) {
        boolean equals = newFile.getAbsolutePath().equals(oldFile.
                getAbsolutePath());
        if (equals) {
            MessageDisplayer.error(this, "RenameDialog.Error.FilenamesEquals");
        }
        return !equals;
    }

    private boolean checkNewFileNotExists(File file) {
        boolean exists = file.exists();
        if (exists) {
            MessageDisplayer.error(this, "RenameDialog.Error.NewFileExists",
                    file.getName());
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
        setFilenameFormatToSelectedItem(
                comboBoxAtBegin, file, textFieldAtBegin.getText().trim());
        setFilenameFormatToSelectedItem(
                comboBoxInTheMiddle, file, textFieldInTheMiddle.getText().trim());
        setFilenameFormatToSelectedItem(
                comboBoxAtEnd, file, textFieldAtEnd.getText().trim());
    }

    private void setFilenameFormatToSelectedItem(
            JComboBox comboBox, File file, String fmt) {
        ComboBoxModel model = comboBox.getModel();
        FilenameFormat format = (FilenameFormat) model.getSelectedItem();
        format.setFile(file);
        format.setFormat(fmt);
        if (format instanceof FilenameFormatNumberSequence) {
            FilenameFormatNumberSequence f =
                    (FilenameFormatNumberSequence) format;
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
        filenameFormatArray.addFormat(
                (FilenameFormat) comboBoxAtBegin.getSelectedItem());
        filenameFormatArray.addFormat(
                new FilenameFormatConstantString(
                textFieldDelim1.getText().trim()));
        filenameFormatArray.addFormat(
                (FilenameFormat) comboBoxInTheMiddle.getSelectedItem());
        filenameFormatArray.addFormat(
                new FilenameFormatConstantString(
                textFieldDelim2.getText().trim()));
        filenameFormatArray.addFormat(
                (FilenameFormat) comboBoxAtEnd.getSelectedItem());
        FilenameFormatFilenamePostfix postfix =
                new FilenameFormatFilenamePostfix();
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
        labelDirectory.setIcon(
                FileSystemView.getFileSystemView().getSystemIcon(dir));
    }

    private synchronized void setThumbnail(File file) {
        Image thumbnail = null;
        if (FileType.isJpegFile(file.getName())) {
            thumbnail =
                    ThumbnailUtil.getScaledImage(file, panelThumbnail.getWidth());
        }
        if (thumbnail != null) {
            panelThumbnail.setImage(thumbnail);
            panelThumbnail.repaint();
        }
    }

    private void errorMessageNotRenamed(String filename) {
        if (MessageDisplayer.confirm(this,
                "RenameDialog.Confirm.RenameNextFile",
                MessageDisplayer.CancelButton.HIDE, filename).equals(
                MessageDisplayer.ConfirmAction.NO)) {
            stop = true;
            setVisible(false);
            dispose();
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            readProperties();
            setExampleFilename();
        } else {
            writeProperties();
        }
        super.setVisible(visible);
    }

    private void readProperties() {
        UserSettings.INSTANCE.getSettings().getSizeAndLocation(this);
        UserSettings.INSTANCE.getSettings().getComponent(this,
                getPersistentSettingsHints());
        if (!tabbedPane.isEnabledAt(1)) {
            tabbedPane.setSelectedComponent(panelInputName);
        }
    }

    private void writeProperties() {
        UserSettings.INSTANCE.getSettings().setSizeAndLocation(this);
        UserSettings.INSTANCE.getSettings().setComponent(this,
                getPersistentSettingsHints());
        UserSettings.INSTANCE.writeToFile();
    }

    private SettingsHints getPersistentSettingsHints() {
        SettingsHints hints = new SettingsHints(EnumSet.of(
                SettingsHints.Option.SET_TABBED_PANE_CONTENT));
        hints.addExclude(getClass().getName() + ".labelBeforeFilename");
        hints.addExclude(getClass().getName() + ".labelAfterFilename");
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
        panelBorder = new javax.swing.JPanel();
        panelThumbnail = new de.elmar_baumann.lib.component.ImagePanel();
        labelOldNamePrompt = new javax.swing.JLabel();
        labelOldName = new javax.swing.JLabel();
        labelNewNamePrompt = new javax.swing.JLabel();
        textFieldNewName = new javax.swing.JTextField();
        buttonNextFile = new javax.swing.JButton();
        buttonRename = new javax.swing.JButton();
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
        setTitle(Bundle.getString("RenameDialog.title"));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        panelInputName.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                panelInputNameComponentShown(evt);
            }
        });

        labelDirectoryPrompt.setText(Bundle.getString("RenameDialog.labelDirectoryPrompt.text"));

        labelDirectory.setForeground(new java.awt.Color(0, 175, 0));
        labelDirectory.setText(Bundle.getString("RenameDialog.labelDirectory.text"));

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
            .addComponent(panelThumbnail, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        panelBorderLayout.setVerticalGroup(
            panelBorderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelThumbnail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        labelOldNamePrompt.setText(Bundle.getString("RenameDialog.labelOldNamePrompt.text"));

        labelOldName.setForeground(new java.awt.Color(0, 175, 0));
        labelOldName.setText(Bundle.getString("RenameDialog.labelOldName.text"));

        labelNewNamePrompt.setText(Bundle.getString("RenameDialog.labelNewNamePrompt.text"));

        buttonNextFile.setMnemonic('b');
        buttonNextFile.setText(Bundle.getString("RenameDialog.buttonNextFile.text"));
        buttonNextFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonNextFileActionPerformed(evt);
            }
        });

        buttonRename.setMnemonic('u');
        buttonRename.setText(Bundle.getString("RenameDialog.buttonRename.text"));
        buttonRename.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRenameActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelInputNameLayout = new javax.swing.GroupLayout(panelInputName);
        panelInputName.setLayout(panelInputNameLayout);
        panelInputNameLayout.setHorizontalGroup(
            panelInputNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInputNameLayout.createSequentialGroup()
                .addGroup(panelInputNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInputNameLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(panelBorder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelInputNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(labelNewNamePrompt, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelOldName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE)
                            .addComponent(labelOldNamePrompt, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(textFieldNewName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelInputNameLayout.createSequentialGroup()
                        .addContainerGap(340, Short.MAX_VALUE)
                        .addComponent(buttonNextFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonRename))
                    .addGroup(panelInputNameLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelInputNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelDirectory, javax.swing.GroupLayout.DEFAULT_SIZE, 532, Short.MAX_VALUE)
                            .addComponent(labelDirectoryPrompt))))
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 183, Short.MAX_VALUE)
                        .addGroup(panelInputNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(buttonRename)
                            .addComponent(buttonNextFile))))
                .addContainerGap())
        );

        tabbedPane.addTab(Bundle.getString("RenameDialog.panelInputName.TabConstraints.tabTitle"), panelInputName);

        panelNumbers.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString("RenameDialog.panelNumbers.border.title")));

        labelStartNumber.setText(Bundle.getString("RenameDialog.labelStartNumber.text"));

        spinnerStartNumber.setModel(new SpinnerNumberModel(1, 1, 999999, 1));
        spinnerStartNumber.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerStartNumberStateChanged(evt);
            }
        });

        labelNumberStepWidth.setText(Bundle.getString("RenameDialog.labelNumberStepWidth.text"));

        spinnerNumberStepWidth.setModel(new SpinnerNumberModel(1, 1, 99, 1));
        spinnerNumberStepWidth.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerNumberStepWidthStateChanged(evt);
            }
        });

        labelNumberCount.setText(Bundle.getString("RenameDialog.labelNumberCount.text"));

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

        labelDateDelim.setText(Bundle.getString("RenameDialog.labelDateDelim.text"));

        textFieldDateDelim.setColumns(1);
        textFieldDateDelim.setText("-");
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
                .addContainerGap(210, Short.MAX_VALUE))
        );
        panelOtherLayout.setVerticalGroup(
            panelOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOtherLayout.createSequentialGroup()
                .addGroup(panelOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelDateDelim)
                    .addComponent(textFieldDateDelim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelDefineName.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString("RenameDialog.panelDefineName.border.title")));

        labelAtBegin.setText(Bundle.getString("RenameDialog.labelAtBegin.text"));

        comboBoxAtBegin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxAtBeginActionPerformed(evt);
            }
        });

        textFieldAtBegin.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldAtBeginKeyReleased(evt);
            }
        });

        labelDelim1.setText(Bundle.getString("RenameDialog.labelDelim1.text"));

        textFieldDelim1.setColumns(1);
        textFieldDelim1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldDelim1KeyReleased(evt);
            }
        });

        labelInTheMid.setText(Bundle.getString("RenameDialog.labelInTheMid.text"));

        comboBoxInTheMiddle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxInTheMiddleActionPerformed(evt);
            }
        });

        textFieldInTheMiddle.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldInTheMiddleKeyReleased(evt);
            }
        });

        labelDelim2.setText(Bundle.getString("RenameDialog.labelDelim2.text"));

        textFieldDelim2.setColumns(1);
        textFieldDelim2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldDelim2KeyReleased(evt);
            }
        });

        labelAtEnd.setText(Bundle.getString("RenameDialog.labelAtEnd.text"));

        comboBoxAtEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxAtEndActionPerformed(evt);
            }
        });

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
                    .addComponent(textFieldAtBegin, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
                    .addComponent(comboBoxAtBegin, 0, 124, Short.MAX_VALUE)
                    .addComponent(labelAtBegin))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDefineNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textFieldDelim1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelDelim1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDefineNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textFieldInTheMiddle, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                    .addComponent(comboBoxInTheMiddle, 0, 127, Short.MAX_VALUE)
                    .addComponent(labelInTheMid))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDefineNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textFieldDelim2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelDelim2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDefineNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelAtEnd)
                    .addComponent(comboBoxAtEnd, 0, 141, Short.MAX_VALUE)
                    .addComponent(textFieldAtEnd, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE))
                .addContainerGap())
        );

        panelDefineNameLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {labelDelim1, textFieldDelim1});

        panelDefineNameLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {labelDelim2, textFieldDelim2});

        panelDefineNameLayout.setVerticalGroup(
            panelDefineNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDefineNameLayout.createSequentialGroup()
                .addGroup(panelDefineNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(labelInTheMid)
                    .addComponent(labelDelim2)
                    .addComponent(labelAtEnd)
                    .addComponent(labelAtBegin)
                    .addComponent(labelDelim1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDefineNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(textFieldDelim1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboBoxInTheMiddle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textFieldDelim2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboBoxAtBegin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboBoxAtEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDefineNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelDefineNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(textFieldInTheMiddle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(textFieldAtEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(textFieldAtBegin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelDefineNameLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {textFieldAtBegin, textFieldAtEnd, textFieldInTheMiddle});

        panelExample.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString("RenameDialog.panelExample.border.title")));

        labelBefore.setText(Bundle.getString("RenameDialog.labelBefore.text"));

        labelBeforeFilename.setForeground(new java.awt.Color(0, 0, 175));
        labelBeforeFilename.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        labelAfter.setText(Bundle.getString("RenameDialog.labelAfter.text"));

        labelAfterFilename.setForeground(new java.awt.Color(0, 0, 255));
        labelAfterFilename.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout panelExampleLayout = new javax.swing.GroupLayout(panelExample);
        panelExample.setLayout(panelExampleLayout);
        panelExampleLayout.setHorizontalGroup(
            panelExampleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelExampleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelExampleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelAfter, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(labelBefore, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelExampleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelAfterFilename, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
                    .addComponent(labelBeforeFilename, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelExampleLayout.setVerticalGroup(
            panelExampleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelExampleLayout.createSequentialGroup()
                .addGroup(panelExampleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(labelBeforeFilename, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelBefore))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelExampleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(labelAfterFilename, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelAfter))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelExampleLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {labelAfterFilename, labelBeforeFilename});

        buttonRenameTemplate.setMnemonic('u');
        buttonRenameTemplate.setText(Bundle.getString("RenameDialog.buttonRenameTemplate.text"));
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addComponent(buttonRenameTemplate)
                .addContainerGap())
        );

        tabbedPane.addTab(Bundle.getString("RenameDialog.panelTemplates.TabConstraints.tabTitle"), panelTemplates);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 557, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
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

private void panelInputNameComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_panelInputNameComponentShown
    if (panelInputName.isVisible()) {
        setCurrentFilenameToInputPanel();
    }
}//GEN-LAST:event_panelInputNameComponentShown

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
    private de.elmar_baumann.lib.component.ImagePanel panelThumbnail;
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
