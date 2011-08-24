package org.jphototagger.program.view.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.xmp.ColumnXmpRating;
import org.jphototagger.domain.event.listener.TextEntryListener;
import org.jphototagger.domain.event.listener.TextEntryListenerSupport;
import org.jphototagger.domain.text.TextEntry;
import org.jphototagger.program.app.AppLookAndFeel;

/*
* RatingSelectionPanel.java
*
* @author  Martin Pohlack
 */
public class RatingSelectionPanel extends JPanel
        implements TextEntry, FocusListener, KeyListener {
    private static final long serialVersionUID = -7955649305451645120L;
    private final Icon star = AppLookAndFeel.getIcon("icon_xmp_rating_set.png");
    private final Icon dark_star = AppLookAndFeel.getIcon("icon_xmp_rating_not_set.png");;
    private final Icon icon_rating_remove = AppLookAndFeel.getIcon("icon_xmp_rating_remove.png");
    private final Icon icon_rating_remove_not_set = AppLookAndFeel.getIcon("icon_xmp_rating_remove_not_set.png");
    private final transient Column column;
    private boolean editable;
    private boolean dirty = false;
    private int value = 0;
    private JButton buttons[] = new JButton[5];
    private JButton lastClickedButton;
    private transient TextEntryListenerSupport textEntryListenerSupport = new TextEntryListenerSupport();
    private final Map<JButton, String> textOfButton = new HashMap<JButton, String>();

    public RatingSelectionPanel(Column column) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        this.column = column;
        init();
    }

    public RatingSelectionPanel() {
        this.column = ColumnXmpRating.INSTANCE;
        init();
    }

    private void init() {
        initComponents();
        buttons[0] = buttonStar1;
        buttons[1] = buttonStar2;
        buttons[2] = buttonStar3;
        buttons[3] = buttonStar4;
        buttons[4] = buttonStar5;
        setPropmt();
        setTextOfButtonMap();
        listenToButtons();
    }

    private void setTextOfButtonMap() {
        textOfButton.put(buttonStar1, "1");
        textOfButton.put(buttonStar2, "2");
        textOfButton.put(buttonStar3, "3");
        textOfButton.put(buttonStar4, "4");
        textOfButton.put(buttonStar5, "5");
        textOfButton.put(buttonNoRating, "");
    }

    private void listenToButtons() {
        for (JButton button : buttons) {
            button.addFocusListener(this);
            button.addKeyListener(this);
        }

        buttonNoRating.addFocusListener(this);
        buttonNoRating.addKeyListener(this);
    }

    private void setPropmt() {
        labelPrompt.setText(column.getDescription());
        labelPrompt.setLabelFor(buttonNoRating);
    }

    @Override
    public String getText() {
        if (value < 0) {
            return "";
        } else {
            return String.valueOf(value);
        }
    }

    @Override
    public void empty(boolean dirty) {
        setText("0");
        this.dirty = dirty;
    }

    @Override
    public void setText(String text) {
        if (text == null) {
            throw new NullPointerException("text == null");
        }

        int val = 0;

        try {
            if (!text.trim().isEmpty()) {
                val = Integer.valueOf(text).intValue();
            }
        } catch (Exception ex) {
            Logger.getLogger(RatingSelectionPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        value = val;

        for (int i = 0; i < buttons.length; i++) {
            if (i >= val) {
                buttons[i].setIcon(dark_star);
            } else {
                buttons[i].setIcon(star);
            }
        }

        buttonNoRating.setIcon((value > 0)
                               ? icon_rating_remove_not_set
                               : icon_rating_remove);
        dirty = false;
    }

    /**
     * Sets the text and notifies change listener. Also sets the dirty flag.
     *
     * @param text text
     */
    public void setTextAndNotify(String text) {
        if (text == null) {
            throw new NullPointerException("text == null");
        }

        String oldText = getText();

        setText(text);
        dirty = true;
        notifyTextChanged(column, oldText, text);
    }

    @Override
    public Column getColumn() {
        return column;
    }

    @Override
    public void focus() {
        buttonNoRating.requestFocus();
    }

    @Override
    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    @Override
    public boolean isEmpty() {
        return value == 0;
    }

    @Override
    public void setAutocomplete() {
        return;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public void addTextEntryListener(TextEntryListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        textEntryListenerSupport.add(listener);
    }

    public void removeTextEntryListener(TextEntryListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        textEntryListenerSupport.remove(listener);
    }

    private void notifyTextChanged(Column column, String oldText,
                                   String newText) {
        textEntryListenerSupport.notifyTextChanged(column, oldText, newText);
    }

    @Override
    public List<Component> getInputComponents() {
        return Arrays.asList((Component) buttonNoRating,
                             (Component) buttonStar1, (Component) buttonStar2,
                             (Component) buttonStar3, (Component) buttonStar4,
                             (Component) buttonStar5);
    }

    public void repeatLastClick() {
        if (lastClickedButton != null) {
            handleButtonPressed(lastClickedButton);
        }
    }

    @Override
    public synchronized void addMouseListenerToInputComponents(
            MouseListener l) {
        if (l == null) {
            throw new NullPointerException("l == null");
        }

        List<Component> inputComponents = getInputComponents();

        for (Component component : inputComponents) {
            component.addMouseListener(l);
        }
    }

    @Override
    public synchronized void removeMouseListenerFromInputComponents(
            MouseListener l) {
        if (l == null) {
            throw new NullPointerException("l == null");
        }

        List<Component> inputComponents = getInputComponents();

        for (Component component : inputComponents) {
            component.removeMouseListener(l);
        }
    }

    private void handleButtonPressed(JButton button) {
        assert textOfButton.containsKey(button);
        lastClickedButton = button;

        if (!editable) {
            return;
        }

        String oldVal = getText();

        setText(textOfButton.get(button));
        dirty = true;
        notifyTextChanged(column, oldVal, getText());
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        if ((evt.getKeyCode() != KeyEvent.VK_ENTER) && (evt.getKeyCode() != KeyEvent.VK_SPACE)) {
            return;
        }

        Object src = evt.getSource();

        if (src instanceof JButton) {
            handleButtonPressed((JButton) src);
        }
    }

    @Override
    public void keyTyped(KeyEvent evt) {

        // ignore
    }

    @Override
    public void keyReleased(KeyEvent evt) {

        // ignore
    }

    // Visualize Focus
    @Override
    public void focusGained(FocusEvent evt) {
        boolean isButton = evt.getSource() instanceof JButton;

        if (!isButton) {
            throw new IllegalArgumentException("Not ab button: " + evt.getSource());
        }

        ((JButton) evt.getSource()).setBorder(BorderFactory.createLineBorder(Color.GRAY));
    }

    // Visualize Focus
    @Override
    public void focusLost(FocusEvent evt) {
        assert evt.getSource() instanceof JButton : evt.getSource();
        ((JButton) evt.getSource()).setBorder(null);
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        labelPrompt = new javax.swing.JLabel();
        buttonNoRating = new javax.swing.JButton();
        buttonStar1 = new javax.swing.JButton();
        buttonStar2 = new javax.swing.JButton();
        buttonStar3 = new javax.swing.JButton();
        buttonStar4 = new javax.swing.JButton();
        buttonStar5 = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(102, 32));
        setName("Rating Selection Panel"); // NOI18N
        setPreferredSize(new java.awt.Dimension(102, 32));
        setLayout(new java.awt.GridBagLayout());

        labelPrompt.setText("Prompt:"); // NOI18N
        labelPrompt.setToolTipText(column.getLongerDescription());
        labelPrompt.setName("labelPrompt"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(labelPrompt, gridBagConstraints);

        buttonNoRating.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_xmp_rating_remove_not_set.png"))); // NOI18N
        buttonNoRating.setMnemonic('0');
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/view/panels/Bundle"); // NOI18N
        buttonNoRating.setToolTipText(bundle.getString("RatingSelectionPanel.buttonNoRating.toolTipText")); // NOI18N
        buttonNoRating.setBorder(null);
        buttonNoRating.setContentAreaFilled(false);
        buttonNoRating.setName("buttonNoRating"); // NOI18N
        buttonNoRating.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                buttonNoRatingMousePressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 1, 6);
        add(buttonNoRating, gridBagConstraints);

        buttonStar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_xmp_rating_not_set.png"))); // NOI18N
        buttonStar1.setMnemonic('1');
        buttonStar1.setToolTipText(bundle.getString("RatingSelectionPanel.buttonStar1.toolTipText")); // NOI18N
        buttonStar1.setBorder(null);
        buttonStar1.setContentAreaFilled(false);
        buttonStar1.setName("buttonStar1"); // NOI18N
        buttonStar1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                buttonStar1MousePressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 1, 0);
        add(buttonStar1, gridBagConstraints);

        buttonStar2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_xmp_rating_not_set.png"))); // NOI18N
        buttonStar2.setMnemonic('2');
        buttonStar2.setToolTipText(bundle.getString("RatingSelectionPanel.buttonStar2.toolTipText")); // NOI18N
        buttonStar2.setBorder(null);
        buttonStar2.setContentAreaFilled(false);
        buttonStar2.setName("buttonStar2"); // NOI18N
        buttonStar2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                buttonStar2MousePressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 1, 0);
        add(buttonStar2, gridBagConstraints);

        buttonStar3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_xmp_rating_not_set.png"))); // NOI18N
        buttonStar3.setMnemonic('3');
        buttonStar3.setToolTipText(bundle.getString("RatingSelectionPanel.buttonStar3.toolTipText")); // NOI18N
        buttonStar3.setBorder(null);
        buttonStar3.setContentAreaFilled(false);
        buttonStar3.setName("buttonStar3"); // NOI18N
        buttonStar3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                buttonStar3MousePressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 1, 0);
        add(buttonStar3, gridBagConstraints);

        buttonStar4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_xmp_rating_not_set.png"))); // NOI18N
        buttonStar4.setMnemonic('4');
        buttonStar4.setToolTipText(bundle.getString("RatingSelectionPanel.buttonStar4.toolTipText")); // NOI18N
        buttonStar4.setBorder(null);
        buttonStar4.setContentAreaFilled(false);
        buttonStar4.setName("buttonStar4"); // NOI18N
        buttonStar4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                buttonStar4MousePressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 1, 0);
        add(buttonStar4, gridBagConstraints);

        buttonStar5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_xmp_rating_not_set.png"))); // NOI18N
        buttonStar5.setMnemonic('5');
        buttonStar5.setToolTipText(bundle.getString("RatingSelectionPanel.buttonStar5.toolTipText")); // NOI18N
        buttonStar5.setBorder(null);
        buttonStar5.setContentAreaFilled(false);
        buttonStar5.setName("buttonStar5"); // NOI18N
        buttonStar5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                buttonStar5MousePressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 1, 0);
        add(buttonStar5, gridBagConstraints);
    }//GEN-END:initComponents

    private void buttonNoRatingMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonNoRatingMousePressed
        handleButtonPressed((JButton) evt.getSource());
    }//GEN-LAST:event_buttonNoRatingMousePressed

    private void buttonStar1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonStar1MousePressed
        handleButtonPressed((JButton) evt.getSource());
    }//GEN-LAST:event_buttonStar1MousePressed

    private void buttonStar2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonStar2MousePressed
        handleButtonPressed((JButton) evt.getSource());
    }//GEN-LAST:event_buttonStar2MousePressed

    private void buttonStar3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonStar3MousePressed
        handleButtonPressed((JButton) evt.getSource());
    }//GEN-LAST:event_buttonStar3MousePressed

    private void buttonStar4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonStar4MousePressed
        handleButtonPressed((JButton) evt.getSource());
    }//GEN-LAST:event_buttonStar4MousePressed

    private void buttonStar5MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonStar5MousePressed
        handleButtonPressed((JButton) evt.getSource());
    }//GEN-LAST:event_buttonStar5MousePressed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonNoRating;
    private javax.swing.JButton buttonStar1;
    private javax.swing.JButton buttonStar2;
    private javax.swing.JButton buttonStar3;
    private javax.swing.JButton buttonStar4;
    private javax.swing.JButton buttonStar5;
    private javax.swing.JLabel labelPrompt;
    // End of variables declaration//GEN-END:variables
}
