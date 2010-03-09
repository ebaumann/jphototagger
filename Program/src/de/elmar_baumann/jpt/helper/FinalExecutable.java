/*
 * JPhotoTagger tags and finds images fast.
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

package de.elmar_baumann.jpt.helper;

import de.elmar_baumann.jpt.app.AppLifeCycle;
import de.elmar_baumann.jpt.app.AppLifeCycle.FinalTask;
import de.elmar_baumann.lib.runtime.External;

/**
 * Executable command that can be called before JPhotoTaggers quits.
 * <p>
 * Usage: Create an instance and add it to
 * {@link AppLifeCycle#addFinalTask(de.elmar_baumann.jpt.app.AppLifeCycle.FinalTask)}
 *
 * @author  Elmar Baumann
 * @version 2010-03-09
 */
public final class FinalExecutable extends FinalTask {
    private String executable;

    public FinalExecutable(String executable) {
        if (executable == null) {
            throw new NullPointerException("exec == null");
        }

        this.executable = executable;
    }

    @Override
    public void execute() {
        External.execute(executable, false);
        notifyFinished();
    }
}
