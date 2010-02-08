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
package de.elmar_baumann.jpt.view.renderer;

import de.elmar_baumann.jpt.cache.RenderedThumbnailCacheIndirection;
import de.elmar_baumann.jpt.cache.ThumbnailRenderer;
import de.elmar_baumann.jpt.cache.XmpCache;
import de.elmar_baumann.jpt.data.ThumbnailFlag;
import de.elmar_baumann.jpt.data.Xmp;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpRating;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import javax.swing.ImageIcon;

/**
 *
 * @author Martin Pohlack <martinp@gmx.de>
 * @version 2009-08-20
 */
public class ThumbnailPanelRenderer implements ThumbnailRenderer {

    /**
     * Width of a thumbnail flag in pixel
     */
    private static final int FLAG_WIDTH = 10;
    /**
     * Height of a thumbnail flag in pixel
     */
    private static final int FLAG_HEIGHT = 10;
    /**
     * Height of the text font for text below the thumbnails in points
     */
    private static final int FONT_HEIGHT = 10;
    /**
     * Font of the text below the thumbnails
     */
    private static final Font FONT = new Font("Arial", Font.PLAIN, FONT_HEIGHT);
    /**
     * Height of the text font for text below the thumbnails in pixels,
     * computed from FONT_HEIGHT.
     */
    private static int FONT_PIXEL_HEIGHT = -1;
    private static int FONT_PIXEL_DESCENT = -1;
    /**
     * Color of the text below the thumbnails
     */
    private static final Color COLOR_TEXT = Color.lightGray;
    /**
     * Background color of the space between a thumbnail and it's border
     */
    private static final Color COLOR_BACKGROUND_PADDING_THUMBNAIL = new Color(
            0, 0, 0);
    /**
     * Color of the border surrounding the thumbnails
     */
    private static final Color COLOR_BORDER_THUMBNAIL = new Color(64, 64, 64);
    /**
     * Color of the text below the thumbnails of a higlighted thumbnail. Depends
     * on {@link #COLOR_BACKGROUND_PADDING_THUMBNAIL_HIGHLIGHTED}
     */
    private static final Color COLOR_TEXT_HIGHLIGHTED = new Color(255, 255, 168);
    /**
     * Background color behind the keywords overlay.
     */
    private static final Color COLOR_TEXT_BACKGROUND = new Color(0, 0, 128);
    /**
     * Color of the background surrounding a highlighted thumbnail. When
     * changing, look for {@link #COLOR_TEXT_HIGHLIGHTED}.
     */
    private static final Color COLOR_BACKGROUND_PADDING_THUMBNAIL_HIGHLIGHTED = new Color(
            112, 122, 148);
    /**
     * Color of the border surrounding the highlighted thumbnails.
     */
    private static final Color COLOR_BORDER_THUMBNAIL_HIGHLIGHTED =
            new Color(128, 128, 164);
    /**
     * Maximum character count of the text below a thumbnail
     */
    private int maxCharCountText = 35;
    /**
     * Empty space surrounding a thumbnail within the border (space between
     * the thumbnail's image and the border) in pixel
     */
    private static final int PADDING_THUMBNAIL = 3;
    /**
     * Width of the border surrounding the thumbnails in pixel
     */
    private static final int WIDHT_BORDER_THUMBNAIL = 1;
    /**
     * Maximimum character count of the text below the thumbnails when the
     * width of a thumbnail is 150 pixels
     */
    private static final int MAX_CHAR_COUNT_PER_150_PX = 30;
    /**
     * Width of a thumbnail
     */
    private int thumbnailWidth = 0;
    private XmpCache xmpCache = XmpCache.INSTANCE;

    private Image starImage[] = new Image[5];
    private final ThumbnailsPanel panel;

    public ThumbnailPanelRenderer(ThumbnailsPanel _panel) {
        panel = _panel;
        computeFontHeight();
        loadRatingImages();
    }

    private void computeFontHeight() {
        FONT_PIXEL_HEIGHT = panel.getFontMetrics(FONT).getHeight();
        FONT_PIXEL_DESCENT = panel.getFontMetrics(FONT).getDescent() +
                panel.getFontMetrics(FONT).getLeading() / 2;
    }

    private void loadRatingImages() {
        for (int i = 0; i < 5; i++) {
            starImage[i] = new ImageIcon(getClass().getResource(
                    "/de/elmar_baumann/jpt/resource/icons/icon_xmp_rating_" +
                    Integer.toString(i + 1) + ".png")).getImage();
        }
    }

    @Override
    public Image getRenderedThumbnail(Image scaled,
            RenderedThumbnailCacheIndirection rtci, boolean dummy) {
        synchronized(panel) {
            int sw = scaled.getWidth(null);
            int sh = scaled.getHeight(null);
            int length = sw > sh ? sw : sh;
            int w = length + 2 * PADDING_THUMBNAIL + 2 * WIDHT_BORDER_THUMBNAIL;
            int h = w + FONT_PIXEL_HEIGHT;

            BufferedImage bi =
                    new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = bi.createGraphics();
            // switch this for performance / beauty
            if (! dummy) {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
            }
            g2.setFont(FONT);

            g2.setColor(ThumbnailsPanel.COLOR_BACKGROUND_PANEL);
            g2.fillRect(0, 0, w, h);
            paintThumbnailBackground(g2, panel.isSelected(rtci.file));
            paintThumbnailFlag(g2, rtci.file);
            paintThumbnail(scaled, g2);
            if (! dummy) {
                paintThumbnailText(g2, rtci.file);
                rtci.renderedForKeywords = panel.isKeywordsOverlay();
                boolean actualOverlay = false;
                if (panel.isKeywordsOverlay()) {
                    actualOverlay = paintThumbnailKeywords(g2, rtci.file);
                    actualOverlay |= paintThumbnailStars(g2, rtci.file);
                }
                rtci.hasKeywords = actualOverlay;
            }
            g2.dispose();
            return bi;
        }
    }

    private void paintThumbnailBackground(Graphics2D g, boolean isSelected) {
        Color backgroundColor = isSelected
                ? COLOR_BACKGROUND_PADDING_THUMBNAIL_HIGHLIGHTED
                : COLOR_BACKGROUND_PADDING_THUMBNAIL;
        Color borderColor = isSelected
                ? COLOR_BORDER_THUMBNAIL_HIGHLIGHTED
                : COLOR_BORDER_THUMBNAIL;
        Color oldColor = g.getColor();
        g.setColor(backgroundColor);
        g.fillRoundRect(0, 0,
                thumbnailWidth + 2 * PADDING_THUMBNAIL + WIDHT_BORDER_THUMBNAIL,
                thumbnailWidth + 2 * PADDING_THUMBNAIL + WIDHT_BORDER_THUMBNAIL,
                PADDING_THUMBNAIL * 2,
                PADDING_THUMBNAIL * 2);
        g.setColor(borderColor);
        g.drawRoundRect(0, 0,
                thumbnailWidth + 2 * PADDING_THUMBNAIL + WIDHT_BORDER_THUMBNAIL,
                thumbnailWidth + 2 * PADDING_THUMBNAIL + WIDHT_BORDER_THUMBNAIL,
                PADDING_THUMBNAIL * 2,
                PADDING_THUMBNAIL * 2);
        g.setColor(oldColor);
    }

    private void paintThumbnailFlag(Graphics g, File file) {
        ThumbnailFlag flag = panel.getFlag(file);
        if (flag != null) {
            Color oldColor = g.getColor();
            g.setColor(flag.getColor());
            g.fillRect(thumbnailWidth + 2 * PADDING_THUMBNAIL - FLAG_WIDTH,
                       thumbnailWidth + 2 * PADDING_THUMBNAIL - FLAG_HEIGHT,
                       FLAG_WIDTH, FLAG_HEIGHT);
            g.setColor(oldColor);
        }
    }

    private void paintThumbnail(Image thumbnail, Graphics g) {
        int indentTop = getThumbnailTopIndent(thumbnail);
        int indentLeft = getThumbnailLeftIndent(thumbnail);

        g.drawImage(thumbnail, indentLeft, indentTop, null);
    }

    private void paintThumbnailText(Graphics g, File file) {
        String text = getFormattedText(file);
        int width = getThumbnailAreaWidth();
        int lenTitle = g.getFontMetrics().stringWidth(text);
        int space = width - lenTitle;
        int xText = space > 0 ? (space + 1) / 2 : 0;

        Color oldColor = g.getColor();
        g.setColor(panel.isSelected(file)
                ? COLOR_TEXT_HIGHLIGHTED
                : COLOR_TEXT);
        g.drawString(text, xText,
                getThumbnailAreaHeight() - FONT_PIXEL_DESCENT);
        g.setColor(oldColor);
    }

    private boolean paintThumbnailKeywords(Graphics g, File file) {
        List<String> keywords = getKeywords(file);
        if (keywords == null || keywords.size() == 0) {
            return false;
        }
        int width = getThumbnailAreaWidth();
        int height = getThumbnailAreaHeightNoText();

        BufferedImage bi = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();
        g2.setColor(new Color(0, 0, 0, 0));
        g2.fillRect(0, 0, width, height);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Iterator<String> is;
        // draw background
        g2.setColor(COLOR_TEXT_BACKGROUND);
        g2.setFont(FONT);
        is = keywords.iterator();
        for (int i = 0; is.hasNext(); i++) {
            String text = is.next();
            int x = WIDHT_BORDER_THUMBNAIL;
            int y = WIDHT_BORDER_THUMBNAIL +
                    (i + 1) * FONT_PIXEL_HEIGHT - FONT_PIXEL_DESCENT;

            g2.drawString(text, x + 1, y);
            g2.drawString(text, x - 1, y);
            g2.drawString(text, x,     y + 1);
            g2.drawString(text, x,     y - 1);
        }
        g2.dispose();
        float frac = 1.0f / 9.0f;
        float[] kernel = {frac, frac, frac,
                          frac, frac, frac,
                          frac, frac, frac};
        ConvolveOp op = new ConvolveOp(new Kernel(3, 3, kernel),
                ConvolveOp.EDGE_NO_OP, null);
        BufferedImage bi2 = op.filter(bi, null);

        // draw foreground
        g2 = bi2.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(COLOR_TEXT_HIGHLIGHTED);
        g2.setFont(FONT);
        is = keywords.iterator();
        for (int i = 0; is.hasNext(); i++) {
            g2.drawString(is.next(),
                    WIDHT_BORDER_THUMBNAIL,
                    WIDHT_BORDER_THUMBNAIL +
                    (i + 1) * FONT_PIXEL_HEIGHT - FONT_PIXEL_DESCENT);
        }
        g2.dispose();

        // copy over
        g.drawImage(bi2, 0, 0, null);  // fixme: is null ok here?
        return true;
    }

    private boolean paintThumbnailStars(Graphics g, File file) {
        int stars = getRating(file);
        if (stars > 0) {
            int i = Math.min(4, stars - 1);
            g.drawImage(starImage[i],
                    getThumbnailAreaWidth() - starImage[i].getWidth(null),
                    0, null);
            return true;
        }
        return false;
    }

    private String getFormattedText(File file) {
        String text = getText(file);
        int charCountText = text.length();

        if (charCountText <= maxCharCountText) {
            return text;
        }

        return "..." + text.substring(text.length() - maxCharCountText + 2);
    }

    private int getThumbnailLeftIndent(Image thumbnail) {
        // fixme: observer
        int indentLeft = (getThumbnailAreaWidth() -
                thumbnail.getWidth(null) + 1) / 2;
        if (indentLeft < 0) {
            indentLeft = 0;
        }
        return indentLeft;
    }

    private int getThumbnailTopIndent(Image thumbnail) {
        // fixme: observer
        int indentTop = (getThumbnailAreaHeightNoText() -
                thumbnail.getHeight(null) + 1) / 2;
        if (indentTop < 0) {
            indentTop = 0;
        }
        return indentTop;
    }

    public int getThumbnailAreaHeight() {
        return getThumbnailAreaHeightNoText() + FONT_PIXEL_HEIGHT;
    }

    private int getThumbnailAreaHeightNoText() {
        return thumbnailWidth + 2 * PADDING_THUMBNAIL +
                2 * WIDHT_BORDER_THUMBNAIL;
    }

    public int getThumbnailAreaWidth() {
        return thumbnailWidth + 2 * PADDING_THUMBNAIL +
                2 * WIDHT_BORDER_THUMBNAIL;
    }

    public void setThumbnailWidth(int width) {
        thumbnailWidth = width;
        maxCharCountText = (int) (((double) MAX_CHAR_COUNT_PER_150_PX *
                (double) width / 150.0));
    }

    public int getThumbnailWidth() {
        return thumbnailWidth;
    }

    protected String getText(File file) {
        String filename = file.getAbsolutePath();
        int indexPathSeparator = filename.lastIndexOf(File.separator);
        if (indexPathSeparator >= 0 && indexPathSeparator + 1 < filename.
                length()) {
            filename = filename.substring(indexPathSeparator + 1);
        }
        return filename;
    }

    /**
     * Delivers keywords for a specific thumbnail.
     *
     * @param file  File designating a Thumbnail
     * @return      keywords
     */
    @SuppressWarnings("unchecked")
    public synchronized List<String> getKeywords(File file) {
        Xmp xmp = xmpCache.getXmp(file);

        if (xmp == null || !xmp.contains(ColumnXmpDcSubjectsSubject.INSTANCE)) {
            return null;
        }

        return (List<String>) xmp.getValue(ColumnXmpDcSubjectsSubject.INSTANCE);
    }

    /**
     * Delivers rating for a specific thumbnail.
     *
     * @param file  File designating the thumbnail
     * @return      rating
     */
    public synchronized int getRating(File file) {
        Xmp xmp = xmpCache.getXmp(file);
        if (xmp == null) {
            return 0;
        }

        Long rating = xmp.contains(ColumnXmpRating.INSTANCE) ? (Long) xmp.getValue(ColumnXmpRating.INSTANCE) : null;
        if (rating == null) {
            return 0;
        }

        return rating.intValue();
    }
}
