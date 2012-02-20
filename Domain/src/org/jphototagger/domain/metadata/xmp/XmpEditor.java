package org.jphototagger.domain.metadata.xmp;

/**
 * @author Elmar Baumann
 */
public interface XmpEditor {

    /**
     * @return null if user declines edits
     */
    Xmp createXmp();

    /**
     * @param xmp 
     * @return true if user accepts edits
     */
    boolean editXmp(Xmp xmp);
}
