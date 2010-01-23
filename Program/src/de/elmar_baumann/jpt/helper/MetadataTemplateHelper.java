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
package de.elmar_baumann.jpt.helper;

import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.database.DatabaseMetadataTemplates;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.lib.dialog.InputDialog;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-08
 */
public final class MetadataTemplateHelper {

    /**
     * Returns a not existing template name.
     *
     * @param  oldName old name or null. If not null, the new name has to be
     *                 different from <code>oldName</code>.
     * @return         name or null
     */
    public static String getNewTemplateName(String oldName) {
        InputDialog dlg = new InputDialog();

        dlg.setInfo(Bundle.getString("MetadataTemplateHelper.Info.InputName"));
        if (oldName != null) dlg.setInput(oldName);

        while (true) {
            dlg.setVisible(true);
            dlg.toFront();
            if (!dlg.isAccepted()) return null;
            String name = dlg.getInput();
            if (name == null || name.trim().length() == 0) return null;
            boolean namesEqual = oldName != null && name.equalsIgnoreCase(oldName);
            if (namesEqual) {
                if (!MessageDisplayer.confirmYesNo(null, "MetadataTemplateHelper.Error.NamEquals")) {
                    return null;
                }
            }
            if (!namesEqual &&
                DatabaseMetadataTemplates.INSTANCE.exists(name)) {
                    if (!MessageDisplayer.confirmYesNo(null, "MetadataTemplateHelper.Error.NameExists", name)) {
                        return null;
                    }
            } else {
                return name;
            }
        }
    }

    private MetadataTemplateHelper() {
    }
}
