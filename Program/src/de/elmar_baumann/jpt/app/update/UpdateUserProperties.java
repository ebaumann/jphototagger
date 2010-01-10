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
package de.elmar_baumann.jpt.app.update;

import de.elmar_baumann.jpt.image.thumbnail.ThumbnailCreator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Updates the user properties.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-09-17
 */
public final class UpdateUserProperties {

    private static final Map<String, String> NEW_PATHNAME_START  = new HashMap<String, String>();
    private static final List<String>        REMOVE_KEY_PATTERNS = new ArrayList<String>();

    static {
        NEW_PATHNAME_START.put("de.elmar_baumann.imv."  , "de.elmar_baumann.jpt.");
        NEW_PATHNAME_START.put("de.elmar_bauman.cpfntc.", "de.elmar_bauman.jpt.plugin.");

        REMOVE_KEY_PATTERNS.add("^InputHelperDialog.ListKeywords.[0-9]+");
    }

    public static void update(Properties properties) {
        renamePathKeysAndValues(properties);
        removeKeys(properties);
        setThumbnailCreator(properties);
    }

    private static void renamePathKeysAndValues(Properties properties) {
        for (String className : NEW_PATHNAME_START.keySet()) {
            String       replace       = NEW_PATHNAME_START.get(className);
            List<String> replaceKeys   = new ArrayList<String>();
            List<String> replaceValues = new ArrayList<String>();
            for (Object objectKey : properties.keySet()) {
                if (objectKey instanceof String) {
                    String key = (String) objectKey;
                    String value = properties.getProperty(key);
                    if (key.startsWith(className)) {
                        replaceKeys.add(key);
                    }
                    if (value.contains(className)) {
                        replaceValues.add(key);
                    }
                }
            }
            renamePathKeys(properties, replaceKeys, className, replace);
            renamePathValues(properties, replaceValues, className, replace);
        }
    }

    private static void renamePathKeys(
            Properties   properties,
            List<String> replaceKeys,
            String       pathStart,
            String       replace) {

        for (String key : replaceKeys) {
            String renamedKey = replace + key.substring(pathStart.length());
            properties.put(renamedKey, properties.get(key));
            properties.remove(key);
        }
    }

    private static void renamePathValues(
            Properties   properties,
            List<String> replaceKeys,
            String       pathStart,
            String       replace
            ) {
        for (String key : replaceKeys) {
            String renamedValue = properties.getProperty(key).replaceAll(pathStart, replace); // Dirty
            properties.put(key, renamedValue);
        }
    }

    private static void removeKeys(Properties properties) {
        List<String> removeKeys = new ArrayList<String>();
        for (Object objectKey : properties.keySet()) {
            if (objectKey instanceof String) {
                String key = (String) objectKey;
                for (String pattern : REMOVE_KEY_PATTERNS) {
                    if (key.matches(pattern)) {
                        removeKeys.add(key);
                    }
                }
            }
        }
        for (String key : removeKeys) {
            properties.remove(key);
        }
    }

    private static final String KEY_CREATE_THUMBNAILS_WITH_EXTERNAL_APP = "UserSettings.IsCreateThumbnailsWithExternalApp";
    private static final String KEY_USE_EMBEDDED_THUMBNAILS             = "UserSettings.IsUseEmbeddedThumbnails";
    private static final String KEY_THUMBNAIL_CREATOR                   = "UserSettings.ThumbnailCreator";

    private static void setThumbnailCreator(Properties properties) {
        if (!properties.containsKey(KEY_CREATE_THUMBNAILS_WITH_EXTERNAL_APP) &&
            !properties.containsKey(KEY_USE_EMBEDDED_THUMBNAILS)) {
            return;
        }


        boolean externalApp = properties.containsKey(KEY_CREATE_THUMBNAILS_WITH_EXTERNAL_APP)
                ? properties.getProperty(KEY_CREATE_THUMBNAILS_WITH_EXTERNAL_APP).equals("1")
                : false;
        boolean useEmbedded = properties.containsKey(KEY_USE_EMBEDDED_THUMBNAILS)
                ? properties.getProperty(KEY_USE_EMBEDDED_THUMBNAILS).equals("1")
                : false;

        if (externalApp && useEmbedded) { // Should never be the case!
            useEmbedded = false;
        }

        properties.remove(KEY_CREATE_THUMBNAILS_WITH_EXTERNAL_APP);
        properties.remove(KEY_USE_EMBEDDED_THUMBNAILS);

        properties.put(KEY_THUMBNAIL_CREATOR,
                  externalApp
                ? ThumbnailCreator.EXTERNAL_APP.name()
                : useEmbedded
                ? ThumbnailCreator.EMBEDDED.name()
                : ThumbnailCreator.JAVA_IMAGE_IO.name()
                );
    }

    private UpdateUserProperties() {
    }
}
