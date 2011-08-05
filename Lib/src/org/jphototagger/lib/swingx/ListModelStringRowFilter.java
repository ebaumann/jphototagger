package org.jphototagger.lib.swingx;

import javax.swing.ListModel;
import javax.swing.RowFilter;

/**
 *
 *
 * @author Elmar Baumann
 */
public abstract class ListModelStringRowFilter extends RowFilter<ListModel, Integer> {

    /**
     *
     * @param string string to filter
     */
    abstract RowFilter<ListModel, Integer> createNewInstance(String string);
}
