package org.jphototagger.lib.swing;

import java.awt.Color;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import javax.swing.text.JTextComponent;
import org.jphototagger.lib.swing.util.MnemonicUtil;

/**
 * Enhances a {@code JTextComponent} with search capabilities.
 *
 * @author Elmar Baumann
 */
public class TextComponentSearchPanel extends javax.swing.JPanel implements DocumentListener {

    private static final long serialVersionUID = 1L;
    private JTextComponent textComponent;
    private static final Color ERROR_BG = Color.RED;
    private final Color searchTextFieldBackground;
    private final transient Highlighter highlighter = new DefaultHighlighter();
    private final transient Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
    private final List<Integer> foundIndices = new ArrayList<Integer>();
    private int currentFoundIndex = -1;

    public TextComponentSearchPanel() {
        initComponents();
        MnemonicUtil.setMnemonics((Container) this);
        searchTextFieldBackground = searchTextField.getBackground();
        listen();
    }

    private void listen() {
        searchTextField.getDocument().addDocumentListener(this);
    }

    public void setSearchableTextComponent(JTextComponent textComponent) {
        if (textComponent == null) {
            throw new NullPointerException("textComponent == null");
        }

        this.textComponent = textComponent;
        textComponent.setHighlighter(highlighter);
    }

    public void requestFocusToSearchTextField() {
        searchTextField.requestFocusInWindow();
    }

    private int search(int startIndex) {
        String searchText = searchTextField.getText().toLowerCase();
        if (textComponent == null || searchText.isEmpty()) {
            return -1;
        }

        highlighter.removeAllHighlights();

        String text = textComponent.getText().toLowerCase();
        int index = text.indexOf(searchText, startIndex);

        synchronized (foundIndices) {
            if (index >= startIndex) {
                try {
                    int end = index + searchText.length();
                    highlighter.addHighlight(index, end, painter);
                    textComponent.setCaretPosition(end);
                    searchTextField.setBackground(searchTextFieldBackground);
                    return index;
                } catch (BadLocationException ex) {
                    Logger.getLogger(TextComponentSearchPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (foundIndices.isEmpty()) {
                searchTextField.setBackground(ERROR_BG);
            }
        }

        return -1;
    }

    @Override
    public void insertUpdate(DocumentEvent evt) {
        searchFromTextStart();
    }

    @Override
    public void removeUpdate(DocumentEvent evt) {
        searchFromTextStart();
    }

    @Override
    public void changedUpdate(DocumentEvent evt) {
        // ignore
    }

    @Override
    public void setEnabled(boolean enabled) {
        searchTextField.setEnabled(enabled);
        searchTextField.setEditable(enabled);

        if (!enabled) {
            buttonSearchUpwards.setEnabled(false);
            buttonSearchDownwards.setEnabled(false);
        }
        super.setEnabled(enabled);
    }

    private void searchFromTextStart() {
        int foundIndex = search(0);
        boolean found = foundIndex >= 0;

        synchronized (foundIndices) {
            foundIndices.clear();

            if (found) {
                currentFoundIndex = foundIndex;

                if (!foundIndices.contains(foundIndex)) {
                    foundIndices.add(foundIndex);
                }
            }
        }

        buttonSearchDownwards.setEnabled(found);
        buttonSearchUpwards.setEnabled(false);
    }

    private void searchDown() {
        if (textComponent == null) {
            return;
        }

        int foundIndex = search(currentFoundIndex + 1);
        boolean found = foundIndex > 0;

        synchronized (foundIndices) {
            if (found) {
                currentFoundIndex = foundIndex;

                if (!foundIndices.contains(foundIndex)) {
                    foundIndices.add(foundIndex);
                }
            }

            buttonSearchDownwards.setEnabled(found);
            buttonSearchUpwards.setEnabled(found || !found && !foundIndices.isEmpty());
        }
    }

    private void searchUp() {
        synchronized (foundIndices) {
            if (textComponent == null || foundIndices.isEmpty()) {
                return;
            }

            int indexCurFoundIndex = foundIndices.indexOf(currentFoundIndex);
            int prevFoundIndex = -1;

            if (indexCurFoundIndex > 0) {
                prevFoundIndex = search(foundIndices.get(indexCurFoundIndex - 1));
                boolean found = prevFoundIndex >= 0;

                if (found) {
                    currentFoundIndex = prevFoundIndex;
                }

                buttonSearchUpwards.setEnabled(foundIndices.indexOf(prevFoundIndex) > 0);
                buttonSearchDownwards.setEnabled(found);
            }
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents

        label = new javax.swing.JLabel();
        searchTextField = new javax.swing.JTextField();
        buttonSearchDownwards = new javax.swing.JButton();
        buttonSearchUpwards = new javax.swing.JButton();

        setName("Form"); // NOI18N

        label.setLabelFor(searchTextField);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/lib/swing/Bundle"); // NOI18N
        label.setText(bundle.getString("TextComponentSearchPanel.label.text")); // NOI18N
        label.setName("label"); // NOI18N

        searchTextField.setColumns(10);
        searchTextField.setName("searchTextField"); // NOI18N

        buttonSearchDownwards.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/lib/resource/icons/icon_arrow_down.png"))); // NOI18N
        buttonSearchDownwards.setText(bundle.getString("TextComponentSearchPanel.buttonSearchDownwards.text")); // NOI18N
        buttonSearchDownwards.setEnabled(false);
        buttonSearchDownwards.setName("buttonSearchDownwards"); // NOI18N
        buttonSearchDownwards.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSearchDownwardsActionPerformed(evt);
            }
        });

        buttonSearchUpwards.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/lib/resource/icons/icon_arrow_up.png"))); // NOI18N
        buttonSearchUpwards.setText(bundle.getString("TextComponentSearchPanel.buttonSearchUpwards.text")); // NOI18N
        buttonSearchUpwards.setEnabled(false);
        buttonSearchUpwards.setName("buttonSearchUpwards"); // NOI18N
        buttonSearchUpwards.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSearchUpwardsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonSearchDownwards)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonSearchUpwards))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {buttonSearchDownwards, buttonSearchUpwards});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(label)
                .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(buttonSearchDownwards)
                .addComponent(buttonSearchUpwards))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {buttonSearchDownwards, buttonSearchUpwards});

    }//GEN-END:initComponents

    private void buttonSearchDownwardsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSearchDownwardsActionPerformed
        searchDown();
    }//GEN-LAST:event_buttonSearchDownwardsActionPerformed

    private void buttonSearchUpwardsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSearchUpwardsActionPerformed
        searchUp();
    }//GEN-LAST:event_buttonSearchUpwardsActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonSearchDownwards;
    private javax.swing.JButton buttonSearchUpwards;
    private javax.swing.JLabel label;
    private javax.swing.JTextField searchTextField;
    // End of variables declaration//GEN-END:variables
}
