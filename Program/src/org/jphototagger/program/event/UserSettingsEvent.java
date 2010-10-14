/*
 * @(#)UserSettingsEvent.java    Created on 2008-09-14
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

package org.jphototagger.program.event;

/**
 * Action: The user has changed the settings.
 *
 * @author Elmar Baumann
 */
public final class UserSettingsEvent {
    private Object source;
    private Type   type;

    public enum Type {
        DISPLAY_IPTC, LOG_LEVEL, MAX_THUMBNAIL_WIDTH, CHECK_FOR_UPDATES
    }

    public UserSettingsEvent(Type type, Object source) {
        if (type == null) {
            throw new NullPointerException("type == null");
        }

        if (source == null) {
            throw new NullPointerException("source == null");
        }

        this.type   = type;
        this.source = source;
    }

    public Type getType() {
        return type;
    }

    public Object getSource() {
        return source;
    }
}
