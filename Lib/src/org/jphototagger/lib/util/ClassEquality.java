package org.jphototagger.lib.util;

/**
 * Object equality if the classes are equals.
 * <p>
 * Motivation: Comparing different instances of stateless classes via <code>equals()</code>.
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

        Class<? extends ClassEquality> thisClass = getClass();
        Class<? extends Object> objClass = obj.getClass();

        return thisClass.equals(objClass);
    }

    @Override
    public int hashCode() {
        Class<? extends ClassEquality> thisClass = getClass();

        return thisClass.hashCode();
    }
}
