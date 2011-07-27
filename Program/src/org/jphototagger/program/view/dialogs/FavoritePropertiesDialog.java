package org.jphototagger.program.view.dialogs;

import org.jphototagger.lib.dialog.DirectoryChooser.Option;
import org.jphototagger.lib.util.Settings;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.database.DatabaseFavorites;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;
import org.jphototagger.lib.componentutil.MnemonicUtil;
import org.jphototagger.lib.dialog.Dialog;
import org.jphototagger.lib.dialog.DirectoryChooser;
import java.awt.Container;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;
import org.jphototagger.domain.Favorite;
import org.jphototagger.program.view.panels.SelectRootFilesPanel;


/**
 * Changes the properties of a {@link org.jphototagger.program.data.Favorite}.
 *
 * @author Elmar Baumann
 */
public final class FavoritePropertiesDialog extends Dialog {
    private static final String KEY_LAST_DIRECTORY = "org.jphototagger.program.view.dialogs.FavoriteDirectoryPropertiesDialog.LastDirectory";
    private static final long serialVersionUID = 750583413264344283L;
    private final transient DatabaseFavorites db = DatabaseFavorites.INSTANCE;
    private File dir = new File("");
    private boolean accepted;
    private boolean update;

    public FavoritePropertiesDialog() {
        super(GUI.getAppFrame(), true, UserSettings.INSTANCE.getSettings(), null);
        initComponents();
        setHelpPages();
        MnemonicUtil.setMnemonics((Container) this);
    }

    private void setHelpPages() {
        setHelpContentsUrl(JptBundle.INSTANCE.getString("Help.Url.Contents"));
        setHelpPageUrl(JptBundle.INSTANCE.getString("Help.Url.FavoriteDirectoryPropertiesDialog"));
    }

    private void chooseDirectory() {
        Option showHiddenDirs = UserSettings.INSTANCE.getDirChooserOptionShowHiddenDirs();
        List<File> hideRootFiles = SelectRootFilesPanel.readPersistentRootFiles(UserSettings.KEY_HIDE_ROOT_FILES_FROM_DIRECTORIES_TAB);
        DirectoryChooser dlg = new DirectoryChooser(GUI.getAppFrame(), dir, hideRootFiles, showHiddenDirs);

        dlg.setSettings(UserSettings.INSTANCE.getSettings(), "FavoritePropertiesDialog.DirChooser");
        dlg.setVisible(true);

        if (dlg.isAccepted()) {
            setDirectory(dlg.getSelectedDirectories().get(0));
        }

        setOkEnabled();
    }

    public boolean isEqualsTo(Favorite favorite) {
        if (favorite == null) {
            throw new NullPointerException("favorite == null");
        }

        if (!valuesOk()) {
            return false;
        }

        String favoriteName = favorite.getName();
        String componentName = getName();
        boolean componentNameIsFavoriteName = componentName.equalsIgnoreCase(favoriteName);
        File favoriteDirectory = favorite.getDirectory();

        return dir.equals(favoriteDirectory) && componentNameIsFavoriteName;
    }

    /**
     *
     * @param name name (alias)
     */
    public void setFavoriteName(String name) {
        if (name == null) {
            throw new NullPointerException("name == null");
        }

        textFieldFavoriteName.setText(name);
        update = true;
    }

    public void setDirectory(File dir) {
        if (dir == null) {
            throw new NullPointerException("dir == null");
        }

        this.dir = dir;

        String dirPathName = dir.getAbsolutePath();

        labelDirectoryname.setText(dirPathName);

        if (textFieldFavoriteName.getText().trim().isEmpty()) {
            textFieldFavoriteName.setText(dir.getName());
        }
    }

    public void setEnabledButtonChooseDirectory(boolean enabled) {
        buttonChooseDirectory.setEnabled(enabled);
    }

    /**
     *
     * @return Name (Alias)
     */
    public String getFavoriteName() {
        return textFieldFavoriteName.getText().trim();
    }

    public File getDirectory() {
        return dir;
    }

    /**
     *
     * @return true, if closed with Ok
     */
    public boolean isAccepted() {
        return accepted;
    }

    private void exitIfOk() {
        if (checkValuesOk()) {
            String favoriteName = textFieldFavoriteName.getText().trim();
            boolean exists = db.exists(favoriteName);

            if (!update && exists) {
                MessageDisplayer.error(this, "FavoritePropertiesDialog.Error.FavoriteExists", favoriteName);
            } else {
                accepted = true;
                setVisible(false);
            }
        }
    }

    private boolean checkValuesOk() {
        if (!valuesOk()) {
            MessageDisplayer.error(this, "FavoritePropertiesDialog.Error.InvalidInput");

            return false;
        }

        return true;
    }

    private boolean valuesOk() {
        return dir.isDirectory() && !getFavoriteName().isEmpty();
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            if (dir == null || !dir.isDirectory()) {
                directoryFromSettings();
            }
            setOkEnabled();
        } else {
            directoryToSettings();
        }

        super.setVisible(visible);
    }

    private void directoryFromSettings() {
        Settings settings = UserSettings.INSTANCE.getSettings();

        if (settings.containsKey(KEY_LAST_DIRECTORY)) {
            dir = new File(settings.getString(KEY_LAST_DIRECTORY));
        }
    }

    private void setOkEnabled() {
        buttonOk.setEnabled(valuesOk());
    }

    private void directoryToSettings() {
        UserSettings.INSTANCE.getSettings().set(KEY_LAST_DIRECTORY, dir.getAbsolutePath());
        UserSettings.INSTANCE.writeToFile();
    }

    @Override
    protected void escape() {
        accepted = false;
        setVisible(false);
    }

    private void handleKeyPressed(KeyEvent evt) {
        if (!buttonOk.isEnabled()) {
            return;
        }

        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            exitIfOk();
        }
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelPromptFavoriteName = new javax.swing.JLabel();
        textFieldFavoriteName = new javax.swing.JTextField();
        buttonChooseDirectory = new javax.swing.JButton();
        labelDirectoryname = new javax.swing.JLabel();
        buttonCancel = new javax.swing.JButton();
        buttonOk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(JptBundle.INSTANCE.getString("FavoritePropertiesDialog.title")); // NOI18N
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        labelPromptFavoriteName.setLabelFor(textFieldFavoriteName);
        labelPromptFavoriteName.setText(JptBundle.INSTANCE.getString("FavoritePropertiesDialog.labelPromptFavoriteName.text")); // NOI18N
        labelPromptFavoriteName.setName("labelPromptFavoriteName"); // NOI18N

        textFieldFavoriteName.setName("textFieldFavoriteName"); // NOI18N
        textFieldFavoriteName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                textFieldFavoriteNameKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldFavoriteNameKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                textFieldFavoriteNameKeyTyped(evt);
            }
        });

        buttonChooseDirectory.setText(JptBundle.INSTANCE.getString("FavoritePropertiesDialog.buttonChooseDirectory.text_1")); // NOI18N
        buttonChooseDirectory.setName("buttonChooseDirectory"); // NOI18N
        buttonChooseDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseDirectoryActionPerformed(evt);
            }
        });

        labelDirectoryname.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelDirectoryname.setName("labelDirectoryname"); // NOI18N

        buttonCancel.setText(JptBundle.INSTANCE.getString("FavoritePropertiesDialog.buttonCancel.text")); // NOI18N
        buttonCancel.setName("buttonCancel"); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        buttonOk.setText(JptBundle.INSTANCE.getString("FavoritePropertiesDialog.buttonOk.text")); // NOI18N
        buttonOk.setEnabled(false);
        buttonOk.setName("buttonOk"); // NOI18N
        buttonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOkActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelDirectoryname, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
                    .addComponent(textFieldFavoriteName, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
                    .addComponent(labelPromptFavoriteName)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(buttonCancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonOk))
                    .addComponent(buttonChooseDirectory, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(labelPromptFavoriteName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textFieldFavoriteName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonChooseDirectory)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelDirectoryname, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonOk)
                    .addComponent(buttonCancel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {labelDirectoryname, textFieldFavoriteName});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonChooseDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseDirectoryActionPerformed
        chooseDirectory();
    }//GEN-LAST:event_buttonChooseDirectoryActionPerformed

    private void buttonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOkActionPerformed
        exitIfOk();
    }//GEN-LAST:event_buttonOkActionPerformed

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        escape();
    }//GEN-LAST:event_buttonCancelActionPerformed

    private void textFieldFavoriteNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldFavoriteNameKeyPressed
        handleKeyPressed(evt);
    }//GEN-LAST:event_textFieldFavoriteNameKeyPressed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        escape();
    }//GEN-LAST:event_formWindowClosing

    private void textFieldFavoriteNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldFavoriteNameKeyTyped
        setOkEnabled();
    }//GEN-LAST:event_textFieldFavoriteNameKeyTyped

    private void textFieldFavoriteNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldFavoriteNameKeyReleased
        setOkEnabled();
    }//GEN-LAST:event_textFieldFavoriteNameKeyReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                FavoritePropertiesDialog dialog =
                    new FavoritePropertiesDialog();

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
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonChooseDirectory;
    private javax.swing.JButton buttonOk;
    private javax.swing.JLabel labelDirectoryname;
    private javax.swing.JLabel labelPromptFavoriteName;
    private javax.swing.JTextField textFieldFavoriteName;
    // End of variables declaration//GEN-END:variables
}
