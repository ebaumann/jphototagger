package org.jphototagger.lib.swing.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.DocumentEvent;
import javax.swing.text.Document;

/**
 * @author Elmar Baumann
 */
public final class DocumentUtil {

    public static String getText(DocumentEvent e) {
        if (e == null) {
            throw new NullPointerException("e == null");
        }
        Document document = e.getDocument();
        int length = document.getLength();
        try {
            if (length > 0) {
                return document.getText(0, length);
            }
        } catch (Throwable t) {
            Logger.getLogger(DocumentUtil.class.getName()).log(Level.SEVERE, null, t);
        }
        return "";
    }

    private DocumentUtil() {
    }
}
