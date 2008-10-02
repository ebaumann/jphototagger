package de.elmar_baumann.imagemetadataviewer.view.renderer;

import de.elmar_baumann.imagemetadataviewer.resource.Bundle;
import java.awt.Component;
import java.util.HashMap;
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
public class ListCellRendererLogfileFormatter extends DefaultListCellRenderer {

    private static HashMap<Class, String> textOfClass = new HashMap<Class, String>();
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
