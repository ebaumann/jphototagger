package de.elmar_baumann.imv.view.panels;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.event.TextSelectionEvent;
import de.elmar_baumann.imv.event.listener.TextSelectionListener;
import de.elmar_baumann.lib.util.Settings;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.Border;

/**
 * Panel offering text to select via text elements.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/26
 */
public class TextSelectionPanel extends javax.swing.JPanel {

    private final int textElementCount;
    private static final String KEY_PREFIX_BUTTONS =
            TextSelectionPanel.class.getName() + "."; // NOI18N
    private static final String MNEMONIC_START = " ["; // NOI18N
    private static final String MNEMONIC_END = "]"; // NOI18N
    private static final Border BORDER_BUTTONS = BorderFactory.
            createEtchedBorder();
    private final List<JButton> buttons = new ArrayList<JButton>();
    private final Set<TextSelectionListener> listeners =
            Collections.synchronizedSet(new HashSet<TextSelectionListener>());

    /**
     * Creates a panel with 9 text elements.
     */
    public TextSelectionPanel() {
        textElementCount = 9;
        initComponents();
        addButtons();
    }

    /**
     * Creates a new panel with n text elements where n = columnCount *
     * rowCount.
     *
     * @param  textElementCount         count of text elements. Range: 1 - 26
     * @throws IllegalArgumentException when text element count is not between
     *                                  1 - 26
     */
    public TextSelectionPanel(int textElementCount) {
        if (textElementCount <= 0 || textElementCount > 26)
            throw new IllegalArgumentException(
                    "Text element count has to be between 1 - 26 but it is " + // NOI18N
                    textElementCount);
        this.textElementCount = textElementCount;
        initComponents();
        addButtons();
    }

    private void addButtons() {
        for (int i = 0; i < textElementCount; i++) {
            JButton button = getButton(i);
            buttons.add(button);
            add(button);
        }
    }

    private JButton getButton(int index) {
        JButton button = new JButton(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                textSelected(e);
            }
        });
        formatButton(button);
        char mnemnonic = String.valueOf(Character.toChars(index + 65)).charAt(0);
        button.setText(MNEMONIC_START + mnemnonic + MNEMONIC_END);
        button.setMnemonic(mnemnonic);
        readProperties(button, index);
        return button;
    }

    private void readProperties(JButton button, int index) {
        Settings settings = UserSettings.INSTANCE.getSettings();
        String key = KEY_PREFIX_BUTTONS + index;
        String text = settings.getString(key).trim();
        if (!text.isEmpty()) {
            setButtonText(button, text);
        }
    }

    private void setButtonText(JButton button, String text) {
        int indexMnemnonic = button.getText().lastIndexOf(MNEMONIC_START);
        if (indexMnemnonic >= 0) {
            String mnemnonic = button.getText().substring(indexMnemnonic);
            button.setText(text + mnemnonic);
        }
    }

    private String getButtonText(JButton button) {
        String text = button.getText();
        int indexMnemnonic = text.lastIndexOf(MNEMONIC_START);
        if (indexMnemnonic > 0) {
            return text.substring(0, indexMnemnonic);
        }
        return ""; // NOI18N
    }

    public void writeProperties() {
        Settings settings = UserSettings.INSTANCE.getSettings();
        int buttonCount = buttons.size();
        for (int i = 0; i < buttonCount; i++) {
            String text = getButtonText(buttons.get(i));
            if (!text.isEmpty()) {
                settings.setString(text, KEY_PREFIX_BUTTONS + i);
            }
        }
    }

    private void formatButton(JButton button) {
        button.setBorder(BORDER_BUTTONS);
    }

    private void textSelected(ActionEvent e) {
        if (e.getSource() instanceof JButton) {
            String text = getButtonText((JButton) e.getSource());
            if (!text.isEmpty()) {
                notifyListeners(text);
            }
        }
    }

    /**
     * Sets a text to the text element. Begins at the first element. When the
     * last element is reached, the text will be set to the first element and so
     * on.
     *
     * @param text text
     */
    public void setText(String text) {
        String trimmedText = text.trim();
        if (!existsText(trimmedText)) {
            shiftRightTexts();
            setButtonText(buttons.get(0), trimmedText);
        }
    }

    private void shiftRightTexts() {
        int buttonCount = buttons.size();
        for (int i = buttonCount - 1; i >= 1; i--) {
            setButtonText(buttons.get(i), getButtonText(buttons.get(i - 1)));
        }
    }

    boolean existsText(String text) {
        for (JButton button : buttons) {
            if (getButtonText(button).equals(text)) return true;
        }
        return false;
    }

    /**
     * Adds a listener that gets text when selected.
     *
     * @param listener listener
     */
    public void addTextSelectionListener(TextSelectionListener listener) {
        listeners.add(listener);
    }

    public void removeTextSelectionListener(TextSelectionListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(String text) {
        TextSelectionEvent evt = new TextSelectionEvent(text);
        synchronized (listeners) {
            for (TextSelectionListener listener : listeners) {
                listener.textSelected(evt);
            }
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
