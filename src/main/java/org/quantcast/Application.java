package org.quantcast;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quantcast.processor.CookiesProcessor;

import java.util.List;

public class Application {
    private static Logger logger = LogManager.getLogger(Application.class);
    private static final String FILE_OPTION = "f";
    private static final String DATE_OPTION = "d";
    private static final String HELP_OPTION = "h";

    public static void main(String[] args) {
        try {
            Options options = getAppOptions();
            CommandLine cmd = parseOptions(args, options);

            if (cmd.hasOption(HELP_OPTION)) {
                logger.info(options.toString());
                return;
            }

            List<String> mostActiveCookies = getMostActiveCookies(cmd);
            for (String mostActiveCookie : mostActiveCookies) {
                logger.info(mostActiveCookie);
            }
        } catch (Exception e) {

        }
    }

    private static List<String> getMostActiveCookies(CommandLine cmd) {
        if (cmd.hasOption(FILE_OPTION) && cmd.hasOption(DATE_OPTION)) {
            String fileName = cmd.getOptionValue(FILE_OPTION);
            String date = cmd.getOptionValue(DATE_OPTION);
            return CookiesProcessor.getMostActiveCookie(fileName, date);
        } else {
            logger.error("Mandatory args (-f or -d) are missing");
            throw new CookieProcessingException("Mandatory args (-f or -d) are missing");
        }
    }

    private static CommandLine parseOptions(String[] args, Options options) {
        try {
            CommandLineParser parser = new DefaultParser();
            return parser.parse(options, args);
        } catch (ParseException e) {
            logger.error("Failed to parse options. Use -h for options details. {}", e.getMessage());
            throw new CookieProcessingException(e);
        }
    }

    private static Options getAppOptions() {
        Options options = new Options();
        options.addOption(FILE_OPTION, true, "Cookies log file to process");
        options.addOption(DATE_OPTION, true, "Date in UTC");
        options.addOption(HELP_OPTION, false, "Help");
        return options;
    }
}
