/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.imv.app;

import de.elmar_baumann.imv.resource.Bundle;

/**
 * Texts used in multiple classes.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-02-19
 */
public final class AppTexts {

    public static final String TOOLTIP_TEXT_PROGRESSBAR_DIRECTORY =
            Bundle.getString("ProgressBarDirectory.TooltipText"); // NOI18N
    public static final String TOOLTIP_TEXT_PROGRESSBAR_CURRENT_TASKS =
            Bundle.getString("ProgressBarCurrentTasks.TooltipText"); // NOI18N
    public static final String TOOLTIP_TEXT_PROGRESSBAR_SCHEDULED_TASKS =
            Bundle.getString("ProgressBarScheduledTasks.TooltipText"); // NOI18N
    public static final String DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_PREV_IMPORT =
            Bundle.getString("DisplayName.ItemImageCollections.LastImport"); // NOI18N
    public static final String DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_PICKED =
            Bundle.getString("DisplayName.ItemImageCollections.Picked"); // NOI18N
    public static final String DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_REJECTED =
            Bundle.getString("DisplayName.ItemImageCollections.Rejected"); // NOI18N

    private AppTexts() {
    }
}
