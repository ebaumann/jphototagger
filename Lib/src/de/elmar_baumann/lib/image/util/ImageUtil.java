/*
 * @(#)ImageUtil.java    2008-07-24
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

package de.elmar_baumann.lib.image.util;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

/**
 * Image utils.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann
 */
public final class ImageUtil {

    /**
     * Returns a <code>ByteArrayInputStream</code> of an image.
     *
     * @param image      image
     * @param formatName a String containg the informal name of the format
     * @return           stream oder null on errors
     */
    public static ByteArrayInputStream getByteArrayInputStream(Image image,
            String formatName) {
        if (image == null) {
            throw new NullPointerException("image == null");
        }

        if (formatName == null) {
            throw new NullPointerException("formatName == null");
        }

        ByteArrayInputStream stream = null;

        try {
            BufferedImage bufferedImage =
                new BufferedImage(image.getWidth(null), image.getHeight(null),
                                  BufferedImage.TYPE_INT_RGB);
            Graphics graphics = bufferedImage.getGraphics();

            graphics.drawImage(image, 0, 0, null);
            graphics.dispose();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            ImageIO.write(bufferedImage, formatName, outputStream);

            byte[] byteArray = outputStream.toByteArray();

            stream = new ByteArrayInputStream(byteArray);
        } catch (Exception ex) {
            Logger.getLogger(ImageUtil.class.getName()).log(Level.SEVERE, null,
                             ex);
        }

        return stream;
    }

    /**
     * Returns the new dimensions of an image if the image shall be scaled.
     *
     * @param img      image to scale
     * @param maxWidth the new width of the longer image side
     * @return         new dimensions or null if they couldn't be calculated
     */
    public static Dimension getNewDimensions(BufferedImage img, int maxWidth) {
        int width  = img.getWidth();
        int height = img.getHeight();

        assert (width > 0) && (height > 0) :
                "Width " + width + " height " + height + " have to be > 0!";

        if ((width <= 0) || (height <= 0)) {
            return null;
        }

        boolean isLandscape  = width > height;
        double  aspectRatio  = (double) width / (double) height;
        int     lenOtherSide = isLandscape
                               ? (int) ((double) maxWidth / aspectRatio + 0.5)
                               : (int) ((double) maxWidth * aspectRatio + 0.5);
        int     newWidth     = isLandscape
                               ? maxWidth
                               : lenOtherSide;
        int     newHeight    = isLandscape
                               ? lenOtherSide
                               : maxWidth;

        assert (newWidth > 0) && (newHeight > 0) :
                "Width " + newWidth + " height " + newHeight
                + " have to be > 0!";

        if ((newWidth <= 0) || (newHeight <= 0)) {
            return null;
        }

        return new Dimension(newWidth, newHeight);
    }

    private ImageUtil() {}
}
