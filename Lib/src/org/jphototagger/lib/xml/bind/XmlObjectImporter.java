package org.jphototagger.lib.xml.bind;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class XmlObjectImporter {

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

    private XmlObjectImporter() {
    }
}
