package org.jphototagger.dfwm;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
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
import org.jphototagger.domain.thumbnails.ThumbnailsPanelSettings;
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
        listen();
    }

    private void listen() {
        lookupResult.addLookupListener(this);
        resultChanged(null);
        AnnotationProcessor.process(this);
    }

    @EventSubscriber(eventClass = ThumbnailsPanelRefreshEvent.class)
    public void refresh(ThumbnailsPanelRefreshEvent evt) {
        if (selectedMetaDataValue != null) {
            displaySelectedMetaDataValue(evt.getThumbnailsPanelSettings());
        }
    }

    @Override
    public void resultChanged(LookupEvent evt) {
        Collection<? extends MetaDataValue> metaDataValues = lookupResult.allInstances();
        selectedMetaDataValue = metaDataValues.size() == 1
                ? CollectionUtil.getFirstElement(metaDataValues)
                : null;
        if (selectedMetaDataValue != null) {
            displaySelectedMetaDataValue(null);
        }
    }

    private void displaySelectedMetaDataValue(ThumbnailsPanelSettings settings) {
        if (selectedMetaDataValue == null) {
            return;
        }
        WaitDisplayer waitDisplayer = Lookup.getDefault().lookup(WaitDisplayer.class);
        waitDisplayer.show();
        setTitle();
        displayThumbnails(settings);
        waitDisplayer.hide();
    }

    private void displayThumbnails(ThumbnailsPanelSettings settings) {
        List<File> imageFiles = repo.findImageFilesWithoutDataValue(selectedMetaDataValue);
        tnDisplayer.displayFiles(imageFiles, OriginOfDisplayedThumbnails.FILES_MATCHING_MISSING_METADATA);
        if (settings != null) {
            tnDisplayer.applyThumbnailsPanelSettings(settings);
    }
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
        if (selectedMetaDataValue == null) {
            return;
        }
        boolean containsMdv = !deleted && xmp.contains(selectedMetaDataValue);
        boolean imageDisplayed = tnDisplayer.isDisplayFile(imageFile);
        if (imageDisplayed && containsMdv || !imageDisplayed && !containsMdv) {
            displaySelectedMetaDataValue(createThumbnailsPanelSettings());
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
            displaySelectedMetaDataValue(createThumbnailsPanelSettings());
        }
    }

    public ThumbnailsPanelSettings createThumbnailsPanelSettings() {
        ThumbnailsPanelSettings settings = new ThumbnailsPanelSettings(tnDisplayer.getViewPosition(), Collections.<Integer>emptyList());
        settings.setSelectedFiles(tnDisplayer.getSelectedFiles());
        return settings;
}
}
