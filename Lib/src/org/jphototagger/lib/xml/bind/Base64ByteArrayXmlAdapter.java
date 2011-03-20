package org.jphototagger.lib.xml.bind;

import org.jphototagger.lib.thirdparty.Base64;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class Base64ByteArrayXmlAdapter extends XmlAdapter<String, byte[]> {
    @Override
    public byte[] unmarshal(String value) throws Exception {
        if (value == null) {
            return null;
        }

        return Base64.decode(value);
    }

    @Override
    public String marshal(byte[] value) throws Exception {
        if (value == null) {
            return null;
        }

        return Base64.encodeBytes(value);
    }
}
