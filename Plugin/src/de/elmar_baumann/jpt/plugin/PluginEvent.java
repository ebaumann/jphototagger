/*
 * @(#)PluginEvent.java    Created on 2010-02-17
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

package de.elmar_baumann.jpt.plugin;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author  Elmar Baumann
 */
public class PluginEvent {
    public enum Type {

        /**
         * The plugin action has been started
         */
        STARTED,

        /**
         * The plugin action has been finished successfully
         */
        FINISHED_SUCCESS,

        /**
         * The plugin action has been finished with errors
         */
        FINISHED_ERRORS,
    }

    private final Type       type;
    private final List<File> processedFiles = new ArrayList<File>();
    private final List<File> changedFiles   = new ArrayList<File>();

    public PluginEvent(Type type) {
        this.type = type;
    }

    public List<File> getChangedFiles() {
        return new ArrayList<File>(changedFiles);
    }

    public void setChangedFiles(List<File> changedFiles) {
        this.changedFiles.clear();
        this.changedFiles.addAll(changedFiles);
    }

    public boolean filesChanged() {
        return changedFiles.size() > 0;
    }

    public void setProcessedFiles(List<File> processedFiles) {
        this.processedFiles.clear();
        this.processedFiles.addAll(processedFiles);
    }

    public List<File> getProcessedFiles() {
        return new ArrayList<File>(processedFiles);
    }

    public Type getType() {
        return type;
    }

    public boolean isStarted() {
        return type.equals(Type.STARTED);
    }

    public boolean isFinishedSuccessfully() {
        return type.equals(Type.FINISHED_SUCCESS);
    }

    public boolean isFinishedWithErrors() {
        return type.equals(Type.FINISHED_ERRORS);
    }

    public boolean isFinished() {
        return isFinishedSuccessfully() || isFinishedWithErrors();
    }
}
