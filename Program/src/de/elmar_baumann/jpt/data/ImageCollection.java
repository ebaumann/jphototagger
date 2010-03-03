/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.data;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-03-03
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class ImageCollection {

    private String name;

    @XmlElementWrapper(name = "Filenames")
    @XmlElement(type = String.class)
    private List<String> filenames;

    public ImageCollection() {
    }

    public ImageCollection(String name, List<String> filenames) {
        this.name      = name;
        this.filenames = new ArrayList<String>(filenames);
    }

    public List<String> getFilenames() {
        return filenames == null ? null : new ArrayList<String>(filenames);
    }

    public void setFilenames(List<String> filenames) {
        this.filenames = filenames == null ? null : new ArrayList<String>(filenames);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name;
    }

}
