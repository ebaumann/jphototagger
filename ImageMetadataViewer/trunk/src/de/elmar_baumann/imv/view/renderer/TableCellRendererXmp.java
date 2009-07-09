package de.elmar_baumann.imv.view.renderer;

import com.adobe.xmp.properties.XMPPropertyInfo;
import de.elmar_baumann.imv.app.AppLookAndFeel;
import de.elmar_baumann.imv.database.metadata.selections.XmpInDatabase;
import de.elmar_baumann.imv.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imv.resource.Translation;
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
