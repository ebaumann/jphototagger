/*
 * @(#)ImageCollection.java    Created on 2010-03-03
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
 * @author  Elmar Baumann
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class ImageCollection {
    @XmlElementWrapper(name = "Filenames")
    @XmlElement(type = String.class)
    private List<File> imageFiles;
    private String     name;

    public ImageCollection() {}

    public ImageCollection(String name, List<File> imageFiles) {
        this.name       = name;
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
}
