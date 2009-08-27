package de.elmar_baumann.lib.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Looks for implementations of a Java service.
 *
 * For details see {@link http://java.sun.com/j2se/1.4.2/docs/guide/jar/jar.html#Service%20Provider}.
 *
 * Inspired by {@link http://bits.netbeans.org/dev/javadoc/org-openide-util/org/openide/util/Lookup.html}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-27
 */
public final class Lookup {

    /**
     * Returns the first implementing class of a Java service.
     *
     * @param <T>    Class type - usually the service interface
     * @param clazz  Class -  usually the service interface's class object
     * @return       first of all implementing classes of that service or null
     *               if no class implements that service
     */
    public static <T> T lookup(Class<T> clazz) {

        Iterator<T> services = ServiceLoader.load(clazz).iterator();

        return services.hasNext()
               ? services.next()
               : null;

    }

    /**
     * Returns all implementing classses of a Java service.
     *
     * @param <T>    Class type - usually the service interface
     * @param clazz  Class -  usually the service interface's class object
     * @return       all implementing classes of that service or null if no
     *               class implements that service
     */
    public static <T> Collection<? extends T> lookupAll(Class<T> clazz) {

        Collection<T> result = new ArrayList<T>();

        for (T service : ServiceLoader.load(clazz)) {
            result.add(service);
        }

        return result;

    }

    private Lookup() {
    }
}
