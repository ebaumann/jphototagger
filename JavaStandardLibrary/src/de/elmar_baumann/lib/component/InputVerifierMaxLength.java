package de.elmar_baumann.lib.component;

import de.elmar_baumann.lib.image.icon.IconUtil;
import de.elmar_baumann.lib.resource.Settings;
import java.text.MessageFormat;
import javax.swing.Icon;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Verifies Input in a <code>JTextField</code> or <code>JTextArea</code> against
 * a maximum allowed length. Shows a message dialog on errors.
 * 
 * To use other components, enhance the private method <code>lengthOk()</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/28
 */
public final class InputVerifierMaxLength extends InputVerifier {

    private final int maxLength;

    public InputVerifierMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public boolean verify(JComponent input) {
        boolean lengthOk = lengthOk(input);
        if (!lengthOk) {
            errorMessage(input);
        }
        return lengthOk;
    }

    private boolean lengthOk(JComponent input) {
        if (input instanceof JTextField) {
            return ((JTextField) input).getText().length() <= maxLength;
        } else if (input instanceof JTextArea) {
            return ((JTextArea) input).getText().length() <= maxLength;
        }
        return true;
    }

    private void errorMessage(JComponent input) {
        MessageFormat msg = new MessageFormat("Bitte geben Sie maximal {0} Zeichen ein!");
        Object[] params = {maxLength};
        JOptionPane.showMessageDialog(
            input,
            msg.format(params),
            "Eingabe überprüfen",
            JOptionPane.ERROR_MESSAGE,
            getIcon());
    }

    private Icon getIcon() {
        Settings settings = Settings.getInstance();
        if (settings.hasIconImages()) {
            return IconUtil.getImageIcon(settings.getIconImagesPaths().get(0));
        }
        return null;
    }
}
