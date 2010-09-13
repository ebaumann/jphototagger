/*
 * @(#)ImageTextArea.java    Created on 2009-12-30
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.lib.component;

import org.jphototagger.lib.image.util.IconUtil;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JTextArea;

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
 * @author  Elmar Baumann
 */
public final class ImageTextArea extends JTextArea implements KeyListener {
    private static final long serialVersionUID = -3386009175292905714L;
    private boolean           consumeEnter;
    private Image             image;
    private String            imagePath;
    private boolean           paintImage;

    public ImageTextArea() {
        addKeyListener(this);
    }

    /**
     * Set whether the text area shall consume enter (disable line wrapping).
     *
     * @param consumeEnter true, if consume. Default: false.
     */
    public void setConsumeEnter(boolean consumeEnter) {
        this.consumeEnter = consumeEnter;
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
     * @param imagePath path, e.g.
     *                  <code>"/com/mydomain/myproject/res/search.png"</code>
     */
    public void setImagePath(String imagePath) {
        if (imagePath == null) {
            throw new NullPointerException("imagePath == null");
        }

        this.imagePath = imagePath;
        image          = IconUtil.getIconImage(imagePath);
        paintImage     = image != null;
        setOpaque(false);
    }

    /**
     * Sets an image rather than an image path.
     *
     * @param image image, can be null
     */
    public void setImage(Image image) {
        paintImage     = image != null;
        this.image     = image;
        this.imagePath = null;
        setOpaque(false);
    }

    @Override
    public void paintComponent(Graphics g) {
        drawImage(g);
        super.paintComponent(g);
    }

    private void drawImage(Graphics g) {
        if (paintImage) {
            int imgHeight  = image.getHeight(this);
            int thisHeight = getHeight();
            int imgY       = (imgHeight >= thisHeight)
                             ? 0
                             : (int) (((double) (thisHeight - imgHeight)) / 2.0
                                      + 0.5);

            g.drawImage(image, 0, imgY, this);
        }
    }

    @Override
    public void keyPressed(KeyEvent evt) {

        // ignore
    }

    @Override
    public void keyTyped(KeyEvent evt) {
        paintImage = (getDocument().getLength() <= 0)
                     && (evt.getKeyChar() < 20);
        setOpaque(!paintImage);
    }

    @Override
    public void keyReleased(KeyEvent evt) {

        // ignore
    }

    @Override
    protected void processComponentKeyEvent(KeyEvent evt) {
        if (consumeEnter && (evt.getID() == KeyEvent.KEY_PRESSED)
                && (evt.getKeyCode() == KeyEvent.VK_ENTER)) {
            evt.consume();
        } else {
            super.processComponentKeyEvent(evt);
        }
    }
}
