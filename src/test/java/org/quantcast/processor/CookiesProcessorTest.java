package org.quantcast.processor;

import org.junit.jupiter.params.ParameterizedTest;

class CookiesProcessorTest {

    @ParameterizedTest
    void getMostActiveCookie() {
        CookiesProcessor.getMostActiveCookie("src/test/resources/cookies.csv", "2018-12-09");
    }
}