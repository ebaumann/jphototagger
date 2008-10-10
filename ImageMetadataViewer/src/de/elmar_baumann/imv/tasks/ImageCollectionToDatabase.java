package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.database.Database;
import de.elmar_baumann.imv.resource.Bundle;
import java.text.MessageFormat;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Verwaltet Bildsammlungen der Datenbank.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class ImageCollectionToDatabase {

    private static Database db = Database.getInstance();

    /**
     * Fügt in die Datenbank eine neue Bildsammlung ein.
     * 
     * @param filenames Namen der Bilddateien
     * @return          Name der Sammlung oder null, wenn keine eingefügt wurde
     */
    public String addImageCollection(List<String> filenames) {
        String name = inputCollectionName(""); // NOI18N
        if (name != null && !name.isEmpty()) {
            if (!db.insertImageCollection(name, filenames)) {
                messageErrorAddImageCollection(name);
                return null;
            }
        }
        return name;
    }

    /**
     * Entfernt Bilder aus einer Bildsammlung.
     * 
     * @param collectionName Name der Bildsammlung
     * @param filenames      Zu entfernende Bilder
     * @return               true, wenn die Bilder entfernt wurden
     */
    public boolean deleteImagesFromCollection(
        String collectionName, List<String> filenames) {
        if (askDelete(collectionName,
            Bundle.getString("ImageCollectionToDatabase.ConfirmMessage.DeleteSelectedFiles"))) {
            boolean removed = db.deleteImagesFromCollection(
                collectionName, filenames) == filenames.size();
            if (!removed) {
                messageErrorDeleteImagesFromCollection(collectionName);
            }
            return removed;
        }
        return false;
    }

    /**
     * Entfernt eine Bildsammlung. Zeigt vorher einen Dialog zur Bestätigung.
     * 
     * @param collectionName Name der Bildsammlung
     * @return               true wenn gelöscht
     */
    public boolean deleteImageCollection(String collectionName) {
        boolean deleted = false;
        if (askDelete(collectionName, Bundle.getString("ImageCollectionToDatabase.ConfirmMessage.DeleteCollection"))) {
            deleted = db.deleteImageCollection(collectionName);
            if (!deleted) {
                messageErrorDeleteImageCollection(collectionName);
            }
        }
        return deleted;
    }

    /**
     * Fügt einer Bildsammlung Bilder hinzu.
     * 
     * @param collectionName Name der Bildsammlung
     * @param filenames      Hinzuzufügende Bilddateien
     * @return               true bei Erfolg
     */
    public boolean addImagesToCollection(String collectionName, List<String> filenames) {
        boolean added = db.insertImagesIntoCollection(collectionName, filenames);
        if (!added) {
            messageErrorAddImagesToCollection(collectionName);
        }
        return added;
    }

    /**
     * Benennt eine Bildsammlung um.
     * 
     * @param oldName Alter Name, der neue wird durch eine Eingabe erfragt.
     * @return        Neuer Name oder null, wenn die Sammlung nicht umbenannt
     *                wurde
     */
    public String renameImageCollection(String oldName) {
        String newName = inputCollectionName(oldName);
        if (newName != null) {
            boolean renamed = db.updateRenameImageCollection(oldName, newName) > 0;
            if (renamed) {
                return newName;
            } else {
                messageErrorRenameImageCollection(oldName);
                return null;
            }
        }
        return null;
    }

    private void messageErrorAddImagesToCollection(String collectionName) {
        errorMessage(Bundle.getString("ImageCollectionToDatabase.ErrorMessage.AddImagesToCollection"),
            collectionName);
    }

    private void messageErrorAddImageCollection(String collectionName) {
        errorMessage(Bundle.getString("ImageCollectionToDatabase.ErrorMessage.AddImageCollection"),
            collectionName);
    }

    private void messageErrorDeleteImageCollection(String collectionName) {
        errorMessage(Bundle.getString("ImageCollectionToDatabase.ErrorMessage.DeleteImageCollection"),
            collectionName);
    }

    private void messageErrorDeleteImagesFromCollection(String collectionName) {
        errorMessage(Bundle.getString("ImageCollectionToDatabase.ErrorMessage.DeleteImagesFromCollection"),
            collectionName);
    }

    private void messageErrorRenameImageCollection(String collectionName) {
        errorMessage(Bundle.getString("ImageCollectionToDatabase.ErrorMessage.RenameImageCollection"),
            collectionName);
    }

    private void errorMessage(String format, String param) {
        MessageFormat msg = new MessageFormat(format);
        Object[] params = {param};
        JOptionPane.showMessageDialog(null,
            msg.format(params),
            Bundle.getString("ImageCollectionToDatabase.ErrorMessage.Title"),
            JOptionPane.ERROR_MESSAGE,
            AppSettings.getMediumAppIcon());
    }

    private boolean askDelete(String collectionName, String message) {
        MessageFormat msg = new MessageFormat(message);
        Object[] params = {collectionName};
        return JOptionPane.showConfirmDialog(
            null,
            msg.format(params),
            Bundle.getString("ImageCollectionToDatabase.ConfirmMessage.Delete.Title"),
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            AppSettings.getMediumAppIcon()) == JOptionPane.YES_OPTION;
    }

    private String getCollectionName(String defaultName) {
        String name = JOptionPane.showInputDialog(
            Bundle.getString("ImageCollectionToDatabase.Input.CollectionName"),
            defaultName);

        if (name != null) {
            name = name.trim();
            if (name.isEmpty()) {
                name = null;
            }
        }

        return name;
    }

    private String inputCollectionName(String defaultName) {
        String name = getCollectionName(defaultName);
        boolean willAdd = name != null;
        while (name != null && willAdd) {
            willAdd = false;
            String nameNextTry = name;
            if (db.existsImageCollection(name)) {
                MessageFormat msg = new MessageFormat(Bundle.getString("ImageCollectionToDatabase.ConfirmMessage.InputNewCollectionName"));
                Object[] params = {name};
                willAdd = JOptionPane.showConfirmDialog(null,
                    msg.format(params),
                    Bundle.getString("ImageCollectionToDatabase.ConfirmMessage.InputNewCollectionName.Title"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    AppSettings.getMediumAppIcon()) == JOptionPane.YES_OPTION;
                name = null;
            }
            if (willAdd) {
                name = getCollectionName(nameNextTry);
            }
        }
        return name;
    }
}
