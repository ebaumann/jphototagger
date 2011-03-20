package org.jphototagger.program.data;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 *
 * @author Elmar Baumann
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class ImageCollection {
    @XmlElementWrapper(name = "Filenames")
    @XmlElement(type = String.class)
    private List<File> imageFiles;
    private String name;

    public ImageCollection() {}

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
}
