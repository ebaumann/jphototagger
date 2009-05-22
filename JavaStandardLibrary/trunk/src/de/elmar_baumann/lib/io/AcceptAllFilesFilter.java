package de.elmar_baumann.lib.io;

import de.elmar_baumann.lib.resource.Bundle;
import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * Accepts all Files, rejects directories.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/05/22
 */
public final class AcceptAllFilesFilter extends FileFilter {

    @Override
    public boolean accept(File pathname) {
        return pathname.isFile() ? true : false;
    }

    @Override
    public String getDescription() {
        return Bundle.getString("AcceptAllFilesFilter.Description");
    }

}
