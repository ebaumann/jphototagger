/*
 * @(#)RegexFileFilter.java    Created on 2008-10-05
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.lib.io.filefilter;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Filter für Dateien, Verzeichnisse werden abgelehnt. Akzeptiert
 * reguläre Ausdrücke als Match-Pattern.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann
 */
public final class RegexFileFilter implements java.io.FileFilter {
    private List<String> acceptedPatterns = new ArrayList<String>();

    /**
     * Erzeugt einen Dateifilter.
     *
     * @param acceptedPatterns String mit regulären Ausdrücken für akzeptierte
     *                         Dateien
     * @param delim            Begrenzer zwischen den einzelnen Mustern
     */
    public RegexFileFilter(String acceptedPatterns, String delim) {
        if (acceptedPatterns == null) {
            throw new NullPointerException("acceptedPatterns == null");
        }

        if (delim == null) {
            throw new NullPointerException("delim == null");
        }

        setAcceptedValues(acceptedPatterns, delim);
    }

    private void setAcceptedValues(String acceptedValueString, String delim) {
        StringTokenizer token = new StringTokenizer(acceptedValueString, delim);

        while (token.hasMoreElements()) {
            acceptedPatterns.add(token.nextToken());
        }
    }

    @Override
    public boolean accept(File pathname) {
        String filename = pathname.getName();

        for (String pattern : acceptedPatterns) {
            if (filename.matches(pattern)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns a file filter for f file chooser.
     *
     * @param  description  description
     * @return file filter
     */
    public javax.swing.filechooser.FileFilter forFileChooser(
            String description) {
        return new FileChooserFilter(this, description);
    }
}
