/*
 * @(#)EditMetadataPanelsListenerSupport.java    Created on 2008-10-18
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

package org.jphototagger.program.event.listener.impl;

import org.jphototagger.program.event.listener.EditMetadataPanelsListener;

/**
 *
 * @author  Elmar Baumann
 */
public final class EditMetadataPanelsListenerSupport
        extends ListenerSupport<EditMetadataPanelsListener> {
    public void notifyEditEnabled() {
        synchronized (listeners) {
            for (EditMetadataPanelsListener listener : listeners) {
                listener.editEnabled();
            }
        }
    }

    public void notifyEditDisabled() {
        synchronized (listeners) {
            for (EditMetadataPanelsListener listener : listeners) {
                listener.editDisabled();
            }
        }
    }
}
