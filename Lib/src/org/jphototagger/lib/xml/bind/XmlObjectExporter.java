package org.jphototagger.lib.xml.bind;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.jphototagger.lib.io.IoUtil;

/**
 * @author Elmar Baumann
 */
public final class XmlObjectExporter {

    public static final String ENCODING = "UTF-8";

    public static void export(Object object, File file) throws JAXBException, IOException {
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
        JAXBContext context = JAXBContext.newInstance(object.getClass());
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.marshal(object, writer);
        close(writer);
    }

    private static Writer createWriter(File file) throws IOException {
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
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
        } catch (Throwable t) {
            Logger.getLogger(XmlObjectExporter.class.getName()).log(Level.SEVERE, null, t);
        }
    }

    public static String marshal(Object object) throws JAXBException {
        if (object == null) {
            throw new NullPointerException("object == null");
        }
        StringWriter sw = new StringWriter();
        try {
            JAXBContext context = JAXBContext.newInstance(object.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, ENCODING);
            marshaller.marshal(object, sw);
        } finally {
            IoUtil.close(sw);
        }
        return sw.toString();
    }

    public static void marshal(Object object, OutputStream os) throws JAXBException {
        if (object == null) {
            throw new NullPointerException("object == null");
        }
        if (os == null) {
            throw new NullPointerException("os == null");
        }
        JAXBContext context = JAXBContext.newInstance(object.getClass());
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, ENCODING);
        marshaller.marshal(object, os);
    }

    private XmlObjectExporter() {
    }
}
