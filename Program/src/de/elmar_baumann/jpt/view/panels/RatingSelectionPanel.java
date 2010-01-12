/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.view.panels;

import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.data.TextEntry;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpRating;
import de.elmar_baumann.jpt.event.listener.TextEntryListener;
import de.elmar_baumann.jpt.event.listener.impl.TextEntryListenerSupport;
import de.elmar_baumann.jpt.resource.Bundle;
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
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;

/*
 * RatingSelectionPanel.java
 *
 * @author  Martin Pohlack  <martinp@gmx.de>
 * @version 2009-08-01
 */
public class RatingSelectionPanel
        extends    JPanel
        implements TextEntry, FocusListener, KeyListener {

    private static final long                     serialVersionUID           = -7955649305451645120L;
    private final        Icon                     star                       = AppLookAndFeel.getIcon("icon_xmp_rating_set.png");
    private final        Icon                     dark_star                  = AppLookAndFeel.getIcon("icon_xmp_rating_not_set.png");;
    private final        Icon                     icon_rating_remove         = AppLookAndFeel.getIcon("icon_xmp_rating_remove.png");
    private final        Icon                     icon_rating_remove_not_set = AppLookAndFeel.getIcon("icon_xmp_rating_remove_not_set.png");
    private final        Column                   column;
    private              boolean                  editable;
    private              boolean                  dirty                      = false;
    private              int                      value                      = 0;
    private              JButton                  buttons[]                  = new JButton[5];
    private              JButton                  lastClickedButton;
    private              TextEntryListenerSupport textEntryListenerSupport   = new TextEntryListenerSupport();
    private final        Map<JButton, String>     textOfButton               = new HashMap<JButton, String>();

    public RatingSelectionPanel(Column column) {
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
        textOfButton.put(buttonStar1   , "1");
        textOfButton.put(buttonStar2   , "2");
        textOfButton.put(buttonStar3   , "3");
        textOfButton.put(buttonStar4   , "4");
        textOfButton.put(buttonStar5   , "5");
        textOfButton.put(buttonNoRating, "" );
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
    }

    @Override
    public String getText() {
        if (value <= 0) {
            return null;
        } else {
            return String.valueOf(value);
        }
    }

    @Override
    public void empty(boolean dirty) {
        value = 0;
        this.dirty = dirty;
    }

    @Override
    public void setText(String text) {
        int val = 0;
        try {
            if (text != null && !text.trim().isEmpty()) {
                val = Integer.valueOf(text).intValue();
            }
        } catch (NumberFormatException e) {
            AppLog.logSevere(getClass(), e);
        }
        value = val;
        for (int i = 0; i < buttons.length; i++) {
            if (i >= val) {
                buttons[i].setIcon(dark_star);
            } else {
                buttons[i].setIcon(star);
            }
        }
        buttonNoRating.setIcon(value > 0
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

    @Override
    public TextEntry clone() {
        throw new UnsupportedOperationException("Not supported yet.");
        // fixme: do we really want to return this?
        //return new TextEntryContent(getText(), column);
    }

    public void addTextEntryListener(TextEntryListener listener) {
        textEntryListenerSupport.add(listener);
    }

    public void removeTextEntryListener(TextEntryListener listener) {
        textEntryListenerSupport.remove(listener);
    }

    private void notifyTextChanged(Column column, String oldText, String newText) {
        textEntryListenerSupport.notifyTextChanged(column, oldText, newText);
    }

    @Override
    public List<Component> getInputComponents() {
        return Arrays.asList(
                (Component)buttonNoRating,
                (Component)buttonStar1,
                (Component)buttonStar2,
                (Component)buttonStar3,
                (Component)buttonStar4,
                (Component)buttonStar5);
    }

    public void repeatLastClick() {
        if (lastClickedButton != null) {
            handleButtonPressed(lastClickedButton);
        }
    }

    @Override
    public synchronized void addMouseListenerToInputComponents(MouseListener l) {
        List<Component> inputComponents = getInputComponents();
        for (Component component : inputComponents) {
            component.addMouseListener(l);
        }
    }

    @Override
    public synchronized void removeMouseListenerFromInputComponents(MouseListener l) {
        List<Component> inputComponents = getInputComponents();
        for (Component component : inputComponents) {
            component.removeMouseListener(l);
        }
    }

    private void handleButtonPressed(JButton button) {

        assert textOfButton.containsKey(button);

        lastClickedButton = button;

        if (!editable) return;

        String oldVal = getText();

        setText(textOfButton.get(button));

        dirty = true;

        notifyTextChanged(column, oldVal, getText());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() != KeyEvent.VK_ENTER && e.getKeyCode() != KeyEvent.VK_SPACE) return;

        Object src = e.getSource();
        if (src instanceof JButton) {
            handleButtonPressed((JButton) src);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // ignore
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // ignore
    }

    // Visualize Focus
    @Override
    public void focusGained(FocusEvent e) {
        assert e.getSource() instanceof JButton : e.getSource();
        ((JButton)e.getSource()).setBorder(BorderFactory.createLineBorder(Color.GRAY));
    }

    // Visualize Focus
    @Override
    public void focusLost(FocusEvent e) {
        assert e.getSource() instanceof JButton : e.getSource();
        ((JButton)e.getSource()).setBorder(null);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
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

        labelPrompt.setText("Prompt:");
        labelPrompt.setToolTipText(column.getLongerDescription());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(labelPrompt, gridBagConstraints);

        buttonNoRating.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_xmp_rating_remove_not_set.png"))); // NOI18N
        buttonNoRating.setToolTipText(Bundle.getString("RatingSelectionPanel.buttonNoRating.toolTipText")); // NOI18N
        buttonNoRating.setBorder(null);
        buttonNoRating.setContentAreaFilled(false);
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

        buttonStar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_xmp_rating_not_set.png"))); // NOI18N
        buttonStar1.setToolTipText(Bundle.getString("RatingSelectionPanel.buttonStar1.toolTipText")); // NOI18N
        buttonStar1.setBorder(null);
        buttonStar1.setContentAreaFilled(false);
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

        buttonStar2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_xmp_rating_not_set.png"))); // NOI18N
        buttonStar2.setToolTipText(Bundle.getString("RatingSelectionPanel.buttonStar2.toolTipText")); // NOI18N
        buttonStar2.setBorder(null);
        buttonStar2.setContentAreaFilled(false);
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

        buttonStar3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_xmp_rating_not_set.png"))); // NOI18N
        buttonStar3.setToolTipText(Bundle.getString("RatingSelectionPanel.buttonStar3.toolTipText")); // NOI18N
        buttonStar3.setBorder(null);
        buttonStar3.setContentAreaFilled(false);
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

        buttonStar4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_xmp_rating_not_set.png"))); // NOI18N
        buttonStar4.setToolTipText(Bundle.getString("RatingSelectionPanel.buttonStar4.toolTipText")); // NOI18N
        buttonStar4.setBorder(null);
        buttonStar4.setContentAreaFilled(false);
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

        buttonStar5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_xmp_rating_not_set.png"))); // NOI18N
        buttonStar5.setToolTipText(Bundle.getString("RatingSelectionPanel.buttonStar5.toolTipText")); // NOI18N
        buttonStar5.setBorder(null);
        buttonStar5.setContentAreaFilled(false);
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
    }// </editor-fold>//GEN-END:initComponents

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
