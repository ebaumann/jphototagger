package org.jphototagger.program.app.ui;

import javax.swing.DefaultListModel;

import org.jphototagger.lib.util.Bundle;

/**
 * Contains exactly one list element with a "wait" text and is a substitute as
 * long as a large list model will be created.
 *
 * @author Elmar Baumann
 */
public final class WaitListModel extends DefaultListModel {

    private static final long serialVersionUID = 1363478529337093293L;
    public static final WaitListModel INSTANCE = new WaitListModel();

    public WaitListModel() {
        addElement(Bundle.getString(WaitListModel.class, "WaitListModel.ItemText"));
    }
}
