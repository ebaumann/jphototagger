package org.jphototagger.program.module.filesystem;

import java.awt.Container;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.filechooser.FileSystemView;
import org.bushe.swing.event.EventBus;
import org.jphototagger.api.file.event.FileRenamedEvent;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesHints;
import org.jphototagger.domain.metadata.xmp.XmpSidecarFileResolver;
import org.jphototagger.domain.repository.SaveOrUpdate;
import org.jphototagger.domain.thumbnails.ThumbnailProvider;
import org.jphototagger.lib.swing.Dialog;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;
import org.jphototagger.program.resource.GUI;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class RenameDialog extends Dialog {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(RenameDialog.class.getName());
    private final FilenameFormatArray filenameFormatArray;
    private final XmpSidecarFileResolver xmpSidecarFileResolver = Lookup.getDefault().lookup(XmpSidecarFileResolver.class);
    private final ThumbnailProvider thumbnailProvider = Lookup.getDefault().lookup(ThumbnailProvider.class);
    private List<File> files = new ArrayList<>();
    private int fileIndex = 0;
    private boolean lockClose = false;
    private boolean cancel = false;

    public RenameDialog() {
        super(GUI.getAppFrame(), true);
        initComponents();
        filenameFormatArray = panelRenameTemplates.getFilenameFormatArray();
        postInitComponents();
    }

    private void postInitComponents() {
        setHelpPage();
        MnemonicUtil.setMnemonics((Container) this);
    }

    private void setHelpPage() {
        setHelpPageUrl(Bundle.getString(RenameDialog.class, "RenameDialog.HelpPage"));
    }

    public void selectRenameViaTemplatesTab(boolean select) {
        tabbedPane.setEnabledAt(1, select);
        if (!select) {
            tabbedPane.setSelectedComponent(panelInputName);
        }
    }

    public void setImageFiles(List<File> imageFiles) {
        if (imageFiles == null) {
            throw new NullPointerException("imageFiles == null");
        }
        this.files = new ArrayList<>(imageFiles);
    }

    public synchronized void notifyFileSystemListeners(File fromImageFile, File toImageFile) {
        if (fromImageFile == null) {
            throw new NullPointerException("fromImageFile == null");
        }
        if (toImageFile == null) {
            throw new NullPointerException("toImageFile == null");
        }
        FileRenamedEvent evt = new FileRenamedEvent(this, fromImageFile, toImageFile);
        evt.putProperty(SaveOrUpdate.class, SaveOrUpdate.NONE);
        EventBus.publish(evt);
    }

    private boolean renameImageFile(File fromImageFile, File toImageFile) {
        boolean renamed = fromImageFile.renameTo(toImageFile);
        if (renamed) {
            renameXmpFileOfImageFile(fromImageFile, toImageFile);
            FilesystemRepositoryUpdater.moveFile(fromImageFile, toImageFile);
        }
        return renamed;
    }

    private void renameXmpFileOfImageFile(File fromImageFile, File toImageFile) {
        File fromXmpFile = xmpSidecarFileResolver.getXmpSidecarFileOrNullIfNotExists(fromImageFile);
        if (fromXmpFile != null) {
            File toXmpFile = xmpSidecarFileResolver.suggestXmpSidecarFile(toImageFile);
            if (toXmpFile.exists()) {
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
        int size = files.size();
        for (int i = 0; !cancel && (i < size); i++) {
            fileIndex = i;
            File oldFile = files.get(i);
            String parent = oldFile.getParent();
            filenameFormatArray.setFile(oldFile);
            File newFile = new File(((parent == null)
                                     ? ""
                                     : parent + File.separator) + filenameFormatArray.format());
            if (checkNewFileDoesNotExist(newFile) && renameImageFile(oldFile, newFile)) {
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
        if ((fileIndex >= 0) && (fileIndex < files.size())) {
            File oldFile = files.get(fileIndex);
            if (canRenameViaInput()) {
                File newFile = getNewFileViaInput();
                if (renameImageFile(oldFile, newFile)) {
                    files.set(fileIndex, newFile);
                    notifyFileSystemListeners(oldFile, newFile);
                    setCurrentFilenameToInputPanel();
                    focusTextFieldToName();
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
            focusTextFieldToName();
        }
    }

    private File getNewFileViaInput() {
        String directory = labelDirectory.getText();
        return new File(directory + (directory.isEmpty()
                                     ? ""
                                     : File.separator) + textFieldToName.getText().trim());
    }

    private boolean canRenameViaInput() {
        File oldFile = files.get(fileIndex);
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
            textFieldToName.requestFocusInWindow();
        }
        return !equals;
    }

    private boolean checkNewFileDoesNotExist(File file) {
        boolean exists = file.exists();
        if (exists) {
            String message = Bundle.getString(RenameDialog.class, "RenameDialog.Error.NewFileExists", file.getName());
            MessageDisplayer.error(this, message);
            textFieldToName.requestFocusInWindow();
        }
        return !exists;
    }

    private void setCurrentFilenameToInputPanel() {
        if ((fileIndex >= 0) && (fileIndex < files.size())) {
            File file = files.get(fileIndex);
            setDirectoryNameLabel(file);
            labelFromName.setText(file.getName());
            textFieldToName.setText(file.getName());
            setThumbnail(file);
        }
    }

    private void focusTextFieldToName() {
        textFieldToName.requestFocus();
        setInToNameCursorBevoreSuffix();
    }

    private void setInToNameCursorBevoreSuffix() {
        String toName = textFieldToName.getText();
        int indexOfSuffixDelimiter = toName.lastIndexOf('.');
        if (indexOfSuffixDelimiter > 0) {
            textFieldToName.setCaretPosition(indexOfSuffixDelimiter);
            textFieldToName.setSelectionStart(0);
            textFieldToName.setSelectionEnd(indexOfSuffixDelimiter);
        }
    }

    private void setDirectoryNameLabel(File file) {
        File dir = file.getParentFile();
        labelDirectory.setText(dir.getAbsolutePath());
        labelDirectory.setIcon(FileSystemView.getFileSystemView().getSystemIcon(dir));
    }

    private synchronized void setThumbnail(File file) {
        Image thumbnail = thumbnailProvider.getThumbnail(file);
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
            setExampleFilenameToRenameTemplatesPanel();
            readProperties();
        } else {
            Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
            prefs.setComponent(this, new PreferencesHints(PreferencesHints.Option.SET_TABBED_PANE_CONTENT));
        }
        super.setVisible(visible);
    }

    private void setExampleFilenameToRenameTemplatesPanel() {
        if (!files.isEmpty()) {
            panelRenameTemplates.setFileForExampleFilename(files.get(0));
            panelRenameTemplates.showExampleFilename();
        }
    }

    private void readProperties() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        prefs.applyComponentSettings(this, new PreferencesHints(PreferencesHints.Option.SET_TABBED_PANE_CONTENT));
        if (!tabbedPane.isEnabledAt(1)) {
            tabbedPane.setSelectedComponent(panelInputName);
        }
    }

    private void inputNamePanelIsShown() {
        if (panelInputName.isVisible()) {
            setCurrentFilenameToInputPanel();
            focusTextFieldToName();
        }
    }

    @Override
    protected void escape() {
        if (!lockClose) {
            setVisible(false);
            dispose();
        }
    }

    private void closeWindowIfNotLocked() {
        if (!lockClose) {
            panelRenameTemplates.checkDirty();
            setVisible(false);
            dispose();
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
        java.awt.GridBagConstraints gridBagConstraints;

        panelContents = new javax.swing.JPanel();
        tabbedPane = new javax.swing.JTabbedPane();
        panelInputName = new javax.swing.JPanel();
        panelFolderPathname = new javax.swing.JPanel();
        labelDirectory = new javax.swing.JLabel();
        panelThumbnailBorder = new javax.swing.JPanel();
        panelThumbnail = new org.jphototagger.lib.swing.ImagePanel();
        panelFromNameToName = new javax.swing.JPanel();
        labelFromNamePrompt = new javax.swing.JLabel();
        labelFromName = new javax.swing.JLabel();
        labelToNamePrompt = new javax.swing.JLabel();
        textFieldToName = new javax.swing.JTextField();
        panelRenameButtons = new javax.swing.JPanel();
        buttonNextFile = new javax.swing.JButton();
        buttonRename = new javax.swing.JButton();
        panelTemplatesContents = new javax.swing.JPanel();
        panelRenameTemplates = new org.jphototagger.program.module.filesystem.RenameTemplatesPanel();
        buttonRenameViaTemplate = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/module/filesystem/Bundle"); // NOI18N
        setTitle(bundle.getString("RenameDialog.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panelContents.setLayout(new java.awt.GridBagLayout());

        panelInputName.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                panelInputNameComponentShown(evt);
            }
        });
        panelInputName.setLayout(new java.awt.GridBagLayout());

        panelFolderPathname.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("RenameDialog.panelFolderPathname.border.title"))); // NOI18N
        panelFolderPathname.setLayout(new java.awt.GridBagLayout());

        labelDirectory.setForeground(new java.awt.Color(0, 175, 0));
        labelDirectory.setText(" "); // NOI18N
        labelDirectory.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelFolderPathname.add(labelDirectory, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        panelInputName.add(panelFolderPathname, gridBagConstraints);

        panelThumbnailBorder.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        panelThumbnailBorder.setMinimumSize(new java.awt.Dimension(260, 260));
        panelThumbnailBorder.setPreferredSize(new java.awt.Dimension(260, 260));
        panelThumbnailBorder.setLayout(new java.awt.GridBagLayout());

        panelThumbnail.setEnabled(false);
        panelThumbnail.setFocusable(false);
        panelThumbnail.setPreferredSize(new java.awt.Dimension(250, 250));

        javax.swing.GroupLayout panelThumbnailLayout = new javax.swing.GroupLayout(panelThumbnail);
        panelThumbnail.setLayout(panelThumbnailLayout);
        panelThumbnailLayout.setHorizontalGroup(
            panelThumbnailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 246, Short.MAX_VALUE)
        );
        panelThumbnailLayout.setVerticalGroup(
            panelThumbnailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 246, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelThumbnailBorder.add(panelThumbnail, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 0, 0);
        panelInputName.add(panelThumbnailBorder, gridBagConstraints);

        panelFromNameToName.setLayout(new java.awt.GridBagLayout());

        labelFromNamePrompt.setText(bundle.getString("RenameDialog.labelFromNamePrompt.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelFromNameToName.add(labelFromNamePrompt, gridBagConstraints);

        labelFromName.setForeground(new java.awt.Color(0, 175, 0));
        labelFromName.setText(" "); // NOI18N
        labelFromName.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panelFromNameToName.add(labelFromName, gridBagConstraints);

        labelToNamePrompt.setLabelFor(textFieldToName);
        labelToNamePrompt.setText(bundle.getString("RenameDialog.labelToNamePrompt.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        panelFromNameToName.add(labelToNamePrompt, gridBagConstraints);

        textFieldToName.setColumns(25);
        textFieldToName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                textFieldToNameKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        panelFromNameToName.add(textFieldToName, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 10);
        panelInputName.add(panelFromNameToName, gridBagConstraints);

        panelRenameButtons.setLayout(new java.awt.GridLayout(1, 0, 5, 0));

        buttonNextFile.setText(bundle.getString("RenameDialog.buttonNextFile.text")); // NOI18N
        buttonNextFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonNextFileActionPerformed(evt);
            }
        });
        panelRenameButtons.add(buttonNextFile);

        buttonRename.setText(bundle.getString("RenameDialog.buttonRename.text")); // NOI18N
        buttonRename.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRenameActionPerformed(evt);
            }
        });
        panelRenameButtons.add(buttonRename);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        panelInputName.add(panelRenameButtons, gridBagConstraints);

        tabbedPane.addTab(bundle.getString("RenameDialog.panelInputName.TabConstraints.tabTitle"), panelInputName); // NOI18N

        panelTemplatesContents.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        panelTemplatesContents.add(panelRenameTemplates, gridBagConstraints);

        buttonRenameViaTemplate.setText(bundle.getString("RenameDialog.buttonRenameViaTemplate.text")); // NOI18N
        buttonRenameViaTemplate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRenameViaTemplateActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        panelTemplatesContents.add(buttonRenameViaTemplate, gridBagConstraints);

        tabbedPane.addTab(bundle.getString("RenameDialog.panelTemplatesContents.TabConstraints.tabTitle"), panelTemplatesContents); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelContents.add(tabbedPane, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(panelContents, gridBagConstraints);

        pack();
    }//GEN-END:initComponents

    private void buttonRenameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRenameActionPerformed
        renameViaInput();
    }//GEN-LAST:event_buttonRenameActionPerformed

    private void buttonNextFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonNextFileActionPerformed
        setNextFileViaInput();
    }//GEN-LAST:event_buttonNextFileActionPerformed

    private void buttonRenameViaTemplateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRenameViaTemplateActionPerformed
        renameViaTemplate();
    }//GEN-LAST:event_buttonRenameViaTemplateActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        closeWindowIfNotLocked();
    }//GEN-LAST:event_formWindowClosing

    private void panelInputNameComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_panelInputNameComponentShown
        inputNamePanelIsShown();
    }//GEN-LAST:event_panelInputNameComponentShown

    private void textFieldToNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldToNameKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER && StringUtil.hasContent(textFieldToName.getText())) {
            renameViaInput();
        }
    }//GEN-LAST:event_textFieldToNameKeyPressed

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
    private javax.swing.JButton buttonRenameViaTemplate;
    private javax.swing.JLabel labelDirectory;
    private javax.swing.JLabel labelFromName;
    private javax.swing.JLabel labelFromNamePrompt;
    private javax.swing.JLabel labelToNamePrompt;
    private javax.swing.JPanel panelContents;
    private javax.swing.JPanel panelFolderPathname;
    private javax.swing.JPanel panelFromNameToName;
    private javax.swing.JPanel panelInputName;
    private javax.swing.JPanel panelRenameButtons;
    private org.jphototagger.program.module.filesystem.RenameTemplatesPanel panelRenameTemplates;
    private javax.swing.JPanel panelTemplatesContents;
    private org.jphototagger.lib.swing.ImagePanel panelThumbnail;
    private javax.swing.JPanel panelThumbnailBorder;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTextField textFieldToName;
    // End of variables declaration//GEN-END:variables
}
