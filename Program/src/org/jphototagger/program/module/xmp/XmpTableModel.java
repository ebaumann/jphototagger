package org.jphototagger.program.module.xmp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.adobe.xmp.properties.XMPPropertyInfo;

import org.jphototagger.lib.swing.TableModelExt;
import org.jphototagger.lib.util.Bundle;

/**
 * Alle elements are {@code XMPPropertyInfo} instances retrieved through
 * {@code #setPropertyInfosOfFile(File, List)}.
 *
 * @author Elmar Baumann, Tobias Stening
 */
public final class XmpTableModel extends TableModelExt {

    private static final long serialVersionUID = 1L;
    private File file;
    private List<XMPPropertyInfo> propertyInfos;

    public XmpTableModel() {
        setRowHeaders();
    }

    /**
     * Setzt die Property-Infos, deren Daten übernommen werden.
     *
     * @param file           Datei, aus der die Property-Infos
     *                       ermittelt wurden oder null, falls diese
     *                       Information unwichtig ist
     * @param propertyInfos  Property-Infos
     */
    public void setPropertyInfosOfFile(File file, List<XMPPropertyInfo> propertyInfos) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        if (propertyInfos == null) {
            throw new NullPointerException("propertyInfos == null");
        }

        this.file = file;
        this.propertyInfos = new ArrayList<XMPPropertyInfo>(propertyInfos);
        removeAllRows();
        addRows();
    }

    /**
     * Liefert die Datei, deren Property-Infos das Model enthält.
     *
     * @return Dateiname oder null, wenn die Property-Infos entfernt wurden
     *         oder null gesetzt wurde mit
     *         {@code #setPropertyInfosOfFile(File, List) }
     */
    public File getFile() {
        return file;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    private void addRows() {
        if (propertyInfos != null) {
            for (XMPPropertyInfo xmpPropertyInfo : propertyInfos) {
                addRow(xmpPropertyInfo);
            }
        }
    }

    private void addRow(XMPPropertyInfo xmpPropertyInfo) {
        String path = xmpPropertyInfo.getPath();
        Object value = xmpPropertyInfo.getValue();

        if ((path != null) && (value != null) && !path.contains("Digest")) {
            List<XMPPropertyInfo> newRow = new ArrayList<XMPPropertyInfo>();

            newRow.add(xmpPropertyInfo);
            newRow.add(xmpPropertyInfo);
            super.addRow(newRow.toArray(new XMPPropertyInfo[newRow.size()]));
        }
    }

    private void setRowHeaders() {
        addColumn(Bundle.getString(XmpTableModel.class, "XmpTableModel.HeaderColumn.1"));
        addColumn(Bundle.getString(XmpTableModel.class, "XmpTableModel.HeaderColumn.2"));
    }
}
