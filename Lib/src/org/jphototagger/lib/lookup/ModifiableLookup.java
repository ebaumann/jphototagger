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

    public void add(Object content) {
        if (content == null) {
            throw new NullPointerException("content == null");
        }

        this.content.add(content);
    }

    public void remove(Object content) {
        if (content == null) {
            throw new NullPointerException("content == null");
        }

        this.content.remove(content);
    }

    public void set(Collection<?> content) {
        if (content == null) {
            throw new NullPointerException("content == null");
        }

        this.content.set(content, null);
    }
}
