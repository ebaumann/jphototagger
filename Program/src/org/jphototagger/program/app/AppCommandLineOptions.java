package org.jphototagger.program.app;

import org.jphototagger.lib.util.CommandLineParser;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class AppCommandLineOptions {
    private static final String OPTION_NO_SPLASH_SCREEN = "nosplash";
    private static final String OPTION_NO_OUTPUT_CAPTURE = "nocapture";
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

        return (option == null)
               ? null
               : option.getValues().isEmpty()
                 ? null
                 : option.getValues().get(0);
    }
}
