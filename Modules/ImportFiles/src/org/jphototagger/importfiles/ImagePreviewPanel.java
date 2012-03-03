package org.jphototagger.importfiles;

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

import org.jphototagger.domain.filefilter.AppFileFilterProvider;
import org.jphototagger.domain.filefilter.FileFilterUtil;
import org.jphototagger.domain.thumbnails.ThumbnailProvider;
import org.jphototagger.image.util.ThumbnailCreatorService;
import org.jphototagger.lib.io.filefilter.FileChooserFilter;
import org.jphototagger.lib.util.Bundle;

//Code based on http://www.javalobby.org/java/forums/t49462.html
/**
 * @author Elmar Baumann
 */
public class ImagePreviewPanel extends JPanel implements PropertyChangeListener {

    private static final long serialVersionUID = 1L;
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
        if ((selFile == null) || !FileFilterUtil.isImageFile(selFile)) {
            image = null;
            repaint();
            return;
        }
        image = tnProvider.getThumbnail(selFile);
        if (image == null) {
            image = ThumbnailCreatorService.INSTANCE.createThumbnail(selFile);
        }
        if (image != null) {
            scaleImage();
        }
        repaint();
    }

    private void scaleImage() {
        width = image.getWidth(this);
        height = image.getHeight(this);

        /*
         * Determine how to scale the image. Since the accessory can expand
         * vertically make sure we don't go larger than 150 when scaling
         * vertically.
         */
        if (width >= height) {
            double ratio = (double) (SIZE - PADDING) / (double) width;
            width = SIZE - PADDING;
            height = (int) (height * ratio);
        } else {
            if (getHeight() > 150) {
                double ratio = (double) (SIZE - PADDING) / (double) height;
                height = SIZE - PADDING;
                width = (int) (width * ratio);
            } else {
                double ratio = (double) getHeight() / height;
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
        private static final FileFilter FILE_FILTER = Lookup.getDefault().lookup(AppFileFilterProvider.class).getAcceptedImageFilesFileFilter();
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
