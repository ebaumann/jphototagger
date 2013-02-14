package org.jphototagger.program.module.imagecollections;

import java.util.List;
import javax.swing.DefaultListModel;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.imagecollections.ImageCollection;
import org.jphototagger.domain.imagecollections.ImageCollectionSortAscendingComparator;
import org.jphototagger.domain.repository.ImageCollectionsRepository;
import org.jphototagger.domain.repository.Repository;
import org.jphototagger.domain.repository.event.imagecollections.ImageCollectionDeletedEvent;
import org.jphototagger.domain.repository.event.imagecollections.ImageCollectionInsertedEvent;
import org.jphototagger.domain.repository.event.imagecollections.ImageCollectionRenamedEvent;
import org.jphototagger.lib.swing.util.ListUtil;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann, Tobias Stening
 */
public final class ImageCollectionsListModel extends DefaultListModel<Object> {

    private static final long serialVersionUID = 1L;
    private static final String PREF_KEY_LOCALIZATION_BUG_FIX = "ImageCollectionsListModel.LocalizationBugFix";
    private final ImageCollectionsRepository imageCollectionsRepo = Lookup.getDefault().lookup(ImageCollectionsRepository.class);

    public ImageCollectionsListModel() {
        applyLocalizationBugFix();
        addElements();
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

    private void addElements() {
        Repository repo = Lookup.getDefault().lookup(Repository.class);
        if (repo == null || !repo.isInit()) {
            return;
        }
        List<String> collections = imageCollectionsRepo.findAllImageCollectionNames();
        addSpecialCollections();
        for (String collection : collections) {
            if (!ImageCollection.isSpecialCollection(collection)) {
                addElement(collection);
            }
        }
    }

    private void addSpecialCollections() {
        for (String collection : ImageCollectionSortAscendingComparator.SORT_ORDER_OF_SPECIAL_COLLECTION.keySet()) {
            addElement(collection);
        }
    }

    @EventSubscriber(eventClass = ImageCollectionRenamedEvent.class)
    public void imageCollectionRenamed(ImageCollectionRenamedEvent evt) {
        String fromName = evt.getFromName();
        String toName = evt.getToName();
        int index = indexOf(fromName);
        if (index >= 0) {
            remove(index);
            insertElementAt(toName, index);
        }
    }

    @EventSubscriber(eventClass = ImageCollectionInsertedEvent.class)
    public void imageCollectionInserted(ImageCollectionInsertedEvent evt) {
        String collectionName = evt.getCollectionName();
        int startIndex = ImageCollection.getSpecialCollectionCount();
        int endIndex = getSize() - 1;
        ListUtil.insertSorted(this, collectionName, StringAscendingComparator.INSTANCE, startIndex, endIndex);
    }

    @EventSubscriber(eventClass = ImageCollectionDeletedEvent.class)
    public void imageCollectionDeleted(ImageCollectionDeletedEvent evt) {
        String collectionName = evt.getCollectionName();
        removeElement(collectionName);
    }

    private void applyLocalizationBugFix() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        if (!prefs.containsKey(PREF_KEY_LOCALIZATION_BUG_FIX) || !prefs.getBoolean(PREF_KEY_LOCALIZATION_BUG_FIX)) {
            imageCollectionsRepo.updateRenameImageCollection("Zuletzt importiert", ImageCollection.PREVIOUS_IMPORT_NAME);
            imageCollectionsRepo.updateRenameImageCollection("Ausgewählt", ImageCollection.PICKED_NAME);
            imageCollectionsRepo.updateRenameImageCollection("Verworfen", ImageCollection.REJECTED_NAME);
            imageCollectionsRepo.updateRenameImageCollection("Previous import", ImageCollection.PREVIOUS_IMPORT_NAME);
            imageCollectionsRepo.updateRenameImageCollection("Picked", ImageCollection.PICKED_NAME);
            imageCollectionsRepo.updateRenameImageCollection("Rejected", ImageCollection.REJECTED_NAME);
            // Delete: updateRename renames only 1 times, the other locale will not be renamed and thus further exists
            imageCollectionsRepo.deleteImageCollection("Zuletzt importiert");
            imageCollectionsRepo.deleteImageCollection("Ausgewählt");
            imageCollectionsRepo.deleteImageCollection("Verworfen");
            imageCollectionsRepo.deleteImageCollection("Previous import");
            imageCollectionsRepo.deleteImageCollection("Picked");
            imageCollectionsRepo.deleteImageCollection("Rejected");
            prefs.setBoolean(PREF_KEY_LOCALIZATION_BUG_FIX, true);
}
    }
}
