package de.elmar_baumann.imv.app;

import de.elmar_baumann.imv.resource.Bundle;

/**
 * Texts used in multiple classes.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/02/19
 */
public final class AppTexts {

    public static final String TOOLTIP_TEXT_PROGRESSBAR_DIRECTORY = Bundle.
            getString("ProgressBarDirectory.TooltipText");
    public static final String TOOLTIP_TEXT_PROGRESSBAR_CURRENT_TASKS = Bundle.
            getString("ProgressBarCurrentTasks.TooltipText");
    public static final String TOOLTIP_TEXT_PROGRESSBAR_SCHEDULED_TASKS = Bundle.
            getString("ProgressBarScheduledTasks.TooltipText");
    public static final String DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_PREV_IMPORT =
            Bundle.getString("DisplayName.ItemImageCollections.LastImport");

    private AppTexts() {
    }
}
