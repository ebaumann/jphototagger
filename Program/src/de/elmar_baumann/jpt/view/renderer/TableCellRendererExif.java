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

import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.database.metadata.selections.ExifInDatabase;
import de.elmar_baumann.jpt.image.metadata.exif.ExifFieldValueFormatter;
import de.elmar_baumann.jpt.image.metadata.exif.entry.ExifGpsMetadata;
import de.elmar_baumann.jpt.image.metadata.exif.IfdEntryProxy;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.resource.Translation;
import de.elmar_baumann.lib.componentutil.TableUtil;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-14
 */
public final class TableCellRendererExif extends FormatterLabelMetadata
        implements TableCellRenderer {

    private static final Translation TRANSLATION =
            new Translation("ExifTagIdTagNameTranslations");

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        assert column < 2 : column;

        JLabel cellLabel = new JLabel();
        setDefaultCellColors(cellLabel, isSelected);

        if (column == 0) {
            setHeaderFont(cellLabel);
        } else {
            setContentFont(cellLabel);
        }

        if (value instanceof IfdEntryProxy) {
            IfdEntryProxy ifdEntry = (IfdEntryProxy) value;

            setIsStoredInDatabaseColor(cellLabel, ifdEntry, isSelected);

            if (column == 0) {
                String translated = TRANSLATION.translate(
                        Integer.toString(ifdEntry.tagId()),
                        ifdEntry.name());
                TableUtil.embedTableCellTextInHtml(table, row, cellLabel,
                        translated.trim(),
                        AppLookAndFeel.TABLE_MAX_CHARS_ROW_HEADER,
                        AppLookAndFeel.TABLE_CSS_ROW_HEADER);
            } else {
                TableUtil.embedTableCellTextInHtml(table, row, cellLabel,
                        ExifFieldValueFormatter.format(ifdEntry),
                        AppLookAndFeel.TABLE_MAX_CHARS_CELL,
                        AppLookAndFeel.TABLE_CSS_CELL);
            }
        } else if (value instanceof ExifGpsMetadata) {
            if (column == 0) {
                cellLabel.setText(Bundle.getString("TableCellRendererExif.Column.ShowLocationIn"));
            }
        } else if (value instanceof Component) {
            return (Component) value;
        } else {
            TableUtil.embedTableCellTextInHtml(table, row, cellLabel,
                    value.toString(),
                    AppLookAndFeel.TABLE_MAX_CHARS_CELL,
                    AppLookAndFeel.TABLE_CSS_CELL);
        }
        return cellLabel;
    }

    private void setIsStoredInDatabaseColor(
            JLabel cellLabel, IfdEntryProxy ifdEntry, boolean isSelected) {
        if (ExifInDatabase.isInDatabase(ifdEntry.tagId())) {
            setIsStoredInDatabaseColors(cellLabel, isSelected);
        }
    }
}
