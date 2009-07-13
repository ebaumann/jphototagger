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
public final class MaxLengthDocument extends PlainDocument {

    private final int maxLenght;

    /**
     * Konstruktor.
     * 
     * @param maxLenght Maximale Zeichenanzahl. Bedingung: {@code maxLenght >= 0}.
     * @throws IllegalArgumentException wenn {@code maxLenght < 0}
     */
    public MaxLengthDocument(int maxLenght) {
        if (maxLenght < 0)
            throw new IllegalArgumentException("maxLength < 0: " + maxLenght); // NOI18N
        this.maxLenght = maxLenght;
    }

    @Override
    public void insertString(int offset, String str, AttributeSet a)
        throws BadLocationException {
        if (getLength() + str.length() > maxLenght) { // TODO Löschen bei Überschreiben berücksichtigen
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
}
