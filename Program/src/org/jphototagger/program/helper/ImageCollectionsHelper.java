package org.jphototagger.program.helper;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.controller.imagecollection.ControllerDeleteFromImageCollection;
import org.jphototagger.program.database.DatabaseImageCollections;
import org.jphototagger.program.model.ListModelImageCollections;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import java.io.File;
import java.util.List;
import org.jdesktop.swingx.JXList;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ImageCollectionsHelper {

    /**
     * Deletes selected files from an image collection.
     */
    public static void deleteSelectedFiles() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                Object selectedValue = getSelectedCollection();

                if (selectedValue != null) {
                    ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();
                    List<File> selectedFiles = tnPanel.getSelectedFiles();

                    if (deleteImagesFromCollection(selectedValue.toString(), selectedFiles)) {
                        tnPanel.removeFiles(selectedFiles);
                    }
                } else {
                    AppLogger.logWarning(ControllerDeleteFromImageCollection.class,
                                         "ImageCollectionsHelper.Error.NoCollectionSelected");
                }
            }
        });
    }

    /**
     * Returns the selected list item from the image collection list.
     *
     * @return item or null if no item is selected
     */
    public static Object getSelectedCollection() {
        JXList list = GUI.getAppPanel().getListImageCollections();

        return list.getSelectedValue();
    }

    /**
     * Inserts a new image collection, prompts the user for the name.
     *
     * @param imageFiles image files to insert
     * @return           name of the collection or null, if no image collection
     *                   was created
     */
    public static String insertImageCollection(List<File> imageFiles) {
        if (imageFiles == null) {
            throw new NullPointerException("imageFiles == null");
        }

        String name = inputCollectionName("");

        if ((name != null) &&!name.isEmpty()) {
            logAddImageCollection(name);

            if (!DatabaseImageCollections.INSTANCE.insert(name, imageFiles)) {
                errorMessageAddImageCollection(name);

                return null;
            }
        }

        return name;
    }

    /**
     * Removes images from an image collection.
     *
     * @param collectionName name of the image collection
     * @param imageFiles     image files to removeFiles
     * @return               true if removed
     */
    public static boolean deleteImagesFromCollection(String collectionName, List<File> imageFiles) {
        if (collectionName == null) {
            throw new NullPointerException("collectionName == null");
        }

        if (imageFiles == null) {
            throw new NullPointerException("imageFiles == null");
        }

        if (confirmDelete("ImageCollectionsHelper.Confirm.DeleteSelectedFiles", collectionName)) {
            boolean removed = DatabaseImageCollections.INSTANCE.deleteImagesFrom(collectionName, imageFiles)
                              == imageFiles.size();

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
        if (collectionName == null) {
            throw new NullPointerException("collectionName == null");
        }

        boolean deleted = false;

        if (confirmDelete("ImageCollectionsHelper.Confirm.DeleteCollection", collectionName)) {
            deleted = DatabaseImageCollections.INSTANCE.delete(collectionName);

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
     * @param imageFiles      Hinzuzufügende Bilddateien
     * @return               true bei Erfolg
     */
    public static boolean addImagesToCollection(String collectionName, List<File> imageFiles) {
        if (collectionName == null) {
            throw new NullPointerException("collectionName == null");
        }

        if (imageFiles == null) {
            throw new NullPointerException("imageFiles == null");
        }

        boolean added = DatabaseImageCollections.INSTANCE.insertImagesInto(collectionName, imageFiles);

        if (!added) {
            errorMessageAddImagesToCollection(collectionName);
        }

        return added;
    }

    /**
     * Benennt eine Bildsammlung um.
     *
     * @param fromName Alter Name, der neue wird durch eine Eingabe erfragt.
     * @return        Neuer Name oder null, wenn die Sammlung nicht umbenannt
     *                wurde
     */
    public static String renameImageCollection(String fromName) {
        if (fromName == null) {
            throw new NullPointerException("fromName == null");
        }

        String newName = inputCollectionName(fromName);

        if ((newName != null) &&!newName.isEmpty()) {
            boolean renamed = DatabaseImageCollections.INSTANCE.updateRename(fromName, newName) > 0;

            if (renamed) {
                return newName;
            } else {
                errorMessageRenameImageCollection(fromName);

                return null;
            }
        }

        return null;
    }

    /**
     * Returns wether a name is valid. This is true if the name is not equals
     * to {@link ListModelImageCollections#NAME_IMAGE_COLLECTION_PREV_IMPORT}
     * ignoring the case.
     *
     * @param  name name
     * @return true if allowed
     */
    public static boolean isValidName(String name) {
        if (name == null) {
            throw new NullPointerException("name == null");
        }

        return !name.trim().equalsIgnoreCase(ListModelImageCollections.NAME_IMAGE_COLLECTION_PREV_IMPORT);
    }

    private static boolean checkIsValidName(String name) {
        if (isValidName(name)) {
            return true;
        }

        MessageDisplayer.error(null, "ImageCollectionsHelper.Error.InvalidName", name);

        return false;
    }

    private static void logAddImageCollection(String name) {
        AppLogger.logInfo(ImageCollectionsHelper.class, "ImageCollectionsHelper.Info.StartInsert", name);
    }

    private static void errorMessageAddImagesToCollection(String collectionName) {
        MessageDisplayer.error(null, "ImageCollectionsHelper.Error.AddImagesToCollection", collectionName);
    }

    private static void errorMessageAddImageCollection(String collectionName) {
        MessageDisplayer.error(null, "ImageCollectionsHelper.Error.AddImageCollection", collectionName);
    }

    private static void errorMessageDeleteImageCollection(String collectionName) {
        MessageDisplayer.error(null, "ImageCollectionsHelper.Error.DeleteImageCollection", collectionName);
    }

    private static void errorMessageDeleteImagesFromCollection(String collectionName) {
        MessageDisplayer.error(null, "ImageCollectionsHelper.Error.DeleteImagesFromCollection", collectionName);
    }

    private static void errorMessageRenameImageCollection(String collectionName) {
        MessageDisplayer.error(null, "ImageCollectionsHelper.Error.RenameImageCollection", collectionName);
    }

    private static boolean confirmDelete(String bundleKey, String collectionName) {
        return MessageDisplayer.confirmYesNo(null, bundleKey, collectionName);
    }

    private static String inputCollectionName(String defaultName) {
        String name = getCollectionName(defaultName);
        boolean willAdd = name != null;

        while ((name != null) && willAdd) {
            willAdd = false;

            String nameNextTry = name;

            if (DatabaseImageCollections.INSTANCE.exists(name) ||!checkIsValidName(name)) {
                willAdd = MessageDisplayer.confirmYesNo(null, "ImageCollectionsHelper.Confirm.InputNewCollectionName",
                        name);
                name = null;
            }

            if (willAdd) {
                name = getCollectionName(nameNextTry);
            }
        }

        return name;
    }

    private static String getCollectionName(String defaultName) {
        String name = MessageDisplayer.input("ImageCollectionsHelper.Input.CollectionName", defaultName,
                          ImageCollectionsHelper.class.getName());

        if (name != null) {
            name = name.trim();

            if (name.isEmpty()) {
                name = null;
            }
        }

        return name;
    }

    private ImageCollectionsHelper() {}
}
