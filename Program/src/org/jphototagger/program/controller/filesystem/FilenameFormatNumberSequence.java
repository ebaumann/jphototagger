package org.jphototagger.program.controller.filesystem;

import org.jphototagger.program.resource.JptBundle;

import java.text.DecimalFormat;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class FilenameFormatNumberSequence extends FilenameFormat {
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
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < countDigits; i++) {
            sb.append("0");
        }

        decimalFormat = new DecimalFormat(sb.toString());
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
        return JptBundle.INSTANCE.getString("FilenameFormatNumberSequence.String");
    }

    private FilenameFormatNumberSequence() {}
}
