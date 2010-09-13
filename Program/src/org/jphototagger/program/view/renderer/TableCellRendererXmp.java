/*
 * @(#)TableCellRendererXmp.java    Created on 2008-10-05
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

package org.jphototagger.program.view.renderer;

import com.adobe.xmp.properties.XMPPropertyInfo;

import org.jphototagger.lib.componentutil.TableUtil;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.database.metadata.selections.XmpInDatabase;
import org.jphototagger.program.image.metadata.xmp.XmpMetadata;
import org.jphototagger.program.resource.Translation;

import java.awt.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Rendert Tabellen mit
 * {@link com.adobe.xmp.properties.XMPPropertyInfo}-Spaltenobjekten.
 *
 * @author  Elmar Baumann
 */
public final class TableCellRendererXmp extends FormatterLabelMetadata
        implements TableCellRenderer {
    private static final String      DELIMITER_PATH      = "/";
    private static final String      DELIMITER_NAMESPACE = ":";
    private static final Translation TRANSLATION_XMP =
        new Translation("XmpPropertyTranslations");
    private static final Translation TRANSLATION_XMP_EXIF_TAG_ID =
        new Translation("XmpPropertyExifTagIdTranslations");
    private static final Translation TRANSLATION_EXIF =
        new Translation("ExifTagIdTagNameTranslations");

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel          cellLabel       = new JLabel();
        XMPPropertyInfo xmpPropertyInfo = (XMPPropertyInfo) value;

        setDefaultCellColors(cellLabel, isSelected);
        setIsStoredInDatabaseColor(cellLabel, xmpPropertyInfo, isSelected);

        if (column == 0) {
            setHeaderFont(cellLabel);

            String xmpPath = xmpPropertyInfo.getPath();

            TableUtil.embedTableCellTextInHtml(
                table, row, cellLabel, translate(xmpPath, xmpPath),
                AppLookAndFeel.TABLE_MAX_CHARS_ROW_HEADER,
                AppLookAndFeel.TABLE_ROW_HEADER_CSS);
        } else {
            assert column < 2 : column;
            setContentFont(cellLabel);
            TableUtil.embedTableCellTextInHtml(
                table, row, cellLabel, xmpPropertyInfo.getValue().toString(),
                AppLookAndFeel.TABLE_MAX_CHARS_CELL,
                AppLookAndFeel.TABLE_CELL_CSS);
        }

        return cellLabel;
    }

    private void setIsStoredInDatabaseColor(JLabel cellLabel,
            XMPPropertyInfo xmpPropertyInfo, boolean isSel) {
        if (XmpInDatabase.isInDatabase(xmpPropertyInfo.getPath())) {
            setIsStoredInDatabaseColors(cellLabel, isSel);
        }
    }

    private static String translate(String path, String alternate) {
        StringBuilder newPath             = new StringBuilder();
        List<String>  pathComponents      = getPathComponents(path);
        int           pathComponentsCount = pathComponents.size();

        for (int i = 0; i < pathComponentsCount; i++) {
            String pathComponent = pathComponents.get(i);
            String withoutIndex  = getWithoutIndex(pathComponent);
            String translated    = (isExifNamespace(pathComponent)
                                    ? TRANSLATION_EXIF.translate(
                                        TRANSLATION_XMP_EXIF_TAG_ID.translate(
                                            withoutIndex, alternate))
                                    : TRANSLATION_XMP.translate(withoutIndex,
                                        alternate));

            newPath.append(getWithoutNamespace(translated)).append(
                getIndexString(pathComponent)).append(
                ((pathComponentsCount > 1) && (i < pathComponentsCount - 1))
                ? DELIMITER_PATH
                : "");
        }

        return newPath.toString();
    }

    private static List<String> getPathComponents(String path) {
        List<String>    components = new ArrayList<String>();
        StringTokenizer tokenizer  = new StringTokenizer(path, DELIMITER_PATH);

        while (tokenizer.hasMoreTokens()) {
            components.add(tokenizer.nextToken());
        }

        return components;
    }

    private static String getWithoutIndex(String string) {
        if (hasIndex(string)) {
            int startIndex = string.lastIndexOf('[');

            return string.substring(0, startIndex);
        }

        return string;
    }

    private static String getIndexString(String string) {
        if (hasIndex(string)) {
            int startIndex = string.lastIndexOf('[');

            return string.substring(startIndex);
        }

        return "";
    }

    private static boolean hasIndex(String string) {
        return string.matches("..*\\[[0-9]+\\]$");
    }

    private static String getWithoutNamespace(String string) {
        if (hasNamespace(string)) {
            int indexDelim = string.indexOf(DELIMITER_NAMESPACE);

            return string.substring(indexDelim + 1);
        }

        return string;
    }

    private static boolean hasNamespace(String string) {
        int indexDelim = string.indexOf(DELIMITER_NAMESPACE);

        if (indexDelim > 0) {
            return XmpMetadata.isKnownNamespace(string.substring(0,
                    indexDelim));
        }

        return false;
    }

    private static boolean isExifNamespace(String string) {
        return string.startsWith("exif:");
    }
}
