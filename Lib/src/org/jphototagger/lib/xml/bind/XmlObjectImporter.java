package org.jphototagger.lib.xml.bind;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import org.jphototagger.lib.util.StringUtil;

/**
 * @author Elmar Baumann
 */
public final class XmlObjectImporter {

    public static final String ENCODING = "UTF-8";

    public static Object importObject(File file, Class<?> clazz) throws JAXBException, FileNotFoundException {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        if (clazz == null) {
            throw new NullPointerException("clazz == null");
        }
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            JAXBContext context = JAXBContext.newInstance(clazz);
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
                Logger.getLogger(XmlObjectImporter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static <T> T unmarshal(String xmlString, Class<T> type) throws JAXBException {
        if (type == null) {
            throw new NullPointerException("type == null");
        }
        if (!StringUtil.hasContent(xmlString)) {
            return null;
        }
        JAXBContext context = JAXBContext.newInstance(type);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        JAXBElement<T> jaxbElement = unmarshaller.unmarshal(new StreamSource(new StringReader(xmlString)), type);
        T result = jaxbElement.getValue();
        return result;
    }

    public static <T> T unmarshal(InputStream is, Class<T> type) throws JAXBException, UnsupportedEncodingException {
        JAXBContext context = JAXBContext.newInstance(type);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        JAXBElement<T> jaxbElement = unmarshaller.unmarshal(new StreamSource(new InputStreamReader(is, ENCODING)), type);
        T result = jaxbElement.getValue();
        return result;
    }

    private XmlObjectImporter() {
    }
}
