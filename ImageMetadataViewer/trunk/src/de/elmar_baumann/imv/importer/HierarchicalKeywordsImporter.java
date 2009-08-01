package de.elmar_baumann.imv.importer;

import java.io.File;
import java.util.Collection;
import java.util.IllegalFormatException;
import java.util.List;
import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;

/**
 * Imports hierarchical keywords.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-01
 */
public interface HierarchicalKeywordsImporter {

    /**
     * Returns all keyword paths.
     *
     * Every path is a list. The first string in a path - the first list element
     * - is the root keyword (no parent) and the following keywords - string
     * elements in the list - are children where a string following a string is
     * a child of the previous string.
     * 
     * @param  file file with keywords to import
     * @return      keyword paths
     * @throws      IllegalFormatException if the file format is invalid
     */
    public Collection<List<String>> getPaths(File file);

    /**
     * Returns the filter of files that can be imported.
     *
     * @return file filter
     */
    public FileFilter getFileFilter();

    /**
     * Returns a description of this importer.
     *
     * @return description, e.g.
     *         <code>"Adobe Photoshop Lightroom exported keywords"</code>
     */
    public String getDescription();

    /**
     * Returns an icon representation of this importer.
     *
     * @return icon or null
     */
    public Icon getIcon();
}
