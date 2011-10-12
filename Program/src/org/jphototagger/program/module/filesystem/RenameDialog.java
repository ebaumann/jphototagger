package org.jphototagger.program.module.filesystem;

import java.awt.Container;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.filechooser.FileSystemView;

import org.bushe.swing.event.EventBus;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.openide.util.Lookup;

import org.jphototagger.api.file.event.FileRenamedEvent;
import org.jphototagger.domain.thumbnails.ThumbnailProvider;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesHints;
import org.jphototagger.domain.repository.RenameTemplatesRepository;
import org.jphototagger.domain.repository.Repository;
import org.jphototagger.domain.repository.event.renametemplates.RenameTemplateDeletedEvent;
import org.jphototagger.domain.repository.event.renametemplates.RenameTemplateInsertedEvent;
import org.jphototagger.domain.repository.event.renametemplates.RenameTemplateUpdatedEvent;
import org.jphototagger.domain.templates.RenameTemplate;
import org.jphototagger.image.ImageFileType;
import org.jphototagger.lib.swing.util.ComboBoxUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.swing.Dialog;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.xmp.XmpMetadata;

/**
 * Dialog for renaming filenames.
 *
 * @author Elmar Baumann
 */
public final class RenameDialog extends Dialog implements ListDataListener {

    private static final long serialVersionUID = 1L;
    private static final String KEY_SEL_TEMPLATE = "RenameDialog.SelectedTemplate";
    private final transient FilenameFormatArray filenameFormatArray = new FilenameFormatArray();
    private List<File> imageFiles = new ArrayList<File>();
    private static final Logger LOGGER = Logger.getLogger(RenameDialog.class.getName());
    private int fileIndex = 0;
    private boolean lockClose = false;
    private boolean cancel = false;
    private transient boolean listen = true;
    private final ThumbnailProvider tnProvider = Lookup.getDefault().lookup(ThumbnailProvider.class);

    public RenameDialog() {
        super(GUI.getAppFrame(), true);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setComboBoxModels();
        setHelpPage();
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

    private void setHelpPage() {
        // Has to be localized!
        setHelpContentsUrl("/org/jphototagger/program/resource/doc/de/contents.xml");
        setHelpPageUrl("rename_images.html");
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
     * Sets the image files to rename.
     *
     * @param imageFiles image files
     */
    public void setImageFiles(List<File> imageFiles) {
        if (imageFiles == null) {
            throw new NullPointerException("imageFiles == null");
        }

        this.imageFiles = new ArrayList<File>(imageFiles);
    }

    public synchronized void notifyFileSystemListeners(File fromImageFile, File toImageFile) {
        if (fromImageFile == null) {
            throw new NullPointerException("fromImageFile == null");
        }

        if (toImageFile == null) {
            throw new NullPointerException("toImageFile == null");
        }

        EventBus.publish(new FileRenamedEvent(this, fromImageFile, toImageFile));
    }

    private boolean renameImageFile(File fromImageFile, File toImageFile) {
        boolean renamed = fromImageFile.renameTo(toImageFile);

        if (renamed) {
            renameXmpFileOfImageFile(fromImageFile, toImageFile);
        }

        return renamed;
    }

    private void renameXmpFileOfImageFile(File fromImageFile, File toImageFile) {
        File fromXmpFile = XmpMetadata.getSidecarFile(fromImageFile);

        if (fromXmpFile != null) {
            File toXmpFile = XmpMetadata.suggestSidecarFile(toImageFile);

            if (fromXmpFile.exists()) {
                if (!toXmpFile.delete()) {
                    LOGGER.log(Level.WARNING, "XMP file ''{0}'' couldn''t be deleted!", toXmpFile);
                }
            }

            if (!fromXmpFile.renameTo(toXmpFile)) {
                LOGGER.log(Level.WARNING, "XMP file ''{0}'' couldn''t be renamed to ''{1}''!", new Object[]{fromXmpFile, toXmpFile});
            }
        }
    }

    private void refreshThumbnailsPanel(int countRenamed) {
        if (countRenamed > 0) {
            GUI.refreshThumbnailsPanel();
        }
    }

    private void renameViaTemplate() {
        lockClose = true;
        tabbedPane.setEnabledAt(1, false);

        int countRenamed = 0;
        int size = imageFiles.size();

        for (int i = 0; !cancel && (i < size); i++) {
            fileIndex = i;

            File oldFile = imageFiles.get(i);
            String parent = oldFile.getParent();

            filenameFormatArray.setFile(oldFile);

            File newFile = new File(((parent == null)
                                     ? ""
                                     : parent + File.separator) + filenameFormatArray.format());

            if (checkNewFileDoesNotExist(newFile) && renameImageFile(oldFile, newFile)) {
                imageFiles.set(i, newFile);
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

        if ((fileIndex >= 0) && (fileIndex < imageFiles.size())) {
            File oldFile = imageFiles.get(fileIndex);

            if (canRenameViaInput()) {
                File newFile = getNewFileViaInput();

                if (renameImageFile(oldFile, newFile)) {
                    imageFiles.set(fileIndex, newFile);
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

        if (fileIndex > imageFiles.size() - 1) {
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
                                     : File.separator) + textFieldToName.getText().trim());
    }

    private boolean canRenameViaInput() {
        File oldFile = imageFiles.get(fileIndex);
        File newFile = getNewFileViaInput();

        return checkNewFilenameIsDefined()
               && checkNamesNotEquals(oldFile, newFile)
               && checkNewFileDoesNotExist(newFile);
    }

    private boolean checkNewFilenameIsDefined() {
        String input = textFieldToName.getText().trim();
        boolean defined = !input.isEmpty();

        if (!defined) {
            String message = Bundle.getString(RenameDialog.class, "RenameDialog.Error.InvalidInput");

            MessageDisplayer.error(this, message);
        }

        return defined;
    }

    private boolean checkNamesNotEquals(File oldFile, File newFile) {
        boolean equals = newFile.getAbsolutePath().equals(oldFile.getAbsolutePath());

        if (equals) {
            String message = Bundle.getString(RenameDialog.class, "RenameDialog.Error.FilenamesEquals");
            MessageDisplayer.error(this, message);
        }

        return !equals;
    }

    private boolean checkNewFileDoesNotExist(File file) {
        boolean exists = file.exists();

        if (exists) {
            String message = Bundle.getString(RenameDialog.class, "RenameDialog.Error.NewFileExists", file.getName());

            MessageDisplayer.error(this, message);
        }

        return !exists;
    }

    private void setCurrentFilenameToInputPanel() {
        if ((fileIndex >= 0) && (fileIndex < imageFiles.size())) {
            File file = imageFiles.get(fileIndex);

            setDirectoryNameLabel(file);
            labelFromName.setText(file.getName());
            textFieldToName.setText(file.getName());
            setThumbnail(file);
            textFieldToName.requestFocus();
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
        if (imageFiles.size() > 0) {
            File file = imageFiles.get(0);

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

        if (ImageFileType.isJpegFile(file.getName())) {
            thumbnail = tnProvider.getThumbnail(file);
        }

        if (thumbnail != null) {
            panelThumbnail.setImage(thumbnail);
            panelThumbnail.repaint();
        }
    }

    private void errorMessageNotRenamed(String filename) {
        String message = Bundle.getString(RenameDialog.class, "RenameDialog.Confirm.RenameNextFile", filename);

        if (!MessageDisplayer.confirmYesNo(this, message)) {
            cancel = true;
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
            Preferences storage = Lookup.getDefault().lookup(Preferences.class);

            storage.setComponent(this, new PreferencesHints(PreferencesHints.Option.SET_TABBED_PANE_CONTENT));
        }

        super.setVisible(visible);
    }

    private void readProperties() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        storage.applyComponentSettings(this, new PreferencesHints(PreferencesHints.Option.SET_TABBED_PANE_CONTENT));

        if (!tabbedPane.isEnabledAt(1)) {
            tabbedPane.setSelectedComponent(panelInputName);
        }

        storage.applySelectedIndex(KEY_SEL_TEMPLATE, comboBoxRenameTemplates);
    }

    private void setEnabledConstantTextFields() {
        textFieldAtBegin.setEditable(comboBoxAtBegin.getSelectedItem() instanceof FilenameFormatConstantString);
        textFieldInTheMiddle.setEditable(comboBoxInTheMiddle.getSelectedItem() instanceof FilenameFormatConstantString);
        textFieldAtEnd.setEditable(comboBoxAtEnd.getSelectedItem() instanceof FilenameFormatConstantString);
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
        spinnerStartNumber.getModel().setValue(template.getStartNumber());
        spinnerNumberStepWidth.getModel().setValue(template.getStepWidth());
        spinnerNumberCount.getModel().setValue(template.getNumberCount());
        ComboBoxUtil.selectString(comboBoxDateDelimiter.getModel(), template.getDateDelimiter());
        select(template.getFormatClassAtBegin(), comboBoxAtBegin);
        textFieldDelim1.setText(template.getDelimiter1());
        select(template.getFormatClassInTheMiddle(), comboBoxInTheMiddle);
        textFieldDelim2.setText(template.getDelimiter2());
        select(template.getFormatClassAtEnd(), comboBoxAtEnd);
        textFieldAtBegin.setText(template.getTextAtBegin());
        textFieldInTheMiddle.setText(template.getTextInTheMiddle());
        textFieldAtEnd.setText(template.getTextAtEnd());
        listen = true;
    }

    private RenameTemplate createTemplate() {
        RenameTemplate template = new RenameTemplate();

        setValuesToTemplate(template);

        return template;
    }

    private void setValuesToTemplate(RenameTemplate template) {
        template.setStartNumber((Integer) spinnerStartNumber.getModel().getValue());
        template.setStepWidth((Integer) spinnerNumberStepWidth.getModel().getValue());
        template.setNumberCount((Integer) spinnerNumberCount.getModel().getValue());
        template.setDateDelimiter(getDateDelimiter());
        template.setFormatClassAtBegin(comboBoxAtBegin.getSelectedItem().getClass());
        template.setDelimiter1(textFieldDelim1.getText());
        template.setFormatClassInTheMiddle(comboBoxInTheMiddle.getSelectedItem().getClass());
        template.setDelimiter2(textFieldDelim2.getText());
        template.setFormatClassAtEnd(comboBoxAtEnd.getSelectedItem().getClass());
        template.setTextAtBegin(textFieldAtBegin.getText());
        template.setTextInTheMiddle(textFieldInTheMiddle.getText());
        template.setTextAtEnd(textFieldAtEnd.getText());
    }

    private void select(Class<?> formatClass, JComboBox comboBox) {
        ComboBoxModel model = comboBox.getModel();
        int size = model.getSize();
        boolean selected = false;
        int index = 0;

        while (!selected && (index < size)) {
            Object element = model.getElementAt(index);
            index++;

            if (element.getClass().equals(formatClass)) {
                model.setSelectedItem(element);
                selected = true;
            }
        }
    }

    private void saveAsRenameTemplate() {
        RenameTemplate template = createTemplate();

        RenameTemplateUtil.insert(template);
    }

    private void renameRenameTemplate() {
        Object selItem = comboBoxRenameTemplates.getSelectedItem();

        if (selItem instanceof RenameTemplate) {
            RenameTemplateUtil.rename((RenameTemplate) selItem);
        }
    }

    private void deleteRenameTemplate() {
        Object selItem = comboBoxRenameTemplates.getSelectedItem();

        if (selItem instanceof RenameTemplate) {
            RenameTemplateUtil.delete((RenameTemplate) selItem);
        }
    }

    private void updateRenameTemplate() {
        Object selItem = comboBoxRenameTemplates.getSelectedItem();

        if (selItem instanceof RenameTemplate) {
            RenameTemplate template = (RenameTemplate) selItem;

            setValuesToTemplate(template);
            RenameTemplateUtil.update(template);
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
    public void intervalAdded(ListDataEvent evt) {
        setEnabledRenameTemplateButtons();
    }

    @Override
    public void intervalRemoved(ListDataEvent evt) {
        setEnabledRenameTemplateButtons();
    }

    @Override
    public void contentsChanged(ListDataEvent evt) {
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
            Preferences storage = Lookup.getDefault().lookup(Preferences.class);

            storage.setSelectedIndex(KEY_SEL_TEMPLATE, comboBoxRenameTemplates);
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

    private static class RenameTemplatesComboBoxModel extends DefaultComboBoxModel {

        private static final long serialVersionUID = 1L;
        private final RenameTemplatesRepository renameTemplatesRepo = Lookup.getDefault().lookup(RenameTemplatesRepository.class);

        private RenameTemplatesComboBoxModel() {
            addElements();
            listen();
        }

        private void listen() {
            AnnotationProcessor.process(this);
        }

        private void addElements() {
            Repository repo = Lookup.getDefault().lookup(Repository.class);

            if (repo == null || !repo.isInit()) {
                return;
            }

            for (RenameTemplate template : renameTemplatesRepo.findAllRenameTemplates()) {
                addElement(template);
            }
        }

        private void updateTemplate(RenameTemplate template) {
            int index = getIndexOf(template);

            if (index >= 0) {
                ((RenameTemplate) getElementAt(index)).set(template);
                fireContentsChanged(this, index, index);
            }
        }

        private void insertTemplate(RenameTemplate template) {
            addElement(template);
            setSelectedItem(template);
        }

        private void deleteTemplate(RenameTemplate template) {
            removeElement(template);
        }

        @EventSubscriber(eventClass = RenameTemplateDeletedEvent.class)
        public void templateDeleted(final RenameTemplateDeletedEvent evt) {
            deleteTemplate(evt.getTemplate());
        }

        @EventSubscriber(eventClass = RenameTemplateInsertedEvent.class)
        public void templateInserted(final RenameTemplateInsertedEvent evt) {
            insertTemplate(evt.getTemplate());
        }

        @EventSubscriber(eventClass = RenameTemplateUpdatedEvent.class)
        public void templateUpdated(final RenameTemplateUpdatedEvent evt) {
            updateTemplate(evt.getTemplate());
        }
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    private void initComponents() {//GEN-BEGIN:initComponents

        tabbedPane = new javax.swing.JTabbedPane();
        panelInputName = new javax.swing.JPanel();
        labelDirectoryPrompt = new javax.swing.JLabel();
        labelDirectory = new javax.swing.JLabel();
        panelBorder = new javax.swing.JPanel();
        panelThumbnail = new org.jphototagger.lib.swing.ImagePanel();
        labelFromNamePrompt = new javax.swing.JLabel();
        labelFromName = new javax.swing.JLabel();
        labelToNamePrompt = new javax.swing.JLabel();
        textFieldToName = new javax.swing.JTextField();
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
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/module/filesystem/Bundle"); // NOI18N
        setTitle(bundle.getString("RenameDialog.title")); // NOI18N
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        tabbedPane.setName("tabbedPane"); // NOI18N

        panelInputName.setName("panelInputName"); // NOI18N
        panelInputName.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                panelInputNameComponentShown(evt);
            }
        });

        labelDirectoryPrompt.setText(bundle.getString("RenameDialog.labelDirectoryPrompt.text")); // NOI18N
        labelDirectoryPrompt.setName("labelDirectoryPrompt"); // NOI18N

        labelDirectory.setForeground(new java.awt.Color(0, 175, 0));
        labelDirectory.setText(bundle.getString("RenameDialog.labelDirectory.text")); // NOI18N
        labelDirectory.setName("labelDirectory"); // NOI18N

        panelBorder.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        panelBorder.setName("panelBorder"); // NOI18N

        panelThumbnail.setEnabled(false);
        panelThumbnail.setFocusable(false);
        panelThumbnail.setName("panelThumbnail"); // NOI18N
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

        labelFromNamePrompt.setText(bundle.getString("RenameDialog.labelFromNamePrompt.text")); // NOI18N
        labelFromNamePrompt.setName("labelFromNamePrompt"); // NOI18N

        labelFromName.setForeground(new java.awt.Color(0, 175, 0));
        labelFromName.setText(bundle.getString("RenameDialog.labelFromName.text")); // NOI18N
        labelFromName.setName("labelFromName"); // NOI18N

        labelToNamePrompt.setLabelFor(textFieldToName);
        labelToNamePrompt.setText(bundle.getString("RenameDialog.labelToNamePrompt.text")); // NOI18N
        labelToNamePrompt.setName("labelToNamePrompt"); // NOI18N

        textFieldToName.setName("textFieldToName"); // NOI18N

        buttonNextFile.setText(bundle.getString("RenameDialog.buttonNextFile.text")); // NOI18N
        buttonNextFile.setName("buttonNextFile"); // NOI18N
        buttonNextFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonNextFileActionPerformed(evt);
            }
        });

        buttonRename.setText(bundle.getString("RenameDialog.buttonRename.text")); // NOI18N
        buttonRename.setName("buttonRename"); // NOI18N
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
                            .addComponent(labelToNamePrompt, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelFromName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                            .addComponent(labelFromNamePrompt, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(textFieldToName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelInputNameLayout.createSequentialGroup()
                        .addContainerGap(370, Short.MAX_VALUE)
                        .addComponent(buttonNextFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonRename))
                    .addGroup(panelInputNameLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelInputNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelDirectory, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
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
                        .addComponent(labelFromNamePrompt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelFromName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelToNamePrompt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textFieldToName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 287, Short.MAX_VALUE)
                        .addGroup(panelInputNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(buttonRename)
                            .addComponent(buttonNextFile))))
                .addContainerGap())
        );

        tabbedPane.addTab(bundle.getString("RenameDialog.panelInputName.TabConstraints.tabTitle"), panelInputName); // NOI18N

        panelTemplates.setName("panelTemplates"); // NOI18N

        panelNumbers.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("RenameDialog.panelNumbers.border.title"))); // NOI18N
        panelNumbers.setName("panelNumbers"); // NOI18N

        labelStartNumber.setLabelFor(spinnerStartNumber);
        labelStartNumber.setText(bundle.getString("RenameDialog.labelStartNumber.text")); // NOI18N
        labelStartNumber.setName("labelStartNumber"); // NOI18N

        spinnerStartNumber.setModel(new SpinnerNumberModel(1, 1, 999999, 1));
        spinnerStartNumber.setName("spinnerStartNumber"); // NOI18N
        spinnerStartNumber.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerStartNumberStateChanged(evt);
            }
        });

        labelNumberStepWidth.setLabelFor(spinnerNumberStepWidth);
        labelNumberStepWidth.setText(bundle.getString("RenameDialog.labelNumberStepWidth.text")); // NOI18N
        labelNumberStepWidth.setName("labelNumberStepWidth"); // NOI18N

        spinnerNumberStepWidth.setModel(new SpinnerNumberModel(1, 1, 99, 1));
        spinnerNumberStepWidth.setName("spinnerNumberStepWidth"); // NOI18N
        spinnerNumberStepWidth.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerNumberStepWidthStateChanged(evt);
            }
        });

        labelNumberCount.setLabelFor(spinnerNumberCount);
        labelNumberCount.setText(bundle.getString("RenameDialog.labelNumberCount.text")); // NOI18N
        labelNumberCount.setName("labelNumberCount"); // NOI18N

        spinnerNumberCount.setModel(new SpinnerNumberModel(3, 1, 7, 1));
        spinnerNumberCount.setName("spinnerNumberCount"); // NOI18N
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

        panelOther.setName("panelOther"); // NOI18N

        labelDateDelim.setText(bundle.getString("RenameDialog.labelDateDelim.text")); // NOI18N
        labelDateDelim.setName("labelDateDelim"); // NOI18N

        comboBoxDateDelimiter.setModel(new DefaultComboBoxModel(new Object[] {"", "-", ".", "/"}));
        comboBoxDateDelimiter.setName("comboBoxDateDelimiter"); // NOI18N
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
                .addContainerGap(329, Short.MAX_VALUE))
        );
        panelOtherLayout.setVerticalGroup(
            panelOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOtherLayout.createSequentialGroup()
                .addGroup(panelOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelDateDelim)
                    .addComponent(comboBoxDateDelimiter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelDefineName.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("RenameDialog.panelDefineName.border.title"))); // NOI18N
        panelDefineName.setName("panelDefineName"); // NOI18N

        labelAtBegin.setLabelFor(comboBoxAtBegin);
        labelAtBegin.setText(bundle.getString("RenameDialog.labelAtBegin.text")); // NOI18N
        labelAtBegin.setName("labelAtBegin"); // NOI18N

        comboBoxAtBegin.setName("comboBoxAtBegin"); // NOI18N
        comboBoxAtBegin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxAtBeginActionPerformed(evt);
            }
        });

        textFieldAtBegin.setName("textFieldAtBegin"); // NOI18N
        textFieldAtBegin.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldAtBeginKeyReleased(evt);
            }
        });

        labelDelim1.setLabelFor(textFieldDelim1);
        labelDelim1.setText(bundle.getString("RenameDialog.labelDelim1.text")); // NOI18N
        labelDelim1.setName("labelDelim1"); // NOI18N

        textFieldDelim1.setColumns(1);
        textFieldDelim1.setName("textFieldDelim1"); // NOI18N
        textFieldDelim1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldDelim1KeyReleased(evt);
            }
        });

        labelInTheMid.setLabelFor(comboBoxInTheMiddle);
        labelInTheMid.setText(bundle.getString("RenameDialog.labelInTheMid.text")); // NOI18N
        labelInTheMid.setName("labelInTheMid"); // NOI18N

        comboBoxInTheMiddle.setName("comboBoxInTheMiddle"); // NOI18N
        comboBoxInTheMiddle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxInTheMiddleActionPerformed(evt);
            }
        });

        textFieldInTheMiddle.setName("textFieldInTheMiddle"); // NOI18N
        textFieldInTheMiddle.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldInTheMiddleKeyReleased(evt);
            }
        });

        labelDelim2.setLabelFor(textFieldDelim2);
        labelDelim2.setText(bundle.getString("RenameDialog.labelDelim2.text")); // NOI18N
        labelDelim2.setName("labelDelim2"); // NOI18N

        textFieldDelim2.setColumns(1);
        textFieldDelim2.setName("textFieldDelim2"); // NOI18N
        textFieldDelim2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldDelim2KeyReleased(evt);
            }
        });

        labelAtEnd.setLabelFor(comboBoxAtEnd);
        labelAtEnd.setText(bundle.getString("RenameDialog.labelAtEnd.text")); // NOI18N
        labelAtEnd.setName("labelAtEnd"); // NOI18N

        comboBoxAtEnd.setName("comboBoxAtEnd"); // NOI18N
        comboBoxAtEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxAtEndActionPerformed(evt);
            }
        });

        textFieldAtEnd.setName("textFieldAtEnd"); // NOI18N
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
                    .addComponent(textFieldAtBegin, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                    .addComponent(comboBoxAtBegin, 0, 134, Short.MAX_VALUE)
                    .addComponent(labelAtBegin))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDefineNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textFieldDelim1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelDelim1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDefineNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textFieldInTheMiddle, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                    .addComponent(comboBoxInTheMiddle, 0, 135, Short.MAX_VALUE)
                    .addComponent(labelInTheMid))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDefineNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textFieldDelim2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelDelim2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDefineNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelAtEnd)
                    .addComponent(comboBoxAtEnd, 0, 149, Short.MAX_VALUE)
                    .addComponent(textFieldAtEnd, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE))
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

        panelExample.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("RenameDialog.panelExample.border.title"))); // NOI18N
        panelExample.setName("panelExample"); // NOI18N

        labelBefore.setText(bundle.getString("RenameDialog.labelBefore.text")); // NOI18N
        labelBefore.setName("labelBefore"); // NOI18N

        labelBeforeFilename.setForeground(new java.awt.Color(0, 0, 175));
        labelBeforeFilename.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelBeforeFilename.setName("labelBeforeFilename"); // NOI18N

        labelAfter.setText(bundle.getString("RenameDialog.labelAfter.text")); // NOI18N
        labelAfter.setName("labelAfter"); // NOI18N

        labelAfterFilename.setForeground(new java.awt.Color(0, 0, 255));
        labelAfterFilename.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelAfterFilename.setName("labelAfterFilename"); // NOI18N

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
                    .addComponent(labelAfterFilename, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
                    .addComponent(labelBeforeFilename, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE))
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

        panelRenameTemplates.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("RenameDialog.panelRenameTemplates.border.title"))); // NOI18N
        panelRenameTemplates.setName("panelRenameTemplates"); // NOI18N

        comboBoxRenameTemplates.setModel(new RenameTemplatesComboBoxModel());
        comboBoxRenameTemplates.setName("comboBoxRenameTemplates"); // NOI18N
        comboBoxRenameTemplates.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxRenameTemplatesActionPerformed(evt);
            }
        });

        buttonSaveRenameTemplate.setText(bundle.getString("RenameDialog.buttonSaveRenameTemplate.text")); // NOI18N
        buttonSaveRenameTemplate.setToolTipText(bundle.getString("RenameDialog.buttonSaveRenameTemplate.toolTipText")); // NOI18N
        buttonSaveRenameTemplate.setName("buttonSaveRenameTemplate"); // NOI18N
        buttonSaveRenameTemplate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveRenameTemplateActionPerformed(evt);
            }
        });

        buttonRenameRenameTemplate.setText(bundle.getString("RenameDialog.buttonRenameRenameTemplate.text")); // NOI18N
        buttonRenameRenameTemplate.setToolTipText(bundle.getString("RenameDialog.buttonRenameRenameTemplate.toolTipText")); // NOI18N
        buttonRenameRenameTemplate.setEnabled(false);
        buttonRenameRenameTemplate.setName("buttonRenameRenameTemplate"); // NOI18N
        buttonRenameRenameTemplate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRenameRenameTemplateActionPerformed(evt);
            }
        });

        buttonDeleteRenameTemplate.setText(bundle.getString("RenameDialog.buttonDeleteRenameTemplate.text")); // NOI18N
        buttonDeleteRenameTemplate.setToolTipText(bundle.getString("RenameDialog.buttonDeleteRenameTemplate.toolTipText")); // NOI18N
        buttonDeleteRenameTemplate.setEnabled(false);
        buttonDeleteRenameTemplate.setName("buttonDeleteRenameTemplate"); // NOI18N
        buttonDeleteRenameTemplate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteRenameTemplateActionPerformed(evt);
            }
        });

        buttonUpdateRenameTemplate.setText(bundle.getString("RenameDialog.buttonUpdateRenameTemplate.text")); // NOI18N
        buttonUpdateRenameTemplate.setToolTipText(bundle.getString("RenameDialog.buttonUpdateRenameTemplate.toolTipText")); // NOI18N
        buttonUpdateRenameTemplate.setEnabled(false);
        buttonUpdateRenameTemplate.setName("buttonUpdateRenameTemplate"); // NOI18N
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
                        .addComponent(comboBoxRenameTemplates, 0, 540, Short.MAX_VALUE)
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

        buttonRenameTemplate.setText(bundle.getString("RenameDialog.buttonRenameTemplate.text")); // NOI18N
        buttonRenameTemplate.setName("buttonRenameTemplate"); // NOI18N
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

        tabbedPane.addTab(bundle.getString("RenameDialog.panelTemplates.TabConstraints.tabTitle"), panelTemplates); // NOI18N

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
    }//GEN-END:initComponents

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
    private javax.swing.JLabel labelFromName;
    private javax.swing.JLabel labelFromNamePrompt;
    private javax.swing.JLabel labelInTheMid;
    private javax.swing.JLabel labelNumberCount;
    private javax.swing.JLabel labelNumberStepWidth;
    private javax.swing.JLabel labelStartNumber;
    private javax.swing.JLabel labelToNamePrompt;
    private javax.swing.JPanel panelBorder;
    private javax.swing.JPanel panelDefineName;
    private javax.swing.JPanel panelExample;
    private javax.swing.JPanel panelInputName;
    private javax.swing.JPanel panelNumbers;
    private javax.swing.JPanel panelOther;
    private javax.swing.JPanel panelRenameTemplates;
    private javax.swing.JPanel panelTemplates;
    private org.jphototagger.lib.swing.ImagePanel panelThumbnail;
    private javax.swing.JSpinner spinnerNumberCount;
    private javax.swing.JSpinner spinnerNumberStepWidth;
    private javax.swing.JSpinner spinnerStartNumber;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTextField textFieldAtBegin;
    private javax.swing.JTextField textFieldAtEnd;
    private javax.swing.JTextField textFieldDelim1;
    private javax.swing.JTextField textFieldDelim2;
    private javax.swing.JTextField textFieldInTheMiddle;
    private javax.swing.JTextField textFieldToName;
    // End of variables declaration//GEN-END:variables
}
