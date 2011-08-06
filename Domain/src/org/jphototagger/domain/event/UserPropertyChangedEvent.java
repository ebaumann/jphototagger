package org.jphototagger.domain.event;

/**
 *
 * @author Elmar Baumann
 */
public final class UserPropertyChangedEvent {

    public static final String PROPERTY_DISPLAY_IPTC = "UserSettings.DisplayIptc";
    public static final String PROPERTY_IPTC_CHARSET = "UserSettings.IptcCharset";
    public static final String PROPERTY_LOG_LEVEL = "UserSettings.LogLevel";
    public static final String PROPERTY_MAX_THUMBNAIL_WIDTH = "UserSettings.MaxThumbnailWidth";
    public static final String PROPERTY_CHECK_FOR_UPDATES = "UserSettings.AutoDownloadNewerVersions";
    public static final String PROPERTY_DISPLAY_THUMBNAIL_TOOLTIP = "UserSettings.DisplayThumbnailTooltip";
    private final Object source;
    private final String property;
    private final Object oldValue;
    private final Object newValue;

    public UserPropertyChangedEvent(Object source, String property, Object oldValue, Object newValue) {
        if (property == null) {
            throw new NullPointerException("property == null");
        }

        this.source = source;
        this.property = property;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public Object getSource() {
        return source;
    }

    public String getProperty() {
        return property;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }
}
