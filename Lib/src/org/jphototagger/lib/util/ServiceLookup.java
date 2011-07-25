package org.jphototagger.lib.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Looks for implementations of a Java Service.
 *
 * For details see http://java.sun.com/j2se/1.4.2/docs/guide/jar/jar.html#Service%20Provider.
 *
 * Inspired by http://bits.netbeans.org/dev/javadoc/org-openide-util/org/openide/util/ServiceLookup.html.
 *
 * @author Elmar Baumann
 */
public final class ServiceLookup {

    /**
     * Returns the first implementation of a Java Service.
     *
     * @param <T>           service interface type
     * @param serviceClass  service interface class
     * @return              first of all implementations of that service
     *                      or null if no class implements that service
     */
    public static <T> T lookup(Class<T> serviceClass) {
        if (serviceClass == null) {
            throw new NullPointerException("serviceClass == null");
        }

        ServiceLoader<T> serviceLoader = ServiceLoader.load(serviceClass);
        Iterator<T> serviceImplementations = serviceLoader.iterator();

        return serviceImplementations.hasNext()
                ? serviceImplementations.next()
                : null;
    }

    /**
     * Returns all implementations of a Java Service.
     *
     * @param <T>           service interface type
     * @param serviceClass  service interface class
     * @return              all implementations of that service
     *                      or an empty collection if no class implements that service
     */
    public static <T> Collection<? extends T> lookupAll(Class<T> serviceClass) {
        if (serviceClass == null) {
            throw new NullPointerException("serviceClass == null");
        }

        Collection<T> serviceImplementations = new ArrayList<T>();
        ServiceLoader<T> serviceLoader = ServiceLoader.load(serviceClass);

        for (T service : serviceLoader) {
            serviceImplementations.add(service);
        }

        return serviceImplementations;
    }

    private ServiceLookup() {
    }
}
