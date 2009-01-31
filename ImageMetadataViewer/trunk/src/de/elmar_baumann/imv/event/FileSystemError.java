package de.elmar_baumann.imv.event;

/**
 *
 * @author Elmar Baumann <eb@elmar-baumann.de>
 */
public enum FileSystemError {

    LOCKED("Die Datei ist gesperrt."),
    MISSING_PRIVILEGES("Es fehlen die erforderlichen Rechte."),
    MOVE_RENAME_EXISTS("Die Zieldatei existiert bereits."),
    READ_ONLY("Die Datei kann nur gelesen werden"),
    UNKNOWN("Die Usache ist unbekannt.");
    
    private final String message;
    
    private FileSystemError(String message) {
        this.message = message;
    }
    
    public String getLocalizedMessage() {
        return message;
    }
}
