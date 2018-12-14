package org.jphototagger.lib.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.Lookup;

/**
 * Contains a button which expands or collapses a component. If the component
 * has a {@code javax.swing.JLabel} the label's text is shown when collapsed.
 *
 * @author Elmar Baumann
 */
public class ExpandCollapseComponentPanel extends JPanel implements FocusListener {

    private static final long serialVersionUID = 1L;
    private static final ImageIcon ICON_EXPAND = org.jphototagger.resources.Icons.getIcon("icon_expand.png");
    private static final ImageIcon ICON_COLLAPSE = org.jphototagger.resources.Icons.getIcon("icon_collapse.png");
    private static final String TOOLTIP_TEXT_EXPAND = Bundle.getString(ExpandCollapseComponentPanel.class, "ExpandCollapseComponentPanel.TooltipTextExpand");
    private static final String TOOLTIP_TEXT_COLLAPSE = Bundle.getString(ExpandCollapseComponentPanel.class, "ExpandCollapseComponentPanel.TooltipTextCollapse");
    private String keyPersistence  = "";
    private final Component component;
    private JLabel labelFill;
    private String fillText;
    private Font fillFont;
    private boolean expanded = true;

    public ExpandCollapseComponentPanel(Component component) {
        if (component == null) {
            throw new NullPointerException("component == null");
        }
        this.component = component;
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        createFillLabel();
        addAsFocusListener();
        setPersistenceKey();
        addComponent(component);
        decorateButton();
        Dimension buttonSize = buttonExpandCollapse.getPreferredSize();
        buttonSize = org.jphototagger.resources.UiFactory.dimension((int) (buttonSize.width * 0.66), (int) (buttonSize.height * 0.66));
        buttonExpandCollapse.setPreferredSize(buttonSize);
    }

    private void addComponent(Component c) {
        add(c, createComponentConstraints());
    }

    private GridBagConstraints createComponentConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        return gbc;
    }

    private void collapseComponent() {
        remove(component);
        addComponent(labelFill);
        decorateButton();
        redraw();
    }

    private void expandComponent() {
        remove(labelFill);
        addComponent(component);
        decorateButton();
        redraw();
    }

    private void decorateButton() {
        buttonExpandCollapse.setIcon(expanded
                                     ? ICON_COLLAPSE
                                     : ICON_EXPAND);
        buttonExpandCollapse.setToolTipText(expanded
                ? TOOLTIP_TEXT_COLLAPSE
                : TOOLTIP_TEXT_EXPAND);
        buttonExpandCollapse.setMnemonic(expanded
                                         ? '-'
                                         : '+');
    }

    private void setPersistenceKey() {
        String labelFillText = labelFill.getText().trim();
        assert !labelFillText.isEmpty();
        if (!labelFillText.isEmpty()) {
            keyPersistence = ExpandCollapseComponentPanel.class.getCanonicalName() + labelFillText;
        }
    }

    private void writeExpandedState() {
        if (!keyPersistence.isEmpty()) {
            Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
            prefs.setBoolean(keyPersistence, expanded);
        }
    }

    /**
     * Reads the persistent written expanded state and expands or collapses.
     */
    public void readExpandedState() {
        if (!keyPersistence.isEmpty()) {
            Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
            if (prefs.containsKey(keyPersistence)) {
                expanded = prefs.getBoolean(keyPersistence);
                setExpanded();
            }
        }
    }

    public boolean isExpanded() {
        return expanded;
    }

    private void toggleExpandCollapse() {
        expanded = !expanded;
        writeExpandedState();
        setExpanded();
    }

    private void setExpanded() {
        if (expanded) {
            expandComponent();
        } else {
            collapseComponent();
        }
    }

    private void redraw() {
        validate();
        getParent().validate();
    }

    private void createFillLabel() {
        if (component instanceof Container) {
            Container container = (Container) component;
            int componentCount = container.getComponentCount();
            for (int i = 0; i < componentCount; i++) {
                Component containerComponent = container.getComponent(i);
                if (containerComponent instanceof JLabel) {
                    JLabel label = (JLabel) containerComponent;
                    fillText = label.getText();
                    fillFont = label.getFont();
                    labelFill = new JLabel(fillText);
                    labelFill.setFont(fillFont);
                    return;
                }
            }
        }
        labelFill = new JLabel();
    }

    private void addAsFocusListener() {
        if (component instanceof Container) {
            Container container = (Container) component;
            int componentCount = container.getComponentCount();
            for (int i = 0; i < componentCount; i++) {
                Component containerComponent = container.getComponent(i);
                if (containerComponent instanceof JTextComponent) {
                    containerComponent.addFocusListener(this);
                }
            }
        }
    }

    @Override
    public void focusGained(FocusEvent evt) {
        Object source = evt.getSource();
        if (!expanded && (source instanceof JTextComponent)) {
            ((JTextComponent) source).transferFocus();
        }
    }

    @Override
    public void focusLost(FocusEvent evt) {}

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        buttonExpandCollapse = new javax.swing.JButton();

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        buttonExpandCollapse.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 125, 125)));
        buttonExpandCollapse.setContentAreaFilled(false);
        buttonExpandCollapse.setFocusable(false);
        buttonExpandCollapse.setMargin(org.jphototagger.resources.UiFactory.insets(0, 0, 0, 0));
        buttonExpandCollapse.setName("buttonExpandCollapse"); // NOI18N
        buttonExpandCollapse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonExpandCollapseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(3, 0, 0, 5);
        add(buttonExpandCollapse, gridBagConstraints);
    }//GEN-END:initComponents

    private void buttonExpandCollapseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonExpandCollapseActionPerformed
        toggleExpandCollapse();
    }//GEN-LAST:event_buttonExpandCollapseActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonExpandCollapse;
    // End of variables declaration//GEN-END:variables
}
