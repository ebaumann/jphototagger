package org.jphototagger.lib.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * Usage only if keys are String objects and nothing else!
 *
 * @author Elmar Baumann
 */
public final class SortedProperties extends Properties {

    public SortedProperties() {
    }

    public SortedProperties(Properties defaults) {
        super(defaults);
    }

    @Override
    public Enumeration keys() {
        List<String> keyList = new ArrayList<>();
        for (Enumeration<Object> e = super.keys(); e.hasMoreElements();) {
            keyList.add(e.nextElement().toString());
        }
        Collections.sort(keyList, String.CASE_INSENSITIVE_ORDER);
        return Collections.enumeration(keyList);
    }
}
