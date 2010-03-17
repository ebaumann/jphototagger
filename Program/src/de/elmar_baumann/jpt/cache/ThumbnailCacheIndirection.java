/*
 * @(#)ThumbnailCacheIndirection.java    2009-07-18
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

package de.elmar_baumann.jpt.cache;

import java.awt.Image;

import java.io.File;

/**
 *
 * @author Martin Pohlack
 */
public class ThumbnailCacheIndirection extends CacheIndirection {
    public Image thumbnail;

    public ThumbnailCacheIndirection(File _file) {
        super(_file);
        thumbnail = null;
    }

    @Override
    public boolean isEmpty() {
        return thumbnail == null;
    }
}
