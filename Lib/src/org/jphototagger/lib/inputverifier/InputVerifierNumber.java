package org.jphototagger.lib.inputverifier;

import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.lib.resource.JslBundle;

import java.awt.HeadlessException;

import java.io.Serializable;

import java.text.NumberFormat;
import java.text.ParseException;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;

/**
 * Verifies whether the input is a number and, if not, displays an error
 * message.
 *
 * @author Elmar Baumann
 */
public final class InputVerifierNumber extends InputVerifier implements Serializable {
    public static final InputVerifierNumber INSTANCE = new InputVerifierNumber();
    private static final long serialVersionUID = -7544590339781154133L;

    private InputVerifierNumber() {}

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
                                      JslBundle.INSTANCE.getString("InputVerifierNumber.Error.NaN"),
                                      JslBundle.INSTANCE.getString("InputVerifierNumber.Error.NaN.Title"),
                                      JOptionPane.ERROR_MESSAGE);
    }

    private boolean isValid(JTextComponent textComponent) {
        String text = textComponent.getText().trim();
        NumberFormat nf = NumberFormat.getInstance();

        try {
            nf.parse(text);

            return true;
        } catch (ParseException ex) {
            Logger.getLogger(InputVerifierNumber.class.getName()).log(Level.FINEST, ex.toString());
        }

        return false;
    }
}
