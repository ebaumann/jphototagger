/*
 * @(#)ControllerEmptyMetadata.java    Created on 2008-10-22
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

package org.jphototagger.program.controller.metadata;

import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.AppPanel;
import org.jphototagger.program.view.panels.EditMetadataPanels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class ControllerEmptyMetadata implements ActionListener {
    private final AppPanel           appPanel    = GUI.INSTANCE.getAppPanel();
    private final JButton            buttonEmpty =
        appPanel.getButtonEmptyMetadata();
    private final EditMetadataPanels editPanels  =
        appPanel.getEditMetadataPanels();

    public ControllerEmptyMetadata() {
        listen();
    }

    private void listen() {
        buttonEmpty.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                editPanels.emptyPanels(true);
            }
        });
    }
}
