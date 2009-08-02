package de.elmar_baumann.imv.exporter;

import java.io.File;
import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;

/**
 * Exports hierarchical keywords.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-02
 */
public interface HierarchicalKeywordsExporter {

    public void export(File file);

    /**
     * Returns the filter of files that can be exported.
     *
     * @return file filter
     */
    public FileFilter getFileFilter();

    /**
     * Returns a description of this exporter.
     *
     * @return description, e.g.
     *         <code>"Adobe Photoshop Lightroom keywords"</code>
     */
    public String getDescription();

    /**
     * Returns an icon representation of this exporter.
     *
     * @return icon or null
     */
    public Icon getIcon();
}
