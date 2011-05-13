package org.jphototagger.lib.component;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

/**
 * Displays an image centered whitin it's previous set area. Draws the empty
 * background if the image is null.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author Elmar Baumann
 */
public class ImagePanel extends JPanel {
    private static final long serialVersionUID = 6103175417678650145L;
    private Image image;
    private Align align = Align.CENTER;

    public static enum Align { CENTER, LEFT_TOP, }

    /**
     * Sets the image.
     *
     * @param image  image, can be null
     */
    public void setImage(Image image) {
        this.image = image;
    }

    public void setAlign(Align align) {
        if (align == null) {
            throw new NullPointerException("align == null");
        }

        this.align = align;
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(getBackground());

        int width = getWidth();
        int height = getHeight();

        if (image != null) {
            int imgHeight = image.getHeight(this);
            int imgWidth = image.getWidth(this);
            int x = ((imgWidth < width) && align.equals(Align.CENTER)
                     ? (width - imgWidth) / 2
                     : 0);
            int y = ((imgHeight < height) && align.equals(Align.CENTER)
                     ? (height - imgHeight) / 2
                     : 0);

            g.fillRect(0, 0, width, height);
            g.drawImage(image, x, y, this);
        } else {
            g.drawRect(0, 0, width, height);
        }
    }
}
