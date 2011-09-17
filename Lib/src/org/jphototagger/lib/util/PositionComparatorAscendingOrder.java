package org.jphototagger.lib.util;

import java.util.Comparator;
import org.jphototagger.api.collections.PositionProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class PositionComparatorAscendingOrder implements Comparator<PositionProvider> {

    public static final PositionComparatorAscendingOrder INSTANCE = new PositionComparatorAscendingOrder();

    @Override
    public int compare(PositionProvider postionProvider1, PositionProvider postionProvider2) {
        int position1 = postionProvider1.getPosition();
        int position2 = postionProvider2.getPosition();

        return position1 == position2
                ? 0
                : position1 > position2
                ? 1
                : -1;
    }

    private PositionComparatorAscendingOrder() {
    }
}
