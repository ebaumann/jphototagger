/*
 * JPhotoTagger tags and finds images fast.
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.view.dialogs;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.controller.filesystem.FilenameFormatDate;
import de.elmar_baumann.jpt.controller.filesystem.FilenameFormatEmptyString;
import de.elmar_baumann.jpt.controller.filesystem.FilenameFormat;
import de.elmar_baumann.jpt.controller.filesystem.FilenameFormatArray;
import de.elmar_baumann.jpt.controller.filesystem.FilenameFormatFileName;
import de.elmar_baumann.jpt.controller.filesystem.FilenameFormatNumberSequence;
import de.elmar_baumann.jpt.controller.filesystem.FilenameFormatConstantString;
import de.elmar_baumann.jpt.controller.filesystem.FilenameFormatFilenamePostfix;
import de.elmar_baumann.jpt.data.RenameTemplate;
import de.elmar_baumann.jpt.event.FileSystemEvent;
import de.elmar_baumann.jpt.event.listener.impl.FileSystemListenerSupport;
import de.elmar_baumann.jpt.event.listener.FileSystemListener;
import de.elmar_baumann.jpt.helper.RenameTemplateHelper;
import de.elmar_baumann.jpt.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.jpt.image.thumbnail.ThumbnailUtil;
import de.elmar_baumann.jpt.model.ComboBoxModelRenameTemplates;
import de.elmar_baumann.jpt.types.FileType;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.lib.componentutil.ComboBoxUtil;
import de.elmar_baumann.lib.componentutil.MnemonicUtil;
import de.elmar_baumann.lib.dialog.Dialog;
import de.elmar_baumann.lib.util.Settings;
import java.awt.Container;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.filechooser.FileSystemView;

/**
 * Dialog for renaming filenames.
 *
 * @author  Elmar Baumann
 */
public final class RenameDialog extends Dialog implements ListDataListener {

    private static final    long                      serialVersionUID    = 2975958115627670989L;
    private static final    String                    KEY_SEL_TEMPLATE    = "RenameDialog.SelectedTemplate";
    private final transient FilenameFormatArray       filenameFormatArray = new FilenameFormatArray();
    private                 List<File>                files               = new ArrayList<File>();
    private final transient FileSystemListenerSupport listenerSupport     = new FileSystemListenerSupport();
    private                 int                       fileIndex           = 0;
    private                 boolean                   lockClose           = false;
    private                 boolean                   stop                = false;
    private transient       boolean                   listen              = true;

    public RenameDialog() {
        super(GUI.INSTANCE.getAppFrame(), true, UserSettings.INSTANCE.getSettings(), null);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setComboBoxModels();
        setHelpPages();
        MnemonicUtil.setMnemonics((Container) this);
        setEnabledRenameTemplateButtons();
        comboBoxRenameTemplates.getModel().addListDataListener(this);
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

    private void setHelpPages() {
        setHelpContentsUrl(JptBundle.INSTANCE.getString("Help.Url.Contents"));
        setHelpPageUrl(JptBundle.INSTANCE.getString("Help.Url.RenameDialog"));
    }

    public void addFileSystemListener(FileSystemListener listener) {
        listenerSupport.add(listener);
    }

    public void removeFileSystemListener(FileSystemListener listener) {
        listenerSupport.remove(listener);
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

    public synchronized void notifyFileSystemListeners(File oldFile, File newFile) {

        FileSystemEvent event = new FileSystemEvent(FileSystemEvent.Type.RENAME, oldFile, newFile);

        listenerSupport.notifyListeners(event);
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
                    AppLogger.logWarning(RenameDialog.class, "RenameDialog.Error.XmpFileCouldNotBeDeleted", newXmpFilename);
                }
            }
            if (!oldXmpFile.renameTo(newXmpFile)) {
                AppLogger.logWarning(RenameDialog.class, "RenameDialog.Error.XmpFileCouldNotBeRenamed", oldXmpFilename, newXmpFilename);
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
            File newFile = new File((parent == null ? ""
                       : parent + File.separator) + filenameFormatArray.format());
            if (checkNewFileDoesNotExist(newFile) && renameFile(oldFile, newFile)) {
                files.set(i, newFile);
                notifyFileSystemListeners(oldFile, newFile);
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
                    notifyFileSystemListeners(oldFile, newFile);
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
                : File.separator) + textFieldNewName.getText().trim());
    }

    private boolean canRenameViaInput() {
        File oldFile = files.get(fileIndex);
        File newFile = getNewFileViaInput();
        return checkNewFilenameIsDefined() &&
                checkNamesNotEquals(oldFile, newFile) &&
                checkNewFileDoesNotExist(newFile);
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
        boolean equals = newFile.getAbsolutePath().equals(oldFile.getAbsolutePath());
        if (equals) {
            MessageDisplayer.error(this, "RenameDialog.Error.FilenamesEquals");
        }
        return !equals;
    }

    private boolean checkNewFileDoesNotExist(File file) {
        boolean exists = file.exists();
        if (exists) {
            MessageDisplayer.error(this, "RenameDialog.Error.NewFileExists", file.getName());
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

    private void setFilenameFormatToSelectedItem(JComboBox comboBox, File file, String fmt) {
        ComboBoxModel  model  = comboBox.getModel();
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
            f.setDelimiter(getDateDelimiter());
        }
    }

    private String getDateDelimiter() {
        return (String) comboBoxDateDelimiter.getModel().getSelectedItem();
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
        labelDirectory.setIcon(FileSystemView.getFileSystemView().getSystemIcon(dir));
    }

    private synchronized void setThumbnail(File file) {
        Image thumbnail = null;
        if (FileType.isJpegFile(file.getName())) {
            thumbnail = ThumbnailUtil.getThumbnailFromJavaImageIo(file, panelThumbnail.getWidth());
        }
        if (thumbnail != null) {
            panelThumbnail.setImage(thumbnail);
            panelThumbnail.repaint();
        }
    }

    private void errorMessageNotRenamed(String filename) {
        if (!MessageDisplayer.confirmYesNo(this, "RenameDialog.Confirm.RenameNextFile", filename)) {
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
            UserSettings.INSTANCE.getSettings().set(this, UserSettings.SET_TABBED_PANE_SETTINGS);
        }
        super.setVisible(visible);
    }

    private void readProperties() {
        Settings settings = UserSettings.INSTANCE.getSettings();
        settings.applySettings(this, UserSettings.SET_TABBED_PANE_SETTINGS);
        if (!tabbedPane.isEnabledAt(1)) {
            tabbedPane.setSelectedComponent(panelInputName);
        }
        settings.applySelectedIndex(comboBoxRenameTemplates, KEY_SEL_TEMPLATE);
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
    protected void escape() {
        if (!lockClose) {
            setVisible(false);
            dispose();
        }
    }

    private void setTemplate(RenameTemplate template) {
        listen = false;
        spinnerStartNumber    .getModel().setValue(template.getStartNumber());
        spinnerNumberStepWidth.getModel().setValue(template.getStepWidth());
        spinnerNumberCount    .getModel().setValue(template.getNumberCount());
        ComboBoxUtil.selectString(comboBoxDateDelimiter.getModel(), template.getDateDelimiter());
        select                                    (template.getFormatClassAtBegin(), comboBoxAtBegin);
        textFieldDelim1     .setText              (template.getDelimiter1());
        select                                    (template.getFormatClassInTheMiddle(), comboBoxInTheMiddle);
        textFieldDelim2     .setText              (template.getDelimiter2());
        select                                    (template.getFormatClassAtEnd(), comboBoxAtEnd);
        textFieldAtBegin    .setText              (template.getTextAtBegin());
        textFieldInTheMiddle.setText              (template.getTextInTheMiddle());
        textFieldAtEnd      .setText              (template.getTextAtEnd());
        listen = true;
    }

    private RenameTemplate createTemplate() {
        RenameTemplate template = new RenameTemplate();
        setValuesToTemplate(template);
        return template;
    }

    private void setValuesToTemplate(RenameTemplate template) {
        template.setStartNumber           ((Integer) spinnerStartNumber.getModel().getValue());
        template.setStepWidth             ((Integer) spinnerNumberStepWidth.getModel().getValue());
        template.setNumberCount           ((Integer) spinnerNumberCount.getModel().getValue());
        template.setDateDelimiter         (getDateDelimiter());
        template.setFormatClassAtBegin    (comboBoxAtBegin.getSelectedItem().getClass());
        template.setDelimiter1            (textFieldDelim1.getText());
        template.setFormatClassInTheMiddle(comboBoxInTheMiddle.getSelectedItem().getClass());
        template.setDelimiter2            (textFieldDelim2.getText());
        template.setFormatClassAtEnd      (comboBoxAtEnd.getSelectedItem().getClass());
        template.setTextAtBegin           (textFieldAtBegin.getText());
        template.setTextInTheMiddle       (textFieldInTheMiddle.getText());
        template.setTextAtEnd             (textFieldAtEnd.getText());
    }

    private void select(Class<?> formatClass, JComboBox comboBox) {
        ComboBoxModel model    = comboBox.getModel();
        int           size     = model.getSize();
        boolean       selected = false;
        int           index    = 0;

        while (!selected && index < size) {
            Object element = model.getElementAt(index++);
            if (element.getClass().equals(formatClass)) {
                model.setSelectedItem(element);
                selected = true;
            }
        }
    }

    private void saveAsRenameTemplate() {
        RenameTemplate template = createTemplate();
        RenameTemplateHelper.insert(template);
    }

    private void renameRenameTemplate() {
        Object selItem = comboBoxRenameTemplates.getSelectedItem();
        if (selItem instanceof RenameTemplate) {
            RenameTemplateHelper.rename((RenameTemplate) selItem);
        }
    }

    private void deleteRenameTemplate() {
        Object selItem = comboBoxRenameTemplates.getSelectedItem();
        if (selItem instanceof RenameTemplate) {
            RenameTemplateHelper.delete((RenameTemplate) selItem);
        }
    }

    private void updateRenameTemplate() {
        Object selItem = comboBoxRenameTemplates.getSelectedItem();
        if (selItem instanceof RenameTemplate) {
            RenameTemplate template = (RenameTemplate) selItem;
            setValuesToTemplate(template);
            RenameTemplateHelper.update(template);
        }
    }

    private void setRenameTemplate() {
        Object selItem = comboBoxRenameTemplates.getSelectedItem();
        if (selItem instanceof RenameTemplate) {
            setTemplate((RenameTemplate) selItem);
            setEnabledConstantTextFields();
            setExampleFilename();
        }
    }

    private void setEnabledRenameTemplateButtons() {
        Object selValue = comboBoxRenameTemplates.getSelectedItem();
        boolean templateSelected = selValue instanceof RenameTemplate;

        buttonRenameRenameTemplate.setEnabled(templateSelected);
        buttonDeleteRenameTemplate.setEnabled(templateSelected);
        buttonUpdateRenameTemplate.setEnabled(templateSelected);
    }

    @Override
    public void intervalAdded(ListDataEvent e) {
        setEnabledRenameTemplateButtons();
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
        setEnabledRenameTemplateButtons();
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        setEnabledRenameTemplateButtons();
    }

    private void handleComboBoxAtBeginActionPerformed() {
        if (listen) {
            setExampleFilename();
            setEnabledConstantTextFields();
        }
    }

    private void handleComboBoxAtEndActionPerformed() {
        if (listen) {
            setExampleFilename();
            setEnabledConstantTextFields();
        }
    }

    private void handleComboBoxDateDelimiterActionPerformed() {
        if (listen) {
            setExampleFilename();
            setEnabledConstantTextFields();
        }
    }

    private void handleComboBoxInTheMiddleActionPerformed() {
        if (listen) {
            setExampleFilename();
            setEnabledConstantTextFields();
        }
    }

    private void handleComboBoxRenameTemplatesActionPerformed() {
        if (listen) {
            UserSettings.INSTANCE.getSettings().setSelectedIndex(comboBoxRenameTemplates, KEY_SEL_TEMPLATE);
            UserSettings.INSTANCE.writeToFile();
            setRenameTemplate();
            setEnabledRenameTemplateButtons();
        }
    }

    private void handlePanelInputNameComponentShown() {
        if (panelInputName.isVisible()) {
            setCurrentFilenameToInputPanel();
        }
    }

    private void handleSpinnerNumberCountStateChanged() {
        if (listen) {
            setExampleFilename();
        }
    }

    private void handleSpinnerNumberStepWidthStateChanged() {
        if (listen) {
            setExampleFilename();
        }
    }

    private void handleSpinnerStartNumberStateChanged() {
        if (listen) {
            setExampleFilename();
        }
    }

    private void handleTextFieldAtBeginKeyReleased() {
        if (listen) {
            setExampleFilename();
        }
    }

    private void handleTextFieldAtEndKeyReleased() {
        if (listen) {
            setExampleFilename();
        }
    }

    private void handleTextFieldDelim1KeyReleased() {
        if (listen) {
            setExampleFilename();
        }
    }

    private void handleTextFieldDelim2KeyReleased() {
        if (listen) {
            setExampleFilename();
        }
    }

    private void handleTextFieldInTheMiddleKeyReleased() {
        if (listen) {
            setExampleFilename();
        }
    }

    private void handleWindowClosing() {
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
        comboBoxDateDelimiter = new javax.swing.JComboBox();
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
        panelRenameTemplates = new javax.swing.JPanel();
        comboBoxRenameTemplates = new javax.swing.JComboBox();
        buttonSaveRenameTemplate = new javax.swing.JButton();
        buttonRenameRenameTemplate = new javax.swing.JButton();
        buttonDeleteRenameTemplate = new javax.swing.JButton();
        buttonUpdateRenameTemplate = new javax.swing.JButton();
        buttonRenameTemplate = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(JptBundle.INSTANCE.getString("RenameDialog.title")); // NOI18N
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

        labelDirectoryPrompt.setText(JptBundle.INSTANCE.getString("RenameDialog.labelDirectoryPrompt.text")); // NOI18N

        labelDirectory.setForeground(new java.awt.Color(0, 175, 0));
        labelDirectory.setText(JptBundle.INSTANCE.getString("RenameDialog.labelDirectory.text")); // NOI18N

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

        labelOldNamePrompt.setText(JptBundle.INSTANCE.getString("RenameDialog.labelOldNamePrompt.text")); // NOI18N

        labelOldName.setForeground(new java.awt.Color(0, 175, 0));
        labelOldName.setText(JptBundle.INSTANCE.getString("RenameDialog.labelOldName.text")); // NOI18N

        labelNewNamePrompt.setLabelFor(textFieldNewName);
        labelNewNamePrompt.setText(JptBundle.INSTANCE.getString("RenameDialog.labelNewNamePrompt.text")); // NOI18N

        buttonNextFile.setText(JptBundle.INSTANCE.getString("RenameDialog.buttonNextFile.text")); // NOI18N
        buttonNextFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonNextFileActionPerformed(evt);
            }
        });

        buttonRename.setText(JptBundle.INSTANCE.getString("RenameDialog.buttonRename.text")); // NOI18N
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
                            .addComponent(labelOldName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE)
                            .addComponent(labelOldNamePrompt, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(textFieldNewName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelInputNameLayout.createSequentialGroup()
                        .addContainerGap(322, Short.MAX_VALUE)
                        .addComponent(buttonNextFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonRename))
                    .addGroup(panelInputNameLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelInputNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelDirectory, javax.swing.GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE)
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 280, Short.MAX_VALUE)
                        .addGroup(panelInputNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(buttonRename)
                            .addComponent(buttonNextFile))))
                .addContainerGap())
        );

        tabbedPane.addTab(JptBundle.INSTANCE.getString("RenameDialog.panelInputName.TabConstraints.tabTitle"), panelInputName); // NOI18N

        panelNumbers.setBorder(javax.swing.BorderFactory.createTitledBorder(JptBundle.INSTANCE.getString("RenameDialog.panelNumbers.border.title"))); // NOI18N

        labelStartNumber.setLabelFor(spinnerStartNumber);
        labelStartNumber.setText(JptBundle.INSTANCE.getString("RenameDialog.labelStartNumber.text")); // NOI18N

        spinnerStartNumber.setModel(new SpinnerNumberModel(1, 1, 999999, 1));
        spinnerStartNumber.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerStartNumberStateChanged(evt);
            }
        });

        labelNumberStepWidth.setLabelFor(spinnerNumberStepWidth);
        labelNumberStepWidth.setText(JptBundle.INSTANCE.getString("RenameDialog.labelNumberStepWidth.text")); // NOI18N

        spinnerNumberStepWidth.setModel(new SpinnerNumberModel(1, 1, 99, 1));
        spinnerNumberStepWidth.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerNumberStepWidthStateChanged(evt);
            }
        });

        labelNumberCount.setLabelFor(spinnerNumberCount);
        labelNumberCount.setText(JptBundle.INSTANCE.getString("RenameDialog.labelNumberCount.text")); // NOI18N

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

        labelDateDelim.setText(JptBundle.INSTANCE.getString("RenameDialog.labelDateDelim.text")); // NOI18N

        comboBoxDateDelimiter.setModel(new DefaultComboBoxModel(new Object[] {"", "-", ".", "/"}));
        comboBoxDateDelimiter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxDateDelimiterActionPerformed(evt);
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
                .addComponent(comboBoxDateDelimiter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(250, Short.MAX_VALUE))
        );
        panelOtherLayout.setVerticalGroup(
            panelOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOtherLayout.createSequentialGroup()
                .addGroup(panelOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelDateDelim)
                    .addComponent(comboBoxDateDelimiter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelDefineName.setBorder(javax.swing.BorderFactory.createTitledBorder(JptBundle.INSTANCE.getString("RenameDialog.panelDefineName.border.title"))); // NOI18N

        labelAtBegin.setLabelFor(comboBoxAtBegin);
        labelAtBegin.setText(JptBundle.INSTANCE.getString("RenameDialog.labelAtBegin.text")); // NOI18N

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

        labelDelim1.setLabelFor(textFieldDelim1);
        labelDelim1.setText(JptBundle.INSTANCE.getString("RenameDialog.labelDelim1.text")); // NOI18N

        textFieldDelim1.setColumns(1);
        textFieldDelim1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldDelim1KeyReleased(evt);
            }
        });

        labelInTheMid.setLabelFor(comboBoxInTheMiddle);
        labelInTheMid.setText(JptBundle.INSTANCE.getString("RenameDialog.labelInTheMid.text")); // NOI18N

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

        labelDelim2.setLabelFor(textFieldDelim2);
        labelDelim2.setText(JptBundle.INSTANCE.getString("RenameDialog.labelDelim2.text")); // NOI18N

        textFieldDelim2.setColumns(1);
        textFieldDelim2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldDelim2KeyReleased(evt);
            }
        });

        labelAtEnd.setLabelFor(comboBoxAtEnd);
        labelAtEnd.setText(JptBundle.INSTANCE.getString("RenameDialog.labelAtEnd.text")); // NOI18N

        comboBoxAtEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxAtEndActionPerformed(evt);
            }
        });

        textFieldAtEnd.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldAtEndKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout panelDefineNameLayout = new javax.swing.GroupLayout(panelDefineName);
        panelDefineName.setLayout(panelDefineNameLayout);
        panelDefineNameLayout.setHorizontalGroup(
            panelDefineNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDefineNameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDefineNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textFieldAtBegin, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
                    .addComponent(comboBoxAtBegin, 0, 116, Short.MAX_VALUE)
                    .addComponent(labelAtBegin))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDefineNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textFieldDelim1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelDelim1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDefineNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textFieldInTheMiddle, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                    .addComponent(comboBoxInTheMiddle, 0, 118, Short.MAX_VALUE)
                    .addComponent(labelInTheMid))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDefineNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textFieldDelim2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelDelim2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDefineNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelAtEnd)
                    .addComponent(comboBoxAtEnd, 0, 132, Short.MAX_VALUE)
                    .addComponent(textFieldAtEnd, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE))
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

        panelExample.setBorder(javax.swing.BorderFactory.createTitledBorder(JptBundle.INSTANCE.getString("RenameDialog.panelExample.border.title"))); // NOI18N

        labelBefore.setText(JptBundle.INSTANCE.getString("RenameDialog.labelBefore.text")); // NOI18N

        labelBeforeFilename.setForeground(new java.awt.Color(0, 0, 175));
        labelBeforeFilename.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        labelAfter.setText(JptBundle.INSTANCE.getString("RenameDialog.labelAfter.text")); // NOI18N

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
                    .addComponent(labelAfterFilename, javax.swing.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
                    .addComponent(labelBeforeFilename, javax.swing.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE))
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

        panelRenameTemplates.setBorder(javax.swing.BorderFactory.createTitledBorder(JptBundle.INSTANCE.getString("RenameDialog.panelRenameTemplates.border.title"))); // NOI18N

        comboBoxRenameTemplates.setModel(new ComboBoxModelRenameTemplates());
        comboBoxRenameTemplates.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxRenameTemplatesActionPerformed(evt);
            }
        });

        buttonSaveRenameTemplate.setText(JptBundle.INSTANCE.getString("RenameDialog.buttonSaveRenameTemplate.text")); // NOI18N
        buttonSaveRenameTemplate.setToolTipText(JptBundle.INSTANCE.getString("RenameDialog.buttonSaveRenameTemplate.toolTipText")); // NOI18N
        buttonSaveRenameTemplate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveRenameTemplateActionPerformed(evt);
            }
        });

        buttonRenameRenameTemplate.setText(JptBundle.INSTANCE.getString("RenameDialog.buttonRenameRenameTemplate.text")); // NOI18N
        buttonRenameRenameTemplate.setToolTipText(JptBundle.INSTANCE.getString("RenameDialog.buttonRenameRenameTemplate.toolTipText")); // NOI18N
        buttonRenameRenameTemplate.setEnabled(false);
        buttonRenameRenameTemplate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRenameRenameTemplateActionPerformed(evt);
            }
        });

        buttonDeleteRenameTemplate.setText(JptBundle.INSTANCE.getString("RenameDialog.buttonDeleteRenameTemplate.text")); // NOI18N
        buttonDeleteRenameTemplate.setToolTipText(JptBundle.INSTANCE.getString("RenameDialog.buttonDeleteRenameTemplate.toolTipText")); // NOI18N
        buttonDeleteRenameTemplate.setEnabled(false);
        buttonDeleteRenameTemplate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteRenameTemplateActionPerformed(evt);
            }
        });

        buttonUpdateRenameTemplate.setText(JptBundle.INSTANCE.getString("RenameDialog.buttonUpdateRenameTemplate.text")); // NOI18N
        buttonUpdateRenameTemplate.setToolTipText(JptBundle.INSTANCE.getString("RenameDialog.buttonUpdateRenameTemplate.toolTipText")); // NOI18N
        buttonUpdateRenameTemplate.setEnabled(false);
        buttonUpdateRenameTemplate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonUpdateRenameTemplateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelRenameTemplatesLayout = new javax.swing.GroupLayout(panelRenameTemplates);
        panelRenameTemplates.setLayout(panelRenameTemplatesLayout);
        panelRenameTemplatesLayout.setHorizontalGroup(
            panelRenameTemplatesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRenameTemplatesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelRenameTemplatesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelRenameTemplatesLayout.createSequentialGroup()
                        .addComponent(comboBoxRenameTemplates, 0, 534, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(panelRenameTemplatesLayout.createSequentialGroup()
                        .addComponent(buttonSaveRenameTemplate)
                        .addGap(6, 6, 6)
                        .addComponent(buttonUpdateRenameTemplate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonRenameRenameTemplate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonDeleteRenameTemplate)
                        .addGap(62, 62, 62))))
        );
        panelRenameTemplatesLayout.setVerticalGroup(
            panelRenameTemplatesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRenameTemplatesLayout.createSequentialGroup()
                .addComponent(comboBoxRenameTemplates, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelRenameTemplatesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonSaveRenameTemplate)
                    .addComponent(buttonRenameRenameTemplate)
                    .addComponent(buttonDeleteRenameTemplate)
                    .addComponent(buttonUpdateRenameTemplate))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        buttonRenameTemplate.setText(JptBundle.INSTANCE.getString("RenameDialog.buttonRenameTemplate.text")); // NOI18N
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
                    .addComponent(panelRenameTemplates, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addComponent(panelRenameTemplates, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonRenameTemplate)
                .addContainerGap())
        );

        tabbedPane.addTab(JptBundle.INSTANCE.getString("RenameDialog.panelTemplates.TabConstraints.tabTitle"), panelTemplates); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 597, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
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

private void textFieldDelim2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldDelim2KeyReleased
    handleTextFieldDelim2KeyReleased();
}//GEN-LAST:event_textFieldDelim2KeyReleased

private void textFieldInTheMiddleKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldInTheMiddleKeyReleased
    handleTextFieldInTheMiddleKeyReleased();
}//GEN-LAST:event_textFieldInTheMiddleKeyReleased

private void textFieldDelim1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldDelim1KeyReleased
    handleTextFieldDelim1KeyReleased();
}//GEN-LAST:event_textFieldDelim1KeyReleased

private void textFieldAtBeginKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldAtBeginKeyReleased
    handleTextFieldAtBeginKeyReleased();
}//GEN-LAST:event_textFieldAtBeginKeyReleased

private void spinnerNumberCountStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinnerNumberCountStateChanged
    handleSpinnerNumberCountStateChanged();
}//GEN-LAST:event_spinnerNumberCountStateChanged

private void spinnerNumberStepWidthStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinnerNumberStepWidthStateChanged
    handleSpinnerNumberStepWidthStateChanged();
}//GEN-LAST:event_spinnerNumberStepWidthStateChanged

private void spinnerStartNumberStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinnerStartNumberStateChanged
    handleSpinnerStartNumberStateChanged();
}//GEN-LAST:event_spinnerStartNumberStateChanged

private void buttonRenameTemplateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRenameTemplateActionPerformed
    renameViaTemplate();
}//GEN-LAST:event_buttonRenameTemplateActionPerformed

private void comboBoxAtBeginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxAtBeginActionPerformed
    handleComboBoxAtBeginActionPerformed();
}//GEN-LAST:event_comboBoxAtBeginActionPerformed

private void comboBoxInTheMiddleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxInTheMiddleActionPerformed
    handleComboBoxInTheMiddleActionPerformed();
}//GEN-LAST:event_comboBoxInTheMiddleActionPerformed

private void comboBoxAtEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxAtEndActionPerformed
    handleComboBoxAtEndActionPerformed();
}//GEN-LAST:event_comboBoxAtEndActionPerformed

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    handleWindowClosing();
}//GEN-LAST:event_formWindowClosing

private void panelInputNameComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_panelInputNameComponentShown
    handlePanelInputNameComponentShown();
}//GEN-LAST:event_panelInputNameComponentShown

private void buttonSaveRenameTemplateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveRenameTemplateActionPerformed
    saveAsRenameTemplate();
}//GEN-LAST:event_buttonSaveRenameTemplateActionPerformed

private void buttonRenameRenameTemplateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRenameRenameTemplateActionPerformed
    renameRenameTemplate();
}//GEN-LAST:event_buttonRenameRenameTemplateActionPerformed

private void buttonDeleteRenameTemplateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteRenameTemplateActionPerformed
    deleteRenameTemplate();
}//GEN-LAST:event_buttonDeleteRenameTemplateActionPerformed

private void comboBoxRenameTemplatesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxRenameTemplatesActionPerformed
    handleComboBoxRenameTemplatesActionPerformed();
}//GEN-LAST:event_comboBoxRenameTemplatesActionPerformed

private void buttonUpdateRenameTemplateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonUpdateRenameTemplateActionPerformed
    updateRenameTemplate();
}//GEN-LAST:event_buttonUpdateRenameTemplateActionPerformed

private void comboBoxDateDelimiterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxDateDelimiterActionPerformed
    handleComboBoxDateDelimiterActionPerformed();
}//GEN-LAST:event_comboBoxDateDelimiterActionPerformed

private void textFieldAtEndKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldAtEndKeyReleased
    handleTextFieldAtEndKeyReleased();
}//GEN-LAST:event_textFieldAtEndKeyReleased

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
    private javax.swing.JButton buttonDeleteRenameTemplate;
    private javax.swing.JButton buttonNextFile;
    private javax.swing.JButton buttonRename;
    private javax.swing.JButton buttonRenameRenameTemplate;
    private javax.swing.JButton buttonRenameTemplate;
    private javax.swing.JButton buttonSaveRenameTemplate;
    private javax.swing.JButton buttonUpdateRenameTemplate;
    private javax.swing.JComboBox comboBoxAtBegin;
    private javax.swing.JComboBox comboBoxAtEnd;
    private javax.swing.JComboBox comboBoxDateDelimiter;
    private javax.swing.JComboBox comboBoxInTheMiddle;
    private javax.swing.JComboBox comboBoxRenameTemplates;
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
    private javax.swing.JPanel panelRenameTemplates;
    private javax.swing.JPanel panelTemplates;
    private de.elmar_baumann.lib.component.ImagePanel panelThumbnail;
    private javax.swing.JSpinner spinnerNumberCount;
    private javax.swing.JSpinner spinnerNumberStepWidth;
    private javax.swing.JSpinner spinnerStartNumber;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTextField textFieldAtBegin;
    private javax.swing.JTextField textFieldAtEnd;
    private javax.swing.JTextField textFieldDelim1;
    private javax.swing.JTextField textFieldDelim2;
    private javax.swing.JTextField textFieldInTheMiddle;
    private javax.swing.JTextField textFieldNewName;
    // End of variables declaration//GEN-END:variables
}
