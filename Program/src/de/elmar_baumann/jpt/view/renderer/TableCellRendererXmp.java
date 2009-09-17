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
package de.elmar_baumann.jpt.view.renderer;

import com.adobe.xmp.properties.XMPPropertyInfo;
import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.database.metadata.selections.XmpInDatabase;
import de.elmar_baumann.jpt.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.jpt.resource.Translation;
import de.elmar_baumann.lib.componentutil.TableUtil;
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
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class TableCellRendererXmp extends FormatterLabelMetadata
        implements TableCellRenderer {

    private static final String DELIMITER_PATH = "/"; // NOI18N
    private static final String DELIMITER_NAMESPACE = ":"; // NOI18N
    private static final Translation TRANSLATION_XMP = new Translation(
            "XmpPropertyTranslations"); // NOI18N
    private static final Translation TRANSLATION_XMP_EXIF_TAG_ID = new Translation(
            "XmpPropertyExifTagIdTranslations"); // NOI18N
    private static final Translation TRANSLATION_EXIF = new Translation(
            "ExifTagIdTagNameTranslations"); // NOI18N

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        JLabel cellLabel = new JLabel();
        XMPPropertyInfo xmpPropertyInfo = (XMPPropertyInfo) value;

        setDefaultCellColors(cellLabel, isSelected);
        setIsStoredInDatabaseColor(cellLabel, xmpPropertyInfo, isSelected);

        if (column == 0) {
            setHeaderFont(cellLabel);
            TableUtil.embedTableCellTextInHtml(table, row, cellLabel,
                    translate(xmpPropertyInfo.getPath()),
                    AppLookAndFeel.TABLE_MAX_CHARS_ROW_HEADER,
                    AppLookAndFeel.TABLE_CSS_ROW_HEADER);
        } else {
            assert column < 2 : column;
            setContentFont(cellLabel);
            TableUtil.embedTableCellTextInHtml(table, row, cellLabel,
                    xmpPropertyInfo.getValue().toString(),
                    AppLookAndFeel.TABLE_MAX_CHARS_CELL,
                    AppLookAndFeel.TABLE_CSS_CELL);
        }
        return cellLabel;
    }

    private void setIsStoredInDatabaseColor(
            JLabel cellLabel, XMPPropertyInfo xmpPropertyInfo, boolean isSel) {
        if (XmpInDatabase.isInDatabase(xmpPropertyInfo.getPath())) {
            setIsStoredInDatabaseColors(cellLabel, isSel);
        }
    }

    private static String translate(String path) {
        StringBuffer newPath = new StringBuffer();
        List<String> pathComponents = getPathComponents(path);
        int count = pathComponents.size();
        for (int i = 0; i < count; i++) {
            String pathComponent = pathComponents.get(i);
            String withoutIndex = getWithoutIndex(pathComponent);
            String translated = (isExifNamespace(pathComponent)
                                 ? TRANSLATION_EXIF.translate(
                    TRANSLATION_XMP_EXIF_TAG_ID.translate(withoutIndex))
                                 : TRANSLATION_XMP.translate(withoutIndex));
            newPath.append(
                    getWithoutNamespace(translated) +
                    getIndexString(pathComponent) +
                    (count > 1 && i < count - 1
                     ? DELIMITER_PATH
                     : "")); // NOI18N
        }

        return newPath.toString();
    }

    private static List<String> getPathComponents(String path) {
        List<String> components = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(path, DELIMITER_PATH);
        while (tokenizer.hasMoreTokens()) {
            components.add(tokenizer.nextToken());
        }
        return components;
    }

    private static String getWithoutIndex(String string) {
        if (hasIndex(string)) {
            int startIndex = string.lastIndexOf("["); // NOI18N
            return string.substring(0, startIndex);
        }
        return string;
    }

    private static String getIndexString(String string) {
        if (hasIndex(string)) {
            int startIndex = string.lastIndexOf("["); // NOI18N
            return string.substring(startIndex);
        }
        return ""; // NOI18N
    }

    private static boolean hasIndex(String string) {
        return string.matches("..*\\[[0-9]+\\]$"); // NOI18N
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
            return XmpMetadata.isKnownNamespace(string.substring(0, indexDelim));
        }
        return false;
    }

    private static boolean isExifNamespace(String string) {
        return string.startsWith("exif:"); // NOI18N
    }
}
