package org.jphototagger.program.model;

import javax.swing.DefaultListModel;
import org.jphototagger.lib.util.Bundle;

/**
 * Contains exactly one list element with a "wait" text and is a substitute as
 * long as a large list model will be created.
 *
 * @author Elmar Baumann
 */
public final class ListModelWait extends DefaultListModel {
    private static final long serialVersionUID = 1363478529337093293L;
    public static final ListModelWait INSTANCE = new ListModelWait();

    public ListModelWait() {
        addElement(Bundle.getString(ListModelWait.class, "ListModelWait.ItemText"));
    }
}
