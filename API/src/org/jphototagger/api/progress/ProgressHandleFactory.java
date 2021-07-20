package org.jphototagger.api.progress;

import org.jphototagger.api.concurrent.Cancelable;

/**
 * @author Elmar Baumann
 */
public interface ProgressHandleFactory {

    ProgressHandle createProgressHandle();

    ProgressHandle createProgressHandle(Cancelable cancelable);
}
