/*
 * @(#)ModifyImageCollections.java    Created on 2008-10-05
 *
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

package de.elmar_baumann.jpt.helper;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.database.DatabaseImageCollections;
import de.elmar_baumann.jpt.model.ListModelImageCollections;

import java.io.File;

import java.util.List;

/**
 * Verwaltet Bildsammlungen der Datenbank.
 *
 * @author  Elmar Baumann
 */
public final class ModifyImageCollections {
    private ModifyImageCollections() {}

    /**
     * Inserts a new image collection, prompts the user for the name.
     *
     * @param imageFiles image files to insert
     * @return           name of the collection or null, if no image collection
     *                   was created
     */
    public static String insertImageCollection(List<File> imageFiles) {
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
     * @param imageFiles     image files to remove
     * @return               true if removed
     */
    public static boolean deleteImagesFromCollection(String collectionName,
            List<File> imageFiles) {
        if (confirmDelete("ModifyImageCollections.Confirm.DeleteSelectedFiles",
                          collectionName)) {
            boolean removed =
                DatabaseImageCollections.INSTANCE.deleteImagesFrom(
                    collectionName, imageFiles) == imageFiles.size();

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

        if (confirmDelete("ModifyImageCollections.Confirm.DeleteCollection",
                          collectionName)) {
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
    public static boolean addImagesToCollection(String collectionName,
            List<File> imageFiles) {
        boolean added =
            DatabaseImageCollections.INSTANCE.insertImagesInto(collectionName,
                imageFiles);

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

        if ((newName != null) &&!newName.isEmpty()) {
            boolean renamed =
                DatabaseImageCollections.INSTANCE.updateRename(oldName,
                    newName) > 0;

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
     * to {@link ListModelImageCollections#NAME_IMAGE_COLLECTION_PREV_IMPORT}
     * ignoring the case.
     *
     * @param  name name
     * @return true if allowed
     */
    public static boolean isValidName(String name) {
        return !name.trim().equalsIgnoreCase(
            ListModelImageCollections.NAME_IMAGE_COLLECTION_PREV_IMPORT);
    }

    private static boolean checkIsValidName(String name) {
        if (isValidName(name)) {
            return true;
        }

        MessageDisplayer.error(null,
                               "ModifyImageCollections.Error.InvalidName",
                               name);

        return false;
    }

    private static void logAddImageCollection(String name) {
        AppLogger.logInfo(ModifyImageCollections.class,
                          "ModifyImageCollections.Info.StartInsert", name);
    }

    private static void errorMessageAddImagesToCollection(
            String collectionName) {
        MessageDisplayer.error(
            null, "ModifyImageCollections.Error.AddImagesToCollection",
            collectionName);
    }

    private static void errorMessageAddImageCollection(String collectionName) {
        MessageDisplayer.error(
            null, "ModifyImageCollections.Error.AddImageCollection",
            collectionName);
    }

    private static void errorMessageDeleteImageCollection(
            String collectionName) {
        MessageDisplayer.error(
            null, "ModifyImageCollections.Error.DeleteImageCollection",
            collectionName);
    }

    private static void errorMessageDeleteImagesFromCollection(
            String collectionName) {
        MessageDisplayer.error(
            null, "ModifyImageCollections.Error.DeleteImagesFromCollection",
            collectionName);
    }

    private static void errorMessageRenameImageCollection(
            String collectionName) {
        MessageDisplayer.error(
            null, "ModifyImageCollections.Error.RenameImageCollection",
            collectionName);
    }

    private static boolean confirmDelete(String bundleKey,
            String collectionName) {
        return MessageDisplayer.confirmYesNo(null, bundleKey, collectionName);
    }

    private static String inputCollectionName(String defaultName) {
        String  name    = getCollectionName(defaultName);
        boolean willAdd = name != null;

        while ((name != null) && willAdd) {
            willAdd = false;

            String nameNextTry = name;

            if (DatabaseImageCollections.INSTANCE.exists(name)
                    ||!checkIsValidName(name)) {
                willAdd = MessageDisplayer.confirmYesNo(
                    null,
                    "ModifyImageCollections.Confirm.InputNewCollectionName",
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
        String name = MessageDisplayer.input(
                          "ModifyImageCollections.Input.CollectionName",
                          defaultName, ModifyImageCollections.class.getName());

        if (name != null) {
            name = name.trim();

            if (name.isEmpty()) {
                name = null;
            }
        }

        return name;
    }
}
