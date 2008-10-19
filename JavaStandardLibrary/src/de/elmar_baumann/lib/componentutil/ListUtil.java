package de.elmar_baumann.lib.componentutil;

import java.util.List;
import javax.swing.JList;

/**
 * Utils for {@link javax.swing.JList}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/19
 */
public class ListUtil {

    /**
     * Clears alls selected items in all lists.
     * 
     * @param lists  lists
     */
    public static void clearSelection(List<JList> lists) {
        for (JList list : lists) {
            if (!list.isSelectionEmpty()) {
                list.clearSelection();
            }
        }
    }

}
