package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.resource.Bundle;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;

/**
 * Thread-Priorität.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/07/27
 */
public final class ComboBoxModelThreadPriority extends DefaultComboBoxModel {

    private static final Map<String, Integer> PRIORITY_OF_STRING =
            new HashMap<String, Integer>();
    private static final Map<Integer, String> STRING_OF_PRIORITY =
            new HashMap<Integer, String>();
    private static final String PRIORITY_HIGH_STRING =
            Bundle.getString("ComboBoxModelThreadPriority.ThreadPriority.High");
    private static final String PRIORITY_MEDIUM_STRING =
            Bundle.getString("ComboBoxModelThreadPriority.ThreadPriority.Medium");
    private static final String PRIORITY_LOW_STRING =
            Bundle.getString("ComboBoxModelThreadPriority.ThreadPriority.Low");
    private static final Integer PRIORITY_HIGH = new Integer(8);
    private static final Integer PRIORITY_MEDIUM = new Integer(5);
    private static final Integer PRIORITY_LOW = new Integer(1);

    public ComboBoxModelThreadPriority() {
        initMap();
        insertElements();
    }

    /**
     * Liefert die Priorität anhand eines Strings.
     * 
     * @param string String
     * @return       Priorität, bei ungültigem String mittlere Priorität
     * @see           #isPriority(String)
     */
    public int getPriorityOf(String string) {
        Integer priority = PRIORITY_MEDIUM;
        boolean isPriority = isPriority(string);
        if (isPriority) {
            priority = PRIORITY_OF_STRING.get(string);
        }
        return priority.intValue();
    }

    public String getItemOfPriority(int priority) {
        return STRING_OF_PRIORITY.get(priority);
    }

    public Integer getSelectedPriority() {
        return getSelectedItem() == null
               ? null
               : PRIORITY_OF_STRING.get(getSelectedItem().toString());
    }

    /**
     * Liefert, ob ein String eine gültige Priorität spezifiziert.
     * 
     * @param string String
     * @return       true, wenn der String eine gültige Priorität spezifiziert
     */
    public boolean isPriority(String string) {
        return PRIORITY_OF_STRING.containsKey(string);
    }

    private void insertElements() {
        insertElementAt(PRIORITY_LOW_STRING, 0);
        insertElementAt(PRIORITY_MEDIUM_STRING, 1);
        insertElementAt(PRIORITY_HIGH_STRING, 2);
        setSelectedItem(PRIORITY_MEDIUM_STRING);
    }

    private void initMap() {
        if (PRIORITY_OF_STRING.isEmpty()) {
            PRIORITY_OF_STRING.put(PRIORITY_LOW_STRING, PRIORITY_LOW);
            PRIORITY_OF_STRING.put(PRIORITY_MEDIUM_STRING, PRIORITY_MEDIUM);
            PRIORITY_OF_STRING.put(PRIORITY_HIGH_STRING, PRIORITY_HIGH);

            for (String string : PRIORITY_OF_STRING.keySet()) {
                STRING_OF_PRIORITY.put(PRIORITY_OF_STRING.get(string), string);
            }
        }
    }
}
