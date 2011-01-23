package org.jphototagger.lib.componentutil;

import java.awt.EventQueue;
import org.jphototagger.lib.util.CollectionUtil;

import java.awt.event.ActionEvent;

import java.io.Serializable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.InputMap;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

//Base code: http://java.sun.com/docs/books/tutorial/uiswing/examples/components/TextAreaDemoProject/src/components/TextAreaDemo.java
//           http://java.sun.com/docs/books/tutorial/uiswing/components/textarea.html

/**
 * Autocomplete for a <code>JTextArea</code>.
 *
 * Transfers the focus forward and backward on TAB or ENTER (Backward: Shift +
 * TAB or ENTER). If a completion is suggested, ENTER autocompletes (does not
 * transfer the focus).
 *
 * @author Elmar Baumann
 */
public final class Autocomplete implements DocumentListener, Serializable {
    private static final String      COMMIT_ACTION               = "commit";
    private static final String      FOCUS_BACKWARD_ACTION = "focus_backward";
    private static final String      FOCUS_FORWARD_ACTION = "focus_forward";
    private static final int         MIN_CHARS                   = 2;
    private static final long        serialVersionUID = 7533238660594168356L;
    private final LinkedList<String> words = new LinkedList<String>();
    private volatile boolean         transferFocusForwardOnEnter = true;
    private Mode                     mode                        = Mode.INSERT;
    private JTextArea                textArea;
    private final boolean ignoreCase;

    private static enum Mode { INSERT, COMPLETION };

    public Autocomplete(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

    public void decorate(JTextArea textArea, List<String> words,
                         boolean sorted) {
        if (textArea == null) {
            throw new NullPointerException("textArea == null");
        }

        if (words == null) {
            throw new NullPointerException("words == null");
        }

        if (textArea != this.textArea) {
            this.textArea = textArea;
            textArea.getDocument().addDocumentListener(this);
            registerKeyStrokes();
        }

        Logger.getLogger(Autocomplete.class.getName()).log(Level.FINEST, 
                "Autocomplete: Will decorating text area named ''{0}'' with {1} new words...",
                new Object[]{textArea.getName(), words.size()});

        synchronized (this.words) {
            this.words.clear();
            if (ignoreCase) {
                for (String word : words) {
                    this.words.add(word.toLowerCase());
                }
            } else {
                this.words.addAll(words);
            }
        }

        Logger.getLogger(Autocomplete.class.getName()).log(Level.FINEST,
                "Autocomplete: Decorated text area named ''{0}'' with {1} new words...",
                new Object[]{textArea.getName(), words.size()});

        init(sorted);
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    /**
     * Sets wether to transfer focus forward on Enter.
     *
     * @param transfer transferring focus forward on Enter key. Default: true.
     */
    public void setTransferFocusForward(boolean transfer) {
        this.transferFocusForwardOnEnter = transfer;
    }

    private void init(boolean sorted) {
        if (!sorted) {

            // Binary search requires natural sort order
            synchronized (words) {
                Collections.sort(words);
            }
        }
    }

    private void registerKeyStrokes() {
        InputMap  im = textArea.getInputMap();
        ActionMap am = textArea.getActionMap();

        am.put(COMMIT_ACTION, new CommitAction());
        am.put(FOCUS_FORWARD_ACTION, new FocusForwardAction());
        am.put(FOCUS_BACKWARD_ACTION, new FocusBackwardAction());
        im.put(KeyStroke.getKeyStroke("ENTER"), COMMIT_ACTION);
        im.put(KeyStroke.getKeyStroke("shift ENTER"), FOCUS_BACKWARD_ACTION);
        im.put(KeyStroke.getKeyStroke("TAB"), FOCUS_FORWARD_ACTION);
        im.put(KeyStroke.getKeyStroke("shift TAB"), FOCUS_BACKWARD_ACTION);
    }

    public void add(String word) {
        if (word == null) {
            throw new NullPointerException("word == null");
        }

        synchronized (words) {
            if (!contains(word)) {
                String lcWord = ignoreCase ? word.toLowerCase() : word;

                CollectionUtil.binaryInsert(words, lcWord);
            }
        }
    }

    public boolean contains(String word) {
        if (word == null) {
            throw new NullPointerException("word == null");
        }

        synchronized (words) {
            String lcWord = ignoreCase ? word.toLowerCase() : word;

            return Collections.binarySearch(words, lcWord) >= 0;
        }
    }

    @Override
    public void changedUpdate(DocumentEvent ev) {}

    @Override
    public void removeUpdate(DocumentEvent ev) {}

    @Override
    public void insertUpdate(DocumentEvent ev) {
        if (ev.getLength() != 1) {
            return;
        }

        int    pos     = ev.getOffset();
        String content = null;

        try {
            content = textArea.getText(0, pos + 1);
        } catch (Exception ex) {
            Logger.getLogger(Autocomplete.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Find where the word starts
        int w;

        for (w = pos; w >= 0; w--) {
            if (!Character.isLetter(content.charAt(w))) {
                break;
            }
        }

        if (pos - w < MIN_CHARS) {

            // Too few chars
            return;
        }

        String prefix = content.substring(w + 1);

        if (ignoreCase) {
            prefix = prefix.toLowerCase();
        }

        synchronized (words) {
            int n = Collections.binarySearch(words, prefix);

            if ((n < 0) && (-n <= words.size())) {
                String match = words.get(-n - 1);

                if (match.startsWith(prefix)) {

                    // A completion is found
                    String completion = match.substring(pos - w);

                    // We cannot modify Document from within notification,
                    // so we submit a task that does the change later
                    EventQueue.invokeLater(new CompletionTask(completion,
                            pos + 1));
                }
            } else {

                // Nothing found
                mode = Mode.INSERT;
            }
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

                // textArea.replaceSelection("\n");
                if (transferFocusForwardOnEnter) {
                    textArea.transferFocus();
                }
            }
        }
    }


    private class CompletionTask implements Runnable {
        private String completion;
        private int    position;

        CompletionTask(String completion, int position) {
            this.completion = completion;
            this.position   = position;
        }

        @Override
        public void run() {
            textArea.insert(completion, position);
            textArea.setCaretPosition(position + completion.length());
            textArea.moveCaretPosition(position);
            mode = Mode.COMPLETION;
        }
    }


    private class FocusBackwardAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent evt) {
            textArea.transferFocusBackward();
        }
    }


    private class FocusForwardAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent evt) {
            textArea.transferFocus();
        }
    }
}
