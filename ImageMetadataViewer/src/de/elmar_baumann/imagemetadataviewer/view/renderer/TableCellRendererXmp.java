package de.elmar_baumann.imagemetadataviewer.view.renderer;

import com.adobe.xmp.properties.XMPPropertyInfo;
import de.elmar_baumann.imagemetadataviewer.database.metadata.selections.XmpInDatabase;
import de.elmar_baumann.imagemetadataviewer.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imagemetadataviewer.resource.Translation;
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
public class TableCellRendererXmp extends TableCellRendererMetadata
    implements TableCellRenderer {

    private static final String pathDelimiter = "/"; // NOI18N
    private static final String namespaceDelimiter = ":"; // NOI18N
    private static final Translation xmpTranslation = new Translation("XmpPropertyTranslations"); // NOI18N
    private static final Translation xmpExifTagIdTranslation = new Translation("XmpPropertyExifTagIdTranslations"); // NOI18N
    private static final Translation exifTranslation = new Translation("ExifTagIdTagNameTranslations"); // NOI18N
    private static final XmpInDatabase xmpInDatabase = XmpInDatabase.getInstance();

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {

        JLabel cellLabel = new JLabel();
        XMPPropertyInfo xmpPropertyInfo = (XMPPropertyInfo) value;

        setDefaultCellColors(cellLabel, isSelected);
        setIsStoredInDatabaseColor(cellLabel, xmpPropertyInfo);

        if (column == 0) {
            setHeaderFont(cellLabel);
            cellLabel.setText(translate(xmpPropertyInfo.getPath()));
        } else {
            assert column < 2 : column;
            setContentFont(cellLabel);
            cellLabel.setText(paddingLeft + xmpPropertyInfo.getValue().toString());
        }
        return cellLabel;
    }

    private void setIsStoredInDatabaseColor(JLabel cellLabel, XMPPropertyInfo xmpPropertyInfo) {
        if (xmpInDatabase.isInDatabase(xmpPropertyInfo.getPath())) {
            setIsStoredInDatabaseColors(cellLabel);
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
                ? exifTranslation.translate(xmpExifTagIdTranslation.translate(withoutIndex))
                : xmpTranslation.translate(withoutIndex));
            newPath.append(
                getWithoutNamespace(translated) +
                getIndexString(pathComponent) +
                (count > 1 && i < count - 1 ? pathDelimiter : "")); // NOI18N
        }

        return newPath.toString();
    }

    private static List<String> getPathComponents(String path) {
        List<String> components = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(path, pathDelimiter);
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
            int indexDelim = string.indexOf(namespaceDelimiter);
            return string.substring(indexDelim + 1);
        }
        return string;
    }

    private static boolean hasNamespace(String string) {
        int indexDelim = string.indexOf(namespaceDelimiter);
        if (indexDelim > 0) {
            return XmpMetadata.isKnownNamespace(string.substring(0, indexDelim));
        }
        return false;
    }

    private static boolean isExifNamespace(String string) {
        return string.startsWith("exif:"); // NOI18N
    }
}
