package org.jphototagger.domain.repository;

/**
 * Statistics about Dublin Core (DC) subjects (keywords) in the
 * {@link ImageFilesRepository}.
 *
 * @author Elmar Baumann
 */
public interface DcSubjectsStatistics {

    /**
     * Returns in how any images a specific Dublin Core subject (keyword) is
     * contained.
     *
     * @param dcSubject DC subject (keyword)
     *
     * @return number of images containing that DC subject (keyword)
     */
    int getImageCountOfDcSubject(String dcSubject);

    /**
     * A Listener will be notified, when Dublin Core subjects (keywords) in the
     * repository were changed.
     */
    interface Listener {

        /**
         * Notification about a change of the DC subject count contained in
         * images.
         *
         * @param dcSubject DC subject (keyword) which count has been changed
         * @param newCount  new count of images containing that DC subject
         *                  (keyword)
         */
        void imageCountContainingDcSubjectChanged(String dcSubject, int newCount);
    }

    /**
     * Adds a statistics listener.
     *
     * @param listener listener to add.
     */
    void addListener(Listener listener);

    /**
     * Removes a statistics listener.
     *
     * @param listener listener to add.
     */
    void removeListener(Listener listener);
}
