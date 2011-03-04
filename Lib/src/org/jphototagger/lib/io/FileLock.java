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
 * @author Elmar Baumann
 */
public final class FileLock {
    public static final FileLock INSTANCE = new FileLock();
    private final Map<File, Object> ownerOfLockedFile = Collections.synchronizedMap(new HashMap<File, Object>());

    /**
     * Locks a file.
     *
     * @param  file  file to lock
     * @param  owner owner of the file
     * @return       true if locked. false if the file is already locked.
     */
    public boolean lock(File file, Object owner) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        if (owner == null) {
            throw new NullPointerException("owner == null");
        }

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
        if (file == null) {
            throw new NullPointerException("file == null");
        }

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
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        if (owner == null) {
            throw new NullPointerException("owner == null");
        }

        synchronized (ownerOfLockedFile) {
            if (ownerOfLockedFile.containsKey(file) && ownerOfLockedFile.get(file).equals(owner)) {
                ownerOfLockedFile.remove(file);

                return true;
            }

            return false;
        }
    }

    private FileLock() {}
}
