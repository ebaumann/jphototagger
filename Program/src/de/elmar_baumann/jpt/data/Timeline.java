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

import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.lib.model.TreeModelUpdateInfo;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

    public static class Date {
        public int year;
        public int month;
        public int day;

        public Date(int year, int month, int date) {
            this.year  = year;
            this.month = month;
            this.day   = date;
        }

        public Date(Calendar cal) {
            set(cal);
        }

        public Date(java.sql.Date date) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            set(cal);
        }

        public void reset() {
            year  = -1;
            month = -1;
            day   = -1;
        }

        public void set(Calendar cal) {
            year  = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH) + 1;
            day   = cal.get(Calendar.DAY_OF_MONTH);
        }

        public boolean setXmpDateCreated(String date) {
            int length = date.length();
            try {
                if (length >= 4) {
                    year = Integer.parseInt(date.substring(0, 4));
                }
                if (length >= 7) {
                    month = Integer.parseInt(date.substring(5, 7));
                }
                if (length == 10) {
                    day = Integer.parseInt(date.substring(8));
                }
                return true;
            } catch (Exception ex) {
                AppLog.logSevere(Date.class, ex);
                reset();
            }
            return false;
        }

        public boolean isValid() {
            return year > 0;
        }

        public boolean hasDay() {
            return day > 0;
        }

        public boolean hasMonth() {
            return month > 0;
        }

        public boolean hasYear() {
            return year > 0;
        }

        public boolean isComplete() {
            return day > 0 && month > 0 && year > 0;
        }

        public String getMonthDisplayName() {
            try {
                if (!hasMonth()) return Bundle.getString("Timeline.DisplayName.NoMonth");

                DateFormat     df   = new SimpleDateFormat("M");
                java.util.Date date = df.parse(Integer.toString(month));
                DateFormat     dfLong = new SimpleDateFormat("MMMM");

                return dfLong.format(date);
            } catch (Exception ex) {
                AppLog.logSevere(Date.class, ex);
            }
            return Bundle.getString("Timeline.DisplayName.NoMonth");
        }

        @Override
        public String toString() {
            return isValid()
                    ? Integer.toString(year)
                    : hasMonth()
                    ? "-" + Integer.toString(month)
                    : hasDay()
                    ? "-" + Integer.toString(day)
                    : "Invalid date"
                    ;
        }
    }

    private final        DefaultMutableTreeNode ROOT_NODE    = new DefaultMutableTreeNode(Bundle.getString("Timeline.RootNode.DisplayName"));
    private static final DefaultMutableTreeNode UNKNOWN_NODE = new DefaultMutableTreeNode(Bundle.getString("Timeline.UnknownNode.DisplayName"));
    private              boolean                unknownNode;

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
     * Returns the node for images without EXIF day time taken.
     *
     * @return node
     */
    public static DefaultMutableTreeNode getUnknownNode() {
        return UNKNOWN_NODE;
    }

    /**
     * Adds a day.
     *
     * @param cal day taken of the image - only year, month and day are
     *            recognized
     * @return    information about the inserted children
     */
    public synchronized TreeModelUpdateInfo.NodesAndChildIndices add(Calendar cal) {
        TreeModelUpdateInfo.NodesAndChildIndices info = new TreeModelUpdateInfo.NodesAndChildIndices();
        Date date = new Date(cal);
        insertDayNode(insertMonthNode(insertYearNode(date, info), date, info), date, info);
        return info;
    }

    /**
     * Adds a date.
     *
     * @param date date taken of the image, must be valid ({@link Date#isValid()}
     * @return     information about the inserted children
     */
    public synchronized TreeModelUpdateInfo.NodesAndChildIndices add(Date date) {
        assert date.isValid() : date;

        TreeModelUpdateInfo.NodesAndChildIndices info = new TreeModelUpdateInfo.NodesAndChildIndices();

        DefaultMutableTreeNode yearNode  = insertYearNode(date, info);

        if (date.hasMonth()) {
            DefaultMutableTreeNode monthNode = insertMonthNode(yearNode, date, info);
            if (date.hasDay()) {
                insertDayNode(monthNode, date, info);
            }
        }

        return info;
    }

    /**
     * Removes a day.
     *
     * @param  date day taken of th image - only year, month and day are
     *             compared
     * @return     update information
     */
    public synchronized TreeModelUpdateInfo.NodeAndChild removeDay(Date date) {
        TreeModelUpdateInfo.NodeAndChild info    = new TreeModelUpdateInfo.NodeAndChild();
        DefaultMutableTreeNode           dayNode = getNodeOfDay(date);
        if (dayNode != null) {
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) dayNode.getParent();
            if (parent != null) {
                info.setNode(parent);
                info.setUpdatedChild(dayNode, parent.getIndex(dayNode));
                parent.remove(dayNode);
                removeIfEmpty(parent, info);
            }
        }
        return info;
    }

    private void removeIfEmpty(DefaultMutableTreeNode node, TreeModelUpdateInfo.NodeAndChild info) {
        if (node.getChildCount() <= 0) {
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
            if (parent != null) {
                info.setNode(parent);
                info.setUpdatedChild(node, parent.getIndex(node));
                parent.remove(node);
                removeIfEmpty(parent, info); // recursive
            }
        }
    }

    /**
     * Adds the node for image files without day created informations.
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
     * @param   date day - only year, month and day are compared
     * @return  true if that day exists
     */
    public synchronized boolean existsDay(Date date) {
        return getNodeOfDay(date) != null;
    }

    /**
     * Returns a node of a specific month.
     *
     * @param   date day - only the year, month and day are compared
     * @return  node or null if no such node exists
     */
    private DefaultMutableTreeNode getNodeOfDay(Date date) {
        DefaultMutableTreeNode monthNodeOfCal = getNodeOfMonth(date);

        if (monthNodeOfCal == null) return null;

        Enumeration            days         = monthNodeOfCal.children();
        DefaultMutableTreeNode dayNodeOfCal = null;

        while (dayNodeOfCal == null && days.hasMoreElements()) {
            DefaultMutableTreeNode childNode  = (DefaultMutableTreeNode) days.nextElement();
            Object                 userObject = childNode.getUserObject();

            if (userObject instanceof Date) {
                Date dateOfNode = (Date) userObject;
                if (dateOfNode.day == date.day) {
                    dayNodeOfCal = childNode;
                }
            }
        }
        return dayNodeOfCal;
    }

    /**
     * Returns a node of a specific month.
     *
     * @param   date day - only the year and month are compared
     * @return  node or null if no such node exists
     */
    private DefaultMutableTreeNode getNodeOfMonth(Date date) {
        DefaultMutableTreeNode yearNodeOfCal = getNodeOfYear(date);

        if (yearNodeOfCal == null) return null;

        Enumeration            months         = yearNodeOfCal.children();
        DefaultMutableTreeNode monthNodeOfCal = null;

        while (monthNodeOfCal == null && months.hasMoreElements()) {
            DefaultMutableTreeNode childNode  = (DefaultMutableTreeNode) months.nextElement();
            Object                 userObject = childNode.getUserObject();

            if (userObject instanceof Date) {
                Date dateOfNode = (Date) userObject;
                if (dateOfNode.month == date.month) {
                    monthNodeOfCal = childNode;
                }
            }
        }
        return monthNodeOfCal;
    }

    /**
     * Returns a node of a specific year.
     *
     * @param   date day - only the year is checked
     * @return  node or null if no such node exists
     */
    private DefaultMutableTreeNode getNodeOfYear(Date date) {
        Enumeration            years         = ROOT_NODE.children();
        DefaultMutableTreeNode yearNodeOfCal = null;

        while (yearNodeOfCal == null && years.hasMoreElements()) {
            DefaultMutableTreeNode childNode  = (DefaultMutableTreeNode) years.nextElement();
            Object                 userObject = childNode.getUserObject();

            if (userObject instanceof Date) {
                Date dateOfNode = (Date) userObject;
                if (dateOfNode.year == date.year) {
                    yearNodeOfCal = childNode;
                }
            }
        }
        return yearNodeOfCal;
    }

    private DefaultMutableTreeNode insertYearNode(Date date, TreeModelUpdateInfo.NodesAndChildIndices info) {
        int                    indexYearNode = indexOfYearNode(date);
        DefaultMutableTreeNode yearNode;

        if (indexYearNode >= 0) {
            yearNode = (DefaultMutableTreeNode) ROOT_NODE.getChildAt(indexYearNode);
        } else {
            yearNode = new DefaultMutableTreeNode(date);
            insertYearNode(yearNode, info);
        }
        return yearNode;
    }

    private void insertYearNode(DefaultMutableTreeNode yearNode, TreeModelUpdateInfo.NodesAndChildIndices info) {
        int     childCount = ROOT_NODE.getChildCount();
        boolean inserted   = false;
        int     index      = 0;

        while (!inserted && index < childCount) {
            Object userObject = ((DefaultMutableTreeNode) ROOT_NODE.getChildAt(index++)).getUserObject();
            inserted = userObject.equals(yearNode.getUserObject());
        }
        if (!inserted) {
            ROOT_NODE.add(yearNode);
            info.addNode(ROOT_NODE, ROOT_NODE.getIndex(yearNode));
        }
    }

    private DefaultMutableTreeNode insertMonthNode(
            DefaultMutableTreeNode                   yearNode,
            Date                                     date,
            TreeModelUpdateInfo.NodesAndChildIndices info
            ) {
        DefaultMutableTreeNode monthNode  = null;
        int                    childCount = yearNode.getChildCount();
        boolean                inserted   = false;
        int                    index      = 0;

        while (!inserted && index < childCount) {
            monthNode = (DefaultMutableTreeNode) yearNode.getChildAt(index++);

            Object  userObject       = monthNode.getUserObject();
            boolean userObjectIsDate = userObject instanceof Date;

            if (!userObjectIsDate) return null;

            int monthValue = ((Date) userObject).month;

            inserted = monthValue == date.month;
        }
        if (!inserted) {
            monthNode = new DefaultMutableTreeNode(date);
            yearNode.add(monthNode);
            info.addNode(yearNode, yearNode.getIndex(monthNode));
        }
        return monthNode;
    }

    private void insertDayNode(
            DefaultMutableTreeNode                   monthNode,
            Date                                     date,
            TreeModelUpdateInfo.NodesAndChildIndices info
            ) {
        if (monthNode == null) return;

        DefaultMutableTreeNode dayNode    = null;
        int                    childCount = monthNode.getChildCount();
        boolean                inserted   = false;
        int                    index      = 0;

        while (!inserted && index < childCount) {
            dayNode = (DefaultMutableTreeNode) monthNode.getChildAt(index++);
            Object  userObject       = dayNode.getUserObject();
            boolean userObjectIsDate = userObject instanceof Date;

            if (!userObjectIsDate) return;
            int dayValue = ((Date) userObject).day;
            inserted = dayValue == date.day;
        }
        if (!inserted) {
            dayNode = new DefaultMutableTreeNode(date);
            monthNode.add(dayNode);
            info.addNode(monthNode, monthNode.getIndex(dayNode));
        }
    }

    private int indexOfYearNode(Date date) {
        int     index      = 0;
        int     childCount = ROOT_NODE.getChildCount();
        boolean yearExists = false;

        while (!yearExists && index < childCount) {
            Object userObject = ((DefaultMutableTreeNode) ROOT_NODE.getChildAt(index++)).getUserObject();
            if (userObject instanceof Date) {
                yearExists = ((Date) userObject).year == date.year;
            }
        }
        return yearExists
               ? index - 1
               : -1;
    }
}
