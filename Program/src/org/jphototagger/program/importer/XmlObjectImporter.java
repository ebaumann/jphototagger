/*
 * @(#)XmlObjectImporter.java    Created on 2010-03-02
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.importer;

import org.jphototagger.program.app.AppLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class XmlObjectImporter {
    public static Object importObject(File file, Class<?> clazz)
            throws JAXBException, FileNotFoundException {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        if (clazz == null) {
            throw new NullPointerException("clazz == null");
        }

        FileInputStream fis = null;

        try {
            fis = new FileInputStream(file);

            JAXBContext  context      = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            return unmarshaller.unmarshal(fis);
        } finally {
            close(fis);
        }
    }

    private static void close(FileInputStream fis) {
        if (fis != null) {
            try {
                fis.close();
            } catch (IOException ex) {
                AppLogger.logSevere(XmlObjectImporter.class, ex);
            }
        }
    }

    private XmlObjectImporter() {}
}
