package de.elmar_baumann.lib.thirdparty.neil_cochrane;

import java.util.ArrayList;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

/**
 * An editable combo class that will autocomplete the user entered text to the entries
 * in the combo drop down. 
 * 
 * You can directly add auto-complete to existing JComboBox derived classes
 * using:
 * ComboCompleterFilter.addCompletion(yourCombo);
 * 
 * @author ncochran
 */
public class CompleterComboBox extends JComboBox {

    public CompleterComboBox(ComboBoxModel aModel) {
        super(aModel);
    // TODO Auto-generated constructor stub
    }

    public CompleterComboBox(Object[] items) {
        super(items);
        _init();
    }

    public CompleterComboBox(ArrayList<?> items) {
        super(items.toArray());
    // TODO Auto-generated constructor stub
    }

    public CompleterComboBox() {
        super();
    // TODO Auto-generated constructor stub
    }

    private void _init() {
        setEditable(true);

        _filter = ComboCompleterFilter.addCompletionMechanism(this);
    }

    public boolean isCaseSensitive() {
        return _filter.isCaseSensitive();
    }

    public boolean isCorrectingCase() {
        return _filter.isCorrectingCase();
    }

    public void setCaseSensitive(boolean caseSensitive) {
        _filter.setCaseSensitive(caseSensitive);
    }

    public void setCorrectCase(boolean correctCase) {
        _filter.setCorrectCase(correctCase);
    }
    private ComboCompleterFilter _filter;
}
