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
import de.elmar_baumann.jpt.image.metadata.iptc.IptcEntry;
import de.elmar_baumann.jpt.resource.Translation;
import de.elmar_baumann.lib.componentutil.TableUtil;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Rendert Tabellen mit
 * {@link com.imagero.reader.iptc.IPTCEntryMeta}-Spaltenobjekten.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-14
 */
public final class TableCellRendererIptc extends FormatterLabelMetadata
        implements TableCellRenderer {

    private static final Translation TRANSLATION = new Translation("IptcRecordDataSetNumberTranslations");

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        JLabel    cellLabel = new JLabel();
        IptcEntry iptcEntry = (IptcEntry) value;

        setDefaultCellColors(cellLabel, isSelected);

        String number = Integer.toString(iptcEntry.getRecordNumber()) + ":" +
                        Integer.toString(iptcEntry.getDataSetNumber());

        if (column == 0) {

            setContentFont(cellLabel);
            TableUtil.embedTableCellTextInHtml(table, row, cellLabel,
                    number,
                    AppLookAndFeel.TABLE_MAX_CHARS_ROW_HEADER,
                    AppLookAndFeel.TABLE_CSS_ROW_HEADER);

        } else if (column == 1) {

            setHeaderFont(cellLabel);
            TableUtil.embedTableCellTextInHtml(table, row, cellLabel,
                    TRANSLATION.translate(number, number),
                    AppLookAndFeel.TABLE_MAX_CHARS_CELL,
                    AppLookAndFeel.TABLE_CSS_CELL);

        } else {

            assert column < 3 : column;
            setContentFont(cellLabel);
            TableUtil.embedTableCellTextInHtml(table, row, cellLabel,
                    iptcEntry.getData(),
                    AppLookAndFeel.TABLE_MAX_CHARS_CELL,
                    AppLookAndFeel.TABLE_CSS_CELL);
        }
        return cellLabel;
    }
}
