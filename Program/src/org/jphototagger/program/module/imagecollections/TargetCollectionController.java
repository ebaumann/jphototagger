package org.jphototagger.program.module.imagecollections;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.messages.MessageType;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesChangedEvent;
import org.jphototagger.api.windows.MainWindowManager;
import org.jphototagger.domain.repository.ImageCollectionsRepository;
import org.jphototagger.domain.repository.event.imagecollections.ImageCollectionDeletedEvent;
import org.jphototagger.domain.repository.event.imagecollections.ImageCollectionRenamedEvent;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;
import org.jphototagger.program.module.thumbnails.ThumbnailsPopupMenu;
import org.jphototagger.program.resource.GUI;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class TargetCollectionController implements ActionListener, KeyListener {

    public static final String KEY_TARGET_COLLECTION_NAME = "TargetCollectionName";
    private String targetCollectionName;

    public TargetCollectionController() {
        init();
    }

    private void init() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        targetCollectionName = prefs.getString(KEY_TARGET_COLLECTION_NAME);
        listen();
    }

    private void listen() {
        GUI.getThumbnailsPanel().addKeyListener(this);
        ThumbnailsPopupMenu.INSTANCE.getItemTargetCollection().addActionListener(this);
        AnnotationProcessor.process(this);
    }

    @EventSubscriber(eventClass=PreferencesChangedEvent.class)
    public void preferencesChanged(PreferencesChangedEvent e) {
        if (KEY_TARGET_COLLECTION_NAME.equals(e.getKey())) {
            targetCollectionName = (String) e.getNewValue();
        }
    }

    @EventSubscriber(eventClass=ImageCollectionDeletedEvent.class)
    public void imageCollectionDeleted(ImageCollectionDeletedEvent e) {
        if (StringUtil.hasContent(targetCollectionName)
                && Objects.equals(targetCollectionName, e.getCollectionName())) {
            Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
            prefs.removeKey(KEY_TARGET_COLLECTION_NAME);
        }
    }

    @EventSubscriber(eventClass=ImageCollectionRenamedEvent.class)
    public void imageCollectionRenamed(ImageCollectionRenamedEvent e) {
        if (StringUtil.hasContent(targetCollectionName)
                && Objects.equals(targetCollectionName, e.getFromName())) {
            Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
            prefs.setString(KEY_TARGET_COLLECTION_NAME, e.getToName());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        addOrRemoveSelectedImages();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_B) {
            addOrRemoveSelectedImages();
        }
    }

    private void addOrRemoveSelectedImages() {
        if (!StringUtil.hasContent(targetCollectionName)) {
            return;
        }
        ImageCollectionsRepository repo = Lookup.getDefault().lookup(ImageCollectionsRepository.class);
        if (repo.existsImageCollection(targetCollectionName)) {
            List<File> selectedFiles = GUI.getThumbnailsPanel().getSelectedFiles();
            List<File> filesToAdd = new ArrayList<>(selectedFiles.size());
            List<File> filesToRemove = new ArrayList<>(selectedFiles.size());
            for (File file : selectedFiles) {
                if (repo.containsFile(targetCollectionName, file.getAbsolutePath())) {
                    filesToRemove.add(file);
                } else {
                    filesToAdd.add(file);
                }
            }
            int countAdded = 0;
            int countDeleted = 0;
            if (!filesToAdd.isEmpty()) {
                if (repo.insertImagesIntoImageCollection(targetCollectionName, filesToAdd)) {
                    countAdded = filesToAdd.size();
                }
            }
            if (!filesToRemove.isEmpty()) {
                countDeleted = repo.deleteImagesFromImageCollection(targetCollectionName, filesToRemove);
            }
            if (countAdded > 0 || countDeleted > 0) {
                MainWindowManager wm = Lookup.getDefault().lookup(MainWindowManager.class);
                wm.setMainWindowStatusbarText(
                        Bundle.getString(TargetCollectionController.class, "TargetCollectionController.Info.Statusbar", StringUtil.toMaxLengthEndingDots(targetCollectionName, 35), countAdded, countDeleted),
                        MessageType.INFO,
                        1500);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // ignore
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // ignore
    }
}
