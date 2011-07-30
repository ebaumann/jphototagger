package org.jphototagger.lib.io;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private static final Logger LOGGER = Logger.getLogger(FileLock.class.getName());
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
     * Locks <em>internally</em> a file (other applications doesn't regognize
     * the lock) and logs a warning if the file couldn't be locked.
     * <p>
     * If a file couldn't be locked a message with {@link Level#WARNING} will be
     * logged with this class' {@link Logger}.
     * <p>
     * <em>The caller has to call {@link FileLock#unlock(java.io.File, java.lang.Object)}
     * after using the file!</em>
     *
     * @param  file  file to lock
     * @param  newOwner owner of the file lock
     * @return       true if the file was locked
     */
    public boolean lockLogWarning(File file, Object newOwner) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        if (newOwner == null) {
            throw new NullPointerException("owner == null");
        }

        if (!lock(file, newOwner)) {
            Object currentOwner = getOwner(file);
            LOGGER.log(Level.WARNING,
                    "The file ''{0}'' couldn''t be locked through {1}, because it''s already locked through {2}!",
                    new Object[]{file, newOwner, currentOwner});

            return false;
        }

        return true;
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

    private FileLock() {
    }
}
