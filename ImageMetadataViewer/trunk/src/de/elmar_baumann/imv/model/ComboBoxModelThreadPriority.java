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

    private static final Map<String, Integer> priorityOfString = new HashMap<String, Integer>();
    private static final Map<Integer, String> stringOfPriority = new HashMap<Integer, String>();
    private static final String highPriorityString = Bundle.getString("ComboBoxModelThreadPriority.ThreadPriority.High");
    private static final String mediumPriorityString = Bundle.getString("ComboBoxModelThreadPriority.ThreadPriority.Medium");
    private static final String lowPriorityString = Bundle.getString("ComboBoxModelThreadPriority.ThreadPriority.Low");
    private static final Integer highPriority = new Integer(8);
    private static final Integer mediumPriority = new Integer(5);
    private static final Integer lowPriority = new Integer(1);

    public ComboBoxModelThreadPriority() {
        initMap();
        addContent();
    }

    /**
     * Liefert die Priorität anhand eines Strings.
     * 
     * @param string String
     * @return       Priorität, bei ungültigem String mittlere Priorität
     * @see           #isPriority(String)
     */
    public int getPriorityOf(String string) {
        Integer priority = mediumPriority;
        boolean isPriority = isPriority(string);
        if (isPriority) {
            priority = priorityOfString.get(string);
        }
        return priority.intValue();
    }

    public String getItemOfPriority(int priority) {
        return stringOfPriority.get(priority);
    }

    public Integer getSelectedPriority() {
        return priorityOfString.get(getSelectedItem());
    }

    /**
     * Liefert, ob ein String eine gültige Priorität spezifiziert.
     * 
     * @param string String
     * @return       true, wenn der String eine gültige Priorität spezifiziert
     */
    public boolean isPriority(String string) {
        return priorityOfString.containsKey(string);
    }

    private void addContent() {
        insertElementAt(lowPriorityString, 0);
        insertElementAt(mediumPriorityString, 1);
        insertElementAt(highPriorityString, 2);
        setSelectedItem(mediumPriorityString);
    }

    private void initMap() {
        if (priorityOfString.isEmpty()) {
            priorityOfString.put(lowPriorityString, lowPriority);
            priorityOfString.put(mediumPriorityString, mediumPriority);
            priorityOfString.put(highPriorityString, highPriority);

            for (String string : priorityOfString.keySet()) {
                stringOfPriority.put(priorityOfString.get(string), string);
            }
        }
    }
}
