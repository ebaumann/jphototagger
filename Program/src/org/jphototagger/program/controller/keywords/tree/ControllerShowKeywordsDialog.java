/*
 * @(#)ControllerShowKeywordsDialog.java    Created on 2009-07-30
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

package org.jphototagger.program.controller.keywords.tree;

import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.InputHelperDialog;
import org.jphototagger.program.view.frames.AppFrame;
import org.jphototagger.lib.componentutil.ComponentUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Listens to the menu item {@link AppFrame#getMenuItemInputHelper()}
 * and shows the {@link InputHelperDialog} on action performed.
 *
 * @author  Elmar Baumann
 */
public final class ControllerShowKeywordsDialog implements ActionListener {
    public ControllerShowKeywordsDialog() {
        listen();
    }

    private void listen() {
        GUI.INSTANCE.getAppFrame().getMenuItemInputHelper().addActionListener(
            this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        showDialog();
    }

    private void showDialog() {
        ComponentUtil.show(InputHelperDialog.INSTANCE);
    }
}
