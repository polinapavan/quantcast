package org.quantcast.processor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quantcast.CookieProcessingException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class CookiesProcessor {
    private static Logger logger = LogManager.getLogger(CookiesProcessor.class);
    public static final String COMA_DELIMITER = ",";
    public static final String INPUT_SEARCH_DATE_PATTERN = "yyyy-MM-dd";
    public static final String ERROR_MSG_COOKIE_NOT_FOUND = "No matching cookie found";

    private CookiesProcessor() {}

    public static List<String> getMostActiveCookie(String fileName, String inputDate) {
        List<String> fileLines = writeFileIntoList(fileName);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(INPUT_SEARCH_DATE_PATTERN);
        LocalDate searchDate = LocalDate.parse(inputDate, formatter);

        int searchIndex = binarySearch(fileLines, searchDate);
        if (searchIndex == -1) {
            logger.error(ERROR_MSG_COOKIE_NOT_FOUND);
            throw new CookieProcessingException(ERROR_MSG_COOKIE_NOT_FOUND);
        }
        Map<String, Integer> cookieCountMap = new HashMap<>();
        processLinesAroundIndex(fileLines, searchDate, cookieCountMap, searchIndex);

        Optional<Integer> maxCountOptional = cookieCountMap.values().stream().max(Comparator.naturalOrder());
        int maxCount = maxCountOptional.isPresent()? maxCountOptional.get() : 1;
        return cookieCountMap.entrySet().stream().filter(entry -> entry.getValue() == maxCount).collect(Collectors.mapping(Map.Entry::getKey, Collectors.toList()));
    }

    private static List<String> writeFileIntoList(String fileName) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            // Considering first line will contain headers, empty reading the first line
            bufferedReader.readLine();
            return bufferedReader.lines().collect(Collectors.toList());
        } catch (FileNotFoundException e) {
            logger.error("File not found. {}", e.getMessage());
            throw new CookieProcessingException(e);
        } catch (IOException e) {
            logger.error("Failed to read file. {}", e.getMessage());
            throw new CookieProcessingException(e);
        }
    }

    private static int binarySearch(List<String> fileLines, LocalDate searchDate) {
        int start = 0;
        int end = fileLines.size() - 1;
        while (start <= end) {
            int mid = (end + start) / 2;
            String line = fileLines.get(mid);
            String[] lineSplit = line.split(COMA_DELIMITER);
            if (lineSplit.length >= 2) {
                LocalDate cookieDate = getUTCDate(lineSplit[1]);
                if (cookieDate.isEqual(searchDate)) {
                    return mid;
                } else if (cookieDate.isAfter(searchDate)) {
                    start = mid + 1;
                } else {
                    end = mid-1;
                }
            } else {
                fileLines.remove(mid);
            }
        }
        return -1;
    }

    private static void processLinesAroundIndex(List<String> fileLines, LocalDate searchDate, Map<String, Integer> cookieCountMap, int searchIndex) {
        for (int i = searchIndex; i >= 0; i--) {
            String[] lineSplit = fileLines.get(i).split(COMA_DELIMITER);
            if (lineSplit.length >= 2) {
                if (getUTCDate(lineSplit[1]).isEqual(searchDate)) {
                    updateCookieCount(cookieCountMap, lineSplit[0]);
                } else {
                    break;
                }
            }
        }
        for (int i = searchIndex +1; i < fileLines.size(); i++) {
            String[] lineSplit = fileLines.get(i).split(COMA_DELIMITER);
            if (lineSplit.length >= 2) {
                if (getUTCDate(lineSplit[1]).isEqual(searchDate)) {
                    updateCookieCount(cookieCountMap, lineSplit[0]);
                } else {
                    break;
                }
            }
        }
    }

    private static void updateCookieCount(Map<String, Integer> cookieCountMap, String cookie) {
        Integer cookieCount = cookieCountMap.get(cookie);
        if (null == cookieCount) {
            cookieCountMap.put(cookie, 1);
        } else {
            cookieCountMap.replace(cookie, cookieCount+1);
        }
    }

    private static LocalDate getUTCDate(String cookieTimestamp) {
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(cookieTimestamp);
        zonedDateTime = zonedDateTime.withZoneSameInstant(ZoneOffset.UTC);
        return zonedDateTime.toLocalDate();
    }
}
