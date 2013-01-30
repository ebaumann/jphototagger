package org.jphototagger.dfwm;

import java.io.File;
import java.util.Collection;
import java.util.List;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.windows.MainWindowManager;
import org.jphototagger.api.windows.WaitDisplayer;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.domain.repository.event.xmp.XmpDeletedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpInsertedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpUpdatedEvent;
import org.jphototagger.domain.thumbnails.OriginOfDisplayedThumbnails;
import org.jphototagger.domain.thumbnails.ThumbnailsDisplayer;
import org.jphototagger.domain.thumbnails.event.ThumbnailsPanelRefreshEvent;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.CollectionUtil;
import org.jphototagger.lib.util.ObjectUtil;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 * @author Elmar Baumann Elmar Baumann
 */
public final class DisplayFilesWithoutMetaDataAction implements LookupListener {

    private static final long serialVersionUID = 1L;
    private final Lookup.Result<? extends MetaDataValue> lookupResult;
    private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);
    private final ThumbnailsDisplayer tnDisplayer = Lookup.getDefault().lookup(ThumbnailsDisplayer.class);
    private MetaDataValue selectedMetaDataValue;

    public DisplayFilesWithoutMetaDataAction(Lookup lookup) {
        lookupResult = lookup.lookupResult(MetaDataValue.class);
        lookupResult.addLookupListener(this);
        resultChanged(null);
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

    @EventSubscriber(eventClass = ThumbnailsPanelRefreshEvent.class)
    public void refresh(ThumbnailsPanelRefreshEvent evt) {
        if (selectedMetaDataValue != null) {
            displaySelectedMetaDataValue();
        }
    }

    @Override
    public void resultChanged(LookupEvent evt) {
        Collection<? extends MetaDataValue> metaDataValues = lookupResult.allInstances();
        selectedMetaDataValue = metaDataValues.size() == 1
                ? CollectionUtil.getFirstElement(metaDataValues)
                : null;
        if (selectedMetaDataValue != null) {
            displaySelectedMetaDataValue();
        }
    }

    private void displaySelectedMetaDataValue() {
        if (selectedMetaDataValue == null) {
            return;
        }
        WaitDisplayer waitDisplayer = Lookup.getDefault().lookup(WaitDisplayer.class);
        waitDisplayer.show();
        setTitle();
        displayThumbnails();
        waitDisplayer.hide();
    }

    private void displayThumbnails() {
        List<File> imageFiles = repo.findImageFilesWithoutDataValue(selectedMetaDataValue);
        ThumbnailsDisplayer thumbnailsDisplayer = Lookup.getDefault().lookup(ThumbnailsDisplayer.class);
        thumbnailsDisplayer.displayFiles(imageFiles, OriginOfDisplayedThumbnails.FILES_MATCHING_MISSING_METADATA);
    }

    private void setTitle() {
        MainWindowManager mainWindowManager = Lookup.getDefault().lookup(MainWindowManager.class);
        mainWindowManager.setMainWindowTitle(
                Bundle.getString(DisplayFilesWithoutMetaDataAction.class, "FilesWithoutMetaDataDisplayAction.MainWindowTitle",
                selectedMetaDataValue.getDescription()));
    }

    @EventSubscriber(eventClass = XmpInsertedEvent.class)
    public void xmpInserted(XmpInsertedEvent evt) {
        update(evt.getImageFile(), evt.getXmp(), false);
    }

    @EventSubscriber(eventClass = XmpDeletedEvent.class)
    public void xmpDeleted(XmpDeletedEvent evt) {
        update(evt.getImageFile(), evt.getXmp(), true);
    }

    private void update(File imageFile, Xmp xmp, boolean deleted) {
        if (selectedMetaDataValue == null || tnDisplayer == null) {
            return;
        }
        boolean containsMdv = !deleted && xmp.contains(selectedMetaDataValue);
        boolean imageDisplayed = tnDisplayer.isDisplayFile(imageFile);
        if (imageDisplayed && containsMdv || !imageDisplayed && !containsMdv) {
            displaySelectedMetaDataValue();
        }
    }

    @EventSubscriber(eventClass = XmpUpdatedEvent.class)
    public void xmpUpdated(XmpUpdatedEvent evt) {
        if (selectedMetaDataValue == null) {
            return;
        }
        Object oldValue = evt.getOldXmp().getValue(selectedMetaDataValue);
        Object updatedValue = evt.getUpdatedXmp().getValue(selectedMetaDataValue);
        if (!ObjectUtil.equals(oldValue, updatedValue)) {
            displaySelectedMetaDataValue();
        }
    }
}
