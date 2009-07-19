package de.elmar_baumann.imv.view.renderer;

import de.elmar_baumann.imv.resource.Bundle;
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

    private static final Map<Class, String> TEXT_OF_CLASS =
            new HashMap<Class, String>();
    private static final String TEXT_UNDEFINED =
            Bundle.getString(
            "ListCellRendererLogfileFormatter.Info.Format.Undefined"); // NOI18N

    static {
        TEXT_OF_CLASS.put(XMLFormatter.class,
                Bundle.getString(
                "ListCellRendererLogfileFormatter.Info.Format.Xml")); // NOI18N
        TEXT_OF_CLASS.put(SimpleFormatter.class,
                Bundle.getString(
                "ListCellRendererLogfileFormatter.Info.Format.Simple")); // NOI18N
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
        String text = TEXT_OF_CLASS.get((Class) value);
        label.setText(text == null
                      ? TEXT_UNDEFINED
                      : text);
        return label;
    }
}
