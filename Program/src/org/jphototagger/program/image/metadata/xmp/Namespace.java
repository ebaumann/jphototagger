package org.jphototagger.program.image.metadata.xmp;

/**
 * Namespaces not existing in {@link com.adobe.xmp.XMPConst}.
 *
 * @author Elmar Baumann
 */
public enum Namespace {
    LIGHTROOM("http://ns.adobe.com/lightroom/1.0/", "lr"),
    ;

    private final String uri;
    private final String prefix;

    private Namespace(String uri, String prefix) {
        this.uri = uri;
        this.prefix = prefix;
    }

    public String getUri() {
        return uri;
    }

    public String getPrefix() {
        return prefix;
    }
}
