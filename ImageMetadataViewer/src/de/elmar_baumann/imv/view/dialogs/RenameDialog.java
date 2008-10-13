package de.elmar_baumann.imv.view.dialogs;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.controller.filesystem.DateFilenameFormat;
import de.elmar_baumann.imv.controller.filesystem.EmptyFilenameFormat;
import de.elmar_baumann.imv.controller.filesystem.NameFilenameFormat;
import de.elmar_baumann.imv.controller.filesystem.NumberSequenceFilenameFormat;
import de.elmar_baumann.imv.controller.filesystem.StringFilenameFormat;
import de.elmar_baumann.imv.event.ErrorEvent;
import de.elmar_baumann.imv.event.RenameFileAction;
import de.elmar_baumann.imv.event.RenameFileListener;
import de.elmar_baumann.imv.event.listener.ErrorListeners;
import de.elmar_baumann.imv.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imv.image.thumbnail.ThumbnailUtil;
import de.elmar_baumann.imv.io.FileType;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.persistence.PersistentAppSizes;
import de.elmar_baumann.lib.persistence.PersistentSettings;
import de.elmar_baumann.lib.persistence.PersistentSettingsHints;
import java.awt.Image;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;

/**
 * Dialog for renaming filenames.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 */
public class RenameDialog extends javax.swing.JDialog {

    private Type type;
    private HashMap<Type, JPanel> panelOfType = new HashMap<Type, JPanel>();
    private List<String> filenames = new ArrayList<String>();
    private List<RenameFileListener> renameFileListeners = new ArrayList<RenameFileListener>();
    private int filenameIndex = 0;

    /**
     * Type of renaming
     */
    public enum Type {

        /**
         * Renaming via input of each new filename
         */
        Input,
        /**
         * Renaming via templates
         */
        Templates
    }

    private void initPanelOfType() {
        panelOfType.put(Type.Input, panelInputName);
        panelOfType.put(Type.Templates, panelTemplates);
    }

    /**
     * Constructor.
     * 
     * @param type  type
     */
    public RenameDialog(Type type) {
        super((java.awt.Frame) null, false);
        this.type = type;
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setIconImages(AppSettings.getAppIcons());
        initPanelOfType();
        tabbedPane.setSelectedComponent(panelOfType.get(type));
        disableOtherPanels();
        setComboBoxModels();
    }

    private void disableOtherPanels() {
        int count = tabbedPane.getComponentCount();
        JPanel selected = panelOfType.get(type);
        for (int i = 0; i < count; i++) {
            if (tabbedPane.getComponentAt(i) != selected) {
                tabbedPane.setEnabledAt(i, false);
            }
        }
    }

    private void setComboBoxModels() {
        comboBoxAtBegin.setModel(getComboBoxModel());
        comboBoxInTheMid.setModel(getComboBoxModel());
        comboBoxAtEnd.setModel(getComboBoxModel());
    }

    private ComboBoxModel getComboBoxModel() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        model.addElement(new StringFilenameFormat());
        model.addElement(new NumberSequenceFilenameFormat(1, 1, 4));
        model.addElement(new NameFilenameFormat());
        model.addElement(new DateFilenameFormat("-"));
        model.addElement(new EmptyFilenameFormat());
        return model;
    }

    /**
     * Sets the filenames to rename;
     * 
     * @param filenames  filenames
     */
    public void setFilenames(List<String> filenames) {
        this.filenames = filenames;
    }

    public void addRenameFileListener(RenameFileListener listener) {
        renameFileListeners.add(listener);
    }

    public void removeRenameFileListener(RenameFileListener listener) {
        renameFileListeners.remove(listener);
    }

    public void notifyRenameListeners(File oldFile, File newFile) {
        RenameFileAction action = new RenameFileAction(oldFile, newFile);
        for (RenameFileListener listener : renameFileListeners) {
            listener.actionPerformed(action);
        }
    }

    private void renameViaInput() {
        if (filenameIndex >= 0 && filenameIndex < filenames.size()) {
            File oldFile = new File(filenames.get(filenameIndex));
            if (canRenameViaInput()) {
                File newFile = getNewFileViaInput();
                if (renameFile(oldFile, newFile)) {
                    notifyRenameListeners(oldFile, newFile);
                    setCurrentFilenameToInputPanel();
                } else {
                    errorMessageNotRenamed(oldFile.getAbsolutePath());
                }
                setNextFileViaInput();
            }
        }
    }

    private void setNextFileViaInput() {
        filenameIndex++;
        if (filenameIndex > filenames.size() - 1) {
            setVisible(false);
            dispose();
        } else {
            setCurrentFilenameToInputPanel();
        }
    }

    private File getNewFileViaInput() {
        String directory = labelDirectory.getText();
        return new File(directory + (directory.isEmpty() ? "" : File.separator) +
            textFieldNewName.getText().trim());
    }

    private boolean canRenameViaInput() {
        return checkNewFilenameIsDefined() &&
            checkNamesNotEquals() &&
            checkNewFileNotExists(getNewFileViaInput());
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
                AppSettings.getMediumAppIcon());
        }
        return defined;
    }

    private boolean checkNamesNotEquals() {
        String newFilename = getNewFileViaInput().getName();
        String oldFilename = new File(filenames.get(filenameIndex)).getName();
        boolean equals = newFilename.equals(oldFilename);
        if (equals) {
            JOptionPane.showMessageDialog(
                null,
                Bundle.getString("RenameDialog.ErrorMessage.FilenamesEquals"),
                Bundle.getString("RenameDialog.ErrorMessage.FilenamesEquals.Title"),
                JOptionPane.ERROR_MESSAGE,
                AppSettings.getMediumAppIcon());
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
                AppSettings.getMediumAppIcon());
        }
        return !exists;
    }

    private void setCurrentFilenameToInputPanel() {
        if (filenameIndex >= 0 && filenameIndex < filenames.size()) {
            File file = new File(filenames.get(filenameIndex));
            setDirectoryNameLabel(file);
            setFilenameLabel(file);
            setThumbnail(file);
            textFieldNewName.requestFocus();
        }
    }

    private boolean renameFile(File oldFile, File newFile) {
        boolean renamed = oldFile.renameTo(newFile);
        if (renamed) {
            renameXmpFile(oldFile.getAbsolutePath(), newFile.getAbsolutePath());
        }
        return renamed;
    }

    private void renameViaTemplate() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void setExampleFilename() {
        throw new UnsupportedOperationException("Not yet implemented");
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
                    ErrorListeners.getInstance().notifyErrorListener(new ErrorEvent(msg.format(params), this));
                }
            }
            if (!oldXmpFile.renameTo(newXmpFile)) {
                MessageFormat msg = new MessageFormat(Bundle.getString("RenameDialog.ErrorMessage.XmpFileCouldNotBeRenamed"));
                Object params[] = {oldXmpFilename, newXmpFilename};
                ErrorListeners.getInstance().notifyErrorListener(new ErrorEvent(msg.format(params), this));
            }
        }
    }

    private void setDirectoryNameLabel(File file) {
        File dir = file.getParentFile();
        labelDirectory.setText(dir.getAbsolutePath());
    }

    private void setFilenameLabel(File file) {
        labelOldName.setText(file.getName());
    }

    synchronized private void setThumbnail(File file) {
        Image thumbnail = null;
        String filename = file.getAbsolutePath();
        if (FileType.isJpegFile(filename)) {
            thumbnail = ThumbnailUtil.getScaledImage(filename, panelThumbnail.getWidth());
        //thumbnail = ThumbnailUtil.getThumbnail(filename, panelThumbnail.getWidth(), true);
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
            AppSettings.getMediumAppIcon()) == JOptionPane.NO_OPTION) {
            setVisible(false);
            dispose();
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            PersistentAppSizes.getSizeAndLocation(this);
            PersistentSettings.getInstance().getComponent(this, getPersistentSettingsHints());
            if (panelOfType.get(type) == panelInputName) {
                setCurrentFilenameToInputPanel();
            }
        } else {
            PersistentAppSizes.setSizeAndLocation(this);
            PersistentSettings.getInstance().setComponent(this, getPersistentSettingsHints());
        }
        super.setVisible(visible);
    }

    private PersistentSettingsHints getPersistentSettingsHints() {
        return new PersistentSettingsHints();
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
        labelStartNumber = new javax.swing.JLabel();
        spinnerStartNumber = new javax.swing.JSpinner();
        labelNumberStepWidth = new javax.swing.JLabel();
        spinnerNumberStepWidth = new javax.swing.JSpinner();
        labelAtBegin = new javax.swing.JLabel();
        comboBoxAtBegin = new javax.swing.JComboBox();
        textFieldDelim1 = new javax.swing.JTextField();
        labelInTheMid = new javax.swing.JLabel();
        comboBoxInTheMid = new javax.swing.JComboBox();
        textFieldDelim2 = new javax.swing.JTextField();
        labelAtEnd = new javax.swing.JLabel();
        comboBoxAtEnd = new javax.swing.JComboBox();
        textFieldDelim3 = new javax.swing.JTextField();
        textFieldString1 = new javax.swing.JTextField();
        textFieldString2 = new javax.swing.JTextField();
        textFieldString3 = new javax.swing.JTextField();
        panelExample = new javax.swing.JPanel();
        labelBefore = new javax.swing.JLabel();
        labelBeforeFilename = new javax.swing.JLabel();
        labelAfter = new javax.swing.JLabel();
        labelAfterFilename = new javax.swing.JLabel();
        buttonRenameTemplate = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Bundle.getString("RenameDialog.title")); // NOI18N

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
                    .addComponent(labelDirectory, javax.swing.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE)
                    .addGroup(panelInputNameLayout.createSequentialGroup()
                        .addComponent(panelBorder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelInputNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(labelOldNamePrompt, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelNewNamePrompt, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelOldName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
                            .addComponent(textFieldNewName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabbedPane.addTab(Bundle.getString("RenameDialog.panelInputName.TabConstraints.tabTitle"), panelInputName); // NOI18N

        labelStartNumber.setText(Bundle.getString("RenameDialog.labelStartNumber.text")); // NOI18N

        spinnerStartNumber.setModel(new SpinnerNumberModel(0, 999999, 1, 1));
        spinnerStartNumber.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerStartNumberStateChanged(evt);
            }
        });

        labelNumberStepWidth.setText(Bundle.getString("RenameDialog.labelNumberStepWidth.text")); // NOI18N

        spinnerNumberStepWidth.setModel(new SpinnerNumberModel(0, 999999, 1, 1));
        spinnerNumberStepWidth.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerNumberStepWidthStateChanged(evt);
            }
        });

        labelAtBegin.setText(Bundle.getString("RenameDialog.labelAtBegin.text")); // NOI18N

        comboBoxAtBegin.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboBoxAtBegin.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                comboBoxAtBeginPropertyChange(evt);
            }
        });

        textFieldDelim1.setText(Bundle.getString("RenameDialog.textFieldDelim1.text")); // NOI18N
        textFieldDelim1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldDelim1KeyReleased(evt);
            }
        });

        labelInTheMid.setText(Bundle.getString("RenameDialog.labelInTheMid.text")); // NOI18N

        comboBoxInTheMid.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboBoxInTheMid.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                comboBoxInTheMidPropertyChange(evt);
            }
        });

        textFieldDelim2.setText(Bundle.getString("RenameDialog.textFieldDelim2.text")); // NOI18N
        textFieldDelim2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldDelim2KeyReleased(evt);
            }
        });

        labelAtEnd.setText(Bundle.getString("RenameDialog.labelAtEnd.text")); // NOI18N

        comboBoxAtEnd.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboBoxAtEnd.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                comboBoxAtEndPropertyChange(evt);
            }
        });

        textFieldDelim3.setText(Bundle.getString("RenameDialog.textFieldDelim3.text")); // NOI18N
        textFieldDelim3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldDelim3KeyReleased(evt);
            }
        });

        textFieldString1.setText(Bundle.getString("RenameDialog.textFieldString1.text")); // NOI18N
        textFieldString1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldString1KeyReleased(evt);
            }
        });

        textFieldString2.setText(Bundle.getString("RenameDialog.textFieldString2.text")); // NOI18N
        textFieldString2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldString2KeyReleased(evt);
            }
        });

        textFieldString3.setText(Bundle.getString("RenameDialog.textFieldString3.text")); // NOI18N
        textFieldString3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                textFieldString3MouseReleased(evt);
            }
        });

        panelExample.setBorder(javax.swing.BorderFactory.createTitledBorder(null, Bundle.getString("RenameDialog.panelExample.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        labelBefore.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        labelBefore.setText(Bundle.getString("RenameDialog.labelBefore.text")); // NOI18N

        labelBeforeFilename.setText(Bundle.getString("RenameDialog.labelBeforeFilename.text")); // NOI18N

        labelAfter.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        labelAfter.setText(Bundle.getString("RenameDialog.labelAfter.text")); // NOI18N

        labelAfterFilename.setText(Bundle.getString("RenameDialog.labelAfterFilename.text")); // NOI18N

        javax.swing.GroupLayout panelExampleLayout = new javax.swing.GroupLayout(panelExample);
        panelExample.setLayout(panelExampleLayout);
        panelExampleLayout.setHorizontalGroup(
            panelExampleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelExampleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelExampleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelBefore)
                    .addComponent(labelAfter))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelExampleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelBeforeFilename, javax.swing.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
                    .addComponent(labelAfterFilename, javax.swing.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE))
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
                .addGroup(panelTemplatesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelTemplatesLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelTemplatesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelTemplatesLayout.createSequentialGroup()
                                .addComponent(labelStartNumber)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spinnerStartNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(labelNumberStepWidth)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spinnerNumberStepWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelTemplatesLayout.createSequentialGroup()
                                .addComponent(labelAtBegin)
                                .addGap(27, 27, 27)
                                .addComponent(labelInTheMid)
                                .addGap(18, 18, 18)
                                .addComponent(labelAtEnd))
                            .addGroup(panelTemplatesLayout.createSequentialGroup()
                                .addGroup(panelTemplatesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(textFieldString1, javax.swing.GroupLayout.Alignment.LEADING, 0, 0, Short.MAX_VALUE)
                                    .addComponent(comboBoxAtBegin, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(panelTemplatesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelTemplatesLayout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(textFieldDelim1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(comboBoxInTheMid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTemplatesLayout.createSequentialGroup()
                                        .addGap(20, 20, 20)
                                        .addComponent(textFieldString2, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(textFieldDelim2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelTemplatesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(textFieldString3, 0, 0, Short.MAX_VALUE)
                                    .addComponent(comboBoxAtEnd, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textFieldDelim3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(panelExample, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTemplatesLayout.createSequentialGroup()
                        .addContainerGap(424, Short.MAX_VALUE)
                        .addComponent(buttonRenameTemplate)))
                .addContainerGap())
        );

        panelTemplatesLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {spinnerNumberStepWidth, spinnerStartNumber});

        panelTemplatesLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {comboBoxAtBegin, textFieldString1});

        panelTemplatesLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {comboBoxInTheMid, textFieldString2});

        panelTemplatesLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {comboBoxAtEnd, textFieldString3});

        panelTemplatesLayout.setVerticalGroup(
            panelTemplatesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTemplatesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelTemplatesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelStartNumber)
                    .addComponent(spinnerStartNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelNumberStepWidth)
                    .addComponent(spinnerNumberStepWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelTemplatesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelAtBegin)
                    .addComponent(labelInTheMid)
                    .addComponent(labelAtEnd))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelTemplatesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboBoxAtBegin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textFieldDelim1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboBoxInTheMid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textFieldDelim2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboBoxAtEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textFieldDelim3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelTemplatesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldString1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textFieldString2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textFieldString3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelExample, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonRenameTemplate)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        panelTemplatesLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {textFieldString1, textFieldString2, textFieldString3});

        tabbedPane.addTab(Bundle.getString("RenameDialog.panelTemplates.TabConstraints.tabTitle"), panelTemplates); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
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

private void buttonRenameTemplateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRenameTemplateActionPerformed
    renameViaTemplate();
}//GEN-LAST:event_buttonRenameTemplateActionPerformed

private void spinnerStartNumberStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinnerStartNumberStateChanged
    setExampleFilename();
}//GEN-LAST:event_spinnerStartNumberStateChanged

private void spinnerNumberStepWidthStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinnerNumberStepWidthStateChanged
    setExampleFilename();
}//GEN-LAST:event_spinnerNumberStepWidthStateChanged

private void comboBoxAtBeginPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_comboBoxAtBeginPropertyChange
    setExampleFilename();
}//GEN-LAST:event_comboBoxAtBeginPropertyChange

private void comboBoxInTheMidPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_comboBoxInTheMidPropertyChange
    setExampleFilename();
}//GEN-LAST:event_comboBoxInTheMidPropertyChange

private void comboBoxAtEndPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_comboBoxAtEndPropertyChange
    setExampleFilename();
}//GEN-LAST:event_comboBoxAtEndPropertyChange

private void textFieldDelim1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldDelim1KeyReleased
    setExampleFilename();
}//GEN-LAST:event_textFieldDelim1KeyReleased

private void textFieldDelim2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldDelim2KeyReleased
    setExampleFilename();
}//GEN-LAST:event_textFieldDelim2KeyReleased

private void textFieldDelim3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldDelim3KeyReleased
    setExampleFilename();
}//GEN-LAST:event_textFieldDelim3KeyReleased

private void textFieldString1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldString1KeyReleased
    setExampleFilename();
}//GEN-LAST:event_textFieldString1KeyReleased

private void textFieldString2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldString2KeyReleased
    setExampleFilename();
}//GEN-LAST:event_textFieldString2KeyReleased

private void textFieldString3MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_textFieldString3MouseReleased
    setExampleFilename();
}//GEN-LAST:event_textFieldString3MouseReleased

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                RenameDialog dialog = new RenameDialog(Type.Input);
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
    private javax.swing.JComboBox comboBoxInTheMid;
    private javax.swing.JLabel labelAfter;
    private javax.swing.JLabel labelAfterFilename;
    private javax.swing.JLabel labelAtBegin;
    private javax.swing.JLabel labelAtEnd;
    private javax.swing.JLabel labelBefore;
    private javax.swing.JLabel labelBeforeFilename;
    private javax.swing.JLabel labelDirectory;
    private javax.swing.JLabel labelDirectoryPrompt;
    private javax.swing.JLabel labelInTheMid;
    private javax.swing.JLabel labelNewNamePrompt;
    private javax.swing.JLabel labelNumberStepWidth;
    private javax.swing.JLabel labelOldName;
    private javax.swing.JLabel labelOldNamePrompt;
    private javax.swing.JLabel labelStartNumber;
    private javax.swing.JPanel panelBorder;
    private javax.swing.JPanel panelExample;
    private javax.swing.JPanel panelInputName;
    private javax.swing.JPanel panelTemplates;
    private de.elmar_baumann.lib.image.ImagePanel panelThumbnail;
    private javax.swing.JSpinner spinnerNumberStepWidth;
    private javax.swing.JSpinner spinnerStartNumber;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTextField textFieldDelim1;
    private javax.swing.JTextField textFieldDelim2;
    private javax.swing.JTextField textFieldDelim3;
    private javax.swing.JTextField textFieldNewName;
    private javax.swing.JTextField textFieldString1;
    private javax.swing.JTextField textFieldString2;
    private javax.swing.JTextField textFieldString3;
    // End of variables declaration//GEN-END:variables
}
