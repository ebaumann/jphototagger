package org.jphototagger.program.view.renderer;

import com.adobe.xmp.properties.XMPPropertyInfo;
import javax.swing.table.TableModel;
import org.jphototagger.lib.componentutil.TableUtil;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.database.metadata.selections.XmpInDatabase;
import org.jphototagger.program.image.metadata.xmp.XmpMetadata;
import org.jphototagger.program.resource.Translation;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableStringConverter;
import org.jphototagger.lib.util.StringUtil;
import org.jphototagger.program.resource.JptBundle;

/**
 * Rendert Tabellen mit
 * {@link com.adobe.xmp.properties.XMPPropertyInfo}-Spaltenobjekten.
 *
 * @author Elmar Baumann
 */
public final class TableCellRendererXmp extends FormatterLabelMetadata implements TableCellRenderer {
    private static final String DELIMITER_PATH = "/";
    private static final String DELIMITER_NAMESPACE = ":";
    private static final Translation TRANSLATION_XMP = new Translation("XmpPropertyTranslations");
    private static final Translation TRANSLATION_XMP_EXIF_TAG_ID = new Translation("XmpPropertyExifTagIdTranslations");
    private static final Translation TRANSLATION_EXIF = new Translation("ExifTagIdTagNameTranslations");

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel cellLabel = new JLabel();
        XMPPropertyInfo xmpPropertyInfo = (XMPPropertyInfo) value;

        setDefaultCellColors(cellLabel, isSelected);
        setIsStoredInDatabaseColor(cellLabel, xmpPropertyInfo, isSelected);

        if (column == 0) {
            setHeaderFont(cellLabel);

            String xmpPath = xmpPropertyInfo.getPath();

            TableUtil.embedTableCellTextInHtml(table, row, cellLabel, translate(xmpPath, xmpPath),
                                               AppLookAndFeel.TABLE_MAX_CHARS_ROW_HEADER,
                                               AppLookAndFeel.TABLE_ROW_HEADER_CSS);
        } else {
            setContentFont(cellLabel);
            TableUtil.embedTableCellTextInHtml(table, row, cellLabel, xmpPropertyInfo.getValue().toString(),
                                               AppLookAndFeel.TABLE_MAX_CHARS_CELL, AppLookAndFeel.TABLE_CELL_CSS);
        }

        return cellLabel;
    }

    private void setIsStoredInDatabaseColor(JLabel cellLabel, XMPPropertyInfo xmpPropertyInfo, boolean isSel) {
        if (XmpInDatabase.isInDatabase(xmpPropertyInfo.getPath())) {
            setIsStoredInDatabaseColors(cellLabel, isSel);
            cellLabel.setToolTipText(JptBundle.INSTANCE.getString("TableCellRendererXmp.ToolTipText.CellLabelStoredInDatabase"));
        }
    }

    private static String translate(String path, String alternate) {
        StringBuilder newPath = new StringBuilder();
        List<String> pathComponents = getPathComponents(path);
        int pathComponentsCount = pathComponents.size();

        for (int i = 0; i < pathComponentsCount; i++) {
            String pathComponent = pathComponents.get(i);
            String withoutIndex = getWithoutIndex(pathComponent);
            String translated = (isExifNamespace(pathComponent)
                                 ? TRANSLATION_EXIF.translate(TRANSLATION_XMP_EXIF_TAG_ID.translate(withoutIndex,
                                     alternate))
                                 : TRANSLATION_XMP.translate(withoutIndex, alternate));

            newPath.append(getWithoutNamespace(translated)).append(getIndexString(pathComponent)).append(
                ((pathComponentsCount > 1) && (i < pathComponentsCount - 1))
                ? DELIMITER_PATH
                : "");
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
            return XmpMetadata.isKnownNamespace(string.substring(0, indexDelim));
        }

        return false;
    }

    private static boolean isExifNamespace(String string) {
        return string.startsWith("exif:");
    }

    public static Comparator<?> createColumn0Comparator() {
        return new Column0Comparator();
    }

    public static Comparator<?> createColumn1Comparator() {
        return new Column1Comparator();
    }

    private static class Column0Comparator implements Comparator<Object> {

        @Override
        public int compare(Object o1, Object o2) {
            if (o1 instanceof XMPPropertyInfo && o2 instanceof XMPPropertyInfo) {
                XMPPropertyInfo xmpPropertyInfo1 = (XMPPropertyInfo) o1;
                XMPPropertyInfo xmpPropertyInfo2 = (XMPPropertyInfo) o2;
                String xmpPath1 = xmpPropertyInfo1.getPath();
                String xmpPath2 = xmpPropertyInfo2.getPath();
                String o1String = translate(xmpPath1, xmpPath1);
                String o2String = translate(xmpPath2, xmpPath2);

                return o1String.compareToIgnoreCase(o2String);
            } else {
                return 0;
            }
        }
    }

    private static class Column1Comparator implements Comparator<Object> {

        @Override
        public int compare(Object o1, Object o2) {
            if (o1 instanceof XMPPropertyInfo && o2 instanceof XMPPropertyInfo) {
                XMPPropertyInfo xmpPropertyInfo1 = (XMPPropertyInfo) o1;
                XMPPropertyInfo xmpPropertyInfo2 = (XMPPropertyInfo) o2;
                String o1String = xmpPropertyInfo1.getValue().toString();
                String o2String = xmpPropertyInfo2.getValue().toString();

                return o1String.compareToIgnoreCase(o2String);
            } else {
                return 0;
            }
        }
    }

    public static TableStringConverter createTableStringConverter() {
        return new XmpTableStringConverter();
    }

    private static class XmpTableStringConverter extends TableStringConverter {

        @Override
        public String toString(TableModel model, int row, int column) {
            Object value = model.getValueAt(row, column);

            if (value instanceof XMPPropertyInfo) {
                XMPPropertyInfo xmpPropertyInfo = (XMPPropertyInfo) value;
                String xmpPath = xmpPropertyInfo.getPath();

                return column == 0
                        ? translate(xmpPath, xmpPath)
                        : xmpPropertyInfo.getValue().toString();
            } else {
                return StringUtil.toStringNullToEmptyString(value);
            }
        }
    }
}
