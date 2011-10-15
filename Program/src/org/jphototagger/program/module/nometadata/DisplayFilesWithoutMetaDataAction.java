package org.jphototagger.program.module.nometadata;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.openide.util.Lookup;

import org.jphototagger.api.windows.MainWindowManager;
import org.jphototagger.api.windows.WaitDisplayer;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.domain.thumbnails.OriginOfDisplayedThumbnails;
import org.jphototagger.domain.thumbnails.ThumbnailsDisplayer;
import org.jphototagger.lib.lookup.LookupAction;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.CollectionUtil;
import org.openide.util.LookupEvent;

/**
 * @author Elmar Baumann Elmar Baumann
 */
public final class DisplayFilesWithoutMetaDataAction extends LookupAction<MetaDataValue> {

    private static final long serialVersionUID = 1L;
    private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);

    public DisplayFilesWithoutMetaDataAction(Lookup lookup) {
        super(MetaDataValue.class, lookup);
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
        thumbnailsDisplayer.displayThumbnails(imageFiles, OriginOfDisplayedThumbnails.FILES_MATCHING_MISSING_METADATA);
    }

    private void setTitle(MetaDataValue metaDataValue) {
        MainWindowManager mainWindowManager = Lookup.getDefault().lookup(MainWindowManager.class);
        mainWindowManager.setMainWindowTitle(
                Bundle.getString(DisplayFilesWithoutMetaDataAction.class,
                "FilesWithoutMetaDataDisplayAction.MainWindowTitle", metaDataValue.getDescription()));
    }

    @Override
    public void resultChanged(LookupEvent evt) {
        Collection<? extends MetaDataValue> metaDataValues = getLookupContent();
        actionPerformed(metaDataValues);
    }

    @Override
    protected boolean isEnabled(Collection<? extends MetaDataValue> metaDataValues) {
        return metaDataValues.size() == 1;
    }

    @Override
    public void actionPerformed(Collection<? extends MetaDataValue> metaDataValues) {
        if (metaDataValues.size() == 1) {
            MetaDataValue metaDataValue = CollectionUtil.getFirstElement(metaDataValues);
            displayMetaDataValue(metaDataValue);
        }
    }
}
