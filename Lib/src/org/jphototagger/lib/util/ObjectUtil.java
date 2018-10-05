package org.jphototagger.lib.util;

/**
 * @author Elmar Baumann
 */
public final class ObjectUtil {

    /**
     * @param object1
     * @param object2
     *
     * @return true if both objects are the same reference, both are null or
     *         {@code object1.equals(object2)}
     */
    public static boolean equals(Object object1, Object object2) {
        if (object1 == object2) {
            return true;
        }

        return object1 == null
                ? object2 == null
                : object1.equals(object2);
    }

    /**
     * @param <T>
     * @param ts
     *
     * @return first of {@code ts} which is not null. null, if all {@code ts}
     *         are null.
     */
    @SafeVarargs
    public static <T> T firstNonNull(T... ts) {
        for (T t : ts) {
            if (t != null) {
                return t;
            }
        }
        return null;
    }

    private ObjectUtil() {
    }
}
