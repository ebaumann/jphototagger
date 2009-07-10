package de.elmar_baumann.imv.data;

import de.elmar_baumann.imv.comparator.ComparatorHierarchicalSubjectId;
import de.elmar_baumann.imv.comparator.ComparatorHierarchicalSubjectIdParent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * A collection of {@link HierarchicalSubject}s with fast access to a subject
 * with a specific ID or parent ID.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/07/10
 */
public final class HierarchicalSubjects {

    private final LinkedList<HierarchicalSubject> subjectsById;
    private final LinkedList<HierarchicalSubject> subjectsByIdParent;

    public HierarchicalSubjects(Collection<HierarchicalSubject> subjects) {
        subjectsById = deepCopy(subjects);
        subjectsByIdParent = new LinkedList<HierarchicalSubject>(subjectsById);
        sort();
    }

    public synchronized void update(HierarchicalSubject subject) {
        int index = subjectsById.indexOf(subject);
        assert index >= 0 : "Subject (" + subject + ") does not exist!";
        if (index >= 0) {
            HierarchicalSubject oldSubject = subjectsById.get(index);
            oldSubject.setIdParent(subject.getIdParent());
            oldSubject.setSubject(subject.getSubject());
        }
    }

    public synchronized void delete(Collection<HierarchicalSubject> subjects) {
        for (HierarchicalSubject subject : subjects) {
            subjectsById.remove(subject);
            subjectsByIdParent.remove(subject);
        }
    }

    public synchronized void insert(HierarchicalSubject subject) {
        boolean contains = subjectsById.contains(subject);
        assert !contains : "Subject (" + subject + ") already exists!";
        insert(subject, subjectsById, ComparatorHierarchicalSubjectId.INSTANCE);
        insert(subject, subjectsByIdParent,
                ComparatorHierarchicalSubjectIdParent.INSTANCE);
    }

    /**
     * Returns a subject with a specific database ID.
     *
     * @param  id database ID
     * @return    subject or null if no subject has that database ID
     */
    public synchronized HierarchicalSubject getSubject(long id) {
        HierarchicalSubject key = new HierarchicalSubject(id, null, null);
        int index = Collections.binarySearch(
                subjectsById, key, ComparatorHierarchicalSubjectId.INSTANCE);

        return index >= 0
               ? copy(subjectsById.get(index))
               : null;
    }

    public synchronized Collection<HierarchicalSubject> getChildren(
            long idParent) {
        List<HierarchicalSubject> children =
                new ArrayList<HierarchicalSubject>();
        HierarchicalSubject key = new HierarchicalSubject(null, idParent, null);
        int index = Collections.binarySearch(subjectsByIdParent, key,
                ComparatorHierarchicalSubjectIdParent.INSTANCE);
        if (index >= 0) {
            children.add(copy(subjectsByIdParent.get(index)));
            boolean found = true;
            for (int i = index - 1; found && i >= 0; i--) {
                found = addFoundParent(i, idParent, found, children);
            }
            found = true;
            int size = subjectsByIdParent.size();
            for (int i = index + 1; found && i < size; i++) {
                found = addFoundParent(i, idParent, found, children);
            }
        }
        return children;
    }

    private boolean addFoundParent(int index, long idParent, boolean found,
            List<HierarchicalSubject> children) {
        HierarchicalSubject subject = subjectsByIdParent.get(index);
        found = subject.getIdParent() == idParent;
        if (found) {
            children.add(copy(subject));
        }
        return found;
    }

    private void insert(HierarchicalSubject subject,
            LinkedList<HierarchicalSubject> subjects,
            Comparator<HierarchicalSubject> compare) {
        int index = getInsertIndex(subject, subjects, compare);
        if (index == 0) {
            subjects.addFirst(subject);
        } else if (index < subjects.size()) {
            subjects.add(index, subject);
        } else {
            subjects.addLast(subject);
        }
    }

    private int getInsertIndex(HierarchicalSubject subject,
            LinkedList<HierarchicalSubject> subjects,
            Comparator<HierarchicalSubject> compare) {
        boolean less = true;
        int index = 0;
        for (ListIterator<HierarchicalSubject> it = subjects.listIterator();
                less && it.hasNext(); index++) {
            HierarchicalSubject subjectInList = it.next();
            less = compare.compare(subject, subjectInList) == -1;
        }
        return index;
    }

    private LinkedList<HierarchicalSubject> deepCopy(
            Collection<HierarchicalSubject> subjects) {
        LinkedList<HierarchicalSubject> copy =
                new LinkedList<HierarchicalSubject>();
        for (HierarchicalSubject subject : subjects) {
            copy.add(copy(subject));
        }
        return copy;
    }

    private HierarchicalSubject copy(HierarchicalSubject subject) {
        return new HierarchicalSubject(
                subject.getId(), subject.getIdParent(), subject.getSubject());
    }

    private void sort() {
        Collections.sort(subjectsById,
                ComparatorHierarchicalSubjectId.INSTANCE);
        Collections.sort(subjectsByIdParent,
                ComparatorHierarchicalSubjectIdParent.INSTANCE);
    }
}
