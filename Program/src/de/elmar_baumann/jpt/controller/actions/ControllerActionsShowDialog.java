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
package de.elmar_baumann.jpt.controller.actions;

import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.dialogs.ActionsDialog;
import de.elmar_baumann.jpt.view.frames.AppFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Displays the dialog {@link de.elmar_baumann.jpt.view.dialogs.ActionsDialog}
 * when the menu item {@link AppFrame#getMenuItemActions()} was klicked or the
 * accelerator keys (F4) were pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-11-06
 */
public final class ControllerActionsShowDialog implements ActionListener {

    public ControllerActionsShowDialog() {
        listen();
    }

    private void listen() {
        GUI.INSTANCE.getAppFrame().getMenuItemActions().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ActionsDialog dialog = ActionsDialog.INSTANCE;

        if (!dialog.isVisible()) {
            dialog.setVisible(true);
        }

        dialog.toFront();
    }
}
