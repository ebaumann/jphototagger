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

import org.jphototagger.lib.concurrent.Cancelable;
import org.jphototagger.lib.resource.MutualExcludedResource;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import javax.swing.JButton;
import javax.swing.JProgressBar;

/**
 * Synchronized access to {@link AppPanel#getProgressBar()}.
 *
 * @author  Elmar Baumann
 */
public final class ProgressBar extends MutualExcludedResource<JProgressBar>
        implements ActionListener {
    public static final ProgressBar INSTANCE = new ProgressBar();
    private final JButton           buttonCancel;
    private volatile boolean        cancelEnabled = true;

    private ProgressBar() {
        AppPanel appPanel = GUI.INSTANCE.getAppPanel();

        setResource(appPanel.getProgressBar());
        buttonCancel = appPanel.getButtonCancelProgress();
        buttonCancel.addActionListener(this);
        buttonCancel.setEnabled(false);
    }

    /**
     * Enables or disables a cancel button if the owner implements
     * {@link Cancelable}.
     * 
     * @param owner   owner
     * @param enabled true if enable a cancel button. Default: true.
     */
    public synchronized void setEnabledCancel(Object owner, boolean enabled) {
        if (owner == getOwner()) {
            cancelEnabled = enabled;
        }
    }

    /**
     * Returns the progress bar and offers a cancel button if the owner
     * implements {@link Cancelable}.
     *
     * @param  owner
     * @return       progress bar or null if locked through another owner
     */
    @Override
    public synchronized JProgressBar getResource(Object owner) {
        JProgressBar pb        = super.getResource(owner);
        boolean      canCancel = (pb != null) && (owner instanceof Cancelable);

        if (cancelEnabled && canCancel) {
            setEnabledCancelButton(true);
        }

        return pb;
    }

    @Override
    public synchronized boolean releaseResource(Object owner) {
        boolean released = super.releaseResource(owner);

        // Only the owner can deactivate the button (released is only true if
        // the owner did call it)
        if (released) {
            setEnabledCancelButton(false);
            cancelEnabled = true;
        }

        return released;
    }

    private void setEnabledCancelButton(boolean enabled) {
        buttonCancel.setEnabled(enabled);

        if (enabled) {
            buttonCancel.setToolTipText(
                JptBundle.INSTANCE.getString("ProgressBar.TooltipText.Cancel"));
            buttonCancel.setIcon(AppLookAndFeel.ICON_CANCEL);
        } else {
            buttonCancel.setToolTipText("");
            buttonCancel.setIcon(null);
        }
    }

    private synchronized void cancel() {
        Object owner = getOwner();

        if (owner instanceof Cancelable) {
            ((Cancelable) owner).cancel();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        cancel();
    }
}
