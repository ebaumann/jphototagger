/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jphototagger.plugin.flickrupload;

import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import org.jphototagger.lib.component.ImagePanel.Align;
import org.jphototagger.lib.component.TabOrEnterLeavingTextArea;

/**
 *
 *
 * @author Elmar Baumann
 */
public class FlickrImageInfoPanel extends javax.swing.JPanel {
    public static final int IMAGE_WIDTH = 150;
    private static final long serialVersionUID = 9219933749046808577L;
    private static final String TAGS_DELIM = ";";
    private final ResourceBundle bundle = ResourceBundle.getBundle("org/jphototagger/plugin/flickrupload/Bundle");
    private File imageFile;

    public static class ImageInfo {
        private final Image image;
        private final File imageFile;
        private final String title;
        private final String description;
        private final List<String> tags;

        public ImageInfo(Image image, File imageFile, String title, String description, List<String> tags) {
            if (imageFile == null) {
                throw new NullPointerException("imageFile == null");
            }

            if (title == null) {
                throw new NullPointerException("title == null");
            }

            if (description == null) {
                throw new NullPointerException("description == null");
            }

            if (tags == null) {
                throw new NullPointerException("tags == null");
            }

            this.image = image;
            this.imageFile = imageFile;
            this.title = title;
            this.description = description;
            this.tags = tags;
        }

        public Image getImage() {
            return image;
        }

        public String getDescription() {
            return description;
        }

        public File getImageFile() {
            return imageFile;
        }

        public List<String> getTags() {
            return tags;
        }

        public String getTitle() {
            return title;
        }
    }

    public FlickrImageInfoPanel() {
        initComponents();
        postInitComponents();
    }

    public FlickrImageInfoPanel(ImageInfo imageInfo) {
        initComponents();
        postInitComponents();

        setImage(imageInfo.getImage());
        setImageFile(imageInfo.getImageFile());
        setTitle(imageInfo.getTitle());
        setTags(imageInfo.getTags());
        setDescription(imageInfo.getDescription());
    }

    private void postInitComponents() {
        panelImage.setAlign(Align.LEFT_TOP);
    }

    public void setFocusToTitle() {
        textFieldTitle.requestFocusInWindow();
    }

    public ImageInfo getImageInfo() {
        return new ImageInfo(null, imageFile, getTitle(), getDescription(), getTags());
    }

    public void setImageFile(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        this.imageFile = imageFile;
        setLabelFilename();
    }

    public File getImageFile() {
        return imageFile;
    }

    private void setLabelFilename() {
        String name = imageFile.getName();

        if (name.length() > 35) {
            name = name.substring(0, 32) + "...";
        }

        labelFilename.setText(name);
    }

    public void setTags(Collection<? extends String> tags) {
        if (tags == null) {
            throw new NullPointerException("tags == null");
        }

        StringBuilder sb = new StringBuilder();
        int index = 0;

        for (String tag : tags) {
            sb.append(index++ == 0 ? "" : TAGS_DELIM);
            sb.append(tag);
        }

        textAreaTags.setText(sb.toString());
    }

    public List<String> getTags() {
        List<String> tags = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(textAreaTags.getText().trim(), TAGS_DELIM);

        while (st.hasMoreTokens()) {
            tags.add(st.nextToken().trim());
        }

        return tags;
    }

    public void setDescription(String description) {
        if (description == null) {
            throw new NullPointerException("description == null");
        }

        textAreaDescription.setText(description);
    }

    public String getDescription() {
        return textAreaDescription.getText().trim();
    }

    public void setTitle(String title) {
        if (title == null) {
            throw new NullPointerException("title == null");
        }

        textFieldTitle.setText(title);
    }

    public String getTitle() {
        return textFieldTitle.getText().trim();
    }

    public void setUpload(boolean upload) {
        checkBoxUpload.setSelected(upload);
    }

    public boolean isUpload() {
        return checkBoxUpload.isSelected();
    }

    public void setImage(Image image) {
        panelImage.setImage(image);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        checkBoxUpload = new javax.swing.JCheckBox();
        panelImage = new org.jphototagger.lib.component.ImagePanel();
        labelFilename = new javax.swing.JLabel();
        labelTitle = new javax.swing.JLabel();
        textFieldTitle = new javax.swing.JTextField();
        labelDescription = new javax.swing.JLabel();
        scrollPaneDescription = new javax.swing.JScrollPane();
        textAreaDescription = new TabOrEnterLeavingTextArea();
        labelTags = new javax.swing.JLabel();
        scrollPaneTags = new javax.swing.JScrollPane();
        textAreaTags = new TabOrEnterLeavingTextArea();

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        checkBoxUpload.setSelected(true);
        checkBoxUpload.setText(bundle.getString("FlickrImageInfoPanel.checkBoxUpload.text")); // NOI18N
        checkBoxUpload.setName("checkBoxUpload"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        add(checkBoxUpload, gridBagConstraints);

        panelImage.setMaximumSize(new java.awt.Dimension(IMAGE_WIDTH, IMAGE_WIDTH));
        panelImage.setMinimumSize(new java.awt.Dimension(IMAGE_WIDTH, IMAGE_WIDTH));
        panelImage.setName("panelImage"); // NOI18N
        panelImage.setPreferredSize(new java.awt.Dimension(IMAGE_WIDTH, IMAGE_WIDTH));

        javax.swing.GroupLayout panelImageLayout = new javax.swing.GroupLayout(panelImage);
        panelImage.setLayout(panelImageLayout);
        panelImageLayout.setHorizontalGroup(
            panelImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        panelImageLayout.setVerticalGroup(
            panelImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        add(panelImage, gridBagConstraints);

        labelFilename.setText(bundle.getString("FlickrImageInfoPanel.labelFilename.text")); // NOI18N
        labelFilename.setName("labelFilename"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 7, 0, 0);
        add(labelFilename, gridBagConstraints);

        labelTitle.setLabelFor(textFieldTitle);
        labelTitle.setText(bundle.getString("FlickrImageInfoPanel.labelTitle.text")); // NOI18N
        labelTitle.setName("labelTitle"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 7, 0, 0);
        add(labelTitle, gridBagConstraints);

        textFieldTitle.setColumns(25);
        textFieldTitle.setName("textFieldTitle"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
        add(textFieldTitle, gridBagConstraints);

        labelDescription.setLabelFor(textAreaDescription);
        labelDescription.setText(bundle.getString("FlickrImageInfoPanel.labelDescription.text")); // NOI18N
        labelDescription.setName("labelDescription"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 7, 0, 0);
        add(labelDescription, gridBagConstraints);

        scrollPaneDescription.setName("scrollPaneDescription"); // NOI18N

        textAreaDescription.setColumns(20);
        textAreaDescription.setRows(2);
        textAreaDescription.setName("textAreaDescription"); // NOI18N
        scrollPaneDescription.setViewportView(textAreaDescription);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(3, 7, 0, 0);
        add(scrollPaneDescription, gridBagConstraints);

        labelTags.setLabelFor(textAreaTags);
        labelTags.setText(bundle.getString("FlickrImageInfoPanel.labelTags.text")); // NOI18N
        labelTags.setName("labelTags"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 7, 0, 0);
        add(labelTags, gridBagConstraints);

        scrollPaneTags.setName("scrollPaneTags"); // NOI18N

        textAreaTags.setColumns(20);
        textAreaTags.setRows(2);
        textAreaTags.setName("textAreaTags"); // NOI18N
        scrollPaneTags.setViewportView(textAreaTags);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(3, 7, 0, 0);
        add(scrollPaneTags, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox checkBoxUpload;
    private javax.swing.JLabel labelDescription;
    private javax.swing.JLabel labelFilename;
    private javax.swing.JLabel labelTags;
    private javax.swing.JLabel labelTitle;
    private org.jphototagger.lib.component.ImagePanel panelImage;
    private javax.swing.JScrollPane scrollPaneDescription;
    private javax.swing.JScrollPane scrollPaneTags;
    private javax.swing.JTextArea textAreaDescription;
    private javax.swing.JTextArea textAreaTags;
    private javax.swing.JTextField textFieldTitle;
    // End of variables declaration//GEN-END:variables

}
