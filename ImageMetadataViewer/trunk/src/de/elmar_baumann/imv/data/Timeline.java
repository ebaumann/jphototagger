package de.elmar_baumann.imv.data;

import de.elmar_baumann.imv.resource.Bundle;
import java.util.Calendar;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

/**
 * Times when images are created for useage in a {@link TreeModel}.
 *
 * All elements of a timeline are of the type
 * {@link DefaultMutableTreeNode}. The nodes has user objects of the type
 * {@link java.util.Calendar}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/11
 */
public final class Timeline {

    private final DefaultMutableTreeNode ROOT_NODE = new DefaultMutableTreeNode(
            Bundle.getString("Timeline.RootNode.DisplayName"));
    private static final DefaultMutableTreeNode UNKNOWN_NODE = new DefaultMutableTreeNode(
            Bundle.getString("Timeline.UnknownNode.DisplayName"));
    private boolean unknownNode;

    /**
     * Returns the root node. It's children are the years, the year's children
     * are the months and the month's children are the days.
     *
     * @return root node
     */
    public DefaultMutableTreeNode getRoot() {
        return ROOT_NODE;
    }

    /**
     * Returns the node for images without EXIF date time taken.
     *
     * @return node
     */
    public static DefaultMutableTreeNode getUnknownNode() {
        return UNKNOWN_NODE;
    }

    /**
     * Adds a date.
     *
     * @param cal date taken of the image
     */
    public void add(Calendar cal) {
        insertDayNode(insertMonth(insertYearNode(cal), cal), cal);
    }

    public synchronized void addUnknownNode() {
        if (!unknownNode) {
            unknownNode = true;
            ROOT_NODE.add(UNKNOWN_NODE);
        }
    }

    private DefaultMutableTreeNode insertYearNode(Calendar cal) {
        int indexYearNode = indexOfYearNode(cal);
        DefaultMutableTreeNode yearNode;
        if (indexYearNode >= 0) {
            yearNode = (DefaultMutableTreeNode) ROOT_NODE.getChildAt(
                    indexYearNode);
        } else {
            yearNode = new DefaultMutableTreeNode(cal);
            insertYearNode(yearNode);
        }
        return yearNode;
    }

    private void insertYearNode(DefaultMutableTreeNode yearNode) {
        int childCount = ROOT_NODE.getChildCount();
        boolean inserted = false;
        int index = 0;
        while (!inserted && index < childCount) {
            Object userObject = ((DefaultMutableTreeNode) ROOT_NODE.getChildAt(
                    index++)).getUserObject();
            inserted = userObject.equals(yearNode.getUserObject());
        }
        if (!inserted) {
            ROOT_NODE.add(yearNode);
        }
    }

    private DefaultMutableTreeNode insertMonth(DefaultMutableTreeNode yearNode,
            Calendar cal) {
        DefaultMutableTreeNode monthNode = null;
        int month = cal.get(Calendar.MONTH) + 1;
        int childCount = yearNode.getChildCount();
        boolean inserted = false;
        int index = 0;
        while (!inserted && index < childCount) {
            monthNode =
                    (DefaultMutableTreeNode) yearNode.getChildAt(index++);
            Object userObject = monthNode.getUserObject();
            boolean userObjectIsCalendar = userObject instanceof Calendar;
            if (!userObjectIsCalendar) return null;
            int monthValue = ((Calendar) userObject).get(Calendar.MONTH) + 1;
            inserted = monthValue == month;
        }
        if (!inserted) {
            monthNode = new DefaultMutableTreeNode(cal);
            yearNode.add(monthNode);
        }
        return monthNode;
    }

    private void insertDayNode(DefaultMutableTreeNode monthNode, Calendar cal) {
        if (monthNode == null) return;
        DefaultMutableTreeNode dayNode = null;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int childCount = monthNode.getChildCount();
        boolean inserted = false;
        int index = 0;
        while (!inserted && index < childCount) {
            dayNode =
                    (DefaultMutableTreeNode) monthNode.getChildAt(index++);
            Object userObject = dayNode.getUserObject();
            boolean userObjectIsCalendar = userObject instanceof Calendar;
            if (!userObjectIsCalendar) return;
            int dayValue = ((Calendar) userObject).get(Calendar.DAY_OF_MONTH);
            inserted = dayValue == day;
        }
        if (!inserted) {
            dayNode = new DefaultMutableTreeNode(cal);
            monthNode.add(dayNode);
        }
    }

    private int indexOfYearNode(Calendar cal) {
        int year = cal.get(Calendar.YEAR);
        int index = 0;
        int childCount = ROOT_NODE.getChildCount();
        boolean yearExists = false;
        while (!yearExists && index < childCount) {
            Object userObject = ((DefaultMutableTreeNode) ROOT_NODE.getChildAt(
                    index++)).getUserObject();
            if (userObject instanceof Calendar) {
                yearExists = ((Calendar) userObject).get(Calendar.YEAR) == year;
            }
        }
        return yearExists
               ? index - 1
               : -1;
    }
}
