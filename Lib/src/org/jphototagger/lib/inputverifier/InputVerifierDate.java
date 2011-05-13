package org.jphototagger.lib.inputverifier;

import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.lib.resource.JslBundle;
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

/**
 * Verifies whether the input is a date and, if not, displays an error
 * message.
 *
 * @author Elmar Baumann
 */
public final class InputVerifierDate extends InputVerifier implements Serializable {
    private static final long serialVersionUID = 1163686474402473633L;
    private final String pattern;

    /**
     * Verifies via {@link DateFormat#getInstance()}.
     */
    public InputVerifierDate() {
        pattern = null;
    }

    /**
     * Uses a {@link SimpleDateFormat} object.
     *
     * @param pattern pattern as described in
     *              {@link SimpleDateFormat#SimpleDateFormat(java.lang.String)}
     */
    public InputVerifierDate(String pattern) {
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
        JOptionPane.showMessageDialog(ComponentUtil.getFrameWithIcon(),
                                      JslBundle.INSTANCE.getString("InputVerifierDate.Error.NaN"),
                                      JslBundle.INSTANCE.getString("InputVerifierDate.Error.NaN.Title"),
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
            Logger.getLogger(InputVerifierDate.class.getName()).log(Level.FINEST, ex.toString());
        }

        return false;
    }
}
