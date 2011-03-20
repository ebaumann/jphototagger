package org.jphototagger.program.image.metadata.exif;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import org.jphototagger.program.exporter.XmlObjectExporter;
import org.jphototagger.program.importer.XmlObjectImporter;

/**
 * EXIF tags separated by their TIFF IFD (image file directory).
 *
 * @author Elmar Baumann
 */
@XmlRootElement(name = "exiftags")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso(ExifTag.class)
public final class ExifTags {
    private String makerNoteDescription;

    /**
     * Tags of EXIF IFD
     */
    private Set<ExifTag> exifTags = new HashSet<ExifTag>();

    /**
     * Tags of GPS IFD
     */
    private Set<ExifTag> gpsTags = new HashSet<ExifTag>();

    /**
     * Tags of  Interoperability IFD
     */
    private Set<ExifTag> interoperabilityTags = new HashSet<ExifTag>();

    /**
     * Maker note tags of EXIF IFD
     */
    private Set<ExifTag> makerNoteTags = new HashSet<ExifTag>();

    public Set<ExifTag> getExifTags() {
        return Collections.unmodifiableSet(exifTags);
    }

    public Set<ExifTag> getGpsTags() {
        return Collections.unmodifiableSet(gpsTags);
    }

    public Set<ExifTag> getInteroperabilityTags() {
        return Collections.unmodifiableSet(interoperabilityTags);
    }

    public Set<ExifTag> getMakerNoteTags() {
        return Collections.unmodifiableSet(makerNoteTags);
    }

    public int tagCount() {
        return exifTags.size() + gpsTags.size() + interoperabilityTags.size() + makerNoteTags.size();
    }

    public List<ExifTag> asList() {
        List<ExifTag> allTags = new ArrayList<ExifTag>(tagCount());

        allTags.addAll(exifTags);
        allTags.addAll(gpsTags);
        allTags.addAll(interoperabilityTags);
        allTags.addAll(makerNoteTags);

        return allTags;
    }

    public void addExifTag(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        exifTags.add(exifTag);
    }

    public void addGpsTag(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        gpsTags.add(exifTag);
    }

    public void addInteroperabilityTag(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        interoperabilityTags.add(exifTag);
    }

    public void addMakerNoteTags(Collection<ExifTag> tags) {
        if (tags == null) {
            throw new NullPointerException("tags == null");
        }

        makerNoteTags.addAll(tags);
    }

    public void addMakerNoteTag(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        makerNoteTags.add(exifTag);
    }

    public String getMakerNoteDescription() {
        return makerNoteDescription;
    }

    public void setMakerNoteDescription(String makerNoteDescription) {
        if (makerNoteDescription == null) {
            throw new NullPointerException("makerNoteDescription == null");
        }

        this.makerNoteDescription = makerNoteDescription;
    }

    public ExifTag exifTagById(int id) {
        return byId(exifTags, id);
    }

    public ExifTag gpsTagById(int id) {
        return byId(gpsTags, id);
    }

    public ExifTag interoparabilityTagById(int id) {
        return byId(interoperabilityTags, id);
    }

    public ExifTag makerNoteTagById(int id) {
        return byId(makerNoteTags, id);
    }

    public void removeExifTag(ExifTag tag) {
        if (tag == null) {
            throw new NullPointerException("tag == null");
        }

        exifTags.remove(tag);
    }

    public void removeMakerNoteTag(ExifTag tag) {
        if (tag == null) {
            throw new NullPointerException("tag == null");
        }

        makerNoteTags.remove(tag);
    }

    public void removeInteroperabilityTag(ExifTag tag) {
        if (tag == null) {
            throw new NullPointerException("tag == null");
        }

        interoperabilityTags.remove(tag);
    }

    public void removeGpsTag(ExifTag tag) {
        if (tag == null) {
            throw new NullPointerException("tag == null");
        }

        gpsTags.remove(tag);
    }

    private ExifTag byId(Collection<ExifTag> tags, int id) {
        for (ExifTag tag : tags) {
            if (tag.idValue() == id) {
                return tag;
            }
        }

        return null;
    }

    public void writeToFile(File file) throws Exception {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        XmlObjectExporter.export(this, file);
    }

    public static ExifTags readFromFile(File file) throws Exception {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        return (ExifTags) XmlObjectImporter.importObject(file, ExifTags.class);
    }
}
