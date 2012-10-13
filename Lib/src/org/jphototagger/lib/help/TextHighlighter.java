package org.jphototagger.lib.help;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Segment;

/**
 * Copied from http://code.google.com/p/wordhighlighter/, modified partially.
 * @author Elmar Baumann
 */
public final class TextHighlighter {

    private final Highlighter highlighter = new DefaultHighlighter();
    private Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
    private final JTextComponent tc;
    private final Set<String> hlWords = new HashSet<>();
    private boolean inWord;
    private int wordStartIndex = -1;

    TextHighlighter(JTextComponent tc) {
        if (tc == null) {
            throw new NullPointerException("tc == null");
        }

        this.tc = tc;
        tc.setHighlighter(highlighter);
    }

    synchronized void setHighlightColor(Color color) {
        if (color == null) {
            throw new NullPointerException("color == null");
        }

        painter = new DefaultHighlighter.DefaultHighlightPainter(color);
    }

    synchronized void highlightWords(Set<String> words) {
        if (words == null) {
            throw new NullPointerException("words == null");
        }

        hlWords.clear();
        addAsLowercase(words);
        highlight();
    }

    private synchronized void addAsLowercase(Set<String> wds) {
        for (String word : wds) {
            hlWords.add(word.toLowerCase());
        }
    }

    synchronized void removeAllHighlights() {
        highlighter.removeAllHighlights();
    }

    private synchronized void highlight() {
        int offset = 0;
        highlighter.removeAllHighlights();
        wordStartIndex = -1;

        Segment segment = new Segment();
        Document document = tc.getDocument();
        try {
            int documentLength = document.getLength();
            document.getText(0, documentLength > 0 ? documentLength - 1 : 0, segment);
        } catch (Throwable t) {
            Logger.getLogger(TextHighlighter.class.getName()).log(Level.SEVERE, null, t);
        }
        String text = segment.toString();
        int len = text.length();
        String currentWord = "";

        for (int i = offset; i < len; i++) {
            inWord = Character.isLetterOrDigit(text.charAt(i));

            if (inWord && (wordStartIndex == -1)) {
                wordStartIndex = i;
            } else if (!inWord) {
                wordStartIndex = -1;
            }

            if (inWord) {
                currentWord = text.substring(wordStartIndex, i + 1);
                currentWord = currentWord.toLowerCase();

                if (hlWords.contains(currentWord)) {
                    try {
                        highlighter.addHighlight(wordStartIndex, i + 1, painter);
                    } catch (Throwable t) {
                        Logger.getLogger(TextHighlighter.class.getName()).log(Level.SEVERE, null, t);
                    }
                }
            }
        }
    }
}
