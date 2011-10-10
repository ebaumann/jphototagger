package org.jphototagger.program.module.imagecollections;

import org.jphototagger.domain.imagecollections.ImageCollectionSortAscendingComparator;
import java.util.List;

import javax.swing.DefaultListModel;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.openide.util.Lookup;

import org.jphototagger.domain.imagecollections.ImageCollection;
import org.jphototagger.domain.repository.ImageCollectionsRepository;
import org.jphototagger.domain.repository.Repository;
import org.jphototagger.domain.repository.event.imagecollections.ImageCollectionDeletedEvent;
import org.jphototagger.domain.repository.event.imagecollections.ImageCollectionInsertedEvent;
import org.jphototagger.domain.repository.event.imagecollections.ImageCollectionRenamedEvent;
import org.jphototagger.lib.componentutil.ListUtil;

/**
 *
 * @author Elmar Baumann, Tobias Stening
 */
public final class ImageCollectionsListModel extends DefaultListModel {

    private static final long serialVersionUID = -929229489709109467L;
    private final ImageCollectionsRepository imageCollectionsRepo = Lookup.getDefault().lookup(ImageCollectionsRepository.class);

    public ImageCollectionsListModel() {
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
    public void imageCollectionDelted(ImageCollectionDeletedEvent evt) {
        String collectionName = evt.getCollectionName();

        removeElement(collectionName);
    }
}
