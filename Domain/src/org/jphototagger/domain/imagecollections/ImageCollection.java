package org.jphototagger.domain.imagecollections;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class ImageCollection {

    public static final transient String PREVIOUS_IMPORT_NAME = "SpecialCollectionName.PreviousImport";
    public static final transient String PICKED_NAME = "SpecialCollectionName.Picked";
    public static final transient String REJECTED_NAME = "SpecialCollectionName.Rejected";
    private static final transient Set<String> SPECIAL_COLLECTION_NAMES = new HashSet<>(3);
    private static final transient Set<String> SPECIAL_COLLECTION_NAMES_LOWER_CASE = new HashSet<>(3);
    private static final transient Set<String> LOCALIZED_SPECIAL_NAMES_LC = new HashSet<>();
    private static final transient Map<String, String> LOCALIZED_NAME_OF_SPECIAL_NAME_LC = new HashMap<>(3);

    static {
        SPECIAL_COLLECTION_NAMES.add(PICKED_NAME);
        SPECIAL_COLLECTION_NAMES.add(PREVIOUS_IMPORT_NAME);
        SPECIAL_COLLECTION_NAMES.add(REJECTED_NAME);
        for (String specialCollectionName : SPECIAL_COLLECTION_NAMES) {
            SPECIAL_COLLECTION_NAMES_LOWER_CASE.add(specialCollectionName.toLowerCase());
        }
        LOCALIZED_NAME_OF_SPECIAL_NAME_LC.put(PREVIOUS_IMPORT_NAME.toLowerCase(), Bundle.getString(ImageCollection.class, "ImageCollection.Name.PrevImport"));
        LOCALIZED_NAME_OF_SPECIAL_NAME_LC.put(PICKED_NAME.toLowerCase(), Bundle.getString(ImageCollection.class, "ImageCollection.Name.Picked"));
        LOCALIZED_NAME_OF_SPECIAL_NAME_LC.put(REJECTED_NAME.toLowerCase(), Bundle.getString(ImageCollection.class, "ImageCollection.Name.Rejected"));
        for (String name : LOCALIZED_NAME_OF_SPECIAL_NAME_LC.values()) {
            LOCALIZED_SPECIAL_NAMES_LC.add(name.toLowerCase());
        }
    }

    @XmlElementWrapper(name = "Filenames")
    @XmlElement(type = File.class)
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
        this.imageFiles = new ArrayList<>(imageFiles);
    }

    public List<File> getFiles() {
        return imageFiles == null
                ? Collections.<File>emptyList()
                : new ArrayList<>(imageFiles);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return SPECIAL_COLLECTION_NAMES_LOWER_CASE.contains(collectionNameLowerCase)
                || LOCALIZED_SPECIAL_NAMES_LC.contains(collectionNameLowerCase);
    }

    public static Set<String> getSpecialCollectionNames() {
        return Collections.unmodifiableSet(SPECIAL_COLLECTION_NAMES);
    }

    /**
     * @param name null ok
     * @return loclized name if {@code name} is a special collection name, else unchanged name
     */
    public static String getLocalizedName(String name) {
        if (name == null) {
            return null;
        }
        String nameLowerCase = name.toLowerCase();
        if (LOCALIZED_NAME_OF_SPECIAL_NAME_LC.containsKey(nameLowerCase)) {
            return LOCALIZED_NAME_OF_SPECIAL_NAME_LC.get(nameLowerCase);
        }
        return name;
    }

    public static int getSpecialCollectionCount() {
        return SPECIAL_COLLECTION_NAMES.size();
    }
}
