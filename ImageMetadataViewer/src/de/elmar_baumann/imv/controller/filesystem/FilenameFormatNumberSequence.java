package de.elmar_baumann.imv.controller.filesystem;

import de.elmar_baumann.imv.resource.Bundle;
import java.text.DecimalFormat;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/13
 */
public class FilenameFormatNumberSequence extends FilenameFormat {

    private int current;
    private int start;
    private int increment;
    private int countDigits;
    private DecimalFormat decimalFormat;

    public FilenameFormatNumberSequence(int start, int increment, int countDigits) {
        this.start = start;
        this.increment = increment;
        this.countDigits = countDigits;
        current = start;
        createDecimalFormat();
    }

    private void createDecimalFormat() {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < countDigits; i++) {
            buffer.append("0"); // NOI18N
        }
        decimalFormat = new DecimalFormat(buffer.toString());
    }

    public int getCountDigits() {
        return countDigits;
    }

    public void setCountDigits(int countDigits) {
        this.countDigits = countDigits;
        createDecimalFormat();
    }

    public int getIncrement() {
        return increment;
    }

    public void setIncrement(int increment) {
        this.increment = increment;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
        current = start;
    }

    @Override
    public void next() {
        current += increment;
    }

    @Override
    public String format() {
        return decimalFormat.format(current);
    }

    @Override
    public String toString() {
        return Bundle.getString("NumberSequenceFilenameFormat.String");
    }
}
