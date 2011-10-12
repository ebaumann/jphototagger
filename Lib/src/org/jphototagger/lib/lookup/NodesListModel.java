package org.jphototagger.lib.lookup;

import java.io.Serializable;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import org.jphototagger.api.nodes.Node;

/**
 * @author Elmar Baumann
 */
public class NodesListModel implements ListModel, Serializable {

    private static final long serialVersionUID = 1L;
    private final DefaultListModel delegate = new DefaultListModel();

    @Override
    public void removeListDataListener(ListDataListener l) {
        delegate.removeListDataListener(l);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        delegate.addListDataListener(l);
    }

    @Override
    public int getSize() {
        return delegate.getSize();
    }

    @Override
    public Object getElementAt(int index) {
        return delegate.getElementAt(index);
    }

    protected void setElementAt(Node node, int index) {
        delegate.setElementAt(node, index);
    }

    protected Object set(int index, Node node) {
        return delegate.set(index, node);
    }

    protected void removeRange(int fromIndex, int toIndex) {
        delegate.removeRange(fromIndex, toIndex);
    }

    protected void removeElementAt(int index) {
        delegate.removeElementAt(index);
    }

    protected boolean removeElement(Node node) {
        return delegate.removeElement(node);
    }

    protected void removeAllElements() {
        delegate.removeAllElements();
    }

    protected Node remove(int index) {
        return (Node) delegate.remove(index);
    }

    protected int lastIndexOf(Node node, int index) {
        return delegate.lastIndexOf(node, index);
    }

    protected int lastIndexOf(Node node) {
        return delegate.lastIndexOf(node);
    }

    protected Node lastElement() {
        return (Node) delegate.lastElement();
    }

    protected boolean isEmpty() {
        return delegate.isEmpty();
    }

    protected void insertElementAt(Node node, int index) {
        delegate.insertElementAt(node, index);
    }

    protected int indexOf(Node node, int index) {
        return delegate.indexOf(node, index);
    }

    protected int indexOf(Node node) {
        return delegate.indexOf(node);
    }

    protected Node get(int index) {
        return (Node) delegate.get(index);
    }

    protected Node firstElement() {
        return (Node) delegate.firstElement();
    }

    protected void ensureCapacity(int minCapacity) {
        delegate.ensureCapacity(minCapacity);
    }

    protected Node elementAt(int index) {
        return (Node) delegate.elementAt(index);
    }

    protected boolean contains(Node node) {
        return delegate.contains(node);
    }

    protected void clear() {
        delegate.clear();
    }

    protected int capacity() {
        return delegate.capacity();
    }

    protected void addElement(Node node) {
        delegate.addElement(node);
    }

    protected void add(int index, Node node) {
        delegate.add(index, node);
    }
}
