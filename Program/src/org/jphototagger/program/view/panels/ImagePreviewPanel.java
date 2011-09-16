package org.jphototagger.program.view.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileFilter;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

import org.openide.util.Lookup;

import org.jphototagger.api.image.thumbnails.ThumbnailProvider;
import org.jphototagger.image.util.ThumbnailCreatorService;
import org.jphototagger.lib.io.filefilter.FileChooserFilter;
import org.jphototagger.lib.io.filefilter.RegexFileFilter;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.AppFileFilters;
import org.jphototagger.program.io.ImageFileFilterer;

//Code based on http://www.javalobby.org/java/forums/t49462.html
/**
 *
 *
 * @author Elmar Baumann
 */
public class ImagePreviewPanel extends JPanel implements PropertyChangeListener {

    private static final long serialVersionUID = 574676806606408192L;
    private static final int SIZE = 155;
    private static final int PADDING = 5;
    private int width;
    private int height;
    private Image image;
    private Color bg;
    private final ThumbnailProvider tnProvider = Lookup.getDefault().lookup(ThumbnailProvider.class);

    public ImagePreviewPanel() {
        setPreferredSize(new Dimension(SIZE, -1));
        bg = getBackground();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!evt.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
            return;
        }

        File selFile = (File) evt.getNewValue();

        if ((selFile == null) || !ImageFileFilterer.isImageFile(selFile)) {
            image = null;
            repaint();

            return;
        }


        image = tnProvider.getThumbnail(selFile);

        if (image == null) {
            image = ThumbnailCreatorService.INSTANCE.createScaledOrFromEmbeddedThumbnail(selFile);
        }

        if (image != null) {
            scaleImage();
        }

        repaint();
    }

    private void scaleImage() {
        width = image.getWidth(this);
        height = image.getHeight(this);

        double ratio = 1.0;

        /*
         * Determine how to scale the image. Since the accessory can expand
         * vertically make sure we don't go larger than 150 when scaling
         * vertically.
         */
        if (width >= height) {
            ratio = (double) (SIZE - PADDING) / (double) width;
            width = SIZE - PADDING;
            height = (int) (height * ratio);
        } else {
            if (getHeight() > 150) {
                ratio = (double) (SIZE - PADDING) / (double) height;
                height = SIZE - PADDING;
                width = (int) (width * ratio);
            } else {
                ratio = (double) getHeight() / height;
                height = getHeight();
                width = (int) (width * ratio);
            }
        }

        image = image.getScaledInstance(width, height, Image.SCALE_DEFAULT);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(bg);
        g.fillRect(0, 0, SIZE, getHeight());

        if (image != null) {
            g.drawImage(image, getWidth() / 2 - width / 2 + PADDING, getHeight() / 2 - height / 2, this);
        }
    }

    public javax.swing.filechooser.FileFilter getFileFilter() {
        return ImageFileFilter.forFileChooser();
    }

    private static class ImageFileFilter implements FileFilter {

        private static final String DESCRIPTION = Bundle.getString(ImagePreviewPanel.class, "ImagePreviewPanel.ImageFileFilter.Description");
        private static final RegexFileFilter FILE_FILTER = AppFileFilters.INSTANCE.getAllAcceptedImageFilesFilter();
        private static final ImageFileFilter INSTANCE = new ImageFileFilter();

        @Override
        public boolean accept(File path) {
            return path.isDirectory() || FILE_FILTER.accept(path);
        }

        public static javax.swing.filechooser.FileFilter forFileChooser() {
            return new FileChooserFilter(INSTANCE, DESCRIPTION);
        }

        private ImageFileFilter() {
        }
    }
}
