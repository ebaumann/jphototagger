package org.jphototagger.lib.xml.bind;

import org.jphototagger.lib.thirdparty.Base64;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * For strings containing "\000".
 *
 * @author Elmar Baumann
 */
public final class Base64ByteStringXmlAdapter extends XmlAdapter<String, String> {
    @Override
    public String unmarshal(String value) throws Exception {
        if (value == null) {
            return null;
        }

        return new String(Base64.decode(value));
    }

    @Override
    public String marshal(String value) throws Exception {
        if (value == null) {
            return null;
        }

        return Base64.encodeBytes(value.getBytes());
    }
}
