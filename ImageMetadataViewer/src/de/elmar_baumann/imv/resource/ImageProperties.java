package de.elmar_baumann.imv.resource;

/**
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/02/17
 */
public final class ImageProperties {

    public final String[] get() {
        String[] s = new String[res.length];
        for (int i = 0; i < s.length; i++) {
            s[i] = res[i];
        }

        return s;
    }

    private final String[] res = {
        "rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAABsrO0ABXQADUVsbWFyIEJhdW1hbm50", // NOI18N
        "AAB0AApYTVAtVmlld2VydAAEbWV0YXQAF25vbi1jb21tZXJjaWFsIHVzZSBvbmx5", // NOI18N
        "dAACbm9xAH4AAXQACDQ5NjdiYTQxdAAINGI0NzljNDF0AAQzLjk5dXEAfgAAAAAA", // NOI18N
        "LzAtAhUAjRXtsIQa82hgU1i+5VkxCFR1vjgCFElopuR1tTXE6yNOcuE6uOFIrLDp", // NOI18N
    }
;

}
