package de.elmar_baumann.lib.componentutil;

import de.elmar_baumann.lib.util.CollectionUtil;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

// Base code: http://java.sun.com/docs/books/tutorial/uiswing/examples/components/TextAreaDemoProject/src/components/TextAreaDemo.java
//            http://java.sun.com/docs/books/tutorial/uiswing/components/textarea.html

/**
 * Autocomplete for a <code>JTextArea</code>.
 *
 * Transfers the focus forward and backward on TAB or ENTER (Backward: Shift +
 * TAB or ENTER). If a completion is suggested, ENTER autocompletes (does not
 * transfer the focus).
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-13
 */
public final class Autocomplete implements DocumentListener  {

    private              JTextArea          textArea;
    private static final String             COMMIT_ACTION         = "commit";
    private static final String             FOCUS_FORWARD_ACTION  = "focus_forward";
    private static final String             FOCUS_BACKWARD_ACTION = "focus_backward";
    private static final int                MIN_CHARS             = 2;
    private final        LinkedList<String> words                 = new LinkedList<String>();
    private              Mode               mode                  = Mode.INSERT;
    private volatile     boolean            transferFocusForwardOnEnter  = true;

    private static enum Mode { INSERT, COMPLETION };

    public void decorate(JTextArea textArea, Collection<String> words) {
        if (textArea != this.textArea) {
            this.textArea = textArea;
            textArea.getDocument().addDocumentListener(this);
            registerKeyStrokes();
        }
        synchronized (this.words) {
            this.words.clear();
            this.words.addAll(words);
        }
        init();
    }

    /**
     * Adds a new word for auto completion.
     *
     * Checks whether this word is already contained and adds it only if
     * unknown.
     *
     * @param word word
     */
    public void add(String word) {
        String newWord = word.trim().toLowerCase();

        synchronized (words) {
            if (contains(newWord)) return;
            CollectionUtil.binaryInsert(words, newWord);
        }
    }

    /**
     * Returns wether autocomplete contains a specific word.
     *
     * @param  word word
     * @return      true if the word is known
     */
    public boolean contains(String word) {
        synchronized (words) {
            return Collections.binarySearch(words, word) >= 0;
        }
    }

    /**
     * Sets wether to transfer focus forward on Enter.
     *
     * @param transfer transferring focus forward on Enter key. Default: true.
     */
    public void setTransferFocusForward(boolean transfer) {
        this.transferFocusForwardOnEnter = transfer;
    }

    private void init() {
        wordsToLowerCase();
    }
    
    private void registerKeyStrokes() {
        InputMap  im  = textArea.getInputMap();
        ActionMap am  = textArea.getActionMap();

        am.put(COMMIT_ACTION        , new CommitAction());
        am.put(FOCUS_FORWARD_ACTION , new FocusForwardAction());
        am.put(FOCUS_BACKWARD_ACTION, new FocusBackwardAction());

        im.put(KeyStroke.getKeyStroke("ENTER")      , COMMIT_ACTION);
        im.put(KeyStroke.getKeyStroke("shift ENTER"), FOCUS_BACKWARD_ACTION);
        im.put(KeyStroke.getKeyStroke("TAB")        , FOCUS_FORWARD_ACTION);
        im.put(KeyStroke.getKeyStroke("shift TAB")  , FOCUS_BACKWARD_ACTION);
    }

    private void wordsToLowerCase() {
        synchronized (this.words) {
            int count = words.size();
            for (int i = 0; i < count; i++) {
                words.set(i, words.get(i).toLowerCase()); // Input converted to lowercase
            }
            Collections.sort(words); // Binary search requires natural sort order
        }
    }

    @Override
    public void changedUpdate(DocumentEvent ev) {
    }

    @Override
    public void removeUpdate(DocumentEvent ev) {
    }

    @Override
    public void insertUpdate(DocumentEvent ev) {
        if (ev.getLength() != 1) {
            return;
        }

        int pos = ev.getOffset();
        String content = null;
        try {
            content = textArea.getText(0, pos + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Find where the word starts
        int w;
        for (w = pos; w >= 0; w--) {
            if (! Character.isLetter(content.charAt(w))) {
                break;
            }
        }
        if (pos - w < MIN_CHARS) {
            // Too few chars
            return;
        }

        String prefix = content.substring(w + 1).toLowerCase();
        synchronized (words) {
            int n = Collections.binarySearch(words, prefix);
            if (n < 0 && -n <= words.size()) {
                String match = words.get(-n - 1);
                if (match.startsWith(prefix)) {
                    // A completion is found
                    String completion = match.substring(pos - w);
                    // We cannot modify Document from within notification,
                    // so we submit a task that does the change later
                    SwingUtilities.invokeLater(
                            new CompletionTask(completion, pos + 1));
                }
            } else {
                // Nothing found
                mode = Mode.INSERT;
            }
        }
    }

    private class CompletionTask implements Runnable {
        String completion;
        int position;

        CompletionTask(String completion, int position) {
            this.completion = completion;
            this.position = position;
        }

        @Override
        public void run() {
            textArea.insert(completion, position);
            textArea.setCaretPosition(position + completion.length());
            textArea.moveCaretPosition(position);
            mode = Mode.COMPLETION;
        }
    }

    private class CommitAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent ev) {
            if (mode == Mode.COMPLETION) {
                int pos = textArea.getSelectionEnd();
                textArea.insert(" ", pos);
                textArea.setCaretPosition(pos + 1);
                mode = Mode.INSERT;
            } else {
                //textArea.replaceSelection("\n");
                if (transferFocusForwardOnEnter) {
                    textArea.transferFocus();
                }
            }
        }
    }

    private class FocusForwardAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            textArea.transferFocus();
        }

    }

    private class FocusBackwardAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            textArea.transferFocusBackward();
        }

    }
}
