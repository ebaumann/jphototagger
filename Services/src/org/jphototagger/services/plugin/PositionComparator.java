/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jphototagger.services.plugin;

import java.util.Comparator;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class PositionComparator implements Comparator<PositionProvider> {

    public static final PositionComparator INSTANCE = new PositionComparator();

    @Override
    public int compare(PositionProvider o1, PositionProvider o2) {
        int position1 = o1.getPosition();
        int position2 = o2.getPosition();

        return position1 == position2
                ? 0
                : position1 > position2
                ? 1
                : -1;
    }

    private PositionComparator() {
    }
}
