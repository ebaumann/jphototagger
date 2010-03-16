/*
 * JPhotoTagger tags and finds images fast.
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package de.elmar_baumann.lib.componentutil;

import de.elmar_baumann.lib.util.CollectionUtil;

import java.awt.event.ActionEvent;

import java.io.Serializable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.InputMap;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

//Base code: http://java.sun.com/docs/books/tutorial/uiswing/examples/components/TextAreaDemoProject/src/components/TextAreaDemo.java
//           http://java.sun.com/docs/books/tutorial/uiswing/components/textarea.html

/**
 * Autocomplete for a <code>JTextArea</code>.
 *
 * Transfers the focus forward and backward on TAB or ENTER (Backward: Shift +
 * TAB or ENTER). If a completion is suggested, ENTER autocompletes (does not
 * transfer the focus).
 *
 * @author  Elmar Baumann
 * @version 2010-01-13
 */
public final class Autocomplete implements DocumentListener, Serializable {
    private static final String      COMMIT_ACTION               = "commit";
    private static final String      FOCUS_BACKWARD_ACTION       =
        "focus_backward";
    private static final String      FOCUS_FORWARD_ACTION        =
        "focus_forward";
    private static final int         MIN_CHARS                   = 2;
    private static final long        serialVersionUID            =
        7533238660594168356L;
    private final LinkedList<String> words                       =
        new LinkedList<String>();
    private volatile boolean         transferFocusForwardOnEnter = true;
    private Mode                     mode                        = Mode.INSERT;
    private JTextArea                textArea;

    private static enum Mode { INSERT, COMPLETION }

    ;
    public void decorate(JTextArea textArea, List<String> words,
                         boolean sorted) {
        if (textArea != this.textArea) {
            this.textArea = textArea;
            textArea.getDocument().addDocumentListener(this);
            registerKeyStrokes();
        }

        synchronized (this.words) {
            this.words.clear();
            this.words.addAll(words);
        }

        init(sorted);
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

    /**
     * Adds a new word for auto completion.
     *
     * Checks whether this word is already contained and adds it only if
     * unknown.
     *
     * @param word word
     */
    public void add(String word) {
        synchronized (words) {
            if (!contains(word)) {
                CollectionUtil.binaryInsert(words, word);
            }
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
        } catch (Exception e) {
            e.printStackTrace();
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

        synchronized (words) {
            int n = Collections.binarySearch(words, prefix);

            if ((n < 0) && (-n <= words.size())) {
                String match = words.get(-n - 1);

                if (match.startsWith(prefix)) {

                    // A completion is found
                    String completion = match.substring(pos - w);

                    // We cannot modify Document from within notification,
                    // so we submit a task that does the change later
                    SwingUtilities.invokeLater(new CompletionTask(completion,
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
        String completion;
        int    position;

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
        public void actionPerformed(ActionEvent e) {
            textArea.transferFocusBackward();
        }
    }


    private class FocusForwardAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            textArea.transferFocus();
        }
    }
}
