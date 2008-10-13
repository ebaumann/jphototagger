package de.elmar_baumann.imv.controller.filesystem;

import de.elmar_baumann.imv.resource.Bundle;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/13
 */
public class EmptyFilenameFormat implements FilenameFormat {

    @Override
    public String format() {
        return ""; // NOI18N
    }

    @Override
    public String toString() {
        return Bundle.getString("EmptyFilenameFormat.String");
    }
}
