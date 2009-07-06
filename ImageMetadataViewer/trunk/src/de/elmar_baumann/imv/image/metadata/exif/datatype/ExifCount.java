package de.elmar_baumann.imv.image.metadata.exif.datatype;

/**
 * Count of an EXIF data.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/07/06
 */
public final class ExifCount {

    public static final ExifCount NUMBER_1 = new ExifCount(1);
    public static final ExifCount NUMBER_2 = new ExifCount(2);
    public static final ExifCount NUMBER_3 = new ExifCount(3);
    public static final ExifCount NUMBER_4 = new ExifCount(4);
    public static final ExifCount NUMBER_11 = new ExifCount(11);
    public static final ExifCount NUMBER_20 = new ExifCount(20);
    public static final ExifCount NUMBER_33 = new ExifCount(33);
    public static final ExifCount ANY = new ExifCount(Type.ANY);

    public enum Type {

        ANY, NUMBER
    };
    private final Integer count;
    private final Type type;

    private ExifCount(int count) {
        this.count = count;
        type = Type.NUMBER;
    }

    private ExifCount(Type type) {
        this.type = type;
        count = null;
    }

    /**
     * Returns the count.
     *
     * @return count if {@link #getType()} equals {@link Type#NUMBER}, else null
     */
    public Integer getValue() {
        return count;
    }

    /**
     * Returns the type of the count.
     *
     * @return type
     */
    public Type getType() {
        return type;
    }
}
