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

import de.elmar_baumann.jpt.resource.Bundle;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.SimpleFormatter;
import java.util.logging.XMLFormatter;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-17
 */
public final class ListCellRendererLogfileFormatter extends DefaultListCellRenderer {

    private static final Map<Class, String> TEXT_OF_CLASS  = new HashMap<Class, String>();
    private static final String             TEXT_UNDEFINED = Bundle.getString("ListCellRendererLogfileFormatter.Info.Format.Undefined");

    static {
        TEXT_OF_CLASS.put(XMLFormatter.class   , Bundle.getString("ListCellRendererLogfileFormatter.Info.Format.Xml"));
        TEXT_OF_CLASS.put(SimpleFormatter.class, Bundle.getString("ListCellRendererLogfileFormatter.Info.Format.Simple"));
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        String text  = TEXT_OF_CLASS.get((Class) value);

        label.setText(text == null ? TEXT_UNDEFINED : text);
        return label;
    }
}
