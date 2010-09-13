/*
 * @(#)ControllerAboutApp.java    Created on 2008-09-12
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

package org.jphototagger.program.controller.misc;

import org.jphototagger.program.app.AppInfo;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

/**
 * Kontrolliert die Aktion: Informationen über die Anwendung sollen angezeigt
 * werden.
 *
 * @author  Elmar Baumann
 */
public final class ControllerAboutApp implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent evt) {
        JOptionPane.showMessageDialog(
            GUI.INSTANCE.getAppFrame(),
            JptBundle.INSTANCE.getString(
                "ControllerAboutApp.Info.About", AppInfo.APP_NAME,
                AppInfo.APP_VERSION), "JPhotoTagger",
                                      JOptionPane.INFORMATION_MESSAGE,
                                      AppLookAndFeel.getIcon(
                                          "icon_logo.png"));
    }
}
