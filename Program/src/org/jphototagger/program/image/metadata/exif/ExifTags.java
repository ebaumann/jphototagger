/*
 * @(#)ExifTags.java    Created on 2010-01-01
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

package org.jphototagger.program.image.metadata.exif;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * EXIF tags separated by their TIFF IFD (image file directory).
 *
 * @author Elmar Baumann
 */
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
        return exifTags.size() + gpsTags.size() + interoperabilityTags.size()
               + makerNoteTags.size();
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
}
