package de.elmar_baumann.lib.thirdparty.neil_cochrane;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.AbstractListModel;

/**
 * Class to hold the remaining objects that still match the users input.
 * @author ncochran
 *
 */
public class FilterListModel extends AbstractListModel {

    public FilterListModel(Object[] unfilteredList) {
        _fullList = unfilteredList;
        _filteredList = new ArrayList<Object>(Arrays.asList(unfilteredList));
    }

    @Override
    public int getSize() {
        return _filteredList.size();
    }

    @Override
    public Object getElementAt(int index) {
        return _filteredList.get(index);
    }

    public String getFilter() {
        return _filter;
    }

    public void setFilter(String filter) {
        _filteredList.clear();
        for (Object obj : _fullList) {
            if (obj.toString().length() < filter.length()) {
                continue;
            }

            if (_caseSensitive) {
                if (obj.toString().startsWith(filter)) {
                    _filteredList.add(obj);
                }
            } else {
                if (obj.toString().substring(0, filter.length()).compareToIgnoreCase(filter) == 0) {
                    _filteredList.add(obj);
                }
            }
        }
        fireContentsChanged(this, 0, _filteredList.size());
    }

    public void clearFilter() {
        _filter = null;
        _filteredList = new ArrayList<Object>(Arrays.asList(_fullList));
    }

    public boolean getCaseSensitive() {
        return _caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        _caseSensitive = caseSensitive;
        clearFilter();
    }

    public void setCompleterMatches(Object[] objectsToMatch) {
        _fullList = objectsToMatch;
        clearFilter();
    }
    private Object[] _fullList;
    private ArrayList<Object> _filteredList;
    private String _filter;
    private boolean _caseSensitive = false;
}
