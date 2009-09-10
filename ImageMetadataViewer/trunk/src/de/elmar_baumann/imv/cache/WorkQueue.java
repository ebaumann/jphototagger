package de.elmar_baumann.imv.cache;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 *
 * @param <E>
 * @author Martin Pohlack <martinp@gmx.de>
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
