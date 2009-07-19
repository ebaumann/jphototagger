package de.elmar_baumann.lib.io;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Locks files. Requires that <em>every</em> objects which manipulate files
 * uses this class to access files. Should be used when changing files or
 * using libraries which fail when opening the same file twice (for reading).
 *
 * <em>This class does not lock the file using the file system! It locks them
 * virtually for objects using this class.</em>
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-22
 */
public final class FileLock {

    public static final FileLock INSTANCE = new FileLock();
    private final Map<File, Object> objectOfLockedFile =
            Collections.synchronizedMap(new HashMap<File, Object>());

    public boolean lock(File file, Object owner) {
        synchronized (objectOfLockedFile) {
            if (!objectOfLockedFile.containsKey(file)) {
                objectOfLockedFile.put(file, owner);
                return true;
            }
            return false;
        }
    }

    public boolean unlock(File file, Object owner) {
        synchronized (objectOfLockedFile) {
            if (objectOfLockedFile.containsKey(file) &&
                    objectOfLockedFile.get(file).equals(owner)) {
                objectOfLockedFile.remove(file);
                return true;
            }
            return false;
        }
    }

    private FileLock() {
    }
}
