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
package de.elmar_baumann.imv.controller.hierarchicalkeywords;

import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.dialogs.InputHelperDialog;
import de.elmar_baumann.imv.view.frames.AppFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Listens to the menu item {@link AppFrame#getMenuItemInputHelper()}
 * and shows the {@link InputHelperDialog} on action performed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-30
 */
public final class ControllerShowHierarchicalKeywordsDialog
        implements ActionListener {

    public ControllerShowHierarchicalKeywordsDialog() {
        listen();
    }

    private void listen() {
        GUI.INSTANCE.getAppFrame().getMenuItemInputHelper().
                addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        showDialog();
    }

    private void showDialog() {
        InputHelperDialog dlg = InputHelperDialog.INSTANCE;
        if (dlg.isVisible()) {
            dlg.toFront();
        } else {
            dlg.setVisible(true);
        }
    }
}
