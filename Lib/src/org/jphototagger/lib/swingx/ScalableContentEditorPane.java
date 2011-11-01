package org.jphototagger.lib.swingx;

import java.awt.Graphics;
import java.awt.Graphics2D;
import org.jdesktop.swingx.JXEditorPane;

/**
 * @author Elmar Baumann
 */
public final class ScalableContentEditorPane extends JXEditorPane {

    private static final long serialVersionUID = 1L;
    private final Object monitor = new Object();
    private double scaleFactor = 1;

    public double getScaleFactor() {
        return scaleFactor;
    }

    /**
     * @param scaleFactor Default: 1.0
     */
    public void setScaleFactor(double scaleFactor) {
        if (scaleFactor <= 0) {
            throw new IllegalArgumentException("Illegal Scale Factor: " + scaleFactor);
        }
        double old = this.scaleFactor;
        if (scaleFactor == old) {
            return;
        }
        synchronized (monitor) {
            this.scaleFactor = scaleFactor;
        }
        repaint();
        firePropertyChange("scaleFactor", old, scaleFactor);
    }

    /**
     *
     * @param percent 1/100 of Scale Factor: 100 == Scale Factor 1.0, 50 == Scale Factor 0.5, 200 == Scale Factor 2.0
     */
    public void setScaleFactorPercent(int percent) {
        if (percent <= 0) {
            throw new IllegalArgumentException("Invalid Scale Percent Factor: " + percent);
        }
        int old = getScaleFactorPercent();
        if (percent == old) {
            return;
        }
        setScaleFactor((double) percent / 100.0);
        firePropertyChange("scaleFactorPercent", old, percent);
    }

    public int getScaleFactorPercent() {
        return (int) scaleFactor * 100;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        synchronized (monitor) {
            g2d.scale(scaleFactor, scaleFactor);
        }

        super.paintComponent(g2d);
    }
}
