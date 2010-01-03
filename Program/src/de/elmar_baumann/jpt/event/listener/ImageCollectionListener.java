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
package de.elmar_baumann.jpt.event.listener;

import java.util.List;

/**
 * Reagiert auf Ereignisse bezüglich Bildsammlungen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-07
 */
public interface ImageCollectionListener {

    /**
     * Erzeugt eine (neue) Bildsammlung.
     *
     * @param filenames Namen der Dateien für die Bildsammlung
     */
    public void createCollection(List<String> filenames);

    /**
     * Löscht eine Bildsammlung.
     *
     * @param collectionName Name der Bildsammlung
     */
    public void deleteCollection(String collectionName);

    /**
     * Entfernt Dateien von einer Bildsammlung.
     *
     * @param filenames Namen der zu entfernenden Bilddateien
     */
    public void deleteFromCollection(List<String> filenames);

    /**
     * Fügt einer Bildsammlung Dateien hinzu.
     *
     * @param filenames Namen der hinzuzufügenden Bilddateien
     */
    public void addToCollection(List<String> filenames);

    /**
     * Benennt eine Bildsammlung um.
     *
     * @param collectionName Name der Bildsammlung
     */
    public void renameCollection(String collectionName);
}
