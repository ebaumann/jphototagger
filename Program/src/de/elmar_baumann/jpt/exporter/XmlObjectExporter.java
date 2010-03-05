/*
 * JPhotoTagger tags and finds images fast.
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

package de.elmar_baumann.jpt.exporter;

import de.elmar_baumann.jpt.app.AppLogger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 *
 *
 * @author  Elmar Baumann
 * @version 2010-03-02
 */
public final class XmlObjectExporter {
    public static void export(Object object, File file)
            throws JAXBException, IOException {
        Writer writer = createWriter(file);

        if (writer == null) {
            return;
        }

        JAXBContext context    = JAXBContext.newInstance(object.getClass());
        Marshaller  marshaller = context.createMarshaller();

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.marshal(object, writer);
        close(writer);
    }

    private static Writer createWriter(File file) throws IOException {
        Writer writer = null;

        try {
            writer = new OutputStreamWriter(new FileOutputStream(file),
                                            "UTF-8");
        } catch (IOException ex) {
            close(writer);

            throw ex;
        }

        return writer;
    }

    private static void close(Writer writer) {
        if (writer == null) {
            return;
        }

        try {
            writer.flush();
            writer.close();
        } catch (Exception ex) {
            AppLogger.logSevere(XmlObjectExporter.class, ex);
        }
    }

    private XmlObjectExporter() {}
}
