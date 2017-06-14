package org.jphototagger.domain.thumbnails;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import org.openide.util.Lookup;

/**
 * Higher priority before lower.
 *
 * @author Elmar Baumann
 */
public final class ThumbnailCreatorPriorityComparator implements Comparator<ThumbnailCreator> {

    public static final ThumbnailCreatorPriorityComparator INSTANCE = new ThumbnailCreatorPriorityComparator();

    public static List<ThumbnailCreator> lookupSorted() {
        List<ThumbnailCreator> thumbnailCreators = new LinkedList<ThumbnailCreator>(
                Lookup.getDefault().lookupAll(ThumbnailCreator.class));
        Collections.sort(thumbnailCreators, INSTANCE);
        return thumbnailCreators;
    }

    @Override
    public int compare(ThumbnailCreator o1, ThumbnailCreator o2) {
        int priority1 = o1.getPriority();
        int priority2 = o2.getPriority();
        return priority1 == priority2
                ? 0
                : priority1 > priority2
                ? -1
                : 1;
    }

    private ThumbnailCreatorPriorityComparator() {
    }
}
