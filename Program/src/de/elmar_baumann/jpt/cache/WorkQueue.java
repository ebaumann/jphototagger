/*
 * JPhotoTagger tags and finds images fast.
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.cache;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 *
 * @param <E>
 * @author Martin Pohlack
 * @version 2009-07-18
 */
public class WorkQueue<E> {
    // fixme: maybe use better data structure here with efficient contains()

    Deque<E> queue = new ArrayDeque<E>();

    /**
     * Add a new import work item to head of list
     *
     * If the item was already in the queue, move to head.
     *
     * @param e
     */
    public synchronized void push(E e) {
        queue.remove(e);  // maybe remove ...
        queue.push(e);    // and insert at head again
        notify();
    }

    /**
     * Add a new low-priority item at end of list (useful for prefetching).
     *
     * If the item was already in the queue, do nothing.
     *
     * @param e
     */
    public synchronized void append(E e) {
        if (! queue.contains(e)) {
            queue.add(e);    // append at end
            notify();
        }
    }

    /**
     * Retrieve the next item to work on from head of queue.
     *
     * @return File to open next.
     * @throws InterruptedException
     */
    public synchronized E fetch() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        return queue.removeFirst();
    }

    /**
     * Retrieve the next item to work on from head of queue or null of
     * none available.  Does not block.
     *
     * @return File to open next.
     */
    public synchronized E poll() {
        return queue.pollFirst();
    }


    /**
     * Remove an image from a queue.
     *
     * @param e
     */
    public synchronized void remove(E e) {
        queue.remove(e);
    }
}
