package de.elmar_baumann.imv.event;

/**
 *
 * @author Elmar Baumann <eb@elmar-baumann.de>
 */
public enum FileSystemError {

    Locked("Die Datei ist gesperrt."),
    MissingPrivileges("Es fehlen die erforderlichen Rechte."),
    MoveRenameExists("Die Zieldatei existiert bereits."),
    ReadOnly("Die Datei kann nur gelesen werden"),
    Unknown("Die Usache ist unbekannt.");
    
    private final String message;
    
    private FileSystemError(String message) {
        this.message = message;
    }
    
    public String getLocalizedMessage() {
        return message;
    }
}
