package org.jphototagger.lib.lookup;

import java.util.Collection;

import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 * @author Elmar Baumann
 */
public final class ModifiableLookup implements Lookup.Provider {

    private final InstanceContent content = new InstanceContent();
    private final Lookup lookup = new AbstractLookup(content);

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    public void add(Object object) {
        content.add(object);
    }

    public void remove(Object object) {
        content.remove(object);
    }

    public void set(Collection<?> ts) {
        content.set(ts, null);
    }
}
