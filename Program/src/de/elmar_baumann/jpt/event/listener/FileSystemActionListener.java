/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.event.listener;

import de.elmar_baumann.jpt.event.FileSystemError;
import de.elmar_baumann.jpt.event.FileSystemEvent;
import java.io.File;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-20
 */
public interface FileSystemActionListener {
    
    
    /**
     * Called if an file system was performed.
     * 
     * @param action  action
     * @param src     source file
     * @param target  target file
     */
    public void actionPerformed(FileSystemEvent action, File src, File target);
    
    /**
     * Called if an file system action failed.
     * 
     * @param action  action
     * @param error   error
     * @param src     source file
     * @param target  target file
     */
    public void actionFailed(FileSystemEvent action, FileSystemError error, File src, File target);

}
