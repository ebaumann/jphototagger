package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.persistence.PersistentSettings;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;

/**
 * Property filenames of the app skin.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/10
 */
public class ComboBoxModelAppLookAndFeel extends DefaultComboBoxModel {

    private static final HashMap<String, String> propertyFilenameOfSkin = new HashMap<String, String>();
    private static final String standardLookAndFeel = Bundle.getString("ComboBoxModelAppLookAndFeel.KeynameStandard");
    public static final String keySelectedIndex = ComboBoxModelAppLookAndFeel.class.getName() + ".SelectedIndex"; // NOI18N
    

    static {
        propertyFilenameOfSkin.put(
            standardLookAndFeel,
            null); // NOI18N
        propertyFilenameOfSkin.put(
            Bundle.getString("ComboBoxModelAppLookAndFeel.KeynameDark"),
            "de/elmar_baumann/imv/resource/DarkSkin.properties"); // NOI18N
    }

    public ComboBoxModelAppLookAndFeel() {
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
            filename = propertyFilenameOfSkin.get((String) selected);
        }
        return filename;
    }

    private void addElements() {
        for (String key : propertyFilenameOfSkin.keySet()) {
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
                Logger.getLogger(ComboBoxModelAppLookAndFeel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
