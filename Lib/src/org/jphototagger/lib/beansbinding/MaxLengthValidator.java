/*
 * @(#)MaxLengthValidator.java    Created on 2010-04-01
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.lib.beansbinding;

import org.jdesktop.beansbinding.Validator;
import org.jdesktop.beansbinding.Validator.Result;

import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.lib.resource.JslBundle;

import java.io.Serializable;

import javax.swing.JOptionPane;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class MaxLengthValidator extends Validator<String>
        implements Serializable {
    private static final long serialVersionUID = -8065376931606080830L;
    private final int         maxLength;

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
        JOptionPane
            .showMessageDialog(ComponentUtil.getFrameWithIcon(), JslBundle
                .INSTANCE
                .getString("MaxLengthValidator.Error", maxLength), JslBundle
                .INSTANCE
                .getString("MaxLengthValidator.Error.Title"), JOptionPane
                .ERROR_MESSAGE);
    }
}
