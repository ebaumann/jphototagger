package org.jphototagger.api.progress;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface MainWindowProgressBarProvider extends ProgressListener {

    boolean isDisplayProgressOfSource(Object source);
}
