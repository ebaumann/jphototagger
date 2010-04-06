/*
 * @(#)ProgressBar.java    Created on 2009-06-16
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

package org.jphototagger.program.view.panels;

import org.jphototagger.lib.resource.MutualExcludedResource;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.resource.GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.lang.reflect.Method;

import javax.swing.JButton;
import javax.swing.JProgressBar;

/**
 * Synchronized access to {@link AppPanel#getProgressBar()}.
 *
 * @author  Elmar Baumann
 */
public final class ProgressBar extends MutualExcludedResource<JProgressBar>
        implements ActionListener {
    public static final ProgressBar INSTANCE           = new ProgressBar();
    private static final String     METHOD_NAME_CANCEL = "cancel";
    private final JButton           buttonCancel;

    private ProgressBar() {
        AppPanel appPanel = GUI.INSTANCE.getAppPanel();

        setResource(appPanel.getProgressBar());
        buttonCancel = appPanel.getButtonCancelProgress();
        buttonCancel.addActionListener(this);
        buttonCancel.setEnabled(false);
    }

    @Override
    public synchronized JProgressBar getResource(Object owner) {
        JProgressBar pb = getResource(owner);

        buttonCancel.setEnabled((pb != null) && hasCancelMethod(owner));

        return pb;
    }

    @Override
    public synchronized boolean releaseResource(Object owner) {
        boolean released = super.releaseResource(owner);

        // Avoid releases through not owners
        if (released) {
            buttonCancel.setEnabled(false);
        }

        return released;
    }

    private synchronized void cancel() {
        Object o            = getOwner();
        Method methodCancel = null;

        if (hasCancelMethod(o)) {
            try {
                methodCancel = o.getClass().getMethod(METHOD_NAME_CANCEL);
                methodCancel.invoke(o);
            } catch (Exception ex) {
                AppLogger.logSevere(ProgressBar.class, ex);
            }
        }
    }

    private boolean hasCancelMethod(Object o) {
        Method[] methods = o.getClass().getDeclaredMethods();

        for (Method method : methods) {
            if (method.getName().equals(METHOD_NAME_CANCEL)
                    && (method.getParameterTypes().length == 0)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        cancel();
    }
}
