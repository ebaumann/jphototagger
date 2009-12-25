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
package de.elmar_baumann.jpt.app.update.tables;

import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.lib.componentutil.ComponentUtil;
import de.elmar_baumann.lib.dialog.ProgressDialog;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-29
 */
class UpdateTablesMessages {

    private ProgressDialog dialog;
    static final UpdateTablesMessages INSTANCE = new UpdateTablesMessages();

    ProgressDialog getProgressDialog() {
        return dialog;
    }

    void message(String text) {
        dialog.setInfoText(text);
        if (dialog.isVisible()) {
            dialog.toFront();
        } else {
            dialog.setVisible(true);
        }
        ComponentUtil.centerScreen(dialog);
    }

    private UpdateTablesMessages() {
        initDialog();
    }

    private void initDialog() {
        dialog = new ProgressDialog(null);
        dialog.setEnabledClose(false);
        dialog.setEnabledStop(false);
        dialog.setTitle(
                Bundle.getString("UpdateTablesMessages.Title"));
        dialog.setIndeterminate(true);
    }
}
