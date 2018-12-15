package org.jphototagger.program.module.thumbnails.info;

import java.awt.Component;
import java.io.File;
import java.util.Collections;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.metadata.thumbnails.ThumbnailInfoProvider;
import org.jphototagger.domain.thumbnails.event.ThumbnailsSelectionChangedEvent;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = ThumbnailInfoProvider.class)
public class SidecarSuffixesInfoPanel extends javax.swing.JPanel implements ThumbnailInfoProvider{

    private static final long serialVersionUID = 1L;
    private final Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
    private final JLabel infoLabel = UiFactory.label(Bundle.getString(SidecarSuffixesInfoPanel.class, "SidecarSuffixesInfoPanel.Label.Info"));
    private int suffixCount;

    public SidecarSuffixesInfoPanel() {
        org.jphototagger.resources.UiFactory.configure(this);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        List<String> suffixes = prefs.getStringCollection(SidecarSuffixesInfoSettingsPanel.KEY_FILE_SUFFIXES);
        Collections.sort(suffixes, String.CASE_INSENSITIVE_ORDER);
        if (!suffixes.isEmpty()) {
            add(infoLabel);
        }
        for (String suffix : suffixes) {
            addSuffix(suffix);
            suffixCount++;
        }
        AnnotationProcessor.process(this);
    }

    private void addSuffix(String suffix) {
        if (suffixCount == 0) {
            add(infoLabel);
        }
        JCheckBox checkBox = UiFactory.checkBox(suffix);
        checkBox.setEnabled(false);
        add(checkBox);
        suffixCount++;
    }

    private void removeSuffix(String suffix) {
        for (JCheckBox checkBox : ComponentUtil.getAllOf(this, JCheckBox.class)) {
            if (suffix.equals(checkBox.getText())) {
                remove(checkBox);
                suffixCount--;
            }
        }
        if (suffixCount == 0) {
            remove(infoLabel);
        }
    }

    @EventSubscriber(eventClass=SidecarSuffixInfoAddedEvent.class)
    public void suffixInfoAdded(SidecarSuffixInfoAddedEvent evt) {
        addSuffix(evt.getSuffix());
    }

    @EventSubscriber(eventClass=SidecarSuffixInfoRemovedEvent.class)
    public void suffixInfoRemoved(SidecarSuffixInfoRemovedEvent evt) {
        removeSuffix(evt.getSuffix());
    }

    @EventSubscriber(eventClass = ThumbnailsSelectionChangedEvent.class)
    public void thumbnailsSelectionChanged(final ThumbnailsSelectionChangedEvent evt) {
        setChecked(evt.getSelectedFiles());
    }

    private void setChecked(List<File> files) {
        for (JCheckBox checkBox : ComponentUtil.getAllOf(this, JCheckBox.class)) {
            checkBox.setSelected(allHaveSidecarFile(files, checkBox.getText()));
        }
    }

    private boolean allHaveSidecarFile(List<File> files, String sidecarSuffix) {
        if (files.isEmpty()) {
            return false;
        }
        for (File file : files) {
            if (!hasSidecarFile(file, sidecarSuffix)) {
                return false;
            }
        }
        return true;
    }

    private boolean hasSidecarFile(File file, String sidecarSuffix) {
        String parent = file.getParent();
        if (!StringUtil.hasContent(parent)) {
            return false;
        }
        String prefix = FileUtil.getPrefix(file);
        File sidecarFile = new File("---012345-JPhotoTagger-should-not-exist-543210---");
        if (sidecarSuffix.contains("/")) {
            int lasDirDelimIndex = sidecarSuffix.lastIndexOf('/');
            if (lasDirDelimIndex < sidecarSuffix.length() - 1) {
                String subdirs = sidecarSuffix.substring(0, lasDirDelimIndex).replace("/", File.separator);
                String suffix = sidecarSuffix.substring(lasDirDelimIndex + 1);
                sidecarFile = new File(parent + File.separator + subdirs + File.separator + prefix + '.' + suffix);
            }
        } else {
            sidecarFile = new File(parent + File.separator + prefix + '.' + sidecarSuffix);
        }
        return sidecarFile.isFile();
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public int getPosition() {
        return 0;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents

        setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, UiFactory.scale(5), 0));
    }//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
