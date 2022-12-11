package org.quantcast.processor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.quantcast.CookieProcessingException;

import java.io.FileNotFoundException;
import java.time.format.DateTimeParseException;
import java.util.List;

class CookiesProcessorTest {

    @ParameterizedTest
    @CsvFileSource(files = {"src/test/resources/TestParameters/mostActiveCookieTestParams.csv"}, useHeadersInDisplayName = true)
    void testSuccessGetMostActiveCookie(String fileName, String searchDate, String activeCookie) {
        List<String> derivedActiveCookies = CookiesProcessor.getMostActiveCookie(fileName, searchDate);
        String[] expectedActiveCookies = activeCookie.split(";");
        Assertions.assertEquals(expectedActiveCookies.length, derivedActiveCookies.size());
        for (String expectedCookie : expectedActiveCookies) {
            Assertions.assertTrue(derivedActiveCookies.contains(expectedCookie));
        }
    }

    @ParameterizedTest
    @CsvFileSource(files = {"src/test/resources/TestParameters/wrongSearchDateTestParams.csv"}, useHeadersInDisplayName = true)
    void testSearchDateNotExist(String fileName, String searchDate) {
        try {
            CookiesProcessor.getMostActiveCookie(fileName, searchDate);
        } catch (CookieProcessingException re) {
            Assertions.assertEquals(CookiesProcessor.ERROR_MSG_COOKIE_NOT_FOUND, re.getMessage());
            return;
        } catch (Exception e) {
            Assertions.fail("This exception is not expected");
        }
        Assertions.fail("Exception is expected here");
    }

    @ParameterizedTest
    @ValueSource(strings = {"wrongCookieFile.csv", "982374jdlfsjk.csv", "i#(9ad.csv"})
    void testWrongFileName(String fileName) {
        try {
            CookiesProcessor.getMostActiveCookie(fileName, "2018-12-07");
        } catch (CookieProcessingException re) {
            Assertions.assertTrue(re.getCause() instanceof FileNotFoundException);
            return;
        } catch (Exception e) {
            Assertions.fail("This exception is not expected");
        }
        Assertions.fail("Exception is expected here");
    }

    @ParameterizedTest
    @ValueSource(strings = {"2020-98-01", "10-12-2022", "12-23-2022", "12345", "#$@183"})
    void testWrongSearchDate(String searchDate) {
        try {
            CookiesProcessor.getMostActiveCookie("src/test/resources/cookiefiles/cookies.csv", searchDate);
        } catch (DateTimeParseException re) {
            return;
        } catch (Exception e) {
            Assertions.fail("This exception is not expected");
        }
        Assertions.fail("Exception is expected here");
    }
}