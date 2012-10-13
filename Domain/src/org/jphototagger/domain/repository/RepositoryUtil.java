package org.jphototagger.domain.repository;

import org.jphototagger.api.event.PropertyEvent;

/**
 * @author Elmar Baumann
 */
public final class RepositoryUtil {

    /**
     * Looks whether a property claims to save or update the repository.
     * @param object maybe null (returning false)
     * @return true if the property is a {@link SaveOrUpdate} instance but not {@link SaveOrUpdate#NONE}
     */
    public static boolean isSaveOrUpdate(Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof SaveOrUpdate) {
            return SaveOrUpdate.NONE != object;
        }
        return false;
    }

    /**
     * Checks whether the event contains the property {@link SaveOrUpdate} (key is it's class literal) and if so,
     * whether it's not {@link SaveOrUpdate#NONE}.
     * @param evt
     * @return true if the event contains the property and it is not {@link SaveOrUpdate#NONE}
     */
    public static boolean isSaveOrUpdate(PropertyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }
        SaveOrUpdate saveOrUpdate = evt.getProperty(SaveOrUpdate.class);
        return saveOrUpdate == null
                ? false
                : SaveOrUpdate.NONE != saveOrUpdate;
    }

    private RepositoryUtil() {
    }
}
