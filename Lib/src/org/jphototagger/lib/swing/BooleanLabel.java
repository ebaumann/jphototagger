package org.jphototagger.lib.swing;

import javax.swing.Icon;
import javax.swing.JLabel;
import org.jphototagger.lib.api.AppIconProvider;
import org.openide.util.Lookup;

/**
 * Displays special icons and/or texts indicating whether something is true
 * and shall be a repacement for a {@code JCheckBox} which is used only for
 * that approach (in it's disabled state the text is greyed out).
 *
 * @author Elmar Baumann
 */
public final class BooleanLabel extends JLabel {

    private static final long serialVersionUID = 1L;
    private Icon trueIcon = Lookup.getDefault().lookup(AppIconProvider.class).getIcon("icon_true.png");
    private Icon falseIcon = Lookup.getDefault().lookup(AppIconProvider.class).getIcon("icon_false.png");
    private String trueText;
    private String falseText;
    private Boolean isTrue;

    public BooleanLabel() {
        this("");
    }

    public BooleanLabel(String text) {
        super(text);
        setIcon(falseIcon);
    }

    public Icon getFalseIcon() {
        return falseIcon;
    }

    public void setFalseIcon(Icon falseIcon) {
        Icon old = this.falseIcon;
        this.falseIcon = falseIcon;
        firePropertyChange("falseIcon", old, this.falseIcon);
    }

    public Icon getTrueIcon() {
        return trueIcon;
    }

    public void setTrueIcon(Icon trueIcon) {
        Icon old = this.trueIcon;
        this.trueIcon = trueIcon;
        firePropertyChange("trueIcon", old, this.trueIcon);
    }

    public Boolean isTrue() {
        return isTrue;
    }

    public void setIsTrue(Boolean isTrue) {
        Boolean old = this.isTrue;
        this.isTrue = isTrue;
        setIcon(isTrue ? trueIcon : falseIcon);
        firePropertyChange("isTrue", old, this.isTrue);
    }

    @Override
    public String getText() {
        if (trueText != null && falseText != null) {
            return isTrue == null ? falseText : isTrue ? trueText : falseText;
        }
        return super.getText();
    }

    public String getTrueText() {
        return trueText;
    }

    /**
     * Will be used only if {@code #getFalseText()} is not null.
     *
     * @param trueText
     */
    public void setTrueText(String trueText) {
        String oldTrueText = this.trueText;
        String oldText = getText();
        this.trueText = trueText;
        firePropertyChange("trueText", oldTrueText, this.trueText);
        firePropertyChange("text", oldText, getText());
    }

    public String getFalseText() {
        return falseText;
    }

    /**
     * Will be used only if {@code #getTrueText()} is not null.
     *
     * @param falseText
     */
    public void setFalseText(String falseText) {
        String oldFalseText = this.falseText;
        String oldText = getText();
        this.falseText = falseText;
        firePropertyChange("falseText", oldFalseText, this.falseText);
        firePropertyChange("text", oldText, getText());
    }
}
