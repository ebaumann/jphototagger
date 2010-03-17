/*
 * @(#)UserSettingsEvent.java    2008-09-14
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

package de.elmar_baumann.jpt.event;

/**
 * Action: The user has changed the settings.
 *
 * @author  Elmar Baumann
 */
public final class UserSettingsEvent {
    private Object source;
    private Type   type;

    public enum Type {
        ACCEPT_HIDDEN_DIRECTORIES, AUTOCOPY_DIRECTORY,
        AUTO_DOWNLOAD_NEWER_VERSIONS, AUTO_SCAN_DIRECTORIES,
        AUTO_SCAN_INCLUDE_DIRECTORIES, DATABASE_DIRECTORY,
        DEFAULT_IMAGE_OPEN_APP, DISPLAY_IPTC, DISPLAY_SEARCH_BUTTON,
        EDIT_COLUMNS, EXECUTE_ACTION_AFTER_IMAGE_CHANGE_IN_DB_ALWAYS,
        EXECUTE_ACTION_AFTER_IMAGE_CHANGE_IN_DB_IF_IMAGE_HAS_XMP,
        EXTERNAL_THUMBNAIL_CREATION_COMMAND, FAST_SEARCH_COLUMNS, IPTC_CHARSET,
        LOGFILE_FORMATTER_CLASS, LOG_LEVEL,
        MAX_SECONDS_TO_TERMINATE_EXTERNAL_PROGRAMS, MAX_THUMBNAIL_WIDTH,
        MINUTES_TO_START_SCHEDULED_TASKS, NO_FAST_SEARCH_COLUMNS,
        OPTIONS_COPY_MOVE_FILES, PDF_VIEWER, SAVE_INPUT_EARLY,
        SCAN_FOR_EMBEDDED_XMP, TREE_DIRECTORIES_SELECT_LAST_DIRECTORY,
        THUMBNAIL_CREATOR, WEB_BROWSER,
    }

    public UserSettingsEvent(Type type, Object source) {
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
