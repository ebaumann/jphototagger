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
package de.elmar_baumann.jpt.app;

import de.elmar_baumann.lib.util.CommandLineParser;

/**
 *
 *
 * @author  Elmar Baumann
 * @version 2010-01-22
 */
public final class AppCommandLineOptions {

    private static final String OPTION_NO_SPLASH_SCREEN   = "nosplash";
    private static final String OPTION_NO_OUTPUT_CAPTURE  = "nocapture";
    private static final String OPTION_IMPORT_IMAGE_FILES = "import";

    private final CommandLineParser commandLineParser;

    AppCommandLineOptions(CommandLineParser commandLineParser) {
        this.commandLineParser = commandLineParser;
    }

    public boolean isCaptureOutput() {
        return !commandLineParser.hasOption(OPTION_NO_OUTPUT_CAPTURE);
    }

    public boolean isImportImageFiles() {
        return hasOption(OPTION_IMPORT_IMAGE_FILES);
    }

    public boolean isShowSplashScreen() {
        return !hasOption(OPTION_NO_SPLASH_SCREEN);
    }

    private boolean hasOption(String name) {
        return commandLineParser.hasOption(name);
    }

    public String getFileImportDir() {
        CommandLineParser.Option option = commandLineParser.getOption(OPTION_IMPORT_IMAGE_FILES);
        return option == null
                ? null
                : option.getValues().isEmpty()
                ? null
                : option.getValues().get(0)
                ;
    }
}
