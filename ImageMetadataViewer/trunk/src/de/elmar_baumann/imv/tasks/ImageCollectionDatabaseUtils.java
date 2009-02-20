package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.database.DatabaseImageCollections;
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
public final class ImageCollectionDatabaseUtils {

    private static final DatabaseImageCollections db = DatabaseImageCollections.getInstance();

    /**
     * Fügt in die Datenbank eine neue Bildsammlung ein.
     * 
     * @param filenames Namen der Bilddateien
     * @return          Name der Sammlung oder null, wenn keine eingefügt wurde
     */
    public static String insertImageCollection(List<String> filenames) {
        String name = inputCollectionName(""); // NOI18N
        if (name != null && !name.isEmpty()) {
            logAddImageCollection(name);
            if (!db.insertImageCollection(name, filenames)) {
                errorMessageAddImageCollection(name);
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
    public static boolean deleteImagesFromCollection(String collectionName, List<String> filenames) {
        if (confirmDelete(collectionName, Bundle.getString("ImageCollectionToDatabase.ConfirmMessage.DeleteSelectedFiles"))) {
            boolean removed = db.deleteImagesFromCollection(collectionName, filenames) == filenames.size();
            if (!removed) {
                errorMessageDeleteImagesFromCollection(collectionName);
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
    public static boolean deleteImageCollection(String collectionName) {
        boolean deleted = false;
        if (confirmDelete(collectionName, Bundle.getString("ImageCollectionToDatabase.ConfirmMessage.DeleteCollection"))) {
            deleted = db.deleteImageCollection(collectionName);
            if (!deleted) {
                errorMessageDeleteImageCollection(collectionName);
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
    public static boolean addImagesToCollection(String collectionName, List<String> filenames) {
        boolean added = db.insertImagesIntoCollection(collectionName, filenames);
        if (!added) {
            errorMessageAddImagesToCollection(collectionName);
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
    public static String renameImageCollection(String oldName) {
        String newName = inputCollectionName(oldName);
        if (newName != null) {
            boolean renamed = db.updateRenameImageCollection(oldName, newName) > 0;
            if (renamed) {
                return newName;
            } else {
                errorMessageRenameImageCollection(oldName);
                return null;
            }
        }
        return null;
    }

    private static void logAddImageCollection(String name) {
        MessageFormat msg = new MessageFormat(Bundle.getString("ImageCollectionToDatabase.InformationMessage.StartInsert"));
        Object[] params = {name};
        AppLog.logInfo(ImageCollectionDatabaseUtils.class, msg.format(params));
    }

    private static void errorMessageAddImagesToCollection(String collectionName) {
        errorMessage(Bundle.getString("ImageCollectionToDatabase.ErrorMessage.AddImagesToCollection"),
                collectionName);
    }

    private static void errorMessageAddImageCollection(String collectionName) {
        errorMessage(Bundle.getString("ImageCollectionToDatabase.ErrorMessage.AddImageCollection"),
                collectionName);
    }

    private static void errorMessageDeleteImageCollection(String collectionName) {
        errorMessage(Bundle.getString("ImageCollectionToDatabase.ErrorMessage.DeleteImageCollection"),
                collectionName);
    }

    private static void errorMessageDeleteImagesFromCollection(String collectionName) {
        errorMessage(Bundle.getString("ImageCollectionToDatabase.ErrorMessage.DeleteImagesFromCollection"),
                collectionName);
    }

    private static void errorMessageRenameImageCollection(String collectionName) {
        errorMessage(Bundle.getString("ImageCollectionToDatabase.ErrorMessage.RenameImageCollection"),
                collectionName);
    }

    private static void errorMessage(String format, String param) {
        MessageFormat msg = new MessageFormat(format);
        Object[] params = {param};
        JOptionPane.showMessageDialog(null,
                msg.format(params),
                Bundle.getString("ImageCollectionToDatabase.ErrorMessage.Title"),
                JOptionPane.ERROR_MESSAGE,
                AppIcons.getMediumAppIcon());
    }

    private static boolean confirmDelete(String collectionName, String message) {
        MessageFormat msg = new MessageFormat(message);
        Object[] params = {collectionName};
        return JOptionPane.showConfirmDialog(
                null,
                msg.format(params),
                Bundle.getString("ImageCollectionToDatabase.ConfirmMessage.Delete.Title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                AppIcons.getMediumAppIcon()) == JOptionPane.YES_OPTION;
    }

    private static String inputCollectionName(String defaultName) {
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
                        AppIcons.getMediumAppIcon()) == JOptionPane.YES_OPTION;
                name = null;
            }
            if (willAdd) {
                name = getCollectionName(nameNextTry);
            }
        }
        return name;
    }

    private static String getCollectionName(String defaultName) {
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

    private ImageCollectionDatabaseUtils() {
    }
}
