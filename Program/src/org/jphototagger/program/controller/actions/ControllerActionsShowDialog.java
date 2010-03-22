/*
 * @(#)ControllerActionsShowDialog.java    Created on 2008-11-06
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.controller.actions;

import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.ActionsDialog;
import org.jphototagger.program.view.frames.AppFrame;
import org.jphototagger.lib.componentutil.ComponentUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Displays the dialog {@link org.jphototagger.program.view.dialogs.ActionsDialog}
 * when the menu item {@link AppFrame#getMenuItemActions()} was klicked or the
 * accelerator keys (F4) were pressed.
 *
 * @author  Elmar Baumann
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
        ComponentUtil.show(ActionsDialog.INSTANCE);
    }
}
