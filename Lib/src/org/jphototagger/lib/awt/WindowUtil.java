package org.jphototagger.lib.awt;

import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Elmar Baumann
 */
public final class WindowUtil {

    /**
     * Returns whether a x/y coordinate is on the same screen as a specific
     * component.
     *
     * @param c a component
     * @param x x coordinate
     * @param y y coordinat
     * @return true, if x and y are on the same screen as the component c
     */
    public static boolean isOnSameScreenAsComponent(Component c, int x, int y) {
        Objects.requireNonNull(c, "c == null");

        if (x < 0 || y < 0) {
            return false;
        }
        GraphicsConfiguration gc = c.getGraphicsConfiguration();
        if (gc == null) {
            Logger.getLogger(WindowUtil.class.getName()).log(Level.WARNING, "Component {0} has no graphics configuration", c);
            return false;
        }
        Rectangle screenBounds = gc.getBounds();
        // Evaluation of screenBounds:
        // x: x-coordinate of the component's screen: Sum of all screen widths before the component's screen
        // y: y-coordinate of the component's screen: Sum of all screen heights obove the component's screen
        // width: width of the component's screen
        // height: height of the component's screen
        int minX = screenBounds.x;
        int maxX = screenBounds.x + screenBounds.width;
        int minY = screenBounds.y;
        int maxY = screenBounds.y + screenBounds.height;
        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }

    private WindowUtil() {
    }
}
