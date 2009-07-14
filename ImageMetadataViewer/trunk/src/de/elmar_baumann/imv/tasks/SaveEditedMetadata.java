package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.data.TextEntry;
import de.elmar_baumann.imv.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.view.panels.EditMetadataPanelsArray;
import java.util.EnumSet;
import java.util.List;

/**
 * Saves Metadata input into an {@link EditMetadataPanelsArray}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008/10/05
 */
public final class SaveEditedMetadata {

    public static void saveMetadata(final EditMetadataPanelsArray editPanels) {
        final List<TextEntry> entries = editPanels.getTextEntries(true);
        final List<String> filenames = editPanels.getFilenames();
        final int filenameCount = filenames.size();
        XmpUpdaterFromTextEntryArray updater =
                new XmpUpdaterFromTextEntryArray();
        if (filenameCount == 1) {
            updater.add(filenames, entries,
                    EnumSet.of(
                    XmpMetadata.UpdateOption.DELETE_IF_SOURCE_VALUE_IS_EMPTY));
        } else if (filenameCount > 1) {
            updater.add(filenames, entries,
                    EnumSet.of(
                    XmpMetadata.UpdateOption.APPEND_TO_REPEATABLE_VALUES));
        } else {
            AppLog.logWarning(SaveEditedMetadata.class,
                    Bundle.getString(
                    "ControllerSaveMetadata.Error.NoImageFilesSelected")); // NOI18N
        }
        editPanels.setDirty(false);
        editPanels.setFocusToLastFocussedComponent();
    }

    private SaveEditedMetadata() {
    }
}
