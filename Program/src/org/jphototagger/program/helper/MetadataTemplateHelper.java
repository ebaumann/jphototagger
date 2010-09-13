/*
 * @(#)MetadataTemplateHelper.java    Created on 2010-01-08
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.helper;

import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.database.DatabaseMetadataTemplates;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.view.dialogs.InputHelperDialog;
import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.lib.dialog.InputDialog;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class MetadataTemplateHelper {

    /**
     * Returns a not existing template name.
     *
     * @param  fromName old name or null. If not null, the new name has to be
     *                 different from <code>fromName</code>.
     * @return         name or null
     */
    public static String getNewTemplateName(String fromName) {
        InputDialog dlg = new InputDialog(InputHelperDialog.INSTANCE);

        dlg.setInfo(
            JptBundle.INSTANCE.getString(
                "MetadataTemplateHelper.Info.InputName"));

        if (fromName != null) {
            dlg.setInput(fromName);
        }

        while (true) {
            ComponentUtil.show(dlg);

            if (!dlg.isAccepted()) {
                return null;
            }

            String name = dlg.getInput();

            if ((name == null) || (name.trim().length() == 0)) {
                return null;
            }

            boolean namesEqual = (fromName != null)
                                 && name.equalsIgnoreCase(fromName);

            if (namesEqual) {
                if (!MessageDisplayer.confirmYesNo(
                        null, "MetadataTemplateHelper.Error.NamEquals")) {
                    return null;
                }
            }

            if (!namesEqual
                    && DatabaseMetadataTemplates.INSTANCE.exists(name)) {
                if (!MessageDisplayer.confirmYesNo(
                        null, "MetadataTemplateHelper.Error.NameExists",
                        name)) {
                    return null;
                }
            } else {
                return name;
            }
        }
    }

    private MetadataTemplateHelper() {}
}
