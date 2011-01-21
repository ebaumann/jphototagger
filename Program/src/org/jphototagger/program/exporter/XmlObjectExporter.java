package org.jphototagger.program.exporter;

import org.jphototagger.program.app.AppLogger;

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
 * @author Elmar Baumann
 */
public final class XmlObjectExporter {
    public static void export(Object object, File file)
            throws JAXBException, IOException {
        if (object == null) {
            throw new NullPointerException("object == null");
        }

        if (file == null) {
            throw new NullPointerException("file == null");
        }

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
