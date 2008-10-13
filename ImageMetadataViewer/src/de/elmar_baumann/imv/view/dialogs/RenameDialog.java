package de.elmar_baumann.imv.view.dialogs;

import de.elmar_baumann.imv.AppSettings;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;

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

    private void postInitComponents() {
        setIconImages(AppSettings.getAppIcons());
        initPanelOfType();
        tabbedPane.setSelectedComponent(panelOfType.get(type));
        disableOtherPanels();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Bundle.getString("RenameDialog.title")); // NOI18N

        labelDirectoryPrompt.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        labelDirectoryPrompt.setText(Bundle.getString("RenameDialog.labelDirectoryPrompt.text")); // NOI18N

        labelDirectory.setText(Bundle.getString("RenameDialog.labelDirectory.text")); // NOI18N

        labelOldNamePrompt.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        labelOldNamePrompt.setText(Bundle.getString("RenameDialog.labelOldNamePrompt.text")); // NOI18N

        labelOldName.setText(Bundle.getString("RenameDialog.labelOldName.text")); // NOI18N

        labelNewNamePrompt.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
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

        javax.swing.GroupLayout panelTemplatesLayout = new javax.swing.GroupLayout(panelTemplates);
        panelTemplates.setLayout(panelTemplatesLayout);
        panelTemplatesLayout.setHorizontalGroup(
            panelTemplatesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 531, Short.MAX_VALUE)
        );
        panelTemplatesLayout.setVerticalGroup(
            panelTemplatesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 237, Short.MAX_VALUE)
        );

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
                .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
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
    private javax.swing.JLabel labelDirectory;
    private javax.swing.JLabel labelDirectoryPrompt;
    private javax.swing.JLabel labelNewNamePrompt;
    private javax.swing.JLabel labelOldName;
    private javax.swing.JLabel labelOldNamePrompt;
    private javax.swing.JPanel panelBorder;
    private javax.swing.JPanel panelInputName;
    private javax.swing.JPanel panelTemplates;
    private de.elmar_baumann.lib.image.ImagePanel panelThumbnail;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTextField textFieldNewName;
    // End of variables declaration//GEN-END:variables
}
