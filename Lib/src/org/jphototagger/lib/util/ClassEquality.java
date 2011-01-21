package org.jphototagger.lib.util;

/**
 * Object equality if the classes are equals.
 * <p>
 * Motivation: Comparing different instances of stateless classes via
 * <code>equals()</code>.
 *
 *
 * @author Elmar Baumann
 */
public class ClassEquality {
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        return getClass().equals(obj.getClass());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
