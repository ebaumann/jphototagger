package org.jphototagger.lib.swing;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.DefaultListModel;

/**
 * @author Elmar Baumann
 * @param <E>
 */
public class ListModelExt<E> extends DefaultListModel<E> {

    private static final long serialVersionUID = 1L;

    public ListModelExt() {
    }

    public ListModelExt(E[] elements) {
        Objects.requireNonNull(elements, "elements == null");

        setElements(elements);
    }

    public ListModelExt(Iterable<? extends E> elements) {
        Objects.requireNonNull(elements, "elements == null");

        setElements(elements);
    }

    public void setElements(E[] elements) {
        Objects.requireNonNull(elements, "elements == null");

        clear();

        for (E e : elements) {
            addElement(e);
        }
    }

    public void setElements(Iterable<? extends E> elements) {
        Objects.requireNonNull(elements, "elements == null");
        clear();
        for (E e : elements) {
            addElement(e);
        }
    }

    public void addToElements(Iterable<? extends E> elements) {
        Objects.requireNonNull(elements, "elements == null");

        for (E e : elements) {
            addElement(e);
        }
    }

    public List<E> getElements() {
        int size = getSize();
        List<E> elements = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            elements.add(getElementAt(i));
        }
        return elements;
    }

    public void fireChanged(int index0, int index1) {
        fireContentsChanged(this, index0, index1);
    }

    public void swap(int index1, int index2) {
        ensureIsIndex(index1);
        ensureIsIndex(index2);

        E element1 = getElementAt(index1);
        E element2 = getElementAt(index2);

        set(index1, element2);
        set(index2, element1);
    }

    public boolean isIndex(int position) {
        return position >= 0 && position < getSize();
    }

    private void ensureIsIndex(int index) {
        if (!isIndex(index)) {
            throw new IllegalArgumentException("Invalid Index: " + index + " with " + getSize() + " elements");
        }
    }

    public void removeFromElements(Iterable<? extends E> elements) {
        Objects.requireNonNull(elements, "elements == null");
        for (E element : elements) {
            removeElement(element);
        }
    }
}
