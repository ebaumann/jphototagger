/*
 * @(#)FileLock.java    Created on 2009-06-22
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

package org.jphototagger.lib.io;

import java.io.File;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Locks  <em>internally</em> a file (other applications doesn't regognize
 * the lock).
 *
 * Requires that <em>every</em> objects which manipulate files
 * uses this class to access files. Should be used when changing files or
 * using libraries which fail when opening the same file twice (for reading).
 *
 * @author  Elmar Baumann
 */
public final class FileLock {
    public static final FileLock    INSTANCE          = new FileLock();
    private final Map<File, Object> ownerOfLockedFile =
        Collections.synchronizedMap(new HashMap<File, Object>());

    /**
     * Locks a file.
     *
     * @param  file  file to lock
     * @param  owner owner of the file
     * @return       true if locked. false if the file is already locked.
     */
    public boolean lock(File file, Object owner) {
        synchronized (ownerOfLockedFile) {
            if (!ownerOfLockedFile.containsKey(file)) {
                ownerOfLockedFile.put(file, owner);

                return true;
            }

            return false;
        }
    }

    /**
     * Returns the owner of a locked file.
     *
     * @param  file file
     * @return      owner or null if no object owns a lock of that file
     */
    public Object getOwner(File file) {
        return ownerOfLockedFile.get(file);
    }

    /**
     * Unlocks a locked file.
     *
     * @param  file  file to unlock
     * @param  owner owner of the lock - only the owner can unlock a file
     * @return true if unlocked
     */
    public boolean unlock(File file, Object owner) {
        synchronized (ownerOfLockedFile) {
            if (ownerOfLockedFile.containsKey(file)
                    && ownerOfLockedFile.get(file).equals(owner)) {
                ownerOfLockedFile.remove(file);

                return true;
            }

            return false;
        }
    }

    private FileLock() {}
}
