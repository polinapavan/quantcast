package org.quantcast.processor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.List;

class CookiesProcessorTest {

    @ParameterizedTest
    @CsvFileSource(files = {"src/test/resources/TestParameters/mostActiveCookieTestParams.csv"}, useHeadersInDisplayName = true)
    void testSuccessGetMostActiveCookie(String fileName, String testDate, String activeCookie) {
        List<String> derivedActiveCookies = CookiesProcessor.getMostActiveCookie(fileName, testDate);
        String[] expectedActiveCookies = activeCookie.split(";");
        Assertions.assertEquals(expectedActiveCookies.length, derivedActiveCookies.size());
        for (String expectedCookie : expectedActiveCookies) {
            Assertions.assertTrue(derivedActiveCookies.contains(expectedCookie));
        }
    }

}