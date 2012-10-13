package org.jphototagger.domain.metadata;

import javax.swing.JPanel;
import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.domain.templates.MetadataTemplate;

/**
 * @author Elmar Baumann
 */
public interface SelectedFilesMetaDataEditor {

    boolean isEditable();

    /**
     * Replaces text of a single metadata value, adds text to repeatable metadata values such as keywords.
     *
     * @param metaDataValue
     * @param text
     */
    void setOrAddText(MetaDataValue metaDataValue, String text);

    void removeText(MetaDataValue metaDataValue, String text);

    void setRating(Long rating);

    /**
     * Adds to repeatable values and replaces not repeatable values.
     *
     * @param xmp
     */
    void setXmp(Xmp xmp);

    void saveIfDirtyAndInputIsSaveEarly();

    void setFocusToLastFocussedEditControl();

    void setMetadataTemplate(MetadataTemplate template);

    /**
     * @param metaDataValue
     * @return panel or null if for that metadata value an edit panel doesn't exist
     */
    JPanel getEditPanelForMetaDataValue(MetaDataValue metaDataValue);

    Xmp createXmpFromInput();
}
