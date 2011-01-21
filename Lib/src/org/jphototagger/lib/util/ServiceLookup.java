package org.jphototagger.lib.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ServiceLoader;

/**
 * Looks for implementations of a Java service.
 *
 * For details see http://java.sun.com/j2se/1.4.2/docs/guide/jar/jar.html#Service%20Provider.
 *
 * Inspired by http://bits.netbeans.org/dev/javadoc/org-openide-util/org/openide/util/ServiceLookup.html.
 *
 * @author Elmar Baumann
 */
public final class ServiceLookup {

    /**
     * Returns the first implementing class of a Java service.
     *
     * @param <T>    Class type - usually the service interface
     * @param clazz  Class -  usually the service interface's class object
     * @return       first of all implementing classes of that service or null
     *               if no class implements that service
     */
    public static <T> T lookup(Class<T> clazz) {
        if (clazz == null) {
            throw new NullPointerException("clazz == null");
        }

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
        if (clazz == null) {
            throw new NullPointerException("clazz == null");
        }

        Collection<T> result = new ArrayList<T>();

        try {
            for (T service : ServiceLoader.load(clazz)) {
                result.add(service);
            }
        } catch (Exception ex) {
            Logger.getLogger(ServiceLookup.class.getName()).log(Level.SEVERE,
                             null, ex);
        }

        return result;
    }

    private ServiceLookup() {}
}
