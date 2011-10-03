package org.jphototagger.domain.imagecollections;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.jphototagger.lib.util.Bundle;

/**
 *
 *
 * @author Elmar Baumann
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class ImageCollection {

    public static final transient String PREVIOUS_IMPORT_NAME = Bundle.getString(ImageCollection.class, "ImageCollection.Name.PrevImport");
    public static final transient String PICKED_NAME = Bundle.getString(ImageCollection.class, "ImageCollection.Name.Picked");
    public static final transient String REJECTED_NAME = Bundle.getString(ImageCollection.class, "ImageCollection.Name.Rejected");
    private static final transient Set<String> SPECIAL_COLLECTION_NAMES = new HashSet<String>(3);
    private static final transient Set<String> SPECIAL_COLLECTION_NAMES_LOWER_CASE = new HashSet<String>(3);

    static {
        SPECIAL_COLLECTION_NAMES.add(PICKED_NAME);
        SPECIAL_COLLECTION_NAMES.add(PREVIOUS_IMPORT_NAME);
        SPECIAL_COLLECTION_NAMES.add(REJECTED_NAME);

        for (String specialCollectionName : SPECIAL_COLLECTION_NAMES) {
            SPECIAL_COLLECTION_NAMES_LOWER_CASE.add(specialCollectionName.toLowerCase());
        }
    }

    @XmlElementWrapper(name = "Filenames")
    @XmlElement(type = String.class)
    private List<File> imageFiles;
    private String name;

    public ImageCollection() {
    }

    public ImageCollection(String name, List<File> imageFiles) {
        if (name == null) {
            throw new NullPointerException("name == null");
        }

        if (imageFiles == null) {
            throw new NullPointerException("imageFiles == null");
        }

        this.name = name;
        this.imageFiles = new ArrayList<File>(imageFiles);
    }

    public List<File> getFiles() {
        return (imageFiles == null)
                ? null
                : new ArrayList<File>(imageFiles);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = (name == null)
                ? null
                : name;
    }

    @Override
    public String toString() {
        return name == null
                ? ""
                : name;
    }

    public static boolean isSpecialCollection(String collectionName) {
        if (collectionName == null) {
            throw new NullPointerException("collectionName == null");
        }

        String collectionNameLowerCase = collectionName.toLowerCase();

        return SPECIAL_COLLECTION_NAMES_LOWER_CASE.contains(collectionNameLowerCase);
    }

    public static Set<String> getSpecialCollectionNames() {
        return Collections.unmodifiableSet(SPECIAL_COLLECTION_NAMES);
    }

    public static int getSpecialCollectionCount() {
        return SPECIAL_COLLECTION_NAMES.size();
    }
}
