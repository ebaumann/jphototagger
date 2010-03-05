/*
 * JPhotoTagger tags and finds images fast
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
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.controller.misc.SizeAndLocationController;
import de.elmar_baumann.jpt.database.DatabaseFavorites;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.lib.componentutil.MnemonicUtil;
import de.elmar_baumann.lib.dialog.Dialog;
import de.elmar_baumann.lib.dialog.DirectoryChooser;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.Container;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;

/**
 * Neues Favoritenverzeichnis erstellen oder modifiziertes aktualisieren.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class FavoritePropertiesDialog extends Dialog {

    private static final    String            KEY_LAST_DIRECTORY = "de.elmar_baumann.jpt.view.dialogs.FavoriteDirectoryPropertiesDialog.LastDirectory";
    private static final    long              serialVersionUID   = 750583413264344283L;
    private final transient DatabaseFavorites db                 = DatabaseFavorites.INSTANCE;
    private                 String            lastDirectory      = "";
    private                 boolean           accepted;
    private                 boolean           isUpdate;

    public FavoritePropertiesDialog() {
        super(GUI.INSTANCE.getAppFrame(), true, UserSettings.INSTANCE.getSettings(), null);
        initComponents();
        setHelpPages();
        MnemonicUtil.setMnemonics((Container) this);
    }

    private void setHelpPages() {
        setHelpContentsUrl(JptBundle.INSTANCE.getString("Help.Url.Contents"));
        setHelpPageUrl(JptBundle.INSTANCE.getString("Help.Url.FavoriteDirectoryPropertiesDialog"));
    }

    private void chooseDirectory() {
        DirectoryChooser dialog = new DirectoryChooser(GUI.INSTANCE.getAppFrame(), new File(lastDirectory), UserSettings.INSTANCE.getDirChooserOptionShowHiddenDirs());

        dialog.addWindowListener(new SizeAndLocationController());
        dialog.setVisible(true);
        if (dialog.accepted()) {
            List<File> files = dialog.getSelectedDirectories();
            String directoryName = files.get(0).getAbsolutePath();
            labelDirectoryname.setText(directoryName);
            lastDirectory = directoryName;
            if (textFieldFavoriteName.getText().trim().isEmpty()) {
                textFieldFavoriteName.setText(directoryName);
            }
        }
        setOkEnabled();
    }

    /**
     * Setzt den Namen des Favoriten (Alias). Ist dieser gesetzt, wird
     * nicht geprüft, ob er bereits in der Datenbank vorhanden ist; es
     * wird angenommen, das Verzeichnis soll aktualisiert werden oder
     * der Name umbenannt.
     *
     * @param name  Name
     */
    public void setFavoriteName(String name) {
        textFieldFavoriteName.setText(name);
        isUpdate = true;
    }

    /**
     * Setzt den Namen des Verzeichnisses.
     *
     * @param  name  Verzeichnisname
     */
    public void setDirectoryName(String name) {
        labelDirectoryname.setText(name);
    }

    /**
     * Aktiviert den Button zur Auswahl eines Verzeichnisses.
     *
     * @param enabled  true, wenn aktiv. Default: true.
     */
    public void setEnabledButtonChooseDirectory(boolean enabled) {
        buttonChooseDirectory.setEnabled(enabled);
    }

    /**
     * Liefert den Namen des Favoritenverzeichnisses.
     *
     * @return Name (Alias)
     */
    public String getFavoriteName() {
        return textFieldFavoriteName.getText().trim();
    }

    /**
     * Liefet den Namen des Verzeichnisses (File).
     *
     * @return Verzeichnisname
     */
    public String getDirectoryName() {
        return labelDirectoryname.getText().trim();
    }

    /**
     * Liefert, ob der Dialog mit Ok abgeschlossen werden konnte:
     * Es existiert ein Favoritentext sowie das Verzeichnis.
     *
     * @return true, wenn ok
     */
    public boolean accepted() {
        return accepted;
    }

    private void exitIfOk() {
        if (checkValuesOk()) {
            String favoriteName = textFieldFavoriteName.getText().trim();
            boolean exists = db.exists(favoriteName);
            if (!isUpdate && exists) {
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
        String directoryName = labelDirectoryname.getText().trim();
        String favoriteName = textFieldFavoriteName.getText().trim();
        return !directoryName.isEmpty() &&
                !favoriteName.isEmpty() &&
                FileUtil.existsDirectory(new File(directoryName));
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            lastDirectoryFromSettings();
        } else {
            lastDirectoryToSettings();
        }
        super.setVisible(visible);
    }

    private void lastDirectoryFromSettings() {
        lastDirectory = UserSettings.INSTANCE.getSettings().getString(KEY_LAST_DIRECTORY);
    }

    private void setOkEnabled() {
        buttonOk.setEnabled(valuesOk());
    }

    private void lastDirectoryToSettings() {
        UserSettings.INSTANCE.getSettings().set(lastDirectory, KEY_LAST_DIRECTORY);
        UserSettings.INSTANCE.writeToFile();
    }

    @Override
    protected void escape() {
        accepted = false;
        setVisible(false);
    }

    private void handleKeyPressed(KeyEvent evt) {
        if (!buttonOk.isEnabled()) return;
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            exitIfOk();
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

        labelPromptFavoriteName = new javax.swing.JLabel();
        textFieldFavoriteName = new javax.swing.JTextField();
        buttonChooseDirectory = new javax.swing.JButton();
        labelDirectoryname = new javax.swing.JLabel();
        buttonCancel = new javax.swing.JButton();
        buttonOk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(JptBundle.INSTANCE.getString("FavoritePropertiesDialog.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        labelPromptFavoriteName.setLabelFor(textFieldFavoriteName);
        labelPromptFavoriteName.setText(JptBundle.INSTANCE.getString("FavoritePropertiesDialog.labelPromptFavoriteName.text")); // NOI18N

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
        buttonChooseDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseDirectoryActionPerformed(evt);
            }
        });

        labelDirectoryname.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        buttonCancel.setText(JptBundle.INSTANCE.getString("FavoritePropertiesDialog.buttonCancel.text")); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        buttonOk.setText(JptBundle.INSTANCE.getString("FavoritePropertiesDialog.buttonOk.text")); // NOI18N
        buttonOk.setEnabled(false);
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
