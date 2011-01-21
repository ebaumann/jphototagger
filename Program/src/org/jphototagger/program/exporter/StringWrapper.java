package org.jphototagger.program.exporter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 *
 * @author Elmar Baumann
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class StringWrapper {
    private String string;

    public StringWrapper() {}

    public StringWrapper(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public static List<StringWrapper> getWrappedStrings(
            Collection<? extends String> strings) {
        if (strings == null) {
            throw new NullPointerException("strings == null");
        }

        List<StringWrapper> wrapped =
            new ArrayList<StringWrapper>(strings.size());

        for (String string : strings) {
            wrapped.add(new StringWrapper(string));
        }

        return wrapped;
    }
}
