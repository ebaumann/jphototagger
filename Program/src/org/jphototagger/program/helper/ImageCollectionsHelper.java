package org.jphototagger.program.helper;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdesktop.swingx.JXList;
import org.jphototagger.domain.repository.ImageCollectionsRepository;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.model.ListModelImageCollections;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.openide.util.Lookup;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ImageCollectionsHelper {

    private static final Logger LOGGER = Logger.getLogger(ImageCollectionsHelper.class.getName());

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
                    LOGGER.log(Level.WARNING, "No Image collection selected!");
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
     * @param imageFiles image files to saveImageCollection
     * @return           name of the collection or null, if no image collection
     *                   was created
     */
    public static String insertImageCollection(List<File> imageFiles) {
        if (imageFiles == null) {
            throw new NullPointerException("imageFiles == null");
        }

        String name = inputCollectionName("");

        if ((name != null) && !name.isEmpty()) {
            logAddImageCollection(name);
            ImageCollectionsRepository repo = Lookup.getDefault().lookup(ImageCollectionsRepository.class);
            if (!repo.saveImageCollection(name, imageFiles)) {
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

        String message = Bundle.getString(ImageCollectionsHelper.class, "ImageCollectionsHelper.Confirm.DeleteSelectedFiles", collectionName);

        if (confirmDelete(message)) {
            ImageCollectionsRepository repo = Lookup.getDefault().lookup(ImageCollectionsRepository.class);
            boolean removed = repo.deleteImagesFromImageCollection(collectionName, imageFiles)
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
        String message = Bundle.getString(ImageCollectionsHelper.class, "ImageCollectionsHelper.Confirm.DeleteCollection", collectionName);

        if (confirmDelete(message)) {
            ImageCollectionsRepository repo = Lookup.getDefault().lookup(ImageCollectionsRepository.class);
            deleted = repo.deleteImageCollection(collectionName);

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

        ImageCollectionsRepository repo = Lookup.getDefault().lookup(ImageCollectionsRepository.class);
        boolean added = repo.insertImagesIntoImageCollection(collectionName, imageFiles);

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

        if ((newName != null) && !newName.isEmpty()) {
            ImageCollectionsRepository repo = Lookup.getDefault().lookup(ImageCollectionsRepository.class);
            boolean renamed = repo.updateRenameImageCollection(fromName, newName) > 0;

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

        String message = Bundle.getString(ImageCollectionsHelper.class, "ImageCollectionsHelper.Error.InvalidName", name);
        MessageDisplayer.error(null, message);

        return false;
    }

    private static void logAddImageCollection(String name) {
        LOGGER.log(Level.INFO, "Insert photo album ''{0}'' into the database", name);
    }

    private static void errorMessageAddImagesToCollection(String collectionName) {
        String message = Bundle.getString(ImageCollectionsHelper.class, "ImageCollectionsHelper.Error.AddImagesToCollection", collectionName);
        MessageDisplayer.error(null, message);
    }

    private static void errorMessageAddImageCollection(String collectionName) {
        String message = Bundle.getString(ImageCollectionsHelper.class, "ImageCollectionsHelper.Error.AddImageCollection", collectionName);
        MessageDisplayer.error(null, message);
    }

    private static void errorMessageDeleteImageCollection(String collectionName) {
        String message = Bundle.getString(ImageCollectionsHelper.class, "ImageCollectionsHelper.Error.DeleteImageCollection", collectionName);
        MessageDisplayer.error(null, message);
    }

    private static void errorMessageDeleteImagesFromCollection(String collectionName) {
        String message = Bundle.getString(ImageCollectionsHelper.class, "ImageCollectionsHelper.Error.DeleteImagesFromCollection", collectionName);
        MessageDisplayer.error(null, message);
    }

    private static void errorMessageRenameImageCollection(String collectionName) {
        String message = Bundle.getString(ImageCollectionsHelper.class, "ImageCollectionsHelper.Error.RenameImageCollection", collectionName);
        MessageDisplayer.error(null, message);
    }

    private static boolean confirmDelete(String message) {
        return MessageDisplayer.confirmYesNo(null, message);
    }

    private static String inputCollectionName(String defaultName) {
        String name = getCollectionName(defaultName);
        boolean willAdd = name != null;

        while ((name != null) && willAdd) {
            willAdd = false;

            String nameNextTry = name;
            ImageCollectionsRepository repo = Lookup.getDefault().lookup(ImageCollectionsRepository.class);

            if (repo.existsImageCollection(name) || !checkIsValidName(name)) {
                String message = Bundle.getString(ImageCollectionsHelper.class, "ImageCollectionsHelper.Confirm.InputNewCollectionName", name);
                willAdd = MessageDisplayer.confirmYesNo(null, message);
                name = null;
            }

            if (willAdd) {
                name = getCollectionName(nameNextTry);
            }
        }

        return name;
    }

    private static String getCollectionName(String defaultName) {
        String info = Bundle.getString(ImageCollectionsHelper.class, "ImageCollectionsHelper.Input.CollectionName");
        String input = defaultName;
        String name = MessageDisplayer.input(info, input);

        if (name != null) {
            name = name.trim();

            if (name.isEmpty()) {
                name = null;
            }
        }

        return name;
    }

    private ImageCollectionsHelper() {
    }
}
