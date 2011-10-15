package org.jphototagger.program.module.dfwm;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

import org.jphototagger.api.windows.MainWindowManager;
import org.jphototagger.api.windows.WaitDisplayer;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.domain.thumbnails.OriginOfDisplayedThumbnails;
import org.jphototagger.domain.thumbnails.ThumbnailsDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.CollectionUtil;

/**
 * @author Elmar Baumann Elmar Baumann
 */
public final class DisplayFilesWithoutMetaDataAction implements LookupListener {

    private static final long serialVersionUID = 1L;
    private final Lookup.Result<? extends MetaDataValue> lookupResult;
    private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);

    public DisplayFilesWithoutMetaDataAction(Lookup lookup) {
        lookupResult = lookup.lookupResult(MetaDataValue.class);
        lookupResult.addLookupListener(this);
        resultChanged(null);
    }

    @Override
    public void resultChanged(LookupEvent evt) {
        Collection<? extends MetaDataValue> metaDataValues = lookupResult.allInstances();
        if (metaDataValues.size() == 1) {
            MetaDataValue metaDataValue = CollectionUtil.getFirstElement(metaDataValues);
            displayMetaDataValue(metaDataValue);
        }
    }

    private void displayMetaDataValue(MetaDataValue metaDataValue) {
        WaitDisplayer waitDisplayer = Lookup.getDefault().lookup(WaitDisplayer.class);
        waitDisplayer.show();
        setTitle(metaDataValue);
        displayThumbnails(metaDataValue);
        waitDisplayer.hide();
    }

    private void displayThumbnails(MetaDataValue selValue) {
        List<File> imageFiles = repo.findImageFilesWithoutDataValue(selValue);
        ThumbnailsDisplayer thumbnailsDisplayer = Lookup.getDefault().lookup(ThumbnailsDisplayer.class);
        thumbnailsDisplayer.displayFiles(imageFiles, OriginOfDisplayedThumbnails.FILES_MATCHING_MISSING_METADATA);
    }

    private void setTitle(MetaDataValue metaDataValue) {
        MainWindowManager mainWindowManager = Lookup.getDefault().lookup(MainWindowManager.class);
        mainWindowManager.setMainWindowTitle(
                Bundle.getString(DisplayFilesWithoutMetaDataAction.class,
                "FilesWithoutMetaDataDisplayAction.MainWindowTitle", metaDataValue.getDescription()));
    }
}
