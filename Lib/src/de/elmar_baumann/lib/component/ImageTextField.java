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
package de.elmar_baumann.lib.component;

import de.elmar_baumann.lib.image.util.IconUtil;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

/**
 * Textfield displaying an (background) image.
 * <p>
 * If the image is heigher than this textfield it will be rendered at position
 * x = 0 and y = 0, if it's less heigh, it will be centered vertically.
 * <p>
 * Can be used as custom control within the <strong>NetBeans</strong> IDE:
 * Changing the property <code>imagePath</code> changes the background image.
 * <p>
 * When the field contains text, the image will not be displayed to avoid
 * overlaying the text. If the text field is empty, the image will be displayed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-12-30
 */
public final class ImageTextField extends JTextField implements KeyListener {

    private   Image   image;
    private   boolean paintImage;
    protected String  imagePath;

    public ImageTextField(Document doc, String text, int columns) {
        super(doc, text, columns);
        addKeyListener(this);
    }

    public ImageTextField(String text, int columns) {
        super(text, columns);
        addKeyListener(this);
    }

    public ImageTextField(int columns) {
        super(columns);
        addKeyListener(this);
    }

    public ImageTextField(String text) {
        super(text);
        addKeyListener(this);
    }

    public ImageTextField() {
        addKeyListener(this);
    }

    /**
     * Get the value of imagePath
     *
     * @return the value of imagePath
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * Sets the path to the image.
     *
     * @param imagePath path, e.g. <code>"/com/mydomain/myproject/res/search.png"</code>
     */
    public void setImagePath(String imagePath) {

        this.imagePath = imagePath;
        image          = IconUtil.getIconImage(imagePath);
        paintImage     = image != null;

        setOpaque(false);
    }

    /**
     * Sets an image rather than an image path.
     *
     * @param image image
     */
    public void setImage(Image image) {

        this.image     = image;
        this.imagePath = null;
        paintImage     = image != null;

        setOpaque(false);
    }

    @Override
    public void paintComponent (Graphics g) {
        drawImage(g);
        super.paintComponent(g);
    }

    private void drawImage(Graphics g) {

        if (paintImage) {

            int imgHeight  = image.getHeight(this);
            int thisHeight = getHeight();
            int imgY       = imgHeight >= thisHeight ? 0 : (int) (((double)(thisHeight - imgHeight)) / 2.0 + 0.5);

            g.drawImage(image, 0, imgY, this);

        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // ignore
    }

    @Override
    public void keyTyped(KeyEvent e) {
        paintImage = getDocument().getLength() <= 0 && e.getKeyChar() < 20;
        setOpaque(!paintImage);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // ignore
    }
}
