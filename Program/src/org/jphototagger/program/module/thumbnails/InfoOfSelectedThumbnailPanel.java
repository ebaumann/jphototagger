package org.jphototagger.program.module.thumbnails;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.swing.JLabel;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.metadata.thumbnails.ThumbnailInfoProvider;
import org.jphototagger.domain.metadata.xmp.XmpSidecarFileResolver;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.domain.thumbnails.ThumbnailsPanelBottomComponentProvider;
import org.jphototagger.domain.thumbnails.event.ThumbnailsSelectionChangedEvent;
import org.jphototagger.lib.api.PositionProviderAscendingComparator;
import org.jphototagger.lib.awt.DesktopUtil;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.CollectionUtil;
import org.jphototagger.program.module.thumbnails.info.SidecarSuffixInfoAddedEvent;
import org.jphototagger.program.module.thumbnails.info.SidecarSuffixInfoRemovedEvent;
import org.jphototagger.program.types.ByteSizeUnit;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = ThumbnailsPanelBottomComponentProvider.class)
public class InfoOfSelectedThumbnailPanel extends javax.swing.JPanel implements ThumbnailsPanelBottomComponentProvider {

    private static final long serialVersionUID = 1L;
    private static final String PROMPT_DATE_TIME_TAKEN = Bundle.getString(InfoOfSelectedThumbnailPanel.class, "InfoOfSelectedThumbnailPanel.Prompt.DateTimeTaken");
    private static final String PROMPT_DATE_TIME_LAST_MODIFIED = Bundle.getString(InfoOfSelectedThumbnailPanel.class, "InfoOfSelectedThumbnailPanel.Prompt.LastModified");
    private final XmpSidecarFileResolver xmpSidecarFileResolver = Lookup.getDefault().lookup(XmpSidecarFileResolver.class);
    private File selectedFile;

    public InfoOfSelectedThumbnailPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        lookupThumbnailInfoProviders();
        AnnotationProcessor.process(this);
    }

    private void lookupThumbnailInfoProviders() {
        List<ThumbnailInfoProvider> providers = new ArrayList<ThumbnailInfoProvider>(Lookup.getDefault().lookupAll(ThumbnailInfoProvider.class));
        Collections.sort(providers, PositionProviderAscendingComparator.INSTANCE);
        for (ThumbnailInfoProvider provider : providers) {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.WEST;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            panelThumbnailInfoProviders.add(provider.getComponent(), gbc);
        }
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @EventSubscriber(eventClass = ThumbnailsSelectionChangedEvent.class)
    public void thumbnailsSelectionChanged(final ThumbnailsSelectionChangedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                int selectionCount = evt.getSelectionCount();
                if (selectionCount == 1) {
                    List<File> selectedFiles = evt.getSelectedFiles();
                    selectedFile = CollectionUtil.getFirstElement(selectedFiles);
                } else {
                    selectedFile = null;
                }
                selectedFileChanged();
            }
        });
    }

    private void selectedFileChanged() {
        reset();
        if (selectedFile == null) {
            return;
        }
        labelSelectedFilePathName.setText(selectedFile.getAbsolutePath());
        File sidecarFile = xmpSidecarFileResolver.getXmpSidecarFileOrNullIfNotExists(selectedFile);
        checkBoxSelectedFileHasSidecarFile.setSelected(sidecarFile != null);
        buttonOpenDirectoryOfSelectedFile.setEnabled(selectedFile.getParentFile() != null);
        setDateTimeTake();
        setSize();
    }

    private void setDateTimeTake() {
        ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);
        long millis = repo.findExifDateTimeOriginalTimestamp(selectedFile);
        labelSelectedFileDatePrompt.setText(millis > 0 ? PROMPT_DATE_TIME_TAKEN : PROMPT_DATE_TIME_LAST_MODIFIED);
        if (millis <= 0) {
            millis = selectedFile.lastModified();
        }
        setDateToLabel(millis, labelSelectedFileDate);
    }

    private void setDateToLabel(long millis, JLabel label) {
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
        label.setText(df.format(new Date(millis)));
    }

    private void setSize() {
        long length = selectedFile.length();
        ByteSizeUnit unit = ByteSizeUnit.unit(length);
        long unitLength = length / unit.bytes();
        labelSelectedFileSize.setText(unitLength + " " + unit.toString());
    }

    private void reset() {
        labelSelectedFilePathName.setText("");
        labelSelectedFileDate.setText("");
        labelSelectedFileSize.setText("");
        checkBoxSelectedFileHasSidecarFile.setSelected(false);
        buttonOpenDirectoryOfSelectedFile.setEnabled(false);
    }

    private void openDirectoryOfSelectedFile() {
        if (selectedFile != null) {
            File directory = selectedFile.getParentFile();
            if (directory != null) {
                DesktopUtil.open(directory, "JPhotoTagger.BrowseFolder.Executable");
            }
        }
    }

    @EventSubscriber(eventClass=SidecarSuffixInfoAddedEvent.class)
    public void suffixInfoAdded(SidecarSuffixInfoAddedEvent evt) {
        ComponentUtil.forceRepaint(this);
    }

    @EventSubscriber(eventClass=SidecarSuffixInfoRemovedEvent.class)
    public void suffixInfoRemoved(SidecarSuffixInfoRemovedEvent evt) {
        ComponentUtil.forceRepaint(this);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        panelContent = new javax.swing.JPanel();
        panelSelectedFilePath = new javax.swing.JPanel();
        labelSelectedFilePathPrompt = new javax.swing.JLabel();
        labelSelectedFilePathPrompt.setFont(ComponentUtil.createBoldFont(labelSelectedFilePathPrompt.getFont()));
        labelSelectedFilePathName = new javax.swing.JLabel();
        buttonOpenDirectoryOfSelectedFile = new javax.swing.JButton();
        panelSelectedFileDateSizeXmpExists = new javax.swing.JPanel();
        labelSelectedFileDatePrompt = new javax.swing.JLabel();
        labelSelectedFileDatePrompt.setFont(ComponentUtil.createBoldFont(labelSelectedFileDatePrompt.getFont()));
        labelSelectedFileDate = new javax.swing.JLabel();
        labelSelectedFileSizePrompt = new javax.swing.JLabel();
        labelSelectedFileSizePrompt.setFont(ComponentUtil.createBoldFont(labelSelectedFileSizePrompt.getFont()));
        labelSelectedFileSize = new javax.swing.JLabel();
        checkBoxSelectedFileHasSidecarFile = org.jphototagger.resources.UiFactory.checkBox();
        panelThumbnailInfoProviders = new javax.swing.JPanel();
        panelPadding = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        panelContent.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString(getClass(), "InfoOfSelectedThumbnailPanel.panelContent.border.title"))); // NOI18N
        panelContent.setLayout(new java.awt.GridBagLayout());

        panelSelectedFilePath.setLayout(new java.awt.GridBagLayout());

        labelSelectedFilePathPrompt.setText(Bundle.getString(getClass(), "InfoOfSelectedThumbnailPanel.labelSelectedFilePathPrompt.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelSelectedFilePath.add(labelSelectedFilePathPrompt, gridBagConstraints);

        labelSelectedFilePathName.setText(Bundle.getString(getClass(), "InfoOfSelectedThumbnailPanel.labelSelectedFilePathName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelSelectedFilePath.add(labelSelectedFilePathName, gridBagConstraints);

        buttonOpenDirectoryOfSelectedFile.setText(Bundle.getString(getClass(), "InfoOfSelectedThumbnailPanel.buttonOpenDirectoryOfSelectedFile.text")); // NOI18N
        buttonOpenDirectoryOfSelectedFile.setEnabled(false);
        buttonOpenDirectoryOfSelectedFile.setMargin(org.jphototagger.resources.UiFactory.insets(0, 0, 0, 0));
        buttonOpenDirectoryOfSelectedFile.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOpenDirectoryOfSelectedFileActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelSelectedFilePath.add(buttonOpenDirectoryOfSelectedFile, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 5);
        panelContent.add(panelSelectedFilePath, gridBagConstraints);

        panelSelectedFileDateSizeXmpExists.setLayout(new java.awt.GridBagLayout());

        labelSelectedFileDatePrompt.setText(Bundle.getString(getClass(), "InfoOfSelectedThumbnailPanel.labelSelectedFileDatePrompt.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelSelectedFileDateSizeXmpExists.add(labelSelectedFileDatePrompt, gridBagConstraints);

        labelSelectedFileDate.setText(Bundle.getString(getClass(), "InfoOfSelectedThumbnailPanel.labelSelectedFileDate.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelSelectedFileDateSizeXmpExists.add(labelSelectedFileDate, gridBagConstraints);

        labelSelectedFileSizePrompt.setText(Bundle.getString(getClass(), "InfoOfSelectedThumbnailPanel.labelSelectedFileSizePrompt.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelSelectedFileDateSizeXmpExists.add(labelSelectedFileSizePrompt, gridBagConstraints);

        labelSelectedFileSize.setText(Bundle.getString(getClass(), "InfoOfSelectedThumbnailPanel.labelSelectedFileSize.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelSelectedFileDateSizeXmpExists.add(labelSelectedFileSize, gridBagConstraints);

        checkBoxSelectedFileHasSidecarFile.setText(Bundle.getString(getClass(), "InfoOfSelectedThumbnailPanel.checkBoxSelectedFileHasSidecarFile.text")); // NOI18N
        checkBoxSelectedFileHasSidecarFile.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelSelectedFileDateSizeXmpExists.add(checkBoxSelectedFileHasSidecarFile, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 0, 5);
        panelContent.add(panelSelectedFileDateSizeXmpExists, gridBagConstraints);

        panelThumbnailInfoProviders.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panelContent.add(panelThumbnailInfoProviders, gridBagConstraints);

        panelPadding.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        panelContent.add(panelPadding, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(panelContent, gridBagConstraints);
    }//GEN-END:initComponents

    private void buttonOpenDirectoryOfSelectedFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOpenDirectoryOfSelectedFileActionPerformed
        openDirectoryOfSelectedFile();
    }//GEN-LAST:event_buttonOpenDirectoryOfSelectedFileActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonOpenDirectoryOfSelectedFile;
    private javax.swing.JCheckBox checkBoxSelectedFileHasSidecarFile;
    private javax.swing.JLabel labelSelectedFileDate;
    private javax.swing.JLabel labelSelectedFileDatePrompt;
    private javax.swing.JLabel labelSelectedFilePathName;
    private javax.swing.JLabel labelSelectedFilePathPrompt;
    private javax.swing.JLabel labelSelectedFileSize;
    private javax.swing.JLabel labelSelectedFileSizePrompt;
    private javax.swing.JPanel panelContent;
    private javax.swing.JPanel panelPadding;
    private javax.swing.JPanel panelSelectedFileDateSizeXmpExists;
    private javax.swing.JPanel panelSelectedFilePath;
    private javax.swing.JPanel panelThumbnailInfoProviders;
    // End of variables declaration//GEN-END:variables
}
