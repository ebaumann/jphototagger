package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.app.AppTexts;
import de.elmar_baumann.imv.database.DatabaseImageCollections;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.GUI;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Verwaltet Bildsammlungen der Datenbank.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ImageCollectionDatabaseUtils {

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
            if (!DatabaseImageCollections.INSTANCE.insertImageCollection(
                    name, filenames)) {
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
    public static boolean deleteImagesFromCollection(String collectionName,
            List<String> filenames) {
        if (confirmDelete(
                "ImageCollectionToDatabase.ConfirmMessage.DeleteSelectedFiles",
                collectionName)) {
            boolean removed = DatabaseImageCollections.INSTANCE.
                    deleteImagesFromCollection(collectionName, filenames) ==
                    filenames.size();
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
        if (confirmDelete(
                "ImageCollectionToDatabase.ConfirmMessage.DeleteCollection",
                collectionName)) {
            deleted = DatabaseImageCollections.INSTANCE.deleteImageCollection(
                    collectionName);
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
    public static boolean addImagesToCollection(String collectionName,
            List<String> filenames) {
        boolean added = DatabaseImageCollections.INSTANCE.
                insertImagesIntoCollection(collectionName, filenames);
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
        if (newName != null && !newName.isEmpty()) {
            boolean renamed = DatabaseImageCollections.INSTANCE.
                    updateRenameImageCollection(oldName, newName) >
                    0;
            if (renamed) {
                return newName;
            } else {
                errorMessageRenameImageCollection(oldName);
                return null;
            }
        }
        return null;
    }

    /**
     * Returns wether a name is valid. This is true if the name is not equals
     * to {@link AppTexts#DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_PREV_IMPORT}
     * ignoring the case.
     *
     * @param  name name
     * @return true if allowed
     */
    public static boolean isValidName(String name) {
        return !name.trim().equalsIgnoreCase(
                AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_PREV_IMPORT);
    }

    private static boolean checkIsValidName(String name) {
        if (isValidName(name)) return true;
        JOptionPane.showMessageDialog(
                GUI.INSTANCE.getAppFrame(),
                Bundle.getString(
                "ImageCollectionDatabaseUtils.Error.InvalidName", name),
                Bundle.getString(
                "ImageCollectionDatabaseUtils.Error.InvalidName.Title", name),
                JOptionPane.ERROR_MESSAGE);
        return false;
    }

    private static void logAddImageCollection(String name) {
        AppLog.logInfo(ImageCollectionDatabaseUtils.class, Bundle.getString(
                "ImageCollectionToDatabase.InformationMessage.StartInsert",
                name));
    }

    private static void errorMessageAddImagesToCollection(String collectionName) {
        errorMessage(
                "ImageCollectionToDatabase.ErrorMessage.AddImagesToCollection",
                collectionName);
    }

    private static void errorMessageAddImageCollection(String collectionName) {
        errorMessage("ImageCollectionToDatabase.ErrorMessage.AddImageCollection",
                collectionName);
    }

    private static void errorMessageDeleteImageCollection(String collectionName) {
        errorMessage(
                "ImageCollectionToDatabase.ErrorMessage.DeleteImageCollection",
                collectionName);
    }

    private static void errorMessageDeleteImagesFromCollection(
            String collectionName) {
        errorMessage(
                "ImageCollectionToDatabase.ErrorMessage.DeleteImagesFromCollection",
                collectionName);
    }

    private static void errorMessageRenameImageCollection(String collectionName) {
        errorMessage(
                "ImageCollectionToDatabase.ErrorMessage.RenameImageCollection",
                collectionName);
    }

    private static void errorMessage(String bundleKey, String param) {
        JOptionPane.showMessageDialog(null,
                Bundle.getString(bundleKey, param),
                Bundle.getString("ImageCollectionToDatabase.ErrorMessage.Title"),
                JOptionPane.ERROR_MESSAGE);
    }

    private static boolean confirmDelete(String bundleKey, String collectionName) {
        return JOptionPane.showConfirmDialog(
                null,
                Bundle.getString(bundleKey, collectionName),
                Bundle.getString(
                "ImageCollectionToDatabase.ConfirmMessage.Delete.Title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    private static String inputCollectionName(String defaultName) {
        String name = getCollectionName(defaultName);
        boolean willAdd = name != null;
        while (name != null && willAdd) {
            willAdd = false;
            String nameNextTry = name;
            if (DatabaseImageCollections.INSTANCE.existsImageCollection(name) ||
                    !checkIsValidName(name)) {
                willAdd = JOptionPane.showConfirmDialog(null,
                        Bundle.getString(
                        "ImageCollectionToDatabase.ConfirmMessage.InputNewCollectionName", name),
                        Bundle.getString(
                        "ImageCollectionToDatabase.ConfirmMessage.InputNewCollectionName.Title"),
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
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
                Bundle.getString(
                "ImageCollectionToDatabase.Input.CollectionName"),
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
