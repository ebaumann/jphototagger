package de.elmar_baumann.lib.component.text;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Dokument mit einer maximalen Zeichenanzahl.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/18
 */
public class MaxLengthDocument extends PlainDocument {

    private int maxLenght;

    /**
     * Konstruktor.
     * 
     * @param maxLenght Maximale Zeichenanzahl
     */
    public MaxLengthDocument(int maxLenght) {
        this.maxLenght = maxLenght;
    }

    @Override
    public void insertString(int offset, String str, AttributeSet a)
        throws BadLocationException {
        if (getLength() + str.length() > maxLenght) {
            java.awt.Toolkit.getDefaultToolkit().beep();
        } else {
            super.insertString(offset, str, a);
        }
    }

    /**
     * Liefert die maximale Zeichenanzahl.
     * 
     * @return Maximale Zeichenanzahl
     */
    public int getMaxLenght() {
        return maxLenght;
    }

    /**
     * Setzt die maximale Zeichenanzahl.
     * 
     * @param maxLenght Maximale Zeichenanzahl
     */
    public void setMaxLenght(int maxLenght) {
        this.maxLenght = maxLenght;
    }
}
