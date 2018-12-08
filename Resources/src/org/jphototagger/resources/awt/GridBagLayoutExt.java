package org.jphototagger.resources.awt;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class GridBagLayoutExt extends GridBagLayout {

    private static final long serialVersionUID = 1L;
    private final double scale;

    public GridBagLayoutExt() {
        AppScaleProvider sp = Lookup.getDefault().lookup(AppScaleProvider.class);
        if (sp != null) {
            scale = sp.getScaleFactor();
        } else {
            scale = 1.0;
        }
    }

    @Override
    public void setConstraints(Component comp, GridBagConstraints constraints) {
        super.setConstraints(comp, clone(constraints));
    }

    private GridBagConstraints clone(GridBagConstraints constraints) {
        if (Double.compare(scale, 1.0) == 0) {
            return constraints;
        }
        GridBagConstraints clone = constraints;
        if (constraints != null) {
            clone = (GridBagConstraints) constraints.clone();
            clone.fill = (int) ((double) constraints.fill * scale);
            clone.ipadx = (int) ((double) constraints.ipadx * scale);
            clone.ipady = (int) ((double) constraints.ipady * scale);
            if (constraints.insets != null) {
                clone.insets = new Insets(
                        (int) ((double) constraints.insets.top * scale),
                        (int) ((double) constraints.insets.left * scale),
                        (int) ((double) constraints.insets.bottom * scale),
                        (int) ((double) constraints.insets.right * scale));
            }
        }
        return clone;
    }
}
