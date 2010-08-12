/*
 * @(#)Translation.java    Created on 2008-09-05
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

package org.jphototagger.program.resource;

import org.jphototagger.program.app.AppLogger;

import java.util.ResourceBundle;

/**
 * Übersetzt Strings. Die Übersetzungen stehen in einer locale-spezifischen
 * Properties-Datei.
 *
 * @author  Elmar Baumann
 */
public final class Translation {
    private static final String PATH_PREFIX =
        "org/jphototagger/program/resource/properties/";
    private ResourceBundle bundle;
    private final String   propertiesFilePath;

    public Translation(String propertiesFileBasename) {
        if (propertiesFileBasename == null) {
            throw new NullPointerException("propertiesFileBasename == null");
        }

        propertiesFilePath = PATH_PREFIX + propertiesFileBasename;

        try {
            bundle = ResourceBundle.getBundle(propertiesFilePath);
        } catch (Exception ex) {
            AppLogger.logSevere(Translation.class, ex);
        }
    }

    /**
     * Übersetzt einen String.
     *
     * @param string Fremdsprachiger String (Key eines Propertys)
     * @return       Übersetzter String (Value eines Propertys) oder zu
     *               übersetzender String, falls keine Übersetzung möglich ist
     *               (Key nicht vorhanden in Properties-Datei)
     */
    public String translate(String string) {
        if (string == null) {
            throw new NullPointerException("string == null");
        }

        try {
            return bundle.getString(string);
        } catch (Exception ex) {
            AppLogger.logInfo(Translation.class,
                              "Translation.Error.NoWordbookEntry", string,
                              propertiesFilePath, ex.getLocalizedMessage());
        }

        return string;
    }

    /**
     * Übersetzt einen String.
     *
     * @param string    Fremdsprachiger String (Key eines Propertys)
     * @param alternate Alternative Übersetzung, die geliefert wird, wenn
     *                  keine Übersetzung gefunden wurde
     * @return          Übersetzter String (Value eines Propertys) oder
     *                  <code>alternate</code>, falls keine Übersetzung möglich
     *                  ist (Key nicht vorhanden in Properties-Datei)
     */
    public String translate(String string, String alternate) {
        if (string == null) {
            throw new NullPointerException("string == null");
        }

        if (alternate == null) {
            throw new NullPointerException("alternate == null");
        }

        try {
            return bundle.getString(string);
        } catch (Exception ex) {
            AppLogger.logInfo(Translation.class,
                              "Translation.Error.NoWordbookEntry", string,
                              propertiesFilePath, ex.getLocalizedMessage());
        }

        return alternate;
    }
}
