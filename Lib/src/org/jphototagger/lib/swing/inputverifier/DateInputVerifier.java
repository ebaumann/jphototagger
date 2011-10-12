package org.jphototagger.lib.swing.inputverifier;

import java.awt.HeadlessException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;

import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;

/**
 * Verifies whether the input is a date and, if not, displays an error
 * message.
 *
 * @author Elmar Baumann
 */
public final class DateInputVerifier extends InputVerifier implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String pattern;

    /**
     * Verifies via {@code DateFormat#getInstance()}.
     */
    public DateInputVerifier() {
        pattern = null;
    }

    /**
     * Uses a {@code SimpleDateFormat} object.
     *
     * @param pattern pattern as described in
     *              {@code SimpleDateFormat#SimpleDateFormat(java.lang.String)}
     */
    public DateInputVerifier(String pattern) {
        this.pattern = pattern;
    }

    /**
     * Verifies the input.
     *
     * @param  input an instance of <code>JTextComponent</code>
     * @return       true if input is ok
     */
    @Override
    public boolean verify(JComponent input) {
        if (input instanceof JTextComponent) {
            if (!isValid((JTextComponent) input)) {
                errorMessage();

                return false;
            }
        }

        return true;
    }

    private void errorMessage() throws HeadlessException {
        JOptionPane.showMessageDialog(ComponentUtil.findFrameWithIcon(),
                Bundle.getString(DateInputVerifier.class, "DateInputVerifier.Error.NaN"),
                Bundle.getString(DateInputVerifier.class, "DateInputVerifier.Error.NaN.Title"),
                JOptionPane.ERROR_MESSAGE);
    }

    private boolean isValid(JTextComponent textComponent) {
        String text = textComponent.getText().trim();
        DateFormat df = (pattern == null)
                ? DateFormat.getInstance()
                : new SimpleDateFormat(pattern);

        try {
            df.parse(text);

            return true;
        } catch (ParseException ex) {
            Logger.getLogger(DateInputVerifier.class.getName()).log(Level.FINEST, ex.toString());
        }

        return false;
    }
}
