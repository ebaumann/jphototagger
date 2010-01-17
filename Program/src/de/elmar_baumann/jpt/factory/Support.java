/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-17
 */
final class Support {

    private final Map<Class<?>, List<Object>> OBJECT_INSTANCES_OF_CLASS = new HashMap<Class<?>, List<Object>>();

    @SuppressWarnings("unchecked")
    <T> List<T>  getAll(Class<T> clazz) {
        return (List<T>) OBJECT_INSTANCES_OF_CLASS.get(clazz);
    }

    @SuppressWarnings("unchecked")
    <T> T getFirst(Class<T> clazz) {
        List<T> instances = (List<T>) OBJECT_INSTANCES_OF_CLASS.get(clazz);
        return instances == null ? null : instances.get(0);
    }

    void add(Object instance) {

        List<Object> instances = OBJECT_INSTANCES_OF_CLASS.get(instance.getClass());

        if (instances == null) {
            instances = new ArrayList<Object>();
            OBJECT_INSTANCES_OF_CLASS.put(instance.getClass(), instances);
        }

        instances.add(instance);
    }
}
