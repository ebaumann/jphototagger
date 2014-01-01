package org.jphototagger.program.filefilter;

import java.io.File;
import java.io.FileFilter;
import org.jphototagger.domain.filefilter.AppFileFilterProvider;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = AppFileFilterProvider.class)
public final class AppFileFilterProviderImpl implements AppFileFilterProvider {

    @Override
    public FileFilter getAcceptedImageFilesFileFilter() {
        return AppFileFilters.INSTANCE.getAllAcceptedImageFilesFilter();
    }

    @Override
    public boolean isAcceptedImageFile(File file) {
        return AppFileFilters.INSTANCE.isAcceptedImageFile(file);
    }
}
