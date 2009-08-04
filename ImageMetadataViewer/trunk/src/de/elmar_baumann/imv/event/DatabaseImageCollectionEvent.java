package de.elmar_baumann.imv.event;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Image collection database event.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-04
 */
public final class DatabaseImageCollectionEvent {

    /**
     * Event type.
     */
    public enum Type {

        /**
         * An image collection was deleted
         */
        COLLECTION_DELETED,
        /**
         * An image collection was inserted
         */
        COLLECTION_INSERTED,
        /**
         * Images were inserted into an image collection
         */
        IMAGES_INSERTED,
        /**
         * Images were deleted from an image collection
         */
        IMAGES_DELETED,
    };
    private final String collectionName;
    private final Set<String> filenames;
    private final Type type;

    /**
     * Creates a new event.
     *
     * @param type           event type
     * @param collectionName name of the image collection
     * @param filenames      names of the affected files
     */
    public DatabaseImageCollectionEvent(Type type,
            String collectionName,
            Collection<? extends String> filenames) {
        this.type = type;
        this.collectionName = collectionName;
        this.filenames = new HashSet<String>(filenames);
    }

    /**
     * Returns the event type.
     * 
     * @return event type
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the name of the affected image collection.
     *
     * @return name of the image collection
     */
    public String getCollectionName() {
        return collectionName;
    }

    /**
     * Returns the affected image filenames.
     * 
     * @return affected image filenames
     */
    public Set<String> getFilenames() {
        return filenames;
    }
}
