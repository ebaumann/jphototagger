package de.elmar_baumann.jpt.image.metadata.xmp;

/**
 * Namespaces not existing in {@link com.adobe.xmp.XMPConst}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-27
 */
public enum Namespace {

    LIGHTROOM("http://ns.adobe.com/lightroom/1.0/"),
    ;
    private final String uri;

    private Namespace(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }
}
