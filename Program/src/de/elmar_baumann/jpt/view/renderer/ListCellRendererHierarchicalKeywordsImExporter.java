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

import de.elmar_baumann.jpt.exporter.HierarchicalKeywordsExporter;
import de.elmar_baumann.jpt.importer.HierarchicalKeywordsImporter;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * Renders lists with {@link HierarchicalKeywordsImporter}s and
 * {@link HierarchicalKeywordsExporter}s.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-11-04
 */
public final class ListCellRendererHierarchicalKeywordsImExporter
        extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
        if (value instanceof HierarchicalKeywordsImporter) {
            HierarchicalKeywordsImporter importer =
                    (HierarchicalKeywordsImporter) value;
            label.setText(importer.getDescription());
            label.setIcon(importer.getIcon());
        } else if (value instanceof HierarchicalKeywordsExporter) {
            HierarchicalKeywordsExporter exporter =
                    (HierarchicalKeywordsExporter) value;
            label.setText(exporter.getDescription());
            label.setIcon(exporter.getIcon());
        }
        return label;
    }
}
