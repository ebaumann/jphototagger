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
 * @version 2008/09/17
 */
public final class ListCellRendererLogfileFormatter extends DefaultListCellRenderer {

    private static final Map<Class, String> textOfClass = new HashMap<Class, String>();
    private static final String undefined = Bundle.getString("ListCellRendererLogfileFormatter.InformationMessage.Format.Undefined");

    static {
        textOfClass.put(XMLFormatter.class, Bundle.getString("ListCellRendererLogfileFormatter.InformationMessage.Format.Xml"));
        textOfClass.put(SimpleFormatter.class, Bundle.getString("ListCellRendererLogfileFormatter.InformationMessage.Format.Simple"));
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value,
            index, isSelected, cellHasFocus);
        String text = textOfClass.get((Class) value);
        label.setText(text == null ? undefined : text);
        return label;
    }
}
