package de.elmar_baumann.imv.cache;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 *
 * @author Martin Pohlack <martinp@gmx.de>
 * @version 2009-07-18
 */
public class WorkQueue {
    // fixme: maybe use better data structure here with efficient contains()

    Deque<File> queue = new ArrayDeque<File>();

    /**
     * Add a new import work item to head of list
     * 
     * If the item was already in the queue, move to head.
     * 
     * @param file
     */
    public synchronized void push(File file) {
        queue.remove(file);  // maybe remove ...
        queue.push(file);    // and insert at head again
        notify();
    }

    /**
     * Add a new low-priority item at end of list (useful for prefetching).
     *
     * If the item was already in the queue, do nothing.
     *
     * @param file
     */
    public synchronized void append(File file) {
        if (! queue.contains(file)) {
            queue.add(file);    // append at end
            notify();
        }
    }

    /**
     * Retrieve the next item to work on from head of queue.
     *
     * @return File to open next.
     * @throws InterruptedException
     */
    public synchronized File fetch() throws InterruptedException {
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
     * @throws InterruptedException
     */
    public synchronized File poll() {
        return queue.pollFirst();
    }


    /**
     * Remove an image from a queue.
     *
     * @param file
     */
    public synchronized void remove(File file) {
        queue.remove(file);
    }
}
