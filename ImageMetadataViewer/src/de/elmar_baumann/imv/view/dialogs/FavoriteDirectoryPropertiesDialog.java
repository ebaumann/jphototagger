package de.elmar_baumann.imv.view.dialogs;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.database.Database;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.dialog.Dialog;
import de.elmar_baumann.lib.dialog.DirectoryChooser;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.persistence.PersistentAppSizes;
import de.elmar_baumann.lib.persistence.PersistentSettings;
import java.awt.Image;
import java.io.File;
import java.text.MessageFormat;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Neues Favoritenverzeichnis erstellen oder modifiziertes aktualisieren.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class FavoriteDirectoryPropertiesDialog extends Dialog {

    private static List<Image> appIcons = AppSettings.getAppIcons();
    private static final String keyLastDirectory = "de.elmar_baumann.imv.view.dialogs.FavoriteDirectoryPropertiesDialog.LastDirectory"; // NOI18N
    private String lastDirectory = ""; // NOI18N
    private boolean ok = true;
    private boolean isUpdate = false;
    private Database db = Database.getInstance();

    /** Creates new form FavoriteDirectoryPropertiesDialog */
    public FavoriteDirectoryPropertiesDialog() {
        super((java.awt.Frame) null, true);
        initComponents();
        setIconImages(appIcons);
        setHelpContentsUrl(Bundle.getString("Help.Url.Contents"));
        registerKeyStrokes();
    }

    private void chooseDirectory() {
        DirectoryChooser dialog = new DirectoryChooser(null, UserSettings.getInstance().isAcceptHiddenDirectories());
        dialog.setStartDirectory(new File(lastDirectory));
        dialog.setMultiSelection(false);
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
    public boolean isOk() {
        return ok;
    }

    private void checkOk() {
        if (checkValuesOk()) {
            String favoriteName = textFieldFavoriteName.getText().trim();
            boolean exists = db.existsFavoriteDirectory(favoriteName);
            if (!isUpdate && exists) {
                MessageFormat msg = new MessageFormat(Bundle.getString("FavoriteDirectoryPropertiesDialog.ErrorMessage.FavoriteExists"));
                Object[] params = {favoriteName};
                JOptionPane.showMessageDialog(
                    null,
                    msg.format(params),
                    Bundle.getString("FavoriteDirectoryPropertiesDialog.ErrorMessage.FavoriteExists.Title"),
                    JOptionPane.ERROR_MESSAGE,
                    AppSettings.getMediumAppIcon());
            } else {
                setVisible(false);
            }
        }
    }

    private boolean checkValuesOk() {
        String directoryName = labelDirectoryname.getText().trim();
        String favoriteName = textFieldFavoriteName.getText().trim();
        if (directoryName.isEmpty() ||
            favoriteName.isEmpty() ||
            !FileUtil.existsDirectory(directoryName)) {
            JOptionPane.showMessageDialog(
                this,
                Bundle.getString("FavoriteDirectoryPropertiesDialog.ErrorMessage.InvalidInput"),
                Bundle.getString("FavoriteDirectoryPropertiesDialog.ErrorMessage.InvalidInput.Title"),
                JOptionPane.ERROR_MESSAGE,
                AppSettings.getMediumAppIcon());
            return false;
        }
        return true;
    }

    @Override
    public void setVisible(
        boolean visible) {
        if (visible) {
            ok = true;
            lastDirectory = PersistentSettings.getInstance().getString(keyLastDirectory);
            PersistentAppSizes.getSizeAndLocation(this);
        } else {
            PersistentSettings.getInstance().setString(lastDirectory, keyLastDirectory);
            PersistentAppSizes.setSizeAndLocation(this);
        }
        super.setVisible(visible);
    }

    @Override
    protected void help() {
        help(Bundle.getString("Help.Url.FavoriteDirectoryPropertiesDialog"));
    }

    @Override
    protected void escape() {
        if (ok) {
            checkOk();
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

        textFieldFavoriteName = new javax.swing.JTextField();
        labelPromptFavoriteName = new javax.swing.JLabel();
        labelDirectoryname = new javax.swing.JLabel();
        buttonChooseDirectory = new javax.swing.JButton();
        buttonOk = new javax.swing.JButton();
        buttonCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(Bundle.getString("FavoriteDirectoryPropertiesDialog.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        labelPromptFavoriteName.setFont(new java.awt.Font("Dialog", 0, 12));
        labelPromptFavoriteName.setText(Bundle.getString("FavoriteDirectoryPropertiesDialog.labelPromptFavoriteName.text")); // NOI18N

        labelDirectoryname.setFont(new java.awt.Font("Dialog", 0, 12));
        labelDirectoryname.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        buttonChooseDirectory.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonChooseDirectory.setMnemonic('a');
        buttonChooseDirectory.setText(Bundle.getString("FavoriteDirectoryPropertiesDialog.buttonChooseDirectory.text_1")); // NOI18N
        buttonChooseDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseDirectoryActionPerformed(evt);
            }
        });

        buttonOk.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonOk.setMnemonic('o');
        buttonOk.setText(Bundle.getString("FavoriteDirectoryPropertiesDialog.buttonOk.text")); // NOI18N
        buttonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOkActionPerformed(evt);
            }
        });

        buttonCancel.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonCancel.setMnemonic('b');
        buttonCancel.setText(Bundle.getString("FavoriteDirectoryPropertiesDialog.buttonCancel.text")); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelDirectoryname, javax.swing.GroupLayout.DEFAULT_SIZE, 543, Short.MAX_VALUE)
                    .addComponent(textFieldFavoriteName, javax.swing.GroupLayout.DEFAULT_SIZE, 543, Short.MAX_VALUE)
                    .addComponent(labelPromptFavoriteName)
                    .addComponent(buttonChooseDirectory)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(buttonCancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonOk)))
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
    checkOk();
}//GEN-LAST:event_buttonOkActionPerformed

private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
    ok = false;
    setVisible(false);
}//GEN-LAST:event_buttonCancelActionPerformed

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    if (ok) {
        checkOk();
    }
}//GEN-LAST:event_formWindowClosing

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                FavoriteDirectoryPropertiesDialog dialog = new FavoriteDirectoryPropertiesDialog();
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
