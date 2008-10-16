package de.elmar_baumann.imv.event;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/14
 */
public class UserSettingsChangeEvent {

    private Type type;
    private Object source;

    public enum Type {

        AutoscanDirectories,
        DefaultImageOpenApp,
        ExternalThumbnailCreationCommand,
        FastSearchColumnDefined,
        IptcCharset,
        IsAcceptHiddenDirectories,
        IsAutoscanIncludeSubdirectories,
        IsCreateThumbnailsWithExternalApp,
        IsTaskRemoveRecordsWithNotExistingFiles,
        IsUseAutocomplete,
        IsUseEmbeddedThumbnails,
        LogfileFormatterClass,
        LogLevel,
        MaxThumbnailWidth,
        MinutesToStartScheduledTasks,
        NoFastSearchColumns,
        OtherImageOpenApps,
        ThreadPriority,
    }

    public UserSettingsChangeEvent(Type type, Object source) {
        this.type = type;
        this.source = source;
    }

    public Type getType() {
        return type;
    }

    public Object getSource() {
        return source;
    }
}
