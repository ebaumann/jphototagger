package de.elmar_baumann.lib.thirdparty.neil_cochrane;

import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class CompleterTextField extends JTextField {

    /**
     * default constructor shows the completer window when offering matches.
     * @param completeMatches
     */
    public CompleterTextField(Object[] completeMatches) {
        super();

        _initWindow(completeMatches);
    }

    /**
     * useWindow - true will popup the completer window to help with matches,
     * false will just complete in the textfield with no window.
     * @param completeMatches
     * @param useWindow 
     */
    public CompleterTextField(Object[] completeMatches, boolean useWindow) {
        super();
        if (useWindow) {
            _initWindow(completeMatches);
        } else {
            _initWindowless(completeMatches);
        }
    }

    private void _initWindow(Object[] completeMatches) {
        PlainDocument pd = new PlainDocument();
        _filter = new CompleterFilterWithWindow(completeMatches, this);
        pd.setDocumentFilter(_filter);
        setDocument(pd);
    }

    private void _initWindowless(Object[] completeMatches) {
        PlainDocument pd = new PlainDocument();
        _filter = new CompleterFilter(completeMatches, this);
        pd.setDocumentFilter(_filter);
        setDocument(pd);
    }

    @Override
    /**
     * Warning: Calling setDocument on a completerTextField will remove the completion
     * mecanhism for this text field if the document is not derived from AbstractDocument.
     * 
     *  Only AbstractDocuments support the required DocumentFilter API for completion. 
     */
    public void setDocument(Document doc) {
        super.setDocument(doc);

        if (doc instanceof AbstractDocument) {
            ((AbstractDocument) doc).setDocumentFilter(_filter);
        }
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

    /**
     * Will change the user entered part of the string to match the case of the matched item.
     * 
     * e.g.
     * "europe/lONdon" would be corrected to "Europe/London"
     * 
     * This option only makes sense if case sensitive is turned off
     * @param correctCase 
     */
    public void setCorrectCase(boolean correctCase) {
        _filter.setCorrectCase(correctCase);
    }

    /**
     * Set the list of objects to match against.
     * @param completeMatches
     */
    public void setCompleterMatches(Object[] completeMatches) {
        _filter.setCompleterMatches(completeMatches);
    }
    private CompleterFilter _filter;
}
