package de.elmar_baumann.imv.controller.filesystem;

import de.elmar_baumann.imv.resource.Bundle;

/**
 * Filename format that returns exactly a set string (formats nothing).
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/13
 */
public class StringFilenameFormat implements FilenameFormat {

    private String string;

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    @Override
    public String format() {
        return string;
    }
    
    @Override
    public String toString() {
        return Bundle.getString("StringFilenameFormat.String");
    }
}
