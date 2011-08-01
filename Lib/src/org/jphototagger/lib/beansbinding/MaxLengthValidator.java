package org.jphototagger.lib.beansbinding;

import java.awt.Frame;
import java.io.Serializable;

import javax.swing.JOptionPane;

import org.jdesktop.beansbinding.Validator;
import org.jdesktop.beansbinding.Validator.Result;
import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.lib.util.Bundle;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class MaxLengthValidator extends Validator<String> implements Serializable {

    private static final long serialVersionUID = -8065376931606080830L;
    private final int maxLength;

    public MaxLengthValidator(int maxLength) {
        if (maxLength < 0) {
            throw new IllegalArgumentException("Max length < 0:" + maxLength);
        }

        this.maxLength = maxLength;
    }

    @Override
    public Result validate(String string) {
        if (string.length() > maxLength) {
            errorMessage();
        }

        return null;
    }

    private void errorMessage() {
        Frame parentFrame = ComponentUtil.getFrameWithIcon();
        String message = Bundle.getString(MaxLengthValidator.class, "MaxLengthValidator.Error", maxLength);
        String title = Bundle.getString(MaxLengthValidator.class, "MaxLengthValidator.Error.Title");
        int messageType = JOptionPane.ERROR_MESSAGE;

        JOptionPane.showMessageDialog(parentFrame, message, title, messageType);
    }
}
