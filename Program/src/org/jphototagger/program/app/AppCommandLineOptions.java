package org.jphototagger.program.app;

import org.jphototagger.lib.util.CommandLineParser;
import org.jphototagger.program.app.ui.AppLookAndFeel;

/**
 * @author Elmar Baumann
 */
public final class AppCommandLineOptions {

    private static final String OPTION_NO_SPLASH_SCREEN = "nosplash";
    private static final String OPTION_IMPORT_IMAGE_FILES = "import";
    private static final String OPTION_CROSS_PLATFORM_LOOK_AND_FEEL = "cplaf";
    private final CommandLineParser commandLineParser;

    AppCommandLineOptions(CommandLineParser commandLineParser) {
        this.commandLineParser = commandLineParser;
    }

    public boolean isImportImageFiles() {
        return hasOption(OPTION_IMPORT_IMAGE_FILES);
    }

    public boolean isShowSplashScreen() {
        return !hasOption(OPTION_NO_SPLASH_SCREEN);
    }

    public AppLookAndFeel.LookAndFeel getLookAndFeel() {
        return hasOption(OPTION_CROSS_PLATFORM_LOOK_AND_FEEL)
                ? AppLookAndFeel.LookAndFeel.CROSS_PLATFORM
                : AppLookAndFeel.LookAndFeel.SYSTEM;
    }

    private boolean hasOption(String name) {
        return commandLineParser.hasOption(name);
    }

    public String getFileImportDir() {
        CommandLineParser.Option option = commandLineParser.getOption(OPTION_IMPORT_IMAGE_FILES);
        return (option == null)
                ? null
                : option.getValues().isEmpty()
                ? null
                : option.getValues().get(0);
    }
}
