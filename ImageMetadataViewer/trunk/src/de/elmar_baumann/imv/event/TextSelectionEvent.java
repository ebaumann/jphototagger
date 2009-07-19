package de.elmar_baumann.imv.event;

/**
 * Text was selected.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-26
 */
public final class TextSelectionEvent {

    private final String text;

    /**
     * Constructor.
     *
     * @param text selected text
     */
    public TextSelectionEvent(String text) {
        this.text = text;
    }

    /**
     * Returns the selected text.
     *
     * @return text
     */
    public String getText() {
        return text;
    }
}
