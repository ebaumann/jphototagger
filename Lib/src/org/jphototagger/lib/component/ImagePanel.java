package org.jphototagger.lib.component;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

/**
 * Displays an image centered or the background color if the image is null.
 *
 * @author Elmar Baumann
 */
public class ImagePanel extends JPanel {

    private static final long serialVersionUID = 6103175417678650145L;
    private Image image;
    private String imageIsAbsentText;

    /**
     *
     * @param image can be null
     */
    public void setImage(Image image) {
        Image oldImage = this.image;
        this.image = image;
        firePropertyChange("image", oldImage, image);
        repaint();
    }

    public Image getImage() {
        return image;
    }

    public String getImageIsAbsentText() {
        return imageIsAbsentText;
    }

    /**
     *
     * @param imageIsAbsentText can be null
     */
    public void setImageIsAbsentText(String imageIsAbsentText) {
        String oldText = this.imageIsAbsentText;
        this.imageIsAbsentText = imageIsAbsentText;
        firePropertyChange("imageIsAbsentText", oldText, imageIsAbsentText);
    }

    @Override
    public void paint(Graphics g) {
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        eraseBackground(g, panelWidth, panelHeight);

        if (image != null) {
            drawImage(panelWidth, panelHeight, g);
        } else {
            drawImageIsAbsentText(g);
        }
    }

    private void eraseBackground(Graphics g, int panelWidth, int panelHeight) {
        g.setColor(getBackground());
        g.fillRect(0, 0, panelWidth, panelHeight);
    }

    private void drawImage(int panelWidth, int panelHeight, Graphics g) {
        Dimension imgageDimension = scaleImageIfSizeExceedsPanel();
        int imageWidth = imgageDimension.width;
        int imageHeight = imgageDimension.height;
        int x = getImageX(imageWidth, panelWidth);
        int y = getImageY(imageHeight, panelHeight);

        g.drawImage(image, x, y, this);
    }

    private int getImageX(int imageWidth, int panelWidth) {
        return imageWidth < panelWidth
                ? (panelWidth - imageWidth) / 2
                : 0;
    }

    private int getImageY(int imageHeight, int panelHeight) {
        return imageHeight < panelHeight
                ? (panelHeight - imageHeight) / 2
                : 0;
    }

    private Dimension scaleImageIfSizeExceedsPanel() {
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int imageHeight = image.getHeight(this);
        int imageWidth = image.getWidth(this);

        if (imageHeight > panelHeight || imageWidth > panelWidth) {
            double scaleFactor = getMaxScaleFactor(image);
            if (scaleFactor != 0) {
                int scaledWidth = (int) (imageWidth * scaleFactor);
                int scaledHeight = (int) (imageHeight * scaleFactor);
                image = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                imageHeight = image.getHeight(this);
                imageWidth = image.getWidth(this);
            }
        }

        return new Dimension(imageWidth, imageHeight);
    }

    private double getMaxScaleFactor(Image image) {
        double imageWidth = image.getWidth(this);
        double imageHeight = image.getHeight(this);
        double panelWidth = getWidth();
        double panelHeight = getHeight();
        boolean panelIsLandscape = panelWidth > panelHeight;

        if (panelIsLandscape) {
            double scaleFactor = panelHeight / imageHeight;
            double newImageWidth = imageWidth * scaleFactor;
            return newImageWidth <= panelWidth
                    ? scaleFactor
                    : panelWidth / imageWidth;
        } else {
            double scaleFactor = panelWidth / imageWidth;
            double newImageHeight = imageHeight * scaleFactor;
            return newImageHeight <= panelHeight
                    ? scaleFactor
                    : panelHeight / imageHeight;
        }
    }

    private void drawImageIsAbsentText(Graphics g) {
        if (imageIsAbsentText == null) {
            return;
        }

        double panelWidth = getWidth();
        double panelHeight = getHeight();
        FontMetrics fm = g.getFontMetrics();
        double textWidth = fm.stringWidth(imageIsAbsentText);
        double textHeight = fm.getAscent();
        int x = 0;
        int y = (int) textHeight;

        if (textWidth < panelWidth) {
            x = (int) ((panelWidth - textWidth) / 2.0 + 0.5);
        }

        if (textHeight < panelHeight) {
            y = (int) (textHeight + (panelHeight - textHeight) / 2.0 + 0.5);
        }

        g.setColor(getForeground());
        g.drawString(imageIsAbsentText, x, y);
    }
}
