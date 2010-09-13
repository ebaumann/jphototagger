/*
 * @(#)ThumbnailRenderer.java    Created on 2009-08-17
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

package org.jphototagger.program.cache;

import java.awt.Image;

/**
 * This object is responsible for rendering a complete thumbnail include
 * overlays, filename, border etc.
 *
 * It provides a single method and this method requires a single pre-scaled
 * thumbnail, all other sizes are based on the longer edge of the provided
 * image.
 *
 * @author Martin Pohlack
 */
public interface ThumbnailRenderer {
    Image getRenderedThumbnail(Image scaled,
                               RenderedThumbnailCacheIndirection rtci,
                               boolean dummy);
}
