package org.jphototagger.domain.repository;

import java.io.File;
import java.util.Collection;
import org.jphototagger.api.concurrent.Cancelable;
import org.jphototagger.api.progress.ProgressListener;

/**
 * @author Elmar Baumann
 */
public interface SaveToOrUpdateFilesInRepository extends Cancelable {

    /**
     * @param files
     * @param saveOrUpdate
     * @return Instance to use. <em>Do not the instance returned from the Service
     * Provider Framework, use the instance returned from this method!</em>
     */
    SaveToOrUpdateFilesInRepository createInstance(Collection<? extends File> files, SaveOrUpdate... saveOrUpdate);

    void saveOrUpdateInNewThread();

    void saveOrUpdateWaitForTermination();

    void addProgressListener(ProgressListener progessListener);

    void removeProgressListener(ProgressListener progessListener);
}
