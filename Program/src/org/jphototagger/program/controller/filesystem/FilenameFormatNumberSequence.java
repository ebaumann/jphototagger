/*
 * @(#)FilenameFormatNumberSequence.java    Created on 2008-10-13
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.controller.filesystem;

import org.jphototagger.program.resource.JptBundle;

import java.text.DecimalFormat;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class FilenameFormatNumberSequence extends FilenameFormat {
    private int           current;
    private int           start;
    private int           increment;
    private int           countDigits;
    private DecimalFormat decimalFormat;

    public FilenameFormatNumberSequence(int start, int increment,
            int countDigits) {
        this.start       = start;
        this.increment   = increment;
        this.countDigits = countDigits;
        current          = start;
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
        current    = start;
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
        return JptBundle.INSTANCE.getString(
            "FilenameFormatNumberSequence.String");
    }

    private FilenameFormatNumberSequence() {}
}
