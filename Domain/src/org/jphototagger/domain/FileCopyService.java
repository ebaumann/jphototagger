package org.jphototagger.domain;

import java.util.Collection;
import org.jphototagger.api.concurrent.Cancelable;
import org.jphototagger.api.file.CopyMoveFilesOptions;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.lib.io.SourceTargetFile;

/**
 * @author Elmar Baumann
 */
public interface FileCopyService extends Cancelable {

    /**
     * @param sourceTargetFiles
     * @param options
     * @return Instance to use. <em>Do not the instance returned from the Service
     * Provider Framework, use the instance returned from this method!</em>
     */
    FileCopyService createInstance(Collection<? extends SourceTargetFile> sourceTargetFiles, CopyMoveFilesOptions options);

    void addProgressListener(ProgressListener progessListener);

    void removeProgressListener(ProgressListener progessListener);

    void copyInNewThread();

    void copyWaitForTermination();
}
