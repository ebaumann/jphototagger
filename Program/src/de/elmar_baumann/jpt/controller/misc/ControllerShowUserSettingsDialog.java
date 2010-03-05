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
package de.elmar_baumann.jpt.controller.misc;

import de.elmar_baumann.jpt.view.dialogs.SettingsDialog;
import de.elmar_baumann.lib.componentutil.ComponentUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: Benutzereinstellungen-Dialog anzeigen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-25
 */
public final class ControllerShowUserSettingsDialog implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        showDialog();
    }

    private void showDialog() {
        ComponentUtil.show(SettingsDialog.INSTANCE);
    }
}
