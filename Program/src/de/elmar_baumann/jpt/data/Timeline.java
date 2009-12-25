/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.data;

import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.lib.model.TreeModelUpdateInfo;
import java.util.Calendar;
import java.util.Enumeration;
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
 * @version 2009-06-11
 */
public final class Timeline {

    private final DefaultMutableTreeNode ROOT_NODE = new DefaultMutableTreeNode(
            Bundle.getString("Timeline.RootNode.DisplayName"));
    private static final DefaultMutableTreeNode UNKNOWN_NODE =
            new DefaultMutableTreeNode(
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
     * @param cal date taken of the image - only year, month and day are
     *            recognized
     * @return    information about the inserted children
     */
    public synchronized TreeModelUpdateInfo.NodesAndChildIndices add(
            Calendar cal) {
        TreeModelUpdateInfo.NodesAndChildIndices info =
                new TreeModelUpdateInfo.NodesAndChildIndices();
        insertDayNode(insertMonthNode(insertYearNode(cal, info), cal, info), cal,
                info);
        return info;
    }

    /**
     * Removes a day.
     *
     * @param  cal date taken of th image - only year, month and day are
     *             compared
     * @return     update information
     */
    public synchronized TreeModelUpdateInfo.NodeAndChild removeDay(Calendar cal) {
        TreeModelUpdateInfo.NodeAndChild info =
                new TreeModelUpdateInfo.NodeAndChild();
        DefaultMutableTreeNode dayNode = getNodeOfDay(cal);
        if (dayNode != null) {
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) dayNode.
                    getParent();
            if (parent != null) {
                info.setNode(parent);
                info.setUpdatedChild(dayNode, parent.getIndex(dayNode));
                parent.remove(dayNode);
                removeIfEmpty(parent, info);
            }
        }
        return info;
    }

    private void removeIfEmpty(DefaultMutableTreeNode node,
            TreeModelUpdateInfo.NodeAndChild info) {
        if (node.getChildCount() <= 0) {
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.
                    getParent();
            if (parent != null) {
                info.setNode(parent);
                info.setUpdatedChild(node, parent.getIndex(node));
                parent.remove(node);
                removeIfEmpty(parent, info); // recursive
            }
        }
    }

    /**
     * Adds the node for image files without date created informations.
     */
    public synchronized void addUnknownNode() {
        if (!unknownNode) {
            unknownNode = true;
            ROOT_NODE.add(UNKNOWN_NODE);
        }
    }

    /**
     * Returns whether a specific day exists into this timeline.
     *
     * @param   cal date - only year, month and day are compared
     * @return  true if that day exists
     */
    public synchronized boolean existsDay(Calendar cal) {
        return getNodeOfDay(cal) != null;
    }

    /**
     * Returns a node of a specific month.
     *
     * @param   cal date - only the year, month and day are compared
     * @return  node or null if no such node exists
     */
    private DefaultMutableTreeNode getNodeOfDay(Calendar cal) {
        DefaultMutableTreeNode monthNodeOfCal = getNodeOfMonth(cal);
        if (monthNodeOfCal == null) return null;
        Enumeration days = monthNodeOfCal.children();
        DefaultMutableTreeNode dayNodeOfCal = null;
        while (dayNodeOfCal == null && days.hasMoreElements()) {
            DefaultMutableTreeNode childNode =
                    (DefaultMutableTreeNode) days.nextElement();
            Object userObject = childNode.getUserObject();
            if (userObject instanceof Calendar) {
                Calendar nodeCalendar = (Calendar) userObject;
                if (nodeCalendar.get(Calendar.DAY_OF_MONTH) == cal.get(
                        Calendar.DAY_OF_MONTH)) {
                    dayNodeOfCal = childNode;
                }
            }
        }
        return dayNodeOfCal;
    }

    /**
     * Returns a node of a specific month.
     *
     * @param   cal date - only the year and month are compared
     * @return  node or null if no such node exists
     */
    private DefaultMutableTreeNode getNodeOfMonth(Calendar cal) {
        DefaultMutableTreeNode yearNodeOfCal = getNodeOfYear(cal);
        if (yearNodeOfCal == null) return null;
        Enumeration months = yearNodeOfCal.children();
        DefaultMutableTreeNode monthNodeOfCal = null;
        while (monthNodeOfCal == null && months.hasMoreElements()) {
            DefaultMutableTreeNode childNode =
                    (DefaultMutableTreeNode) months.nextElement();
            Object userObject = childNode.getUserObject();
            if (userObject instanceof Calendar) {
                Calendar nodeCalendar = (Calendar) userObject;
                if (nodeCalendar.get(Calendar.MONTH) == cal.get(Calendar.MONTH)) {
                    monthNodeOfCal = childNode;
                }
            }
        }
        return monthNodeOfCal;
    }

    /**
     * Returns a node of a specific year.
     *
     * @param   cal date - only the year is checked
     * @return  node or null if no such node exists
     */
    private DefaultMutableTreeNode getNodeOfYear(Calendar cal) {
        Enumeration years = ROOT_NODE.children();
        DefaultMutableTreeNode yearNodeOfCal = null;
        while (yearNodeOfCal == null && years.hasMoreElements()) {
            DefaultMutableTreeNode childNode =
                    (DefaultMutableTreeNode) years.nextElement();
            Object userObject = childNode.getUserObject();
            if (userObject instanceof Calendar) {
                Calendar nodeCalendar = (Calendar) userObject;
                if (nodeCalendar.get(Calendar.YEAR) == cal.get(Calendar.YEAR)) {
                    yearNodeOfCal = childNode;
                }
            }
        }
        return yearNodeOfCal;
    }

    private DefaultMutableTreeNode insertYearNode(Calendar cal,
            TreeModelUpdateInfo.NodesAndChildIndices info) {
        int indexYearNode = indexOfYearNode(cal);
        DefaultMutableTreeNode yearNode;
        if (indexYearNode >= 0) {
            yearNode = (DefaultMutableTreeNode) ROOT_NODE.getChildAt(
                    indexYearNode);
        } else {
            yearNode = new DefaultMutableTreeNode(cal);
            insertYearNode(yearNode, info);
        }
        return yearNode;
    }

    private void insertYearNode(DefaultMutableTreeNode yearNode,
            TreeModelUpdateInfo.NodesAndChildIndices info) {
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
            info.addNode(ROOT_NODE, ROOT_NODE.getIndex(yearNode));
        }
    }

    private DefaultMutableTreeNode insertMonthNode(
            DefaultMutableTreeNode yearNode,
            Calendar cal, TreeModelUpdateInfo.NodesAndChildIndices info) {
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
            info.addNode(yearNode, yearNode.getIndex(monthNode));
        }
        return monthNode;
    }

    private void insertDayNode(DefaultMutableTreeNode monthNode, Calendar cal,
            TreeModelUpdateInfo.NodesAndChildIndices info) {
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
            info.addNode(monthNode, monthNode.getIndex(dayNode));
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
