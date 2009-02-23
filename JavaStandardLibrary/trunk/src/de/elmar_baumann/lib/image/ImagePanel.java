package de.elmar_baumann.lib.image;

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
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/07/20
 */
public class ImagePanel extends JPanel {

    private Image image;

    /**
     * Sets the image.
     * 
     * @param image  image, can be null
     */
    public void setImage(Image image) {
        if (image == null)
            throw new NullPointerException("image == null");

        this.image = image;
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(getBackground());
        int width = getWidth();
        int height = getHeight();
        if (image != null) {
            int imgHeight = image.getHeight(this);
            int imgWidth = image.getWidth(this);
            int x = (imgWidth < width ? (width - imgWidth) / 2 : 0);
            int y = (imgHeight < height ? (height - imgHeight) / 2 : 0);

            g.fillRect(0, 0, width, height);
            g.drawImage(image, x, y, this);
        } else {
            g.drawRect(0, 0, width, height);
        }
    }
}
