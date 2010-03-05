/*
 * JPhotoTagger tags and finds images fast.
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.helper;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.data.RenameTemplate;
import de.elmar_baumann.jpt.database.DatabaseRenameTemplates;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.lib.dialog.InputDialog;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-03-01
 */
public final class RenameTemplateHelper {

    /**
     * Inserts a new rename template into the database.
     * 
     * @param  template new template
     */
    public static void insert(RenameTemplate template) {
        assert template.getId() == null;
        String name = getUniqueName(null);
        if (name != null) {
            template.setName(name);
            if (!DatabaseRenameTemplates.INSTANCE.insert(template)) {
                MessageDisplayer.error(null, "RenameTemplateHelper.Error.Insert", template);
            }
        }
    }

    public static void update(RenameTemplate template) {
        assert template.getId() != null : template.getId();
        if (!DatabaseRenameTemplates.INSTANCE.update(template)) {
            MessageDisplayer.error(null, "RenameTemplateHelper.Error.Update", template);
        }
    }

    public static void rename(RenameTemplate template) {
        assert template.getId() != null : template.getId();
        String name = getUniqueName(template.getName());
        if (name != null) {
            template.setName(name);
            update(template);
        }
    }

    public static void delete(RenameTemplate template) {
        assert template.getId() != null : template.getId();
        if (MessageDisplayer.confirmYesNo(null, "RenameTemplateHelper.Confirm.Delete", template)) {
            if (DatabaseRenameTemplates.INSTANCE.delete(template.getName()) != 1) {
                MessageDisplayer.error(null, "RenameTemplateHelper.Error.Delete", template);
            }
        }
    }

    private static String getUniqueName(String suggest) {
        InputDialog dlg = new InputDialog(
                                          JptBundle.INSTANCE.getString("RenameTemplateHelper.Input.Name"),
                                          suggest == null ? "" : suggest,
                                          UserSettings.INSTANCE.getProperties(),
                                          "RenameTemplateHelper.Input.Name");
        dlg.setVisible(true);
        boolean unique = false;
        while (!unique && dlg.isAccepted()) {
            String name = dlg.getInput().trim();
            if (name.isEmpty()) return null;
            unique = !DatabaseRenameTemplates.INSTANCE.exists(name);
            if (!unique &&
                MessageDisplayer.confirmYesNo(null, "RenameTemplateHelper.Confirm.InputUniqueName", name)
                ) {
                dlg.setVisible(true);
            } else if (unique) {
                return name;
            } else {
                return null;
            }
        }
        return null;
    }

    private RenameTemplateHelper() {
    }
}
