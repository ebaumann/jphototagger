package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.persistence.PersistentSettings;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;

/**
 * Property filenames of the app skin.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/10
 */
public class ComboBoxModelAppColors extends DefaultComboBoxModel {

    private static final Map<String, String> propertyFilenameOf = new HashMap<String, String>();
    private static final String standardLookAndFeel = Bundle.getString("ComboBoxModelAppColors.NameStandard");
    public static final String keySelectedIndex = ComboBoxModelAppColors.class.getName() + ".SelectedIndex"; // NOI18N
    

    static {
        propertyFilenameOf.put(
            standardLookAndFeel,
            null); // NOI18N
        propertyFilenameOf.put(
            Bundle.getString("ComboBoxModelAppColors.NameDark"),
            "de/elmar_baumann/imv/resource/DarkAppColors.properties"); // NOI18N
    }

    public ComboBoxModelAppColors() {
        addElements();
    }

    /**
     * Returns wheter to apply the standard skin.
     * 
     * @return true, if standard skin should be applied
     */
    public boolean isSystemLookAndFeel() {
        return getSelectedItem() == null || getSelectedItem().equals(standardLookAndFeel);
    }

    /**
     * Returns the name of the properties file with the skin description.
     * 
     * @return filename or null if the standard skin should be applied
     */
    public String getLookAndFeelPropertiesFilename() {
        Object selected = getSelectedItem();
        String filename = null;
        if (selected != null) {
            filename = propertyFilenameOf.get((String) selected);
        }
        return filename;
    }

    private void addElements() {
        for (String key : propertyFilenameOf.keySet()) {
            addElement(key);
        }
        selectElement();
    }

    private void selectElement() {
        String index = PersistentSettings.getInstance().getString(keySelectedIndex);
        if (!index.isEmpty()) {
            try {
                setSelectedItem(getElementAt(Integer.parseInt(index)));
            } catch (NumberFormatException ex) {
                Logger.getLogger(ComboBoxModelAppColors.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
