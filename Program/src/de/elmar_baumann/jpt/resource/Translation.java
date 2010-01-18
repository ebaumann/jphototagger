/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.resource;

import de.elmar_baumann.jpt.app.AppLogger;
import java.util.ResourceBundle;

/**
 * Übersetzt Strings. Die Übersetzungen stehen in einer locale-spezifischen
 * Properties-Datei.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-05
 */
public final class Translation {

    private static final String         PATH_PREFIX = "de/elmar_baumann/jpt/resource/properties/";
    private              ResourceBundle bundle;

    public Translation(String propertiesFileBasename) {
        try {

            bundle = ResourceBundle.getBundle(PATH_PREFIX + propertiesFileBasename);

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
        try {
            return bundle.getString(string);

        } catch (Exception ex) {

            AppLogger.logSevere(Translation.class, ex);
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
        try {
            return bundle.getString(string);

        } catch (Exception ex) {

            AppLogger.logSevere(Translation.class, ex);

        }
        return alternate;
    }
}
