package org.quantcast;

import org.apache.commons.cli.*;
import org.quantcast.processor.CookiesProcessor;

import java.util.List;

public class Application {

    private static final String FILE_OPTION = "f";
    private static final String DATE_OPTION = "d";

    public static void main(String[] args) {
        try {
            Options options = getAppOptions();
            CommandLine cmd = parseOptions(args, options);
            List<String> mostActiveCookies = getMostActiveCookies(cmd);
            for (String mostActiveCookie : mostActiveCookies) {
                System.out.println(mostActiveCookie);
            }
        } catch (Exception e) {
            System.exit(1);
        }
    }

    private static List<String> getMostActiveCookies(CommandLine cmd) {
        if (cmd.hasOption(FILE_OPTION) && cmd.hasOption(DATE_OPTION)) {
            String fileName = cmd.getOptionValue(FILE_OPTION);
            String date = cmd.getOptionValue(DATE_OPTION);
            return CookiesProcessor.getMostActiveCookie(fileName, date);
        } else {
            System.out.println("Mandatory args (-f or -d) are missing");
            throw new RuntimeException("Mandatory args (-f or -d) are missing");
        }
    }

    private static CommandLine parseOptions(String[] args, Options options) {
        try {
            CommandLineParser parser = new DefaultParser();
            return parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println("Failed to parse args. " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static Options getAppOptions() {
        Options options = new Options();
        options.addOption(FILE_OPTION, true, "Cookies log file to process");
        options.addOption(DATE_OPTION, true, "Date in UTC");
        return options;
    }
}
