/*
 * @(#)TableModelXmp.java    Created on 2008-10-05
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.model;

import com.adobe.xmp.properties.XMPPropertyInfo;

import org.jphototagger.program.resource.JptBundle;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

/**
 * Alle elements are {@code XMPPropertyInfo} instances retrieved through
 * {@link #setPropertyInfosOfFile(File, List)}.
 *
 * @author Elmar Baumann, Tobias Stening
 */
public final class TableModelXmp extends DefaultTableModel {
    private static final long     serialVersionUID = -647814140321831383L;
    private File                  file;
    private List<XMPPropertyInfo> propertyInfos;

    public TableModelXmp() {
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
    public void setPropertyInfosOfFile(File file,
                                       List<XMPPropertyInfo> propertyInfos) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        if (propertyInfos == null) {
            throw new NullPointerException("propertyInfos == null");
        }

        this.file          = file;
        this.propertyInfos = new ArrayList<XMPPropertyInfo>(propertyInfos);
        getDataVector().clear();
        addRows();
    }

    /**
     * Liefert die Datei, deren Property-Infos das Model enthält.
     *
     * @return Dateiname oder null, wenn die Property-Infos entfernt wurden
     *         oder null gesetzt wurde mit
     *         {@link #setPropertyInfosOfFile(File, List) }
     */
    public File getFile() {
        return file;
    }

    /**
     * Entfernt alle XMP-Daten.
     */
    public void removeAllRows() {
        getDataVector().clear();
        file = null;
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
        String path  = xmpPropertyInfo.getPath();
        Object value = xmpPropertyInfo.getValue();

        if ((path != null) && (value != null) &&!path.contains("Digest")) {
            List<XMPPropertyInfo> newRow = new ArrayList<XMPPropertyInfo>();

            newRow.add(xmpPropertyInfo);
            newRow.add(xmpPropertyInfo);
            super.addRow(newRow.toArray(new XMPPropertyInfo[newRow.size()]));
        }
    }

    private void setRowHeaders() {
        addColumn(JptBundle.INSTANCE.getString("TableModelXmp.HeaderColumn.1"));
        addColumn(JptBundle.INSTANCE.getString("TableModelXmp.HeaderColumn.2"));
    }
}
