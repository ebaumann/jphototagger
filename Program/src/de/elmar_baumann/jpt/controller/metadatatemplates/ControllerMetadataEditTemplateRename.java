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
package de.elmar_baumann.jpt.controller.metadatatemplates;

import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.data.MetadataEditTemplate;
import de.elmar_baumann.jpt.database.DatabaseMetadataEditTemplates;
import de.elmar_baumann.jpt.helper.MetadataEditTemplateHelper;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuMetadataEditTemplates;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-08
 */
public final class ControllerMetadataEditTemplateRename extends ControllerMetadataEditTemplate {

    @Override
    protected boolean myKey(KeyEvent evt) {
        return evt.getKeyCode() == KeyEvent.VK_F2;
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        return evt.getSource() == PopupMenuMetadataEditTemplates.INSTANCE.getItemRename();
    }

    @Override
    protected void action(MetadataEditTemplate template) {
        String oldName = template.getName();
        String newName = MetadataEditTemplateHelper.getNewTemplateName(oldName);
        if (newName != null) {
            if (!DatabaseMetadataEditTemplates.INSTANCE.updateRenameMetadataEditTemplate(oldName, newName)) {
                MessageDisplayer.error(null, "ControllerMetadataEditTemplateRename.Error", oldName);
            }
        }
        focusList();
    }
}
