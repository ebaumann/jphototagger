package org.jphototagger.lib.component;

import javax.swing.Icon;
import javax.swing.JLabel;
import org.jphototagger.lib.image.util.IconUtil;

/**
 * Displays an icon indicating whether something is true or not before a text
 * and shall be a repacement for a {@link JCheckBox} which is used only for
 * that approach (in it's disabled state the text is greyed out).
 *
 * @author Elmar Baumann
 */
public final class BooleanLabel extends JLabel {

    private static final long serialVersionUID = 1L;
    private Icon trueIcon = IconUtil.getImageIcon(BooleanLabel.class, "icon_boolean_true.png");
    private Icon falseIcon = IconUtil.getImageIcon(BooleanLabel.class, "icon_boolean_false.png");
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
        firePropertyChange("falseIcon", old, falseIcon);
    }

    public Icon getTrueIcon() {
        return trueIcon;
    }

    public void setTrueIcon(Icon trueIcon) {
        Icon old = this.trueIcon;
        this.trueIcon = trueIcon;
        firePropertyChange("trueIcon", old, trueIcon);
    }

    public Boolean isTrue() {
        return isTrue;
    }

    public void setIsTrue(Boolean isTrue) {
        Boolean old = this.isTrue;
        this.isTrue = isTrue;
        setIcon(isTrue ? trueIcon : falseIcon);
        firePropertyChange("isTrue", old, isTrue);
    }
}
