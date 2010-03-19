/*
 * @(#)ServiceLookup.java    Created on 2009-08-27
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package de.elmar_baumann.lib.util;

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
 * @author  Elmar Baumann
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

        try {
            for (T service : ServiceLoader.load(clazz)) {
                result.add(service);
            }
        } catch (Exception ex) {
            Logger.getLogger(ServiceLookup.class.getName()).log(Level.SEVERE, null,
                             ex);
        }

        return result;
    }

    private ServiceLookup() {}
}
